package com.tmw.etl.etlapp.processes;

import com.tmw.etl.etlapp.db.entities.Game;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

public class DataTransformer implements Callable<ArrayList<Game>>{
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
}
