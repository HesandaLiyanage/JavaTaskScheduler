package com.hess.javataskscheduler.repository;

import com.hess.javataskscheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    /**
     * THE THUNDER HERD SOLVER
     * This query locks rows in the database atomically so no two worker threads 
     * can ever grab the same task.
     */
    @Query(value = """
        UPDATE tasks
        SET 
            status = 'RUNNING',
            locked_by = :workerId,
            last_heartbeat = NOW(),
            updated_at = NOW()
        WHERE id IN (
            SELECT id 
            FROM tasks 
            WHERE status = 'PENDING' AND run_at <= NOW()
            ORDER BY created_at ASC
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
        )
        RETURNING *
        """, nativeQuery = true)
    List<Task> acquireTasks(@Param("workerId") String workerId, @Param("limit") int limit);

    /**
     * Finds tasks that have been RUNNING but haven't updated their heartbeat in 30 seconds.
     * Re-queues them (Sets back to PENDING) if they have retries left.
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE tasks
        SET 
            status = 'PENDING',
            locked_by = NULL,
            retry_count = retry_count + 1,
            error_message = 'Worker node died or timed out (Reaped)'
        WHERE 
            status = 'RUNNING' 
            AND last_heartbeat < (NOW() - INTERVAL '30 seconds')
            AND retry_count < max_retries
        """, nativeQuery = true)
    int requeueOrphanedTasks();

    /**
     * If a task has been reaped too many times (exceeded max_retries),
     * mark it as permanently FAILED so it stops clogging the system.
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE tasks
        SET status = 'FAILED'
        WHERE 
            status = 'RUNNING' 
            AND last_heartbeat < (NOW() - INTERVAL '30 seconds')
            AND retry_count >= max_retries
        """, nativeQuery = true)
    int failExhaustedTasks();
}