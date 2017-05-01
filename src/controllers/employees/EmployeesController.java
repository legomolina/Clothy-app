package controllers.employees;


import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controllers.Controller;
import controllers.database.DatabaseMethods;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.ImageView;
import models.Employee;
import utils.ImageUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeesController implements Controller, Initializable {

    private ObservableList<Employee> employees;

    @FXML
    private ImageView userImage;

    @FXML
    private JFXTreeTableView<Employee> infoTable;

    private void fillTable() {
        employees = FXCollections.observableList(DatabaseMethods.getAllEmployees());

        JFXTreeTableColumn<Employee, Integer> idColumn = new JFXTreeTableColumn<>("Id");
        JFXTreeTableColumn<Employee, String> nameColumn = new JFXTreeTableColumn<>("Nombre");
        JFXTreeTableColumn<Employee, String> addressColumn = new JFXTreeTableColumn<>("Dirección");
        JFXTreeTableColumn<Employee, String> phoneColumn = new JFXTreeTableColumn<>("Teléfono");
        JFXTreeTableColumn<Employee, String> emailColumn = new JFXTreeTableColumn<>("Email");

        idColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<Integer>(param.getValue().getValue().getId().getValue())); //TODO
        nameColumn.setCellValueFactory(param -> param.getValue().getValue().getName());
        addressColumn.setCellValueFactory(param -> param.getValue().getValue().getAddress());
        phoneColumn.setCellValueFactory(param -> param.getValue().getValue().getPhone());
        emailColumn.setCellValueFactory(param -> param.getValue().getValue().getEmail());

        idColumn.setPrefWidth(50);
        nameColumn.setPrefWidth(200);
        addressColumn.setPrefWidth(250);
        phoneColumn.setPrefWidth(125);
        emailColumn.setPrefWidth(250);

        final TreeItem<Employee> root = new RecursiveTreeItem<>(employees, RecursiveTreeObject::getChildren);
        infoTable.setRoot(root);
        infoTable.setEditable(false);
        infoTable.setShowRoot(false);
        infoTable.getColumns().setAll(idColumn, nameColumn, addressColumn, phoneColumn, emailColumn);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ImageUtils.cropImage(userImage, 120, 120);
        ImageUtils.roundImage(userImage, 60);

        fillTable();
    }
}
