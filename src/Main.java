import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by maloletniy on 18/02/15.
 */
public class Main {

    public static final String URL = "http://www.bikez.com/year/index.php?year=";
    public static final String BASE_URL = "http://www.bikez.com/";

    public static void main(String[] args) throws IOException, InterruptedException {
//        System.setProperty("http.proxyHost", "");
//        System.setProperty("http.proxyPort", "8000");

        List<Item> items = new ArrayList<Item>();
        Main main = new Main();
        System.out.println("start working...");
        for (int year = 1970; year <= 2015; year++) {
            Thread.sleep(5000);
            System.out.println(year);
            items.addAll(main.grabUrl(year));
        }

        System.out.println("serializing...");
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(items);

        writeFile(json);

        System.out.println("Done. Summary count:" + items.size());
    }

    private static void writeFile(String json) throws IOException {
        File out = new File("models.json");
        FileWriter fileWriter = new FileWriter(out);
        fileWriter.write(json);
        fileWriter.close();
        System.out.println("Saved to " + out.getAbsolutePath());
    }


    private List<Item> grabUrl(int year) throws IOException {
        List<Item> result = new ArrayList<Item>();

        Document doc = Jsoup.connect(URL + year).timeout(1000).get();
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
            parseModelDescr(item);
            result.add(item);

        }
        return result;
    }

    private void parseModelDescr(Item item) {
        Document doc = null;
        try {
            Thread.sleep(200);
            doc = Jsoup.connect(item.url).get();
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
        } catch (IOException e) {
            System.err.println(item.url + ": " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Couldn't suspend thread.");
        }

    }
}




