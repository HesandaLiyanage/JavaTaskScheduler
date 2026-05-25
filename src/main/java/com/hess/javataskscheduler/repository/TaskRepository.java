package com.hess.javataskscheduler.repository;

import com.hess.javataskscheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}