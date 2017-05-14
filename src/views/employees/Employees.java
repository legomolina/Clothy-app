package views.employees;


import controllers.employees.EmployeesController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Employee;
import views.BaseView;

public class Employees extends BaseView {
    public Employees(Stage previousStage, Employee loggedEmployee) {
        super(previousStage, false);

        Stage employeesStage = createStage("Empleados | Clothy App", "employees");
        Scene employeesScene = loadScene(employeesStage, "sizes.fxml", new EmployeesController(loggedEmployee, employeesStage));

        employeesStage.getIcons().add(new Image("resources/clothy_icon.png"));
        employeesStage.setScene(employeesScene);
        employeesStage.setMaximized(true);
        employeesStage.show();
    }
}
