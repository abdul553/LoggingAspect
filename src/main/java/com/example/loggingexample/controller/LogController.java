package com.example.loggingexample.controller;

import com.example.loggingexample.AutoLogging;
import com.example.loggingexample.LoggingAspect;
import com.example.loggingexample.model.Data;
import com.example.loggingexample.service.LogService;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @RequestMapping(method = RequestMethod.GET, path = "/log")
    public ResponseEntity<?> getLogs() {
        return ResponseEntity.ok(logService.getLogData());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/throw-log")
    public ResponseEntity<?> throwLog(
            @RequestParam("name") String username
    ) {
        if (Objects.equals(username, "throw"))
            throw new IllegalArgumentException("username not provided");
        return ResponseEntity.ok("test" + username);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/post")
    public ResponseEntity<?> postData(@RequestBody Data data) {
        return ResponseEntity.ok(logService.postLogData(data));
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/update")
    public ResponseEntity<?> updateData(@RequestBody Data data) {
        return ResponseEntity.ok(logService.updateLogData(data));
    }

    @AutoLogging
    @GetMapping("/autologging")
    public String showAutoLogging() {
        return "auto logging result";
    }


}

