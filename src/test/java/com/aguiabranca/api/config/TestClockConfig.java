package com.aguiabranca.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * Fixes the app's {@code Clock} bean for the whole test suite (shared by every subclass of
 * {@code AbstractApiIntegrationTest}, so the test context configuration never diverges and stays
 * a single cached context) so JWT expiry and task-completion dates are assertable against a
 * literal value instead of the real wall clock.
 */
@TestConfiguration
public class TestClockConfig {

    public static final Instant INSTANTE_FIXO = Instant.parse("2026-06-15T12:00:00Z");

    // Named differently from ClockConfig#clock() - two @Bean methods called "clock" would collide
    // on registration; @Primary alone only resolves ambiguous injection, not a name clash.
    @Bean
    @Primary
    public Clock clockFixoDeTeste() {
        return Clock.fixed(INSTANTE_FIXO, ZoneOffset.UTC);
    }
}
