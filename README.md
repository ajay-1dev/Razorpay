# Razorpay Payment Gateway

A payment gateway backend developed using Java and Spring Boot. The project implements core payment platform workflows, including merchant onboarding, API-key management, order creation, payment processing, settlement processing, and webhook delivery.

The project focuses on backend architecture, secure API design, database transactions, asynchronous event processing, concurrency handling, and distributed system concepts.

## 1. Technology Stack

| Category | Technologies |
|---|---|
| Language | Java 25 |
| Backend Framework | Spring Boot 4.1.1 |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Distributed Operations | Redis |
| Messaging | Apache Kafka |
| Security | Spring Security, JWT, BCrypt |
| Object Mapping | MapStruct, ModelMapper |
| Build Tool | Maven |
| API Documentation | Springdoc OpenAPI |
| Containerization | Docker, Docker Compose |

## 2. System Architecture

The application follows a layered architecture. Each layer has a defined responsibility, keeping request handling, business logic, persistence, and infrastructure operations separate.

### High-Level Architecture

```text
                      MERCHANT APPLICATION
                              |
                              v
                         REST APIs
                              |
                              v
                       SPRING SECURITY
                              |
                  +-----------+-----------+
                  |                       |
                  v                       v
           JWT Authentication     API-Key Authentication
                  |                       |
                  +-----------+-----------+
                              |
                              v
                         CONTROLLERS
                              |
                              v
                         SERVICE LAYER
                              |
          +-------------------+-------------------+
          |                   |                   |
          v                   v                   v
      MERCHANT             PAYMENT            OPERATIONS
      OPERATIONS          OPERATIONS          OPERATIONS
          |                   |                   |
          +-------------------+-------------------+
                              |
                              v
                         REPOSITORIES
                              |
                              v
                          POSTGRESQL
                              |
                  +-----------+-----------+
                  |                       |
                  v                       v
                REDIS                   KAFKA
                  |                       |
          +-------+-------+               |
          |       |       |               v
          v       v       v         KAFKA CONSUMERS
       Idem-    Rate    Webhook           |
       potency  Limit   Retry Queue       |
                                          v
                                   WEBHOOK EVENTS
                                          |
                                          v
                                   WEBHOOK DELIVERY
                                          |
                                          v
                                   MERCHANT CALLBACK

                  KAFKA FAILURE HANDLING
                           |
                           v
                      RETRIES
                           |
                           v
                    DEAD LETTER QUEUE
```

### Architectural Components

**Controller Layer**

Exposes REST APIs and handles incoming HTTP requests. Controllers validate incoming data, extract authentication information, delegate operations to services, and return HTTP responses.

**Service Layer**

Contains the business logic and coordinates repositories, Redis, Kafka, and other application components.

**Repository Layer**

Communicates with PostgreSQL using Spring Data JPA. It handles persistence and retrieval of merchants, users, API keys, orders, payments, settlements, and webhook records.

**PostgreSQL**

Acts as the primary persistent data store for business records, including merchants, users, API keys, orders, payments, payment transition logs, settlements, settlement-payment relationships, webhook configurations, webhook events, outbox events, and vault records.

**Redis**

Supports fast and distributed operations such as idempotency management, API rate limiting, and webhook retry scheduling.

**Apache Kafka**

Handles asynchronous event communication between application components. Business events are published through the Transactional Outbox Pattern and processed by Kafka consumers.

**Dead Letter Queue (DLQ)**

Provides a place for Kafka messages that cannot be processed successfully after the configured retry and error-handling process. It helps isolate repeatedly failing messages so they can be investigated and, where appropriate, reprocessed without blocking normal message processing.

The Kafka DLQ and Redis webhook retry queue have different responsibilities:

- Kafka DLQ handles messages that fail during Kafka consumer processing.
- Redis webhook retry queue schedules webhook HTTP deliveries that fail.

---

## 3. Complete Business Flow: Merchant Signup to Settlement Completion

This section explains how the main application components work together during the payment lifecycle.

### Step 1: Merchant Signup

A merchant first registers with the payment platform.

```text
Merchant
   |
   v
Signup API
   |
   v
Merchant Controller
   |
   v
Merchant Service
   |
   v
Validate Registration Details
   |
   v
Save Merchant and User
   |
   v
Registration Completed
```

