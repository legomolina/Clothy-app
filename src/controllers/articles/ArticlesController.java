package controllers.articles;


import controllers.BaseController;
import controllers.database.ArticleStockInfo;
import controllers.database.ArticlesMethods;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.stage.Stage;
import models.Article;
import models.Employee;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class ArticlesController extends BaseController {
    private ObservableList<Article> articles;
    private ObservableList<ArticleStockInfo> stock;
    private FilteredList<ArticleStockInfo> filteredStock;
    private FilteredList<Article> filteredArticles;
    private Article selectedArticle;

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
    private void removeSelectedListener() {}

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
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredArticles.setPredicate(article -> {
            currentStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for article name
            if (article.getName().toLowerCase().contains(lowerCaseValue))
                return true;
            else if(article.getCode().toLowerCase().contains(lowerCaseValue))
                return true;

            //check for sizes and categories (array => for)

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<ArticleStockInfo> sortedData = new SortedList<>(filteredStock);
        sortedData.comparatorProperty().bind(articlesTable.comparatorProperty());

        //Add sortedItems to the TableView

        articlesTable.setItems(sortedData);
    }

    private void createTable() {
        articlesTableIdColumn.setCellValueFactory(param -> param.getValue().getArticle().idProperty());
        articlesTableNameColumn.setCellValueFactory(param -> param.getValue().getArticle().nameProperty());
        articlesTableCodeColumn.setCellValueFactory(param -> param.getValue().getArticle().codeProperty());
        articlesTableSizesColumn.setCellValueFactory(param -> param.getValue().getSize().sizeProperty());
        articlesTableStockColumn.setCellValueFactory(param -> param.getValue().stockProperty());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        articles = FXCollections.observableArrayList(ArticlesMethods.getAllArticles());
                        articles.forEach(article -> {
                            stock.addAll(ArticlesMethods.getStock(article));
                        });
                        filteredStock = new FilteredList<>(stock);
                        filteredArticles = new FilteredList<>(articles);

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

                                createTable();

                                //searchListener();
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
