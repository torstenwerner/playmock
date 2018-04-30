package com.example.playmock.enumfun;

import java.util.function.Supplier;

import static java.lang.String.format;

public enum GreetingPrefix {
    HI("Hi"),
    HELLO("Hello");

    private final String prefix;

    GreetingPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Supplier<String> newGreeter(String name) {
        return new Greeter(name);
    }

    private class Greeter implements Supplier<String> {
        private Greeter(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String get() {
            return format("%s %s!", prefix, name);
        }
    }
}
