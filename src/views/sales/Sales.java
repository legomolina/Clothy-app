package views.sales;


import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Employee;
import controllers.sales.SalesController;
import views.BaseView;

public class Sales extends BaseView {
    public Sales(Stage previousStage, Employee loggedEmployee) {
        super(previousStage, false);

        Stage salesStage = createStage("Ventas | Clothy App", "sales");
        Scene salesScene = loadScene(salesStage, "sales.fxml", new SalesController(loggedEmployee, salesStage));

        salesStage.getIcons().add(new Image("resources/clothy_icon.png"));
        salesStage.setScene(salesScene);
        salesStage.setMaximized(true);
        salesStage.show();
    }
}
