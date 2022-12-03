package com.missioncontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void launch() {
        try (ServerSocket serverSocket = new ServerSocket(9991)) {
            Socket connectionSocket = serverSocket.accept();

            InputStream inputToServer = connectionSocket.getInputStream();
            OutputStream outputFromServer = connectionSocket.getOutputStream();

            Scanner scanner = new Scanner(inputToServer, "UTF-8");
            PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);

            // Have the server take input from the client and echo it back
            // This should be placed in a loop that listens for a terminator text e.g. bye
            boolean done = false;

            do {
                serverPrintOut.println("Server running");
            } while (!done);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
