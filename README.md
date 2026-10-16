# Refugee Allocation System (Java)

Implements a refugee allocation system for EU countries with these rules:

1. Countries and max capacity are read from CSV.
2. Refugees are read from JSON.
3. Allocation is proportional using weighted rotation (countries with bigger capacity are visited more often).
4. Family groups (`groupId`) must stay together.
5. If no country can take an entire group, the group is rejected.

## Requirements

- Java 27+
- Maven 3.9+

## Run tests

```bash
mvn test
```

## Run with sample files

```bash
mvn -q exec:java
```

## Run with custom files

```bash
mvn -q exec:java -Dexec.args="--countries path/to/countries.csv --refugees path/to/refugees.json --output path/to/output.json"
```

## Input formats

### countries.csv

```csv
Sverige,1
Tyskland,2
Polen,1
```

### refugees.json

```json
{
  "desc": "List of refugees",
  "date": "2026-10-12",
  "refugees": [
    {"name": "Nisse Hulth", "identifier": "...", "groupId": ""}
  ]
}
```

## Output format

```json
{
  "lyckade": [
    {
      "land": "Sverige",
      "fordelning": [
        {"name": "Nisse Hulth", "identifier": "...", "groupId": ""}
      ]
    }
  ],
  "misslyckade": []
}
```
