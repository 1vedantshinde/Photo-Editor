package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;


import java.awt.image.BufferedImage;

public class ImageDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/photoeditor";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "foodonthesqltable";

    // Save Image to the database
    public static void saveImageToDB(Image fxImage, String imageName) throws SQLException, IOException {
        if (fxImage == null) {
            throw new IllegalArgumentException("Image is null. Cannot save to database.");
        }

        // Convert JavaFX Image to BufferedImage
        BufferedImage bImage = convertImage(fxImage);

        // Write to byte array in PNG format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // Save to database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO edited_images (name, image_data) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, imageName);
            stmt.setBytes(2, imageBytes);
            stmt.executeUpdate();
        }
    }

    // Custom method to convert JavaFX Image to BufferedImage
    private static BufferedImage convertImage(Image fxImage) {
        if (fxImage == null) {
            throw new IllegalArgumentException("JavaFX Image cannot be null.");
        }

        // Convert JavaFX Image to BufferedImage manually
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();
        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        PixelReader pixelReader = fxImage.getPixelReader();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pixelReader.getColor(x, y);

                // Convert to ARGB integer values (0-255)
                int alpha = (int) (color.getOpacity() * 255);
                int red = (int) (color.getRed() * 255);
                int green = (int) (color.getGreen() * 255);
                int blue = (int) (color.getBlue() * 255);

                int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                bImage.setRGB(x, y, argb);
            }
        }

        return bImage;
    }
}
