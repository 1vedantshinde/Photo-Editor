package dao;

import java.sql.*;
import java.io.ByteArrayInputStream;

public class ImageDao {

    /**
     * Inserts an image blob and returns generated image ID.
     */
    public int saveImage(String filename, byte[] imageData, int width, int height) throws SQLException {
        String sql = "INSERT INTO images (filename, data, width, height) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, filename);
            stmt.setBlob(2, new ByteArrayInputStream(imageData));
            stmt.setInt(3, width);
            stmt.setInt(4, height);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting image failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserting image failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves raw image bytes by image ID.
     */
    public byte[] getImageData(int imageId) throws SQLException {
        String sql = "SELECT data FROM images WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, imageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Blob blob = rs.getBlob("data");
                    return blob.getBytes(1, (int) blob.length());
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Deletes an image (and cascades adjustments) by its ID.
     */
    public boolean deleteImage(int imageId) throws SQLException {
        String sql = "DELETE FROM images WHERE id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, imageId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }
}
