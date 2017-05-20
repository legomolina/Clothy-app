package controllers.articles;


import com.jfoenix.controls.JFXCheckBox;
import controllers.BaseController;
import controllers.database.ArticlesMethods;
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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class ArticlesController extends BaseController {
    private ObservableList<Article> articles;
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

    @FXML private TableView<Article> articlesTable;
    @FXML private TableColumn<Article, Number> articlesTableIdColumn;
    @FXML private TableColumn<Article, String> articlesTableNameColumn;
    @FXML private TableColumn<Article, String> articlesTableCodeColumn;
    @FXML private TableColumn<Article, ObservableList<ArticleStockInfo>> articlesTableSizesColumn;
    @FXML private TableColumn<Article, ObservableList<ArticleStockInfo>> articlesTableStockColumn;
    @FXML private TableColumn<Article, ObservableList<Category>> articlesTableCategoriesColumn;
    @FXML private TableColumn<Article, Brand> articlesTableBrandColumn;
    @FXML private TableColumn<Article, Boolean> articlesTableCheckColumn;

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
            if (article.getName().toLowerCase().contains(lowerCaseValue))
                return true;
            else if (article.getCode().toLowerCase().contains(lowerCaseValue))
                return true;

            //check for sizes and categories (array => for)

            //If there are not coincidences
            return false;
        }));

        //As FilterList is immutable, copy the filtered employees to a SortedList and bind it to the TableView
        SortedList<Article> sortedData = new SortedList<>(filteredArticle);
        sortedData.comparatorProperty().bind(articlesTable.comparatorProperty());

        //Add sortedItems to the TableView

        articlesTable.setItems(sortedData);
    }

    @FXML
    private void changes() {
        System.out.println("asd");
        articles.get(0).getCategories().add(new Category(10, "asd", "descripcion"));
    }

    private void createTable() {
        articlesTableIdColumn.setCellValueFactory(param -> param.getValue().idProperty());
        articlesTableNameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        articlesTableCodeColumn.setCellValueFactory(param -> param.getValue().codeProperty());

        articlesTableCheckColumn.setCellValueFactory(param -> param.getValue().checkedProperty());
        articlesTableCheckColumn.setCellFactory(param -> new MaterialCheckBoxCell<>(Pos.TOP_CENTER));

        articlesTableBrandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        articlesTableBrandColumn.setCellFactory(articleBrandTableColumn -> new TableCell<Article, Brand>() {
            @Override
            protected void updateItem(Brand brand, boolean empty) {
                if (brand != null)
                    setGraphic(new Text(brand.getName()));
            }
        });

        articlesTableCategoriesColumn.setCellValueFactory(new PropertyValueFactory<>("categories"));
        articlesTableCategoriesColumn.setCellFactory(param -> new TableCell<Article, ObservableList<Category>>() {
            @Override
            protected void updateItem(ObservableList<Category> categories, boolean empty) {
                if (categories != null)
                    setGraphic(new Text(categories.stream().map(Category::getName).collect(Collectors.joining(", "))));
            }
        });

        articlesTableSizesColumn.setCellValueFactory(new PropertyValueFactory<>("stockInfo"));
        articlesTableSizesColumn.setCellFactory(param -> new TableCell<Article, ObservableList<ArticleStockInfo>>() {
            @Override
            protected void updateItem(ObservableList<ArticleStockInfo> stockInfo, boolean empty) {
                if(stockInfo != null) {
                    VBox container = new VBox();

                    for(ArticleStockInfo info : stockInfo)
                        container.getChildren().add(new Text(info.getSize().getSize()));

                    container.getStyleClass().add("sizes-cell-container");

                    setGraphic(container);
                }
            }
        });

        articlesTableStockColumn.setCellValueFactory(new PropertyValueFactory<>("stockInfo"));
        articlesTableStockColumn.setCellFactory(param -> new TableCell<Article, ObservableList<ArticleStockInfo>>() {
            @Override
            protected void updateItem(ObservableList<ArticleStockInfo> stockInfo, boolean empty) {
                if(stockInfo != null) {
                    VBox container = new VBox();

                    for(ArticleStockInfo info : stockInfo)
                        container.getChildren().add(new Text(String.valueOf(info.getStock())));

                    container.getStyleClass().add("sizes-cell-container");

                    setGraphic(container);
                }
            }
        });

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
            for (Article a : articles)
                a.setChecked(newValue);
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
                        articles = FXCollections.observableArrayList(ArticlesMethods.getAllArticles());
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
