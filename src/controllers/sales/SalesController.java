package controllers.sales;

import com.jfoenix.controls.*;
import controllers.BaseController;
import controllers.database.ClientsMethods;
import controllers.database.DatabaseMethods;
import controllers.database.EmployeesMethods;
import controllers.database.SalesMethods;
import custom.MaterialCheckBoxCell;
import custom.validators.ClientIdExistsValidator;
import custom.validators.CustomRequiredFieldValidator;
import custom.validators.EmployeeIdExistsValidator;
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
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Employee;
import models.Sale;
import models.SaleLine;
import utils.AnimationHandler;
import utils.DialogBuilder;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class SalesController extends BaseController {
    private ObservableList<Sale> sales;
    private FilteredList<Sale> filteredSales;
    private Sale selectedSale;

    private int selectedSalesCount;

    private ChangeListener<Boolean> selectedSaleListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            selectedSalesCount += (newValue) ? 1 : -1;
            deleteSelected.setVisible(selectedSalesCount > 0);
        }
    };

    private final static String PAYMENT_CARD_TEXT = "Tarjeta";
    private final static String PAYMENT_CASH_TEXT = "Efectivo";

    @FXML
    private Label saleClientLabel;
    @FXML
    private Label saleEmployeeLabel;
    @FXML
    private Label saleDateLabel;
    @FXML
    private Label salePaymentLabel;
    @FXML
    private Label saleTotalLabel;

    @FXML
    private JFXTextField saleClientInput;
    @FXML
    private JFXTextField saleEmployeeInput;
    private JFXDatePicker saleDateInput;
    @FXML
    private JFXComboBox<Label> salePaymentInput;
    @FXML
    private Pane saleDatePane;

    @FXML
    private TableView<Sale> salesTable;
    @FXML
    private TableColumn<Sale, Boolean> salesTableCheckColumn;
    @FXML
    private TableColumn<Sale, Number> salesTableIdColumn;
    @FXML
    private TableColumn<Sale, String> salesTableClientColumn;
    @FXML
    private TableColumn<Sale, String> salesTableEmployeeColumn;
    @FXML
    private TableColumn<Sale, String> salesTableDateColumn;
    @FXML
    private TableColumn<Sale, String> salesTablePaymentColumn;

    public SalesController(Employee loggedEmployee, Stage currentStage) {
        super(loggedEmployee, currentStage);

        selectedSale = null;

        //There's no selected sale
        selectedSalesCount = 0;
    }

    @FXML
    private void showLines() {
        if(currentStatus == ActionStatus.STATUS_NONE || selectedSale == null)
            return;

        new SaleLinesController(selectedSale).buildDialog(rootStackPane);
    }

    @Override
    protected void addListener() {
        //If there is no edition or addition in course
        if (currentStatus != ActionStatus.STATUS_NONE && currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        currentStatus = ActionStatus.STATUS_ADDING;
        selectedSale = new Sale(DatabaseMethods.getLastId("sales", "sale_id") + 1, loggedEmployee);

        selectedSale.checkedProperty().addListener(selectedSaleListener);

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedSale);
    }

    @Override
    protected void editListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING || selectedSale == null)
            return;

        currentStatus = ActionStatus.STATUS_EDITING;

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedSale);
    }

    @Override
    protected void removeListener() {
    }

    @Override
    protected void acceptChanges(ActionEvent event) {
        //Validate inputs before sending data
        if (!saleClientInput.validate()) return;
        if (!saleEmployeeInput.validate()) return;

        loaderContainer.setVisible(true);
        AnimationHandler.fadeIn(loaderContainer, 500, 0.6).execute();

        //TODO validate employee id with validators
        selectedSale.setEmployee(EmployeesMethods.getEmployee(Integer.parseInt(saleEmployeeInput.getText())));
        selectedSale.setClient(ClientsMethods.getClient(Integer.parseInt(saleClientInput.getText())));
        selectedSale.setDate(Date.valueOf(saleDateInput.getValue()));

        switch (salePaymentInput.getSelectionModel().getSelectedItem().getText()) {
            case PAYMENT_CARD_TEXT:
                selectedSale.setPayment("TYPE_CARD");
                break;

            case PAYMENT_CASH_TEXT:
                selectedSale.setPayment("TYPE_CASH");
                break;

            default:
                selectedSale.setPayment("TYPE_CASH");
        }

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CountDownLatch latch = new CountDownLatch(1);

                        if (currentStatus == ActionStatus.STATUS_ADDING) {
                            SalesMethods.addSales(selectedSale);

                            sales.add(selectedSale);
                        } else if (currentStatus == ActionStatus.STATUS_EDITING) {
                            SalesMethods.updateSales(selectedSale);

                            sales.set(sales.indexOf(selectedSale), selectedSale);
                        }

                        Platform.runLater(() -> {
                            try {
                                currentStatus = ActionStatus.STATUS_VIEWING;

                                showInformationLabels(true);
                                showModificationInputs(false);

                                setSaleInformation(selectedSale);

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
                        setSaleInformation(selectedSale);
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
        saleClientLabel.setVisible(show);
        saleEmployeeLabel.setVisible(show);
        salePaymentLabel.setVisible(show);
        saleDateLabel.setVisible(show);
    }

    @Override
    protected void showModificationInputs(boolean show) {
        saleClientInput.setVisible(show);
        salePaymentInput.setVisible(show);
        saleDateInput.setVisible(show);
        saleEmployeeInput.setVisible(show);

        formButtonsContainer.setVisible(show);
    }

    @Override
    protected void setInformationLabelsPlaceholder() {
        //Set label default name
        saleClientLabel.setText("Cliente");
        saleEmployeeLabel.setText("Empleado");
        saleDateLabel.setText("Fecha");
        salePaymentLabel.setText("Pago");
        saleTotalLabel.setText("Total");

        setSalesLabelsStyle("-fx-text-fill: " + Style.LIGHT_GREY);
    }

    private void setModificationInputsText(Sale sale) {
        saleClientInput.setText(String.valueOf(sale.getClient().getId()));
        saleEmployeeInput.setText(String.valueOf(sale.getEmployee().getId()));
        saleDateInput.setValue(sale.getDate().toLocalDate());

        switch (sale.getPayment()) {
            case "TYPE_CASH":
                salePaymentInput.getSelectionModel().select(0);
                break;

            case "TYPE_CARD":
                salePaymentInput.getSelectionModel().select(1);
                break;

            default:
                salePaymentInput.getSelectionModel().select(0);
        }
    }

    private void setSaleInformation(Sale sale) {
        setSalesLabelsStyle("-fx-text-fill: " + Style.BLACK);

        //Set label text with sale information
        saleClientLabel.setText(sale.getClient().getCompleteName());
        saleEmployeeLabel.setText(sale.getEmployee().getCompleteName());
        saleDateLabel.setText(saleDateInput.getValue().toString());

        switch (sale.getPayment()) {
            case "TYPE_CASH":
                salePaymentLabel.setText(PAYMENT_CASH_TEXT);
                break;

            case "TYPE_CARD":
                salePaymentLabel.setText(PAYMENT_CARD_TEXT);

            default:
                salePaymentLabel.setText(PAYMENT_CASH_TEXT);
        }
    }

    private void setSalesLabelsStyle(String style) {
        salePaymentLabel.setStyle(style);
        saleDateLabel.setStyle(style);
        saleEmployeeLabel.setStyle(style);
        saleClientLabel.setStyle(style);
        saleTotalLabel.setStyle(style);
    }

    private void setDatePicker() {
        saleDateInput = new JFXDatePicker(LocalDate.now());
        saleDateInput.setVisible(false);
        saleDateInput.setLayoutX(51);
        saleDateInput.setLayoutY(1);
        saleDateInput.setDefaultColor(Color.valueOf("#7856ff"));
        saleDateInput.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item.isAfter(LocalDate.now()))
                            setDisable(true);
                    }
                };
            }
        });
        saleDatePane.getChildren().add(saleDateInput);
    }

    private void searchListener() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredSales.setPredicate(sale -> {
            currentStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for employee
            if (sale.getEmployee().getName().toLowerCase().contains(lowerCaseValue))
                return true;
                //Checks for client
            else if (sale.getClient().getName().toLowerCase().contains(lowerCaseValue))
                return true;
                //Checks for cash payment
            else if (sale.getPayment().equals("TYPE_CASH") && ("efectivo").contains(lowerCaseValue))
                return true;
                //Checks for cash payment
            else if (sale.getPayment().equals("TYPE_CARD") && ("tarjeta").contains(lowerCaseValue))
                return true;

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Sale> sortedData = new SortedList<>(filteredSales);
        sortedData.comparatorProperty().bind(salesTable.comparatorProperty());

        //Add sortedItems to the TableView
        salesTable.setItems(sortedData);
    }

    private void createTable() {
        salesTableIdColumn.setCellValueFactory(param -> param.getValue().idProperty());
        salesTableClientColumn.setCellValueFactory(param -> param.getValue().getClient().nameProperty());
        salesTableEmployeeColumn.setCellValueFactory(param -> param.getValue().getEmployee().nameProperty());
        salesTableDateColumn.setCellValueFactory(param -> param.getValue().dateProperty());

        salesTablePaymentColumn.setCellValueFactory(param -> param.getValue().paymentProperty());
        salesTablePaymentColumn.setCellFactory(new Callback<TableColumn<Sale, String>, TableCell<Sale, String>>() {
            @Override
            public TableCell<Sale, String> call(TableColumn<Sale, String> saleStringTableColumn) {
                return new TableCell<Sale, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            switch (item) {
                                case "TYPE_CASH":
                                    setGraphic(new Text(PAYMENT_CASH_TEXT));
                                    break;

                                case "TYPE_CARD":
                                    setGraphic(new Text(PAYMENT_CARD_TEXT));
                                    break;

                                default:
                                    setGraphic(new Text(PAYMENT_CASH_TEXT));
                                    break;
                            }
                        } else
                            setGraphic(null);
                    }
                };
            }
        });

        salesTableCheckColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        salesTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());

        salesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (currentStatus == ActionStatus.STATUS_NONE || currentStatus == ActionStatus.STATUS_VIEWING) {
                selectedSale = newValue;
                currentStatus = ActionStatus.STATUS_VIEWING;

                //Check if selected employee exists (maybe it doesn't because of search)
                if (newValue != null)
                    setSaleInformation(newValue);
                else {
                    //Default employee placeholder
                    setInformationLabelsPlaceholder();
                }
            }
        });

        salesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        salesTable.setPlaceholder(new Label("No hay ventas registradas"));

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            for (Sale s : sales)
                s.setChecked(newValue);
        });
        salesTableCheckColumn.setGraphic(selectAllCheckbox);
    }

    private void setInputValidator() {
        saleClientInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        saleClientInput.getValidators().add(new ClientIdExistsValidator("El cliente no existe"));
        saleEmployeeInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        saleEmployeeInput.getValidators().add(new EmployeeIdExistsValidator("El empleado no existe"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDatePicker();

        //Default information placeholder
        setInformationLabelsPlaceholder();

        //Set ripple effect for edit and remove button
        editButtonRippler.setControl(editButton);

        //Binds managed property to visible so when visible is true, it becomes managed as well
        formButtonsContainer.managedProperty().bind(formButtonsContainer.visibleProperty());
        loaderContainer.managedProperty().bind(loaderContainer.visibleProperty());

        //Fill employee role comboBox
        salePaymentInput.getItems().add(new Label(PAYMENT_CASH_TEXT));
        salePaymentInput.getItems().add(new Label(PAYMENT_CARD_TEXT));

        setInputValidator();

        createTable();

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        sales = FXCollections.observableList(SalesMethods.getAllSales());
                        filteredSales = new FilteredList<>(sales, p -> true);

                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
/*                                                    NOTE
 *                          ============================================================
 *                          Run here all methods that use sales or filteredSales
 */
                                for (Sale s : sales) {
                                    s.checkedProperty().addListener(selectedSaleListener);
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
