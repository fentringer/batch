# ETL Application with Spring Batch

A complete ETL (Extract, Transform, Load) application built with Spring Boot, Spring Batch, JPA, and Oracle Database.

## 🚀 Features

- **Spring Batch ETL Pipeline**: Processes CSV data with chunk-based processing
- **RESTful API**: CRUD operations for Person entities
- **Oracle Database**: Production-ready database integration
- **React Frontend**: Modern UI for data management
- **Docker Support**: Containerized deployment
- **Comprehensive Monitoring**: Detailed logging and job execution tracking

## 📋 Prerequisites

- Docker & Docker Compose (for easy setup with `start.sh`)
- **OR** for manual setup:
  - Java 17+
  - Maven 3.6+
  - Oracle Database
  - Node.js 14+ (for frontend)

## 🎯 What is `start.sh`?

An automated script that **starts the entire application stack** with one command!

**What it does:**
1. ✅ Checks if Docker is running
2. ✅ Starts all containers (Oracle DB + Backend + Frontend)
3. ✅ Waits for each service to be fully ready
4. ✅ Shows you the URLs when everything is up

**No manual configuration needed!** The script handles everything automatically.

## 🏗️ Architecture

### Spring Batch Components

```
ETL Pipeline:
┌─────────┐     ┌───────────┐     ┌─────────┐
│ CSV     │────▶│ Transform │────▶│ Database│
│ Reader  │     │ Processor │     │ Writer  │
└─────────┘     └───────────┘     └─────────┘
   EXTRACT         TRANSFORM          LOAD
```

### Project Structure

```
src/main/java/com/example/demo/
├── batch/
│   ├── config/          # Batch configuration
│   ├── job/             # Job definitions
│   ├── reader/          # Data readers (Extract)
│   ├── processor/       # Data transformers (Transform)
│   ├── writer/          # Data writers (Load)
│   ├── listener/        # Monitoring listeners
│   └── model/           # DTOs
├── controller/          # REST endpoints
├── model/              # JPA entities
├── repository/         # Data access layer
└── service/            # Business logic
```

## ⚙️ Configuration

### Database Setup

Edit `src/main/resources/application.properties`:

```properties
# Oracle Database
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
spring.datasource.username=system
spring.datasource.password=oracle

# Spring Batch
spring.batch.job.enabled=false
spring.batch.jdbc.initialize-schema=always
```

### CSV Data

The ETL process reads data from `src/main/resources/data/data.csv`.

**Sample data included:**

```csv
name
joão silva
maria santos
pedro oliveira
ana costa
carlos pereira
lucia fernandes
fernando almeida
juliana rodrigues
rafael souza
camila azevedo
```

**What the ETL does:**
1. **Extract**: Reads names from the CSV file
2. **Transform**: Capitalizes each name (e.g., "joão silva" → "João Silva")
3. **Load**: Saves the transformed data to Oracle database

**Note**: The CSV file uses UTF-8 encoding and supports special characters (Portuguese names with accents).

## 🚀 Quick Start

### 1. Compile the Project

```bash
mvn clean compile
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

### 3. Run the Frontend (Optional)

```bash
cd frontend
npm install
npm start
```

## 📡 API Endpoints

### ETL Operations

```bash
# Execute ETL Job
POST http://localhost:8080/etl/run

# Get Job Information
GET http://localhost:8080/etl/info
```

### Person CRUD

```bash
# Create Person
POST http://localhost:8080/person/create?name=John%20Doe

# Get All Persons
GET http://localhost:8080/person/all

# Get Person by ID
GET http://localhost:8080/person/id/1

# Update Person
PUT http://localhost:8080/person/1?name=Jane%20Doe

# Delete Person
DELETE http://localhost:8080/person/1

