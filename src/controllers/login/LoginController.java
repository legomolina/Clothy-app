package controllers.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import models.User;
import utils.AnimationHandler;
import utils.BCrypt;
import utils.DatabaseHandler;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

            new Thread(() -> {
                if(username.getText().equals("")) {

                }

                Connection connection = DatabaseHandler.getConnection();
                String selectSQL = "SELECT * FROM users WHERE user_login_name = ?";
                User activeUser = null;

                try {
                    PreparedStatement statement = connection.prepareStatement(selectSQL);
                    statement.setString(1, username.getText());

                    ResultSet result = statement.executeQuery();

                    while (result.next()) {
                        if (BCrypt.checkpw(password.getText(), result.getString("user_login_password"))) {
                            activeUser = new User(result.getInt("user_id"), result.getString("user_name"),
                                    result.getString("user_surname"), result.getString("user_address"),
                                    result.getString("user_phone"), result.getString("user_email"),
                                    result.getString("user_login_name"), result.getString("user_login_password"),
                                    result.getString("user_login_type"));

                            break;
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("An error occurred with Database connection");
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    System.out.println("An error occurred while preparing the Query: " + selectSQL);
                    e.printStackTrace();
                }

                System.out.println("hola: " + activeUser.getName());

                new AnimationHandler().fadeOut(loader, 500).execute();
            }).start();



            //new MainMenu();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginBackground.setEffect(new BoxBlur(7, 7, 3));

        setListeners();
    }
}
