-- Cyphera Trino UDF Demo
-- Run: docker compose up -d && trino http://localhost:8080

-- ── Policy-based encryption (the primary interface) ──

-- Protect SSNs (output is tagged alphanumeric with dashes preserved)
SELECT cyphera_protect('ssn', '123-45-6789') AS protected_ssn;

-- Access (decrypt) using embedded tag — no policy name needed
SELECT cyphera_access(cyphera_protect('ssn', '123-45-6789')) AS accessed_ssn;

-- Access with explicit policy name
SELECT cyphera_access('ssn', cyphera_protect('ssn', '123-45-6789')) AS accessed_ssn;

-- Protect credit card numbers
SELECT cyphera_protect('credit_card', '4111-1111-1111-1111') AS protected_cc;

-- Round-trip proof
SELECT
    '123-45-6789' AS original,
    cyphera_protect('ssn', '123-45-6789') AS protected,
    cyphera_access(cyphera_protect('ssn', '123-45-6789')) AS accessed;

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
