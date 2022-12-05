package com.missioncontrol;

import java.io.IOException;

import com.mashape.unirest.http.exceptions.UnirestException;

import twitter4j.TwitterException;

public class App {
    public static void main(String[] args) throws Exception {
        Nasa.tweetHazard();
    }
}