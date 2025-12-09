package main;

import controller.SplashScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/splash_screen.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/splash.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);
            InputStream iconStream = getClass().getResourceAsStream("/images/logo.png");
            if (iconStream != null) {
                Image appIcon = new Image(iconStream);
                primaryStage.getIcons().add(appIcon);
            } else {
                System.err.println("⚠️ Icon /images/logo.png introuvable dans les ressources.");
            }
            SplashScreenController controller = loader.getController();
            controller.setStage(primaryStage);
            primaryStage.setTitle("GHARBI'S LIBRARY");
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
            System.out.println("✅ Splash screen loaded successfully (TRANSPARENT MODE)");
        } catch (Exception e) {
            System.err.println("❌ ERROR: Unable to start application");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}