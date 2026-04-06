package dev.cyphera.trino;

import io.airlift.slice.Slice;
import io.airlift.slice.Slices;
import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlType;
import io.trino.spi.type.StandardTypes;

/**
 * Cyphera UDFs for Trino.
 *
 * Currently uses dummy (reversible shift) encryption as a placeholder.
 * Will be replaced with real FF1 FPE when cyphera-java is published to Maven.
 */
public final class CypheraFunctions {

    private CypheraFunctions() {}

    // ── Policy-based API (the primary interface) ──

    @ScalarFunction("cyphera_protect")
    @Description("Encrypt a value using a named policy from cyphera.yaml")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice cypheraProtect(
            @SqlType(StandardTypes.VARCHAR) Slice policyName,
            @SqlType(StandardTypes.VARCHAR) Slice value) {
        PolicyEntry policy = PolicyLoader.getInstance().getPolicy(policyName.toStringUtf8());
        if (policy == null) {
            return Slices.utf8Slice("[unknown policy: " + policyName.toStringUtf8() + "]");
        }
        String input = value.toStringUtf8();
        String result = DummyCipher.encrypt(input, policy.alphabet(), policy.keyMaterial());
        return Slices.utf8Slice(result);
    }

    @ScalarFunction("cyphera_unprotect")
    @Description("Decrypt a value using a named policy from cyphera.yaml")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice cypheraUnprotect(
            @SqlType(StandardTypes.VARCHAR) Slice policyName,
            @SqlType(StandardTypes.VARCHAR) Slice value) {
        PolicyEntry policy = PolicyLoader.getInstance().getPolicy(policyName.toStringUtf8());
        if (policy == null) {
            return Slices.utf8Slice("[unknown policy: " + policyName.toStringUtf8() + "]");
        }
        String input = value.toStringUtf8();
        String result = DummyCipher.decrypt(input, policy.alphabet(), policy.keyMaterial());
        return Slices.utf8Slice(result);
    }

    // ── Direct engine API (for testing / simple integrations) ──

    @ScalarFunction("cyphera_ff1_encrypt")
    @Description("Encrypt a value with FF1 FPE (placeholder — uses dummy cipher)")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice cypheraFf1Encrypt(
            @SqlType(StandardTypes.VARCHAR) Slice value,
            @SqlType(StandardTypes.VARCHAR) Slice keyHex,
            @SqlType(StandardTypes.VARCHAR) Slice alphabet) {
        return Slices.utf8Slice(
                DummyCipher.encrypt(value.toStringUtf8(), alphabet.toStringUtf8(), keyHex.toStringUtf8()));
    }

    @ScalarFunction("cyphera_ff1_decrypt")
    @Description("Decrypt a value with FF1 FPE (placeholder — uses dummy cipher)")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice cypheraFf1Decrypt(
            @SqlType(StandardTypes.VARCHAR) Slice value,
            @SqlType(StandardTypes.VARCHAR) Slice keyHex,
            @SqlType(StandardTypes.VARCHAR) Slice alphabet) {
        return Slices.utf8Slice(
                DummyCipher.decrypt(value.toStringUtf8(), alphabet.toStringUtf8(), keyHex.toStringUtf8()));
    }
}
