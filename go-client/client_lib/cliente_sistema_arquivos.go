package clientlib

import (
	"context"
	"fmt"
	"log"
	"time"

	pb "ine5418/go-client/proto"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

type ClienteSistemaArquivos struct{
    conection *grpc.ClientConn 
    clientAPI pb.SistemaArquivosClient
    cache map[int]byte
    ultimoDescritor int
    versaoLocal int64
    cancelNotificacao context.CancelFunc
}

func NovoCliente(endereco string) (*ClienteSistemaArquivos, error) {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    conn, err := grpc.DialContext(ctx, endereco,
        grpc.WithTransportCredentials(insecure.NewCredentials()))

    if err != nil {
        fmt.Println("Erro ao criar conexao", err)
        return nil, err
    }
    
    cliente := &ClienteSistemaArquivos{
        conection: conn,
        clientAPI: pb.NewSistemaArquivosClient(conn),
        cache: make(map[int]byte),
        ultimoDescritor: -1,
        versaoLocal: 0,
    }
    cliente.iniciarEscutaNotificacoes()
    
    return cliente, nil
}

func (c *ClienteSistemaArquivos) iniciarEscutaNotificacoes() {
    ctx, cancel := context.WithCancel(context.Background())
    c.cancelNotificacao = cancel
    
    req := &pb.NotificacaoRequest{
        ClientId: fmt.Sprintf("go-client-%d", time.Now().UnixNano()),
    }
    
    stream, err := c.clientAPI.RegistrarNotificacao(ctx, req)
    if err != nil {
        log.Printf("Erro ao registrar para notificações: %v", err)
        return
    }
    
    go func() {
        for {
            notificacao, err := stream.Recv()
            if err != nil {
                log.Printf("Erro ao receber notificação: %v", err)
                // Tenta reconectar apos 5 segundos
                time.Sleep(5 * time.Second)
                c.iniciarEscutaNotificacoes()
                return
            }
            
            novaVersao := notificacao.GetVersaoGlobal()
            if novaVersao != c.versaoLocal {
                fmt.Printf("Notificação [SERVIDOR]: versão %d (era %d)\n", novaVersao, c.versaoLocal)
                c.versaoLocal = novaVersao
                c.limparCache() // Invalida cache quando versão muda
            }
        }
    }()
    
    fmt.Println("Escuta de notificações iniciada")
}

func (c *ClienteSistemaArquivos) CloseConnection() error {
    // ⭐ CANCELA A ESCUTA DE NOTIFICAÇÕES
    if c.cancelNotificacao != nil {
        c.cancelNotificacao()
    }
    return c.conection.Close()
}

func (c *ClienteSistemaArquivos) Abre(nome_arquivo string) int{
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    req := &pb.AbreRequest{
        NomeArquivo: nome_arquivo,
    }

    rply, _ := c.clientAPI.Abre(ctx, req)

    if rply.GetStatus() < 0{
        fmt.Println("Erro ao abrir arquivo", nome_arquivo, ". Status:", rply.GetStatus())
        return int(rply.GetStatus())
    }
    
    return int(rply.GetDescritor())
}

func (c *ClienteSistemaArquivos) Le(descritor int, posicao int, tamanho int) int {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    // ⭐ NÃO PRECISA VERIFICAR VERSÃO - JÁ RECEBEMOS NOTIFICAÇÕES!

    if c.ultimoDescritor != descritor{
        c.limparCache()
        c.ultimoDescritor = descritor
    } 

    // Verifica cache
    cacheMiss := false
    conteudoCache := make([]byte, tamanho)
    for i := 0; i<tamanho; i++{
        if val, ok := c.cache[posicao+i]; ok {
            conteudoCache[i] = val
        } else {
            cacheMiss = true
            break
        }
    } 

    if !cacheMiss{
        fmt.Println("Conteudo lido da cache:", string(conteudoCache))
        return 0
    }

    req := &pb.LeRequest{
        Descritor: int32(descritor),
        Posicao: int32(posicao),
        Tamanho: int32(tamanho),
    }

    rply, _ := c.clientAPI.Le(ctx, req)

    if rply.GetStatus() < 0 {
        fmt.Println("Erro ao ler o arquivo. Status:", rply.GetStatus())
    } else {
        fmt.Println("Conteudo lido:", string(rply.GetConteudoLer()))

        dados := rply.GetConteudoLer()
        for i := 0; i < len(dados); i++ {
            c.cache[posicao+i] = dados[i]
        }
    }
    return int(rply.GetStatus())
}

func (c *ClienteSistemaArquivos) Escreve(descritor int, posicao int, conteudo string) int {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    c.limparCache()

    if c.ultimoDescritor != descritor{
        c.limparCache()
        c.ultimoDescritor = descritor
    } 

    req := &pb.EscreveRequest{
        Descritor: int32(descritor),
        Posicao: int32(posicao),
        ConteudoEscrever: []byte(conteudo),
    }

    rply, _ := c.clientAPI.Escreve(ctx, req)

    if rply.GetStatus() < 0 {
        fmt.Println("Erro ao escrever no arquivo. Status:", rply.GetStatus())
        return int(rply.GetStatus())
    }
    fmt.Println("Bytes escritos:", rply.GetBytesEscritos())

    // Escreve na cache
    conteudoCache := []byte(conteudo)
    for i := 0; i < int(rply.GetBytesEscritos()) ; i++{
        c.cache[posicao+i] = conteudoCache[i]
    } 

    return int(rply.GetBytesEscritos())
}

func (c *ClienteSistemaArquivos) Fecha(descritor int) int{
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    req := &pb.FechaRequest{
        Descritor: int32(descritor),
    }

    rply, _ := c.clientAPI.Fecha(ctx, req)

    if rply.GetStatus() < 0{
        fmt.Println("Erro ao fechar o arquivo. Status:", rply.GetStatus())
    }
    return int(rply.GetStatus())
}

func (c *ClienteSistemaArquivos) limparCache() {
    for k := range c.cache {
        delete(c.cache, k)
    }
}