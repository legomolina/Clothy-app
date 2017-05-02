package views.employees;


import controllers.employees.EmployeesController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import views.BaseView;

public class Employees extends BaseView {
    public Employees(Stage previousStage) {
        super(previousStage, false);

        Stage employeesStage = createStage("Gestión de empleados | Clothy App", "employees");
        Scene employeesScene = loadScene(employeesStage, "../information_test.fxml", new EmployeesController());

        employeesStage.getIcons().add(new Image("resources/clothy_icon.png"));
        employeesStage.setScene(employeesScene);
        employeesStage.setMaximized(true);
        employeesStage.show();
    }
}