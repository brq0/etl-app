package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.entities.GameDetails;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class DataTransformer implements Callable<ArrayList<GameDetails>> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArrayList<Document> rawDataPages;

    public DataTransformer(ArrayList<Document> rawDataPages) {
        this.rawDataPages = rawDataPages;
    }

    @Override
    public ArrayList<GameDetails> call() {
        return transformData(rawDataPages);
    }

    private ArrayList<GameDetails> transformData(ArrayList<Document> rawDataPages) {

        ArrayList<GameDetails> games = new ArrayList<>();

        int position = 1;

        for (Document gamePage : rawDataPages) {

            GameDetails gameDetails = new GameDetails();

            String gameId = gamePage.getElementsByClass("js-reco-productlist").first().attr("page-product-id");
            String gameDescription = gamePage.getElementsByClass("productDescription").text();
            String gameTitle = gamePage.getElementsByAttributeValue("property", "og:title").first().attr("content");
            String gameImgUrl = gamePage.getElementsByAttributeValue("property", "og:url").first().attr("content");
            String gamePrice = gamePage.getElementsByClass("ta-price").first().text();
            String gameCategory = gamePage.getElementsByClass("productsList__product swiper-slide ta-product js-reco-product").attr("data-product-category");
            Elements elements = gamePage.getElementsByClass("productDataTable").first().getElementsByClass("row--text row--text  attributeName");

            String gameProducent = elements.get(1).text();
            String gamePublisher = elements.get(2).text();
            String gameDistributor = elements.get(3).text();
            String releaseDate = elements.get(4).text();

            gameDetails.setId(gameId);
            gameDetails.setName(gameTitle);
            gameDetails.setPosition(position);
            gameDetails.setPrice(gamePrice);
            gameDetails.setCategory( gameCategory );
            gameDetails.setImgUrl(gameImgUrl);
            gameDetails.setDescription(gameDescription);
            gameDetails.setProducent(gameProducent);
            gameDetails.setPublisher(gamePublisher);
            gameDetails.setDistributor(gameDistributor);
            gameDetails.setReleaseDate(releaseDate);

            games.add(gameDetails);

        }

        return games;
    }
}