# Delete All Persons
DELETE http://localhost:8080/person/all
```

## 📊 ETL Process

### How It Works

The ETL processes data in chunks of 5 records using Spring Batch:

1. **Extract** - Reads CSV file (`PersonCSVReader`)
2. **Transform** - Capitalizes names (`PersonDataProcessor`)
3. **Load** - Saves to Oracle DB (`PersonDatabaseWriter`)

Each chunk (5 records) is processed and committed as a transaction.

### Spring Batch Features
- ✅ Chunk-based processing (5 records per batch)
- ✅ Automatic transaction management
- ✅ Fault tolerance (skip up to 10 errors)
- ✅ Job execution tracking with statistics
- ✅ Detailed logging

### Example Response

```json
{
  "status": "COMPLETED",
  "exitStatus": "COMPLETED",
  "message": "ETL Job executed via Spring Batch",
  "jobId": 1,
  "readCount": 10,
  "writeCount": 10,
  "skipCount": 0,
  "startTime": "2025-11-24T10:00:00",
  "endTime": "2025-11-24T10:00:05"
}
```

## 🔍 Spring Batch Concepts

### Job
The complete batch processing unit containing one or more Steps.

### Step
A processing phase with Reader → Processor → Writer.

### Chunk
Batch size (5 records) processed before committing.

### Listeners
Monitor job execution at different levels:
- **JobExecutionListener**: Monitors entire job
- **StepExecutionListener**: Monitors each step
- **ItemProcessListener**: Monitors individual items

## 📦 Database Tables

Spring Batch creates these tables automatically:

- `BATCH_JOB_INSTANCE`
- `BATCH_JOB_EXECUTION`
- `BATCH_JOB_EXECUTION_PARAMS`
- `BATCH_STEP_EXECUTION`
- `BATCH_JOB_EXECUTION_CONTEXT`
- `BATCH_STEP_EXECUTION_CONTEXT`

Query these tables to see job execution history!


## 🧪 Testing

```bash
# Run tests
mvn test

# Test ETL endpoint
curl -X POST http://localhost:8080/etl/run

# Test Person API
curl http://localhost:8080/person/all
```

## 📈 Performance Features

- **Chunk Processing**: Reduces memory usage
- **Transaction Management**: Automatic commits per chunk
- **Fault Tolerance**: Skip up to 10 errors
- **Parallel Processing**: Can be configured for multi-threading

## 🛠️ Customization

### Change Chunk Size

Edit `ETLJobConfiguration.java`:

```java
.chunk(10, transactionManager) // Change from 5 to 10
```

### Add Data Validation

Edit `PersonDataProcessor.java`:

```java
if (name.length() < 3) {
    return null; // Skip short names
}
```

### Add More Steps

Edit `ETLJobConfiguration.java`:

```java
.start(etlStep())
.next(validationStep())
.next(reportStep())
```

## 📚 Additional Resources

- [Spring Batch Documentation](https://spring.io/projects/spring-batch)
- [Spring Boot Reference](https://spring.io/projects/spring-boot)
- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
- [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Creating a Batch Service](https://spring.io/guides/gs/batch-processing/)

## 🆘 Troubleshooting

### Docker Issues

```bash
# Check if Docker is running
docker info

# View container logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f oracle

# Restart all services
docker-compose down
./start.sh
```

### Database Connection Issues

```bash
# If using start.sh, wait for Oracle to be fully ready (30-60 seconds)
# The script will wait automatically

# Check Oracle manually
docker exec oracle-xe bash -c "echo 'SELECT 1 FROM DUAL;' | sqlplus -s system/oracle@//localhost:1521/XEPDB1"
```

### Port Already in Use

```bash
# Check what's using the port
sudo lsof -i :8080  # Backend
sudo lsof -i :3000  # Frontend
sudo lsof -i :1521  # Oracle

# Kill the process or change port in application.properties
```

## ✨ Features Highlights

- ✅ Production-ready Spring Batch implementation
- ✅ Comprehensive error handling
- ✅ Detailed logging and monitoring
- ✅ RESTful API design
- ✅ Docker containerization
- ✅ React frontend integration
- ✅ Oracle Database support
- ✅ Chunk-based processing
- ✅ Transaction management
- ✅ Fault tolerance

## 🤝 Contributing

1. Fork the project
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📝 License

This project is open source and available under the MIT License.

---

**Happy Coding! 🚀**

