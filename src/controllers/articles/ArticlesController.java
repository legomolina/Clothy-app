package controllers.articles;

import com.jfoenix.controls.*;
import controllers.BaseController;
import controllers.database.ArticlesMethods;
import controllers.database.BrandsMethods;
import controllers.database.CategoriesMethods;
import controllers.database.DatabaseMethods;
import custom.MaterialCheckBoxCell;
import custom.MaterialCheckBoxListCell;
import custom.validators.CustomDoubleValidator;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Article;
import models.Brand;
import models.Category;
import models.Employee;
import utils.AnimationHandler;
import utils.DialogBuilder;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class ArticlesController extends BaseController {
    private ObservableList<Article> articles;
    private ObservableList<Category> categories;
    private FilteredList<Article> filteredArticle;
    private Article selectedArticle;

    private int selectedArticlesCount;

    private ChangeListener<Boolean> selectedArticlesListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            selectedArticlesCount += (newValue) ? 1 : -1;
            deleteSelected.setVisible(selectedArticlesCount > 0);
        }
    };

    @FXML
    private TableView<Article> articlesTable;
    @FXML
    private TableColumn<Article, Number> articlesTableIdColumn;
    @FXML
    private TableColumn<Article, String> articlesTableNameColumn;
    @FXML
    private TableColumn<Article, String> articlesTableCodeColumn;
    @FXML
    private TableColumn<Article, ObservableList<Category>> articlesTableCategoriesColumn;
    @FXML
    private TableColumn<Article, String> articlesTableBrandColumn;
    @FXML
    private TableColumn<Article, Boolean> articlesTableCheckColumn;

    @FXML
    private JFXTextField articleNameInput;
    @FXML
    private JFXTextField articleCodeInput;
    @FXML
    private JFXTextArea articleDescriptionInput;
    @FXML
    private JFXComboBox<Brand> articleBrandInput;
    @FXML
    private JFXListView<Category> articleCategoryInput;
    @FXML
    private JFXTextField articlePriceInput;

    @FXML
    private Label articleNameLabel;
    @FXML
    private Label articleCodeLabel;
    @FXML
    private Label articleDescriptionLabel;
    @FXML
    private Label articlePriceLabel;
    @FXML
    private Label articleCategoryLabel;
    @FXML
    private Label articleBrandLabel;

    @FXML
    private Pane formButtonsContainer;

    @FXML
    private Pane editButton;
    @FXML
    private JFXRippler editButtonRippler;

    @FXML
    private Pane removeButton;
    @FXML
    private JFXRippler removeButtonRippler;

    @FXML
    private JFXButton acceptChanges;


    public ArticlesController(Employee loggedEmployee, Stage currentStage) {
        super(loggedEmployee, currentStage);
        selectedArticle = null;

        //There's no selected articles
        selectedArticlesCount = 0;
    }

    @Override
    protected void addListener() {
        //If there is no edition or addition in course
        if (currentStatus != ActionStatus.STATUS_NONE && currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        currentStatus = ActionStatus.STATUS_ADDING;
        selectedArticle = new Article(DatabaseMethods.getLastId("articles", "article_id") + 1);

        selectedArticle.checkedProperty().addListener(selectedArticlesListener);

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedArticle);

        //Select first brand
        articleBrandInput.getSelectionModel().clearSelection();
    }

    @Override
    protected void editListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING || selectedArticle == null)
            return;

        currentStatus = ActionStatus.STATUS_EDITING;

        showInformationLabels(false);
        showModificationInputs(true);

        setModificationInputsText(selectedArticle);
    }

    @Override
    protected void removeListener() {
        if (currentStatus != ActionStatus.STATUS_VIEWING)
            return;

        DialogBuilder dialogBuilder = new DialogBuilder(rootStackPane, DialogBuilder.DialogType.CONFIRM, JFXDialog.DialogTransition.CENTER, "custom-dialog");
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar el artículo " + selectedArticle.getName() + "?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    ArticlesMethods.removeArticles(selectedArticle);
                    selectedArticle.setChecked(false);
                    articles.remove(selectedArticle);
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
        JFXDialog dialog = dialogBuilder.setContent(new Text("¿Seguro que quieres eliminar los artículos seleccionados?"))
                .setOverlayClose(false)
                .setCancelButton(actionEvent -> dialogBuilder.getDialog().close())
                .setAcceptButton(actionEvent -> {
                    ArticlesMethods.removeArticles(articlesTable.getSelectionModel().getSelectedItems());
                    articles.removeAll(articlesTable.getSelectionModel().getSelectedItems());

                    deleteSelected.setVisible(false);

                    dialogBuilder.getDialog().close();
                })
                .build();

        dialog.show();
    }

    @Override
    protected void acceptChanges(ActionEvent event) {
        //Validate inputs before sending data
        if (!articleNameInput.validate()) return;
        if (!articleCodeInput.validate()) return;
        if (!articlePriceInput.validate()) return;

        loaderContainer.setVisible(true);
        AnimationHandler.fadeIn(loaderContainer, 500, 0.6).execute();

        selectedArticle.setName(articleNameInput.getText());
        selectedArticle.setCode(articleCodeInput.getText());
        selectedArticle.setDescription(articleDescriptionInput.getText());
        selectedArticle.setPrice(Float.parseFloat(articlePriceInput.getText()));
        selectedArticle.setBrand(articleBrandInput.getSelectionModel().getSelectedItem());
        selectedArticle.getCategories().clear();

        for (Category c : categories)
            if (c.isChecked())
                selectedArticle.getCategories().add(c);

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CountDownLatch latch = new CountDownLatch(1);

                        if (currentStatus == ActionStatus.STATUS_ADDING) {
                            ArticlesMethods.addArticles(selectedArticle);

                            articles.add(selectedArticle);
                        } else if (currentStatus == ActionStatus.STATUS_EDITING) {
                            ArticlesMethods.updateArticles(selectedArticle);

                            articles.set(articles.indexOf(selectedArticle), selectedArticle);
                        }

                        Platform.runLater(() -> {
                            try {
                                currentStatus = ActionStatus.STATUS_VIEWING;

                                showInformationLabels(true);
                                showModificationInputs(false);

                                setArticleInformation(selectedArticle);

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
                        setArticleInformation(selectedArticle);
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
        articleNameLabel.setVisible(show);
        articleCodeLabel.setVisible(show);
        articleDescriptionLabel.setVisible(show);
        articlePriceLabel.setVisible(show);
        articleBrandLabel.setVisible(show);
        articleCategoryLabel.setVisible(show);
    }

    @Override
    protected void showModificationInputs(boolean show) {
        articleCodeInput.setVisible(show);
        articleNameInput.setVisible(show);
        articleDescriptionInput.setVisible(show);
        articlePriceInput.setVisible(show);
        articleBrandInput.setVisible(show);
        articleCategoryInput.setVisible(show);

        formButtonsContainer.setVisible(show);
    }

    @Override
    protected void setInformationLabelsPlaceholder() {
        //Set label default name
        articleCodeLabel.setText("Nombre");
        articleNameLabel.setText("email@email.com");
        articleDescriptionLabel.setText("Dirección");
        articlePriceLabel.setText("+00 000000000");
        articleBrandLabel.setText("Nombre de usuario");
        articleCategoryLabel.setText("Rol del empleado");

        setArticlesLabelsStyle("-fx-text-fill: " + Style.LIGHT_GREY);
    }

    private void setModificationInputsText(Article article) {
        articleNameInput.setText(article.getName());
        articleCodeInput.setText(article.getCode());
        articleDescriptionInput.setText(article.getDescription());
        articlePriceInput.setText(String.valueOf(article.getPrice()));
        articleBrandInput.getSelectionModel().select(article.getBrand());

        for (Category c : categories) {
            c.setChecked(article.getCategories().contains(c));
        }
    }

    private void setArticleInformation(Article article) {
        setArticlesLabelsStyle("-fx-text-fill: " + Style.BLACK);

        //Set label text with employee information
        articleNameLabel.setText(article.getName());
        articleCodeLabel.setText(article.getCode());
        articleDescriptionLabel.setText(article.getDescription());
        articlePriceLabel.setText(String.valueOf(article.getPrice()));
        articleBrandLabel.setText(article.getBrand().getName());
        articleCategoryLabel.setText(article.getCategories().stream().map(Category::getName).collect(Collectors.joining(", ")));
    }

    private void setArticlesLabelsStyle(String style) {
        articleNameLabel.setStyle(style);
        articleCodeLabel.setStyle(style);
        articleDescriptionLabel.setStyle(style);
        articlePriceLabel.setStyle(style);
        articleBrandLabel.setStyle(style);
        articleCategoryLabel.setStyle(style);
    }

    private void searchListener() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredArticle.setPredicate(article -> {
            currentStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for article name
            if (article.getName().toLowerCase().contains(lowerCaseValue))
                return true;
            else if (article.getCode().toLowerCase().contains(lowerCaseValue))
                return true;
            else if (article.getBrand().getName().toLowerCase().contains(lowerCaseValue))
                return true;

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Article> sortedData = new SortedList<>(filteredArticle);
        sortedData.comparatorProperty().bind(articlesTable.comparatorProperty());

        //Add sortedItems to the TableView
        articlesTable.setItems(sortedData);
    }

    private void createTable() {
        articlesTableIdColumn.setCellValueFactory(param -> param.getValue().idProperty());
        articlesTableNameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        articlesTableCodeColumn.setCellValueFactory(param -> param.getValue().codeProperty());
        articlesTableBrandColumn.setCellValueFactory(param -> param.getValue().getBrand().nameProperty());

        articlesTableCheckColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        articlesTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());


        articlesTableCategoriesColumn.setCellValueFactory(new PropertyValueFactory<>("categories"));
        articlesTableCategoriesColumn.setCellFactory(param -> new TableCell<Article, ObservableList<Category>>() {
            @Override
            protected void updateItem(ObservableList<Category> categories, boolean empty) {
                if (categories != null)
                    setGraphic(new Text(categories.stream().map(Category::getName).collect(Collectors.joining(", "))));
                if (empty)
                    setGraphic(null);
            }
        });

        articlesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (currentStatus == ActionStatus.STATUS_NONE || currentStatus == ActionStatus.STATUS_VIEWING) {
                selectedArticle = newValue;
                currentStatus = ActionStatus.STATUS_VIEWING;

                //Check if selected employee exists (maybe it doesn't because of search)
                if (newValue != null)
                    setArticleInformation(newValue);
                else {
                    //Default employee placeholder
                    setInformationLabelsPlaceholder();
                }
            }
        });

        articlesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        articlesTable.setPlaceholder(new Label("No hay artículos registrados"));

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            for (Article a : articles)
                a.setChecked(newValue);
        });
        articlesTableCheckColumn.setGraphic(selectAllCheckbox);
    }

    private void setInputValidator() {
        articleNameInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        articleCodeInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        articlePriceInput.getValidators().add(new CustomRequiredFieldValidator("El campo no puede estar vacío"));
        articlePriceInput.getValidators().add(new CustomDoubleValidator("El campo no es un número"));
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

        articleBrandInput.setCellFactory(new Callback<ListView<Brand>, ListCell<Brand>>() {
            @Override
            public ListCell<Brand> call(ListView<Brand> brandListView) {
                return new ListCell<Brand>() {
                    @Override
                    protected void updateItem(Brand item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null)
                            setText(item.getName());
                        else
                            setGraphic(null);
                    }
                };
            }
        });
        //Fill brand comboBox
        articleBrandInput.getItems().addAll(BrandsMethods.getAllBrands());

        articleCategoryInput.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        articleCategoryInput.setCellFactory(MaterialCheckBoxListCell.forListView(Category::checkedProperty));

        setInputValidator();
        createTable();

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        articles = FXCollections.observableArrayList(ArticlesMethods.getAllArticles());
                        categories = FXCollections.observableArrayList(CategoriesMethods.getAllCategories());
                        filteredArticle = new FilteredList<>(articles, p -> true);

                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
/*                                                    NOTE
 *                          ============================================================
 *                          Run here all methods that use employees or filteredEmployees
 */
                                for (Article a : articles) {
                                    a.checkedProperty().addListener(selectedArticlesListener);
                                }

                                //Fill category list
                                articleCategoryInput.getItems().addAll(categories);

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
