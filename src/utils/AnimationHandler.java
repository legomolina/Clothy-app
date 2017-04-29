package utils;

import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationHandler {

    public AnimationBuilder fadeOut(Node node, double time) {
        return fade(node, time, node.getOpacity(), 0);
    }

    public AnimationBuilder fadeOut(Node node, double time, double to) {
        return fade(node, time, node.getOpacity(), to);
    }

    public AnimationBuilder fadeIn(Node node, double time) {
        return fade(node, time, node.getOpacity(), 1.0);
    }

    public AnimationBuilder fadeIn(Node node, double time, double to) {
        return fade(node, time, node.getOpacity(), to);
    }

    private AnimationBuilder fade(Node node, double time, double from, double to) {
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
    }
}
