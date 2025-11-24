package com.example.demo.batch.job;

import com.example.demo.batch.listener.ETLItemProcessListener;
import com.example.demo.batch.listener.ETLJobListener;
import com.example.demo.batch.listener.ETLStepListener;
import com.example.demo.batch.model.PersonCSVData;
import com.example.demo.batch.processor.PersonDataProcessor;
import com.example.demo.batch.reader.PersonCSVReader;
import com.example.demo.batch.writer.PersonDatabaseWriter;
import com.example.demo.model.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * ETL Job Configuration using Spring Batch
 *
 * IMPORTANT CONCEPTS:
 *
 * 1. JOB: The complete batch processing unit
 *    - A Job contains one or more Steps
 *    - Can have listeners for monitoring
 *    - Is executed by the JobLauncher
 *
 * 2. STEP: A processing phase
 *    - Each Step has a Reader, Processor and Writer
 *    - Processes data in "chunks" (batches)
 *    - Can have retry, skip logic, etc.
 *
 * 3. CHUNK: Batch processing size
 *    - Defines how many items are processed before committing
 *    - Example: chunk(10) = reads 10, processes 10, writes 10, commits
 *
 * 4. READER: Reads data from source (CSV, DB, API, etc)
 * 5. PROCESSOR: Transforms the data
 * 6. WRITER: Writes data to destination
 */
@Configuration
public class ETLJobConfiguration {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private PersonCSVReader csvReader;

    @Autowired
    private PersonDataProcessor dataProcessor;

    @Autowired
    private PersonDatabaseWriter databaseWriter;

    @Autowired
    private ETLJobListener jobListener;

    @Autowired
    private ETLStepListener stepListener;

    @Autowired
    private ETLItemProcessListener itemProcessListener;

    /**
     * Defines the Reader to be used in the Step
     * Step-scoped to allow dynamic file selection (uploaded or default)
     */
    @Bean
    @StepScope
    public ItemReader<PersonCSVData> personReader(
            @Value("#{jobParameters['uploadedFile']}") String uploadedFile,
            @Value("#{jobParameters['csvFile']}") String csvFile) {

        // Priority: uploaded file > specified CSV file > default
        String filename;
        if (uploadedFile != null && !uploadedFile.isEmpty()) {
            filename = uploadedFile; // Full path to uploaded file
        } else if (csvFile != null && !csvFile.isEmpty()) {
            filename = csvFile; // Classpath resource
        } else {
            filename = "data.csv"; // Default
        }

        return csvReader.createReader(filename);
    }

    /**
     * Defines the Processor to be used in the Step
     */
    @Bean
    public ItemProcessor<PersonCSVData, Person> personProcessor() {
        return dataProcessor;
    }

    /**
     * Defines the Writer to be used in the Step
     */
    @Bean
    public ItemWriter<Person> personWriter() {
        return databaseWriter;
    }

    /**
     * Configures the ETL Step
     *
     * CHUNK SIZE = 5:
     * - Reads 5 records from CSV
     * - Processes the 5 records
     * - Writes the 5 records to database
    /**
     * Configures the ETL Step
     */
    @Bean
    public Step etlStep(ItemReader<PersonCSVData> personReader) {
        return new StepBuilder("etlStep", jobRepository)
                .<PersonCSVData, Person>chunk(5, transactionManager)
                .reader(personReader)
                .processor(personProcessor())
                .writer(personWriter())
                .listener(stepListener)
                .listener(itemProcessListener)
                .build();
    }

    /**
     * Configures the complete ETL Job
     *
     * This Job contains only 1 Step, but you can add more:
     * - Step 1: Read and validate data
     * - Step 2: Process data
     * - Step 3: Generate report
     * etc.
     */
    @Bean
    public Job importPersonJob(Step etlStep) {
        return new JobBuilder("importPersonJob", jobRepository)
                .listener(jobListener)
                .start(etlStep)
                // .next(anotherStep()) // You can chain more steps
                .build();
    }
}

