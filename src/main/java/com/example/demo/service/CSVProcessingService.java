package com.example.demo.service;

import com.example.demo.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for processing CSV files
 * Handles reading, transforming and validating CSV data
 */
@Service
public class CSVProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(CSVProcessingService.class);
    private final PersonService personService;

    public CSVProcessingService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Process an uploaded CSV file
     * @param file The CSV file to process
     * @return Processing result with statistics
     */
    public CSVProcessingResult processFile(MultipartFile file) {
        validateFile(file);

        CSVProcessingResult result = new CSVProcessingResult();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            processCSVLines(reader, result);

        } catch (Exception e) {
            logger.error("Error processing CSV file: {}", file.getOriginalFilename(), e);
            throw new CSVProcessingException("Failed to process CSV file", e);
        }

        logProcessingResult(result);
        return result;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CSVProcessingException("File is empty");
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            throw new CSVProcessingException("Only CSV files are allowed");
        }
    }

    private void processCSVLines(BufferedReader reader, CSVProcessingResult result) throws Exception {
        String line;
        boolean isHeader = true;

        while ((line = reader.readLine()) != null) {
            if (isHeader) {
                isHeader = false;
                continue;
            }

            result.incrementReadCount();
            processLine(line.trim(), result);
        }
    }

    private void processLine(String name, CSVProcessingResult result) {
        if (name.isEmpty()) {
            return;
        }

        try {
            String transformedName = capitalizeName(name);

            if (isDuplicate(transformedName)) {
                handleDuplicate(transformedName, result);
            } else {
                savePerson(transformedName, result);
            }
        } catch (Exception e) {
            handleError(name, e, result);
        }
    }

    private String capitalizeName(String name) {
        String[] words = name.split(" ");
        StringBuilder capitalizedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalizedName.append(Character.toUpperCase(word.charAt(0)))
                              .append(word.substring(1).toLowerCase())
                              .append(" ");
            }
        }

        return capitalizedName.toString().trim();
    }

    private boolean isDuplicate(String name) {
        List<Person> existingPersons = personService.getAllPersons();
        return existingPersons.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name));
    }

    private void handleDuplicate(String name, CSVProcessingResult result) {
        result.incrementDuplicateCount();
        result.addDuplicate(name);
        logger.warn("⊗ DUPLICATE SKIPPED: '{}' already exists in database", name);
    }

    private void savePerson(String name, CSVProcessingResult result) {
        personService.createPerson(name);
        result.incrementWriteCount();
        logger.info("✓ Processed and saved: '{}'", name);
    }

    private void handleError(String name, Exception e, CSVProcessingResult result) {
        String errorMessage = "Error processing '" + name + "': " + e.getMessage();
        result.addError(errorMessage);
        logger.error("✗ Error processing: {}", name, e);
    }

    private void logProcessingResult(CSVProcessingResult result) {
        logger.info("CSV processing complete: {} read, {} written, {} duplicates skipped",
                result.getReadCount(), result.getWriteCount(), result.getDuplicateCount());
    }

    /**
     * Custom exception for CSV processing errors
     */
    public static class CSVProcessingException extends RuntimeException {
        public CSVProcessingException(String message) {
            super(message);
        }

        public CSVProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Result object containing CSV processing statistics
     */
    public static class CSVProcessingResult {
        private int readCount = 0;
        private int writeCount = 0;
        private int duplicateCount = 0;
        private final List<String> errors = new ArrayList<>();
        private final List<String> duplicates = new ArrayList<>();

        public void incrementReadCount() {
            readCount++;
        }

        public void incrementWriteCount() {
            writeCount++;
        }

        public void incrementDuplicateCount() {
            duplicateCount++;
        }

        public void addError(String error) {
            errors.add(error);
        }

        public void addDuplicate(String duplicate) {
            duplicates.add(duplicate);
        }

        public int getReadCount() {
            return readCount;
        }

        public int getWriteCount() {
            return writeCount;
        }

        public int getDuplicateCount() {
            return duplicateCount;
        }

        public int getSkipCount() {
            return readCount - writeCount;
        }

        public List<String> getErrors() {
            return errors;
        }

        public List<String> getDuplicates() {
            return duplicates;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean hasDuplicates() {
            return !duplicates.isEmpty();
        }
    }
}

