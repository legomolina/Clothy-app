package views.users;


import controllers.UsersController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Users extends Stage {
    public Users(){
        try {
            FXMLLoader userLoader = new FXMLLoader(getClass().getResource("users.fxml"));
            Parent userRoot = userLoader.load();

            UsersController controller = userLoader.getController();
            controller.setInitialText();

            this.setScene(new Scene(userRoot));
            this.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
