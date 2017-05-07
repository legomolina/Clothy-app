//package controllers.employees;
//
//
//import com.jfoenix.controls.*;
//import controllers.BaseController;
//import controllers.database.DatabaseMethods;
//import custom.MaterialCheckBoxCell;
//import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.collections.transformation.FilteredList;
//import javafx.collections.transformation.SortedList;
//import javafx.concurrent.Service;
//import javafx.concurrent.Task;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.Pane;
//import javafx.scene.text.Text;
//import models.Employee;
//import utils.DialogBuilder;
//import utils.ImageUtils;
//
//import java.io.File;
//import java.net.URL;
//import java.util.ResourceBundle;
//import java.util.concurrent.CountDownLatch;
//
//public class EmployeesController2 extends BaseController implements Initializable {
//
//    private ObservableList<Employee> employees;
//    private FilteredList<Employee> filteredEmployees;
//    private Employee selectedEmployee = null;
//    private boolean editingEmployee = false;
//
//    private final static String ACTIVE_USER_TEXT = "activo";
//    private final static String INACTIVE_USER_TEXT = "inactivo";
//    private final static String ADMIN_ROLE_TEXT = "Administrador";
//    private final static String USER_ROLE_TEXT = "Usuario";
//    private final static String GUESTS_ROLE_TEXT = "Invitado";
//
//    @FXML private ImageView employeeImage;
//    @FXML private Label employeeNameLabel;
//    @FXML private Label employeeEmailLabel;
//    @FXML private Label employeeAddressLabel;
//    @FXML private Label employeePhoneLabel;
//    @FXML private Label employeeLoginNameLabel;
//    @FXML private Label employeeLoginTypeLabel;
//    @FXML private Label employeeStatusLabel;
//
//    @FXML private JFXTextField employeeNameInput;
//    @FXML private JFXTextField employeeSurnameInput;
//    @FXML private JFXTextField employeeEmailInput;
//    @FXML private JFXTextArea employeeAddressInput;
//    @FXML private JFXTextField employeePhoneInput;
//    @FXML private JFXTextField employeeLoginNameInput;
//    @FXML private JFXComboBox<Label> employeeLoginTypeInput;
//    @FXML private JFXToggleButton employeeStatusInput;
//
//    @FXML private JFXRippler editButtonRipple;
//    @FXML private Pane editButtonImage;
//    @FXML private Pane editButton;
//
//    @FXML private JFXRippler removeButtonRipple;
//    @FXML private Pane removeButtonImage;
//    @FXML private Pane removeButton;
//
//    @FXML private JFXButton addButton;
//
//    @FXML private TableView<Employee> infoTable;
//    @FXML private TableColumn<Employee, Number> infoTableId;
//    @FXML private TableColumn<Employee, String> infoTableName;
//    @FXML private TableColumn<Employee, String> infoTableSurname;
//    @FXML private TableColumn<Employee, String> infoTableAddress;
//    @FXML private TableColumn<Employee, String> infoTableEmail;
//    @FXML private TableColumn<Employee, String> infoTablePhone;
//    @FXML private TableColumn<Employee, Boolean> infoTableCheck;
//
//    @FXML private ScrollPane infoContainer;
//
//    @FXML private Pane buttonsContainer;
//
//    public EmployeesController2(Employee loggedEmployee) {
//        super(loggedEmployee);
//    }
//
//    @FXML
//    private void addEmployee() {
//        if (isEditMode())
//            return;
//
//        selectedEmployee = null;
//
//        employeeNameInput.setText("");
//        employeeSurnameInput.setText("");
//        employeeEmailInput.setText("");
//        employeeAddressInput.setText("");
//        employeePhoneInput.setText("");
//        employeeLoginNameInput.setText("");
//        employeeStatusInput.setSelected(false);
//        employeeLoginTypeInput.getSelectionModel().select(1);
//
//        setVisibleLabels(false);
//        setVisibleInputs(true);
//
//        infoTable.getSelectionModel().clearSelection();
//    }
//
//    @FXML
//    private void editEmployee() {
//        if (selectedEmployee != null)
//            setEditMode(true, selectedEmployee);
//    }
//
//    private void removeEmployee() {
//        if (isEditMode() || selectedEmployee == null)
//            return;
//
//        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "employee-dialog");
//
//        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres borrar el usuario?\nEsta acción no puede deshacerse."))
//                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
//                .setAcceptButton(actionEvent -> {
//                    DatabaseMethods.removeEmployees(selectedEmployee);
//                    employees.remove(employees.indexOf(selectedEmployee));
//
//                    dialogBuilder.getDialog().close();
//                })
//                .setOverlayClose(false)
//                .build();
//
//        dialog.show();
//    }
//
//    @FXML
//    private void saveEmployee() {
//        if (selectedEmployee == null)
//            selectedEmployee = new Employee(DatabaseMethods.getLastId("employees", "employee_id") + 1);
//
//        //Set Employee with new values
//        selectedEmployee.setName(employeeNameInput.getText());
//        selectedEmployee.setSurname(employeeSurnameInput.getText());
//        selectedEmployee.setEmail(employeeEmailInput.getText());
//        selectedEmployee.setAddress(employeeAddressInput.getText());
//        selectedEmployee.setPhone(employeePhoneInput.getText());
//        selectedEmployee.setLoginName(employeeLoginNameInput.getText());
//        selectedEmployee.setLoginActive(employeeStatusInput.isSelected());
//
//        switch (employeeLoginTypeInput.getSelectionModel().getSelectedItem().getText()) {
//            case ADMIN_ROLE_TEXT:
//                selectedEmployee.setLoginType("TYPE_ADMIN");
//                break;
//
//            case USER_ROLE_TEXT:
//                selectedEmployee.setLoginType("TYPE_USER");
//                break;
//
//            default:
//                selectedEmployee.setLoginType("TYPE_GUEST");
//        }
//
//        //Updates employees with employee new data.
//        if (employees.indexOf(selectedEmployee) > 0) {
//            employees.set(employees.indexOf(selectedEmployee), selectedEmployee);
//            final Service<Void> service = new Service<Void>() {
//                @Override
//                protected Task<Void> createTask() {
//                    return new Task<Void>() {
//                        @Override
//                        protected Void call() throws Exception {
//                            DatabaseMethods.updateEmployees(selectedEmployee);
//                            return null;
//                        }
//                    };
//                }
//            };
//            service.start();
//        } else {
//            employees.add(selectedEmployee);
//            final Service<Void> service = new Service<Void>() {
//                @Override
//                protected Task<Void> createTask() {
//                    return new Task<Void>() {
//                        @Override
//                        protected Void call() throws Exception {
//                            DatabaseMethods.addEmployees(selectedEmployee);
//                            return null;
//                        }
//                    };
//                }
//            };
//            service.start();
//        }
//
//        setEditMode(false, selectedEmployee);
//    }
//
//    @FXML
//    private void cancelEmployee() {
//        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "employee-dialog");
//
//        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres cancelar la edición?"))
//                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
//                .setAcceptButton(actionEvent -> {
//                    dialogBuilder.getDialog().close();
//                    setEditMode(false, selectedEmployee);
//                })
//                .setOverlayClose(true)
//                .build();
//
//        dialog.show();
//    }
//
//    private void showDialog() {
//        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "employee-dialog");
//
//        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres cancelar la edición?"))
//                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
//                .setAcceptButton(actionEvent -> dialogBuilder.getDialog().close())
//                .setOverlayClose(true)
//                .build();
//
//        dialog.show();
//    }
//
//    private void setEditMode(boolean set, Employee selectedEmployee) {
//        if (set) {
//            //Set inputs with current values from selected employee
//            employeeNameInput.setText(selectedEmployee.getName());
//            employeeSurnameInput.setText(selectedEmployee.getSurname());
//            employeeEmailInput.setText(selectedEmployee.getEmail());
//            employeeAddressInput.setText(selectedEmployee.getAddress());
//            employeePhoneInput.setText(selectedEmployee.getPhone());
//            employeeLoginNameInput.setText(selectedEmployee.getLoginName());
//            employeeStatusInput.setSelected(selectedEmployee.isLoginActive());
//
//            switch (selectedEmployee.getLoginType()) {
//                case "TYPE_ADMIN":
//                    employeeLoginTypeInput.getSelectionModel().select(0);
//                    break;
//
//                case "TYPE_USER":
//                    employeeLoginTypeInput.getSelectionModel().select(1);
//                    break;
//
//                default:
//                    employeeLoginTypeInput.getSelectionModel().select(2);
//            }
//
//            setVisibleInputs(true);
//            setVisibleLabels(false);
//
//            editingEmployee = true;
//        } else {
//            //Fill Labels with new information
//            fillEmployeeInformation(selectedEmployee);
//
//            setVisibleInputs(false);
//            setVisibleLabels(true);
//
//            editingEmployee = false;
//        }
//    }
//
//    private boolean isEditMode() {
//        return editingEmployee;
//    }
//
//    private void setEmployeeLabelPlaceholder(boolean set) {
//        String style = "";
//
//        if (set) {
//            style = "-fx-text-fill: #CCCCCC;";
//
//            employeeName.setText("Nombre");
//            employeeEmail.setText("email@email.com");
//            employeeAddress.setText("Dirección");
//            employeePhone.setText("+00 000000000");
//            employeeLoginName.setText("Nombre de usuario");
//            employeeLoginType.setText("Rol del empleado");
//            employeeStatus.setText("estado");
//
//            employeeImage.setImage(new Image("/resources/images/employees_image/default.png"));
//            ImageUtils.cropImage(employeeImage, 120, 120);
//            ImageUtils.roundImage(employeeImage, 60);
//        } else
//            style = "-fx-text-fill: #000000;";
//
//        employeeName.setStyle(style);
//        employeeEmail.setStyle(style);
//        employeeAddress.setStyle(style);
//        employeePhone.setStyle(style);
//        employeeLoginName.setStyle(style);
//        employeeLoginType.setStyle(style);
//        employeeStatus.setStyle(style);
//    }
//
//    private void fillEmployeeInformation(Employee employee) {
//        setEmployeeLabelPlaceholder(false);
//
//        employeeName.setText(employee.getName() + " " + employee.getSurname());
//        employeeEmail.setText(employee.getEmail());
//        employeeAddress.setText(employee.getAddress());
//        employeePhone.setText(employee.getPhone());
//        employeeLoginName.setText(employee.getLoginName());
//
//        //TODO see .jar
//        //Load user image from /resources/images/employees_image asynchronously
//        final Service<Void> service = new Service<Void>() {
//            @Override
//            protected Task<Void> createTask() {
//                return new Task<Void>() {
//                    @Override
//                    protected Void call() throws Exception {
//                        //Store all files (user images) from employee images directory
//                        File imagesFolder = new File(getClass().getResource("/resources/images/employees_image").toURI());
//                        File[] files = imagesFolder.listFiles();
//                        String fileName = "/resources/images/employees_image/default.png";
//
//                        CountDownLatch latch = new CountDownLatch(1);
//
//                        //If user has custom image, set it
//                        for (int i = 0; i < files.length; i++) {
//                            int pos = files[i].getName().lastIndexOf(".");
//
//                            if (files[i].getName().toLowerCase().substring(0, pos).equals(employee.getLoginName().toLowerCase())) {
//                                fileName = "/resources/images/employees_image/" + files[i].getName();
//                                break;
//                            }
//                        }
//
//                        String finalFileName = fileName;
//                        Platform.runLater(() -> {
//                            try {
//                                employeeImage.setImage(new Image(finalFileName));
//                                ImageUtils.cropImage(employeeImage, 120, 120);
//                                ImageUtils.roundImage(employeeImage, 60);
//                            } finally {
//                                latch.countDown();
//                            }
//                        });
//                        latch.await();
//
//                        return null;
//                    }
//                };
//            }
//        };
//        service.start();
//
//        switch (employee.getLoginType()) {
//            case "TYPE_ADMIN":
//                employeeLoginType.setText(ADMIN_ROLE_TEXT);
//                break;
//
//            case "TYPE_USER":
//                employeeLoginType.setText(USER_ROLE_TEXT);
//                break;
//
//            default:
//                employeeLoginType.setText(GUESTS_ROLE_TEXT);
//        }
//
//        if (employee.isLoginActive()) {
//            employeeStatus.setText(ACTIVE_USER_TEXT);
//            employeeStatus.setStyle("-fx-text-fill: #35ba48;");
//        } else {
//            employeeStatus.setText(INACTIVE_USER_TEXT);
//            employeeStatus.setStyle("-fx-text-fill: #ff5440;");
//        }
//    }
//
//    private void fillTable() {
//        infoTableId.setCellValueFactory(param -> param.getValue().idProperty());
//        infoTableName.setCellValueFactory(param -> param.getValue().nameProperty());
//        infoTableSurname.setCellValueFactory(param -> param.getValue().surnameProperty());
//        infoTableAddress.setCellValueFactory(param -> param.getValue().addressProperty());
//        infoTablePhone.setCellValueFactory(param -> param.getValue().phoneProperty());
//        infoTableEmail.setCellValueFactory(param -> param.getValue().emailProperty());
//
//        infoTableCheck.setCellFactory(param -> new MaterialCheckBoxCell<>());
//        infoTableCheck.setCellValueFactory(param -> param.getValue().checkedProperty());
//
//        infoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
//            selectedEmployee = newValue;
//
//            //Check if selected employee exists (maybe it doesn't because of search)
//            if (newValue != null)
//                fillEmployeeInformation(selectedEmployee);
//            else {
//                //Default employee placeholder
//                setEmployeeLabelPlaceholder(true);
//            }
//        });
//    }
//
//    private void setListeners() {
//        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> {
//            filteredEmployees.setPredicate(employee -> {
//                //If text field is not empty
//                if (newValue == null || newValue.isEmpty())
//                    return true;
//
//                String lowerCaseValue = newValue.toLowerCase();
//
//                //Checks for employee name
//                if (employee.getName().toLowerCase().contains(lowerCaseValue))
//                    return true;
//                    //Checks for employee surname
//                else if (employee.getSurname().toLowerCase().contains(lowerCaseValue))
//                    return true;
//                    //Checks for employee phone
//                else if (employee.getPhone().toLowerCase().contains((lowerCaseValue)))
//                    return true;
//                    //Checks for employee email
//                else if (employee.getEmail().toLowerCase().contains(lowerCaseValue))
//                    return true;
//
//                //If there are not coincidences
//                return false;
//            });
//        });
//
//        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
//        SortedList<Employee> sortedData = new SortedList<>(filteredEmployees);
//        sortedData.comparatorProperty().bind(infoTable.comparatorProperty());
//
//        //Add sortedItems to the TableView
//        infoTable.setItems(sortedData);
//
//        editButtonImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//            editEmployee();
//            event.consume();
//        });
//
//        removeButtonImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//            removeEmployee();
//            event.consume();
//        });
//
//        infoTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        infoTableCheck.setGraphic(new JFXCheckBox());
//    }
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        ImageUtils.cropImage(employeeImage, 120, 120);
//        ImageUtils.roundImage(employeeImage, 60);
//
//        //Default employee placeholder
//        setEmployeeLabelPlaceholder(true);
//
//        //Set ripple effect for edit and remove button
//        editButtonRipple.setControl(editButtonImage);
//        removeButtonRipple.setControl(removeButtonImage);
//
//        //Adds employee role into ComboBox
//        employeeLoginTypeInput.getItems().add(new Label(ADMIN_ROLE_TEXT));
//        employeeLoginTypeInput.getItems().add(new Label(USER_ROLE_TEXT));
//        employeeLoginTypeInput.getItems().add(new Label(GUESTS_ROLE_TEXT));
//
//        //Binds managed property to visible so when visible is true, it becomes managed as well
//        buttonsContainer.managedProperty().bind(buttonsContainer.visibleProperty());
//
//        editButton.visibleProperty().bind(editButtonImage.visibleProperty());
//
//        final Service<Void> service = new Service<Void>() {
//            @Override
//            protected Task<Void> createTask() {
//                return new Task<Void>() {
//                    @Override
//                    protected Void call() throws Exception {
//                        employees = FXCollections.observableList(DatabaseMethods.getAllEmployees());
//                        filteredEmployees = new FilteredList<>(employees, p -> true);
//
//                        CountDownLatch latch = new CountDownLatch(1);
//
//                        Platform.runLater(() -> {
//                            try {
//                                /*
//                                 *                             NOTE
//                                 * ============================================================
//                                 * Run here all methods that use employees or filteredEmployees
//                                 */
//                                fillTable();
//                                setListeners();
//                            } finally {
//                                latch.countDown();
//                            }
//                        });
//                        latch.await();
//
//                        return null;
//                    }
//                };
//            }
//        };
//        service.start();
//    }
//
//    @Override
//    protected void addListener() {
//
//    }
//
//    @Override
//    protected void editListener() {
//
//    }
//
//    @Override
//    protected void removeListener() {
//
//    }
//
//    @Override
//    protected void acceptChanges() {
//
//    }
//
//    @Override
//    protected void cancelChanges() {
//
//    }
//
//    @Override
//    protected void showInformationLabels(boolean show) {
//        employeeNameLabel.setVisible(show);
//        employeeEmailLabel.setVisible(show);
//        employeeAddressLabel.setVisible(show);
//        employeePhoneLabel.setVisible(show);
//        employeeLoginNameLabel.setVisible(show);
//        employeeLoginTypeLabel.setVisible(show);
//        employeeStatusLabel.setVisible(show);
//    }
//
//    @Override
//    protected void showModificationInputs(boolean show) {
//        employeeNameInput.setVisible(show);
//        employeeSurnameInput.setVisible(show);
//        employeeEmailInput.setVisible(show);
//        employeeAddressInput.setVisible(show);
//        employeePhoneInput.setVisible(show);
//        employeeLoginNameInput.setVisible(show);
//        employeeLoginTypeInput.setVisible(show);
//        employeeStatusInput.setVisible(show);
//
//        //Workaround for surname input. When visible, it stays at the bottom of name input
//        employeeSurnameInput.setLayoutX(51);
//        employeeSurnameInput.setLayoutY(show ? 62 : 1);
//    }
//
//    @Override
//    protected void setInformationLabelsPlaceholder(boolean set) {
//
//    }
//}
