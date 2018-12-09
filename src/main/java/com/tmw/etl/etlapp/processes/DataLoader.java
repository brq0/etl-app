package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.*;
import com.tmw.etl.etlapp.db.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

public class DataLoader implements Callable<Integer[]> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ArrayList<Game> games;
    private ArrayList<GameDetails> gamesDetails;
    private ArrayList<Category> categories;
    private ArrayList<Producer> producers;
    private ArrayList<PegiCode> pegiCodes;

    private GameRepository gameRepository;
    private GameDetailsRepository gameDetailsRepository;
    private CategoryRepository categoryRepository;
    private ProducerRepository producerRepository;
    private PegiCodeRepository pegiCodeRepository;

    public DataLoader(Map<String, ArrayList<Object>> transferredData, GameRepository gameRepository, GameDetailsRepository gameDetailsRepository,
                      CategoryRepository categoryRepository, ProducerRepository producerRepository, PegiCodeRepository pegiCodeRepository) {
        games = (ArrayList) transferredData.get("games");
        gamesDetails = (ArrayList) transferredData.get("gamesDetails");
        categories = (ArrayList) transferredData.get("categories");
        producers = (ArrayList) transferredData.get("producers");
        pegiCodes = (ArrayList) transferredData.get("pegiCodes");


        this.gameRepository = gameRepository;
        this.gameDetailsRepository = gameDetailsRepository;
        this.categoryRepository = categoryRepository;
        this.producerRepository = producerRepository;
        this.pegiCodeRepository = pegiCodeRepository;
    }

    @Override
    public Integer[] call() {
        return loadData();
    }

    private Integer[] loadData() {
        logger.info("LOADING DATA");
        int counter = 0;
        int updateCounter = 0;

        categories.forEach( it ->
//                @TODO jeśli nie ma takiej kategorii w bd to save, jeśli jest to nic -> dla wszystkich poniżej to samo
//                @TODO bo tu sie może zmieniać id, bo są one generowane dynamicznie wg popularności na empik
                categoryRepository.save(it)
        );

        producers.forEach( it ->
                producerRepository.save(it)
        );

        pegiCodes.forEach( it ->
                pegiCodeRepository.save(it)
        );

        for (int i=0; i<games.size(); i++){
            gameRepository.save(games.get(i));
            gameDetailsRepository.save(gamesDetails.get(i));
            counter++;
        }

//        @TODO bardzo umiejetny check na kategorii, producent i pegiCode w grze (update if sth has changed)


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
