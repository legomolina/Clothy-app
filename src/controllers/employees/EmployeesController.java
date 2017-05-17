package controllers.employees;

import com.jfoenix.controls.*;
import controllers.BaseController;
import controllers.database.DatabaseMethods;
import controllers.database.EmployeesMethods;
import custom.CustomRequiredFieldValidator;
import custom.EmailFieldValidator;
import custom.MaterialCheckBoxCell;
import custom.PhoneFieldValidator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Employee;
import utils.AnimationHandler;
import utils.DialogBuilder;
import utils.ImageUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class EmployeesController extends BaseController {
    private ObservableList<Employee> employees;
    private FilteredList<Employee> filteredEmployees;
    private Employee selectedEmployee;

    private int selectedEmployeesCount;

    private ChangeListener<Boolean> selectedEmployeeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            selectedEmployeesCount += (newValue) ? 1 : -1;
            deleteSelected.setVisible(selectedEmployeesCount > 0);
        }
    };

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

    @FXML private JFXButton acceptChanges;

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
    @FXML private AnchorPane loaderContainer;

    @FXML private JFXButton deleteSelected;

    public EmployeesController(Employee loggedEmployee, Stage currentStage) {
        super(loggedEmployee, currentStage);

        //Set selectedEmployee as null to avoid problems
        selectedEmployee = null;

        //There's no selected employee
        selectedEmployeesCount = 0;
    }

    @Override
    protected void addListener() {
        //If there is no edition or addition in course
        if (currentStatus != ActionStatus.STATUS_NONE && currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        currentStatus = ActionStatus.STATUS_ADDING;
        selectedEmployee = new Employee(DatabaseMethods.getLastId("employees", "employee_id") + 1);

        selectedEmployee.checkedProperty().addListener(selectedEmployeeListener);

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedEmployee);
    }

    @Override
    protected void editListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        currentStatus = ActionStatus.STATUS_EDITING;

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedEmployee);
    }

    //TODO Watch for logged employee. Prevent removing itself
    @Override
    protected void removeListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar al usuario " + selectedEmployee.getName() + "?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    EmployeesMethods.removeEmployees(selectedEmployee);
                    selectedEmployee.setChecked(false);
                    employees.remove(selectedEmployee);
                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();
    }

    @FXML
    private void removeSelectedListener() {
        if (currentStatus != ActionStatus.STATUS_NONE && currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar a los usuarios seleccionados?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    EmployeesMethods.removeEmployees(employeesTable.getSelectionModel().getSelectedItems());
                    employees.removeAll(employeesTable.getSelectionModel().getSelectedItems());

                    deleteSelected.setVisible(false);

                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();
    }

    @Override
    protected void acceptChanges(ActionEvent event) {
        //Validate inputs before sending data
        if (!employeeNameInput.validate()) return;
        if (!employeeSurnameInput.validate()) return;
        if (!employeeEmailInput.validate()) return;
        if (!employeePhoneInput.validate()) return;
        if (!employeeAddressInput.validate()) return;
        if (!employeeLoginNameInput.validate()) return;

        loaderContainer.setVisible(true);
        AnimationHandler.fadeIn(loaderContainer, 500, 0.6).execute();

        selectedEmployee.setName(employeeNameInput.getText());
        selectedEmployee.setSurname(employeeSurnameInput.getText());
        selectedEmployee.setEmail(employeeEmailInput.getText());
        selectedEmployee.setPhone(employeePhoneInput.getText());
        selectedEmployee.setAddress(employeeAddressInput.getText());
        selectedEmployee.setLoginName(employeeLoginNameInput.getText());

        switch (employeeLoginTypeInput.getSelectionModel().getSelectedItem().getText()) {
            case ADMIN_ROLE_TEXT:
                selectedEmployee.setLoginType("TYPE_ADMIN");
                break;

            case USER_ROLE_TEXT:
                selectedEmployee.setLoginType("TYPE_USER");
                break;

            default:
                selectedEmployee.setLoginType("TYPE_GUEST");
        }

        selectedEmployee.setLoginActive(employeeStatusInput.isSelected());

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CountDownLatch latch = new CountDownLatch(1);

                        if (currentStatus == ActionStatus.STATUS_ADDING) {
                            EmployeesMethods.addEmployees(selectedEmployee);

                            employees.add(selectedEmployee);
                        } else if (currentStatus == ActionStatus.STATUS_EDITING) {
                            EmployeesMethods.updateEmployees(selectedEmployee);

                            employees.set(employees.indexOf(selectedEmployee), selectedEmployee);
                        }

                        Platform.runLater(() -> {
                            try {
                                currentStatus = ActionStatus.STATUS_VIEWING;

                                showInformationLabels(true);
                                showModificationInputs(false);

                                setEmployeeInformation(selectedEmployee);

                                AnimationHandler.fadeOut(loaderContainer, 500).execute(finishedEvent -> loaderContainer.setVisible(false));
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

    @Override
    protected void cancelChanges() {
        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres cancelar la edición?"))
                .setOverlayClose(true)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    if (currentStatus == ActionStatus.STATUS_EDITING) {
                        setEmployeeInformation(selectedEmployee);
                        currentStatus = ActionStatus.STATUS_VIEWING;
                    } else if (currentStatus == ActionStatus.STATUS_ADDING) {
                        setInformationLabelsPlaceholder();
                        currentStatus = ActionStatus.STATUS_NONE;
                    }

                    showInformationLabels(true);
                    showModificationInputs(false);
                    dialogBuilder.getDialog().close();
                })
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
        employeeSurnameInput.setLayoutY(show ? 70 : 1);

        setEmployeeImage("default");
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
        employeeStatusLabel.setStyle(employee.isLoginActive() ? ("-fx-text-fill: " + Style.GREEN) : ("-fx-text-fill: " + Style.RED));

        setEmployeeImage(employee.getLoginName());
    }

    private void setEmployeeImage(String employeeUsername) {
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

                        if (files != null) {
                            //If user has custom image, set it
                            for (File file : files) {
                                int pos = file.getName().lastIndexOf(".");

                                if (file.getName().toLowerCase().substring(0, pos).equals(employeeUsername.toLowerCase())) {
                                    fileName = "/resources/images/employees_image/" + file.getName();
                                    break;
                                }
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
            currentStatus = ActionStatus.STATUS_NONE;

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
        SortedList<Employee> sortedData = new SortedList<>(filteredEmployees);
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
            if (currentStatus == ActionStatus.STATUS_NONE || currentStatus == ActionStatus.STATUS_VIEWING) {
                selectedEmployee = newValue;
                currentStatus = ActionStatus.STATUS_VIEWING;

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
        employeesTable.setPlaceholder(new Label("No hay empleados registrados"));

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            for (Employee e : employees)
                e.setChecked(newValue);
        });
        employeesTableCheckColumn.setGraphic(selectAllCheckbox);
    }

    private void setInputValidator() {
        employeeNameInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        employeeSurnameInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        employeeEmailInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        employeeEmailInput.getValidators().add(new EmailFieldValidator("El email no corresponde a un email válido"));
        employeeAddressInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        employeePhoneInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        employeePhoneInput.getValidators().add(new PhoneFieldValidator("El campo no es un teléfono válido"));
        employeeLoginNameInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        //Apply crop and round effects to employee image
        ImageUtils.cropImage(employeeImage, 120, 120);
        ImageUtils.roundImage(employeeImage, 60);

        //Fill employee role comboBox
        employeeLoginTypeInput.getItems().add(new Label(ADMIN_ROLE_TEXT));
        employeeLoginTypeInput.getItems().add(new Label(USER_ROLE_TEXT));
        employeeLoginTypeInput.getItems().add(new Label(GUESTS_ROLE_TEXT));

        setInputValidator();

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        employees = FXCollections.observableList(EmployeesMethods.getAllEmployees());
                        filteredEmployees = new FilteredList<>(employees, p -> true);

                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
/*                                                    NOTE
 *                          ============================================================
 *                          Run here all methods that use employees or filteredEmployees
 */
                                for (Employee e : employees) {
                                    e.checkedProperty().addListener(selectedEmployeeListener);
                                }

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