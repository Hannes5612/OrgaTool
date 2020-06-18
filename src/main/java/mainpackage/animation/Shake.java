package mainpackage.animation;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Shake {
    private TranslateTransition translateTransition;

    public Shake(Node node) {
        translateTransition =
                new TranslateTransition(Duration.millis(50), node);
        translateTransition.setFromX(0f);
        translateTransition.setByX(5f);
        translateTransition.setByX(-5f);
        translateTransition.setCycleCount(6);
        translateTransition.setAutoReverse(true);

    }

    public void shake() {
        translateTransition.playFromStart();
    }
}
