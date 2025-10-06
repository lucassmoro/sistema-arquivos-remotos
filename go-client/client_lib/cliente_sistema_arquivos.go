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

     retryExecutor *RetryExecutor
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
 
        //retry com 3 tentativas
        retryExecutor: NewDefaultRetryExecutor(),
    }
    
    cliente.iniciarEscutaNotificacoes()
    
    return cliente, nil
}

func (c *ClienteSistemaArquivos) Abre(nome_arquivo string) int{
    resultado, err := c.executarComRetry(func() (int, error) {
        ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
        defer cancel()

        req := &pb.AbreRequest{
            NomeArquivo: nome_arquivo,
        }

        rply, err := c.clientAPI.Abre(ctx, req)
        if err != nil {
            return -1, err
        }

        if rply.GetStatus() < 0 {
            return -1, fmt.Errorf("Falha ao abrir arquivo. Status: %d", rply.GetStatus())
        }
        
        fmt.Printf("%s aberto, descritor: %d\n", nome_arquivo, rply.GetDescritor())
        return int(rply.GetDescritor()), nil
    })
    
    if err != nil {
        fmt.Println("Erro após retries:", err)
        return -1
    }
    return resultado
}

func (c *ClienteSistemaArquivos) Le(descritor int, posicao int, tamanho int) int {
   if c.ultimoDescritor != descritor {
        c.limparCache()
        c.ultimoDescritor = descritor
    }

    // Verifica cache primeiro (sem retry)
    cacheMiss := false
    conteudoCache := make([]byte, tamanho)
    for i := 0; i < tamanho; i++ {
        if val, ok := c.cache[posicao+i]; ok {
            conteudoCache[i] = val
        } else {
            cacheMiss = true
            break
        }
    }

    if !cacheMiss {
        fmt.Println("Conteudo lido da cache:", string(conteudoCache))
        return 0
    }

    resultado, err := c.executarComRetry(func() (int, error) {
        ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
        defer cancel()

        req := &pb.LeRequest{
            Descritor: int32(descritor),
            Posicao:   int32(posicao),
            Tamanho:   int32(tamanho),
        }

        rply, err := c.clientAPI.Le(ctx, req)
        if err != nil {
            return -1, err
        }

        if rply.GetStatus() < 0 {
            return -1, fmt.Errorf("Falha na leitura. Status: %d", rply.GetStatus())
        }

        fmt.Println("Conteudo lido:", string(rply.GetConteudoLer()))

        // Atualiza cache (sem retry)
        dados := rply.GetConteudoLer()
        for i := 0; i < len(dados); i++ {
            c.cache[posicao+i] = dados[i]
        }

        return int(rply.GetStatus()), nil
    })

    if err != nil {
        fmt.Println("Erro após retries:", err)
        return -1
    }
    return resultado
}

func (c *ClienteSistemaArquivos) Escreve(descritor int, posicao int, conteudo string) int {
    // Limpa cache local (sem retry, operação local)
    c.limparCache()
    
    if c.ultimoDescritor != descritor {
        c.limparCache()
        c.ultimoDescritor = descritor
    }

    resultado, err := c.executarComRetry(func() (int, error) {
        ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
        defer cancel()

        req := &pb.EscreveRequest{
            Descritor:        int32(descritor),
            Posicao:          int32(posicao),
            ConteudoEscrever: []byte(conteudo),
        }

        rply, err := c.clientAPI.Escreve(ctx, req)
        if err != nil {
            return -1, err
        }

        if rply.GetStatus() < 0 {
            return -1, fmt.Errorf("Falha na escrita. Status: %d", rply.GetStatus())
        }

        fmt.Println("Bytes escritos:", rply.GetBytesEscritos(), "Status: ", rply.GetStatus())

        // Atualiza cache local (sem retry, operação local)
        conteudoCache := []byte(conteudo)
        for i := 0; i < int(rply.GetBytesEscritos()); i++ {
            c.cache[posicao+i] = conteudoCache[i]
        }

        return int(rply.GetBytesEscritos()), nil
    })

    if err != nil {
        fmt.Println("Erro após retries:", err)
        return -1
    }
    return resultado
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
func (c *ClienteSistemaArquivos) executarComRetry(operacao RetryableOperation) (int, error) {
    return c.retryExecutor.ExecuteWithRetry(operacao)
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
                // tenta reconectar apos 5 segundos
                time.Sleep(5 * time.Second)
                c.iniciarEscutaNotificacoes()
                return
            }
            
            novaVersao := notificacao.GetVersaoGlobal()
            if novaVersao != c.versaoLocal {
                fmt.Printf("Notificação [SERVIDOR]: versão %d (era %d)\n", novaVersao, c.versaoLocal)
                c.versaoLocal = novaVersao
                c.limparCache() //invalida cache quando versão muda
            }
        }
    }()
    
    fmt.Println("Escuta de notificações iniciada")
}

func (c *ClienteSistemaArquivos) CloseConnection() error {
    // CANCELA A ESCUTA DE NOTIFICAÇÕES
    if c.cancelNotificacao != nil {
        c.cancelNotificacao()
    }
    return c.conection.Close()
}

