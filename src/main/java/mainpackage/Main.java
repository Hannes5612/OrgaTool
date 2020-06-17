package mainpackage;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL fxmlFileUrl = getClass().getClassLoader().getResource("view/Login.fxml");
        Parent root = null;
        if (fxmlFileUrl != null) {
            root = FXMLLoader.load(fxmlFileUrl);
        }
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.show();
        System.out.println("Application started!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
