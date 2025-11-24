package com.example.demo.batch.model;

/**
 * DTO para representar os dados brutos do CSV durante o processo ETL
 */
public class PersonCSVData {

    private String rawName;
    private int lineNumber;

    public PersonCSVData() {
    }

    public PersonCSVData(String rawName, int lineNumber) {
        this.rawName = rawName;
        this.lineNumber = lineNumber;
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "PersonCSVData{" +
                "rawName='" + rawName + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}