Process:

1. The merchant submits registration details.
2. The controller receives the request.
3. The service validates the submitted information.
4. Merchant and associated user information are persisted.
5. The merchant account is created.

The merchant is now registered with the platform.

### Step 2: Merchant Authentication and API-Key Generation

After registration, the merchant can authenticate and generate API credentials.

The application supports two authentication mechanisms.

**JWT Authentication**

Used for merchant login and protected merchant operations.

**API-Key Authentication**

Used by merchant applications when accessing payment-related APIs.

API-key generation flow:

```text
Merchant
   |
   v
Request API Key
   |
   v
Generate Key ID and Secret
   |
   v
Encode Secret Using Password Encoder
   |
   v
Save API-Key Information
   |
   v
Return Credentials
```

Important security considerations:

- The API secret is encoded using a password encoder before persistence.
- The original secret is not stored as a reusable plaintext credential.
- API-key rotation and revocation are supported.
- A grace period can be provided during key rotation.

For Basic authentication, the client encodes the `keyId:keySecret` credential pair using Base64 and sends it in the Authorization header.

The server decodes the Base64 value and validates the credentials.

Base64 is encoding, not encryption. HTTPS is required to protect credentials during transmission.

### Step 3: Merchant Creates an Order

After obtaining API credentials, the merchant application can create an order.

```text
Merchant Application
        |
        v
     Order API
        |
        v
 API-Key Authentication
        |
        v
   Rate Limit Check
        |
        v
 Validate Order Request
        |
        v
     Order Service
        |
        v
    Save Order
        |
        v
   Return Order Details
```

Process:

1. The merchant sends an order creation request.
2. Spring Security authenticates the API key.
3. The rate limiter checks the request limit.
4. The order service validates the request.
5. The order is saved in PostgreSQL.
6. The created order is returned to the merchant.

The order represents the amount that the merchant expects to collect from the customer.

### Step 4: Payment Initiation

After creating an order, the merchant initiates a payment.

```text
Merchant Application
        |
        v
    Payment API
        |
        v
 API-Key Authentication
        |
        v
  Idempotency Check
        |
        v
 Validate Order and Amount
        |
        v
    Payment Service
        |
        v
 Select Payment Adapter
        |
        v
 Payment Processing Strategy
        |
        v
   Payment Result
```

Important concepts:

**Idempotency**

The client sends an idempotency key with a payment request.

Redis stores and coordinates the request-processing state. This helps prevent duplicate business operations when the client retries the same request.

**Strategy Pattern in Payment Adapters**

The payment adapters follow the Strategy Pattern.

Different adapter implementations provide a common contract for payment processing. The application selects the appropriate implementation based on the payment method.

This avoids placing all payment-method-specific logic inside a single large conditional block.

It also makes it easier to introduce additional payment methods without significantly changing the main payment service.

### Step 5: Payment State Management

Payments move through defined states during their lifecycle.

The payment transition service controls valid state changes.

```text
Payment Created
      |
      v
Payment Processing
      |
      +----------------------+
      |                      |
      v                      v
  Captured                 Failed
      |
      v
Eligible for Settlement
      |
      v
  Settled
```

The actual transitions depend on the configured payment events and transition rules.

The application also maintains payment transition logs to track payment status changes.

### Step 6: Transactional Outbox and Kafka

When a business operation needs to publish an event, the application uses the Transactional Outbox Pattern.

```text
Business Service
      |
      v
Database Transaction
      |
      +---------------------------+
      |                           |
      v                           v
Update Business Data        Save Outbox Event
      |                           |
      +-------------+-------------+
                    |
                    v
              Transaction Commit
                    |
                    v
              Outbox Publisher
                    |
                    v
                  Kafka
                    |
                    v
              Kafka Consumer
```

Why use the Outbox Pattern?

Consider a payment operation where the database update succeeds but Kafka publishing fails.

Without an outbox, the application could have a successful payment but no corresponding event.

With the outbox pattern:

1. The business data is updated.
2. The corresponding event is saved in the outbox table.
3. Both changes are committed in the same database transaction.
4. A scheduled publisher reads pending outbox records.
5. The publisher sends events to Kafka.
6. Successfully published records are updated.

