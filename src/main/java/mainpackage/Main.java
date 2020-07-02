package mainpackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mainpackage.animation.FadeIn;

import java.net.URL;

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
        primaryStage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
        primaryStage.setResizable(false);
        primaryStage.show();

        new FadeIn(root).play();
        System.out.println("Application started successfully!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
