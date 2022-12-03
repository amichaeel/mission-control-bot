package com.missioncontrol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import twitter4j.TwitterException;

public class Nasa {

    public static void tweetImageOfTheDay() throws UnirestException, FileNotFoundException, IOException {
        String URL = "https://api.nasa.gov/planetary/apod?api_key=";
        Props props = Props.getProps();
        String nasaKey = props.nasaKey;
        HttpResponse<JsonNode> response = Unirest.get(URL + nasaKey).asJson();
        JSONObject resObj = response.getBody().getObject();
        String hdurl = resObj.getString("hdurl");
        String title = resObj.getString("title") + ".png";
        String text = resObj.getString("explanation");
        if (text.length() > 280) {
            text = text.substring(0, 276) + "...";
        }

        Image.saveImage(hdurl, title);
        File file = new File("src/img/" + title);
        try {
            Tweet.newTweetWithImage(text, file);
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        Image.deleteImage(file);
    }
}
