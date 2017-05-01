package controllers.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controllers.BaseController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Employee;
import utils.AnimationHandler;
import utils.BCrypt;
import utils.DatabaseHandler;
import views.main_menu.MainMenu;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController extends BaseController implements Initializable {
    @FXML private JFXTextField username;
    @FXML private JFXPasswordField password;
    @FXML private JFXButton submit;
    @FXML private Label errorText;
    @FXML private Pane loader;
    @FXML private Pane frostView;
    @FXML private ImageView frostImage;

    @FXML
    private void submitLogin() {
        loader.setVisible(true);
        AnimationHandler.fadeIn(loader, 500, 0.6).execute();

        //DEBUG
        Employee activeEmployeeDEBUG = new Employee(1, "Cristian", "Molina", "", "", "", "", "", "TYPE_ADMIN", true);
        Platform.runLater(() -> new MainMenu((Stage) submit.getScene().getWindow(), activeEmployeeDEBUG));

        new Thread(() -> {
            Connection connection = DatabaseHandler.getConnection();
            String sqlQuery = "SELECT * FROM employees WHERE employee_login_name = ?";
            Employee activeEmployee = null;

            try {
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, username.getText());

                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    if (BCrypt.checkpw(password.getText(), result.getString("employee_login_password")) && result.getInt("employee_is_active") > 0) {


                        activeEmployee = new Employee(result.getInt("employee_id"), result.getString("employee_name"),
                                result.getString("employee_surname"), result.getString("employee_address"),
                                result.getString("employee_phone"), result.getString("employee_email"),
                                result.getString("employee_login_name"), result.getString("employee_login_password"),
                                result.getString("employee_login_type"), result.getInt("employee_is_active") > 0);
                        break;
                    }
                }

            } catch (NullPointerException e) {
                System.out.println("An error occurred with Database connection");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("An error occurred preparing the Query: " + sqlQuery);
                e.printStackTrace();
            }

            if (activeEmployee == null) {
                errorText.setVisible(true);
                AnimationHandler.fadeIn(errorText, 500).execute();
            } else {
                Employee finalActiveEmployee = activeEmployee;
                Platform.runLater(() -> new MainMenu((Stage) submit.getScene().getWindow(), finalActiveEmployee));
            }

            AnimationHandler.fadeOut(loader, 500).execute(finishedEvent -> loader.setVisible(false));
        }).start();
    }

    @FXML
    private void sendLogin() {
        if (!submit.isDisabled()) {
            submitLogin();
        }
    }

    private void setListeners() {
        //DEBUG
        //submit.disableProperty().bind((username.textProperty().isNotEmpty()).and(password.textProperty().isNotEmpty()).not());

        username.textProperty().addListener((observableValue, s, t1) -> {
            if (errorText.isVisible())
                AnimationHandler.fadeOut(errorText, 250).execute(actionEvent -> errorText.setVisible(false));
        });

        password.textProperty().addListener((observableValue, s, t1) -> {
            if (errorText.isVisible())
                AnimationHandler.fadeOut(errorText, 250).execute(actionEvent -> errorText.setVisible(false));
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        frostImage.setClip(new Rectangle(frostView.getLayoutX(), frostView.getLayoutY(), frostView.getPrefWidth(), frostView.getPrefHeight()));
        frostView.setClip(new Rectangle(0, 0, frostView.getPrefWidth(), frostView.getPrefHeight()));

        setListeners();
    }
}
