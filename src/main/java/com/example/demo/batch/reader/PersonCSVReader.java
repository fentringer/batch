package com.example.demo.batch.reader;

import com.example.demo.batch.model.PersonCSVData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Custom reader to read data from CSV file
 * Implements the Spring Batch ItemReader pattern
 */
@Component
public class PersonCSVReader {

    private static final Logger logger = LoggerFactory.getLogger(PersonCSVReader.class);

    /**
     * Creates an ItemReader configured to read the CSV file
     * This is the EXTRACT component of the ETL process
     *
     * @param filename CSV file name or full path
     */
    public ItemReader<PersonCSVData> createReader(String filename) {
        logger.info("Configuring CSV Reader for {}", filename);

        org.springframework.core.io.Resource resource;

        // Check if it's an absolute path (uploaded file) or classpath resource
        if (filename.startsWith("/") || filename.contains(":")) {
            // Absolute path - uploaded file
            logger.info("Loading from file system: {}", filename);
            resource = new org.springframework.core.io.FileSystemResource(filename);
        } else {
            // Classpath resource - built-in CSV
            String resourcePath = "data/" + filename;
            logger.info("Loading from classpath: {}", resourcePath);
            resource = new ClassPathResource(resourcePath);
        }

        // Verify file exists
        if (!resource.exists()) {
            String path = filename.startsWith("/") ? filename : "data/" + filename;
            logger.error("CSV file NOT FOUND: {}", path);
            throw new RuntimeException("CSV file not found: " + path);
        }

        logger.info("CSV file found: (exists: {}, isReadable: {})",
                    resource.exists(), resource.isReadable());

        FlatFileItemReader<PersonCSVData> reader = new FlatFileItemReader<>();
        reader.setResource(resource);
        reader.setName("personCSVReader");
        reader.setLinesToSkip(1); // Skip header
        reader.setEncoding("UTF-8"); // Support Portuguese characters

        // Configure how to map each CSV line
        DefaultLineMapper<PersonCSVData> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("name");
        tokenizer.setDelimiter(",");

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper((FieldSet fieldSet) -> {
            PersonCSVData data = new PersonCSVData();
            data.setRawName(fieldSet.readString("name"));
            data.setLineNumber(fieldSet.getFieldCount());
            logger.debug("Read from CSV: {}", data.getRawName());
            return data;
        });

        reader.setLineMapper(lineMapper);

        logger.info("CSV Reader configured successfully for: {}", filename);
        return reader;
    }
}

