package com.example.playmock.enumfun;

import org.junit.Test;

import java.util.function.UnaryOperator;

import static com.example.playmock.enumfun.GreetingPrefix.HELLO;
import static com.example.playmock.enumfun.GreetingPrefix.HI;
import static org.assertj.core.api.Assertions.assertThat;

public class GreetingPrefixTest {
    @Test
    public void shouldGreetCorrectly() {
        final UnaryOperator<String> greeter01 = HI.newGreeter("Hildegunst");
        assertThat(greeter01.apply("!")).isEqualTo("Hi Hildegunst!");

        final UnaryOperator<String> greeter02 = HELLO.newGreeter("Danzelot");
        assertThat(greeter02.apply(",")).isEqualTo("Hello Danzelot,");
    }
}
