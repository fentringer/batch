package com.example.demo.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Listener to monitor Job execution
 * Logs information before and after execution
 */
@Component
public class ETLJobListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(ETLJobListener.class);
    private LocalDateTime startTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = LocalDateTime.now();
        logger.info("╔════════════════════════════════════════════════════════╗");
        logger.info("║         STARTING ETL JOB - SPRING BATCH                ║");
        logger.info("╚════════════════════════════════════════════════════════╝");
        logger.info("Job ID: {}", jobExecution.getJobId());
        logger.info("Job Name: {}", jobExecution.getJobInstance().getJobName());
        logger.info("Start Time: {}", startTime);
        logger.info("Parameters: {}", jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);

        logger.info("╔════════════════════════════════════════════════════════╗");
        logger.info("║         ETL JOB COMPLETED - SPRING BATCH               ║");
        logger.info("╚════════════════════════════════════════════════════════╝");
        logger.info("Status: {}", jobExecution.getStatus());
        logger.info("End Time: {}", endTime);
        logger.info("Duration: {} seconds", duration.getSeconds());
        logger.info("Read Count: {}", jobExecution.getStepExecutions().stream()
                .mapToLong(step -> step.getReadCount()).sum());
        logger.info("Write Count: {}", jobExecution.getStepExecutions().stream()
                .mapToLong(step -> step.getWriteCount()).sum());
        logger.info("Skip Count: {}", jobExecution.getStepExecutions().stream()
                .mapToLong(step -> step.getSkipCount()).sum());

        if (jobExecution.getStatus().isUnsuccessful()) {
            logger.error("Job failed with exceptions:");
            jobExecution.getAllFailureExceptions().forEach(e ->
                logger.error("Exception: {}", e.getMessage(), e)
            );
        }
    }
}

