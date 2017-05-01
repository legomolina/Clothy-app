package controllers.employees;


import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controllers.BaseController;
import controllers.database.DatabaseMethods;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import models.Employee;
import utils.ImageUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class EmployeesController extends BaseController implements Initializable {

    @FXML private ImageView employeeImage;
    @FXML private Label employeeName;
    @FXML private Label employeeEmail;
    @FXML private Label employeeAddress;
    @FXML private Label employeePhone;
    @FXML private Label employeeLoginName;
    @FXML private Label employeeLoginType;
    @FXML private Label employeeStatus;
    @FXML private JFXTreeTableView<Employee> infoTable;

    private void fillEmployeeInformation(Employee employee) {
        employeeName.setText(employee.getName().getValue() + " " + employee.getSurname().getValue());
        employeeEmail.setText(employee.getEmail().getValue());
        employeeAddress.setText(employee.getAddress().getValue());
        employeePhone.setText(employee.getPhone().getValue());
        employeeLoginName.setText(employee.getLoginName().getValue());

        switch (employee.getLoginType().getValue()) {
            case "TYPE_ADMIN":
                employeeLoginType.setText("Administrador");
                break;

            case "TYPE_USER":
                employeeLoginType.setText("Usuario");
                break;

            default:
                employeeLoginType.setText("Invitado");
        }

        if (employee.getLoginActive().getValue()) {
            employeeStatus.setText("activo");
            employeeStatus.setStyle("-fx-text-fill: green;");
        } else {
            employeeStatus.setText("inactivo");
            employeeStatus.setStyle("-fx-text-fill: red;");
        }
    }

    private void fillTable(ObservableList<Employee> employees) {
        JFXTreeTableColumn<Employee, Integer> idColumn = new JFXTreeTableColumn<>("Id");
        JFXTreeTableColumn<Employee, String> nameColumn = new JFXTreeTableColumn<>("Nombre");
        JFXTreeTableColumn<Employee, String> surnameColumn = new JFXTreeTableColumn<>("Apellidos");
        JFXTreeTableColumn<Employee, String> addressColumn = new JFXTreeTableColumn<>("Dirección");
        JFXTreeTableColumn<Employee, String> phoneColumn = new JFXTreeTableColumn<>("Teléfono");
        JFXTreeTableColumn<Employee, String> emailColumn = new JFXTreeTableColumn<>("Email");

        idColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getId().getValue()));
        nameColumn.setCellValueFactory(param -> (param.getValue().getValue().getName()));
        surnameColumn.setCellValueFactory(param -> (param.getValue().getValue().getSurname()));
        addressColumn.setCellValueFactory(param -> param.getValue().getValue().getAddress());
        phoneColumn.setCellValueFactory(param -> param.getValue().getValue().getPhone());
        emailColumn.setCellValueFactory(param -> param.getValue().getValue().getEmail());

        idColumn.setPrefWidth(90);
        nameColumn.setPrefWidth(100);
        surnameColumn.setPrefWidth(150);
        addressColumn.setPrefWidth(250);
        phoneColumn.setPrefWidth(125);
        emailColumn.setPrefWidth(250);

        final TreeItem<Employee> root = new RecursiveTreeItem<>(employees, RecursiveTreeObject::getChildren);
        infoTable.setRoot(root);
        infoTable.setEditable(false);
        infoTable.setShowRoot(false);
        infoTable.getColumns().setAll(idColumn, nameColumn, surnameColumn, addressColumn, phoneColumn, emailColumn);
        infoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> fillEmployeeInformation(newValue.getValue()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ImageUtils.cropImage(employeeImage, 120, 120);
        ImageUtils.roundImage(employeeImage, 60);

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        final ObservableList<Employee> employees = FXCollections.observableList(DatabaseMethods.getAllEmployees());
                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
                                fillTable(employees);
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
