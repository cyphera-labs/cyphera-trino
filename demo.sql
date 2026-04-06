-- Cyphera Trino UDF Demo
-- Run: docker compose up -d && trino http://localhost:8080

-- ── Policy-based encryption (the primary interface) ──

-- Encrypt SSNs
SELECT cyphera_protect('ssn', '123-45-6789') AS encrypted_ssn;

-- Decrypt SSNs
SELECT cyphera_unprotect('ssn', cyphera_protect('ssn', '123-45-6789')) AS decrypted_ssn;

-- Encrypt credit card numbers
SELECT cyphera_protect('credit_card', '4111-1111-1111-1111') AS encrypted_cc;

-- Round-trip proof
SELECT
    '123-45-6789' AS original,
    cyphera_protect('ssn', '123-45-6789') AS encrypted,
    cyphera_unprotect('ssn', cyphera_protect('ssn', '123-45-6789')) AS decrypted;

-- ── Direct engine API (for testing) ──

SELECT cyphera_ff1_encrypt('123456789', '2B7E151628AED2A6ABF7158809CF4F3C', 'digits') AS encrypted;
SELECT cyphera_ff1_decrypt(
    cyphera_ff1_encrypt('123456789', '2B7E151628AED2A6ABF7158809CF4F3C', 'digits'),
    '2B7E151628AED2A6ABF7158809CF4F3C',
    'digits'
) AS decrypted;

-- ── Bulk example with inline data ──

SELECT
    name,
    ssn AS original_ssn,
    cyphera_protect('ssn', ssn) AS protected_ssn
FROM (
    VALUES
        ('Alice', '123-45-6789'),
        ('Bob',   '987-65-4321'),
        ('Carol', '555-12-3456')
) AS t(name, ssn);
