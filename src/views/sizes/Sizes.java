package views.sizes;

import controllers.sizes.SizesController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Employee;
import views.BaseView;


public class Sizes extends BaseView {
    public Sizes(Stage previousStage, Employee loggedEmployee) {
        super(previousStage, false);

        Stage sizesStage = createStage("Tallas | Clothy App", "clients");
        Scene sizesScene = loadScene(sizesStage, "sizes.fxml", new SizesController(loggedEmployee, sizesStage));

        sizesStage.getIcons().add(new Image("resources/clothy_icon.png"));
        sizesStage.setScene(sizesScene);
        sizesStage.setMaximized(true);
        sizesStage.show();
    }
}
