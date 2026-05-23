package io.cyphera.trino;

import io.cyphera.Cyphera;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class CypheraLoader {
    private static final Logger LOG = Logger.getLogger(CypheraLoader.class.getName());
    private static volatile Cyphera instance;

    private CypheraLoader() {}

    public static Cyphera getInstance() {
        if (instance == null) {
            synchronized (CypheraLoader.class) {
                if (instance == null) {
                    String path = System.getProperty("cyphera.configuration.file",
                            System.getenv() != null && System.getenv().containsKey("CYPHERA_CONFIGURATION_FILE")
                                ? System.getenv("CYPHERA_CONFIGURATION_FILE")
                                : "/etc/cyphera/cyphera.json");
                    try {
                        instance = Cyphera.fromFile(path);
                        LOG.info("Cyphera SDK loaded from " + path);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to load Cyphera configuration: " + path, e);
                        throw new RuntimeException("Failed to load Cyphera configuration: " + path, e);
                    }
                }
            }
        }
        return instance;
    }
}
