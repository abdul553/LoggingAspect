package com.example.loggingexample.service;

import com.example.loggingexample.model.Data;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LogService {
    List<Data> getLogData();
    public ResponseEntity<Data> postLogData(Data data);
    public ResponseEntity<Data> updateLogData(Data data);
}
