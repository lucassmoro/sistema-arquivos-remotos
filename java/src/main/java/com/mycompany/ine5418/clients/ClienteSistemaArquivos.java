package com.mycompany.ine5418.clients;

import com.mycompany.ine5418.SistemaArquivosGrpc;
import com.mycompany.ine5418.SistemaArquivosProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClienteSistemaArquivos {
    public static void main(String[] args){

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        SistemaArquivosGrpc.SistemaArquivosBlockingStub userStub = SistemaArquivosGrpc.newBlockingStub(channel);

        SistemaArquivosProto.AbreRequest request = SistemaArquivosProto.AbreRequest.newBuilder().setNomeArquivo("gorila.txt").build();

        SistemaArquivosProto.AbreReply reply = userStub.abre(request);

        System.out.println("Server status"+reply.getStatus());
        System.out.println("Descritor "+reply.getDescritor());

        channel.shutdown();
    }
}