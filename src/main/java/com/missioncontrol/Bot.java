package com.missioncontrol;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Bot {
    public static void launch() throws Exception {
        System.out.println("Bot launched");
        boolean running = true;

        // Times for bot to call API's;
        LocalTime AODTime = LocalTime.parse("08:00");
        int botCycle = 0;

        while (running) {
            botCycle += 1;
            System.out.println("Bot Cycle Count: " + botCycle);
            LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

            // AOD API
            if (now.equals(AODTime)) {
                Nasa.tweetAOD();
            }

            // Hazard API Call
            if (now.toString().split(":")[1].equals("30") || now.toString().split(":")[1].equals("00")) {
                System.out.println("CONTACTING NASA HAZARD API");
                Nasa.tweetHazard();
            }

            Thread.sleep(30000);
        }
    }

}
