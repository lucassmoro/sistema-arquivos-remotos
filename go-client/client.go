package main

import (
	"bufio"
	"fmt"
	clientlib "ine5418/go-client/client_lib"
	"log"
	"os"
	"strconv"
	"strings"
	"time"
)

func main() {
	cliente, err := clientlib.NovoCliente("localhost:9090")
	if err != nil {
		log.Fatalf("Erro ao conectar: %v", err)
	}
	defer cliente.CloseConnection()

	reader := bufio.NewReader(os.Stdin)

	for {
		fmt.Println("\nMENU DO SISTEMA DE ARQUIVOS REMOTO")
		fmt.Println("1 - Abrir arquivo")
		fmt.Println("2 - Ler arquivo")
		fmt.Println("3 - Escrever no arquivo")
		fmt.Println("4 - Fechar arquivo")
		fmt.Println("0 - Sair")
		fmt.Print("Escolha uma opcao: ")

		linha, _ := reader.ReadString('\n')
		linha = strings.TrimSpace(linha)
		opcao, _ := strconv.Atoi(linha)

		switch opcao {
		case 1: // Abre
			fmt.Print("Digite o nome do arquivo: ")
			nome, _ := reader.ReadString('\n')
			nome = strings.TrimSpace(nome)
			descritor := cliente.Abre(nome)
			fmt.Println("Descritor retornado:", descritor)

		case 2: // Le
			fmt.Print("Descritor: ")
			linha, _ := reader.ReadString('\n')
			descritor, _ := strconv.Atoi(strings.TrimSpace(linha))

			fmt.Print("Posicao inicial: ")
			linha, _ = reader.ReadString('\n')
			posicao, _ := strconv.Atoi(strings.TrimSpace(linha))

			fmt.Print("Tamanho da leitura: ")
			linha, _ = reader.ReadString('\n')
			tamanho, _ := strconv.Atoi(strings.TrimSpace(linha))

			cliente.Le(descritor, posicao, tamanho)

		case 3: // Escreve
			fmt.Print("Descritor: ")
			linha, _ := reader.ReadString('\n')
			descritor, _ := strconv.Atoi(strings.TrimSpace(linha))

			fmt.Print("Posicao inicial: ")
			linha, _ = reader.ReadString('\n')
			posicao, _ := strconv.Atoi(strings.TrimSpace(linha))

			fmt.Print("Conteudo para escrever: ")
			conteudo, _ := reader.ReadString('\n')
			conteudo = strings.TrimSpace(conteudo)

			cliente.Escreve(descritor, posicao, conteudo)

		case 4: // Fecha
			fmt.Print("Descritor: ")
			linha, _ := reader.ReadString('\n')
			descritor, _ := strconv.Atoi(strings.TrimSpace(linha))

			cliente.Fecha(descritor)

		case 0: // Sair
			fmt.Println("Encerrando cliente...")
			return

		default:
			fmt.Println("Opcao invalida!")
		}

		time.Sleep(2 * time.Second)
	}
}
