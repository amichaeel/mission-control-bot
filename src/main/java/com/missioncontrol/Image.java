package com.missioncontrol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Image {
    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        System.out.println("Saving image titled: " + destinationFile + ", from URL: " + imageUrl);

        try {
            URL urlImage = new URL(imageUrl);
            InputStream in = urlImage.openStream();

            byte[] buffer = new byte[4096];
            int n = -1;

            OutputStream os = new FileOutputStream("src/img/" + destinationFile);

            while ((n = in.read(buffer)) != -1) {
                os.write(buffer, 0, n);
            }

            os.close();

            System.out.println("Image saved");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void deleteImage(File file) throws IOException {
        System.out.println("Deleting file from local storage: " + file.getName());
        file.delete();
        if (!file.exists()) {
            System.out.println("File has been successfully deleted.");
        } else {
            System.out.println("File was not successfully deleted.");
        }
    }

}
