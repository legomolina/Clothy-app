package views.users;

import controllers.users.UserTabController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class UserTab extends Stage {
    public UserTab() {
        try {
            FXMLLoader userLoader = new FXMLLoader(getClass().getResource("user_tab.fxml"));
            Parent userRoot = userLoader.load();

            UserTabController controller = userLoader.getController();
            controller.setInitialText();

            this.setScene(new Scene(userRoot));
            this.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
