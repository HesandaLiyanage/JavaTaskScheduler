package com.hess.javataskscheduler.service;

import com.hess.javataskscheduler.dto.TaskRequest;
import com.hess.javataskscheduler.model.Task;
import com.hess.javataskscheduler.repository.TaskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TaskProducerService {

    private final TaskRepository taskRepository;

    public TaskProducerService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public UUID submitTask(TaskRequest request) {
        Task task = new Task();
        task.setType(request.type());
        task.setPayload(request.payload());
        task.setIdempotencyKey(request.idempotencyKey());

        // Note: The @PrePersist in our Task entity will automatically 
        // set the status to PENDING and fill in the created_at timestamps.

        try {
            Task savedTask = taskRepository.save(task);
            return savedTask.getId();
        } catch (DataIntegrityViolationException e) {
            // This catches the PostgreSQL UNIQUE constraint violation on idempotency_key
            throw new IllegalArgumentException(
                    "A task with idempotency key '" + request.idempotencyKey() + "' already exists."
            );
        }
    }
}