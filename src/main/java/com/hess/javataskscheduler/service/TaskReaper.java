package com.hess.javataskscheduler.service;

import com.hess.javataskscheduler.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TaskReaper {

    private final TaskRepository taskRepository;

    public TaskReaper(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Run this cleanup job every 15 seconds
    @Scheduled(fixedDelay = 15000)
    public void reapDeadTasks() {

        // 1. First, permanently fail any tasks that have no retries left
        int failedCount = taskRepository.failExhaustedTasks();
        if (failedCount > 0) {
            System.err.println("☠️ Reaper permanently failed " + failedCount + " exhausted tasks.");
        }

        // 2. Then, rescue any dead tasks that can still be retried
        int rescuedCount = taskRepository.requeueOrphanedTasks();
        if (rescuedCount > 0) {
            System.out.println("🧟 Reaper rescued and re-queued " + rescuedCount + " orphaned tasks.");
        }
    }
}