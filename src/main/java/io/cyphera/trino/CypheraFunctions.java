package io.cyphera.trino;

import io.airlift.slice.Slice;
import io.airlift.slice.Slices;
import io.cyphera.Cyphera;
import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlType;
import io.trino.spi.type.StandardTypes;

/**
 * Cyphera UDFs for Trino.
 *
 * Policy-based API: cyphera_protect / cyphera_unprotect
 * Uses the real Cyphera SDK with FF1/FF3 FPE, AES-GCM, Mask, Hash.
 */
public final class CypheraFunctions {

    private CypheraFunctions() {}

    private static final Cyphera CLIENT = CypheraLoader.getInstance();

    // ── Policy-based API (the primary interface) ──

    @ScalarFunction("cyphera_protect")
    @Description("Protect a value using a named policy from cyphera.yaml")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice cypheraProtect(
            @SqlType(StandardTypes.VARCHAR) Slice policyName,
            @SqlType(StandardTypes.VARCHAR) Slice value) {
        try {
            String result = CLIENT.protect(value.toStringUtf8(), policyName.toStringUtf8());
            return Slices.utf8Slice(result);
        } catch (Exception e) {
            return Slices.utf8Slice("[error: " + e.getMessage() + "]");
        }
    }

    @ScalarFunction("cyphera_unprotect")
    @Description("Access (decrypt) a protected value using its embedded tag")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice cypheraUnprotect(
            @SqlType(StandardTypes.VARCHAR) Slice protectedValue) {
        try {
            String result = CLIENT.access(protectedValue.toStringUtf8());
            return Slices.utf8Slice(result);
        } catch (Exception e) {
            return Slices.utf8Slice("[error: " + e.getMessage() + "]");
        }
    }

    @ScalarFunction("cyphera_access")
    @Description("Access (decrypt) a protected value with explicit policy name")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice cypheraAccess(
            @SqlType(StandardTypes.VARCHAR) Slice policyName,
            @SqlType(StandardTypes.VARCHAR) Slice protectedValue) {
        try {
            String result = CLIENT.access(protectedValue.toStringUtf8(), policyName.toStringUtf8());
            return Slices.utf8Slice(result);
        } catch (Exception e) {
            return Slices.utf8Slice("[error: " + e.getMessage() + "]");
        }
    }
}
