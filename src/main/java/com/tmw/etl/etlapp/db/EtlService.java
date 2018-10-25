package com.tmw.etl.etlapp.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EtlService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public String getData() {
        logger.info("GETTING DATA");
        return "SOME DATA";
    }

    public String transferData(String data) {
        logger.info("DATA: " + data);
        logger.info("TRANSFERING DATA");

        return "AFTER TRANSFER --- " + data;
    }

    public void loadData(String transferredData) {
        logger.info("LOADING DATA");
        logger.info("LOADED DATA: " + transferredData);
    }
}
