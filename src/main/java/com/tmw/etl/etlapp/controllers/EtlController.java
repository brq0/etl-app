package com.tmw.etl.etlapp.controllers;

import com.tmw.etl.etlapp.db.repositories.GameRepository;
import com.tmw.etl.etlapp.services.EtlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EtlController {


    @Autowired
    private EtlService etlService;

    @GetMapping("/extract")
    public ResponseEntity<String> extract() {
       return etlService.extractData();
    }

    @GetMapping("/transform")
    public ResponseEntity<String> transform() {
        return etlService.transformData();
    }

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        return etlService.loadData();
    }

    @GetMapping("/etl")
    public ResponseEntity<String> etl(){
        return etlService.runFulleEtlProcess();
    }
}
