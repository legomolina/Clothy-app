package controllers.clients;


import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import controllers.BaseController;
import controllers.database.ClientsMethods;
import controllers.database.DatabaseMethods;
import custom.MaterialCheckBoxCell;
import custom.validators.CustomRequiredFieldValidator;
import custom.validators.EmailFieldValidator;
import custom.validators.NifFieldValidator;
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
import models.Client;
import models.Employee;
import utils.AnimationHandler;
import utils.DialogBuilder;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class ClientsController extends BaseController {
    private ObservableList<Client> clients;
    private FilteredList<Client> filteredClients;
    private Client selectedClient;

    private int selectedClientsCount;

    private ChangeListener<Boolean> selectedClientListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            selectedClientsCount += (newValue) ? 1 : -1;
            deleteSelected.setVisible(selectedClientsCount > 0);
        }
    };

    @FXML
    private Label clientNameLabel;
    @FXML
    private Label clientNifLabel;
    @FXML
    private Label clientEmailLabel;
    @FXML
    private Label clientAddressLabel;
    @FXML
    private Label clientPhoneLabel;

    @FXML
    private JFXTextField clientNameInput;
    @FXML
    private JFXTextField clientSurnameInput;
    @FXML
    private JFXTextField clientNifInput;
    @FXML
    private JFXTextField clientEmailInput;
    @FXML
    private JFXTextArea clientAddressInput;
    @FXML
    private JFXTextField clientPhoneInput;

    @FXML
    private TableView<Client> clientsTable;
    @FXML
    private TableColumn<Client, Boolean> clientsTableCheckColumn;
    @FXML
    private TableColumn<Client, Number> clientsTableIdColumn;
    @FXML
    private TableColumn<Client, String> clientsTableNameColumn;
    @FXML
    private TableColumn<Client, String> clientsTableSurnameColumn;
    @FXML
    private TableColumn<Client, String> clientsTableAddressColumn;
    @FXML
    private TableColumn<Client, String> clientsTablePhoneColumn;
    @FXML
    private TableColumn<Client, String> clientsTableEmailColumn;

    public ClientsController(Employee loggedEmployee, Stage currentStage) {
        super(loggedEmployee, currentStage);

        selectedClient = null;

        //There's no selected client
        selectedClientsCount = 0;
    }

    @Override
    protected void addListener() {
        //If there is no edition or addition in course
        if (currentStatus != ActionStatus.STATUS_NONE && currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        currentStatus = ActionStatus.STATUS_ADDING;
        selectedClient = new Client(DatabaseMethods.getLastId("clients", "client_id") + 1);

        selectedClient.checkedProperty().addListener(selectedClientListener);

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedClient);
    }

    @Override
    protected void editListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING || selectedClient == null)
            return;

        currentStatus = ActionStatus.STATUS_EDITING;

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedClient);
    }

    @Override
    protected void removeListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar al cliente " + selectedClient.getName() + "?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    ClientsMethods.removeClients(selectedClient);
                    selectedClient.setChecked(false);
                    clients.remove(selectedClient);
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
                    ClientsMethods.removeClients(clientsTable.getSelectionModel().getSelectedItems());
                    clients.removeAll(clientsTable.getSelectionModel().getSelectedItems());

                    deleteSelected.setVisible(false);

                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();
    }

    @Override
    protected void acceptChanges(ActionEvent event) {
        //Validate inputs before sending data
        if (!clientNameInput.validate()) return;
        if (!clientSurnameInput.validate()) return;
        if (!clientNifInput.validate()) return;
        if (!clientEmailInput.validate()) return;
        if (!clientPhoneInput.validate()) return;
        if (!clientAddressInput.validate()) return;

        loaderContainer.setVisible(true);
        AnimationHandler.fadeIn(loaderContainer, 500, 0.6).execute();

        selectedClient.setName(clientNameInput.getText());
        selectedClient.setSurname(clientSurnameInput.getText());
        selectedClient.setNif(clientNifInput.getText());
        selectedClient.setEmail(clientEmailInput.getText());
        selectedClient.setPhone(clientPhoneInput.getText());
        selectedClient.setAddress(clientAddressInput.getText());

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CountDownLatch latch = new CountDownLatch(1);

                        if (currentStatus == ActionStatus.STATUS_ADDING) {
                            ClientsMethods.addClients(selectedClient);

                            clients.add(selectedClient);
                        } else if (currentStatus == ActionStatus.STATUS_EDITING) {
                            ClientsMethods.updateClients(selectedClient);

                            clients.set(clients.indexOf(selectedClient), selectedClient);
                        }

                        Platform.runLater(() -> {
                            try {
                                currentStatus = ActionStatus.STATUS_VIEWING;

                                showInformationLabels(true);
                                showModificationInputs(false);

                                setClientInformation(selectedClient);

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
                        setClientInformation(selectedClient);
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
        clientNameLabel.setVisible(show);
        clientNifLabel.setVisible(show);
        clientEmailLabel.setVisible(show);
        clientAddressLabel.setVisible(show);
        clientPhoneLabel.setVisible(show);
    }

    @Override
    protected void showModificationInputs(boolean show) {
        clientNameInput.setVisible(show);
        clientSurnameInput.setVisible(show);
        clientNifInput.setVisible(show);
        clientEmailInput.setVisible(show);
        clientAddressInput.setVisible(show);
        clientPhoneInput.setVisible(show);

        //Workaround for surname input. When visible, it stays at the bottom of name input
        clientSurnameInput.setLayoutX(51);
        clientSurnameInput.setLayoutY(show ? 70 : 1);

        formButtonsContainer.setVisible(show);
    }

    @Override
    protected void setInformationLabelsPlaceholder() {
        //Set label default name
        clientNameLabel.setText("Nombre");
        clientNifLabel.setText("00000000-A");
        clientEmailLabel.setText("email@email.com");
        clientAddressLabel.setText("Dirección");
        clientPhoneLabel.setText("+00 000000000");

        setClientsLabelsStyle("-fx-text-fill: " + Style.LIGHT_GREY);
    }

    private void setModificationInputsText(Client client) {
        clientNameInput.setText(client.getName());
        clientSurnameInput.setText(client.getSurname());
        clientNifInput.setText(client.getNif());
        clientEmailInput.setText(client.getEmail());
        clientAddressInput.setText(client.getAddress());
        clientPhoneInput.setText(client.getPhone());
    }

    private void setClientInformation(Client client) {
        setClientsLabelsStyle("-fx-text-fill: " + Style.BLACK);

        //Set label text with employee information
        clientNameLabel.setText(client.getCompleteName());
        clientNifLabel.setText(client.getNif());
        clientEmailLabel.setText(client.getEmail());
        clientAddressLabel.setText(client.getAddress());
        clientPhoneLabel.setText(client.getPhone());
    }

    private void setClientsLabelsStyle(String style) {
        clientNameLabel.setStyle(style);
        clientNifLabel.setStyle(style);
        clientEmailLabel.setStyle(style);
        clientAddressLabel.setStyle(style);
        clientPhoneLabel.setStyle(style);
    }

    private void searchListener() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredClients.setPredicate(client -> {
            currentStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for employee name
            if (client.getName().toLowerCase().contains(lowerCaseValue))
                return true;
                //Checks for employee surname
            else if (client.getSurname().toLowerCase().contains(lowerCaseValue))
                return true;
                //Checks for employee phone
            else if (client.getPhone().toLowerCase().contains((lowerCaseValue)))
                return true;
                //Checks for employee email
            else if (client.getEmail().toLowerCase().contains(lowerCaseValue))
                return true;

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Client> sortedData = new SortedList<>(filteredClients);
        sortedData.comparatorProperty().bind(clientsTable.comparatorProperty());

        //Add sortedItems to the TableView
        clientsTable.setItems(sortedData);
    }

    private void createTable() {
        clientsTableIdColumn.setCellValueFactory(param -> param.getValue().idProperty());
        clientsTableNameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        clientsTableSurnameColumn.setCellValueFactory(param -> param.getValue().surnameProperty());
        clientsTableAddressColumn.setCellValueFactory(param -> param.getValue().addressProperty());
        clientsTablePhoneColumn.setCellValueFactory(param -> param.getValue().phoneProperty());
        clientsTableEmailColumn.setCellValueFactory(param -> param.getValue().emailProperty());

        clientsTableCheckColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        clientsTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());

        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (currentStatus == ActionStatus.STATUS_NONE || currentStatus == ActionStatus.STATUS_VIEWING) {
                selectedClient = newValue;
                currentStatus = ActionStatus.STATUS_VIEWING;

                //Check if selected employee exists (maybe it doesn't because of search)
                if (newValue != null)
                    setClientInformation(newValue);
                else {
                    //Default employee placeholder
                    setInformationLabelsPlaceholder();
                }
            }
        });

        clientsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        clientsTable.setPlaceholder(new Label("No hay clientes registrados"));

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            for (Client c : clients)
                c.setChecked(newValue);
        });
        clientsTableCheckColumn.setGraphic(selectAllCheckbox);
    }

    private void setInputValidator() {
        clientNameInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        clientNifInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        clientNifInput.getValidators().add(new NifFieldValidator("El DNI no corresponde a un DNI válido"));
        clientEmailInput.getValidators().add(new EmailFieldValidator("El email no corresponde a un email válido"));
        clientPhoneInput.getValidators().add(new PhoneFieldValidator("El campo no es un teléfono válido"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Create table. Adds checkboxes, selection mode, and listener when select row
        createTable();

        //Default information placeholder
        setInformationLabelsPlaceholder();

        //Set ripple effect for edit and remove button
        editButtonRippler.setControl(editButton);
        removeButtonRippler.setControl(removeButton);

        //Binds managed property to visible so when visible is true, it becomes managed as well
        formButtonsContainer.managedProperty().bind(formButtonsContainer.visibleProperty());
        loaderContainer.managedProperty().bind(loaderContainer.visibleProperty());

        setInputValidator();

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        clients = FXCollections.observableList(ClientsMethods.getAllClients());
                        filteredClients = new FilteredList<>(clients, p -> true);

                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
/*                                                    NOTE
 *                          ============================================================
 *                          Run here all methods that use clients or filteredClients
 */
                                for (Client c : clients) {
                                    c.checkedProperty().addListener(selectedClientListener);
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
