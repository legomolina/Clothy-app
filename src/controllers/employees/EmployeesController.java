package controllers.employees;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import controllers.BaseController;
import custom.MaterialCheckBoxCell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
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

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Boolean> employeeTableCheckColumn;
    @FXML private TableColumn<Employee, Number> employeeTableIdColumn;
    @FXML private TableColumn<Employee, String> employeeTableNameColumn;
    @FXML private TableColumn<Employee, String> employeeTableSurnameColumn;
    @FXML private TableColumn<Employee, String> employeeTableAddressColumn;
    @FXML private TableColumn<Employee, String> employeeTablePhoneColumn;
    @FXML private TableColumn<Employee, String> employeeTableEmailColumn;

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
    protected void setInformationLabelsPlaceholder() {
        final String placeHolderStyle = "-fx-text-fill: " + Style.LIGHT_GREY;

        //Set label default name
        employeeNameLabel.setText("Nombre");
        employeeEmailLabel.setText("email@email.com");
        employeeAddressLabel.setText("Dirección");
        employeePhoneLabel.setText("+00 000000000");
        employeeLoginNameLabel.setText("Nombre de usuario");
        employeeLoginTypeLabel.setText("Rol del empleado");
        employeeStatusLabel.setText("estado");

        //Set employee default image
        employeeImage.setImage(new Image("/resources/images/employees_image/default.png"));
        ImageUtils.cropImage(employeeImage, 120, 120);
        ImageUtils.roundImage(employeeImage, 60);

        //Set light color for text
        employeeNameLabel.setStyle(placeHolderStyle);
        employeeEmailLabel.setStyle(placeHolderStyle);
        employeeAddressLabel.setStyle(placeHolderStyle);
        employeePhoneLabel.setStyle(placeHolderStyle);
        employeeLoginNameLabel.setStyle(placeHolderStyle);
        employeeLoginTypeLabel.setStyle(placeHolderStyle);
        employeeStatusLabel.setStyle(placeHolderStyle);
    }

    private void fillTable() {
        employeeTableIdColumn.setCellValueFactory(param -> param.getValue().idProperty());
        employeeTableNameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        employeeTableSurnameColumn.setCellValueFactory(param -> param.getValue().surnameProperty());
        employeeTableAddressColumn.setCellValueFactory(param -> param.getValue().addressProperty());
        employeeTablePhoneColumn.setCellValueFactory(param -> param.getValue().phoneProperty());
        employeeTableEmailColumn.setCellValueFactory(param -> param.getValue().emailProperty());

        employeeTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());
        employeeTableCheckColumn.setCellValueFactory(param -> param.getValue().checkedProperty());

        /*infoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            selectedEmployee = newValue;

            //Check if selected employee exists (maybe it doesn't because of search)
            if (newValue != null)
                fillEmployeeInformation(selectedEmployee);
            else {
                //Default employee placeholder
                setEmployeeLabelPlaceholder(true);
            }
        });*/
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Apply crop and round effects to employee image
        ImageUtils.cropImage(employeeImage, 120, 120);
        ImageUtils.roundImage(employeeImage, 60);

    }
}
