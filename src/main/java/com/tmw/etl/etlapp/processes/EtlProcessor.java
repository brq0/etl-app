package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.repositories.*;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

public class EtlProcessor implements Callable<Integer[]> {

    private GameRepository gameRepository;
    private GameDetailsRepository gameDetailsRepository;
    private CategoryRepository categoryRepository;
    private ProducerRepository producerRepository;
    private PegiCodeRepository pegiCodeRepository;

    public EtlProcessor(GameRepository gameRepository, GameDetailsRepository gameDetailsRepository,
                        CategoryRepository categoryRepository, ProducerRepository producerRepository, PegiCodeRepository pegiCodeRepository) {
        this.gameRepository = gameRepository;
        this.gameDetailsRepository = gameDetailsRepository;
        this.categoryRepository = categoryRepository;
        this.producerRepository = producerRepository;
        this.pegiCodeRepository = pegiCodeRepository;
    }

    @Override
    public Integer[] call() {
        DataExtractor dataExtractor = new DataExtractor();
        ArrayList<Document> pagesDocs = dataExtractor.extractData();

        DataTransformer dataTransformer = new DataTransformer(pagesDocs);
        Map<String, ArrayList<Object>> transformedData = dataTransformer.transformData(pagesDocs);

        DataLoader dataLoader = new DataLoader(transformedData, gameRepository, gameDetailsRepository,
                categoryRepository, producerRepository, pegiCodeRepository);
        return dataLoader.loadData();
    }


}
