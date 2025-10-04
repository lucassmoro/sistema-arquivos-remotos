package com.mycompany.ine5418.clients;

import com.google.protobuf.ByteString;
import com.mycompany.ine5418.SistemaArquivosGrpc;
import com.mycompany.ine5418.SistemaArquivosProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.nio.charset.StandardCharsets;

public class ClienteSistemaArquivos {
    private final ManagedChannel channel;
    private final SistemaArquivosGrpc.SistemaArquivosBlockingStub userStub;

    public ClienteSistemaArquivos(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.userStub = SistemaArquivosGrpc.newBlockingStub(this.channel);
    }

    public int abre(String nome_arquivo){
        SistemaArquivosProto.AbreRequest request = SistemaArquivosProto.AbreRequest.newBuilder().setNomeArquivo(nome_arquivo).build();
        SistemaArquivosProto.AbreReply reply =  userStub.abre(request);

        if (reply.getStatus() >= 0){
            System.out.println(nome_arquivo+" aberto, descritor: "+reply.getDescritor());
            return reply.getDescritor();
        }
        System.out.println("Falha ao abrir "+nome_arquivo+", Status: "+reply.getStatus());
        return reply.getStatus();
    }

    public int le(int descritor, int posicao, int tamanho){
        SistemaArquivosProto.LeRequest request = SistemaArquivosProto.LeRequest.newBuilder()
                .setDescritor(descritor).setPosicao(posicao).setTamanho(tamanho).build();

        SistemaArquivosProto.LeReply reply = userStub.le(request);
        if (reply.getStatus() >= 0){
            System.out.println("Conteudo lido: "+reply.getConteudoLer().toStringUtf8());
        } else {
            System.out.println("Nao foi possivel ler o conteudo, Status: "+reply.getStatus());
        }
        return reply.getStatus();
    }

    public int escreve(int descritor, int posicao, String conteudo){
        ByteString conteudoReq = ByteString.copyFrom(conteudo.getBytes(StandardCharsets.UTF_8));

        SistemaArquivosProto.EscreveRequest request = SistemaArquivosProto.EscreveRequest.newBuilder()
                .setDescritor(descritor).setPosicao(posicao).setConteudoEscrever(conteudoReq).build();

        SistemaArquivosProto.EscreveReply reply = userStub.escreve(request);

        if (reply.getStatus() >= 0){
            System.out.println("Bytes escritos: " + reply.getBytesEscritos());
            return reply.getBytesEscritos();
        }
        System.out.println("Falha ao escrever, Status: "+reply.getStatus());
        return reply.getStatus();
    }

    public int fecha(int descritor){
        SistemaArquivosProto.FechaRequest request = SistemaArquivosProto.FechaRequest.newBuilder().setDescritor(descritor).build();
        SistemaArquivosProto.FechaReply reply = userStub.fecha(request);

        if (reply.getStatus() < 0){
            System.out.println("Falha ao fechar o arquivo, Status: "+reply.getStatus());
        }

        return reply.getStatus();
    }
}