package controllers.users;


import com.jfoenix.controls.JFXRippler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import utils.ImageUtils;

public class UserTabController {

    @FXML
    private JFXRippler editButtonRipple;

    @FXML
    private Pane editButton;

    @FXML
    private Label userAddress;

    @FXML
    private ImageView userImage;

    @FXML
    private ImageView editButtonImage;

    @FXML
    private Label userPhone;

    @FXML
    private Label userEmail;

    @FXML
    private Label userName;

    public void setInitialText() {
        ImageUtils.cropImage(userImage, 120, 120);
        ImageUtils.roundImage(userImage, 60);

        editButtonRipple.setControl(editButton);
    }
}
