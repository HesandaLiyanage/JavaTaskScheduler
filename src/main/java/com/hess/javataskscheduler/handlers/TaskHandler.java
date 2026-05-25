package com.hess.javataskscheduler.handlers;

public interface TaskHandler {
    String getTaskType();

    void execute(String payload) throws Exception;
}