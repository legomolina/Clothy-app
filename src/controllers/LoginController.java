package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import utils.AnimationHandler;
import views.users.UserTab;
import views.users.Users;

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

    private void setListeners() {
        submit.setOnAction(actionEvent -> {
            loader.setVisible(true);
            new AnimationHandler().fadeIn(loader, 500, 0.6).execute();

            new UserTab();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setListeners();
    }
}
