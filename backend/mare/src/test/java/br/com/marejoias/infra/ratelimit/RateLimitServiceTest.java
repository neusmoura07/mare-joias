package br.com.marejoias.infra.ratelimit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitServiceTest {

    @Test
    @DisplayName("Deve aceitar até o limite por IP e recusar a requisição seguinte")
    void shouldAllowUpToLimitAndRejectNext() {
        RateLimitService service = new RateLimitService(5);

        for (int i = 0; i < 5; i++) {
            assertTrue(service.tryAcquire("10.0.0.1"));
        }
        assertFalse(service.tryAcquire("10.0.0.1"));
    }

    @Test
    @DisplayName("Deve contar o limite separadamente para cada IP")
    void shouldTrackLimitPerIp() {
        RateLimitService service = new RateLimitService(1);

        assertTrue(service.tryAcquire("10.0.0.1"));
        assertFalse(service.tryAcquire("10.0.0.1"));
        assertTrue(service.tryAcquire("10.0.0.2"));
    }

    @Test
    @DisplayName("Deve restaurar o limite padrão ao resetar")
    void shouldRestoreDefaultLimitOnReset() {
        RateLimitService service = new RateLimitService(2);
        service.setRequestsPerMinute(1);
        assertTrue(service.tryAcquire("10.0.0.1"));
        assertFalse(service.tryAcquire("10.0.0.1"));

        service.reset();

        assertTrue(service.tryAcquire("10.0.0.1"));
        assertTrue(service.tryAcquire("10.0.0.1"));
        assertFalse(service.tryAcquire("10.0.0.1"));
    }
}
