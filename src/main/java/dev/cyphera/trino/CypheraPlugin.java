package dev.cyphera.trino;

import io.trino.spi.Plugin;

import java.util.Set;

public class CypheraPlugin implements Plugin {
    @Override
    public Set<Class<?>> getFunctions() {
        return Set.of(CypheraFunctions.class);
    }
}
