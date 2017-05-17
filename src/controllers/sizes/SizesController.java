package controllers.sizes;


import com.jfoenix.controls.*;
import controllers.BaseController;
import controllers.database.DatabaseMethods;
import controllers.database.SizesMethods;
import custom.CustomRequiredFieldValidator;
import custom.MaterialCheckBoxCell;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Employee;
import models.Size;
import utils.AnimationHandler;
import utils.DialogBuilder;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class SizesController extends BaseController {

    private ObservableList<Size> sizes;
    private FilteredList<Size> filteredSizes;
    private Size selectedSize;

    private int selectedSizesCount;

    private ChangeListener<Boolean> selectedSizeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            selectedSizesCount += (newValue) ? 1 : -1;
            deleteSelected.setVisible(selectedSizesCount > 0);
        }
    };

    @FXML private Label sizeSizeLabel;
    @FXML private JFXTextField sizeSizeInput;

    @FXML private TableView<Size> sizesTable;
    @FXML private TableColumn<Size, Boolean> sizesTableCheckColumn;
    @FXML private TableColumn<Size, Number> sizesTableIdColumn;
    @FXML private TableColumn<Size, String> sizesTableSizeColumn;

    @FXML private JFXButton acceptChanges;

    @FXML private Pane removeButton;
    @FXML private JFXRippler removeButtonRippler;

    @FXML private Pane editButton;
    @FXML private JFXRippler editButtonRippler;

    @FXML private Pane formButtonsContainer;
    @FXML private AnchorPane loaderContainer;

    @FXML private JFXButton deleteSelected;

    public SizesController(Employee loggedEmployee, Stage currentStage) {
        super(loggedEmployee, currentStage);

        selectedSize = null;

        //There's no selected size
        selectedSizesCount = 0;
    }

    @Override
    protected void addListener() {
        //If there is no edition or addition in course
        if (currentStatus != ActionStatus.STATUS_NONE && currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        currentStatus = ActionStatus.STATUS_ADDING;
        selectedSize = new Size(DatabaseMethods.getLastId("sizes", "size_id") + 1);

        selectedSize.checkedProperty().addListener(selectedSizeListener);

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedSize);
    }

    @Override
    protected void editListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        currentStatus = ActionStatus.STATUS_EDITING;

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedSize);
    }

    @Override
    protected void removeListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar la talla " + selectedSize.getSize() + "?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    SizesMethods.removeSizes(selectedSize);
                    selectedSize.setChecked(false);
                    sizes.remove(selectedSize);
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
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar a los clientes seleccionados?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    SizesMethods.removeSizes(sizesTable.getSelectionModel().getSelectedItems());
                    sizes.removeAll(sizesTable.getSelectionModel().getSelectedItems());

                    deleteSelected.setVisible(false);

                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();
    }

    @Override
    protected void acceptChanges(ActionEvent event) {
//Validate inputs before sending data
        if (!sizeSizeInput.validate()) return;

        loaderContainer.setVisible(true);
        AnimationHandler.fadeIn(loaderContainer, 500, 0.6).execute();

        selectedSize.setSize(sizeSizeInput.getText());

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CountDownLatch latch = new CountDownLatch(1);

                        if (currentStatus == ActionStatus.STATUS_ADDING) {
                            SizesMethods.addSizes(selectedSize);

                            sizes.add(selectedSize);
                        } else if (currentStatus == ActionStatus.STATUS_EDITING) {
                            SizesMethods.updateSizes(selectedSize);

                            sizes.set(sizes.indexOf(selectedSize), selectedSize);
                        }

                        Platform.runLater(() -> {
                            try {
                                currentStatus = ActionStatus.STATUS_VIEWING;

                                showInformationLabels(true);
                                showModificationInputs(false);

                                setSizeInformation(selectedSize);

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
                        setSizeInformation(selectedSize);
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
        sizeSizeLabel.setVisible(show);
    }

    @Override
    protected void showModificationInputs(boolean show) {
        sizeSizeInput.setVisible(show);

        formButtonsContainer.setVisible(show);
    }

    @Override
    protected void setInformationLabelsPlaceholder() {
        //Set label default name
        sizeSizeLabel.setText("Talla");

        setSizesLabelsStyle("-fx-text-fill: " + Style.LIGHT_GREY);
    }

    private void setModificationInputsText(Size size) {
        sizeSizeInput.setText(size.getSize());
    }

    private void setSizeInformation(Size size) {
        setSizesLabelsStyle("-fx-text-fill: " + Style.BLACK);

        //Set label text with employee information
        sizeSizeLabel.setText(size.getSize());
    }

    private void setSizesLabelsStyle(String style) {
        sizeSizeLabel.setStyle(style);
    }

    private void searchListener() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredSizes.setPredicate(size -> {
            currentStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for employee name
            if (size.getSize().toLowerCase().contains(lowerCaseValue))
                return true;

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Size> sortedData = new SortedList<>(filteredSizes);
        sortedData.comparatorProperty().bind(sizesTable.comparatorProperty());

        //Add sortedItems to the TableView
        sizesTable.setItems(sortedData);
    }

    private void createTable() {
        sizesTableIdColumn.setCellValueFactory(param -> param.getValue().idProperty());
        sizesTableSizeColumn.setCellValueFactory(param -> param.getValue().sizeProperty());

        sizesTableCheckColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        sizesTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());

        sizesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (currentStatus == ActionStatus.STATUS_NONE || currentStatus == ActionStatus.STATUS_VIEWING) {
                selectedSize = newValue;
                currentStatus = ActionStatus.STATUS_VIEWING;

                //Check if selected employee exists (maybe it doesn't because of search)
                if (newValue != null)
                    setSizeInformation(newValue);
                else {
                    //Default employee placeholder
                    setInformationLabelsPlaceholder();
                }
            }
        });

        sizesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sizesTable.setPlaceholder(new Label("No hay tallas registradas"));

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            for (Size s : sizes)
                s.setChecked(newValue);
        });
        sizesTableCheckColumn.setGraphic(selectAllCheckbox);
    }

    private void setInputValidator() {
        sizeSizeInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        //Create table. Adds checkboxes, selection mode, and listener when select row
        createTable();

        setInputValidator();

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        sizes = FXCollections.observableList(SizesMethods.getAllSizes());
                        filteredSizes = new FilteredList<>(sizes, p -> true);

                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
/*                                                    NOTE
 *                          ============================================================
 *                          Run here all methods that use sizes or filteredSizes
 */
                                for (Size s : sizes) {
                                    s.checkedProperty().addListener(selectedSizeListener);
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
