package com.tmw.etl.etlapp.processes;

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

public class DataExtractor implements Callable<ArrayList<Document>> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String baseUrl = "";
    private int maxPages = 1;
    private ArrayList<Document> docsByGames = new ArrayList<>();

    public DataExtractor() {
        baseUrl = "https://www.empik.com/multimedia/xbox-one/gry/,342402,";
        maxPages = 2;
    }

    @Override
    public ArrayList<Document> call() {
        return extractData(null);
    }

    public ArrayList<Document> extractData(EtlProcessor etlProcessor) {
            try {
                for (int i = 1; i <= maxPages * 30; i += 30) {
                    Document doc = Jsoup.connect(baseUrl + "s," + i).get();
                    doc.charset(Charset.forName("UTF-8"));
                    boolean hasElements = !(doc.getAllElements().hasClass("sort notFound"));

                    if (hasElements) {
                        Elements gamesURL = doc.getElementsByClass("img seoImage");
                        for (Element game : gamesURL) {
                            Document gameDoc = Jsoup.connect("https://www.empik.com" + game.attr("href")).get();
                            gameDoc.charset(Charset.forName("UTF-8"));
                            docsByGames.add(gameDoc);
                            if(etlProcessor != null) {
                                etlProcessor.setInfo("E T L: Data is being extracted.. Extracted: " + docsByGames.size() + " games.");
                            }
                        }
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        logger.info("Extracted " + docsByGames.size() + " games."); //
        return docsByGames;
    }

    public int getDataSize() {
        return docsByGames.size();
    }
}
