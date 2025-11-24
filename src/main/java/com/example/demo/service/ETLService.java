package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class ETLService {

    private static final Logger logger = LoggerFactory.getLogger(ETLService.class);

    private PersonService personService;

    public ETLService(PersonService personService) {
        this.personService = personService;
    }

    public void runETL() {
        logger.info("========================================");
        logger.info("Starting ETL Process");
        logger.info("========================================");

        try {
            ClassPathResource resource = new ClassPathResource("data/data.csv");

            if (!resource.exists()) {
                logger.error("CSV file not found: data.csv");
                throw new RuntimeException("CSV file not found");
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            int count = 0;

            // Skip header
            reader.readLine();

            logger.info("Reading and transforming data from CSV...");

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // Extract - Read CSV
                    String name = line.trim();
                    logger.debug("Extract: Read name '{}'", name);

                    // Transform - Capitalize name
                    String transformedName = capitalizeName(name);
                    logger.debug("Transform: '{}' -> '{}'", name, transformedName);

                    // Load - Save to database
                    personService.createPerson(transformedName);
                    logger.info("Load: Saved person '{}'", transformedName);

                    count++;
                }
            }

            reader.close();

            logger.info("========================================");
            logger.info("ETL Process completed successfully");
            logger.info("Total records processed: {}", count);
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("ETL Process failed", e);
            throw new RuntimeException("ETL failed: " + e.getMessage(), e);
        }
    }

    private String capitalizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }

        return result.toString().trim();
    }
}

