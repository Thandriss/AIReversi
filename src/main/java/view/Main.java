package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        InputStream iconStream = getClass().getResourceAsStream("/img/icon2.png");
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/startMenu.fxml"));
        Parent root = loader.load();
        primaryStage.setResizable(false);
        Scene sceneOfGame = new Scene(root, 1280, 800);
        MainContr contr = loader.getController();
        contr.setStage(primaryStage);
        primaryStage.setTitle("Reversi");
        primaryStage.setScene(sceneOfGame);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
