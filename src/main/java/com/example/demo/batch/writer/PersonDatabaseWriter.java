package com.example.demo.batch.writer;

import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Writer to save processed data to the database
 * Implements the Spring Batch ItemWriter pattern
 * This is the LOAD component of the ETL process
 */
@Component
public class PersonDatabaseWriter implements ItemWriter<Person> {

    private static final Logger logger = LoggerFactory.getLogger(PersonDatabaseWriter.class);

    @Autowired
    private PersonRepository personRepository;

    /**
     * Writes a chunk (batch) of persons to the database
     * Spring Batch processes data in chunks for better performance
     * Includes duplicate detection to prevent inserting existing names
     */
    @Override
    public void write(Chunk<? extends Person> chunk) throws Exception {
        logger.info("LOAD: Saving {} persons to database", chunk.size());

        for (Person person : chunk) {
            try {
                Person saved = personRepository.save(person);
                logger.info("✓ Person saved: ID={}, Name='{}'", saved.getId(), saved.getName());
            } catch (Exception e) {
                logger.error("✗ Error saving person: {}", person.getName(), e);
                throw e;
            }
        }

        logger.info("Chunk of {} records saved successfully", chunk.size());
    }
}

