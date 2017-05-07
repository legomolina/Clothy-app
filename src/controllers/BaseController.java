package controllers;


import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import models.Employee;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {
    protected final Employee loggedEmployee;

    @FXML protected StackPane rootStackPane;
    @FXML protected JFXTextField searchInput;

    @FXML
    protected abstract void addListener();

    @FXML
    protected abstract void editListener();

    @FXML
    protected abstract void removeListener();

    @FXML
    protected abstract void acceptChanges();

    @FXML
    protected abstract void cancelChanges();

    public BaseController(Employee loggedEmployee) {
        this.loggedEmployee = loggedEmployee;
    }

    @FXML
    protected void clearSearchInput() {
        searchInput.setText("");
    }

    protected abstract void showInformationLabels(boolean show);

    protected abstract void showModificationInputs(boolean show);

    protected abstract void setInformationLabelsPlaceholder(boolean set);

    public abstract void initialize(URL url, ResourceBundle resourceBundle);
}
