package com.example.demo.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Listener to monitor Step execution
 * Provides detailed metrics about processing
 */
@Component
public class ETLStepListener implements StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(ETLStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("┌─────────────────────────────────────────────┐");
        logger.info("│ Starting Step: {}", stepExecution.getStepName());
        logger.info("└─────────────────────────────────────────────┘");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("┌─────────────────────────────────────────────┐");
        logger.info("│ Step Completed: {}", stepExecution.getStepName());
        logger.info("├─────────────────────────────────────────────┤");
        logger.info("│ Status: {}", stepExecution.getStatus());
        logger.info("│ Read Count: {}", stepExecution.getReadCount());
        logger.info("│ Write Count: {}", stepExecution.getWriteCount());
        logger.info("│ Commit Count: {}", stepExecution.getCommitCount());
        logger.info("│ Rollback Count: {}", stepExecution.getRollbackCount());
        logger.info("│ Skip Count: {}", stepExecution.getSkipCount());
        logger.info("│ Filter Count: {}", stepExecution.getFilterCount());
        logger.info("└─────────────────────────────────────────────┘");

        return stepExecution.getExitStatus();
    }
}

