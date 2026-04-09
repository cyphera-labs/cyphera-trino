# Cyphera Trino Demo

Protect sensitive data with format-preserving encryption directly in Trino SQL queries.

## Prerequisites

- Docker and Docker Compose

## Start the Demo

```bash
docker compose up -d
# Wait ~30s for Trino to start
```

Trino UI available at **http://localhost:8080**.

## Run the Demo

```bash
trino http://localhost:8080 < demo.sql
```

Or open the Trino CLI and paste queries manually:

```bash
trino http://localhost:8080
```

### Protect an SSN

```sql
SELECT cyphera_protect('ssn', '123-45-6789') AS protected_ssn;
```

Output:
```
  protected_ssn
-----------------
 T01i6J-xF-07pX
```

### Access (decrypt) using the tag

```sql
SELECT cyphera_access(cyphera_protect('ssn', '123-45-6789')) AS accessed_ssn;
```

Output:
```
 accessed_ssn
--------------
 123-45-6789
```

### Round-trip proof

```sql
SELECT
    '123-45-6789' AS original,
    cyphera_protect('ssn', '123-45-6789') AS protected,
    cyphera_access(cyphera_protect('ssn', '123-45-6789')) AS accessed;
```

Output:
```
   original    |    protected    |   accessed
---------------+-----------------+--------------
 123-45-6789   | T01i6J-xF-07pX | 123-45-6789
```

### Bulk protect

```sql
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
```

Output:
```
 name  |  original_ssn  |  protected_ssn
-------+----------------+-----------------
 Alice | 123-45-6789    | T01i6J-xF-07pX
 Bob   | 987-65-4321    | T01Q1I-cH-Sdcb
 Carol | 555-12-3456    | T01b54-Un-4zHt
```

## What's Happening

The Cyphera Trino plugin registers SQL functions that call the Cyphera Java SDK:

1. `cyphera_protect('ssn', value)` — looks up the `ssn` policy, encrypts with FF1, prepends tag `T01`, preserves dashes
2. `cyphera_access(protected_value)` — reads the tag `T01`, finds the `ssn` policy, decrypts

Policy is loaded from `/etc/cyphera/cyphera.json` mounted into the container.

## Cleanup

```bash
docker compose down
```
