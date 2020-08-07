package mainpackage.animation;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Shake animation for nodes
 */
public class Shake {
    private TranslateTransition translateTransition;

    /**
     * Constructor
     * @param node to apply the shake to
     */
    public Shake(Node node) {
        translateTransition =
                new TranslateTransition(Duration.millis(50), node);
        translateTransition.setFromX(0f);
        translateTransition.setByX(5f);
        translateTransition.setByX(-5f);
        translateTransition.setCycleCount(6);
        translateTransition.setAutoReverse(true);

    }

    /**
     * Execute the animation
     */
    public void shake() {
        translateTransition.playFromStart();
    }
}
