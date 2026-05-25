package com.hess.javataskscheduler.handlers;

import org.springframework.stereotype.Component;

@Component
public class GeneratePdfHandler implements TaskHandler {

    @Override
    public String getTaskType() {
        return "GENERATE_PDF";
    }

    @Override
    public void execute(String payload) throws Exception {
        System.out.println("🖨️ Starting PDF Generation for payload: " + payload);

        // Simulate a task that takes 5 seconds to complete
        Thread.sleep(5000);

        System.out.println("✅ Finished PDF Generation!");
    }
}