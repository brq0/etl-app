package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class EtlProcessor implements Callable<Integer> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private GameRepository gameRepository;

    public EtlProcessor(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Integer call() {
        ArrayList<Document> pagesDocs = extractData();
        ArrayList<Game> games = transformData(pagesDocs);
        return loadData(games);
    }

    private ArrayList<Document> extractData() {
        ArrayList<Document> docsByPages = new ArrayList<>();
        try {
            for (int i = 1; ; i += 60) {
                Document doc = Jsoup.connect("https://www.empik.com/multimedia/xbox-one/gry/,342402,s," + i + "?resultsPP=60").get();
                doc.charset(Charset.forName("UTF-8"));
                boolean hasElements = !(doc.getAllElements().hasClass("sort notFound"));

                if (hasElements)
                    docsByPages.add(doc);
                else
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("" + docsByPages.size()); // CHECK IF THERE 7 PAGES
        return docsByPages;
    }

    private ArrayList<Game> transformData(ArrayList<Document> rawDataPages) {
        ArrayList<Game> games = new ArrayList<>();
        int position = 1;
        for (Document doc : rawDataPages) {
            Elements elements = doc.getElementsByClass("js-reco-product");
            for (Element element : elements) {
                Game game = new Game();

                String productId = element.attr("data-product-id");
                String productName = element.attr("data-product-name");
                String productCategory = element.attr("data-product-category");
                String productPrice = element.getElementsByClass("price").text();
                String productImageUrl = element.getElementsByClass("lazy").attr("lazy-img");

                if (productPrice.equals("")) {
                    productPrice = "product unvailable";
                }
                if (productId.equals("")) {
                    continue;
                }
                if (productName.equals("")) {
                    continue;
                }

                game.setProductId(productId);
                game.setProductName(productName);
                game.setProductCategory(productCategory);
                game.setProductPrice(productPrice);
                game.setProductImageUrl(productImageUrl);
                game.setPosition(position++);

                games.add(game);
            }
        }
        return games;
    }

    private Integer loadData(ArrayList<Game> games) {
        logger.info("LOADING DATA");
        Integer counter = 0;
        for (Game game : games) {
            if (!gameRepository.findById(game.getProductId()).isPresent()) {         //if not in db
                gameRepository.save(game);
                logger.debug(game.toString());
                counter++;
            }
        }
        return counter;
    }
}
