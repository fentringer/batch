package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for ETL responses
 * Encapsulates ETL job execution results
 */
public class ETLResponse {

    private String status;
    private String exitStatus;
    private String message;
    private Long jobId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer readCount;
    private Integer writeCount;
    private Integer skipCount;
    private Integer duplicateCount;
    private String filename;
    private List<String> errors;
    private List<String> duplicates;

    private ETLResponse() {
        // Private constructor - use builder
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        if (status != null) map.put("status", status);
        if (exitStatus != null) map.put("exitStatus", exitStatus);
        if (message != null) map.put("message", message);
        if (jobId != null) map.put("jobId", jobId);
        if (startTime != null) map.put("startTime", startTime);
        if (endTime != null) map.put("endTime", endTime);
        if (readCount != null) map.put("readCount", readCount);
        if (writeCount != null) map.put("writeCount", writeCount);
        if (skipCount != null) map.put("skipCount", skipCount);
        if (duplicateCount != null) map.put("duplicateCount", duplicateCount);
        if (filename != null) map.put("filename", filename);
        if (errors != null && !errors.isEmpty()) map.put("errors", errors);
        if (duplicates != null && !duplicates.isEmpty()) map.put("duplicates", duplicates);

        return map;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ETLResponse response = new ETLResponse();

        public Builder status(String status) {
            response.status = status;
            return this;
        }

        public Builder exitStatus(String exitStatus) {
            response.exitStatus = exitStatus;
            return this;
        }

        public Builder message(String message) {
            response.message = message;
            return this;
        }

        public Builder jobId(Long jobId) {
            response.jobId = jobId;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            response.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            response.endTime = endTime;
            return this;
        }

        public Builder readCount(Integer readCount) {
            response.readCount = readCount;
            return this;
        }

        public Builder writeCount(Integer writeCount) {
            response.writeCount = writeCount;
            return this;
        }

        public Builder skipCount(Integer skipCount) {
            response.skipCount = skipCount;
            return this;
        }

        public Builder duplicateCount(Integer duplicateCount) {
            response.duplicateCount = duplicateCount;
            return this;
        }

        public Builder filename(String filename) {
            response.filename = filename;
            return this;
        }

        public Builder errors(List<String> errors) {
            response.errors = errors;
            return this;
        }

        public Builder duplicates(List<String> duplicates) {
            response.duplicates = duplicates;
            return this;
        }

        public ETLResponse build() {
            return response;
        }
    }

    // Getters
    public String getStatus() { return status; }
    public String getExitStatus() { return exitStatus; }
    public String getMessage() { return message; }
    public Long getJobId() { return jobId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Integer getReadCount() { return readCount; }
    public Integer getWriteCount() { return writeCount; }
    public Integer getSkipCount() { return skipCount; }
    public Integer getDuplicateCount() { return duplicateCount; }
    public String getFilename() { return filename; }
    public List<String> getErrors() { return errors; }
    public List<String> getDuplicates() { return duplicates; }
}

