package fr.noctu.sld;

import fr.noctu.sld.utils.LogUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static LogUtils logger = new LogUtils();
    public static boolean disableAutoUpdate = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Gui.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("SkiddiesLauncherCheat");
        primaryStage.show();
    }

    public static void main(String[] args) {
          launch(args);
          logger.log("Starting the app");
    }
}
