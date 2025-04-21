package filters;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class VignetteFilter extends AbstractFilter{
    public Image applyFilter(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);

        WritableImage output = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double dx = centerX - x;
                double dy = centerY - y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                double vignetteFactor = 1 - (distance / maxDistance) * 0.5;

                Color color = reader.getColor(x, y);
                Color newColor = color.deriveColor(0, 1, vignetteFactor, 1.0);
                writer.setColor(x, y, newColor);
            }
        }

        return output;
    }
}
