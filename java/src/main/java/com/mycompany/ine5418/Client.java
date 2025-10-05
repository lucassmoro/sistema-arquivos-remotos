package com.mycompany.ine5418;
import com.mycompany.ine5418.client_lib.ClienteSistemaArquivos;
import java.util.Scanner;
import java.util.Map;
import java.lang.Thread;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        ClienteSistemaArquivos cliente = new ClienteSistemaArquivos("localhost",9090);
        Scanner scanner = new Scanner(System.in);

        //int descritor = -1; // guarda o descritor atual do arquivo

        while (true) {
            System.out.println("\nMENU DO SISTEMA DE ARQUIVOS REMOTO");
            System.out.println("1 - Abrir arquivo");
            System.out.println("2 - Ler arquivo");
            System.out.println("3 - Escrever no arquivo");
            System.out.println("4 - Fechar arquivo");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opcao: ");

            int opcao = scanner.nextInt();
            int descritor;
            scanner.nextLine(); // consome o \n que sobra do nextInt

            switch (opcao) {
                case 1: // Abre
                    System.out.print("Digite o nome do arquivo: ");
                    String nome = scanner.nextLine();
                    descritor = cliente.Abre(nome);
                    break;

                case 2: // Le
                    System.out.print("Descritor: ");
                    descritor = scanner.nextInt();
                    System.out.print("Posicao inicial: ");
                    int posicaoLe = scanner.nextInt();
                    System.out.print("Tamanho da leitura: ");
                    int tamanho = scanner.nextInt();
                    scanner.nextLine();
                    cliente.Le(descritor, posicaoLe, tamanho);
                    break;

                case 3: // Escreve
                    System.out.print("Descritor: ");
                    descritor = scanner.nextInt();
                    System.out.print("Posicao inicial: ");
                    int posicaoEscreve = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Conteudo para escrever: ");
                    String conteudo = scanner.nextLine();
                    cliente.Escreve(descritor, posicaoEscreve, conteudo);
                    break;

                case 4: // Fecha
                    System.out.println("Descritor: ");
                    descritor = scanner.nextInt();
                    cliente.Fecha(descritor);
                    break;

                case 0: // Sair
                    System.out.println("Encerrando cliente...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Opcao invalida!");
            }
            Thread.sleep(2000);
        }
    }
}