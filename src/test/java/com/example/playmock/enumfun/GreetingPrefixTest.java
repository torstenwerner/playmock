package com.example.playmock.enumfun;

import org.junit.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingPrefixTest {
    @Test
    public void shouldGreetCorrectly() {
        final Supplier<String> greeter01 = GreetingPrefix.HI.newGreeter("Hildegunst");
        assertThat(greeter01.get()).isEqualTo("Hi Hildegunst!");

        final Supplier<String> greeter02 = GreetingPrefix.HELLO.newGreeter("Danzelot");
        assertThat(greeter02.get()).isEqualTo("Hello Danzelot!");
    }
}
