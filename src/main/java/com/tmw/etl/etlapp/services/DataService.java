package com.tmw.etl.etlapp.services;

import com.tmw.etl.etlapp.db.entities.Category;
import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.entities.PegiCode;
import com.tmw.etl.etlapp.db.entities.Producer;
import com.tmw.etl.etlapp.db.repositories.CategoryRepository;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import com.tmw.etl.etlapp.db.repositories.PegiCodeRepository;
import com.tmw.etl.etlapp.db.repositories.ProducerRepository;
import com.tmw.etl.etlapp.db.responses.GameResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

@Service
@PropertySource("application.properties")
public class DataService {
    private Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProducerRepository producerRepository;
    @Autowired
    private PegiCodeRepository pegiCodeRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String dbAccess;

    @Autowired
    DataSource dataSource;

    @PostConstruct
    public void createDatabase(){
        System.out.println(dbAccess + "EEEE");
        String fileName = "etlapp_";
        String query = "";
        File file = new File(fileName);
        switch (dbAccess.toLowerCase()){
            case "create":
            case "none":
                fileName += dbAccess + ".sql";
                break;
            default:
                logger.error("No property {spring.jpa.hibernate.ddl-auto} or invalid value." +
                        "\n Set it to [create] or [none]");
                System.exit(1);
                break;
        }
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/" + fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    query += line;
                }
            }
            System.out.println(query);

            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
        }catch (IOException | SQLException exc){
            exc.printStackTrace();
        }
    }

    public ResponseEntity<Iterable<GameResponse>> getFullData() {
        return new ResponseEntity<>(getFullDataFromDb(), HttpStatus.OK);
    }

    private ArrayList<GameResponse> getFullDataFromDb() {
        ArrayList<GameResponse> gameResponsesList = new ArrayList<>();

        for (Game game : gameRepository.findAll()) {
            GameResponse gameResponse = new GameResponse();
            gameResponse.setId(game.getId());
            gameResponse.setName(game.getName());

            gameResponse.setPrice(game.getPrice());
            gameResponse.setImgUrl(game.getImgUrl());
            gameResponse.setPosition(game.getPosition());
            gameResponse.setReleaseDate(game.getReleaseDate());
            gameResponse.setDescription(game.getDescription());

            Optional<Category> category = categoryRepository.findById(game.getCategoryId());
            gameResponse.setCategory(category.isPresent() ? category.get().getName() : "None/Error");
            Optional<Producer> producer = producerRepository.findById(game.getProducerId());
            gameResponse.setProducer(producer.isPresent() ? producer.get().getName() : "None/Error");
            Optional<PegiCode> pegiCode = pegiCodeRepository.findById(game.getPegiCodeId());
            gameResponse.setPegiUrl(pegiCode.isPresent() ? pegiCode.get().getImgUrl() : "None/Erorr");

            gameResponsesList.add(gameResponse);
        }

        return gameResponsesList;
    }

    public GameResponse getSingleGameDataFromDb(String gameId) {
        Optional<Game> game = gameRepository.findById(gameId);
        GameResponse gameResponse = new GameResponse();
        if (game.isPresent()) {
            gameResponse.setId(game.get().getId());
            gameResponse.setName(game.get().getName());

            gameResponse.setPrice(game.get().getPrice());
            gameResponse.setImgUrl(game.get().getImgUrl());
            gameResponse.setPosition(game.get().getPosition());
            gameResponse.setReleaseDate(game.get().getReleaseDate());
            gameResponse.setDescription(game.get().getDescription());

            Optional<Category> category = categoryRepository.findById(game.get().getCategoryId());
            gameResponse.setCategory(category.isPresent() ? category.get().getName() : "None/Error");
            Optional<Producer> producer = producerRepository.findById(game.get().getProducerId());
            gameResponse.setProducer(producer.isPresent() ? producer.get().getName() : "None/Error");
            Optional<PegiCode> pegiCode = pegiCodeRepository.findById(game.get().getPegiCodeId());
            gameResponse.setPegiUrl(pegiCode.isPresent() ? pegiCode.get().getImgUrl() : "None/Erorr");
        }
        return gameResponse;
    }

    public ResponseEntity<String> generateTxt(HttpServletResponse response, String id) {
        logger.info("ROW ID:" + id);
        String fileName = "record.txt";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        GameResponse content = getSingleGameDataFromDb(id);
        return new ResponseEntity<>(content.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> generateCsv(HttpServletResponse response) {
        String fileName = "records.csv";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        String output = "id,name,category,price,releaseDate,producer,position,pegiUrl,imgUrl" + System.lineSeparator();

        for (GameResponse x : getFullDataFromDb()) {
            output += x.toString() + System.lineSeparator();
        }
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    public ResponseEntity<String> restartDb() {
        gameRepository.restartDb();
        categoryRepository.restartDb();
        producerRepository.restartDb();
        pegiCodeRepository.restartDb();

        return new ResponseEntity<>("Database restarted.", HttpStatus.OK);
    }
}
