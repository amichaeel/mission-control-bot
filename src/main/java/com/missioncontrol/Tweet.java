package com.missioncontrol;

import twitter4j.*;
import twitter4j.v1.*;

import java.io.*;

public class Tweet {

    public static void newTweet(String tweet) throws TwitterException, IOException {
        Props keys = Props.getProps();
        Twitter twitter = Twitter.newBuilder().oAuthConsumer(keys.consumerKey, keys.consumerSecret).oAuthAccessToken(keys.accessToken, keys.accessSecret).build();
        Status status = twitter.v1().tweets().updateStatus(tweet);
        System.out.println("Successfully updated the status to [" + status.getText() + "].");
    }

    public static void newTweetWithImage(String title, File file) throws TwitterException, IOException {
        Props keys = Props.getProps();
        Twitter twitter = Twitter.newBuilder().oAuthConsumer(keys.consumerKey, keys.consumerSecret).oAuthAccessToken(keys.accessToken, keys.accessSecret).build();
        StatusUpdate statusUpdate = StatusUpdate.of(title).media(file);
        Status status = twitter.v1().tweets().updateStatus(statusUpdate);
        System.out.println("Successfully updated the status to [" + status.getText() + "].");
    }
}
