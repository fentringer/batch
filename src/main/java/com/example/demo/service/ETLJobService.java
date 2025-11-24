package com.example.demo.service;

import com.example.demo.dto.ETLResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for executing ETL jobs using Spring Batch
 * Encapsulates all Spring Batch job execution logic
 */
@Service
public class ETLJobService {

    private static final Logger logger = LoggerFactory.getLogger(ETLJobService.class);

    private final JobLauncher jobLauncher;
    private final Job importPersonJob;

    public ETLJobService(JobLauncher jobLauncher, Job importPersonJob) {
        this.jobLauncher = jobLauncher;
        this.importPersonJob = importPersonJob;
    }

    /**
     * Execute ETL job for a given CSV file
     * @param fileName Name of the CSV file to process
     * @return ETL execution response with statistics
     * @throws Exception if job execution fails
     */
    public ETLResponse executeJob(String fileName) throws Exception {
        logger.info("Starting ETL job for file: {}", fileName);

        JobParameters jobParameters = buildJobParameters(fileName);
        JobExecution jobExecution = jobLauncher.run(importPersonJob, jobParameters);

        logger.info("ETL Job completed with status: {}", jobExecution.getStatus());

        return buildResponse(jobExecution);
    }

    /**
     * Get job configuration information
     */
    public JobInfo getJobInformation() {
        return new JobInfo(
            importPersonJob.getName(),
            "ETL Job using Spring Batch to import persons from CSV",
            importPersonJob.isRestartable(),
            "Reader -> Processor -> Writer with Chunk Processing",
            5
        );
    }

    private JobParameters buildJobParameters(String fileName) {
        return new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("runId", UUID.randomUUID().toString())
                .addString("csvFile", fileName.endsWith(".csv") ? fileName : fileName + ".csv")
                .toJobParameters();
    }

    private ETLResponse buildResponse(JobExecution jobExecution) {
        ETLResponse.Builder builder = ETLResponse.builder()
                .status(jobExecution.getStatus().toString())
                .exitStatus(jobExecution.getExitStatus().getExitCode())
                .message("ETL Job executed via Spring Batch")
                .jobId(jobExecution.getJobId())
                .startTime(jobExecution.getStartTime())
                .endTime(jobExecution.getEndTime());

        addStepStatistics(builder, jobExecution);

        return builder.build();
    }

    private void addStepStatistics(ETLResponse.Builder builder, JobExecution jobExecution) {
        jobExecution.getStepExecutions().stream()
                .findFirst()
                .ifPresent(step ->
                    builder.readCount((int) step.getReadCount())
                           .writeCount((int) step.getWriteCount())
                           .skipCount((int) step.getSkipCount())
                );
    }

    /**
     * Simple DTO for job information
     */
    public record JobInfo(
        String jobName,
        String description,
        boolean restartable,
        String architecture,
        int chunkSize
    ) {}
}

