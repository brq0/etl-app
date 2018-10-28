package com.tmw.etl.etlapp;

import com.tmw.etl.etlapp.db.EtlService;
import com.tmw.etl.etlapp.db.entities.OutputEntity;
import com.tmw.etl.etlapp.db.repositories.OutputRepository;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EtlController {

    @Autowired
    private OutputRepository outputRepository;

    @Autowired
    private EtlService etlService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String rawData = null;

    private Document rawDataDoc = null;
    private String transferredData = null;

    private DataExtractor dataExtractor = null;

    private Future<Document> documentFuture = null;

    @GetMapping("/extract")
    public ResponseEntity<String> extract() {
        if(documentFuture == null){
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            documentFuture = executorService.submit(new DataExtractor());
        }else if(documentFuture.isDone()){
            try{
                rawDataDoc = documentFuture.get();
            }catch (InterruptedException | ExecutionException ex){
                logger.error("ERROR EXTRACTING DATA");
            }
            documentFuture = null;
            return new ResponseEntity<>("DATA EXTRACTED", HttpStatus.OK);
        }
        return new ResponseEntity<>("EXTRACTing", HttpStatus.OK);
    }

    @GetMapping("/transfer")
    public ResponseEntity<String> transfer() {
        logger.debug("DATA: " + rawData);
        if (rawData == null) {
            try {
                throw new NoDataException("There is no data to transfer.");
            } catch (NoDataException exc) {
                logger.error(exc.getMessage());
                return new ResponseEntity<>("TRANSFER", HttpStatus.CONFLICT);
            }
        } else {
            transferredData = etlService.transferData(rawData);
            return new ResponseEntity<>("TRANSFER", HttpStatus.OK);
        }
    }

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        logger.debug("DATA: " + transferredData);
        if (transferredData == null) {
            try {
                throw new NoDataException("There is no data or data was not transferred.");
            } catch (NoDataException exc) {
                logger.error(exc.getMessage());
                return new ResponseEntity<>("Failed Loading", HttpStatus.CONFLICT);
            }
        } else {
            etlService.loadData(transferredData);

            transferredData = null;
            rawData = null;
            return new ResponseEntity<>("LOAD", HttpStatus.OK);
        }
    }

    @GetMapping("getData")
    public ResponseEntity<Iterable<OutputEntity>> getData() {
        return new ResponseEntity<>(outputRepository.findAll(), HttpStatus.OK);
    }


    @GetMapping("/generateTxt")
    public ResponseEntity<Optional<OutputEntity>> generateTxt(HttpServletResponse response, @RequestParam(required = true) String rowId){
        logger.debug("ROW ID:" + rowId);
        String fileName = "record.txt";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try {
            Optional<OutputEntity> content = outputRepository.findById(Integer.parseInt(rowId));
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
        for(OutputEntity x : outputRepository.findAll()){
            output += x.toString() + System.lineSeparator();
        }
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

}
