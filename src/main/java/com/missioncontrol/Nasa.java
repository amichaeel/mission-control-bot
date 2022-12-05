package com.missioncontrol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import twitter4j.TwitterException;

public class Nasa {

    public static void tweetAOD() throws TwitterException, UnirestException, FileNotFoundException, IOException {
        System.out.println("Running AOD API Call");

        Props props = Props.getProps();
        String nasaKey = props.nasaKey;

        String URL = "https://api.nasa.gov/planetary/apod?api_key=" + nasaKey;
        HttpResponse<JsonNode> response = Unirest.get(URL).asJson();
        JSONObject resObj = response.getBody().getObject();

        String title = resObj.getString("title");
        String text = resObj.getString("explanation");
        String mediaType = resObj.getString("media_type");
        String hdurl;
        String videoUrl;

        if (text.length() > 280) {
            text = text.substring(0, 276);
            if (text.substring(text.length() - 1).equals(" ")) {
                text = text.substring(0, 275);
            }

            text = text.substring(0, text.lastIndexOf(".") + 1);
        }

        switch (mediaType) {
            case "video":
                videoUrl = resObj.getString("url");
                videoUrl = "https://www.youtube.com/watch?v="
                        + videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.indexOf("?"));
                Tweet.newTweet(title + " " + videoUrl);
                break;

            case "image":
                hdurl = resObj.getString("hdurl");

                Image.saveImage(hdurl, title);
                File file = new File("src/img/" + title + ".png");

                Tweet.newTweetWithImage(text, file);
                Image.deleteImage(file);
                break;
        }

    }

    public static void tweetBlueMarble() throws UnirestException, IOException, TwitterException {
        System.out.println("Running Blue Marble API Call");

        Props props = Props.getProps();
        String nasaKey = props.nasaKey;

        String URL1 = "https://api.nasa.gov/EPIC/api/natural/images?api_key=" + nasaKey;
        HttpResponse<JsonNode> response1 = Unirest.get(URL1).asJson();
        JSONArray arr = response1.getBody().getArray();
        JSONObject object = arr.getJSONObject(6);

        String caption = object.getString("caption") + " on " + object.getString("date").split(" ")[0];
        String imageName = object.getString("image");
        String[] date = object.getString("date").split(" ")[0].split("-");
        String year = date[0];
        String month = date[1];
        String day = date[2];

        System.out.println("Getting image....");
        String URL2 = "https://api.nasa.gov/EPIC/archive/natural/" + year + "/" + month + "/" + day + "/png/"
                + imageName + ".png?api_key=" + nasaKey;
        Image.saveImage(URL2, "earth");
        File file = new File("src/img/earth.png");

        Tweet.newTweetWithImage(caption, file);
        Image.deleteImage(file);
    }

    public static void tweetNaturalDisaster() throws Exception {
        String urlString = "https://eonet.gsfc.nasa.gov/api/v3/events";

        // Send the HTTP request and get the response
        HttpResponse<JsonNode> response = Unirest.get(urlString).asJson();
        // Parse the JSON response
        JSONObject responseObject = response.getBody().getObject();
        JSONArray eventsArray = responseObject.getJSONArray("events");
        JSONObject latestEvent = eventsArray.getJSONObject(0);

        String eventTitle = latestEvent.getString("title");

        JSONArray eventGeometryArr = latestEvent.getJSONArray("geometry");
        JSONObject eventGeometryObj = eventGeometryArr.getJSONObject(0);
        String magnitudeValue = "";
        String magnitudeUnit = "";
        if (eventGeometryObj.get("magnitudeValue") != null) {
            magnitudeValue = eventGeometryObj.get("magnitudeValue").toString();
            magnitudeUnit = eventGeometryObj.getString("magnitudeUnit");
        }


        String date = eventGeometryObj.getString("date").substring(0, 10);
        JSONArray cords = eventGeometryObj.getJSONArray("coordinates");
        String lat = cords.get(0).toString();
        String lon = cords.get(1).toString();

        // Tweet the latest natural disaster information
        Tweet.newTweet("Natural Disaster Detected\nEvent Title: " + eventTitle + "\nDate: " + date + "\nLattitude: " + lat + "\nLongitude: " + lon + "\nMagnitude: " + magnitudeValue + " " + magnitudeUnit);
    }
}
