package controllers.main_menu;


import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXRippler;
import com.sun.javafx.stage.StageHelper;
import controllers.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import models.Employee;
import views.BaseStage;
import views.employees.Employees;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController extends BaseController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Pane titleContainer;
    @FXML private Pane employeesButton;
    @FXML private Pane articlesButton;
    @FXML private Pane clientsButton;
    @FXML private Pane salesButton;
    @FXML private Pane ordersButton;
    @FXML private JFXMasonryPane menuButtons;

    @FXML
    private void openEmployeesManagement() {
        boolean opened = false;

        for (Stage s : StageHelper.getStages()) {
            if (((BaseStage) s).getStageIdentification().equals("employees")) {
                opened = true;
                break;
            }
        }

        if (!opened)
            new Employees((Stage) employeesButton.getScene().getWindow(), loggedEmployee);
    }

    @Override
    protected void addListener() {}

    @Override
    protected void editListener() {}

    @Override
    protected void removeListener() {}

    @Override
    protected void acceptChanges(ActionEvent event) {

    }

    @Override
    protected void cancelChanges() {

    }

    public MainMenuController(Employee loggedEmployee, Stage currentStage) {
        super(loggedEmployee, currentStage);
    }

    @Override
    protected void showInformationLabels(boolean show) {

    }

    @Override
    protected void showModificationInputs(boolean show) {

    }

    @Override
    protected void setInformationLabelsPlaceholder() {

    }

    private void setListeners() {
        titleContainer.setEffect(new BoxBlur(5, 5, 3));

        JFXRippler employeesRipple = new JFXRippler(employeesButton);
        employeesRipple.setCursor(Cursor.HAND);
        employeesRipple.setRipplerFill(Paint.valueOf("#ffd351"));
        menuButtons.getChildren().add(employeesRipple);

        JFXRippler articlesRipple = new JFXRippler(articlesButton);
        articlesRipple.setCursor(Cursor.HAND);
        articlesRipple.setRipplerFill(Paint.valueOf("#e24848"));
        menuButtons.getChildren().add(articlesRipple);

        JFXRippler clientsRipple = new JFXRippler(clientsButton);
        clientsRipple.setCursor(Cursor.HAND);
        clientsRipple.setRipplerFill(Paint.valueOf("#00c8ff"));
        menuButtons.getChildren().add(clientsRipple);

        JFXRippler salesRipple = new JFXRippler(salesButton);
        salesRipple.setCursor(Cursor.HAND);
        salesRipple.setRipplerFill(Paint.valueOf("#7856ff"));
        menuButtons.getChildren().add(salesRipple);

        JFXRippler ordersRipple = new JFXRippler(ordersButton);
        ordersRipple.setCursor(Cursor.HAND);
        ordersRipple.setRipplerFill(Paint.valueOf("#76e285"));
        menuButtons.getChildren().add(ordersRipple);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Bienvenido " + loggedEmployee.getName());
        setListeners();
    }
}
