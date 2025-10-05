package main

import (
	clientlib "ine5418/go-client/client_lib"
	"log"
)
func main() {
	cliente, err := clientlib.NovoCliente("localhost:9090")
	if err != nil {
		log.Fatalf("Erro ao conectar: %v", err)
	}
	defer cliente.CloseConnection()

	descritor := cliente.Abre("cliente_go.txt")
	bytes_escritos := cliente.Escreve(descritor, 0, "Hello from Golang")
	cliente.Le(descritor, 0, bytes_escritos)
	cliente.Fecha(descritor)

}