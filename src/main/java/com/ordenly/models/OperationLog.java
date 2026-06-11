package com.ordenly.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OperationLog {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final LocalDateTime timestamp;
    private final String operation;
    private final int filesAffected;
    private final List<String> details;

    public OperationLog(String operation, int filesAffected, List<String> details) {
        this.timestamp = LocalDateTime.now();
        this.operation = operation;
        this.filesAffected = filesAffected;
        this.details = details;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getOperation() { return operation; }
    public int getFilesAffected() { return filesAffected; }
    public List<String> getDetails() { return details; }

    @Override
    public String toString() {
        return "[" + timestamp.format(FMT) + "] " + operation + " — " + filesAffected + " archivos";
    }
}
