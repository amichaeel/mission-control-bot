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

    public static void tweetImageOfTheDay() throws TwitterException, UnirestException, FileNotFoundException, IOException {
        System.out.println("Running AOD API Call");

        Props props = Props.getProps();
        String nasaKey = props.nasaKey;

        String URL = "https://api.nasa.gov/planetary/apod?api_key=" + nasaKey;
        HttpResponse<JsonNode> response = Unirest.get(URL).asJson();
        JSONObject resObj = response.getBody().getObject();

        String hdurl = resObj.getString("hdurl");
        String title = resObj.getString("title") + ".png";
        String text = resObj.getString("explanation");

        if (text.length() > 280) {
            text = text.substring(0, 276);
            if (text.substring(text.length()-1).equals(" ")) {
                text = text.substring(0, 275);
            }

            text += "...";
        }

        Image.saveImage(hdurl, title);
        File file = new File("src/img/" + title);
        
        Tweet.newTweetWithImage(text, file);
        Image.deleteImage(file);
    }

    public static void tweetBlueMarble() throws UnirestException, IOException, TwitterException {
        System.out.println("Running Blue Marble API Call");

        Props props = Props.getProps();
        String nasaKey = props.nasaKey;

        String URL1 = "https://api.nasa.gov/EPIC/api/natural/images?api_key=" + nasaKey;
        HttpResponse<JsonNode> response1 = Unirest.get(URL1).asJson();
        JSONArray arr = response1.getBody().getArray();
        JSONObject object = arr.getJSONObject(0);

        String caption = object.getString("caption") + " on " + object.getString("date").split(" ")[0];
        String imageName = object.getString("image");
        String[] date = object.getString("date").split(" ")[0].split("-");
        String year = date[0];
        String month = date[1];
        String day = date[2];

        System.out.println("Getting image....");
        String URL2 = "https://api.nasa.gov/EPIC/archive/natural/" + year + "/" + month + "/" + day + "/png/" + imageName + ".png?api_key=" + nasaKey;
        Image.saveImage(URL2, "earth.png");
        File file = new File("src/img/earth.png");

        Tweet.newTweetWithImage(caption, file);
        Image.deleteImage(file);
    }
}
