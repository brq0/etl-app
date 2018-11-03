package com.tmw.etl.etlapp.services;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import com.tmw.etl.etlapp.exc.NoDataException;
import com.tmw.etl.etlapp.processes.DataExtractor;
import com.tmw.etl.etlapp.processes.DataLoader;
import com.tmw.etl.etlapp.processes.DataTransformer;
import com.tmw.etl.etlapp.processes.EtlProcessor;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class EtlService {

    @Autowired
    private GameRepository gameRepository;

    private Logger logger = LoggerFactory.getLogger(EtlService.class);

    private ArrayList<Document> rawData = null;
    private ArrayList<Game> transformedData = null;

    private Future<ArrayList<Document>> documentFuture = null;
    private Future<ArrayList<Game>> gameFuture = null;
    private Future<Integer> loadFuture = null;
    private Future<Integer> etlProcessorFuture = null;

    public ResponseEntity<String> extractData() {
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

    public ResponseEntity<String> transformData() {
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


    public ResponseEntity<String> loadData() {
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

    public ResponseEntity<String> runFulleEtlProcess() {
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
