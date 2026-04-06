package dev.cyphera.trino;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads policy definitions from a YAML file.
 *
 * Looks for the policy file at:
 *   1. System property "cyphera.policy.file"
 *   2. Environment variable "CYPHERA_POLICY_FILE"
 *   3. Default: /etc/cyphera/cyphera.yaml
 */
public final class PolicyLoader {

    private static final Logger LOG = Logger.getLogger(PolicyLoader.class.getName());
    private static volatile PolicyLoader instance;

    private final Map<String, PolicyEntry> policies = new HashMap<>();

    private PolicyLoader() {
        String path = System.getProperty("cyphera.policy.file",
                System.getenv().getOrDefault("CYPHERA_POLICY_FILE", "/etc/cyphera/cyphera.yaml"));
        load(path);
    }

    public static PolicyLoader getInstance() {
        if (instance == null) {
            synchronized (PolicyLoader.class) {
                if (instance == null) {
                    instance = new PolicyLoader();
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private void load(String path) {
        try (InputStream in = new FileInputStream(path)) {
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(in);

            Map<String, Map<String, String>> keys = (Map<String, Map<String, String>>)
                    root.getOrDefault("keys", Map.of());

            Map<String, Map<String, String>> pols = (Map<String, Map<String, String>>)
                    root.getOrDefault("policies", Map.of());

            for (var entry : pols.entrySet()) {
                String name = entry.getKey();
                Map<String, String> def = entry.getValue();
                String engine = def.getOrDefault("engine", "ff1");
                String alphabet = def.getOrDefault("alphabet", "digits");
                String keyRef = def.getOrDefault("key_ref", "");
                String material = "";
                if (keys.containsKey(keyRef)) {
                    material = keys.get(keyRef).getOrDefault("material", "");
                }
                policies.put(name, new PolicyEntry(engine, alphabet, keyRef, material));
            }
            LOG.info("Loaded " + policies.size() + " policies from " + path);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Could not load policy file: " + path, e);
        }
    }

    public PolicyEntry getPolicy(String name) {
        return policies.get(name);
    }
}
