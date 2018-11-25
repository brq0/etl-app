package com.tmw.etl.etlapp.processes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.Doc;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class DataExtractor implements Callable<ArrayList<Document>> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public DataExtractor() {

    }

    @Override
    public ArrayList<Document> call() {
        return extractData();
    }

    private ArrayList<Document>  extractData() {
        ArrayList<Document> docsByPages = new ArrayList<>();
        ArrayList<Document> docsByGames = new ArrayList<>();

        try {
//            for (int i = 1; ; i += 60) {

//                Document doc = Jsoup.connect("https://www.empik.com/multimedia/xbox-one/gry/,342402,s," + i + "?resultsPP=60").get();
            Document doc = Jsoup.connect("https://www.empik.com/multimedia/xbox-one/gry/,342402,s?resultsPP=60").get();
                doc.charset(Charset.forName("UTF-8"));

                boolean hasElements = !(doc.getAllElements().hasClass("sort notFound"));

                if (hasElements) {
                    docsByPages.add(doc);

                    Elements gamesURL = doc.getElementsByClass("img seoImage");

                    for (Element game : gamesURL) {

                        System.out.println(game.attr("href"));
                        Document gameDoc = Jsoup.connect("https://www.empik.com" + game.attr("href")).get();
                        gameDoc.charset(Charset.forName("UTF-8"));

                        docsByGames.add(gameDoc);
                    }

                }else{
//                    break;
                }

//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Extracted " + docsByGames.size() + " games."); //
        return docsByGames;
    }
}
