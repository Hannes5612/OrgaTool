package mainpackage;

import animatefx.animation.FadeIn;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

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
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.show();

        new FadeIn(root).setSpeed(1).play();
        System.out.println("Application started successfully!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
