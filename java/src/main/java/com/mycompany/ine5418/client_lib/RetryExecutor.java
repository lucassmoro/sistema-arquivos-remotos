package com.mycompany.ine5418.client_lib;

import java.util.concurrent.TimeUnit;

public class RetryExecutor {
    private final int maxAttempts;
    private final long initialDelayMs;
    private final long maxDelayMs;

    public RetryExecutor(int maxAttempts, long initialDelayMs, long maxDelayMs) {
        this.maxAttempts = maxAttempts;
        this.initialDelayMs = initialDelayMs;
        this.maxDelayMs = maxDelayMs;
    }

    // Construtor com valores padrão
    public RetryExecutor() {
        this(3, 1500, 3000); // 3 tentativas, começando com 100ms, máximo 1s
    }

    public <T> T executeWithRetry(RetryableOperation<T> operation) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxAttempts) {
            try {
                attempt++;
                System.out.println("Tentativa " + attempt + " de " + maxAttempts);
                return operation.execute();

            } catch (Exception e) {
                lastException = e;
                System.out.println("Tentativa " + attempt + " falhou: " + e.getMessage());

                if (attempt == maxAttempts) {
                    break; // Última tentativa falhou
                }

                // Calcula delay com backoff exponencial
                long delay = calculateBackoffDelay(attempt);
                System.out.println("Aguardando " + delay + "ms antes da próxima tentativa...");

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Operação interrompida", ie);
                }
            }
        }

        throw new RuntimeException("Todas as " + maxAttempts + " tentativas falharam", lastException);
    }

    private long calculateBackoffDelay(int attempt) {
        // Backoff exponencial: delay * 2^(attempt-1), com limite máximo
        long delay = initialDelayMs * (long) Math.pow(2, attempt - 1);
        return Math.min(delay, maxDelayMs);
    }

    @FunctionalInterface
    public interface RetryableOperation<T> {
        T execute() throws Exception;
    }
}