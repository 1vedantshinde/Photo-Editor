package transforms;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FlipTransform extends AbstractTransform{
    private boolean horizontal;

    public FlipTransform(boolean horizontal) {
        this.horizontal = horizontal;
    }

    @Override
    public Image applyTransform(Image image) {
        ImageView iv = new ImageView(image);
        if (horizontal) {
            iv.setScaleX(-1);
        } else {
            iv.setScaleY(-1);
        }
        SnapshotParameters params = new SnapshotParameters();
        params.setViewport(new Rectangle2D(0, 0, image.getWidth(), image.getHeight()));
        return iv.snapshot(params, null);
    }
}
