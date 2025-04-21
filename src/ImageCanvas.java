import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import filters.Filter;
import transforms.Transform;
import exceptions.ImageNotLoadedException;
import exceptions.AdjustmentOutOfBoundsException;

public class ImageCanvas {

    private StackPane canvasPane;
    private ImageView imageView;
    private Image currentImage;

    public ImageCanvas() {
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800);
        imageView.setFitHeight(600);

        canvasPane = new StackPane(imageView);
    }

    public void setImage(Image image) {
        this.currentImage = image;
        imageView.setImage(image);
    }

    public void applyFilter(Filter filter) {
        try {
            if (currentImage == null) {
                throw new ImageNotLoadedException("Please load an image before applying filters.");
            }
            Image filtered = filter.applyFilter(currentImage);
            setImage(filtered);
        } catch (ImageNotLoadedException e) {
            System.out.println("Filter Error: " + e.getMessage());
        }
    }

    public void applyTransform(Transform transform) {
        try {
            if (currentImage == null) {
                throw new ImageNotLoadedException("Please load an image before applying transforms.");
            }
            Image transformed = transform.applyTransform(currentImage);
            setImage(transformed);
        } catch (ImageNotLoadedException e) {
            System.out.println("Transform Error: " + e.getMessage());
        }
    }

    public StackPane getCanvasPane() {
        return canvasPane;
    }

    public Image getCurrentImage() {
        return currentImage;
    }
}