package com.aguiabranca.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    /**
     * JPA auditing reads a {@link DateTimeProvider}, not the {@link Clock} bean, by default -
     * without wiring this explicitly {@code criadoEm}/{@code atualizadoEm} cannot be asserted
     * against a fixed value in tests that control the clock.
     */
    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider auditingDateTimeProvider(Clock clock) {
        return () -> Optional.of(LocalDateTime.now(clock));
    }
}
