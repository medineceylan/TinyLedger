# TinyLedger

A small in-memory ledger service. It lets you record deposits and withdrawals, check the current balance, and view the full transaction history.

## Architecture

The project follows a **hexagonal (ports & adapters) style separation of concerns**:

- `web` — inbound adapter: REST controller + DTOs + mapper between DTOs and the domain model.
- `service` — application/domain logic: validates and records transactions.
- `port` — the interfaces (`TransactionService`, `TransactionStorage`) that decouple the web layer and the service layer from any specific persistence technology.
- `persistence` — outbound adapter: an in-memory implementation of `TransactionStorage` (`TransactionInMemoryStorage`), easily swappable for a real database later without touching the service or web layers.
- `domain` — the core `Transaction` model and `TransactionType` enum, framework-agnostic.

Identity (`transactionId`) and the timestamp (`transactionTime`) are assigned by the **service layer**, not the mapper, since generating a transaction's identity/time is a domain concern rather than a translation concern.

## Endpoints

| Method | Path            | Description                                  |
|--------|-----------------|-----------------------------------------------|
| POST   | `/transactions` | Records a deposit or withdrawal              |
| GET    | `/transactions` | Returns the full transaction history         |
| GET    | `/balance`      | Returns the current balance                  |

### POST /transactions

Records a new deposit or withdrawal.

Request body:
```json
{
  "transactionAmount": 100,
  "transactionType": "DEPOSIT"
}
```

`transactionType` is one of `DEPOSIT` or `WITHDRAWAL`.

Responses:
- `202 Accepted` — transaction recorded.
- `400 Bad Request` — amount is zero/negative, a withdrawal exceeds the current balance, or `transactionAmount`/`transactionType` is missing.

### GET /transactions

Returns the transaction history, ordered by insertion.

```json
[
  {
    "transactionAmount": 100,
    "transactionType": "DEPOSIT",
    "transactionTime": "2026-08-02T10:15:30"
  }
]
```

### GET /balance

Returns the current balance as a plain number, e.g. `100`.

## Assumptions

- **Zero-amount transactions are rejected.** A deposit or withdrawal of `0` has no economic effect, so it's treated as invalid input (`400 Bad Request`) rather than a silent no-op.
- **Negative amounts are rejected** for the same reason — the sign of the amount is implied by `transactionType`, not by the number itself.
- **Withdrawals cannot exceed the current balance.** The balance is never allowed to go negative; an over-withdrawal returns `400 Bad Request` and nothing is stored.
- **A withdrawal exactly equal to the current balance is allowed** (balance is allowed to reach exactly zero).
- **No authentication, no persistence, no logging** — data lives only in memory for the lifetime of the process, per the assignment scope.
- **Single ledger, no accounts/users** — all transactions share one global balance.
- **No thread-safety/atomic operations around recording a transaction** — this is a deliberate choice, not an oversight, since the assignment explicitly excludes "transactions/atomic operations" from scope.

## How to run

Requires JDK 17+ (developed and tested with JDK 17/23).

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`.

### Run the tests

```bash
./gradlew test
```

## API documentation (Swagger)

Once the app is running, interactive API docs are available at:

```
http://localhost:8080/swagger-ui/index.html
```

You can try out all three endpoints directly from there. Raw OpenAPI spec:

```
http://localhost:8080/v3/api-docs
```

Alternatively, you can call the endpoints with curl:

```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{"transactionAmount": 100, "transactionType": "DEPOSIT"}'

curl http://localhost:8080/balance

curl http://localhost:8080/transactions
```
