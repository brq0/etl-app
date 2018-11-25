package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class DataLoader implements Callable<Integer[]> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArrayList<Game> transferredData;
    private GameRepository gameRepository;

    public DataLoader(ArrayList<Game> transferredData, GameRepository gameRepository) {
        this.transferredData = transferredData;
        this.gameRepository = gameRepository;
    }

    @Override
    public Integer[] call() {
        return loadData();
    }

    private Integer[] loadData() {
        logger.info("LOADING DATA");
        int counter = 0;
        int updateCounter = 0;

        for (Game game : transferredData) {
            if (!gameRepository.findById(game.getId()).isPresent()) {         //if not in db
                gameRepository.save(game);
                logger.debug(game.toString());
                counter++;
            } else {
                    Game compareGame = gameRepository.findById(game.getId()).get();
                    if (!game.equals(compareGame)) {
                        gameRepository.updateGame(
                                game.getId(),
                                game.getName());

                        updateCounter++;
                    }
                }
            }
        return new Integer[]{counter, updateCounter};
    }
}
