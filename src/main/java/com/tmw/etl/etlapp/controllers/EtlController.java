package com.tmw.etl.etlapp.controllers;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import com.tmw.etl.etlapp.services.DataService;
import com.tmw.etl.etlapp.services.EtlService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Future;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EtlController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EtlService etlService;
    @Autowired
    private DataService dataService;

    private ArrayList<Document> rawData = null;
    private ArrayList<Game> transformedData = null;

    private Future<Integer> etlProcessorFuture = null;

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

    @GetMapping("getData")
    public ResponseEntity<Iterable<Game>> getData() {
        return dataService.getFullData();
    }


    @GetMapping("/generateTxt")
    public ResponseEntity<Optional<Game>> generateTxt(HttpServletResponse response, @RequestParam(required = true) String id) {
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
