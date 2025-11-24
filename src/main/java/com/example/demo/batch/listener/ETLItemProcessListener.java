package com.example.demo.batch.listener;

import com.example.demo.batch.model.PersonCSVData;
import com.example.demo.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

/**
 * Listener to monitor the processing of each item
 * Useful for debugging and auditing
 */
@Component
public class ETLItemProcessListener implements ItemProcessListener<PersonCSVData, Person> {

    private static final Logger logger = LoggerFactory.getLogger(ETLItemProcessListener.class);

    @Override
    public void beforeProcess(PersonCSVData item) {
        logger.debug("→ Processing: {}", item);
    }

    @Override
    public void afterProcess(PersonCSVData item, Person result) {
        if (result != null) {
            logger.debug("✓ Successfully processed: {} -> {}", item.getRawName(), result.getName());
        } else {
            logger.warn("⊗ Item was filtered/skipped: {}", item.getRawName());
        }
    }

    @Override
    public void onProcessError(PersonCSVData item, Exception e) {
        logger.error("✗ Error processing item: {}", item, e);
    }
}

