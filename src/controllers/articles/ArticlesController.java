package controllers.articles;


import controllers.BaseController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import models.Article;
import models.Employee;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
