package views.main_menu;


import controllers.main_menu.MainMenuController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Employee;
import views.BaseView;

public class MainMenu extends BaseView {
    public MainMenu(Stage previousStage, Employee loggedEmployee) {
        super(previousStage, true);

        Stage mainMenuStage = createStage("Clothy App");
        Scene mainMenuScene = loadScene(mainMenuStage, "main_menu.fxml", new MainMenuController(loggedEmployee));

        mainMenuStage.getIcons().add(new Image("resources/clothy_icon.png"));
        mainMenuStage.setScene(mainMenuScene);
        mainMenuStage.show();
    }
}
