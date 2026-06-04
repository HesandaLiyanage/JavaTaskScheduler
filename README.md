# Java Task Scheduler

A learning-focused Java Spring Boot project that builds a resilient background task scheduler using PostgreSQL as the durable state machine. The long-term goal is to support delayed jobs, safe concurrent workers, retries, heartbeat-based crash recovery, and idempotent task submission.

## Project Goal

This project is based on a distributed task scheduler architecture. The scheduler is designed around a simple but powerful idea: every background job lives in the database, and workers safely claim jobs by moving them through a strict lifecycle.

Target task lifecycle:

```text
PENDING -> RUNNING -> COMPLETED
                  \
                   -> FAILED
```

The architecture is intended to provide:

- A REST API for submitting background tasks.
- PostgreSQL-backed task persistence.
- Idempotency protection for duplicate task submissions.
- `SKIP LOCKED` row-level locking so multiple workers can safely poll for work.
- A worker engine that executes task handlers.
- Heartbeats while tasks are running.
- A reaper process that recovers stuck or orphaned tasks.
- Retry handling and permanent failure after max retries.

## Current Implementation State

The project currently has the foundation in place, but the full worker engine is not implemented yet.

Already implemented:

- Spring Boot application setup.
- REST endpoint for creating tasks.
- `Task` entity mapped to the `tasks` table.
- `TaskStatus` enum with `PENDING`, `RUNNING`, `COMPLETED`, and `FAILED`.
- `TaskRepository` with a PostgreSQL `SKIP LOCKED` query for safely acquiring pending tasks.
- Reaper queries for rescuing stale `RUNNING` tasks or marking exhausted tasks as `FAILED`.
- A `TaskHandler` interface.
- A sample `GeneratePdfHandler`.
- Basic idempotency support through `idempotencyKey`.

Not implemented yet:

- Flyway database migration for creating the schema.
- Worker poller that calls `TaskRepository.acquireTasks(...)`.
- In-memory `BlockingQueue` for handing tasks from the poller to executors.
- Execution engine using Java threads or virtual threads.
- Handler registry that maps task types to handler implementations.
- Heartbeat updates while a task is running.
- Repository methods for marking tasks `COMPLETED`, retrying failed tasks, or permanently failing tasks.
- Docker Compose setup for PostgreSQL and multiple worker nodes.

## Current Flow

At the moment, the application flow is:

```text
Client sends POST /api/tasks
        |
        v
TaskController receives request
        |
        v
TaskProducerService creates a Task entity
        |
        v
TaskRepository saves the task as PENDING
        |
        v
Task waits in PostgreSQL
```

The current code can create queued tasks, but it does not yet execute them. The missing piece is the worker engine that should poll the database, acquire pending tasks, call the correct `TaskHandler`, and update the task status afterward.

## Important Components

### Producer API

The producer API accepts task requests and stores them in PostgreSQL.

```text
TaskController -> TaskProducerService -> TaskRepository
```

### Task Repository

The repository contains the most important database query in the scheduler: the task acquisition query. It uses PostgreSQL `FOR UPDATE SKIP LOCKED` so multiple workers can safely claim different tasks without processing the same task at the same time.

### Task Reaper

The reaper is a scheduled cleanup process. It looks for tasks that are stuck in `RUNNING` state with an old heartbeat. Those tasks are either moved back to `PENDING` for retry or marked as `FAILED` when retries are exhausted.

### Task Handlers

Task handlers contain the actual business logic for each task type. For example, `GeneratePdfHandler` represents a fake PDF generation job. The handler exists, but no worker currently calls it.

## Next Steps

The next implementation steps are:

1. Add Flyway and create the database schema migration.
2. Add repository methods for completion, retry, failure, and heartbeat updates.
3. Build a handler registry.
4. Add a task queue.
5. Add a scheduled worker poller.
6. Add the task execution engine.
7. Add heartbeat handling.
8. Add Docker Compose for PostgreSQL and multiple app workers.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

## Repository Description

Resilient Java Spring Boot background task scheduler using PostgreSQL, row-level locking, retries, and crash recovery.
