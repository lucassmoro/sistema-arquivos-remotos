package sistemaarquivos

import (
	"bytes"
	"context"
	"fmt"
	"log"
	"time"

	pb "github.com/seuuser/ine5418/go-client/proto" // importa o pacote gerado do .proto
	"google.golang.org/grpc"
)

type ClienteSistemaArquivos struct{
    conection *grpc.ClientConn
    clientAPI pb.SistemaArquivosClient
}

func NovoCliente (endereco string) (*ClienteSistemaArquivos, error) {
    conn, err := grpc.Dial(endereco, grpc.WithInsecure())
    if err != nil {
        fmt.Println("Erro ao criar conexao", err)
        return nil, err
    } 
    return &ClienteSistemaArquivos{
        conection: conn,
        clientAPI: pb.NewSistemaArquivosClient(conn),
    }, nil
}

func (c *ClienteSistemaArquivos) Close() error{
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

    req := &pb.LeRequest{
        Descritor: int32(descritor),
        Posicao: int32(posicao),
        Tamanho: int32(tamanho),
    }

    rply, _ := c.clientAPI.Le(ctx, req)

    if rply.GetStatus() < 0 {
        fmt.Println("Erro ao ler o arquivo. Status:", rply.GetStatus())
    } else {
        fmt.Println("Conteudo lido:", rply.GetConteudoLer())
    }
    return int(rply.GetStatus())
}

func (c *ClienteSistemaArquivos) Escreve(descritor int, posicao int, conteudo string) int {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

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