package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.entities.GameDetails;
import com.tmw.etl.etlapp.db.repositories.GameDetailsRepository;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

public class DataLoader implements Callable<Integer[]> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ArrayList<Game> games;
    private ArrayList<GameDetails> gamesDetails;

    private GameRepository gameRepository;
    private GameDetailsRepository gameDetailsRepository;

    public DataLoader(Map<String, ArrayList<Object>> transferredData, GameRepository gameRepository, GameDetailsRepository gameDetailsRepository) {
        games = (ArrayList) transferredData.get("games");
        gamesDetails = (ArrayList) transferredData.get("gamesDetails");
        this.gameRepository = gameRepository;
        this.gameDetailsRepository = gameDetailsRepository;
    }

    @Override
    public Integer[] call() {
        return loadData();
    }

    private Integer[] loadData() {
        logger.info("LOADING DATA");
        int counter = 0;
        int updateCounter = 0;

        for (int i=0; i<games.size(); i++){
            gameRepository.save(games.get(i));
            gameDetailsRepository.save(gamesDetails.get(i));
            counter++;
        }



//        for (GameDetails gameDetails : transferredData) {
//            Game game = new Game();
//
//            game.setName(gameDetails.getName());
//            game.setId(gameDetails.getId());
//
//            if(!gameDetailsRepository.findById(gameDetails.getId()).isPresent()){
//
//                gameDetailsRepository.save(gameDetails);
//                logger.debug(gameDetails.toString());
//                counter++;
//            } else {
//
//                GameDetails compareGame = gameDetailsRepository.findById(gameDetails.getId()).get();
//
//                if (!game.equals(compareGame)) {
//
//                    System.out.println("UPDATING GAME - TODO");
//
//                    gameDetailsRepository.updateRow( gameDetails.getId(), gameDetails.getName());
//
//                    // TODO: HERE gameRepositoryDetails.updateRow()
//
//                }
//
//            }
//
//            if (!gameRepository.findById(game.getId()).isPresent()) {         //if not in db
//                gameRepository.save(game);
//                logger.debug(game.toString());
//
//            } else {
//                    Game compareGame = gameRepository.findById(game.getId()).get();
//                    if (!game.equals(compareGame)) {
//                        gameRepository.updateGame(
//                                game.getId(),
//                                game.getName());
//
//                        updateCounter++;
//                    }
//                }
//            }
        return new Integer[]{counter, updateCounter};
    }
}
