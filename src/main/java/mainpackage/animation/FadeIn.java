package mainpackage.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Fade in transformation for Nodes
 */
public class FadeIn {

    private FadeTransition fadeTransition;

    /**
     * Constructor
     * @param node to apply the fade in to
     */
    public FadeIn(Node node) {
        fadeTransition = new FadeTransition(Duration.millis(500), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
    }

    /**
     * Execute the animation
     */
    public void play() {
        fadeTransition.play();
    }

}
