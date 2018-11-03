package com.tmw.etl.etlapp.services;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
public class DataService {
    private Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private GameRepository gameRepository;

    public ResponseEntity<Iterable<Game>> getFullData() {
        return new ResponseEntity<>(gameRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Optional<Game>> generateTxt(HttpServletResponse response, String id) {
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

    public ResponseEntity<String> generateCsv(HttpServletResponse response) {
        String fileName = "records.csv";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        String output = "id,name,category,price,img_url,position" + System.lineSeparator();
        for (Game x : gameRepository.findAll()) {
            output += x.toString() + System.lineSeparator();
        }
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    public ResponseEntity<String> restartDb() {
        gameRepository.restartDb();
        return new ResponseEntity<>("Database restarted.", HttpStatus.OK);
    }
}
