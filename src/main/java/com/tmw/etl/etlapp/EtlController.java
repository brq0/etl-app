package com.tmw.etl.etlapp;

import com.tmw.etl.etlapp.db.EtlService;
import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EtlController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EtlService etlService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArrayList<Document> rawData = null;
    private ArrayList<Game> transformedData = null;

    private Future<ArrayList<Document>> documentFuture = null;
    private Future<ArrayList<Game>> gameFuture = null;
    private Future<Integer> loadFuture = null;
    private Future<Integer> etlProcessorFuture = null;

    @GetMapping("/extract")
    public ResponseEntity<String> extract() {
        if (documentFuture == null) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            documentFuture = executorService.submit(new DataExtractor());
            executorService.shutdown();
        } else if (documentFuture.isDone()) {
            try {
                rawData = documentFuture.get();
            } catch (InterruptedException | ExecutionException ex) {
                logger.error("Error extracting data");
                return new ResponseEntity<>("Error extracting data.", HttpStatus.CONFLICT);
            }
            documentFuture = null;
            return new ResponseEntity<>("Data extracted successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Data is being extracted..", HttpStatus.OK);
    }

    @GetMapping("/transform")
    public ResponseEntity<String> transform() {
        logger.debug("DATA: " + rawData);
        if (rawData != null) {
            if (gameFuture == null) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                gameFuture = executorService.submit(new DataTransformer(rawData));
                executorService.shutdown();
            } else if (gameFuture.isDone()) {
                try {
                    transformedData = gameFuture.get();
                } catch (InterruptedException | ExecutionException ex) {
                    logger.error("Error transforming data.");
                    return new ResponseEntity<>("Error transforming data.", HttpStatus.CONFLICT);
                }
                gameFuture = null;
                return new ResponseEntity<>("Data transformed successfully.", HttpStatus.OK);
            }
            return new ResponseEntity<>("Data is being transformed..", HttpStatus.OK);
        } else {
            try {
                throw new NoDataException("There is no data to transform. Extract data in the first place.");
            } catch (NoDataException exc) {
                logger.error(exc.getMessage());
                return new ResponseEntity<>(exc.getMessage(), HttpStatus.ACCEPTED);
            }
        }
    }

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        logger.debug("DATA: " + transformedData);
        if (transformedData != null) {
            if (loadFuture != null && loadFuture.isDone()) {
                Integer counter = 0;
                try {
                    counter = loadFuture.get();
                } catch (InterruptedException | ExecutionException ex) {
                    logger.error("Error loading data.");
                    return new ResponseEntity<>("Error loading data.", HttpStatus.CONFLICT);
                }
                transformedData = null;
                rawData = null;
                loadFuture = null;
                return new ResponseEntity<>("Data loaded successfully. Inserted: " + counter + " rows.", HttpStatus.OK);
            } else if (loadFuture == null) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                loadFuture = executorService.submit(new DataLoader(transformedData, gameRepository));
                executorService.shutdown();
            }
            return new ResponseEntity<>("Data is being loaded..", HttpStatus.OK);
        } else {
            try {
                throw new NoDataException("There is no data or data was not transformed. Extract data or transform it in the first place.");
            } catch (NoDataException exc) {
                logger.error(exc.getMessage());
                return new ResponseEntity<>(exc.getMessage(), HttpStatus.ACCEPTED);
            }
        }


    }

    @GetMapping("getData")
    public ResponseEntity<Iterable<Game>> getData() {
        return new ResponseEntity<>(gameRepository.findAll(), HttpStatus.OK);
    }


    @GetMapping("/generateTxt")
    public ResponseEntity<Optional<Game>> generateTxt(HttpServletResponse response, @RequestParam(required = true) String id) {
        logger.debug("ROW ID:" + id);
        String fileName = "record.txt";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try {
            Optional<Game> content = gameRepository.findById(id);
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (NumberFormatException exc) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("generateCsv")
    public ResponseEntity<String> generateCsv(HttpServletResponse response) {
        String fileName = "records.csv";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        String output = "id,name,category,price,img_url" + System.lineSeparator();
        for (Game x : gameRepository.findAll()) {
            output += x.toString() + System.lineSeparator();
        }
        return new ResponseEntity<>(output, HttpStatus.OK);
    }


    @GetMapping("restartDb")
    public ResponseEntity<String> restartDb() {
        gameRepository.restartDb();
        return new ResponseEntity<>("DbRestarted", HttpStatus.OK);  
    }
  
    @GetMapping("/etl")
    public ResponseEntity<String> etl(){
        if(etlProcessorFuture == null){
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            etlProcessorFuture = executorService.submit(new EtlProcessor(gameRepository));
            executorService.shutdown();
        }
        if(etlProcessorFuture != null && etlProcessorFuture.isDone()){
            Integer counter = 0;
            try {
                counter = etlProcessorFuture.get();
            } catch (InterruptedException | ExecutionException ex) {
                logger.error("An error encountered during full ETL process.");
                return new ResponseEntity<>("An error encountered during full ETL process.", HttpStatus.CONFLICT);
            }
            etlProcessorFuture = null;
            return new ResponseEntity<>("Full ETL Process Done. Inserted: " + counter + " rows.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Full ETL Process is running.. Please wait..", HttpStatus.OK);
        }
    }
}
