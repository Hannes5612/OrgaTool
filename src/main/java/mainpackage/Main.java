package mainpackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mainpackage.animation.FadeIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

/**
 * Entry point of the application, directly loads the Login screen
 */
public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class.getName());
    @Override
    public void start(Stage primaryStage) throws Exception {

        //new ProcessBuilder().command("gsettings set org.gnome.desktop.interface scaling-factor 2").start();

        URL fxmlFileUrl = getClass().getClassLoader().getResource("view/Login.fxml");
        Parent root = null;
        if (fxmlFileUrl != null) {
            root = FXMLLoader.load(fxmlFileUrl);
        }

        primaryStage.setTitle("Login");
        assert root != null;
        Scene login = new Scene(root);
        primaryStage.setScene(login);
        primaryStage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
        primaryStage.setResizable(false);
        primaryStage.show();

        new FadeIn(root).play();

        logger.info("Application started");
    }

    public static void main(String[] args) {
        launch(args);
    }

}
