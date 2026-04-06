package dev.cyphera.trino;

import com.google.common.collect.ImmutableSet;
import io.trino.spi.Plugin;

import java.util.Set;

public class CypheraPlugin implements Plugin {
    @Override
    public Set<Class<?>> getFunctions() {
        return ImmutableSet.of(CypheraFunctions.class);
    }
}
