package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.*;
import com.tmw.etl.etlapp.db.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

public class DataLoader implements Callable<Integer[]> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ArrayList<Game> games;
    private ArrayList<Category> categories;
    private ArrayList<Producer> producers;
    private ArrayList<PegiCode> pegiCodes;

    private GameRepository gameRepository;
    private CategoryRepository categoryRepository;
    private ProducerRepository producerRepository;
    private PegiCodeRepository pegiCodeRepository;

    public DataLoader(Map<String, ArrayList<Object>> transformedData, GameRepository gameRepository,
                      CategoryRepository categoryRepository, ProducerRepository producerRepository, PegiCodeRepository pegiCodeRepository) {
        games = (ArrayList) transformedData.get("games");
        categories = (ArrayList) transformedData.get("categories");
        producers = (ArrayList) transformedData.get("producers");
        pegiCodes = (ArrayList) transformedData.get("pegiCodes");

        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
        this.producerRepository = producerRepository;
        this.pegiCodeRepository = pegiCodeRepository;
    }

    @Override
    public Integer[] call() {
        return loadData(null);
    }

    public Integer[] loadData(EtlProcessor etlProcessor) {
        logger.info("LOADING DATA");
        int insertCounter = 0;
        int updateCounter = 0;

        if(etlProcessor != null) {
            etlProcessor.setInfo("E T L: Data is being loaded.");
        }

        categories.forEach(it -> {
                    Optional<Category> cat = categoryRepository.findByName(it.getName());
                    if (!cat.isPresent()) {
                        categoryRepository.save(it);
                    } else if (cat.isPresent()) {
                        games.forEach(game -> {
                                    if (game.getCategoryId() == it.getId()) {
                                        game.setCategoryId(cat.get().getId());
                                    }
                                }
                        );
                    }
                }
        );

        producers.forEach(it -> {
                    Optional<Producer> producer = producerRepository.findByName(it.getName());
                    if (!producer.isPresent()) {
                        producerRepository.save(it);
                    } else if (producer.isPresent()) {
                        games.forEach(game -> {
                                    if (game.getProducerId() == it.getId()) {
                                        game.setProducerId(producer.get().getId());
                                    }
                                }
                        );
                    }
                }
        );

        pegiCodes.forEach(it -> {
                    Optional<PegiCode> pegiCode = pegiCodeRepository.findByPegiImgUrl(it.getImgUrl());
                    if (!pegiCode.isPresent()) {
                        pegiCodeRepository.save(it);
                    } else if (pegiCode.isPresent()) {
                        games.forEach(game -> {
                                    if (game.getPegiCodeId() == it.getId()) {
                                        game.setPegiCodeId(pegiCode.get().getId());
                                    }
                                }
                        );
                    }
                }
        );

        for(Game game : games){
            Optional<Game> dbGame = gameRepository.findById(game.getId());
            if(!dbGame.isPresent()){
                gameRepository.save(game);
                insertCounter++;
            }else if(dbGame.isPresent()){
                if(!dbGame.get().equals(game)){
                    gameRepository.updateGame(game.getId(),
                                              game.getName(),
                                              game.getCategoryId(),
                                              game.getPrice(),
                                              game.getImgUrl(),
                                              game.getPosition(),
                                              game.getDescription(),
                                              game.getProducerId(),
                                              game.getReleaseDate(),
                                              game.getPegiCodeId()
                                            );
                    updateCounter++;
                }
            }
        }

        logger.info("Data loaded successfully. Inserted: " + insertCounter + " games. Updated: " + updateCounter);
        return new Integer[]{insertCounter, updateCounter};
    }
}
