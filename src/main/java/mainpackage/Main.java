package mainpackage;

import animatefx.animation.FadeIn;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
        primaryStage.getIcons().add(new Image("icon/Logo organizingTool small.png"));
        primaryStage.setResizable(true);
        primaryStage.show();

        new FadeIn(root).setSpeed(1).play();
        System.out.println("Application started successfully!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
