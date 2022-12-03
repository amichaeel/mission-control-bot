package com.missioncontrol;

import java.io.*;
import java.util.*;

public class Props {
    String consumerKey;
    String consumerSecret;
    String accessToken;
    String accessSecret;
    String nasaKey;

    public Props() {

    }

    public static Props getProps() throws FileNotFoundException, IOException {
        Props propsObj = new Props();
        Properties props = readPropertiesFile("props.properties");
        
        propsObj.consumerKey = props.getProperty("oauth.consumerKey");
        propsObj.consumerSecret = props.getProperty("oauth.consumerSecret");
        propsObj.accessToken = props.getProperty("oauth.accessToken");
        propsObj.accessSecret = props.getProperty("oauth.accessTokenSecret");
        propsObj.nasaKey = props.getProperty("nasaKey");

        return propsObj;
    }

    public static Properties readPropertiesFile(String fileName) throws FileNotFoundException, IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            fis.close();
        }
        return prop;
    }

}
