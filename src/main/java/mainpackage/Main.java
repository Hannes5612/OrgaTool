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

public class Main extends Application {

    //private static final Logger logger = LogManager.getLogger(Main.class.getName());
    @Override
    public void start(Stage primaryStage) throws Exception {
//        String log4jConfPath = "src/main/resources/log4j2.xml";
//        PropertyConfigurator.configure(log4jConfPath);

        //new ProcessBuilder().command("gsettings set org.gnome.desktop.interface scaling-factor 2").start();

        URL fxmlFileUrl = getClass().getClassLoader().getResource("view/Login.fxml");
        Parent root = null;
        if (fxmlFileUrl != null) {
            root = FXMLLoader.load(fxmlFileUrl);
        }

        primaryStage.setTitle("Login");
        assert root != null;
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
        primaryStage.setResizable(false);
        primaryStage.show();

        new FadeIn(root).play();

        System.out.println("Application started successfully!");
        //logger.debug("Application started");
        //logger.info("Applichation started");
        //logger.error("Applichation started");
        //logger.info("Application started");
    }

    public static void main(String[] args) {
        launch(args);
    }

}
