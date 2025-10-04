/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ine5418.servers;

import com.google.protobuf.ByteString;
import com.mycompany.ine5418.SistemaArquivosGrpc;
import com.mycompany.ine5418.SistemaArquivosProto;
import io.grpc.stub.StreamObserver;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @author Lucas
 */

public class ServidorSistemaArquivos extends SistemaArquivosGrpc.SistemaArquivosImplBase {
    
    private final ConcurrentHashMap<Integer, String> tabelaArquivos = new ConcurrentHashMap<>(); //mapper thread-safe que mapeia um descritor do cliente para um arquivo do servidor
    private final AtomicInteger contadorDescritor = new AtomicInteger(0); // variavel atomica que garante o incremento com concorrencia, gera descritores unicos pro cliente
    
    @Override
    public void abre(SistemaArquivosProto.AbreRequest req, //AbreRequest é uma classe dentro da classe SistemaArquivosProto gerada no protobuf
                     StreamObserver<SistemaArquivosProto.AbreReply> responseObserver){

        SistemaArquivosProto.AbreReply.Builder responseBuilder = SistemaArquivosProto.AbreReply.newBuilder();
        try{
            String nome_arquivo = req.getNomeArquivo(); // retorna o nome do arquivo do servidor

            Path path = Paths.get(nome_arquivo);
            if(!Files.exists(path)) {
                Files.createFile(path);
            }

            int descritor = contadorDescritor.incrementAndGet(); // gera um descritor pro cliente que requisitou a abertura do arquivo
            tabelaArquivos.put(descritor, nome_arquivo); // mapeia o descritor pro arquivo

            responseBuilder.setStatus(0).setDescritor(descritor);


        } catch (Exception e){
            System.err.println("Erro ao abrir arquivo: " + e.getMessage());
            responseBuilder.setStatus(-1).setDescritor(-1);
        }
        //falta implementar a resposta
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
    public void le(SistemaArquivosProto.LeRequest req,
                   StreamObserver<SistemaArquivosProto.LeReply> responseObserver){

        SistemaArquivosProto.LeReply.Builder responseBuilder = SistemaArquivosProto.LeReply.newBuilder();
        try{
            int descritor = req.getDescritor();
            int posicao = req.getPosicao();
            int tamanho = req.getTamanho();

            String nome_arquivo = tabelaArquivos.get(descritor);
            if (nome_arquivo == null) {
                throw new IllegalArgumentException("Descritor invalido");
            }

            Path path = Paths.get(nome_arquivo);
            byte conteudo[] = Files.readAllBytes(path);
            int fim = Math.min(posicao + tamanho, conteudo.length);
            byte lidos[] = Arrays.copyOfRange(conteudo, posicao, fim);

            responseBuilder.setStatus(0).setConteudoLer(ByteString.copyFrom(lidos));

        } catch (Exception e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            responseBuilder.setStatus(-1).setConteudoLer(ByteString.EMPTY);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    public void escreve(SistemaArquivosProto.EscreveRequest req,
                        StreamObserver<SistemaArquivosProto.EscreveReply> responseObserver){

        SistemaArquivosProto.EscreveReply.Builder responseBuilder = SistemaArquivosProto.EscreveReply.newBuilder();
        try {
            int descritor = req.getDescritor();
            int posicao = req.getPosicao();
            ByteString conteudo = req.getConteudoEscrever();


            String nome_arquivo = tabelaArquivos.get(descritor);
            if (nome_arquivo == null){
                throw new IllegalArgumentException("Descritor invalido");
            }

            Path path = Paths.get(nome_arquivo);
            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
                ByteBuffer buffer = ByteBuffer.wrap(conteudo.toByteArray());
                channel.position(posicao);

                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
            }

            responseBuilder.setStatus(0).setBytesEscritos(conteudo.size());

        } catch (Exception e) {
            responseBuilder.setStatus(-1).setBytesEscritos(0);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    public void fecha(SistemaArquivosProto.FechaRequest req,
                      StreamObserver<SistemaArquivosProto.FechaReply> responseObserver){

        SistemaArquivosProto.FechaReply.Builder responseBuilder = SistemaArquivosProto.FechaReply.newBuilder();

        try {
            int descritor = req.getDescritor();
            tabelaArquivos.remove(descritor);

            responseBuilder.setStatus(0);

        } catch (Exception e) {
            responseBuilder.setStatus(-1);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
