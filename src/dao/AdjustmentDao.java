package dao;

import java.sql.*;

public class AdjustmentDao {

    /**
     * Inserts an adjustment record for an image.
     */
    public int saveAdjustment(int imageId, String type, String value) throws SQLException {
        String sql = "INSERT INTO adjustments (image_id, type, value) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, imageId);
            stmt.setString(2, type);
            stmt.setString(3, value);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting adjustment failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserting adjustment failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Deletes all adjustments associated with an image (called when image is deleted).
     */
    public boolean deleteAdjustmentsByImage(int imageId) throws SQLException {
        String sql = "DELETE FROM adjustments WHERE image_id = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, imageId);
            stmt.executeUpdate();
            return true;
        }
    }
}
