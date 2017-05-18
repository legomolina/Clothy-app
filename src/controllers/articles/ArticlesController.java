package controllers.articles;


import com.jfoenix.controls.JFXCheckBox;
import controllers.BaseController;
import controllers.database.ArticleStockInfo;
import controllers.database.ArticlesMethods;
import custom.MaterialCheckBoxCell;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Article;
import models.Brand;
import models.Category;
import models.Employee;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class ArticlesController extends BaseController {
    private ObservableList<ArticleStockInfo> articles;
    private FilteredList<ArticleStockInfo> filteredArticle;
    private ArticleStockInfo selectedArticle;

    private int selectedArticlesCount;

    private ChangeListener<Boolean> selectedArticlesListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            selectedArticlesCount += (newValue) ? 1 : -1;
            deleteSelected.setVisible(selectedArticlesCount > 0);
        }
    };

    @FXML private TableView<ArticleStockInfo> articlesTable;
    @FXML private TableColumn<ArticleStockInfo, Number> articlesTableIdColumn;
    @FXML private TableColumn<ArticleStockInfo, String> articlesTableNameColumn;
    @FXML private TableColumn<ArticleStockInfo, String> articlesTableCodeColumn;
    @FXML private TableColumn<ArticleStockInfo, String> articlesTableSizesColumn;
    @FXML private TableColumn<ArticleStockInfo, Number> articlesTableStockColumn;
    @FXML private TableColumn<ArticleStockInfo, String> articlesTableCategoriesColumn;
    @FXML private TableColumn<ArticleStockInfo, String> articlesTableBrandColumn;
    @FXML private TableColumn<ArticleStockInfo, Boolean> articlesTableCheckColumn;

    public ArticlesController(Employee loggedEmployee, Stage currentStage) {
        super(loggedEmployee, currentStage);
        selectedArticle = null;

        //There's no selected articles
        selectedArticlesCount = 0;
    }

    @Override
    protected void addListener() {

    }

    @Override
    protected void editListener() {

    }

    @Override
    protected void removeListener() {

    }

    @FXML
    private void removeSelectedListener() {
    }

    @Override
    protected void acceptChanges(ActionEvent event) {

    }

    @Override
    protected void cancelChanges() {

    }

    @Override
    protected void showInformationLabels(boolean show) {

    }

    @Override
    protected void showModificationInputs(boolean show) {

    }

    @Override
    protected void setInformationLabelsPlaceholder() {

    }

    private void searchListener() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredArticle.setPredicate(article -> {
            currentStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for article name
            if (article.getArticle().getName().toLowerCase().contains(lowerCaseValue))
                return true;
            else if (article.getArticle().getCode().toLowerCase().contains(lowerCaseValue))
                return true;

            //check for sizes and categories (array => for)

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<ArticleStockInfo> sortedData = new SortedList<>(filteredArticle);
        sortedData.comparatorProperty().bind(articlesTable.comparatorProperty());

        //Add sortedItems to the TableView

        articlesTable.setItems(sortedData);
    }

    @FXML
    private void changes() {
        ArrayList<Category> a = new ArrayList<>();
        a.add(new Category(10, "categoria_1", "descripcion"));
        articles.get(0).getArticle().setCategories(a);
    }

    private void createTable() {
        articlesTableIdColumn.setCellValueFactory(param -> param.getValue().getArticle().idProperty());
        articlesTableNameColumn.setCellValueFactory(param -> param.getValue().getArticle().nameProperty());
        articlesTableCodeColumn.setCellValueFactory(param -> param.getValue().getArticle().codeProperty());
        articlesTableSizesColumn.setCellValueFactory(param -> param.getValue().getSize().sizeProperty());
        articlesTableStockColumn.setCellValueFactory(param -> param.getValue().stockProperty());
        articlesTableCategoriesColumn.setCellValueFactory(param -> param.getValue().getArticle().getAllCategories());

        articlesTableCheckColumn.setCellValueFactory(param -> param.getValue().getArticle().checkedProperty());
        articlesTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>());

        articlesTableBrandColumn.setCellValueFactory(param -> param.getValue().getArticle().getBrand().nameProperty());

        /*articlesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
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
        });*/

        articlesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        articlesTable.setPlaceholder(new Label("No hay artículos registrados"));

        JFXCheckBox selectAllCheckbox = new JFXCheckBox();
        selectAllCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            for (ArticleStockInfo a : articles)
                a.getArticle().setChecked(newValue);
        });
        articlesTableCheckColumn.setGraphic(selectAllCheckbox);
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

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        articles = FXCollections.observableArrayList();
                        ArticlesMethods.getAllArticles().forEach(article -> {
                            articles.addAll(ArticlesMethods.getStock(article));
                        });
                        filteredArticle = new FilteredList<>(articles, p -> true);

                        CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            try {
/*                                                    NOTE
 *                          ============================================================
 *                          Run here all methods that use employees or filteredEmployees
 */
                                for (ArticleStockInfo a : articles) {
                                    a.getArticle().checkedProperty().addListener(selectedArticlesListener);
                                }

                                searchListener();
                                createTable();
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
