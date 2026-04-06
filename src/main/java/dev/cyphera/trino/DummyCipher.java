package dev.cyphera.trino;

/**
 * Placeholder cipher that does a reversible character shift within the alphabet.
 * Preserves format: non-alphabet characters (dashes, spaces) stay in place.
 *
 * This will be replaced with real FF1 FPE when cyphera-java is on Maven.
 */
public final class DummyCipher {

    private static final String DIGITS = "0123456789";
    private static final String ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private DummyCipher() {}

    static String resolveAlphabet(String name) {
        return switch (name.toLowerCase()) {
            case "digits" -> DIGITS;
            case "alpha_lower" -> ALPHA_LOWER;
            case "alphanumeric" -> ALPHANUMERIC;
            default -> DIGITS;
        };
    }

    /**
     * Derive a shift amount from the key material (simple hash).
     */
    private static int deriveShift(String keyHex) {
        int h = 0;
        for (int i = 0; i < keyHex.length(); i++) {
            h = 31 * h + keyHex.charAt(i);
        }
        return Math.abs(h) % 256 + 1; // always positive, 1-256
    }

    /**
     * Encrypt: shift each alphabet character forward by the derived amount.
     * Non-alphabet characters are preserved in place.
     */
    public static String encrypt(String input, String alphabetName, String keyHex) {
        String alpha = resolveAlphabet(alphabetName);
        int shift = deriveShift(keyHex);
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int idx = alpha.indexOf(c);
            if (idx >= 0) {
                sb.append(alpha.charAt((idx + shift) % alpha.length()));
            } else {
                sb.append(c); // preserve dashes, spaces, etc.
            }
        }
        return sb.toString();
    }

    /**
     * Decrypt: shift each alphabet character backward.
     */
    public static String decrypt(String input, String alphabetName, String keyHex) {
        String alpha = resolveAlphabet(alphabetName);
        int shift = deriveShift(keyHex);
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int idx = alpha.indexOf(c);
            if (idx >= 0) {
                sb.append(alpha.charAt((idx - shift % alpha.length() + alpha.length()) % alpha.length()));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
