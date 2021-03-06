package views;

import controllers.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class BaseView {
    public BaseView(Stage previousStage, boolean close) {
        if (close)
            previousStage.close();
    }

    protected Stage createStage(String stageView, String stageIdentification) {
        Stage newStage = new BaseStage(stageIdentification);
        newStage.setTitle(stageView);

        return newStage;
    }

    protected Scene loadScene(Stage stage, String sceneUrl) {
        return loadScene(stage, sceneUrl, null);
    }

    protected Scene loadScene(Stage stage, String sceneUrl, BaseController sceneController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneUrl));

            if (sceneController != null)
                loader.setController(sceneController);

            Parent root = loader.load();
            Scene newScene = new Scene(root);

            return newScene;

        } catch (IOException e) {
            System.out.println("Unable to load Scene " + sceneUrl);
            e.printStackTrace();
        }

        return null;
    }
}
