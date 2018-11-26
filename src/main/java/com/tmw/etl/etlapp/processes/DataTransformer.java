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

            String gameId = getGameId(gamePage);
            String gameDescription = getGameDescription(gamePage);
            String gameName = getGameName(gamePage);
            String gameImgUrl = getGameImgUrl(gamePage);
            String gamePrice = getGamePrice(gamePage);
            String category = getGameCategory(gamePage);


            Elements elements = getDetailsTableElements(gamePage);
            String gameProducer = "";
            String gamePublisher = "";
            String gameReleaseDate = "";
            String gamePegiUrl = "";

            for (Element element : elements) {
                String[] elemDt = element.text().split(":");

                switch (elemDt[0]) {
                    case "Wydawca":
                        gamePublisher = getTableRowContent(elemDt);
                        break;
                    case "Data premiery":
                        gameReleaseDate = getTableRowContent(elemDt);
                        break;
                    case "Producent":
                        gameProducer = getTableRowContent(elemDt);
                        break;
                    case "Liczba nośników":

                        break;
                    case "Wersja językowa":

                        break;
                    case "PEGI":
                        gamePegiUrl = getGamePegiUrl(element);
                        break;
                }
            }

            System.out.println(gameId + " -- " + gameImgUrl + " --- " + gameName + " --- " +  gamePrice + " --- " + category + " --- " + gamePegiUrl + " --- " + gameProducer + " ---- " + gamePublisher + " ---- " + gameReleaseDate);

            game.setId(gameId);
            game.setName(gameName);

            gameDetails.setId(gameId);
            gameDetails.setPosition(position);
            gameDetails.setPrice(gamePrice);
            gameDetails.setCategory(category);
            gameDetails.setImgUrl(gameImgUrl);

            games.add(game);
        }

        return games;
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
