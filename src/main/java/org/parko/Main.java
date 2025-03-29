package org.parko;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //URL fxmlUrl = getClass().getResource("/interfaceUtilisateur/MainView.fxml");
        URL fxmlUrl = getClass().getResource("/interfaceUtilisateur/UserDashboard/userDashboard.fxml");
        System.out.println("FXML URL: " + fxmlUrl);
        if (fxmlUrl == null) {
            System.err.println("Le fichier FXML n'a pas été trouvé!");
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 1200, 695);
        primaryStage.setTitle("Système de Gestion de Parking");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}