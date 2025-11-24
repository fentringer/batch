package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller to execute the ETL Job using Spring Batch
 *
 * This controller demonstrates how to execute a Spring Batch Job via REST API
 */
@RestController
@RequestMapping("/etl")
@CrossOrigin(origins = "*")
public class ETLController {

    private static final Logger logger = LoggerFactory.getLogger(ETLController.class);

    @Autowired
    private JobLauncher jobLauncher; // Spring Batch JobLauncher to execute jobs

    @Autowired
    private Job importPersonJob; // The job we configured in ETLJobConfiguration

    /**
     * Endpoint to execute the ETL using Spring Batch
     *
     * POST /etl/run
     *
     * How it works:
     * 1. JobLauncher starts the Job
     * 2. Job executes its Steps in the configured order
     * 3. Each Step executes Reader -> Processor -> Writer
     * 4. Listeners monitor the entire process
     * 5. Returns result to the client
     */
    @Autowired
    private com.example.demo.service.PersonService personService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadAndRunETL(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        logger.info("═══════════════════════════════════════════════");
        logger.info("ETL upload endpoint triggered");
        logger.info("Received file: {}", file.getOriginalFilename());
        logger.info("═══════════════════════════════════════════════");

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "File is empty"
                ));
            }

            if (!file.getOriginalFilename().endsWith(".csv")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Only CSV files are allowed"
                ));
            }

            // Process CSV directly without Spring Batch
            int readCount = 0;
            int writeCount = 0;
            int duplicateCount = 0;
            java.util.List<String> errors = new java.util.ArrayList<>();
            java.util.List<String> duplicates = new java.util.ArrayList<>();

            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(file.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {

                String line;
                boolean isHeader = true;

                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue; // Skip header
                    }

                    readCount++;
                    String name = line.trim();

                    if (!name.isEmpty()) {
                        try {
                            // Capitalize name
                            String[] words = name.split(" ");
                            StringBuilder capitalizedName = new StringBuilder();
                            for (String word : words) {
                                if (!word.isEmpty()) {
                                    capitalizedName.append(Character.toUpperCase(word.charAt(0)))
                                                  .append(word.substring(1).toLowerCase())
                                                  .append(" ");
                                }
                            }
                            String transformedName = capitalizedName.toString().trim();

                            // Check for duplicates (case-insensitive)
                            java.util.List<com.example.demo.model.Person> existingPersons =
                                personService.getAllPersons();

                            boolean isDuplicate = existingPersons.stream()
                                .anyMatch(p -> p.getName().equalsIgnoreCase(transformedName));

                            if (isDuplicate) {
                                duplicateCount++;
                                duplicates.add(transformedName);
                                logger.warn("⊗ DUPLICATE SKIPPED: '{}' already exists in database", transformedName);
                            } else {
                                // Save to database
                                personService.createPerson(transformedName);
                                writeCount++;
                                logger.info("✓ Processed and saved: '{}'", transformedName);
                            }
                        } catch (Exception e) {
                            errors.add("Error processing '" + name + "': " + e.getMessage());
                            logger.error("✗ Error processing: {}", name, e);
                        }
                    }
                }
            }

            logger.info("Upload processing complete: {} read, {} written, {} duplicates skipped",
                        readCount, writeCount, duplicateCount);

            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("status", errors.isEmpty() ? "COMPLETED" : "COMPLETED_WITH_ERRORS");
            response.put("exitStatus", "COMPLETED");
            response.put("message", duplicateCount > 0
                ? String.format("File processed: %d written, %d duplicates skipped", writeCount, duplicateCount)
                : "File processed successfully");
            response.put("filename", file.getOriginalFilename());
            response.put("readCount", readCount);
            response.put("writeCount", writeCount);
            response.put("skipCount", readCount - writeCount);
            response.put("duplicateCount", duplicateCount);
            if (!duplicates.isEmpty()) {
                response.put("duplicates", duplicates);
            }
            if (!errors.isEmpty()) {
                response.put("errors", errors);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing uploaded file", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "FAILED",
                "message", "Error processing file: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runETL(@RequestParam(required = false, defaultValue = "data") String file) {
        logger.info("═══════════════════════════════════════════════");
        logger.info("ETL endpoint triggered - Starting Spring Batch Job");
        logger.info("Processing file: {}.csv", file);
        logger.info("═══════════════════════════════════════════════");

        try {
            // JobParameters are used to identify each job execution
            // Adding timestamp and unique identifier ensures each execution is unique
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("runId", java.util.UUID.randomUUID().toString())
                    .addString("csvFile", file + ".csv")
                    .toJobParameters();

            // Execute the job synchronously (waits for completion)
            var jobExecution = jobLauncher.run(importPersonJob, jobParameters);

            Map<String, Object> response = new HashMap<>();
            response.put("status", jobExecution.getStatus().toString());
            response.put("exitStatus", jobExecution.getExitStatus().getExitCode());
            response.put("message", "ETL Job executed via Spring Batch");
            response.put("jobId", jobExecution.getJobId());
            response.put("startTime", jobExecution.getStartTime());
            response.put("endTime", jobExecution.getEndTime());

            // Processing statistics
            var stepExecutions = jobExecution.getStepExecutions();
            if (!stepExecutions.isEmpty()) {
                var step = stepExecutions.iterator().next();
                response.put("readCount", step.getReadCount());
                response.put("writeCount", step.getWriteCount());
                response.put("skipCount", step.getSkipCount());
            }

            logger.info("ETL Job completed with status: {}", jobExecution.getStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("ETL Job execution failed", e);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "FAILED");
            response.put("message", "ETL Job failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Additional endpoint to get information about the Job
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getJobInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("jobName", importPersonJob.getName());
        info.put("description", "ETL Job using Spring Batch to import persons from CSV");
        info.put("restartable", String.valueOf(importPersonJob.isRestartable()));
        info.put("architecture", "Reader -> Processor -> Writer with Chunk Processing");
        info.put("chunkSize", "5");

        return ResponseEntity.ok(info);
    }

    /**
     * Helper method to build response from JobExecution
     */
    private Map<String, Object> buildResponse(org.springframework.batch.core.JobExecution jobExecution) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", jobExecution.getStatus().toString());
        response.put("exitStatus", jobExecution.getExitStatus().getExitCode());
        response.put("message", "ETL Job executed via Spring Batch");
        response.put("jobId", jobExecution.getJobId());
        response.put("startTime", jobExecution.getStartTime());
        response.put("endTime", jobExecution.getEndTime());

        // Processing statistics
        var stepExecutions = jobExecution.getStepExecutions();
        if (!stepExecutions.isEmpty()) {
            var step = stepExecutions.iterator().next();
            response.put("readCount", step.getReadCount());
            response.put("writeCount", step.getWriteCount());
            response.put("skipCount", step.getSkipCount());
        }

        return response;
    }
}

