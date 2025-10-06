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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServidorSistemaArquivos extends SistemaArquivosGrpc.SistemaArquivosImplBase {

    private final ConcurrentHashMap<Integer, String> tabelaArquivos = new ConcurrentHashMap<>();
    private final AtomicInteger contadorDescritor = new AtomicInteger(0);
    private final AtomicLong versaoGlobal = new AtomicLong(0);
    private final CopyOnWriteArrayList<StreamObserver<SistemaArquivosProto.NotificacaoReply>> observadores = new CopyOnWriteArrayList<>();

    @Override
    public void abre(SistemaArquivosProto.AbreRequest req,
                     StreamObserver<SistemaArquivosProto.AbreReply> responseObserver){

        SistemaArquivosProto.AbreReply.Builder responseBuilder = SistemaArquivosProto.AbreReply.newBuilder();
        try{
            String nome_arquivo = req.getNomeArquivo();

            Path path = Paths.get(nome_arquivo);
            if(!Files.exists(path)) {
                Files.createFile(path);
            }

            int descritor = contadorDescritor.incrementAndGet();
            if (tabelaArquivos.contains(nome_arquivo)){
                System.out.println("Arquivo ja aberto");
                responseBuilder.setStatus(-1).setDescritor(-1);
            } else {
                tabelaArquivos.put(descritor, nome_arquivo);
                responseBuilder.setStatus(0).setDescritor(descritor);
            }

        } catch (Exception e){
            System.err.println("Erro ao abrir arquivo: " + e.getMessage());
            responseBuilder.setStatus(-1).setDescritor(-1);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
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

    @Override
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

            long novaVersao = versaoGlobal.incrementAndGet();
            System.out.println("Versão global incrementada para: " + novaVersao);
            notificarTodosClientes(novaVersao);

            responseBuilder.setStatus(0).setBytesEscritos(conteudo.size());

        } catch (Exception e) {
            responseBuilder.setStatus(-1).setBytesEscritos(0);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
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

    @Override
    public void registrarNotificacao(SistemaArquivosProto.NotificacaoRequest req,
                                     StreamObserver<SistemaArquivosProto.NotificacaoReply> responseObserver) {

        String clientId = req.getClientId();
        System.out.println("Cliente registrado para notificações: " + clientId);

        // adiciona na lista de observadores
        observadores.add(responseObserver);

        // envia a versao atual
        SistemaArquivosProto.NotificacaoReply notificacaoInicial =
                SistemaArquivosProto.NotificacaoReply.newBuilder()
                        .setVersaoGlobal(versaoGlobal.get())
                        .build();
        responseObserver.onNext(notificacaoInicial);

        // O stream permanece aberto para futuras notificacoes
        // O cliente vai receber onNext() sempre que a versao mudar
    }

    private void notificarTodosClientes(long novaVersao) {
        SistemaArquivosProto.NotificacaoReply notificacao =
                SistemaArquivosProto.NotificacaoReply.newBuilder()
                        .setVersaoGlobal(novaVersao)
                        .build();

        System.out.println("Notificando " + observadores.size() + " clientes sobre versão: " + novaVersao);

        for (StreamObserver<SistemaArquivosProto.NotificacaoReply> observer : observadores) {
            try {
                observer.onNext(notificacao);
            } catch (Exception e) {
                System.err.println("Erro ao notificar cliente (pode ter desconectado): " + e.getMessage());
                // remove observadores que deram erro (clientes desconectados)
                observadores.remove(observer);
            }
        }
    }
}