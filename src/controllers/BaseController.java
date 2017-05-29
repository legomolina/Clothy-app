package controllers;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Employee;
import utils.DialogBuilder;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {
    protected static class Style {
        public static final String LIGHT_GREY = "#CCCCCC";
        public static final String BLACK = "#000000";
        public static final String GREEN = "#35BA48";
        public static final String RED = "#FF5440";
    }

    public enum ActionStatus {
        STATUS_NONE,
        STATUS_VIEWING,
        STATUS_ADDING,
        STATUS_EDITING
    }

    protected ActionStatus currentStatus;

    protected final Employee loggedEmployee;
    private final Stage currentStage;

    @FXML
    protected StackPane rootStackPane;
    @FXML
    protected JFXTextField searchInput;

    @FXML
    protected Pane removeButton;
    @FXML
    protected JFXRippler removeButtonRippler;

    @FXML
    protected Pane editButton;
    @FXML
    protected JFXRippler editButtonRippler;

    @FXML
    protected Pane formButtonsContainer;
    @FXML
    protected AnchorPane loaderContainer;

    @FXML
    protected JFXButton deleteSelected;

    @FXML
    protected JFXButton acceptChanges;

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

    public BaseController(Employee loggedEmployee, Stage currentStage) {
        this.loggedEmployee = loggedEmployee;
        this.currentStage = currentStage;

        //Set actual status as none, there is no action in course
        currentStatus = ActionStatus.STATUS_NONE;

        //Ask for confirmation on close window
        this.setStageCloseListener();
    }

    private void setStageCloseListener() {
        currentStage.setOnCloseRequest(windowEvent -> {
            if (currentStatus != ActionStatus.STATUS_NONE && currentStatus != ActionStatus.STATUS_VIEWING) {
                windowEvent.consume();

                DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
                dialogBuilder.setContent(new Text("¿Seguro que quieres cerrar y cancelar la edición?\n\nPerderás los datos no guardados."))
                        .setOverlayClose(false)
                        .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                        .setAcceptButton(actionEvent -> currentStage.close())
                        .build().show();
            }
        });
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
