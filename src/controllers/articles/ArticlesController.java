package controllers.articles;


import controllers.BaseController;
import controllers.database.ArticleStockInfo;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import models.Article;
import models.Employee;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class ArticlesController extends BaseController {
    private ObservableList<Article> articles;
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

    @FXML private TreeTableView<Article> articlesTable;
    @FXML private TreeTableColumn<Article, Number> articlesTableIdColumn;
    @FXML private TreeTableColumn<Article, String> articlesTableNameColumn;
    @FXML private TreeTableColumn<Article, String> articlesTableCodeColumn;
    @FXML private TreeTableColumn<ArticleStockInfo, String> articlesTableSizesColumn;
    @FXML private TreeTableColumn<ArticleStockInfo, Number> articlesTableStockColumn;
    @FXML private TreeTableColumn<Article, String> articlesTableCategoriesColumn;

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

    /*private void searchListener() {
        searchInput.textProperty().addListener((observableValue, oldValue, newValue) -> filteredArticles.setPredicate(article -> {
            currentStatus = ActionStatus.STATUS_NONE;

            //If text field is not empty
            if (newValue == null || newValue.isEmpty())
                return true;

            String lowerCaseValue = newValue.toLowerCase();

            //Checks for employee name
            if (article.getName().toLowerCase().contains(lowerCaseValue))
                return true;

            //check for sizes and categories (array => for)

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Article> sortedData = new SortedList<>(filteredArticles);
        sortedData.comparatorProperty().bind(articlesTable.comparatorProperty());

        //Add sortedItems to the TableView
        articlesTable.setItems(sortedData);
    }*/

    private void createTable() {
        articlesTableIdColumn.setCellValueFactory(param -> param.getValue().getValue().idProperty());
        articlesTableNameColumn.setCellValueFactory(param -> param.getValue().getValue().nameProperty());
        articlesTableCodeColumn.setCellValueFactory(param -> param.getValue().getValue().codeProperty());
        articlesTableSizesColumn.setCellValueFactory(param -> param.getValue().getValue());
        articlesTableStockColumn.setCellValueFactory(param -> param.getValue().getValue().getCategories());
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
