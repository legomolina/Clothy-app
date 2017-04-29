package controllers.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import models.Employee;
import utils.AnimationHandler;
import utils.BCrypt;
import utils.DatabaseHandler;
import views.main_menu.MainMenu;
import views.users.UserTab;

import javax.swing.text.BoxView;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private static final double width = 847;
    private static final double height = 1271;

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton submit;
    @FXML
    private Label errorText;
    @FXML
    private Pane loader;
    @FXML
    private Pane frostView;
    @FXML
    private ImageView frostImage;

    private void setListeners() {
        submit.disableProperty().bind((username.textProperty().isNotEmpty()).and(password.textProperty().isNotEmpty()).not());

        username.textProperty().addListener((observableValue, s, t1) -> {
            if(errorText.isVisible())
                new AnimationHandler().fadeOut(errorText, 250).execute(actionEvent -> errorText.setVisible(false));
        });

        password.textProperty().addListener((observableValue, s, t1) -> {
            if(errorText.isVisible())
                new AnimationHandler().fadeOut(errorText, 250).execute(actionEvent -> errorText.setVisible(false));
        });

        submit.setOnAction(actionEvent -> {
            loader.setVisible(true);
            new AnimationHandler().fadeIn(loader, 500, 0.6).execute();

            new Thread(() -> {

                Connection connection = DatabaseHandler.getConnection();
                String selectSQL = "SELECT * FROM employees WHERE employee_login_name = ?";
                Employee activeEmployee = null;

                try {
                    PreparedStatement statement = connection.prepareStatement(selectSQL);
                    statement.setString(1, username.getText());

                    ResultSet result = statement.executeQuery();

                    while (result.next()) {
                        if (BCrypt.checkpw(password.getText(), result.getString("employee_login_password")) && result.getInt("employee_is_active") > 0) {
                            activeEmployee = new Employee(result.getInt("employee_id"), result.getString("employee_name"),
                                    result.getString("employee_surname"), result.getString("employee_address"),
                                    result.getString("employee_phone"), result.getString("employee_email"),
                                    result.getString("employee_login_name"), result.getString("employee_login_password"),
                                    result.getString("employee_login_type"));
                            break;
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("An error occurred with Database connection");
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    System.out.println("An error occurred preparing the Query: " + selectSQL);
                    e.printStackTrace();
                }

                if(activeEmployee == null) {
                    errorText.setVisible(true);
                    new AnimationHandler().fadeIn(errorText, 500).execute();
                }
                else {
                    System.out.println("hola: " + activeEmployee.getName());
                }

                new AnimationHandler().fadeOut(loader, 500).execute(finishedEvent -> loader.setVisible(false));
            }).start();

            new MainMenu();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        frostImage.setClip(new Rectangle(frostView.getLayoutX(), frostView.getLayoutY(), frostView.getPrefWidth(), frostView.getPrefHeight()));
        frostView.setClip(new Rectangle(0, 0, frostView.getPrefWidth(), frostView.getPrefHeight()));

        setListeners();
    }
}
