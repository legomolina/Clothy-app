package utils;

import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationHandler {

    public AnimationBuilder fadeIn(Node node, double time) {
        return fadeIn(node, time, 0.0, 1.0);
    }

    public AnimationBuilder fadeIn(Node node, double time, double to) {
        return fadeIn(node, time, 0.0, to);
    }

    public AnimationBuilder fadeIn(Node node, double time, double from, double to) {
        FadeTransition animation = new FadeTransition(Duration.millis(time));

        animation.setNode(node);
        animation.setFromValue(from);
        animation.setToValue(to);
        animation.setCycleCount(1);
        animation.setAutoReverse(false);

        return new AnimationBuilder(animation);
    }


    public class AnimationBuilder {
        private Transition animation;

        AnimationBuilder(Transition animation) {
            this.animation = animation;
        }

        public void execute() {
            animation.playFromStart();
        }
    }
}
