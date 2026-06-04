package com.hess.javataskscheduler.handlers;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TaskHandlerRegistry {

    private final Map<String, TaskHandler> handlersByType;

    public TaskHandlerRegistry(List<TaskHandler> handlers) {
        this.handlersByType = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        TaskHandler::getTaskType,
                        Function.identity()
                ));
    }

    public Optional<TaskHandler> findHandler(String taskType) {
        return Optional.ofNullable(handlersByType.get(taskType));
    }
}
