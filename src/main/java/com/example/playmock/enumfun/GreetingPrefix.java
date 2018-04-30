package com.example.playmock.enumfun;

import java.util.function.UnaryOperator;

import static java.lang.String.format;

public enum GreetingPrefix {
    HI("Hi"),
    HELLO("Hello");

    private final String prefix;

    GreetingPrefix(String prefix) {
        this.prefix = prefix;
    }

    public UnaryOperator<String> newGreeter(String name) {
        return new Greeter(name);
    }

    private class Greeter implements UnaryOperator<String> {
        private Greeter(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String apply(String punctuation) {
            return format("%s %s%s", prefix, name, punctuation);
        }
    }
}
