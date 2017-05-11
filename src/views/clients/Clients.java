package views.clients;


import controllers.clients.ClientsController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Employee;
import views.BaseView;

public class Clients extends BaseView {

    public Clients(Stage previousStage, Employee loggedEmployee) {
        super(previousStage, false);

        Stage clientsStage = createStage("Clientes | Clothy App", "clients");
        Scene clientsScene = loadScene(clientsStage, "clients.fxml", new ClientsController(loggedEmployee, clientsStage));

        clientsStage.getIcons().add(new Image("resources/clothy_icon.png"));
        clientsStage.setScene(clientsScene);
        clientsStage.setMaximized(true);
        clientsStage.show();
    }
}
