package controllers.employees;


import com.jfoenix.controls.*;
import controllers.BaseController;
import controllers.database.DatabaseMethods;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import models.Employee;
import utils.ImageUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class EmployeesController extends BaseController implements Initializable {

    private ObservableList<Employee> employees;
    private FilteredList<Employee> filteredEmployees;
    private boolean editingEmployee = false;

    private final static String ACTIVE_USER_TEXT = "activo";
    private final static String INACTIVE_USER_TEXT = "inactivo";
    private final static String ADMIN_ROLE_TEXT = "Administrador";
    private final static String USER_ROLE_TEXT = "Usuario";
    private final static String GUESTS_ROLE_TEXT = "Invitado";

    @FXML private ImageView employeeImage;
    @FXML private Label employeeName;
    @FXML private Label employeeEmail;
    @FXML private Label employeeAddress;
    @FXML private Label employeePhone;
    @FXML private Label employeeLoginName;
    @FXML private Label employeeLoginType;
    @FXML private Label employeeStatus;

    @FXML private JFXTextField employeeNameInput;
    @FXML private JFXTextField employeeSurnameInput;
    @FXML private JFXTextField employeeEmailInput;
    @FXML private JFXTextArea employeeAddressInput;
    @FXML private JFXTextField employeePhoneInput;
    @FXML private JFXTextField employeeLoginNameInput;
    @FXML private JFXComboBox<Label> employeeLoginTypeInput;
    @FXML private JFXToggleButton employeeStatusInput;

    @FXML private JFXRippler editButtonRipple;
    @FXML private Pane editButtonImage;

    @FXML private TableView<Employee> infoTable;
    @FXML private TableColumn<Employee, Number> infoTableId;
    @FXML private TableColumn<Employee, String> infoTableName;
    @FXML private TableColumn<Employee, String> infoTableSurname;
    @FXML private TableColumn<Employee, String> infoTableAddress;
    @FXML private TableColumn<Employee, String> infoTableEmail;
    @FXML private TableColumn<Employee, String> infoTablePhone;

    @FXML private JFXTextField searchInput;

    @FXML
    private void clearSearch() {
        searchInput.setText("");
    }

    @FXML
    private void editEmployee() {
        if (isEditMode())
            setEditMode(false, infoTable.getSelectionModel().getSelectedItem());
        else
            setEditMode(true, infoTable.getSelectionModel().getSelectedItem());
    }

    private void setEditMode(boolean set, Employee actualEmployee) {
        if (set) {
            employeeNameInput.setText(actualEmployee.getName().getValue());
            employeeSurnameInput.setText(actualEmployee.getSurname().getValue());
            employeeEmailInput.setText(actualEmployee.getEmail().getValue());
            employeeAddressInput.setText(actualEmployee.getAddress().getValue());
            employeePhoneInput.setText(actualEmployee.getPhone().getValue());
            employeeLoginNameInput.setText(actualEmployee.getLoginName().getValue());

            switch (actualEmployee.getLoginType().getValue()) {
                case "TYPE_ADMIN":
                    employeeLoginTypeInput.getSelectionModel().select(0);
                    break;

                case "TYPE_USER":
                    employeeLoginTypeInput.getSelectionModel().select(1);
                    break;

                default:
                    employeeLoginTypeInput.getSelectionModel().select(2);
            }

            employeeStatusInput.setSelected(actualEmployee.getLoginActive().getValue());

            employeeSurnameInput.setLayoutX(51);
            employeeSurnameInput.setLayoutY(62);

            employeeNameInput.setVisible(true);
            employeeSurnameInput.setVisible(true);
            employeeEmailInput.setVisible(true);
            employeeAddressInput.setVisible(true);
            employeePhoneInput.setVisible(true);
            employeeLoginNameInput.setVisible(true);
            employeeLoginTypeInput.setVisible(true);
            employeeStatusInput.setVisible(true);

            employeeName.setVisible(false);
            employeeEmail.setVisible(false);
            employeeAddress.setVisible(false);
            employeePhone.setVisible(false);
            employeeLoginName.setVisible(false);
            employeeLoginType.setVisible(false);
            employeeStatus.setVisible(false);

            editingEmployee = true;
        } else {
            actualEmployee.setName(employeeNameInput.getText());
            actualEmployee.setSurname(employeeSurnameInput.getText());
            actualEmployee.setEmail(employeeEmailInput.getText());
            actualEmployee.setAddress(employeeAddressInput.getText());
            actualEmployee.setPhone(employeePhoneInput.getText());
            actualEmployee.setLoginName(employeeLoginNameInput.getText());

            switch (employeeLoginTypeInput.getSelectionModel().getSelectedItem().getText()) {
                case ADMIN_ROLE_TEXT:
                    actualEmployee.setLoginType("TYPE_ADMIN");
                    break;

                case USER_ROLE_TEXT:
                    actualEmployee.setLoginType("TYPE_USER");
                    break;

                default:
                    actualEmployee.setLoginType("TYPE_GUEST");
            }

            actualEmployee.setLoginActive(employeeStatusInput.isSelected());

            employeeName.setText(actualEmployee.getName().getValue() + " " + actualEmployee.getSurname().getValue());
            employeeEmail.setText(actualEmployee.getEmail().getValue());
            employeeAddress.setText(actualEmployee.getAddress().getValue());
            employeePhone.setText(actualEmployee.getPhone().getValue());
            employeeLoginName.setText(actualEmployee.getLoginName().getValue());

            switch (actualEmployee.getLoginType().getValue()) {
                case "TYPE_ADMIN":
                    employeeStatus.setText(ADMIN_ROLE_TEXT);
                    break;

                case "TYPE_USER":
                    employeeStatus.setText(USER_ROLE_TEXT);
                    break;

                default:
                    employeeStatus.setText(GUESTS_ROLE_TEXT);
            }

            employeeStatus.setText(actualEmployee.getLoginActive().getValue() ? ACTIVE_USER_TEXT : INACTIVE_USER_TEXT);
            employeeStatus.setTextFill(actualEmployee.getLoginActive().getValue() ? Paint.valueOf("#35ba48") : Paint.valueOf("#ff5440"));

            employeeNameInput.setVisible(false);
            employeeSurnameInput.setVisible(false);
            employeeEmailInput.setVisible(false);
            employeeAddressInput.setVisible(false);
            employeePhoneInput.setVisible(false);
            employeeLoginNameInput.setVisible(false);
            employeeLoginTypeInput.setVisible(false);
            employeeStatusInput.setVisible(false);

            employeeSurnameInput.setLayoutX(51);
            employeeSurnameInput.setLayoutY(1);

            employeeName.setVisible(true);
            employeeEmail.setVisible(true);
            employeeAddress.setVisible(true);
            employeePhone.setVisible(true);
            employeeLoginName.setVisible(true);
            employeeLoginType.setVisible(true);
            employeeStatus.setVisible(true);

            employees.set(employees.indexOf(actualEmployee), actualEmployee);

            editingEmployee = false;
        }
    }

    private boolean isEditMode() {
        return editingEmployee;
    }

    private void setEmployeeLabelPlaceholder(boolean set) {
        String style = "";

        if (set) {
            style = "-fx-text-fill: #CCCCCC;";

            employeeName.setText("Nombre");
            employeeEmail.setText("email@email.com");
            employeeAddress.setText("Dirección");
            employeePhone.setText("+00 000000000");
            employeeLoginName.setText("Nombre de usuario");
            employeeLoginType.setText("Rol del empleado");
            employeeStatus.setText("estado");

            employeeImage.setImage(new Image("/resources/images/employees_image/default.png"));
            ImageUtils.cropImage(employeeImage, 120, 120);
            ImageUtils.roundImage(employeeImage, 60);
        } else
            style = "-fx-text-fill: #000000;";

        employeeName.setStyle(style);
        employeeEmail.setStyle(style);
        employeeAddress.setStyle(style);
        employeePhone.setStyle(style);
        employeeLoginName.setStyle(style);
        employeeLoginType.setStyle(style);
        employeeStatus.setStyle(style);
    }

    private void fillEmployeeInformation(Employee employee) {
        setEmployeeLabelPlaceholder(false);

        employeeName.setText(employee.getName().getValue() + " " + employee.getSurname().getValue());
        employeeEmail.setText(employee.getEmail().getValue());
        employeeAddress.setText(employee.getAddress().getValue());
        employeePhone.setText(employee.getPhone().getValue());
        employeeLoginName.setText(employee.getLoginName().getValue());

        //TODO see .jar
        //Load user image from /resources/images/employees_image asynchronously
        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Store all files (user images) from employee images directory
                        File imagesFolder = new File(getClass().getResource("/resources/images/employees_image").toURI());
                        File[] files = imagesFolder.listFiles();
                        String fileName = "/resources/images/employees_image/default.png";

                        CountDownLatch latch = new CountDownLatch(1);

                        //If user has custom image, set it
                        for (int i = 0; i < files.length; i++) {
                            int pos = files[i].getName().lastIndexOf(".");

                            if (files[i].getName().toLowerCase().substring(0, pos).equals(employee.getLoginName().getValue().toLowerCase())) {
                                fileName = "/resources/images/employees_image/" + files[i].getName();
                                break;
                            }
                        }

                        String finalFileName = fileName;
                        Platform.runLater(() -> {
                            try {
                                employeeImage.setImage(new Image(finalFileName));
                                ImageUtils.cropImage(employeeImage, 120, 120);
                                ImageUtils.roundImage(employeeImage, 60);
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

        switch (employee.getLoginType().getValue()) {
            case "TYPE_ADMIN":
                employeeLoginType.setText(ADMIN_ROLE_TEXT);
                break;

            case "TYPE_USER":
                employeeLoginType.setText(USER_ROLE_TEXT);
                break;

            default:
                employeeLoginType.setText(GUESTS_ROLE_TEXT);
        }

        if (employee.getLoginActive().getValue()) {
            employeeStatus.setText(ACTIVE_USER_TEXT);
            employeeStatus.setStyle("-fx-text-fill: #35ba48;");
        } else {
            employeeStatus.setText(INACTIVE_USER_TEXT);
            employeeStatus.setStyle("-fx-text-fill: #ff5440;");
        }
    }

    private void fillTable() {
        infoTableId.setCellValueFactory(param -> param.getValue().getId());
        infoTableName.setCellValueFactory(param -> param.getValue().getName());
        infoTableSurname.setCellValueFactory(param -> param.getValue().getSurname());
        infoTableAddress.setCellValueFactory(param -> param.getValue().getAddress());
        infoTablePhone.setCellValueFactory(param -> param.getValue().getPhone());
        infoTableEmail.setCellValueFactory(param -> param.getValue().getEmail());

        infoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            //Check if selected employee exists (maybe it doesn't because of search)
            if (newValue != null)
                fillEmployeeInformation(newValue);
            else {
                //Default employee placeholder
                setEmployeeLabelPlaceholder(true);
            }
        });
    }

    private void setListeners() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredEmployees.setPredicate(employee -> {
                //If text field is not empty
                if (newValue == null || newValue.isEmpty())
                    return true;

                String lowerCaseValue = newValue.toLowerCase();

                //Checks for employee name
                if (employee.getName().getValue().toLowerCase().contains(lowerCaseValue))
                    return true;
                    //Checks for employee surname
                else if (employee.getSurname().getValue().toLowerCase().contains(lowerCaseValue))
                    return true;
                    //Checks for employee phone
                else if (employee.getPhone().getValue().toLowerCase().contains((lowerCaseValue)))
                    return true;
                    //Checks for employee email
                else if (employee.getEmail().getValue().toLowerCase().contains(lowerCaseValue))
                    return true;

                //If there are not coincidences
                return false;
            });
        });

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Employee> sortedData = new SortedList<>(filteredEmployees);
        sortedData.comparatorProperty().bind(infoTable.comparatorProperty());

        //Add sortedItems to the TableView
        infoTable.setItems(sortedData);

        editButtonImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            editEmployee();
            event.consume();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ImageUtils.cropImage(employeeImage, 120, 120);
        ImageUtils.roundImage(employeeImage, 60);

        //Default employee placeholder
        setEmployeeLabelPlaceholder(true);

        //Set ripple effect for edit button
        editButtonRipple.setControl(editButtonImage);

        //Adds employee role into ComboBox
        employeeLoginTypeInput.getItems().add(new Label(ADMIN_ROLE_TEXT));
        employeeLoginTypeInput.getItems().add(new Label(USER_ROLE_TEXT));
        employeeLoginTypeInput.getItems().add(new Label(GUESTS_ROLE_TEXT));

        //employeeSurnameInput.managedProperty().bind(employeeSurnameInput.visibleProperty());

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
                                /*
                                 *                             NOTE
                                 * ============================================================
                                 * Run here all methods that use employees or filteredEmployees
                                 */
                                fillTable();
                                setListeners();
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
