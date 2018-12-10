package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.repositories.*;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

public class EtlProcessor implements Callable<Integer[]> {

    private GameRepository gameRepository;
    private CategoryRepository categoryRepository;
    private ProducerRepository producerRepository;
    private PegiCodeRepository pegiCodeRepository;
    private String info = "Full E T L process is running. Please wait..";

    public EtlProcessor(GameRepository gameRepository, CategoryRepository categoryRepository,
                        ProducerRepository producerRepository, PegiCodeRepository pegiCodeRepository) {
        this.gameRepository = gameRepository;
        this.categoryRepository = categoryRepository;
        this.producerRepository = producerRepository;
        this.pegiCodeRepository = pegiCodeRepository;
    }

    @Override
    public Integer[] call() {
        DataExtractor dataExtractor = new DataExtractor();
        ArrayList<Document> pagesDocs = dataExtractor.extractData(this);

        DataTransformer dataTransformer = new DataTransformer(pagesDocs);
        Map<String, ArrayList<Object>> transformedData = dataTransformer.transformData(this);

        DataLoader dataLoader = new DataLoader(transformedData, gameRepository,
                categoryRepository, producerRepository, pegiCodeRepository);
        return dataLoader.loadData(this);
    }

    public void setInfo(String info){
        this.info = info;
    }

    public String getInfo(){
        return info;
    }
}
