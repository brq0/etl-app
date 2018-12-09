package com.tmw.etl.etlapp.controllers;

import com.tmw.etl.etlapp.db.responses.GameResponse;
import com.tmw.etl.etlapp.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class DataController {

    @Autowired
    private DataService dataService;

    @GetMapping("getData")
    public ResponseEntity<Iterable<GameResponse>> getData() {
        return dataService.getFullData();
    }

    @GetMapping("/generateTxt")
    public ResponseEntity<String> generateTxt(HttpServletResponse response, @RequestParam(required = true) String id) {
        return dataService.generateTxt(response, id);
    }

    @GetMapping("generateCsv")
    public ResponseEntity<String> generateCsv(HttpServletResponse response) {
        return dataService.generateCsv(response);
    }

    @GetMapping("restartDb")
    public ResponseEntity<String> restartDb() {
        return dataService.restartDb();
    }
}
