package views.brands;

import controllers.brands.BrandsController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Employee;
import views.BaseView;

public class Brands extends BaseView {
    public Brands(Stage previousStage, Employee loggedEmployee) {
        super(previousStage, false);

        Stage sizesStage = createStage("Marcas | Clothy App", "brands");
        Scene sizesScene = loadScene(sizesStage, "brands.fxml", new BrandsController(loggedEmployee, sizesStage));

        sizesStage.getIcons().add(new Image("resources/clothy_icon.png"));
        sizesStage.setScene(sizesScene);
        sizesStage.setMaximized(true);
        sizesStage.show();
    }
}
