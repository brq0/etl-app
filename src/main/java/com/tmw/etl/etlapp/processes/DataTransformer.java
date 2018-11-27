package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.entities.GameDetails;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class DataTransformer implements Callable<Map<String, ArrayList<Object>>> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArrayList<Document> rawDataPages;

    public DataTransformer(ArrayList<Document> rawDataPages) {
        this.rawDataPages = rawDataPages;
    }

    @Override
    public Map<String, ArrayList<Object>> call() {
        return transformData(rawDataPages);
    }

    private Map<String, ArrayList<Object>> transformData(ArrayList<Document> rawDataPages) {

        Map<String, ArrayList<Object>> transformedData = new HashMap<>();

        ArrayList<Object> games = new ArrayList<>();
        ArrayList<Object> gamesDetails = new ArrayList<>();

        int position = 1;

        for (Document gamePage : rawDataPages) {

            Game game = new Game();
            GameDetails gameDetails = new GameDetails();

            String gameId = getGameId(gamePage);
            String gameDescription = getGameDescription(gamePage);
            String gameName = getGameName(gamePage);
            String gameImgUrl = getGameImgUrl(gamePage);
            String gamePrice = getGamePrice(gamePage);
            String category = getGameCategory(gamePage);


            Elements elements = getDetailsTableElements(gamePage);
            String gameProducer = "";
            String gameReleaseDate = "";
            String gamePegiUrl = "";

            for (Element element : elements) {
                String[] elemDt = element.text().split(":");

                switch (elemDt[0]) {
                    case "Data premiery":
                        gameReleaseDate = getTableRowContent(elemDt);
                        break;
                    case "Producent":
                        gameProducer = getTableRowContent(elemDt);
                        break;
                    case "PEGI":
                        gamePegiUrl = getGamePegiUrl(element);
                        break;
                }
            }

            System.out.println(gameId + " -- " + gameImgUrl + " --- " + gameName + " --- " +  gamePrice + " --- " + category + " --- " + gamePegiUrl + " --- " + gameProducer + " ---- " + gameReleaseDate);

            game.setId(gameId);
            game.setName(gameName);

            System.out.println("gd length: " + gameDescription.length());
            if(gameDescription.length() >= GameDetails.MAX_DESC_LENGTH){
                System.out.println("LENGTH more than : " + GameDetails.MAX_DESC_LENGTH);
                gameDescription = gameDescription.substring(0, GameDetails.MAX_DESC_LENGTH - 5) + "...";
                System.out.println("L. now: " + gameDescription.length());
            }

            gameDetails.setId(gameId);
            gameDetails.setCategory(category);
            gameDetails.setPrice(gamePrice);
            gameDetails.setImgUrl(gameImgUrl);
            gameDetails.setPosition(position);
            gameDetails.setDescription(gameDescription);
            gameDetails.setProducer(gameProducer);
            gameDetails.setReleaseDate(gameReleaseDate);
            gameDetails.setPegiUrl(gamePegiUrl);

            games.add(game);
            gamesDetails.add(gameDetails);

            position++;
        }

        transformedData.put("games", games);
        transformedData.put("gamesDetails", gamesDetails);

        return transformedData;
    }

    private String getGamePegiUrl(Element element) {
        return "https://www.empik.com" + element.getElementsByClass("pegiCode").first()
                .getElementsByTag("img").first().attr("src");
    }

    private Elements getDetailsTableElements(Document gamePage) {
        return gamePage.getElementsByClass("productDataTable").first()
                .getElementsByClass("row--text row--text  attributeName");
    }

    private String getGameCategory(Document gamePage) {
        return gamePage.getElementsByAttributeValue("itemtype", "http://schema.org/BreadcrumbList").last()
                .getElementsByAttributeValue("itemprop", "itemListElement").last().text();
    }

    private String getGamePrice(Document gamePage) {
        return gamePage.getElementsByClass("ta-price").first().text();
    }

    private String getGameImgUrl(Document gamePage) {
        return gamePage.getElementsByAttributeValue("property", "og:image").first().attr("content");
    }

    private String getGameName(Document gamePage) {
        return gamePage.getElementsByAttributeValue("property", "og:title").first().attr("content");
    }

    private String getGameDescription(Document gamePage) {
        return gamePage.getElementsByClass("productDescription").text();
    }

    private String getGameId(Document gamePage) {
        return gamePage.getElementsByClass("js-reco-productlist").first().attr("page-product-id");
    }

    private String getTableRowContent(String[] elemDt) {
        return String.join("", Arrays.copyOfRange(elemDt, 1, elemDt.length));
    }

    private String getPureTextOfTableRow(String row, String text) {
        return row.substring(row.indexOf(text) + text.length()).trim();
    }
}
