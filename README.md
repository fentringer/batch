# 🚀 ETL Application - Spring Boot + React + Spring Batch
A modern **ETL (Extract, Transform, Load)** application built with Spring Boot, React, Spring Batch, and Oracle Database. Upload CSV files, process data with automatic duplicate detection, and manage persons through a clean, intuitive interface.
## ✨ Features
### Backend (Spring Boot)
- ✅ **Spring Batch ETL Pipeline** - Robust batch processing with chunk-oriented steps
- ✅ **CSV Upload & Processing** - Drag & drop file upload with real-time processing
- ✅ **Duplicate Detection** - Automatic case-insensitive duplicate checking
- ✅ **Oracle Database Integration** - Full CRUD operations with JPA/Hibernate
- ✅ **RESTful API** - Complete REST endpoints for all operations
- ✅ **Comprehensive Logging** - Detailed ETL process monitoring
- ✅ **Sample Data Included** - 4 pre-configured CSV files for testing
### Frontend (React)
- ✅ **Modern UI** - Clean, responsive design
- ✅ **File Upload** - Drag & drop or browse for CSV files
- ✅ **Dual Mode** - Use sample data or upload custom files
- ✅ **Real-time Statistics** - Live ETL job status and metrics
- ✅ **Person Management** - View, edit, and delete persons
- ✅ **Bulk Operations** - Delete all records with one click
### ETL Process
- 📥 **Extract**: Read CSV files (classpath or uploaded)
- 🔄 **Transform**: Capitalize names (e.g., "john doe" → "John Doe")
- 💾 **Load**: Save to Oracle database with duplicate prevention
- 📊 **Monitor**: Track read, write, skip, and duplicate counts
## 🏗️ Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                        React Frontend                        │
│              (Port 3000 - File Upload + UI)                 │
└──────────────────────────┬──────────────────────────────────┘
                           │ REST API
┌──────────────────────────▼──────────────────────────────────┐
│                   Spring Boot Backend                        │
│                      (Port 8080)                            │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Spring Batch ETL Jobs                    │  │
│  │  Reader → Processor → Writer (Chunk Size: 5)         │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────┘
                           │ JDBC
┌──────────────────────────▼──────────────────────────────────┐
│                    Oracle Database                           │
│                   (Port 1521 - XEPDB1)                      │
└─────────────────────────────────────────────────────────────┘
```
## 🛠️ Tech Stack
### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Batch** - ETL framework
- **Spring Data JPA** - Database access
- **Hibernate** - ORM
- **Oracle JDBC Driver**
- **Maven**
### Frontend
- **React 18**
- **Axios**
- **Modern CSS**
### Database
- **Oracle Database 21c XE**
### DevOps
- **Docker & Docker Compose**
## 📋 Prerequisites
- **Docker** & **Docker Compose**
- **Java 17** (if running locally)
- **Node.js 16+** (if running frontend locally)
- **Maven 3.6+** (if building locally)
## 🚀 Quick Start with Docker
Simply run the start script:
```bash
chmod +x start.sh
./start.sh
```
This will:
1. ✅ Check Docker is running
2. ✅ Start all services (Oracle, Backend, Frontend)
3. ✅ Wait for services to be ready
4. ✅ Display access URLs
**Access the application:**
- 🌐 Frontend: http://localhost:3000
- 🔧 Backend API: http://localhost:8080
- 🗄️ Oracle DB: localhost:1521/XEPDB1 (system/oracle)
## 📁 Project Structure
```
demo/
├── src/main/java/com/example/demo/
│   ├── batch/                    # Spring Batch components
│   │   ├── config/              # Batch configuration
│   │   ├── job/                 # Job definitions
│   │   ├── listener/            # Job/Step listeners
│   │   ├── model/               # CSV data models
│   │   ├── processor/           # Data transformation
│   │   ├── reader/              # CSV file readers
│   │   └── writer/              # Database writers
│   ├── controller/              # REST endpoints
│   ├── model/                   # JPA entities
│   ├── repository/              # Data access layer
│   └── service/                 # Business logic
├── src/main/resources/
│   ├── application.properties   # App configuration
│   └── data/                    # Sample CSV files
│       ├── data.csv            (10 names)
│       ├── employees.csv       (10 names)
│       ├── customers.csv       (10 names)
│       └── partners.csv        (10 names)
├── frontend/
│   ├── public/
│   └── src/
│       ├── components/
│       │   ├── ETLButton.js
│       │   ├── PersonForm.js
│       │   └── PersonList.js
│       ├── App.js
│       └── index.js
├── docker-compose.yml
├── Dockerfile
├── start.sh                     # Quick start script
└── README.md
```
## 🔌 API Endpoints
### Person Management
```bash
# Get all persons
GET /person/all
# Get person by ID
GET /person/{id}
# Create person
POST /person
Body: { "name": "John Doe" }
# Update person
PUT /person/{id}
Body: { "name": "Jane Doe" }
# Delete person
DELETE /person/{id}
# Delete all persons
DELETE /person/all
```
### ETL Operations
```bash
# Run ETL with sample data (data.csv)
POST /etl/run
# Upload and process custom CSV file
POST /etl/upload
Content-Type: multipart/form-data
Body: file=@yourfile.csv
# Get ETL job info
GET /etl/info
```
## 📊 CSV File Format
Your CSV files should follow this format:
```csv
name
João Silva
Maria Santos
Pedro Oliveira
```
**Rules:**
- ✅ First line must be header: `name`
- ✅ One name per line
- ✅ UTF-8 encoding supported
- ✅ Names automatically capitalized
- ✅ Duplicates automatically skipped (case-insensitive)
## 🎯 How It Works
### ETL Flow (File Upload)
1. **Upload CSV** via drag & drop or file browser
2. **Read**: Parse CSV line by line
3. **Transform**: Capitalize names ("john doe" → "John Doe")
4. **Validate**: Check for duplicates (case-insensitive)
5. **Write**: Save new records to Oracle DB
6. **Report**: Display statistics (read, written, duplicates skipped)
### ETL Flow (Sample Data with Spring Batch)
1. **Select Sample Data** mode
2. **Extract**: FlatFileItemReader reads data.csv from classpath
3. **Transform**: PersonDataProcessor capitalizes names
4. **Load**: PersonDatabaseWriter saves to Oracle DB
5. **Monitor**: Listeners track progress and statistics
### Duplicate Detection
The system prevents duplicate entries:
- ✅ **Case-insensitive**: "John Doe" = "john doe" = "JOHN DOE"
- ✅ **Database check**: Compares with existing records
- ✅ **Skip & count**: Duplicates are logged and counted
- ✅ **No errors**: Duplicates don't cause job failures
**Example:**
```
First upload:  10 read, 10 written, 0 duplicates
Second upload: 10 read, 0 written, 10 duplicates ✅
```
### Spring Batch Chunk Processing
Processes data in chunks (default: 5 records):
```
Read 5 → Process 5 → Write 5 → Commit → Repeat
```
Benefits:
- ✅ Better performance
- ✅ Lower memory usage
- ✅ Transactional processing
- ✅ Automatic error recovery
## 🧪 Testing the Application
### Using the Frontend
1. Open http://localhost:3000
2. Choose mode:
   - **📊 Use Sample Data**: Processes data.csv with Spring Batch
   - **📁 Upload CSV File**: Drag & drop your own file
3. Click "⚡ Run ETL Job"
4. View real-time statistics:
   - Records read
   - Records written
   - Duplicates skipped
5. Manage persons in the table:
   - ✏️ Edit names
   - 🗑️ Delete individuals
   - 🗑️ Delete all records
### Using cURL
```bash
# Run ETL with sample data
curl -X POST http://localhost:8080/etl/run
# Upload CSV file
curl -X POST http://localhost:8080/etl/upload \
  -F "file=@mydata.csv"
