package com.hess.javataskscheduler.dto;

public record TaskRequest (
    String type,
    String payload,         // We will pass this as a raw JSON string for now
    String idempotencyKey
) {}