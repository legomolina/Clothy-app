package views;

import javafx.stage.Stage;


public class BaseStage extends Stage {
    private String stageIdentification;

    public BaseStage(String stageIdentification) {
        this.stageIdentification = stageIdentification;
    }

    public String getStageIdentification() {
        return this.stageIdentification;
    }

    public void setStageIdentification(String stageIdentification) {
        this.stageIdentification = stageIdentification;
    }

    @Override
    public String toString() {
        return "hola, baseStage";
    }
}
