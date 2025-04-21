package filters;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class BlueFilter extends AbstractFilter{
    public Image applyFilter(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage output = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, y, new Color(0, 0, color.getBlue(), color.getOpacity()));
            }
        }

        return output;
    }
}
