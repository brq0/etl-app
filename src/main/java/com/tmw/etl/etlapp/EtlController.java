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

    @GetMapping("/extract")
    public ResponseEntity<String> extract() {
        rawData = etlService.getData();
        return new ResponseEntity<>("EXTRACT", HttpStatus.OK);
    }

    @GetMapping("/transform")
    public ResponseEntity<String> transform() {
        logger.debug("DATA: " + rawData);
        if (rawData == null) {
            try {
                throw new NoDataException("There is no data to transform.");
            } catch (NoDataException exc) {
                logger.error(exc.getMessage());
                return new ResponseEntity<>("TRANSFORM", HttpStatus.CONFLICT);
            }
        } else {
            transformedData = etlService.transformData(rawData);
            return new ResponseEntity<>("TRANSFORM", HttpStatus.OK);
        }
    }

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        logger.debug("DATA: " + transformedData);
        if (transformedData == null) {
            try {
                throw new NoDataException("There is no data or data was not transferred.");
            } catch (NoDataException exc) {
                logger.error(exc.getMessage());
                return new ResponseEntity<>("Failed Loading", HttpStatus.CONFLICT);
            }
        } else {
            etlService.loadData(transformedData);

            transformedData = null;
            rawData = null;
            return new ResponseEntity<>("LOAD", HttpStatus.OK);
        }
    }

    @GetMapping("getData")
    public ResponseEntity<Iterable<Game>> getData() {
        return new ResponseEntity<>(gameRepository.findAll(), HttpStatus.OK);
    }


    @GetMapping("/generateTxt")
    public ResponseEntity<Optional<Game>> generateTxt(HttpServletResponse response, @RequestParam(required = true) String rowId){
        logger.debug("ROW ID:" + rowId);
        String fileName = "record.txt";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try {
            Optional<Game> content = gameRepository.findById(Integer.parseInt(rowId));
            return new ResponseEntity<>(content, HttpStatus.OK);
        }catch (NumberFormatException exc){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("generateCsv")
    public ResponseEntity<String> generateCsv(HttpServletResponse response){
        String fileName = "records.csv";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        String output = "id,name,description" + System.lineSeparator();
        for(Game x : gameRepository.findAll()){
            output += x.toString() + System.lineSeparator();
        }
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

}
