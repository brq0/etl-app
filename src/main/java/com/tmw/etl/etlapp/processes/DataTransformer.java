package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.*;
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
        return transformData(null);
    }

    public Map<String, ArrayList<Object>> transformData(EtlProcessor etlProcessor) {
        Map<String, ArrayList<Object>> transformedData = new HashMap<>();
        ArrayList<Object> games = new ArrayList<>();
        ArrayList<Object> categories = new ArrayList<>();
        ArrayList<Object> pegiCodes = new ArrayList<>();
        ArrayList<Object> producers = new ArrayList<>();
        int position = 1;

        for (Document gamePage : rawDataPages) {
            Game game = new Game();

            String gameId = getGameId(gamePage);
            String gameDescription = getGameDescription(gamePage);
            String gameName = getGameName(gamePage);
            String gameImgUrl = getGameImgUrl(gamePage);
            String gamePrice = getGamePrice(gamePage);
            String gameCategory = getGameCategory(gamePage);

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

            logger.info(gameId + " -- " + gameImgUrl + " --- " + gameName + " --- " + gamePrice + " --- " + gameCategory + " --- " + gamePegiUrl + " --- " + gameProducer + " ---- " + gameReleaseDate);

            game.setId(gameId);
            game.setName(gameName);

            if (gameDescription.length() >= Game.MAX_DESC_LENGTH) {
                gameDescription = gameDescription.substring(0, Game.MAX_DESC_LENGTH - 5) + "...";
            }

            game.setId(gameId);
            game.setPrice(gamePrice);
            game.setImgUrl(gameImgUrl);
            game.setPosition(position);
            game.setDescription(gameDescription);
            game.setReleaseDate(gameReleaseDate);

            Category category = new Category();
            category.setName(gameCategory);
            if (!categories.contains(category)) {
                categories.add(category);
                category.setId(categories.size());
            } else {
                category.setId(categories.indexOf(category) + 1);
            }

            PegiCode pegiCode = new PegiCode();
            pegiCode.setImgUrl(gamePegiUrl);
            if (!pegiCodes.contains(pegiCode)) {
                pegiCodes.add(pegiCode);
                pegiCode.setId(pegiCodes.size());
            } else {
                pegiCode.setId(pegiCodes.indexOf(pegiCode) + 1);
            }

            Producer producer = new Producer();
            producer.setName(gameProducer);
            if (!producers.contains(producer)) {
                producers.add(producer);
                producer.setId(producers.size());
            } else {
                producer.setId(producers.indexOf(producer) + 1);
            }

            game.setCategoryId(category.getId());
            game.setProducerId(producer.getId());
            game.setPegiCodeId(pegiCode.getId());

            games.add(game);
            position++;
        }

        logger.info("Categories size: " + categories.size());
        logger.info("Producers size: " + producers.size());
        logger.info("PegiCodes size: " + pegiCodes.size());

        transformedData.put("categories", categories);
        transformedData.put("producers", producers);
        transformedData.put("pegiCodes", pegiCodes);
        transformedData.put("games", games);

        if(etlProcessor != null) {
            etlProcessor.setInfo("E T L: Data is being transformed.. Transformed: " + transformedData.size() + " games.");
        }

        return transformedData;
    }

    private String getGamePegiUrl(Element element) {
        try {
            return "https://www.empik.com" + element.getElementsByClass("pegiCode").first()
                    .getElementsByTag("img").first().attr("src");
        } catch (NullPointerException exc) {
            return "";
        }
    }

    private Elements getDetailsTableElements(Document gamePage) {
        return gamePage.getElementsByClass("productDataTable").first()
                .getElementsByClass("ta-attribute-row");
    }

    private String getGameCategory(Document gamePage) {
        try {
            return gamePage.getElementsByAttributeValue("itemtype", "http://schema.org/BreadcrumbList").last()
                    .getElementsByAttributeValue("itemprop", "itemListElement").last().text();
        } catch (NullPointerException exc) {
            return "";
        }
    }

    private String getGamePrice(Document gamePage) {
        try {
            return gamePage.getElementsByClass("ta-price").first().text();
        } catch (NullPointerException exc) {
            return "";
        }
    }

    private String getGameImgUrl(Document gamePage) {
        try {
            return gamePage.getElementsByAttributeValue("property", "og:image").first().attr("content");
        } catch (NullPointerException exc) {
            return "";
        }
    }

    private String getGameName(Document gamePage) {
        try {
            return gamePage.getElementsByAttributeValue("property", "og:title").first().attr("content");
        } catch (NullPointerException exc) {
            return "";
        }
    }

    private String getGameDescription(Document gamePage) {
        try {
            return gamePage.getElementsByClass("productDescription").text();
        } catch (NullPointerException exc) {
            return "";
        }
    }

    private String getGameId(Document gamePage) {
        try {
            return gamePage.getElementsByClass("js-reco-productlist").first().attr("page-product-id");
        } catch (NullPointerException exc) {
            return "";
        }
    }

    private String getTableRowContent(String[] elemDt) {
        try {
            return String.join("", Arrays.copyOfRange(elemDt, 1, elemDt.length));
        } catch (NullPointerException exc) {
            return "";
        }
    }
}