# Get all persons
curl http://localhost:8080/person/all
# Delete person
curl -X DELETE http://localhost:8080/person/1
# Delete all
curl -X DELETE http://localhost:8080/person/all
```
## ⚙️ Configuration
### Database Settings
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=system
spring.datasource.password=oracle
```
### Frontend API URL
Edit `frontend/src/App.js`:
```javascript
const API_URL = 'http://localhost:8080';
```
## 🐳 Docker Commands
```bash
# Start all services
docker-compose up -d
# Stop all services
docker-compose down
# View logs
docker-compose logs -f backend
docker-compose logs -f frontend
# Rebuild images
docker-compose up --build
# Remove all (including volumes)
docker-compose down -v
```
## 📝 Sample Data
The project includes 4 CSV files with 40 unique names:
| File | Names | Description |
|------|-------|-------------|
| data.csv | 10 | General data |
| employees.csv | 10 | Employee names |
| customers.csv | 10 | Customer names |
| partners.csv | 10 | Partner names |
All files use Portuguese names with proper UTF-8 encoding.
## 🛑 Stopping the Application
```bash
# Stop Docker services
docker-compose down
# Or Ctrl+C if running locally
```
## 🔍 Monitoring
The application provides detailed logs:
```
═══════════════════════════════════════════════
          STARTING ETL JOB - SPRING BATCH
═══════════════════════════════════════════════
┌─────────────────────────────────────────────┐
│ Starting Step: etlStep
└─────────────────────────────────────────────┘
TRANSFORM: 'joão silva' -> 'João Silva'
✓ Person saved: ID=1, Name='João Silva'
⊗ DUPLICATE SKIPPED: 'Maria Santos' already exists
┌─────────────────────────────────────────────┐
│ Step Completed: etlStep
├─────────────────────────────────────────────┤
│ Status: COMPLETED
│ Read Count: 10
│ Write Count: 8
│ Skip Count: 0
│ Duplicate Count: 2
└─────────────────────────────────────────────┘
```
## 🆘 Troubleshooting
### Port Already in Use
```bash
# Check ports
sudo lsof -i :8080  # Backend
sudo lsof -i :3000  # Frontend
sudo lsof -i :1521  # Oracle
# Kill process or change port in configuration
```
### Oracle Not Ready
```bash
# Wait 30-60 seconds for Oracle to initialize
# Or check manually:
docker exec oracle-xe bash -c \
  "echo 'SELECT 1 FROM DUAL;' | sqlplus -s system/oracle@//localhost:1521/XEPDB1"
```
### Docker Issues
```bash
# Restart Docker daemon
sudo systemctl restart docker
# Check Docker
docker info
# Clean restart
docker-compose down -v
./start.sh
```
## 🤝 Contributing to GitHub
To push this project to your GitHub:
```bash
# Create repository on GitHub first, then:
git remote add origin https://github.com/YOUR_USERNAME/REPO_NAME.git
git branch -M main
git push -u origin main
```
## 📚 Learn More
- [Spring Batch Documentation](https://spring.io/projects/spring-batch)
- [Spring Boot Reference](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [Oracle Database](https://www.oracle.com/database/)
## 📄 License
This project is open source and available for educational purposes.
---
**Built with ❤️ using Spring Boot, React, Spring Batch, and Oracle Database**
🚀 **Ready to process your data!**
