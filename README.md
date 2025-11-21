High-Scale Link Distribution System
A high-performance, distributed URL shortener built to handle massive read traffic and concurrent write operations. Designed with a focus on scalability, low latency, and data consistency using industry-standard caching patterns.

System Architecture
This system moves beyond basic CRUD by implementing advanced backend patterns to solve specific scaling bottlenecks:

Getty Images
Explore

1. Distributed ID Generation (Collision-Free)
Problem: Random UUIDs cause collisions and require expensive DB checks.

Solution: Implemented a Distributed Counter using Redis (INCR) combined with Base62 Encoding.

Result: Guaranteed uniqueness, sequential IDs (q0V, q0W), and O(1) generation time without database locks.

2. Caching Strategy (Cache-Aside Pattern)
Problem: Hitting PostgreSQL for every redirect (Read) creates high latency and disk I/O bottlenecks.

Solution: All redirects are cached in Redis with a TTL (Time-To-Live).

Result: Sub-millisecond read latency for hot links. Database traffic is reduced by ~90%.

3. Async Analytics (Write-Behind Pattern)
Problem: Incrementing click counters in the DB synchronously (UPDATE links...) locks rows and slows down redirects.

Solution: Clicks are counted atomically in Redis in real-time. A background scheduler flushes these counts to PostgreSQL in batches every 10 seconds.

Result: The "Redirect" API remains lightning-fast, decoupling user latency from database write performance.

4. Security (Rate Limiting)
Problem: API abuse (spam bots) can exhaust resources.

Solution: Implemented a Fixed-Window Rate Limiter using Redis expiry keys. Blocks IPs exceeding 10 requests/minute.

Tech Stack
Language: Java 25 (OpenJDK)

Framework: Spring Boot 3.x

Database: PostgreSQL 15 (Alpine)

Cache/Broker: Redis (Alpine)

Containerization: Docker & Docker Compose

Getting Started
You do not need Java or Maven installed. The entire system is containerized.

Prerequisites
Docker Desktop (or Docker Engine + Compose)

Installation
Clone the repository:

Bash

git clone https://github.com/YOUR_USERNAME/high-scale-link-shortener.git
cd high-scale-link-shortener
Start the infrastructure:

Bash

docker compose up --build
Wait until you see: Started DemoApplication in ... seconds

🔌 API Documentation
1. Shorten a Link
POST /api/v1/shorten

Bash

curl -X POST http://localhost:8080/api/v1/shorten \
     -H "Content-Type: application/json" \
     -d '{"longUrl": "https://www.google.com"}'
Response:

JSON

{
  "shortCode": "q0V",
  "longUrl": "https://www.google.com",
  "clickCount": 0
}
2. Redirect (Open in Browser)
GET http://localhost:8080/{shortCode}

Redirects to the original URL (302 Found).

3. View Real-Time Analytics
GET /api/v1/shorten/{shortCode}/stats

Fetches the hybrid count (Redis real-time buffer + Database persisted count).

Bash

curl http://localhost:8080/api/v1/shorten/q0V/stats
Testing Performance
Rate Limiter Test
To verify the security, run this loop in your terminal to simulate a spam attack:

Bash

for i in {1..12}; do
    curl -X POST http://localhost:8080/api/v1/shorten \
         -H "Content-Type: application/json" \
         -d '{"longUrl": "https://google.com"}'
    echo ""
done
Result: Request #11 will be blocked with 429 Too Many Requests.

Future Improvements
Horizontal Scaling: Deploy behind a Load Balancer (Nginx) with multiple Spring Boot replicas.

Metrics: Integrate Prometheus/Grafana to visualize Redis cache hit/miss rates.

User Accounts: Add JWT Authentication for user-specific link management.
