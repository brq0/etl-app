package com.tmw.etl.etlapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class DataExtractor implements Callable<ArrayList<Document>> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Document rawData;

    public DataExtractor() {

    }

    @Override
    public ArrayList<Document> call() {
        return extractData();
    }

    private ArrayList<Document> extractData() {
        ArrayList<Document> docsByPages = new ArrayList<>();
        try {
            for (int i = 1; ; i += 60) {
                Document doc = Jsoup.connect("https://www.empik.com/multimedia/xbox-one/gry/,342402,s," + i + "?resultsPP=60").get();
                doc.charset(Charset.forName("UTF-8"));
                Boolean hasElements = !(doc.getAllElements().hasClass("sort notFound"));

                if (hasElements)
                    docsByPages.add(doc);
                else
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("" + docsByPages.size()); // CHECK IF THERE 7 PAGES

//        @TODO handle null
        return docsByPages;
    }
}
