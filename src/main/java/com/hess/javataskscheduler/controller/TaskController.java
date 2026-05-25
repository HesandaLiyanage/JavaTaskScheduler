package com.hess.javataskscheduler.controller;

import com.hess.javataskscheduler.dto.TaskRequest;
import com.hess.javataskscheduler.service.TaskProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskProducerService taskProducerService;

    public TaskController(TaskProducerService taskProducerService) {
        this.taskProducerService = taskProducerService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest request) {
        try {
            UUID taskId = taskProducerService.submitTask(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Task successfully queued",
                    "taskId", taskId
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}