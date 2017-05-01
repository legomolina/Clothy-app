package utils;

import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationHandler {

    public static AnimationBuilder fadeOut(Node node, double time) {
        return fade(node, time, node.getOpacity(), 0);
    }

    public static AnimationBuilder fadeOut(Node node, double time, double to) {
        return fade(node, time, node.getOpacity(), to);
    }

    public static AnimationBuilder fadeIn(Node node, double time) {
        return fade(node, time, node.getOpacity(), 1.0);
    }

    public static AnimationBuilder fadeIn(Node node, double time, double to) {
        return fade(node, time, node.getOpacity(), to);
    }

    private static AnimationBuilder fade(Node node, double time, double from, double to) {
        FadeTransition animation = new FadeTransition(Duration.millis(time));

        animation.setNode(node);
        animation.setFromValue(from);
        animation.setToValue(to);
        animation.setCycleCount(1);
        animation.setAutoReverse(false);

        return new AnimationBuilder(animation);
    }

    public static class AnimationBuilder {
        private Transition animation;

        AnimationBuilder(Transition animation) {
            this.animation = animation;
        }

        public void execute() {
            animation.playFromStart();
        }

        public void execute(EventHandler<ActionEvent> callback) {
            animation.playFromStart();
            animation.setOnFinished(callback);
        }
    }
}
