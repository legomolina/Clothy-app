package views.articles;


import controllers.articles.ArticlesController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Employee;
import views.BaseView;

public class Articles extends BaseView {
    public Articles(Stage previousStage, Employee loggedEmployee) {
        super(previousStage, false);

        Stage sizesStage = createStage("Artículos | Clothy App", "articles");
        Scene sizesScene = loadScene(sizesStage, "articles.fxml", new ArticlesController(loggedEmployee, sizesStage));

        sizesStage.getIcons().add(new Image("resources/clothy_icon.png"));
        sizesStage.setScene(sizesScene);
        sizesStage.setMaximized(true);
        sizesStage.show();
    }
}
