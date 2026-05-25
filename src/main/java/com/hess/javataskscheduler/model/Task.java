package com.hess.javataskscheduler.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String type;

    // This tells Hibernate to treat this as Postgres JSONB
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "run_at")
    private OffsetDateTime runAt;

    @Column(name = "last_heartbeat")
    private OffsetDateTime lastHeartbeat;

    @Column(name = "locked_by")
    private String lockedBy;

    @Column(name = "retry_count")
    private int retryCount = 0;

    @Column(name = "max_retries")
    private int maxRetries = 3;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    // TODO: Generate your Getters and Setters here using your IDE

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        if (this.runAt == null) {
            this.runAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}