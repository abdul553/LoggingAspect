package com.example.loggingexample.service;

import com.example.loggingexample.model.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceImpl implements LogService{
    @Override
    public List<Data> getLogData() {
        return List.of(
                new Data("log1", "log detail"),
                new Data("log2", "log detail2"),
                new Data("log3", "log detail3")
        );
    }

    @Override
    public ResponseEntity<Data> postLogData(Data data) {
        return ResponseEntity.ok(new Data(data.text(), data.description()));
    }

    @Override
    public ResponseEntity<Data> updateLogData(Data data) {
        return ResponseEntity.ok(new Data(data.text(), data.description()));
    }
}
