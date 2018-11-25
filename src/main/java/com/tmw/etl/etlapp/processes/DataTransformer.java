package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.entities.GameDetails;
import org.jsoup.nodes.Document;
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

            String description = gamePage.getElementsByClass("productDescription").text();
            String title = gamePage.getElementsByAttributeValue("property", "og:title").first().attr("content");
            String imgUrl = gamePage.getElementsByAttributeValue("property", "og:url").first().attr("content");

            Elements elements = gamePage.getElementsByClass("productDataTable").first().getElementsByClass("row--text row--text  attributeName");

            System.out.println(title);
            System.out.println(elements);

//            if(gameInfo == null) continue;

//            String gameId = gameInfo.attr("product-id");
//            String gameName = gameInfo.attr("product-name");
//            String gamePrice = product.getElementsByClass("ta-price").text();
//
//            game.setId(gameId);
//            game.setName(gameName);
//
//            gameDetails.setId(gameId);
//            gameDetails.setName(gameName);
//            gameDetails.setPosition(position);
//            gameDetails.setPrice(gamePrice);
//            gameDetails.setCategory( gameInfo.attr("product-category") );
//            gameDetails.setImgUrl( gameInfo.attr("product-image") );

            game.setId((position++) + "");
            game.setName("test");

            games.add(game);

        }

        return games;
    }
}
