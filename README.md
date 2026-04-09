# cyphera-trino

Format-preserving encryption UDFs for [Trino](https://trino.io/).

Built on [`io.cyphera:cyphera`](https://central.sonatype.com/artifact/io.cyphera/cyphera) from Maven Central.

## Quick Start

```bash
docker compose up -d
# Wait for Trino to start (~30s)
trino http://localhost:8080 < demo.sql
```

## Functions

### Policy-based (primary interface)

```sql
-- Protect with a named policy
SELECT cyphera_protect('ssn', '123-45-6789');
-- → 'T01948-37-2150' (tagged, format preserved)

-- Access — tag tells Cyphera which policy to use, no policy name needed
SELECT cyphera_access(cyphera_protect('ssn', '123-45-6789'));
-- → '123-45-6789'
```

## Policy File

Mount a `cyphera.json` to `/etc/cyphera/cyphera.json`:

```json
{
  "policies": {
    "ssn": { "engine": "ff1", "alphabet": "digits", "key_ref": "demo-key", "tag": "T01" }
  },
  "keys": {
    "demo-key": { "material": "2B7E151628AED2A6ABF7158809CF4F3C" }
  }
}
```

Override the path with `CYPHERA_POLICY_FILE` env var.

## Alphabets

| Name | Characters |
|------|-----------|
| `digits` | `0-9` |
| `alpha_lower` | `a-z` |
| `alphanumeric` | `0-9a-zA-Z` |
