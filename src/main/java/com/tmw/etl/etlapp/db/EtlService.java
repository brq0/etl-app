package com.tmw.etl.etlapp.db;

import com.tmw.etl.etlapp.db.entities.Game;
import com.tmw.etl.etlapp.db.repositories.GameRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class EtlService {

    @Autowired
    private GameRepository gameRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ArrayList<Document> getData() {
        logger.info("GETTING DATA");

        ArrayList<Document> docsByPages = new ArrayList<>();

        try{

            for(int i = 1; ; i += 60){
                Document doc = Jsoup.connect("https://www.empik.com/multimedia/xbox-one/gry/,342402,s," + i + "?resultsPP=60").get();
                Boolean hasElements = !(doc.getAllElements().hasClass("sort notFound"));

                if(hasElements)
                    docsByPages.add(doc);
                else
                    break;

            }
//           Elements elements = doc.getElementsByClass("js-reco-product");
//
//           for (Element item : elements){
//               System.out.println(item.attr("data-product-id"));
//           }

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("" + docsByPages.size()); // CHECK IF THERE 7 PAGES

        return docsByPages;
    }

    public ArrayList<Game> transformData(ArrayList<Document> htmlAllPages) {
        logger.info("TRANSFORMING DATA");

        ArrayList<Game> games = new ArrayList<>();


        for (Document doc : htmlAllPages ){

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

                logger.info("PRODUCT ID: " + productId + " *** PRODUCT NAME: " + productName + " *** PRODUCT CATEGORY: " + productCategory + " *** PRICE: " + productPrice + " \nIMAGE URL: " + productImageUrl);

                game.setProductId(productId);
                game.setProductName(productName);
                game.setProductCategory(productCategory);
                game.setProductPrice(productPrice);
                game.setProductImageUrl(productImageUrl);

                games.add(game);
            }

        }

        return games;
    }

    public void loadData(ArrayList<Game> transofmedData) {
        logger.info("LOADING DATA");
        logger.info("LOADED DATA: " + transofmedData);

        for (Game game : transofmedData) {
            gameRepository.save(game);
        }



    }
}
