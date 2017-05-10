package controllers;


import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import models.Employee;

import java.beans.EventHandler;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {
    protected static class Style {
        public static final String LIGHT_GREY = "#CCCCCC";
        public static final String BLACK = "#000000";
        public static final String GREEN = "#35BA48";
        public static final String RED = "#FF5440";
    }

    protected enum ActionStatus {
        STATUS_NONE,
        STATUS_VIEWING,
        STATUS_ADDING,
        STATUS_EDITING
    }

    protected ActionStatus currentStatus;

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
    protected abstract void acceptChanges(ActionEvent event);

    @FXML
    protected abstract void cancelChanges();

    public BaseController(Employee loggedEmployee) {
        this.loggedEmployee = loggedEmployee;

        //Set actual status as none, there is no action in course
        currentStatus = ActionStatus.STATUS_NONE;
    }

    @FXML
    protected void clearSearchInput() {
        searchInput.setText("");
    }

    protected abstract void showInformationLabels(boolean show);

    protected abstract void showModificationInputs(boolean show);

    protected abstract void setInformationLabelsPlaceholder();

    public abstract void initialize(URL url, ResourceBundle resourceBundle);
}
