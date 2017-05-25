package controllers.categories;


import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import controllers.BaseController;
import controllers.database.CategoriesMethods;
import controllers.database.DatabaseMethods;
import custom.MaterialCheckBoxCell;
import custom.validators.CustomRequiredFieldValidator;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Category;
import models.Employee;
import utils.AnimationHandler;
import utils.DialogBuilder;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class CategoriesController extends BaseController {
    private ObservableList<Category> categories;
    private FilteredList<Category> filteredCategories;
    private Category selectedCategory;

    private int selectedCategoriesCount;

    private ChangeListener<Boolean> selectedCategoriesListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            selectedCategoriesCount += (newValue) ? 1 : -1;
            deleteSelected.setVisible(selectedCategoriesCount > 0);
        }
    };

    @FXML
    private Label categoryNameLabel;
    @FXML
    private Label categoryDescriptionLabel;

    @FXML
    private JFXTextField categoryNameInput;
    @FXML
    private JFXTextArea categoryDescriptionInput;

    @FXML
    private TableView<Category> categoriesTable;
    @FXML
    private TableColumn<Category, Boolean> categoriesTableCheckColumn;
    @FXML
    private TableColumn<Category, Number> categoriesTableIdColumn;
    @FXML
    private TableColumn<Category, String> categoriesTableNameColumn;

    public CategoriesController(Employee loggedEmployee, Stage currentStage) {
        super(loggedEmployee, currentStage);

        selectedCategory = null;

        //There's no selected brands
        selectedCategoriesCount = 0;
    }

    @Override
    protected void addListener() {
        //If there is no edition or addition in course
        if (currentStatus != ActionStatus.STATUS_NONE && currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        currentStatus = ActionStatus.STATUS_ADDING;
        selectedCategory = new Category(DatabaseMethods.getLastId("categories", "category_id") + 1);

        selectedCategory.checkedProperty().addListener(selectedCategoriesListener);

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedCategory);
    }

    @Override
    protected void editListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING || selectedCategory == null)
            return;

        currentStatus = ActionStatus.STATUS_EDITING;

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedCategory);
    }

    @Override
    protected void removeListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar la categoría " + selectedCategory.getName() + "?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    CategoriesMethods.removeCategories(selectedCategory);
                    selectedCategory.setChecked(false);
                    categories.remove(selectedCategory);
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
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar las categorías seleccionados?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    CategoriesMethods.removeCategories(categoriesTable.getSelectionModel().getSelectedItems());
                    categories.removeAll(categoriesTable.getSelectionModel().getSelectedItems());

                    deleteSelected.setVisible(false);

                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();
    }

    @Override
    protected void acceptChanges(ActionEvent event) {
//Validate inputs before sending data
        if (!categoryNameInput.validate()) return;

        loaderContainer.setVisible(true);
        AnimationHandler.fadeIn(loaderContainer, 500, 0.6).execute();

        selectedCategory.setName(categoryNameInput.getText());
        selectedCategory.setDescription(categoryDescriptionInput.getText());

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CountDownLatch latch = new CountDownLatch(1);

                        if (currentStatus == ActionStatus.STATUS_ADDING) {
                            CategoriesMethods.addCategories(selectedCategory);

                            categories.add(selectedCategory);
                        } else if (currentStatus == ActionStatus.STATUS_EDITING) {
                            CategoriesMethods.updateCategories(selectedCategory);

                            categories.set(categories.indexOf(selectedCategory), selectedCategory);
                        }

                        Platform.runLater(() -> {
                            try {
                                currentStatus = ActionStatus.STATUS_VIEWING;

                                showInformationLabels(true);
                                showModificationInputs(false);

                                setCategoryInformation(selectedCategory);

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
                        setCategoryInformation(selectedCategory);
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
        categoryNameLabel.setVisible(show);
        categoryDescriptionLabel.setVisible(show);
    }

    @Override
    protected void showModificationInputs(boolean show) {
        categoryNameInput.setVisible(show);
        categoryDescriptionInput.setVisible(show);

        formButtonsContainer.setVisible(show);
    }

    @Override
    protected void setInformationLabelsPlaceholder() {
        //Set label default name
        categoryNameLabel.setText("Nombre");
        categoryDescriptionLabel.setText("Descripción");

        setCategoryLabelsStyle("-fx-text-fill: " + Style.LIGHT_GREY);
    }

    private void setModificationInputsText(Category category) {
        categoryNameInput.setText(category.getName());
        categoryDescriptionInput.setText(category.getDescription());
    }

    private void setCategoryInformation(Category category) {
        setCategoryLabelsStyle("-fx-text-fill: " + Style.BLACK);

        //Set label text with employee information
        categoryNameLabel.setText(category.getName());
        categoryDescriptionLabel.setText(category.getDescription());
    }

    private void setCategoryLabelsStyle(String style) {
        categoryNameLabel.setStyle(style);
        categoryDescriptionLabel.setStyle(style);
    }

    private void searchListener() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredCategories.setPredicate(category -> {
            currentStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for employee name
            if (category.getName().toLowerCase().contains(lowerCaseValue))
                return true;

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Category> sortedData = new SortedList<>(filteredCategories);
        sortedData.comparatorProperty().bind(categoriesTable.comparatorProperty());

        //Add sortedItems to the TableView
        categoriesTable.setItems(sortedData);
    }

    private void createTable() {
        categoriesTableIdColumn.setCellValueFactory(param -> param.getValue().idProperty());
        categoriesTableNameColumn.setCellValueFactory(param -> param.getValue().nameProperty());

        categoriesTableCheckColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        categoriesTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());

        categoriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (currentStatus == ActionStatus.STATUS_NONE || currentStatus == ActionStatus.STATUS_VIEWING) {
                selectedCategory = newValue;
                currentStatus = ActionStatus.STATUS_VIEWING;

                //Check if selected employee exists (maybe it doesn't because of search)
                if (newValue != null)
                    setCategoryInformation(newValue);
                else {
                    //Default employee placeholder
                    setInformationLabelsPlaceholder();
                }
            }
        });

        categoriesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoriesTable.setPlaceholder(new Label("No hay categorías registrados"));

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            for (Category c : categories)
                c.setChecked(newValue);
        });
        categoriesTableCheckColumn.setGraphic(selectAllCheckbox);
    }

    private void setInputValidator() {
        categoryNameInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Default information placeholder
        setInformationLabelsPlaceholder();

        //Set ripple effect for edit and remove button
        editButtonRippler.setControl(editButton);
        removeButtonRippler.setControl(removeButton);

        //Binds managed property to visible so when visible is true, it becomes managed as well
        formButtonsContainer.managedProperty().bind(formButtonsContainer.visibleProperty());
        loaderContainer.managedProperty().bind(loaderContainer.visibleProperty());

        //Create table. Adds checkboxes, selection mode, and listener when select row
        createTable();

        setInputValidator();

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        categories = FXCollections.observableList(CategoriesMethods.getAllCategories());
                        filteredCategories = new FilteredList<>(categories, p -> true);

                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
/*                                                    NOTE
 *                          ============================================================
 *                          Run here all methods that use clients or filteredClients
 */
                                for (Category c : categories) {
                                    c.checkedProperty().addListener(selectedCategoriesListener);
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
