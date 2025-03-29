package org.parko.controllers.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class loginAdminController {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField emailLabel;

    @FXML
    private PasswordField mdpLabel;

    @FXML
    private Button btnQuit;


    private final String adminEmail = "admin@parkonova.com";
    private final String adminPassword = "admin123";

    @FXML
    private void initialize() {
        // Configuration du bouton Quitter
        if (btnQuit != null) {
            btnQuit.setOnAction(this::retourAccueil);
        }

        // Configuration du bouton Connexion
        if (btnLogin != null) {
            btnLogin.setOnAction(this::handleLoginClick);
    }
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = btnQuit.getScene();
            if (scene != null) {
                Stage stage = (Stage) scene.getWindow();
                stage.setOnCloseRequest(this::handleCloseRequest);
            }
        });
    }

    private void handleCloseRequest(WindowEvent event) {
        event.consume();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceUtilisateur/UserDashboard/userDashboard.fxml"));
            Parent accueilRoot = loader.load();
            Scene accueilScene = new Scene(accueilRoot);
            
            Stage currentStage = (Stage) btnQuit.getScene().getWindow();
            currentStage.setScene(accueilScene);
            currentStage.setTitle("ParkoNova - Accueil");
            

            currentStage.setOnCloseRequest(e -> {
                // Fermer l'application normalement
                Platform.exit();
            });
            
            currentStage.centerOnScreen();
            currentStage.show();
            
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à l'écran d'accueil: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur de navigation", 
                        "Impossible de retourner à l'écran d'accueil", 
                        e.getMessage());
        }
    }
    @FXML
    private
    void retourAccueil(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceUtilisateur/UserDashboard/userDashboard.fxml"));
            Parent accueilRoot = loader.load();
            Scene accueilScene = new Scene(accueilRoot);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Configurer la nouvelle scène
            currentStage.setScene(accueilScene);
            currentStage.setTitle("ParkoNova - Accueil");
            
            // Remplacer le gestionnaire de fermeture pour permettre une fermeture normale
            currentStage.setOnCloseRequest(e -> {
                Platform.exit();
            });
        
        currentStage.centerOnScreen();
        currentStage.show();
            
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à l'écran d'accueil: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur de navigation", 
                          "Impossible de retourner à l'écran d'accueil", 
                          e.getMessage());
        }
    }
    
    private void afficherErreur(String titre, String entete, String contenu) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    /* ============ Connexion de l'administrateur ==============*/


    @FXML
    private void handleLoginClick(ActionEvent event) {
        String email = emailLabel.getText().trim();
        String motDePasse = mdpLabel.getText();
        
        if (email.isEmpty() || motDePasse.isEmpty()) {
            afficherErreur("Champs vides", 
                        "Veuillez remplir tous les champs", 
                        "L'email et le mot de passe sont requis.");
            return;
        }
        
        // Appel de la méthode de vérification des identifiants
        if (verifierIdentifiantsAdmin(email, motDePasse)) {
            // Si les identifiants sont valides, rediriger vers le tableau de bord admin
            naviguerVersTableauBordAdmin(event);
        } else {
            // Si les identifiants sont invalides, afficher une erreur
            afficherErreur("Échec de connexion", 
                        "Identifiants incorrects", 
                        "L'email ou le mot de passe que vous avez saisi est incorrect.");
            mdpLabel.clear();
        }
    }

    private boolean verifierIdentifiantsAdmin(String email, String motDePasse) {
        // Comparaison avec les identifiants prédéfinis
        return email.equals(adminEmail) && motDePasse.equals(adminPassword);
    }


    private void naviguerVersTableauBordAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceUtilisateur/MainView.fxml"));
            Parent adminRoot = loader.load();
            Scene adminScene = new Scene(adminRoot);
            
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(adminScene);
            currentStage.setTitle("ParkoNova - Administration");
            currentStage.centerOnScreen();
            currentStage.show();
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la redirection vers le tableau de bord admin: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur de navigation", 
                        "Impossible d'accéder au tableau de bord administrateur", 
                        e.getMessage());
        }
    }



}