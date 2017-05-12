package controllers.main_menu;


import com.jfoenix.controls.JFXMasonryPane;
import com.sun.javafx.stage.StageHelper;
import controllers.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Employee;
import views.BaseStage;
import views.clients.Clients;
import views.employees.Employees;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController extends BaseController {

    @FXML private Label welcomeLabel;
    @FXML private Pane titleContainer;
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
            new Employees((Stage) menuButtons.getScene().getWindow(), loggedEmployee);
    }

    @FXML
    private void openClientsManagement() {
        boolean opened = false;

        for (Stage s : StageHelper.getStages()) {
            if (((BaseStage) s).getStageIdentification().equals("clients")) {
                opened = true;
                break;
            }
        }

        if (!opened)
            new Clients((Stage) menuButtons.getScene().getWindow(), loggedEmployee);
    }

    @Override
    protected void addListener() {
    }

    @Override
    protected void editListener() {
    }

    @Override
    protected void removeListener() {
    }

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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Bienvenido " + loggedEmployee.getName());
        menuButtons.setLayoutMode(JFXMasonryPane.LayoutMode.MASONRY);
        setListeners();
    }
}
