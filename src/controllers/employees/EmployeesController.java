package controllers.employees;

import com.jfoenix.controls.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import controllers.BaseController;
import controllers.database.DatabaseMethods;
import custom.MaterialCheckBoxCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import models.Employee;
import utils.DialogBuilder;
import utils.ImageUtils;

import javax.xml.ws.Action;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class EmployeesController extends BaseController {

    private static class Style {
        static final String LIGHT_GREY = "#CCCCCC";
        static final String BLACK = "#000000";
    }

    private enum ActionStatus {
        STATUS_NONE,
        STATUS_VIEWING,
        STATUS_ADDING,
        STATUS_EDITING,
        STATUS_SEARCHING
    }

    private ObservableList<Employee> employees;
    private FilteredList<Employee> filteredEmployees;
    private Employee selectedEmployee;
    private ActionStatus actualStatus;

    private final static String ACTIVE_USER_TEXT = "activo";
    private final static String INACTIVE_USER_TEXT = "inactivo";
    private final static String ADMIN_ROLE_TEXT = "Administrador";
    private final static String USER_ROLE_TEXT = "Usuario";
    private final static String GUESTS_ROLE_TEXT = "Invitado";

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

    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, Boolean> employeesTableCheckColumn;
    @FXML private TableColumn<Employee, Number> employeesTableIdColumn;
    @FXML private TableColumn<Employee, String> employeesTableNameColumn;
    @FXML private TableColumn<Employee, String> employeesTableSurnameColumn;
    @FXML private TableColumn<Employee, String> employeesTableAddressColumn;
    @FXML private TableColumn<Employee, String> employeesTablePhoneColumn;
    @FXML private TableColumn<Employee, String> employeesTableEmailColumn;

    @FXML private Pane removeButton;
    @FXML private JFXRippler removeButtonRippler;

    @FXML private Pane editButton;
    @FXML private JFXRippler editButtonRippler;

    @FXML private Pane formButtonsContainer;

    public EmployeesController(Employee loggedEmployee) {
        super(loggedEmployee);

        //Set selectedEmployee as null to avoid problems
        selectedEmployee = null;

        //Set actual status as none, there is no action in course
        actualStatus = ActionStatus.STATUS_NONE;
    }

    @Override
    protected void addListener() {
        if(actualStatus != ActionStatus.STATUS_NONE && actualStatus != ActionStatus.STATUS_VIEWING)
            return;

        actualStatus = ActionStatus.STATUS_ADDING;

        showInformationLabels(false);
        showModificationInputs(true);
        setModificationInputsText(new Employee(DatabaseMethods.getLastId("employees", "employee_id")));
    }

    @Override
    protected void editListener() {
        if(actualStatus != ActionStatus.STATUS_VIEWING)
            return;

        actualStatus = ActionStatus.STATUS_EDITING;

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedEmployee);
    }

    @Override
    protected void removeListener() {
        if(actualStatus != ActionStatus.STATUS_VIEWING)
            return;

        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "employee-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar al usuario " + selectedEmployee.getName() + "?"))
                .setOverlayClose(false)
                .setAcceptButton(actionEvent -> {
                    DatabaseMethods.removeEmployees(selectedEmployee);
                    employees.remove(selectedEmployee);
                    dialogBuilder.getDialog().close();
                })
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .build();

        dialog.show();
    }

    @Override
    protected void acceptChanges() {

    }

    @Override
    protected void cancelChanges() {
        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "employee-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres cancelar la edición?"))
                .setOverlayClose(true)
                .setAcceptButton(actionEvent -> {
                    if(actualStatus == ActionStatus.STATUS_EDITING) {
                        setEmployeeInformation(selectedEmployee);
                        actualStatus = ActionStatus.STATUS_VIEWING;
                    } else if(actualStatus == ActionStatus.STATUS_ADDING) {
                        setInformationLabelsPlaceholder();
                        actualStatus = ActionStatus.STATUS_NONE;
                    }

                    showInformationLabels(true);
                    showModificationInputs(false);
                    dialogBuilder.getDialog().close();
                })
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .build();

        dialog.show();
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

        formButtonsContainer.setVisible(show);
    }

    @Override
    protected void setInformationLabelsPlaceholder() {
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

        setEmployeesLabelsStyle("-fx-text-fill: " + Style.LIGHT_GREY);
    }

    private void setModificationInputsText(Employee employee) {
        employeeNameInput.setText(employee.getName());
        employeeSurnameInput.setText(employee.getSurname());
        employeeEmailInput.setText(employee.getEmail());
        employeeAddressInput.setText(employee.getAddress());
        employeePhoneInput.setText(employee.getPhone());
        employeeLoginNameInput.setText(employee.getLoginName());

        switch (employee.getLoginType()) {
            case "TYPE_ADMIN":
                employeeLoginTypeInput.getSelectionModel().select(0);
                break;

            case "TYPE_USER":
                employeeLoginTypeInput.getSelectionModel().select(1);
                break;

            default:
                employeeLoginTypeInput.getSelectionModel().select(2);
        }

        employeeStatusInput.setSelected(employee.isLoginActive());
    }

    private void setEmployeeInformation(Employee employee) {
        setEmployeesLabelsStyle("-fx-text-fill: " + Style.BLACK);

        //Set label text with employee information
        employeeNameLabel.setText(employee.getCompleteName());
        employeeEmailLabel.setText(employee.getEmail());
        employeeAddressLabel.setText(employee.getAddress());
        employeePhoneLabel.setText(employee.getPhone());
        employeeLoginNameLabel.setText(employee.getLoginName());

        switch (employee.getLoginType()) {
            case "TYPE_ADMIN":
                employeeLoginTypeLabel.setText(ADMIN_ROLE_TEXT);
                break;

            case "TYPE_USER":
                employeeLoginTypeLabel.setText(USER_ROLE_TEXT);

            default:
                employeeLoginTypeLabel.setText(GUESTS_ROLE_TEXT);
        }

        employeeStatusLabel.setText(employee.isLoginActive() ? ACTIVE_USER_TEXT : INACTIVE_USER_TEXT);
    }

    private void setEmployeesLabelsStyle(String style) {
        employeeNameLabel.setStyle(style);
        employeeEmailLabel.setStyle(style);
        employeeAddressLabel.setStyle(style);
        employeePhoneLabel.setStyle(style);
        employeeLoginNameLabel.setStyle(style);
        employeeLoginTypeLabel.setStyle(style);
        employeeStatusLabel.setStyle(style);
    }

    private void searchListener() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredEmployees.setPredicate(employee -> {
            actualStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for employee name
            if (employee.getName().toLowerCase().contains(lowerCaseValue))
                return true;
                //Checks for employee surname
            else if (employee.getSurname().toLowerCase().contains(lowerCaseValue))
                return true;
                //Checks for employee phone
            else if (employee.getPhone().toLowerCase().contains((lowerCaseValue)))
                return true;
                //Checks for employee email
            else if (employee.getEmail().toLowerCase().contains(lowerCaseValue))
                return true;

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Employee> sortedData = new SortedList<Employee>(filteredEmployees);
        sortedData.comparatorProperty().bind(employeesTable.comparatorProperty());

        //Add sortedItems to the TableView
        employeesTable.setItems(sortedData);
    }

    private void createTable() {
        employeesTableIdColumn.setCellValueFactory(param -> param.getValue().idProperty());
        employeesTableNameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        employeesTableSurnameColumn.setCellValueFactory(param -> param.getValue().surnameProperty());
        employeesTableAddressColumn.setCellValueFactory(param -> param.getValue().addressProperty());
        employeesTablePhoneColumn.setCellValueFactory(param -> param.getValue().phoneProperty());
        employeesTableEmailColumn.setCellValueFactory(param -> param.getValue().emailProperty());

        employeesTableCheckColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        employeesTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());

        employeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(actualStatus == ActionStatus.STATUS_NONE || actualStatus == ActionStatus.STATUS_VIEWING) {
                selectedEmployee = newValue;
                actualStatus = ActionStatus.STATUS_VIEWING;

                //Check if selected employee exists (maybe it doesn't because of search)
                if (newValue != null)
                    setEmployeeInformation(newValue);
                else {
                    //Default employee placeholder
                    setInformationLabelsPlaceholder();
                }
            }
        });

        employeesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue) {
                for(Employee e : employees)
                    e.setChecked(true);
            }
            else {
                for(Employee e : employees)
                    e.setChecked(false);
            }
        });
        employeesTableCheckColumn.setGraphic(selectAllCheckbox);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Apply crop and round effects to employee image
        ImageUtils.cropImage(employeeImage, 120, 120);
        ImageUtils.roundImage(employeeImage, 60);

        //Default employee placeholder
        setInformationLabelsPlaceholder();

        //Create table. Adds checkboxes, selection mode, and listener when select row
        createTable();

        //Set ripple effect for edit and remove button
        editButtonRippler.setControl(editButton);
        removeButtonRippler.setControl(removeButton);

        //Binds managed property to visible so when visible is true, it becomes managed as well
        formButtonsContainer.managedProperty().bind(formButtonsContainer.visibleProperty());

        //Fill employee role comboBox
        employeeLoginTypeInput.getItems().add(new Label(ADMIN_ROLE_TEXT));
        employeeLoginTypeInput.getItems().add(new Label(USER_ROLE_TEXT));
        employeeLoginTypeInput.getItems().add(new Label(GUESTS_ROLE_TEXT));

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        employees = FXCollections.observableList(DatabaseMethods.getAllEmployees());
                        filteredEmployees = new FilteredList<>(employees, p -> true);

                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
/*                                                    NOTE
 *                          ============================================================
 *                          Run here all methods that use employees or filteredEmployees
 */
                                searchListener();
                            } finally {
                                latch.countDown();
                            }
                        });
                        latch.await();

                        return null;
                    }
                };
            }
        };
        service.start();
    }
}
