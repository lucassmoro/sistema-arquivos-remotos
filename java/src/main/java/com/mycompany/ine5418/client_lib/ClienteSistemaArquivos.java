package com.mycompany.ine5418.client_lib;

import com.mycompany.ine5418.client_lib.RetryExecutor;
import com.google.protobuf.ByteString;
import com.mycompany.ine5418.SistemaArquivosGrpc;
import com.mycompany.ine5418.SistemaArquivosProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class ClienteSistemaArquivos {
    private final ManagedChannel channel;
    private final SistemaArquivosGrpc.SistemaArquivosBlockingStub userStub;
    private final SistemaArquivosGrpc.SistemaArquivosStub asyncStub; // stub pra receber notificacao de cache invalida
    private final Map<Integer, Character> cache = new HashMap<>(); // implementacao da cache que mapeia uma posicao para um caractere
    private int ultimoDescritor = -1; // guarda o ultimo descritor utilizado, caso leia um arquivo de descritor diferente, substitui a cache pelo conteudo lido no servidor
    private long versaoLocal = 0; // guarda a versao do arquivo local para comparar com a do servidor quando for ler
    private final String clientId;
    private StreamObserver<SistemaArquivosProto.NotificacaoReply> notificacaoObserver;
    private final RetryExecutor retryExecutor;

    public ClienteSistemaArquivos(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.userStub = SistemaArquivosGrpc.newBlockingStub(this.channel);
        this.asyncStub = SistemaArquivosGrpc.newStub(this.channel);
        this.clientId = UUID.randomUUID().toString(); // ID único para este cliente

        this.retryExecutor = new RetryExecutor(3, 3000, 9000);

        iniciarEscutaNotificacoes();
    }
    public int Abre(String nome_arquivo) {
        return executarComRetry(() -> {
            SistemaArquivosProto.AbreRequest request = SistemaArquivosProto.AbreRequest.newBuilder()
                    .setNomeArquivo(nome_arquivo).build();
            SistemaArquivosProto.AbreReply reply = userStub.abre(request);

            if (reply.getStatus() >= 0) {
                System.out.println(nome_arquivo + " aberto, descritor: " + reply.getDescritor());
                return reply.getDescritor();
            }
            // ⭐ TRATA ERROS DE APLICAÇÃO COMO FALHAS (para fazer retry)
            throw new RuntimeException("Falha ao abrir arquivo. Status: " + reply.getStatus());
        });
    }

    public int Le(int descritor, int posicao, int tamanho){

        // se mudou o arquivo, reseta a cache (sem retry, operacao local)
        if (descritor != ultimoDescritor) {
            cache.clear();
            ultimoDescritor = descritor;
        }

        // percorre cada posicao desejada e verifica se esta presente na cache (sem retry, operacao local)
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
        return executarComRetry(() -> {
            SistemaArquivosProto.LeRequest request = SistemaArquivosProto.LeRequest.newBuilder()
                    .setDescritor(descritor).setPosicao(posicao).setTamanho(tamanho).build();

            SistemaArquivosProto.LeReply reply = userStub.le(request);

            if (reply.getStatus() >= 0) {
                System.out.println("Conteudo lido: " + reply.getConteudoLer().toStringUtf8());

                byte[] dados = reply.getConteudoLer().toByteArray();
                for (int i = 0; i < dados.length; i++) {
                    cache.put(posicao + i, (char) dados[i]);
                }

                return reply.getStatus();
            } else {
                throw new RuntimeException("Falha na leitura. Status: " + reply.getStatus());
            }
        });
    }

    public int Escreve(int descritor, int posicao, String conteudo){
        cache.clear();

        // se mudou o arquivo, reseta a cache (sem retry, operacao local)
        if (descritor != ultimoDescritor) {
            cache.clear();
            ultimoDescritor = descritor;
        }

        // RETRY
        return executarComRetry(() -> {
            ByteString conteudoReq = ByteString.copyFrom(conteudo.getBytes(StandardCharsets.UTF_8));

            SistemaArquivosProto.EscreveRequest request = SistemaArquivosProto.EscreveRequest.newBuilder()
                    .setDescritor(descritor).setPosicao(posicao).setConteudoEscrever(conteudoReq).build();

            SistemaArquivosProto.EscreveReply reply = userStub.escreve(request);

            if (reply.getStatus() >= 0) {
                System.out.println("Bytes escritos: " + reply.getBytesEscritos());

                // Atualiza cache local (sem retry)
                char[] conteudo_cache = conteudo.toCharArray();
                for (int i = 0; i < reply.getBytesEscritos(); i++) {
                    cache.put(posicao + i, conteudo_cache[i]);
                }

                return reply.getBytesEscritos();
            } else {
                throw new RuntimeException("Falha na escrita. Status: " + reply.getStatus());
            }
        });
    }

    public int Fecha(int descritor){
        return executarComRetry(() -> {
            SistemaArquivosProto.FechaRequest request = SistemaArquivosProto.FechaRequest.newBuilder()
                    .setDescritor(descritor).build();
            SistemaArquivosProto.FechaReply reply = userStub.fecha(request);

            if (reply.getStatus() < 0) {
                throw new RuntimeException("Falha ao fechar arquivo. Status: " + reply.getStatus());
            }
            return reply.getStatus();
        });
    }

    // ================================================== METODOS DE COERENCIA DE CACHE =====================================================================================

    private void iniciarEscutaNotificacoes() {
        SistemaArquivosProto.NotificacaoRequest request =
                SistemaArquivosProto.NotificacaoRequest.newBuilder()
                        .setClientId(clientId)
                        .build();

        notificacaoObserver = new StreamObserver<SistemaArquivosProto.NotificacaoReply>() {
            @Override
            public void onNext(SistemaArquivosProto.NotificacaoReply notificacao) {
                long novaVersao = notificacao.getVersaoGlobal();
                if (novaVersao != versaoLocal) {
                    System.out.println("Notificação [SERVIDOR]: versão " + novaVersao + " (era " + versaoLocal + ")");
                    versaoLocal = novaVersao;
                    cache.clear(); // Invalida cache quando versão muda
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Erro na escuta de notificações: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Conexão de notificação fechada pelo servidor");
            }
        };

        asyncStub.registrarNotificacao(request, notificacaoObserver);
        System.out.println("Escuta de notificações iniciada para cliente: " + clientId);
    }
    // =================================================================================================================================

    private <T> T executarComRetry(RetryExecutor.RetryableOperation<T> operacao) {
        return retryExecutor.executeWithRetry(operacao);
    }




}