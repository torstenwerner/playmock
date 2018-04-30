package com.example.playmock.enumfun;

import org.junit.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingPrefixTest {
    @Test
    public void shouldGreetCorrectly() {
        final Supplier<String> greeter = GreetingPrefix.HI.newGreeter("Hildegunst");
        assertThat(greeter.get()).isEqualTo("Hi Hildegunst!");
    }
}
