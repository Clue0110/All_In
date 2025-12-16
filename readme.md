# All-In: Real-Time Stock Market Simulation

A full-stack, real-time stock trading simulation game built with **Java Spring Boot**, **WebSockets**, and **Docker**.

This application simulates a live market environment where multiple users can buy and sell stocks, competing for profit while navigating market volatility, "boom/crash" events, and realistic concurrency constraints.

---

## Quick Start (Running with Docker)

The easiest way to run the application is using Docker. This ensures all dependencies (Java, Maven) are handled automatically.

### Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.

### 1. Build the Image
Open your terminal in the project root folder and run:
```bash
docker build -t all-in-game .