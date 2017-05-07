package controllers.employees;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import models.Employee;
import utils.ImageUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeesController extends BaseController {

    private static class Style {
        public static final String LIGHT_GREY = "#CCCCCC";
        public static final String BLACK = "#222222";
    }

    @FXML private ImageView employeeImage;
    @FXML private Label employeeNameLabel;
    @FXML private Label employeeEmailLabel;
    @FXML private Label employeeAddressLabel;
    @FXML private Label employeePhoneLabel;
    @FXML private Label employeeLoginNameLabel;
    @FXML private Label employeeLoginTypeLabel;
    @FXML private Label employeeStatusLabel;

    @FXML private JFXTextField employeeNameInput;
    @FXML private JFXTextField employeeSurnameInput;
    @FXML private JFXTextField employeeEmailInput;
    @FXML private JFXTextArea employeeAddressInput;
    @FXML private JFXTextField employeePhoneInput;
    @FXML private JFXTextField employeeLoginNameInput;
    @FXML private JFXComboBox<Label> employeeLoginTypeInput;
    @FXML private JFXToggleButton employeeStatusInput;

    public EmployeesController(Employee loggedEmployee) {
        super(loggedEmployee);
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
    protected void acceptChanges() {

    }

    @Override
    protected void cancelChanges() {

    }

    @Override
    protected void showInformationLabels(boolean show) {
        employeeNameLabel.setVisible(show);
        employeeEmailLabel.setVisible(show);
        employeeAddressLabel.setVisible(show);
        employeePhoneLabel.setVisible(show);
        employeeLoginNameLabel.setVisible(show);
        employeeLoginTypeLabel.setVisible(show);
        employeeStatusLabel.setVisible(show);
    }

    @Override
    protected void showModificationInputs(boolean show) {
        employeeNameInput.setVisible(show);
        employeeSurnameInput.setVisible(show);
        employeeEmailInput.setVisible(show);
        employeeAddressInput.setVisible(show);
        employeePhoneInput.setVisible(show);
        employeeLoginNameInput.setVisible(show);
        employeeLoginTypeInput.setVisible(show);
        employeeStatusInput.setVisible(show);

        //Workaround for surname input. When visible, it stays at the bottom of name input
        employeeSurnameInput.setLayoutX(51);
        employeeSurnameInput.setLayoutY(show ? 62 : 1);
    }

    @Override
    protected void setInformationLabelsPlaceholder(boolean set) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Apply crop and round effects to employee image
        ImageUtils.cropImage(employeeImage, 120, 120);
        ImageUtils.roundImage(employeeImage, 60);

    }
}
