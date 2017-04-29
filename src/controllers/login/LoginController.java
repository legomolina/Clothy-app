package controllers.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import utils.AnimationHandler;
import views.main_menu.MainMenu;
import views.users.UserTab;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton submit;
    @FXML
    private Pane loader;
    @FXML
    private Pane loginBackground;

    private void setListeners() {
        submit.setOnAction(actionEvent -> {
            loader.setVisible(true);
            new AnimationHandler().fadeIn(loader, 500, 0.6).execute();

            new MainMenu();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginBackground.setEffect(new BoxBlur(7, 7, 3));

        setListeners();
    }
}
