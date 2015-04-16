import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by maloletniy on 16/04/15.
 */
public class Filter {
    private static Gson gson = new Gson();

    private static List<String> makes;


    public static void main(String[] args) throws IOException {

        initMakes();

        int count = 0;

        File rootDir = new File(new File("").getAbsolutePath());
        System.out.println(rootDir.getAbsolutePath());
        File[] listFiles = rootDir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".json"));
        Set<Map<String, String>> result = new HashSet<Map<String, String>>();
        for (File f : listFiles) {
            System.out.println("reading " + f.getName());
            String json = IOUtils.toString(new FileInputStream(f));
            Collection objects = gson.fromJson(json, Collection.class);

            for (Object m : objects) {
                String make = (String) ((Map) m).get("make");
                make = make.replace("Ural", "Урал");
                make = make.replace("Dnepr", "Днепр");
                make = make.replace("IZH", " ИЖ");
                //if not in makes list -> skip it
                if (makes.contains(make)) {
                    ((Map) m).remove("url");
                    ((Map) m).remove("year");
                    //make was changed to localization
                    if (!make.equals(((Map) m).get("make"))) {
                        ((Map) m).remove("make");
                        ((Map) m).put("make", make);
                    }
                    final boolean add = result.add((Map<String, String>) m);
                    if (!add) {
                        count++;
                    }
                } else {
                    count++;
                }
            }
        }

        System.out.println(result.size() + ", skipped " + count);

        final String json = gson.toJson(result);
        final FileOutputStream outputStream = new FileOutputStream(new File("filtered_models_result.json"));
        IOUtils.write(json, outputStream);
        outputStream.close();

    }

    private static void initMakes() throws IOException {
        URL resource = Filter.class.getResource("makes.json");
        String s = IOUtils.toString(resource);
        makes = gson.fromJson(s, List.class);
    }
}
