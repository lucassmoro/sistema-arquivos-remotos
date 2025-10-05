package com.mycompany.ine5418;
import com.mycompany.ine5418.client_lib.ClienteSistemaArquivos;

public class Client {
    public static void main(String[] args){
        ClienteSistemaArquivos cliente = new ClienteSistemaArquivos("localhost", 9090);
        int abreDescritor = cliente.Abre("distribuida.txt");
        int bytesEscritos = cliente.Escreve(abreDescritor, 0, "Hello World");
        cliente.Le(abreDescritor, 0, bytesEscritos);
        cliente.Fecha(abreDescritor);
    }
}
