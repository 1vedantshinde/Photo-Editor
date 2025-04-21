package transforms;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import javafx.scene.image.ImageView;

public class RotateTransform extends AbstractTransform{
    private double angle;

    public RotateTransform(double angle) {
        this.angle = angle;
    }

    @Override
    public Image applyTransform(Image image) {
        ImageView iv = new ImageView(image);
        iv.getTransforms().add(new Rotate(angle, image.getWidth() / 2, image.getHeight() / 2));
        SnapshotParameters params = new SnapshotParameters();
        return iv.snapshot(params, null);
    }
}
