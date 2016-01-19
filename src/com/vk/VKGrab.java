package com.vk;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maloletniy on 20/01/16.
 */
public class VKGrab {

    public static void main(String[] args) throws IOException, InterruptedException {

        final String albums = readUrl("https://api.vk.com/method/photos.getAlbums?owner_id=228104309");

        final HashMap response = new Gson().fromJson(albums, HashMap.class);

        List<Map> elements = (List<Map>) response.get("response");

        for (Map el : elements) {
            String owner = (String) el.get("owner_id");
            String title = (String) el.get("title");
            String thumb_id = (String) el.get("thumb_id");

            final String thumb_data = readUrl("https://api.vk.com/method/photos.getById?photos=" + owner + "_" + thumb_id);
            final HashMap imageResponse = new Gson().fromJson(thumb_data, HashMap.class);
            List thumbs = (List) imageResponse.get("response");
            String imageUrl = (String) ((Map) thumbs.get(0)).get("src_big");

            System.out.println(String.format("Категория;%s;0;;%s", title, imageUrl));

            Thread.sleep(100);
        }
    }


    private static String readUrl(String url) throws IOException {
        StringBuilder result = new StringBuilder();

        URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
//            System.out.println(inputLine);
            result.append(inputLine);
        }
        in.close();

        return result.toString();
    }
}
