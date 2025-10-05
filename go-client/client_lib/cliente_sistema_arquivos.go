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
    }, nil
}

func (c *ClienteSistemaArquivos) CloseConnection() error{
    return c.conection.Close()
}

func (c *ClienteSistemaArquivos) Abre(nome_arquivo string) int{
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second) // controla a chamada. A conexao ja é estabelecida no construtor
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

    // se o descritor for diferente do ultimo usado, esvazia a cache
    if c.ultimoDescritor != descritor{
        for k := range c.cache {
            delete(c.cache, k)
        }
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

    // se o descritor for diferente do ultimo usado, esvazia a cache
    if c.ultimoDescritor != descritor{
        for k := range c.cache {
            delete(c.cache, k)
        }
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