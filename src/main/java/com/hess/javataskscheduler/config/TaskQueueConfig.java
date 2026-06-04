package com.hess.javataskscheduler.config;

import com.hess.javataskscheduler.model.Task;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class TaskQueueConfig {

    @Bean
    public BlockingQueue<Task> taskQueue() {
        return new LinkedBlockingQueue<>();
    }
}
