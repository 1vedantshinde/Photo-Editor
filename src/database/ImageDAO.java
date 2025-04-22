package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * ImageDAO provides CRUD operations on the "edited_images" table:
 *  - Create: saveImageToDB
 *  - Read:   getImageFromDB
 *  - Update: updateImageName, updateImageData
 *  - Delete: deleteImage
 */
public class ImageDAO {
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/photoeditor?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "foodonthesqltable";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }

    /**
     * Inserts a new image record.
     */
    public static int saveImageToDB(Image fxImage, String imageName) throws SQLException, IOException {
        byte[] imageBytes = imageToBytes(fxImage);
        String sql = "INSERT INTO edited_images (name, image_data) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, imageName);
            stmt.setBytes(2, imageBytes);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        }
    }

    /**
     * Retrieves an Image by its ID, or null if not found.
     */
    public static Image getImageFromDB(int id) throws SQLException, IOException {
        String sql = "SELECT image_data FROM edited_images WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    byte[] bytes = rs.getBytes("image_data");
                    return new Image(new ByteArrayInputStream(bytes));
                }
            }
            return null;
        }
    }

    /**
     * Updates the name (metadata) of an existing image.
     */
    public static boolean updateImageName(int id, String newName) throws SQLException {
        String sql = "UPDATE edited_images SET name = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Replaces the binary data of an existing image.
     */
    public static boolean updateImageData(int id, Image fxImage) throws SQLException, IOException {
        byte[] imageBytes = imageToBytes(fxImage);
        String sql = "UPDATE edited_images SET image_data = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, imageBytes);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an image record by ID.
     */
    public static boolean deleteImage(int id) throws SQLException {
        String sql = "DELETE FROM edited_images WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    //─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Converts JavaFX Image into PNG byte[] via BufferedImage.
     */
    private static byte[] imageToBytes(Image fxImage) throws IOException {
        if (fxImage == null) throw new IllegalArgumentException("Image is null");
        BufferedImage bImage = convertImage(fxImage);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bImage, "png", baos);
            return baos.toByteArray();
        }
    }

    /**
     * Converts a JavaFX Image into a BufferedImage for encoding.
     */
    private static BufferedImage convertImage(Image fxImage) {
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();
        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        PixelReader pixelReader = fxImage.getPixelReader();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = pixelReader.getColor(x, y);
                int alpha = (int) (color.getOpacity() * 255);
                int red   = (int) (color.getRed()     * 255);
                int green = (int) (color.getGreen()   * 255);
                int blue  = (int) (color.getBlue()    * 255);
                int argb  = (alpha << 24) | (red << 16) | (green << 8) | blue;
                bImage.setRGB(x, y, argb);
            }
        }
        return bImage;
    }
}
