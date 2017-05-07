package views.main_menu;


import controllers.main_menu.MainMenuController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Employee;
import utils.DatabaseHandler;
import views.BaseView;

import java.util.Optional;

public class MainMenu extends BaseView {
    public MainMenu(Stage previousStage, Employee loggedEmployee) {
        super(previousStage, true);

        Stage mainMenuStage = createStage("Clothy App", "main_menu");
        Scene mainMenuScene = loadScene(mainMenuStage, "main_menu.fxml", new MainMenuController(loggedEmployee));

        mainMenuStage.getIcons().add(new Image("resources/clothy_icon.png"));
        mainMenuStage.setScene(mainMenuScene);
        mainMenuStage.show();

        //Closing main menu exists application
        mainMenuStage.setOnCloseRequest(event -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Cerrar Clothy");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Seguro que quieres cerrar Clothy?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == ButtonType.OK) {
                DatabaseHandler.closeConnection();
                Platform.exit();
            }
            else
                event.consume();
        });
    }
}
