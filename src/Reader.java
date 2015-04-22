import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * Created by maloletniy on 19/04/15.
 */
public class Reader {
    private static final Gson gson = new Gson();
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


    public static void main(String[] args) throws IOException {

        final URL resource = Reader.class.getResource("models.json");
        final String json = IOUtils.toString(resource);

        final Collection collection = gson.fromJson(json, Collection.class);

        Map<String, HashSet<Element>> storage = new HashMap<String, HashSet<Element>>();

        for (Object o : collection) {
            Map<String, String> obj = (Map<String, String>) o;
            String make = obj.get("make");
            String model = obj.get("model");
            String category = obj.get("category");
            Element element = new Element(make, model, category);
            if (!storage.containsKey(make)) {
                HashSet<Element> newSet = new HashSet<Element>();
                newSet.add(element);
                storage.put(make, newSet);
            } else {
                boolean fl = storage.get(make).add(element);
                if (!fl)
                    System.out.println("skip duplicate " + element);
            }
        }
        while (true) {
            System.out.print("command:");
            final String line = readLine();

            int index = line.indexOf(" ");
            String arg = null;
            String cmd = null;
            if (index > 0) {
                arg = line.substring(index + 1).trim();
                cmd = line.substring(0, index).trim();

            } else {
                cmd = line.trim();
            }


            if (cmd.equals("make")) {
                Object[] array = storage.get(arg).toArray();
                Arrays.sort(array);
                for (Object s : array) {
                    System.out.println(s);
                }
            } else if (cmd.equals("makes")) {
                Object[] array = storage.keySet().toArray();
                Arrays.sort(array);
                for (Object s : array) {
                    System.out.println(s);
                }
            } else if (cmd.equals("edit")) {
                Object[] array = storage.get(arg).toArray();
                Arrays.sort(array);
                for (Object s : array) {
                    System.out.println(s + "remove y/n?");
                    String answ = readLine();
                    if (answ.equals("y")) {
                        storage.get(arg).remove(s);
                    }
                }
            }
        }
    }

    private static String readLine() {

        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
