package com.tmw.etl.etlapp;

import com.tmw.etl.etlapp.db.EtlService;
import com.tmw.etl.etlapp.db.entities.OutputEntity;
import com.tmw.etl.etlapp.db.repositories.OutputRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EtlController {

    @Autowired
    private OutputRepository outputRepository;

    @Autowired
    private EtlService etlService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String rawData = null;
    private String transferredData = null;

    @GetMapping("/extract")
    public ResponseEntity<String> extract() {
        rawData = etlService.getData();

        return new ResponseEntity<>("EXTRACT", HttpStatus.OK);
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
}
