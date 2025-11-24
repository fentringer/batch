package com.example.demo.controller;

import com.example.demo.dto.ETLResponse;
import com.example.demo.service.CSVProcessingService;
import com.example.demo.service.CSVProcessingService.CSVProcessingResult;
import com.example.demo.service.ETLJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST Controller for ETL operations
 */
@RestController
@RequestMapping("/etl")
@CrossOrigin(origins = "*")
public class ETLController {

    private static final Logger logger = LoggerFactory.getLogger(ETLController.class);

    private final CSVProcessingService csvProcessingService;
    private final ETLJobService etlJobService;

    public ETLController(CSVProcessingService csvProcessingService,
                        ETLJobService etlJobService) {
        this.csvProcessingService = csvProcessingService;
        this.etlJobService = etlJobService;
    }

    /**
     * Upload and process a CSV file
     * POST /etl/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("CSV upload request: {}", file.getOriginalFilename());

        try {
            CSVProcessingResult result = csvProcessingService.processFile(file);
            ETLResponse response = buildUploadResponse(file.getOriginalFilename(), result);
            return ResponseEntity.ok(response.toMap());

        } catch (CSVProcessingService.CSVProcessingException e) {
            logger.error("CSV processing failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "FAILED", "message", e.getMessage()));

        } catch (Exception e) {
            logger.error("Unexpected error", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "FAILED", "message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Run ETL job using Spring Batch
     * POST /etl/run?file=data
     */
    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runBatchJob(
            @RequestParam(required = false, defaultValue = "data") String file) {

        logger.info("ETL Batch job request for file: {}", file);

        try {
            ETLResponse response = etlJobService.executeJob(file);
            return ResponseEntity.ok(response.toMap());

        } catch (Exception e) {
            logger.error("ETL Job failed", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                        "status", "FAILED",
                        "message", "Job failed: " + e.getMessage(),
                        "error", e.getClass().getSimpleName()
                    ));
        }
    }

    /**
     * Get ETL job configuration info
     * GET /etl/info
     */
    @GetMapping("/info")
    public ResponseEntity<ETLJobService.JobInfo> getJobInfo() {
        return ResponseEntity.ok(etlJobService.getJobInformation());
    }

    // Helper method for upload response building
    private ETLResponse buildUploadResponse(String filename, CSVProcessingResult result) {
        String message = result.getDuplicateCount() > 0
                ? String.format("Processed: %d saved, %d duplicates skipped",
                        result.getWriteCount(), result.getDuplicateCount())
                : "File processed successfully";

        return ETLResponse.builder()
                .status(result.hasErrors() ? "COMPLETED_WITH_ERRORS" : "COMPLETED")
                .exitStatus("COMPLETED")
                .message(message)
                .filename(filename)
                .readCount(result.getReadCount())
                .writeCount(result.getWriteCount())
                .skipCount(result.getSkipCount())
                .duplicateCount(result.getDuplicateCount())
                .duplicates(result.hasDuplicates() ? result.getDuplicates() : null)
                .errors(result.hasErrors() ? result.getErrors() : null)
                .build();
    }
}

