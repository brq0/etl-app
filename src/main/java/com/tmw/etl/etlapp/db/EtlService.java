package com.tmw.etl.etlapp.db;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EtlService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public String getData() {
        logger.info("GETTING DATA");

        try {
            Document doc = Jsoup.connect("https://www.empik.com/multimedia/xbox-one/gry/,342402,s,1?resultsPP=60").get();
            for (Element item : doc.getElementsByClass("search-list-item js-reco-product ta-product-tile ")){
                String id = item.attr("data-product-id");
                String name = item.attr("data-product-name");
                String category = item.attr("data-product-category");
                String img = "";
                for (Element subItem : item.getElementsByClass("lazy")) {
                    img = subItem.attr("lazy-img");
                    break;
                }

                logger.info(id);
                logger.info(name);
                logger.info(category);
                logger.info(img);
            }
        }catch (IOException exc){
            logger.error("ERROR IO");
        }


        return "SOME DATA";
    }

    public String transferData(String data) {
        logger.info("DATA: " + data);
        logger.info("TRANSFERING DATA");

        return "AFTER TRANSFER --- " + data;
    }

    public void loadData(String transferredData) {
        logger.info("LOADING DATA");
        logger.info("LOADED DATA: " + transferredData);
    }
}
