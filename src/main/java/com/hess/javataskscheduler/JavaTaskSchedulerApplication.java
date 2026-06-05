package com.hess.javataskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JavaTaskSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaTaskSchedulerApplication.class, args);
    }

}
