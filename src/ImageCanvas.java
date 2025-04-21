import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import filters.Filter;
import transforms.Transform;

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
        if (currentImage != null) {
            Image filtered = filter.applyFilter(currentImage);
            setImage(filtered);
        }
    }

    public void applyTransform(Transform transform) {
        if (currentImage != null) {
            Image transformed = transform.applyTransform(currentImage);
            setImage(transformed);
        }
    }

    public StackPane getCanvasPane() {
        return canvasPane;
    }

    public Image getCurrentImage() {
        return currentImage;
    }
}
