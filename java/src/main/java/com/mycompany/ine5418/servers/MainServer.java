package com.mycompany.ine5418.servers;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) throws InterruptedException {
        int porta = 9090;
        Server servidor;

        try {
            servidor = ServerBuilder.forPort(porta).addService(new ServidorSistemaArquivos())
                    .build()
                    .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Servidor rodando na porta "+ porta);

        servidor.awaitTermination();

    }
}
