package com.tmw.etl.etlapp.services;

import com.tmw.etl.etlapp.db.repositories.*;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class EtlService {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProducerRepository producerRepository;
    @Autowired
    private PegiCodeRepository pegiCodeRepository;

    private Logger logger = LoggerFactory.getLogger(EtlService.class);

    private ArrayList<Document> rawData = null;
    private Map<String, ArrayList<Object>> transformedData = null;

    private Future<ArrayList<Document>> documentFuture = null;
    private Future<Map<String, ArrayList<Object>>> gameFuture = null;
    private Future<Integer[]> loadFuture = null;
    private Future<Integer[]> etlProcessorFuture = null;

    private DataExtractor dataExtractor = null;
    private EtlProcessor etlProcessor = null;

    public ResponseEntity<String> extractData() {

        if (transformedData != null) transformedData = null;

        if (documentFuture == null) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            dataExtractor = new DataExtractor();
            documentFuture = executorService.submit(dataExtractor);
            executorService.shutdown();
        } else if (documentFuture.isDone()) {
            try {
                rawData = documentFuture.get();
            } catch (InterruptedException | ExecutionException ex) {
                logger.error("Error extracting data");
                ex.printStackTrace();
                return new ResponseEntity<>("Error extracting data.", HttpStatus.CONFLICT);
            }
            documentFuture = null;
            dataExtractor = null;
            return new ResponseEntity<>("Data extracted successfully. " +
                    "Extracted " + rawData.size() + " pages of items.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Data is being extracted.. Extracted: " + dataExtractor.getDataSize() + " games.", HttpStatus.OK);
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
                    ex.printStackTrace();
                    return new ResponseEntity<>("Error transforming data. " +
                            "", HttpStatus.CONFLICT);
                }
                gameFuture = null;
                return new ResponseEntity<>("Data transformed successfully. " +
                        "Created " + transformedData.get("games").size() + " items.", HttpStatus.OK);
            }
            return new ResponseEntity<>("Data is being transformed..", HttpStatus.OK);
        } else {
            try {
                throw new NoDataException("There is no data to transform. Extract data in the first place.");
            } catch (NoDataException exc) {
                logger.error("There is no data to transform. Extract data in the first place.");
                exc.printStackTrace();
                return new ResponseEntity<>(exc.getMessage(), HttpStatus.ACCEPTED);
            }
        }
    }


    public ResponseEntity<String> loadData() {
        logger.debug("DATA: " + transformedData);
        if (transformedData != null) {
            if (loadFuture != null && loadFuture.isDone()) {
                Integer[] counters = {0, 0};
                try {
                    counters = loadFuture.get();
                } catch (InterruptedException | ExecutionException ex) {
                    logger.error("Error loading data.");
                    ex.printStackTrace();
                    return new ResponseEntity<>("Error loading data.", HttpStatus.CONFLICT);
                }
                transformedData = null;
                rawData = null;
                loadFuture = null;
                return new ResponseEntity<>(
                        "Data loaded successfully. Inserted: " + counters[0] + " games. " +
                                "Updated: " + counters[1] + " games.", HttpStatus.OK);
            } else if (loadFuture == null) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                loadFuture = executorService.submit(new DataLoader(transformedData, gameRepository, categoryRepository,
                        producerRepository, pegiCodeRepository));
                executorService.shutdown();
            }
            return new ResponseEntity<>("Data is being loaded..", HttpStatus.OK);
        } else {
            try {
                throw new NoDataException("There is no data or data was not transformed. Extract data or transform it in the first place.");
            } catch (NoDataException exc) {
                logger.error("There is no data or data was not transformed. Extract data or transform it in the first place.");
                exc.printStackTrace();
                return new ResponseEntity<>(exc.getMessage(), HttpStatus.ACCEPTED);
            }
        }
    }

    public ResponseEntity<String> runFulleEtlProcess() {
        if (etlProcessorFuture == null) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            etlProcessor = new EtlProcessor(gameRepository, categoryRepository,
                    producerRepository, pegiCodeRepository);
            etlProcessorFuture = executorService.submit(etlProcessor);
            executorService.shutdown();
        }
        if (etlProcessorFuture != null && etlProcessorFuture.isDone()) {
            Integer[] counters = {0, 0};
            try {
                counters = etlProcessorFuture.get();
            } catch (InterruptedException | ExecutionException ex) {
                logger.error("An error encountered during full ETL process.");
                ex.printStackTrace();
                return new ResponseEntity<>("An error encountered during full ETL process.", HttpStatus.CONFLICT);
            }
            etlProcessorFuture = null;
            etlProcessor = null;
            return new ResponseEntity<>("Full ETL Process Done. Inserted: " + counters[0] + " games. " + "Updated: " + counters[1] + " games.", HttpStatus.OK);
        } else {
            logger.info("ETL PROCESSOR+ " + etlProcessor.getInfo());
            return new ResponseEntity<>(etlProcessor.getInfo(), HttpStatus.OK);
        }
    }
}
