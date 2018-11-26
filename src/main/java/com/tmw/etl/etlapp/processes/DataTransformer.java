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

public class DataTransformer implements Callable<ArrayList<Game>> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArrayList<Document> rawDataPages;

    public DataTransformer(ArrayList<Document> rawDataPages) {
        this.rawDataPages = rawDataPages;
    }

    @Override
    public ArrayList<Game> call() {
        return transformData(rawDataPages);
    }

    private ArrayList<Game> transformData(ArrayList<Document> rawDataPages) {
        ArrayList<Game> games = new ArrayList<>();

        int position = 1;

        for (Document gamePage : rawDataPages) {

            Game game = new Game();
            GameDetails gameDetails = new GameDetails();

            String gameId = gamePage.getElementsByClass("js-reco-productlist").first().attr("page-product-id");
            String gameDescription = gamePage.getElementsByClass("productDescription").text();
            String gameTitle = gamePage.getElementsByAttributeValue("property", "og:title").first().attr("content");
            String gameImgUrl = gamePage.getElementsByAttributeValue("property", "og:url").first().attr("content");
            String gamePrice = gamePage.getElementsByClass("ta-price").first().text();



            Elements elements = gamePage.getElementsByClass("productDataTable").first().getElementsByClass("row--text row--text  attributeName");

            String gameProducent = elements.get(1).text();
            String gamePublisher = elements.get(2).text();
            String gameDistributor = elements.get(3).text();
            String releaseDate = elements.get(4).text();

            System.out.println(gameId);
            System.out.println(gameTitle);
            System.out.println(gamePrice);
            System.out.println(gameProducent);
            System.out.println(gamePublisher);
            System.out.println(gameDistributor);
            System.out.println(releaseDate);

            System.out.println();
            System.out.println(            );

            System.out.println(elements);


            game.setId(gameId);
            game.setName(gameTitle);

            gameDetails.setId(gameId);
            gameDetails.setName(gameTitle);
            gameDetails.setPosition(position);
            gameDetails.setPrice(gamePrice);
            gameDetails.setCategory( gamePage.attr("product-category") );
            gameDetails.setImgUrl(gameImgUrl);

            game.setId((position++) + "");
            game.setName("test");

            games.add(game);

        }

        return games;
    }
}
