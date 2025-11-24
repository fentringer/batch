package com.example.demo.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

/**
 * Main Spring Batch Configuration
 * Enables batch processing in Spring Boot
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    // This class enables Spring Batch and allows job creation
    // Specific job configurations will be in separate classes
}