This improves reliability between database transactions and asynchronous event publishing.

### Step 7: Kafka Consumer Failure Handling and DLQ

Kafka consumers process business events asynchronously. However, message processing can fail because of exceptions, unavailable dependencies, invalid data, or other runtime problems.

The failure-handling flow is:

```text
Kafka Topic
     |
     v
Kafka Consumer
     |
     v
Process Message
     |
     +----------------------+
     |                      |
     v                      v
  Success                 Failure
     |                      |
     v                      v
 Acknowledge           Retry Processing
                            |
                            v
                    Retry Attempts Exhausted
                            |
                            v
                     Dead Letter Queue
                            |
                            v
                    Investigate / Reprocess
```

The DLQ separates messages that repeatedly fail from normal message processing.

This is useful because:

- A problematic message does not continuously interrupt normal processing.
- Failed messages can be inspected independently.
- Developers can investigate the root cause.
- Corrected messages can be reprocessed where appropriate.

The DLQ is different from webhook delivery retries. It handles Kafka consumer processing failures, whereas webhook retries handle unsuccessful HTTP callbacks to merchants.

### Step 8: Settlement Processing

Settlement is the process of transferring the merchant's collected payment amount after deducting applicable fees and taxes.

The application uses a scheduled settlement engine to initiate settlement processing.

The current implementation uses a scheduled job to retrieve active merchants and process them using a virtual-thread executor.

```text
Settlement Scheduler
        |
        v
 Fetch Active Merchants
        |
        v
 Process Each Merchant
        |
        v
 Find Captured, Unsettled Payments
        |
        v
 Calculate Gross Amount
        |
        v
 Calculate Platform Fee
        |
        v
 Calculate GST on Fee
        |
        v
 Create Settlement
        |
        v
 Create Settlement-Payment Links
        |
        v
 Initiate Bank Transfer
        |
        v
 TRANSFER_PENDING
```

#### 8.1 Fetch Eligible Payments

The settlement transaction executor retrieves payments that are:

- Captured.
- Not yet settled.
- Associated with the merchant being processed.

These payments are considered for settlement.

#### 8.2 Calculate Settlement Amount

The current implementation calculates the settlement amount as follows:

```text
Gross Amount = Sum of Eligible Payment Amounts

Platform Fee = Gross Amount × 2%

GST = Platform Fee × 18%

Net Settlement Amount = Gross Amount - Platform Fee - GST
```

These percentages represent the current project implementation.

#### 8.3 Create Settlement Record

A settlement record is created with the initial status:

```text
INITIATED
```

The application then creates settlement-payment relationship records.

These records associate each eligible payment with the settlement.

Example:

```text
Settlement
    |
    |-- Payment 1
    |-- Payment 2
    |-- Payment 3
    |-- Payment 4
```

This relationship allows the application to identify which payments belong to a particular settlement.

#### 8.4 Initiate Bank Transfer

After creating the settlement and payment relationships:

1. The application retrieves the merchant's bank details.
2. The bank transfer processor is invoked.
3. The current implementation simulates the bank transfer.
4. A synthetic bank reference is generated.
5. The settlement status is updated to `TRANSFER_PENDING`.

At this point, the transfer has been initiated, but settlement completion has not yet been confirmed.

### Step 9: Bank Transfer Resolution and Settlement Completion

The application has a bank settlement callback simulator that periodically checks pending settlements and invokes the settlement resolution logic.

This is an internal simulation, not a real bank integration.

```text
Bank Settlement Callback Simulator
                |
                v
      Find TRANSFER_PENDING Settlement
                |
                v
          resolveTransfer()
                |
                v
       Check Settlement Status
                |
          +-----+-----+
          |           |
          v           v
        Success     Failure
          |           |
          v           v
    Set PROCESSED   Set FAILED
          |
          v
    Set processedAt
          |
          v
    Fetch Linked Payments
          |
          v
    Apply SETTLE Transition
          |
          v
    Update settledAt
          |
          v
    Save Payment Updates
          |
          v
    Publish Settlement Event
```

On successful transfer resolution:

