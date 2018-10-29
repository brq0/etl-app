package com.tmw.etl.etlapp;

import com.tmw.etl.etlapp.db.EtlService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.Callable;

public class DataExtractor implements Callable<Document> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Thread thrd;
    private Document rawData;

    @Autowired
    EtlService etlService;

    public DataExtractor(){

    }

    public DataExtractor(Document rawData) {
        this.rawData = rawData;
        thrd = new Thread();
        thrd.start();
    }

    public Thread getThread(){
        return thrd;
    }

    @Override
    public Document call() {
        return extractData();
    }

    public Document extractData(){
        try {
            rawData = Jsoup.connect("https://www.empik.com/multimedia/xbox-one/gry/,342402,s,1?resultsPP=60").get();
            for (Element item : rawData.getElementsByClass("search-list-item js-reco-product ta-product-tile ")){
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

//        @TODO handle null
        return rawData;
    }
}
