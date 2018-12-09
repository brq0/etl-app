package com.tmw.etl.etlapp.services;

import com.tmw.etl.etlapp.db.entities.*;
import com.tmw.etl.etlapp.db.repositories.*;
import com.tmw.etl.etlapp.db.responses.GameResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class DataService {
    private Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameDetailsRepository gameDetailsRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProducerRepository producerRepository;
    @Autowired
    private PegiCodeRepository pegiCodeRepository;


    public ResponseEntity<Iterable<GameResponse>> getFullData() {
        ArrayList<GameResponse> gameResponsesList = new ArrayList<>();

        for(Game game : gameRepository.findAll()){
            GameResponse gameResponse = new GameResponse();
            gameResponse.setId(game.getId());
            gameResponse.setName(game.getName());

            Optional<GameDetails> gameDetails = gameDetailsRepository.findById(game.getId());
            if(gameDetails.isPresent()){
                GameDetails gd = gameDetails.get();

                gameResponse.setPrice(gd.getPrice());
                gameResponse.setImgUrl(gd.getImgUrl());
                gameResponse.setPosition(gd.getPosition());
                gameResponse.setReleaseDate(gd.getReleaseDate());
                gameResponse.setDescription(gd.getDescription());

                Optional<Category> category = categoryRepository.findById(gd.getCategoryId());
                gameResponse.setCategory(category.isPresent() ? category.get().getName() : "None/Error");
                Optional<Producer> producer = producerRepository.findById(gd.getProducerId());
                gameResponse.setProducer(producer.isPresent() ? producer.get().getName() : "None/Error");
                Optional<PegiCode> pegiCode = pegiCodeRepository.findById(gd.getPegiCodeId());
                gameResponse.setPegiUrl(pegiCode.isPresent() ? pegiCode.get().getImgUrl() : "None/Erorr");
            }
            gameResponsesList.add(gameResponse);
        }

        return new ResponseEntity<>(gameResponsesList, HttpStatus.OK);
    }

    public ResponseEntity<Optional<Game>> generateTxt(HttpServletResponse response, String id) {
        logger.info("ROW ID:" + id);
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
