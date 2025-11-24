package com.example.demo.batch.processor;

import com.example.demo.batch.model.PersonCSVData;
import com.example.demo.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Processor to transform raw CSV data into Person entities
 * Implements the Spring Batch ItemProcessor pattern
 * This is the TRANSFORM component of the ETL process
 */
@Component
public class PersonDataProcessor implements ItemProcessor<PersonCSVData, Person> {

    private static final Logger logger = LoggerFactory.getLogger(PersonDataProcessor.class);

    /**
     * Transforms PersonCSVData into Person
     * Applies business rules such as name capitalization
     */
    @Override
    public Person process(PersonCSVData csvData) throws Exception {
        if (csvData == null || csvData.getRawName() == null || csvData.getRawName().trim().isEmpty()) {
            logger.warn("Invalid data found at line {}, skipping...", csvData.getLineNumber());
            return null; // Returning null skips this item
        }

        String rawName = csvData.getRawName().trim();
        String transformedName = capitalizeName(rawName);


        logger.info("TRANSFORM: '{}' -> '{}'", rawName, transformedName);

        Person person = new Person();
        person.setName(transformedName);

        return person;
    }

    /**
     * Capitalizes each word in the name
     * Example: "john doe" -> "John Doe"
     */
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

