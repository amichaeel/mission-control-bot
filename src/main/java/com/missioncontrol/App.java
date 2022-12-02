package com.missioncontrol;

import java.io.IOException;

import com.mashape.unirest.http.exceptions.UnirestException;

public class App {
    public static void main(String[] args) {
        try {
            Nasa.tweetImageOfTheDay();
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }
    }
}