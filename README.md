# cyphera-trino

[![CI](https://github.com/cyphera-labs/cyphera-trino/actions/workflows/ci.yml/badge.svg)](https://github.com/cyphera-labs/cyphera-trino/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](LICENSE)

Format-preserving encryption UDFs for [Trino](https://trino.io/).

Built on [`io.cyphera:cyphera`](https://central.sonatype.com/artifact/io.cyphera/cyphera) from Maven Central.

## Quick Start (Demo)

```bash
docker compose up -d
# Wait for Trino to start (~30s)
trino http://localhost:8080 < demo.sql
```

See [DEMO.md](DEMO.md) for the full walkthrough with verified input/output.

## Build

### From source

```bash
mvn package -DskipTests
```

Produces `target/cyphera-trino-0.1.0.jar` (fat JAR with all dependencies).

### Via Docker

```bash
docker build -t cyphera-trino .
```

## Install / Deploy

1. Copy the fat JAR to `$TRINO_HOME/plugin/cyphera/`:
   ```bash
   mkdir -p /usr/lib/trino/plugin/cyphera
   cp target/cyphera-trino-0.1.0.jar /usr/lib/trino/plugin/cyphera/
   ```
2. Place policy file at `/etc/cyphera/cyphera.json` (or set `CYPHERA_POLICY_FILE` env var)
3. Restart Trino

The plugin auto-registers `cyphera_protect` and `cyphera_access` functions on startup.

## Usage

```sql
-- Protect with a named policy
SELECT cyphera_protect('ssn', '123-45-6789');
-- → 'T01i6J-xF-07pX' (tagged, dashes preserved)

-- Access — tag tells Cyphera which policy to use, no policy name needed
SELECT cyphera_access(cyphera_protect('ssn', '123-45-6789'));
-- → '123-45-6789'

-- Bulk protect
SELECT name, cyphera_protect('ssn', ssn) AS protected_ssn
FROM customers;
```

## Operations

### Policy Configuration

- Policy file: `/etc/cyphera/cyphera.json` (override with `CYPHERA_POLICY_FILE` env var)
- Policy changes require a Trino restart (the plugin loads policy at startup)

### Monitoring

- Errors return `[error: message]` as the function output instead of failing the query
- Check Trino server logs for `CypheraLoader` entries on startup

### Upgrading

1. Build a new JAR with the updated SDK version in `pom.xml`
2. Replace the JAR in `$TRINO_HOME/plugin/cyphera/`
3. Restart Trino

### Troubleshooting

- **"Unknown policy"** — policy name doesn't match cyphera.json. Check file path and contents.
- **"Unknown key"** — key_ref in policy doesn't match a key in the keys section.
- **Function not found** — JAR not in the plugin directory, or Trino hasn't been restarted.

## Policy File

```json
{
  "policies": {
    "ssn": { "engine": "ff1", "key_ref": "demo-key", "tag": "T01" },
    "credit_card": { "engine": "ff1", "key_ref": "demo-key", "tag": "T02" },
    "name": { "engine": "ff1", "alphabet": "alpha_lower", "key_ref": "demo-key", "tag": "T03" }
  },
  "keys": {
    "demo-key": { "material": "2B7E151628AED2A6ABF7158809CF4F3C" }
  }
}
```

## Future

- Aggregate functions (protect/access across result sets)
- Dynamic policy reload without restart
- Trino connector for policy metadata discovery

## License

Apache 2.0 — Copyright 2026 Horizon Digital Engineering LLC
