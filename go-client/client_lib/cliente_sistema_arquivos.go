package clientlib

import (
	"context"
	"fmt"
	"time"

	pb "ine5418/go-client/proto" // importa o pacote gerado do .proto
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

type ClienteSistemaArquivos struct{
    conection *grpc.ClientConn
    clientAPI pb.SistemaArquivosClient
    cache map[int]byte
    ultimoDescritor int
    versaoLocal int64 // Versão que o cliente conhece
}

func NovoCliente (endereco string) (*ClienteSistemaArquivos, error) {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    conn, err := grpc.DialContext(ctx, endereco,
        grpc.WithTransportCredentials(insecure.NewCredentials()))

    if err != nil {
        fmt.Println("Erro ao criar conexao", err)
        return nil, err
    } 
    return &ClienteSistemaArquivos{
        conection: conn,
        clientAPI: pb.NewSistemaArquivosClient(conn),
        cache: make(map[int]byte),
        ultimoDescritor: -1,
        versaoLocal: 0,
    }, nil
}

func (c *ClienteSistemaArquivos) CloseConnection() error{
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
    
    // ⭐ ATUALIZA A VERSÃO LOCAL AO ABRIR ARQUIVO
    c.atualizarVersaoLocal()
    
    return int(rply.GetDescritor())
}

func (c *ClienteSistemaArquivos) Le(descritor int, posicao int, tamanho int) int {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    // ⭐ VERIFICA SE HOUVE MUDANÇA GLOBAL ANTES DE USAR CACHE
    if !c.versaoEstaAtualizada() {
        c.limparCache()
        fmt.Println("Cache invalidada - houve escrita no servidor")
    }

    // se o descritor for diferente do ultimo usado, esvazia a cache
    if c.ultimoDescritor != descritor{
        c.limparCache()
        c.ultimoDescritor = descritor
    } 

    // percorre cada posicao na cache e caso algum nao esteja presente, substitui a cache
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

        // adiciona na cache
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

    // ⭐ SEMPRE LIMPA CACHE ANTES DE ESCREVER
    c.limparCache()

    // se o descritor for diferente do ultimo usado, esvazia a cache
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

    // escreve na cache o conteudo escrito no servidor
    conteudoCache := []byte(conteudo)
    for i := 0; i < int(rply.GetBytesEscritos()) ; i++{
        c.cache[posicao+i] = conteudoCache[i]
    } 

    // ⭐ ATUALIZA VERSÃO LOCAL APÓS ESCREVER
    c.atualizarVersaoLocal()

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

// ⭐ MÉTODOS PARA CONTROLE DE VERSÃO GLOBAL

// versaoEstaAtualizada verifica se a versão local está sincronizada com o servidor
func (c *ClienteSistemaArquivos) versaoEstaAtualizada() bool {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    req := &pb.VersaoRequest{}
    rply, err := c.clientAPI.ObterVersaoGlobal(ctx, req)
    
    if err != nil {
        fmt.Println("Erro ao verificar versão:", err)
        return false // Em caso de erro, assume que cache está inválida
    }

    versaoServidor := rply.GetVersaoGlobal()
    
    if versaoServidor != c.versaoLocal {
        c.versaoLocal = versaoServidor // Atualiza versão local
        return false // Cache inválida
    }
    return true // Cache válida
}

// atualizarVersaoLocal atualiza a versão local com a versão atual do servidor
func (c *ClienteSistemaArquivos) atualizarVersaoLocal() {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    req := &pb.VersaoRequest{}
    rply, err := c.clientAPI.ObterVersaoGlobal(ctx, req)
    
    if err != nil {
        fmt.Println("Erro ao atualizar versão local:", err)
        return
    }

    c.versaoLocal = rply.GetVersaoGlobal()
    fmt.Println("Versão local atualizada para:", c.versaoLocal)
}

// limparCache limpa completamente a cache
func (c *ClienteSistemaArquivos) limparCache() {
    for k := range c.cache {
        delete(c.cache, k)
    }
}