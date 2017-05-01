import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Clothy extends Application {
    @Override
    public void start(Stage loginStage) throws Exception {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("views/login/login.fxml"));
        loginStage.setTitle("Clothy App");
        loginStage.getIcons().add(new Image("resources/clothy_icon.png"));
        loginStage.setScene(new Scene(loginRoot));
        loginStage.sizeToScene();
        loginStage.setResizable(false);
        loginStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