1. The settlement is loaded from the database.
2. Its current status is checked.
3. The settlement status is updated to `PROCESSED`.
4. The processing timestamp is recorded.
5. Payments associated with the settlement are retrieved.
6. The payment transition service applies the `SETTLE` event to each payment.
7. Each payment's `settledAt` timestamp is updated.
8. The payment updates are saved.
9. A `SETTLEMENT_PROCESSED` event is published through the outbox.

If the transfer fails:

1. The settlement status is updated to `FAILED`.
2. The failure reason is recorded.
3. A `SETTLEMENT_FAILED` event is published through the outbox.

The important distinction is that initiating a bank transfer does not mean the settlement is completed.

Settlement completion happens after the successful transfer result is resolved and the linked payments are updated.

### Step 10: Webhook Event Processing

After the settlement event is published to Kafka, the webhook consumer processes the business event.

The consumer subscribes to the configured event topics.

```yaml
app:
  kafka:
    topics:
      payment: payments.events
      order: order.events
      refund: refund.events
      settlement: settlement.events
```

The current webhook consumer handles events from the configured payment, order, refund, and settlement topics.

The presence of a configured topic does not mean that its corresponding business workflow has been implemented.

Webhook processing flow:

```text
Kafka
  |
  v
WebhookKafkaConsumer
  |
  v
Read Event Envelope
  |
  v
Extract Merchant ID and Event Data
  |
  v
Find Merchant Webhook Configuration
  |
  v
Create WebhookEvent Record
  |
  v
Save in PostgreSQL
  |
  v
Add Event ID to Redis Retry Queue
  |
  v
Acknowledge Kafka Message
```

The consumer does not directly send the HTTP callback.

Its responsibility is to create and schedule the webhook event for delivery.

### Step 11: Webhook Delivery and Retry

A separate webhook delivery process retrieves due event IDs from the Redis retry queue.

```text
Redis Retry Queue
       |
       v
 Poll Due Webhook Events
       |
       v
 Fetch Webhook Event
       |
       v
 Prepare Request Payload
       |
       v
 Generate Webhook Signature
       |
       v
 Send HTTP Request
       |
       +----------------------+
       |                      |
       v                      v
   Successful               Failed
       |                      |
       v                      v
 Mark Delivered          Schedule Retry
                              |
                              v
                       Redis Retry Queue
```

The delivery process:

1. Polls webhook events that are due for delivery.
2. Retrieves the webhook event and merchant configuration.
3. Prepares the outgoing request.
4. Generates the webhook signature.
5. Sends the HTTP request to the merchant's callback URL.
6. Updates the event status based on the delivery result.
7. Schedules another attempt when delivery fails, according to the retry logic.

This separates webhook delivery from the original payment or settlement operation.

---

## 4. Other Implemented Features

### API-Key Management

The application supports:

- API-key generation.
- API-key listing.
- API-key revocation.
- API-key rotation.
- Grace period during key rotation.

### Card Vault

The vault functionality demonstrates secure handling of card-related information.

The implementation uses encryption and tokenization concepts, including AES-based encryption and encryption-key management.

The application is designed not to store raw card PAN data as ordinary application data.

This is an educational implementation and is not independently certified for PCI DSS compliance.

### Rate Limiting

The application includes Redis-backed rate-limiting implementations.

The purpose is to control request frequency and protect APIs from excessive traffic.

### Idempotency

Idempotency helps ensure that retrying the same operation does not accidentally execute the same business action multiple times.

Redis is used to store and coordinate idempotency state.

### Concurrency Handling

The application uses database locking for selected payment operations to reduce the risk of concurrent transactions modifying the same payment records incorrectly.

---

## 5. Design Patterns and Engineering Concepts

| Pattern / Concept | Usage | Purpose |
|---|---|---|
| Strategy Pattern | Payment adapters and processors | Select the appropriate implementation based on payment method |
| Transactional Outbox | Event publishing | Persist business changes and event records atomically |
| State Transition | Payment lifecycle | Control valid payment status changes |
| Idempotency | Payment requests | Prevent duplicate business operations on retries |
| Distributed Rate Limiting | Redis | Control request frequency across application instances |
| Pessimistic Locking | Selected payment operations | Coordinate concurrent database updates |
| Scheduled Processing | Settlement and outbox jobs | Execute background operations |
| Event-Driven Architecture | Kafka | Decouple business operations from downstream processing |
| Dead Letter Queue | Kafka consumer failure handling | Isolate messages that repeatedly fail processing |
| Retry Queue | Webhook delivery | Retry failed callback deliveries |

