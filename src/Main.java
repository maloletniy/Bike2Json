import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maloletniy on 18/02/15.
 */
public class Main {

    public static final String URL = "http://www.bikez.com/year/index.php?year=";
    public static final String BASE_URL = "http://www.bikez.com/";

    private static final int PAUSE_INTERVAL = 5000;
    private static final int SHORT_PAUSE_INTERVAL = 200;

    private static final int FROM_YEAR = 1970;
    private static final int TO_YEAR = 1979;

    public static void main(String[] args) throws IOException, InterruptedException {
//        System.setProperty("http.proxyHost", "");
//        System.setProperty("http.proxyPort", "8000");

        Main main = new Main();
        System.out.println("start working...");

        for (int year = FROM_YEAR; year <= TO_YEAR; year++) {

            List<Item> items = main.getItemsByYear(year);

            System.out.println("loading descriptions");
            for (Item i : items) {
                Thread.sleep(SHORT_PAUSE_INTERVAL);
                main.getItemDescription(i);
            }

            System.out.println("serializing...");
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            String json = gson.toJson(items);

            writeFile(year, json);

            System.out.println("Done. Summary count:" + items.size());
            Thread.sleep(PAUSE_INTERVAL);
        }

    }

    private static void writeFile(int year, String json) throws IOException {
        File out = new File(year + "models.json");
        FileWriter fileWriter = new FileWriter(out);
        fileWriter.write(json);
        fileWriter.close();
        System.out.println("Saved to " + out.getAbsolutePath());
    }


    private List<Item> getItemsByYear(int year) throws IOException, InterruptedException {
        List<Item> result = new ArrayList<Item>();

        Document doc = connectAndGetDocument(URL + year);

        Element table = doc.select("body table.zebra").get(0);
        //remove table header
        table.select("tr.head").remove();
        //remove ads
        table.select("tr td[rowspan]").remove();

        Elements rows = table.select("tr");
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            String make;
            String model;
            String url;
            if (row.children().size() == 3) {
                make = row.child(1).text();
                model = row.child(0).text();
                url = row.child(0).child(0).attr("href");

            } else {
                model = row.child(0).text();
                url = row.child(0).child(0).attr("href");
                i++;
                row = rows.get(i);
                make = row.child(0).text();
            }
            url = url.replaceFirst("../", BASE_URL);
            model = model.substring(model.indexOf(make) + make.length() + 1);
            Item item = new Item(make, model, String.valueOf(year), url);
//            getItemDescription(item);
            result.add(item);

        }
        return result;
    }

    private void getItemDescription(Item item) {
        Document doc = null;
        try {
            Thread.sleep(SHORT_PAUSE_INTERVAL);
            doc = connectAndGetDocument(item.url);
            Elements rows = doc.select("table.Grid tr");
            for (Element row : rows) {
                if (row.children().size() == 0) {
                    continue;
                }
                if (row.child(0).text().equalsIgnoreCase("category:")) {
                    item.setCategory(row.child(1).text());
                    break;
                }
//                else if (row.child(0).text().equalsIgnoreCase("engine type:")) {
//                    item.setEngine(row.child(1).text());
//                }
                //if fields already set then break cycle
//                if (item.engine != null && item.category != null) {
//                    break;
//                }
            }
        } catch (InterruptedException e) {
            System.out.println("Couldn't suspend thread.");
        }

    }

    /**
     * Tries get document continously with 5sec timeout in case of exception
     */
    private Document connectAndGetDocument(String url) throws InterruptedException {
        Document doc = null;
        System.out.println("connection to: " + url);
        while (doc == null) {
            try {
                doc = Jsoup.connect(url).timeout(1000).get();
            } catch (Exception e) {
                System.out.println("Error. Trying to reconnect");
                Thread.sleep(PAUSE_INTERVAL);
            }
        }
        return doc;
    }
}




