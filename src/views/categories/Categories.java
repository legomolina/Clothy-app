package views.categories;

import controllers.categories.CategoriesController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Employee;
import views.BaseView;

public class Categories extends BaseView {
    public Categories(Stage previousStage, Employee loggedEmployee) {
        super(previousStage, false);

        Stage sizesStage = createStage("Categorías | Clothy App", "categories");
        Scene sizesScene = loadScene(sizesStage, "categories.fxml", new CategoriesController(loggedEmployee, sizesStage));

        sizesStage.getIcons().add(new Image("resources/clothy_icon.png"));
        sizesStage.setScene(sizesScene);
        sizesStage.setMaximized(true);
        sizesStage.show();
    }
}
