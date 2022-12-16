package com.missioncontrol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

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
        // Access NASA API Key
        Props keys = Props.getProps();
        String nasaKey = keys.nasaKey;

        // Contact api and parse information
        String URL = "https://api.nasa.gov/planetary/apod?api_key=" + nasaKey;
        HttpResponse<JsonNode> response = Unirest.get(URL).asJson();
        JSONObject resObj = response.getBody().getObject();
        String title = resObj.getString("title");
        String text = resObj.getString("explanation");
        String mediaType = resObj.getString("media_type");
        String imageURL;
        String videoUrl;

        // Shorten explanation to fit within twitter character limit
        if (text.length() > 280) {
            text = text.substring(0, 280);
            text = text.substring(0, text.lastIndexOf(".") + 1);
        }

        // Tweet based on media type
        switch (mediaType) {
            case "video":
                // obtain video URL
                videoUrl = resObj.getString("url");

                // Reformat URL to allow for embeding
                videoUrl = "https://www.youtube.com/watch?v="
                        + videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.indexOf("?"));
                Tweet.newTweet(title + " " + videoUrl);
                break;

            case "image":
                // Obtain HD image
                imageURL = resObj.getString("hdurl");
                Image.saveImage(imageURL, title);
                File HDImage = new File("src/img/" + title + ".png");

                // If image size is too large for twitter, obtain SD image
                if ((double) HDImage.length() / (1024 * 1024) > 512) {
                    System.out.println("HD File too large for Twitter. Retreving smaller file.");
                    imageURL = resObj.getString("url");

                    // Delete HD image from local memory and save SD image
                    Image.deleteImage(HDImage);
                    Image.saveImage(imageURL, title);
                    File SDImage = new File("src/img/" + title + ".png");

                    // Tweet SD Image
                    Tweet.newTweetWithImage(text, SDImage);

                    // Delete image from local storage
                    Image.deleteImage(SDImage);
                    break;
                }
                
                // Tweet HD image
                Tweet.newTweetWithImage(text, HDImage);

                // Delete image from local storage
                Image.deleteImage(HDImage);
                break;
        }

    }

    public static void tweetBlueMarble() throws UnirestException, IOException, TwitterException {
        System.out.println("Running Blue Marble API Call");

        // Access NASA API Key
        Props keys = Props.getProps();
        String nasaKey = keys.nasaKey;

        // Construct URL for NASA API
        String nasaURl = "https://api.nasa.gov/EPIC/api/natural/images?api_key=" + nasaKey;
        HttpResponse<JsonNode> response1 = Unirest.get(nasaURl).asJson();
        JSONArray arr = response1.getBody().getArray();
        JSONObject object = arr.getJSONObject(6);
        String caption = object.getString("caption") + " on " + object.getString("date").split(" ")[0];
        String imageName = object.getString("image");
        String[] date = object.getString("date").split(" ")[0].split("-");
        String year = date[0];
        String month = date[1];
        String day = date[2];

        // Given info for image, construct URL to access specific image
        System.out.println("Getting image....");
        String URL2 = "https://api.nasa.gov/EPIC/archive/natural/" + year + "/" + month + "/" + day + "/png/"
                + imageName + ".png?api_key=" + nasaKey;

        // Save image
        Image.saveImage(URL2, "earth");
        File file = new File("src/img/earth.png");

        //Tweet image and delete from local system
        Tweet.newTweetWithImage(caption, file);
        Image.deleteImage(file);
    }

    public static void tweetHazard() throws Exception {
        System.out.println("Running hazard API Call");

        // Access API keys
        Props keys = Props.getProps();

        // Access text document with most recent event
        File data = new File("src/data/latestEvent.txt");
        FileReader fr = new FileReader(data);
        BufferedReader br = new BufferedReader(fr);
        String latestEventTitle = br.readLine();
        br.close();

        // EONET URl
        String eonetURL = "https://eonet.gsfc.nasa.gov/api/v3/events";

        // Contact EONET database to retrieve the latest event
        HttpResponse<JsonNode> response = Unirest.get(eonetURL).asJson();
        JSONObject responseObject = response.getBody().getObject();
        JSONArray eventsArray = responseObject.getJSONArray("events");
        JSONObject latestEvent = eventsArray.getJSONObject(0);

        // Access title of event and compare to system's latest event
        String eventTitle = latestEvent.getString("title");
        if (eventTitle.equals(latestEventTitle)) {
            // If latest event from EONET is same as system's, exit method
            System.out.println("No new event found. Exiting API Call.");
            return;
        }

        // Update system's latest event
        latestEventTitle = eventTitle;
        PrintWriter pw = new PrintWriter(data);
        pw.print(latestEventTitle);
        pw.close();

        // Parse through JSON to access coordinates, magniute of event, and date
        JSONArray eventGeometryArr = latestEvent.getJSONArray("geometry");
        JSONObject eventGeometryObj = eventGeometryArr.getJSONObject(0);
        String date = eventGeometryObj.getString("date").substring(0, 10);
        JSONArray cords = eventGeometryObj.getJSONArray("coordinates");
        String lat = cords.get(1).toString();
        String lon = cords.get(0).toString();
        String magnitudeValue = "";
        String magnitudeUnit = "";

        // If magnitude of event exists, set unit and value
        if (eventGeometryObj.get("magnitudeValue") != null) {
            magnitudeValue = eventGeometryObj.get("magnitudeValue").toString();
            magnitudeUnit = eventGeometryObj.get("magnitudeUnit").toString();
        }

        // Call Geoapify API to get name of location of coordinates
        System.out.println("Contacting Geoapify for coordinates information...");
        String geoAPI = "https://api.geoapify.com/v1/geocode/reverse?lon=" + lon + "&lat=" + lat + "&format=json&apiKey=" + keys.geoapifykey;
        HttpResponse<JsonNode> geoapify = Unirest.get(geoAPI).asJson();

        // Parse through response
        JSONObject geoResults = geoapify.getBody().getObject();
        JSONArray geoArray = geoResults.getJSONArray("results");
        JSONObject geoInfo = geoArray.getJSONObject(0);
        String location = "";

        // If location is not on land, retrieve name, else get city and country
        if (geoInfo.has("name")) {
            location = geoInfo.getString("name");
        } else if (geoInfo.has("country")) {
            location = geoInfo.getString("city") + ", " + geoInfo.getString("country");
        }

        // Construct OpenAI api call, passing in prompt
        String prompt;

        // Make sure promt only includes magnitude if it exists
        if (!magnitudeValue.equals("null")) {
            prompt = "Event Title: " + eventTitle + "\nDate: " + date + "\nNear: " + location + "\nMagnitude: " + magnitudeValue + " " + magnitudeUnit;
        }
        prompt = "Event Title: " + eventTitle + "\nDate: " + date + "\nNear: " + location;

        // Contact OpenAI to Structure information into a fluent sentence
        String res = OpenAI.fluentSentence(prompt);

        // Tweet
        Tweet.newTweet(res);
    }
}
