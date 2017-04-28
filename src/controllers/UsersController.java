package controllers;


import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import utils.ImageUtils;

public class UsersController {
    @FXML private ImageView userImage;

    public void setInitialText() {
        ImageUtils.cropImage(userImage, 120, 120);
        ImageUtils.roundImage(userImage, 60);
    }
}
