package views.main_menu;


import controllers.main_menu.MainMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenu extends Stage {
    public MainMenu() {
        try {
            FXMLLoader userLoader = new FXMLLoader(getClass().getResource("main_menu.fxml"));
            Parent userRoot = userLoader.load();

            MainMenuController controller = userLoader.getController();
            controller.setInitialText();

            this.setScene(new Scene(userRoot));
            this.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
