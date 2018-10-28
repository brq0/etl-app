package com.tmw.etl.etlapp.db;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class EtlService {
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

        System.out.println(docsByPages.size()); // CHECK IF THERE 7 PAGES

        return docsByPages;
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
