package clientlib

import (
	"fmt"
	"time"
)

type RetryableOperation func() (int, error)

type RetryExecutor struct {
	MaxAttempts   int
	InitialDelay  time.Duration
	MaxDelay      time.Duration
}

func NewRetryExecutor(maxAttempts int, initialDelay, maxDelay time.Duration) *RetryExecutor {
	return &RetryExecutor{
		MaxAttempts:  maxAttempts,
		InitialDelay: initialDelay,
		MaxDelay:     maxDelay,
	}
}

func NewDefaultRetryExecutor() *RetryExecutor {
	return NewRetryExecutor(3, 3*time.Second, 9*time.Second)
}

func (r *RetryExecutor) ExecuteWithRetry(operation RetryableOperation) (int, error) {
	var lastErr error
	attempt := 0
	delay := r.InitialDelay
	for attempt < r.MaxAttempts { 
		attempt++
		fmt.Println()
		fmt.Printf("Tentativa %d de %d\n", attempt, r.MaxAttempts)

		result, err := operation()
		if err == nil {
			return result, nil 
		}

		lastErr = err
		fmt.Println()
		fmt.Printf("Tentativa %d falhou: %v\n", attempt, err)

		if attempt == r.MaxAttempts {
			break 
		}

		fmt.Println()
		fmt.Printf("Aguardando %v antes da próxima tentativa...\n", delay)

		time.Sleep(delay)
		delay = r.calculaDelay(attempt)
	}

	return -1, fmt.Errorf("Todas as %d tentativas falharam. Último erro: %v", r.MaxAttempts, lastErr)
}

func (r *RetryExecutor) calculaDelay(attempt int) time.Duration {
	delay := time.Duration(float64(r.InitialDelay) * 2) // dobra o tempo de espera
	if delay > r.MaxDelay {
		return r.MaxDelay
	}
	return delay
}