---

## 6. Kafka Topics

The application configures the following business-event topics:

| Topic | Purpose |
|---|---|
| `payments.events` | Payment-related events |
| `order.events` | Order-related events |
| `refund.events` | Configured refund event topic |
| `settlement.events` | Settlement-related events |

The webhook consumer subscribes to these topics and creates webhook delivery records for relevant merchants.

The bank transfer callback simulator currently invokes settlement resolution directly. It is separate from the Kafka consumer that processes settlement business events for webhook delivery.

---

## 7. Project Structure

The project uses feature-oriented package organization under `com.ajay.developer.razorpay`.

```text
razorpay/
├── .idea/
├── .mvn/
├── .vscode/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ajay/
│   │   │           └── developer/
│   │   │               └── razorpay/
│   │   │                   ├── common/
│   │   │                   ├── merchant/
│   │   │                   ├── operations/
│   │   │                   ├── payment/
│   │   │                   ├── vault/
│   │   │                   └── RazorpayApplication.java
│   │   └── resources/
│   └── test/
├── target/
├── .env
├── .gitattributes
├── .gitignore
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

### Package Responsibilities

| Package | Responsibility |
|---|---|
| `common` | Shared application components and reusable functionality |
| `merchant` | Merchant-related functionality, onboarding, and API-key management |
| `operations` | Operational workflows such as settlement and webhook processing |
| `payment` | Order and payment-related functionality and processing |
| `vault` | Card vault and encryption-related functionality |

`RazorpayApplication.java` is the main Spring Boot application entry point.

The project uses feature-oriented packages rather than creating a separate top-level service package for every business domain.

---

## 8. Running the Project

### Prerequisites

- Java 25
- Maven
- PostgreSQL
- Redis
- Docker and Docker Compose
- Apache Kafka

### Configure Application Properties

Provide the required configuration through environment variables or local application configuration.

Example:

```yaml
spring:
  application:
    name: razorpay

  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: update

app:
  kafka:
    topics:
      payment: payments.events
      order: order.events
      refund: refund.events
      settlement: settlement.events
```

Configure Redis, Kafka bootstrap servers, security secrets, encryption keys, and other required properties before starting the application.

Do not commit real credentials, API secrets, JWT signing keys, or encryption keys to GitHub.

### Start Infrastructure

Start the configured services using the project's Docker Compose file.

```bash
docker compose -f services.docker-compose.yaml.yml up -d
```

### Run the Spring Boot Application

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

---

## 9. API Documentation

After starting the application, use Springdoc OpenAPI to inspect the available API operations.

Swagger UI is typically available at:

```text
http://localhost:8080/swagger-ui/index.html
```

The actual endpoint depends on the application's OpenAPI configuration.

---

## 10. Implementation Scope

This project is an educational payment gateway implementation. Some external integrations are simulated rather than connected to real payment networks or financial institutions.

Current implementation notes:

- Merchant registration and API-key management are implemented.
- Order creation and payment processing are implemented.
- Redis-based idempotency and rate limiting are implemented.
- Payment adapters use the Strategy Pattern.
- Kafka event publishing uses the transactional outbox approach.
- Kafka consumer failure handling includes DLQ concepts.
- Settlement processing and payment settlement updates are implemented.
- Bank transfers are simulated.
- Settlement completion is driven by the internal callback simulator.
- Webhook event creation and delivery use Kafka, PostgreSQL, and Redis.
- Refund processing has not yet been implemented.

The architecture demonstrates important payment-system concepts, but the project is not a production-certified payment platform.

---

## 11. Author

**Ajay Kumar — Java Backend Developer**

- GitHub: https://github.com/ajay-1dev
- LinkedIn: https://www.linkedin.com/in/ajaypayyabula/
- LeetCode: https://leetcode.com/u/ajaypayyabula/
- HackerRank: https://www.hackerrank.com/profile/ajaypayyabula/