package com.mycompany.ine5418.client_lib;

import com.google.protobuf.ByteString;
import com.mycompany.ine5418.SistemaArquivosGrpc;
import com.mycompany.ine5418.SistemaArquivosProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ClienteSistemaArquivos {
    private final ManagedChannel channel;
    private final SistemaArquivosGrpc.SistemaArquivosBlockingStub userStub;
    private final Map<Integer, Character> cache = new HashMap<>();
    private int ultimoDescritor = -1;

    public ClienteSistemaArquivos(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.userStub = SistemaArquivosGrpc.newBlockingStub(this.channel);
    }

    public int Abre(String nome_arquivo){
        SistemaArquivosProto.AbreRequest request = SistemaArquivosProto.AbreRequest.newBuilder().setNomeArquivo(nome_arquivo).build();
        SistemaArquivosProto.AbreReply reply =  userStub.abre(request);

        if (reply.getStatus() >= 0){
            System.out.println(nome_arquivo+" aberto, descritor: "+reply.getDescritor());
            return reply.getDescritor();
        }
        System.out.println("Falha ao abrir "+nome_arquivo+". Status: "+reply.getStatus());
        return reply.getStatus();
    }

    public int Le(int descritor, int posicao, int tamanho){
        // se mudou o arquivo, reseta a cache
        if (descritor != ultimoDescritor) {
            cache.clear();
            ultimoDescritor = descritor;
        }

        // percorre cada posicao desejada e verifica se esta presente na cache, evitando requisitar ao servidor
        boolean cacheMiss = false;
        char[] conteudo_cache = new char[tamanho];
        for (int i = 0; i<tamanho; i++){
            if (cache.get(posicao+i) == null) {
                cacheMiss = true;
                break;
            }
            conteudo_cache[i] = cache.get(posicao+i);
        }
        if (!cacheMiss){
            System.out.println("Conteudo lido da cache: "+ new String(conteudo_cache));
            return 0;
        }

        SistemaArquivosProto.LeRequest request = SistemaArquivosProto.LeRequest.newBuilder()
                .setDescritor(descritor).setPosicao(posicao).setTamanho(tamanho).build();

        SistemaArquivosProto.LeReply reply = userStub.le(request);
        if (reply.getStatus() >= 0){
            System.out.println("Conteudo lido: "+reply.getConteudoLer().toStringUtf8());


            if (reply.getStatus() >= 0) {
                byte[] dados = reply.getConteudoLer().toByteArray();
                for (int i = 0; i < dados.length; i++) {
                    cache.put(posicao + i, (char) dados[i]);
                }
            }

        } else {
            System.out.println("Nao foi possivel ler o conteudo. Status: "+reply.getStatus());
        }
        return reply.getStatus();
    }

    public int Escreve(int descritor, int posicao, String conteudo){
        // se mudou o arquivo, reseta a cache
        if (descritor != ultimoDescritor) {
            cache.clear();
            ultimoDescritor = descritor;
        }
        ByteString conteudoReq = ByteString.copyFrom(conteudo.getBytes(StandardCharsets.UTF_8));

        SistemaArquivosProto.EscreveRequest request = SistemaArquivosProto.EscreveRequest.newBuilder()
                .setDescritor(descritor).setPosicao(posicao).setConteudoEscrever(conteudoReq).build();

        SistemaArquivosProto.EscreveReply reply = userStub.escreve(request);

        if (reply.getStatus() >= 0){
            System.out.println("Bytes escritos: " + reply.getBytesEscritos());

            // escreve o conteudo na cache tambem
            char[] conteudo_cache = conteudo.toCharArray();
            for (int i = 0; i<reply.getBytesEscritos(); i++){
                cache.put(posicao+i, conteudo_cache[i]);
            }

            return reply.getBytesEscritos();
        }
        System.out.println("Falha ao escrever. Status: "+reply.getStatus());
        return reply.getStatus();
    }

    public int Fecha(int descritor){
        SistemaArquivosProto.FechaRequest request = SistemaArquivosProto.FechaRequest.newBuilder().setDescritor(descritor).build();
        SistemaArquivosProto.FechaReply reply = userStub.fecha(request);

        if (reply.getStatus() < 0){
            System.out.println("Falha ao fechar o arquivo. Status: "+reply.getStatus());
        }

        return reply.getStatus();
    }
}