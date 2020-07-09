package mainpackage.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeIn {

    private FadeTransition fadeTransition;

    // Constructor for the transition
    public FadeIn(Node node) {
        fadeTransition = new FadeTransition(Duration.millis(500), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
    }

    //play the transition
    public void play() {
        fadeTransition.play();
    }

}
