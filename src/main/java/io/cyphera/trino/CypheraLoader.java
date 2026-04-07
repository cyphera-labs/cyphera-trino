package io.cyphera.trino;

import io.cyphera.Cyphera;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads the Cyphera SDK client from a YAML policy file.
 *
 * Looks for the policy file at:
 *   1. System property "cyphera.policy.file"
 *   2. Environment variable "CYPHERA_POLICY_FILE"
 *   3. Default: /etc/cyphera/cyphera.yaml
 */
public final class CypheraLoader {

    private static final Logger LOG = Logger.getLogger(CypheraLoader.class.getName());
    private static volatile Cyphera instance;

    private CypheraLoader() {}

    @SuppressWarnings("unchecked")
    public static Cyphera getInstance() {
        if (instance == null) {
            synchronized (CypheraLoader.class) {
                if (instance == null) {
                    String path = System.getProperty("cyphera.policy.file",
                            System.getenv().getOrDefault("CYPHERA_POLICY_FILE", "/etc/cyphera/cyphera.yaml"));
                    try (InputStream in = new FileInputStream(path)) {
                        Yaml yaml = new Yaml();
                        Map<String, Object> config = yaml.load(in);
                        instance = Cyphera.fromMap(config);
                        LOG.info("Cyphera SDK loaded from " + path);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to load Cyphera config: " + path, e);
                        throw new RuntimeException("Failed to load Cyphera config: " + path, e);
                    }
                }
            }
        }
        return instance;
    }
}
