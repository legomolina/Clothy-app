package controllers.brands;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import controllers.BaseController;
import controllers.database.ArticlesMethods;
import controllers.database.BrandsMethods;
import controllers.database.DatabaseMethods;
import controllers.database.SalesMethods;
import custom.MaterialCheckBoxCell;
import custom.validators.CustomRequiredFieldValidator;
import custom.validators.EmailFieldValidator;
import custom.validators.PhoneFieldValidator;
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
import models.Article;
import models.Brand;
import models.Employee;
import models.Sale;
import utils.AnimationHandler;
import utils.DialogBuilder;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class BrandsController extends BaseController {
    private ObservableList<Brand> brands;
    private FilteredList<Brand> filteredBrands;
    private Brand selectedBrand;

    private int selectedBrandsCount;

    private ChangeListener<Boolean> selectedBrandListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            selectedBrandsCount += (newValue) ? 1 : -1;
            deleteSelected.setVisible(selectedBrandsCount > 0);
        }
    };

    @FXML
    private Label brandNameLabel;
    @FXML
    private Label brandEmailLabel;
    @FXML
    private Label brandAddressLabel;
    @FXML
    private Label brandPhoneLabel;

    @FXML
    private JFXTextField brandNameInput;
    @FXML
    private JFXTextField brandEmailInput;
    @FXML
    private JFXTextArea brandAddressInput;
    @FXML
    private JFXTextField brandPhoneInput;

    @FXML
    private TableView<Brand> brandsTable;
    @FXML
    private TableColumn<Brand, Boolean> brandsTableCheckColumn;
    @FXML
    private TableColumn<Brand, Number> brandsTableIdColumn;
    @FXML
    private TableColumn<Brand, String> brandsTableNameColumn;
    @FXML
    private TableColumn<Brand, String> brandsTableAddressColumn;
    @FXML
    private TableColumn<Brand, String> brandsTablePhoneColumn;
    @FXML
    private TableColumn<Brand, String> brandsTableEmailColumn;

    public BrandsController(Employee loggedEmployee, Stage currentStage) {
        super(loggedEmployee, currentStage);

        selectedBrand = null;

        //There's no selected brands
        selectedBrandsCount = 0;
    }

    @Override
    protected void addListener() {
        //If there is no edition or addition in course
        if (currentStatus != ActionStatus.STATUS_NONE && currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        currentStatus = ActionStatus.STATUS_ADDING;
        selectedBrand = new Brand(DatabaseMethods.getLastId("brands", "brand_id") + 1);

        selectedBrand.checkedProperty().addListener(selectedBrandListener);

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedBrand);
    }

    @Override
    protected void editListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING || selectedBrand == null)
            return;

        currentStatus = ActionStatus.STATUS_EDITING;

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedBrand);
    }

    private boolean usedBrand(Brand... brands) {
        for (Article a : ArticlesMethods.getAllArticles()) {
            for (Brand b : brands) {
                if (a.getBrand().equals(b))
                    return true;
            }
        }

        return false;
    }

    @Override
    protected void removeListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar la marca " + selectedBrand.getName() + "?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    if(usedBrand(selectedBrand)) {
                        DialogBuilder dialogBuilder1 = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.ALERT, JFXDialog.DialogTransition.CENTER, "custom-dialog");
                        JFXDialog dialog1 = dialogBuilder1.setContent(new Text("No se puede eliminar la marca porque hay artículos relacionadas"))
                                .setOverlayClose(false)
                                .setAcceptButton(actionEvent1 -> {
                                    dialogBuilder1.getDialog().close();
                                    dialogBuilder.getDialog().close();
                                }).build();
                        dialog1.show();
                        actionEvent.consume();
                        return;
                    }

                    BrandsMethods.removeBrands(selectedBrand);
                    selectedBrand.setChecked(false);
                    brands.remove(selectedBrand);
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
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar las marcas seleccionados?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    if(usedBrand(brandsTable.getSelectionModel().getSelectedItems().toArray(new Brand[brandsTable.getSelectionModel().getSelectedItems().size()]))) {
                        DialogBuilder dialogBuilder1 = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.ALERT, JFXDialog.DialogTransition.CENTER, "custom-dialog");
                        JFXDialog dialog1 = dialogBuilder1.setContent(new Text("No se pueden eliminar las marcas porque hay alguna que tiene artículos relacionados"))
                                .setOverlayClose(false)
                                .setAcceptButton(actionEvent1 -> {
                                    dialogBuilder1.getDialog().close();
                                    dialogBuilder.getDialog().close();
                                }).build();
                        dialog1.show();
                        actionEvent.consume();
                        return;
                    }

                    BrandsMethods.removeBrands(brandsTable.getSelectionModel().getSelectedItems());
                    brands.removeAll(brandsTable.getSelectionModel().getSelectedItems());

                    deleteSelected.setVisible(false);

                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();
    }

    @Override
    protected void acceptChanges(ActionEvent event) {
        //Validate inputs before sending data
        if (!brandNameInput.validate()) return;
        if (!brandEmailInput.validate()) return;
        if (!brandPhoneInput.validate()) return;
        if (!brandAddressInput.validate()) return;

        loaderContainer.setVisible(true);
        AnimationHandler.fadeIn(loaderContainer, 500, 0.6).execute();

        selectedBrand.setName(brandNameInput.getText());
        selectedBrand.setEmail(brandEmailInput.getText());
        selectedBrand.setPhone(brandPhoneInput.getText());
        selectedBrand.setAddress(brandAddressInput.getText());

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CountDownLatch latch = new CountDownLatch(1);

                        if (currentStatus == ActionStatus.STATUS_ADDING) {
                            BrandsMethods.addBrands(selectedBrand);

                            brands.add(selectedBrand);
                        } else if (currentStatus == ActionStatus.STATUS_EDITING) {
                            BrandsMethods.updateBrands(selectedBrand);

                            brands.set(brands.indexOf(selectedBrand), selectedBrand);
                        }

                        Platform.runLater(() -> {
                            try {
                                currentStatus = ActionStatus.STATUS_VIEWING;

                                showInformationLabels(true);
                                showModificationInputs(false);

                                setBrandInformation(selectedBrand);

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
                        setBrandInformation(selectedBrand);
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
        brandNameLabel.setVisible(show);
        brandEmailLabel.setVisible(show);
        brandAddressLabel.setVisible(show);
        brandPhoneLabel.setVisible(show);
    }

    @Override
    protected void showModificationInputs(boolean show) {
        brandNameInput.setVisible(show);
        brandEmailInput.setVisible(show);
        brandAddressInput.setVisible(show);
        brandPhoneInput.setVisible(show);

        formButtonsContainer.setVisible(show);
    }

    @Override
    protected void setInformationLabelsPlaceholder() {
        //Set label default name
        brandNameLabel.setText("Nombre");
        brandEmailLabel.setText("email@email.com");
        brandAddressLabel.setText("Dirección");
        brandPhoneLabel.setText("+00 000000000");

        setBrandsLabelsStyle("-fx-text-fill: " + Style.LIGHT_GREY);
    }

    private void setModificationInputsText(Brand brand) {
        brandNameInput.setText(brand.getName());
        brandEmailInput.setText(brand.getEmail());
        brandAddressInput.setText(brand.getAddress());
        brandPhoneInput.setText(brand.getPhone());
    }

    private void setBrandInformation(Brand brand) {
        setBrandsLabelsStyle("-fx-text-fill: " + Style.BLACK);

        //Set label text with employee information
        brandEmailLabel.setText(brand.getEmail());
        brandAddressLabel.setText(brand.getAddress());
        brandPhoneLabel.setText(brand.getPhone());
    }

    private void setBrandsLabelsStyle(String style) {
        brandNameLabel.setStyle(style);
        brandEmailLabel.setStyle(style);
        brandAddressLabel.setStyle(style);
        brandPhoneLabel.setStyle(style);
    }

    private void searchListener() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredBrands.setPredicate(brand -> {
            currentStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for employee name
            if (brand.getName().toLowerCase().contains(lowerCaseValue))
                return true;
                //Checks for employee phone
            else if (brand.getPhone().toLowerCase().contains((lowerCaseValue)))
                return true;
                //Checks for employee email
            else if (brand.getEmail().toLowerCase().contains(lowerCaseValue))
                return true;

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Brand> sortedData = new SortedList<>(filteredBrands);
        sortedData.comparatorProperty().bind(brandsTable.comparatorProperty());

        //Add sortedItems to the TableView
        brandsTable.setItems(sortedData);
    }

    private void createTable() {
        brandsTableIdColumn.setCellValueFactory(param -> param.getValue().idProperty());
        brandsTableNameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        brandsTableAddressColumn.setCellValueFactory(param -> param.getValue().addressProperty());
        brandsTablePhoneColumn.setCellValueFactory(param -> param.getValue().phoneProperty());
        brandsTableEmailColumn.setCellValueFactory(param -> param.getValue().emailProperty());

        brandsTableCheckColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        brandsTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());

        brandsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (currentStatus == ActionStatus.STATUS_NONE || currentStatus == ActionStatus.STATUS_VIEWING) {
                selectedBrand = newValue;
                currentStatus = ActionStatus.STATUS_VIEWING;

                //Check if selected employee exists (maybe it doesn't because of search)
                if (newValue != null)
                    setBrandInformation(newValue);
                else {
                    //Default employee placeholder
                    setInformationLabelsPlaceholder();
                }
            }
        });

        brandsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        brandsTable.setPlaceholder(new Label("No hay marcas registrados"));

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            for (Brand b : brands)
                b.setChecked(newValue);
        });
        brandsTableCheckColumn.setGraphic(selectAllCheckbox);
    }

    private void setInputValidator() {
        brandNameInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        brandEmailInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        brandEmailInput.getValidators().add(new EmailFieldValidator("El email no corresponde a un email válido"));
        brandPhoneInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        brandPhoneInput.getValidators().add(new PhoneFieldValidator("El campo no es un teléfono válido"));
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
                        brands = FXCollections.observableList(BrandsMethods.getAllBrands());
                        filteredBrands = new FilteredList<>(brands, p -> true);

                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
/*                                                    NOTE
 *                          ============================================================
 *                          Run here all methods that use clients or filteredClients
 */
                                for (Brand b : brands) {
                                    b.checkedProperty().addListener(selectedBrandListener);
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
