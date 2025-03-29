package org.parko.controllers.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.Objects;
import java.net.URL;


public class userDashboard {
    @FXML
    private Button btnAdmin;
    
    @FXML
    private Button btnParking;
    
    @FXML
    private Button btnRapports;
    
    @FXML
    private Button btnParametres;

    @FXML
    private ScrollPane contentScrollPane;
    
    @FXML
    private BorderPane mainHeader;

    @FXML
    private void initialize() {

        setupMenuButtons();
        try {
            System.out.println("Initialisation du tableau de bord utilisateur...");

            if (btnAdmin != null) {
                btnAdmin.setOnAction(this::openAdminPanel);

            }

            loadViewInScrollPane("/InterfaceUtilisateur/UserDashboard/userParking.fxml");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur d'initialisation",
                    "Impossible de charger la vue du tableau de bord",
                    e.getMessage());
        }
    }

    @FXML
    private void openAdminPanel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceUtilisateur/register/loginAdmin.fxml"));
            Parent adminRoot = loader.load();
            
            // Créer une nouvelle scène
            Scene adminScene = new Scene(adminRoot);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(adminScene);
            currentStage.setTitle("ParkoNova - Administration");
            currentStage.centerOnScreen();
            
            currentStage.show();
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue d'administration: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur de navigation", 
                          "Impossible d'ouvrir le panneau d'administration", 
                          e.getMessage());
        }
    }


    private void showErrorAlert(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadViewInScrollPane(String fxmlPath) {
        try {
            // Charger la vue
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Parent root = loader.load();

            // Configuration du ScrollPane pour qu'il s'adapte
            contentScrollPane.setFitToWidth(true);
            contentScrollPane.setFitToHeight(true);

            contentScrollPane.setContent(root);
            // Masquer le header
            if (mainHeader != null) {
                mainHeader.setVisible(false);
                mainHeader.setManaged(false);
            }

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur de chargement",
                          "Impossible de charger la vue " + fxmlPath,
                          e.getMessage());
        }
    }

    // Methode pour mieux distinguer les boutons sélectionnés
    private Button currentSelectedButton = null;

    private void setupMenuButtons() {
        Button[] menuButtons = {
                btnParking
        };
        for (Button button : menuButtons) {
            button.setOnAction(event -> {
                if (currentSelectedButton != null) {
                    currentSelectedButton.getStyleClass().remove("menu-button-selected");
                }

                button.getStyleClass().add("menu-button-selected");
                currentSelectedButton = button;
            });
        }

        if (menuButtons.length > 0) {
            menuButtons[0].getStyleClass().add("menu-button-selected");
            currentSelectedButton = menuButtons[0];
        }
    }


}