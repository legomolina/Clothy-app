package utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class ImageUtils {
    public static void cropImage(ImageView image, double finalMeasureX, double finalMeasureY) {
        double newMeasure = (image.getImage().getWidth() < image.getImage().getHeight()) ? image.getImage().getWidth() : image.getImage().getHeight();
        double x = (image.getImage().getWidth() - newMeasure) / 2;
        double y = (image.getImage().getHeight() - newMeasure) / 2;

        Rectangle2D rect = new Rectangle2D(x, y, newMeasure, newMeasure);
        image.setViewport(rect);
        image.setFitWidth(finalMeasureX);
        image.setFitHeight(finalMeasureY);
        image.setSmooth(true);
    }

    public static void roundImage(ImageView image, double radius) {
        double x = image.getFitWidth() / 2;
        double y = image.getFitHeight() / 2;

        Circle clip = new Circle(x, y, radius);

        image.setClip(clip);
    }
}

