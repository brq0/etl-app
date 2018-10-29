package com.tmw.etl.etlapp;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class DataLoader implements Runnable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArrayList<Game> transferredData;
    private Thread thrd;
    private GameRepository gameRepository;

    public DataLoader(ArrayList<Game> transferredData, GameRepository gameRepository) {
        this.transferredData = transferredData;
        this.gameRepository = gameRepository;
        thrd = new Thread(this);
        thrd.start();
    }

    public Thread getThread(){
        return thrd;
    }

    @Override
    public void run(){
        loadData();
    }

    private void loadData() {
        logger.info("LOADING DATA");
        for (Game game : transferredData) {
            gameRepository.save(game);
        }
    }
}
