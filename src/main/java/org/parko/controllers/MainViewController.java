package org.parko.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.stage.WindowEvent;

import javafx.geometry.Insets;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class MainViewController {

    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private Button btnStatistiques;

    @FXML
    private Button btnParking;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnDashboard;

    @FXML
    private Node mainHeader;

    @FXML
    private Button btnParametres;

    @FXML
    private Button btnHistorique;

    @FXML
    private BarChart<String, Number> comparisonChart;

    public void initialize() {
        if (btnLogout != null) {
            btnLogout.setOnAction(this::loadUserView);
        }
    
        // Charge par défaut la vue Dashboard
        loadView("/InterfaceUtilisateur/Dashboard.fxml");
        //configuresChart();
        
        // Marquer le bouton Dashboard comme sélectionné par défaut
        if (btnDashboard != null) {
            btnDashboard.getStyleClass().add("menu-button-selected");
            currentSelectedButton = btnDashboard;
        }
        
        // S'assurer que le header est visible au démarrage
        if (mainHeader != null) {
            mainHeader.setVisible(true);
            mainHeader.setManaged(true);
        }
        
        // Ajouter un gestionnaire pour la fermeture de la fenêtre

        Platform.runLater(() -> {
            Stage stage = (Stage) contentScrollPane.getScene().getWindow();
            stage.setOnCloseRequest(this::handleCloseRequest);
        });
    }
    /* FERMETURE GRACE A CLOSE */
    private void handleCloseRequest(WindowEvent event) {
        Platform.exit();
    }
    private Button currentSelectedButton = null;

    // Méthode pour changer de page au clique d'un boutton
    @FXML
    private void handleButtonAction(ActionEvent event){
        if (event.getSource() == btnParking) {
            loadView("/InterfaceUtilisateur/Parking.fxml");
            // Masquer le header
            if (mainHeader != null) {
                mainHeader.setVisible(false);
                mainHeader.setManaged(false);
            }

            // Ajout de la couleur au btn
            // Remove selected style from previously selected button
            if (currentSelectedButton != null) {
                currentSelectedButton.getStyleClass().remove("menu-button-selected");
            }
            // Add selected style to current button
            btnParking.getStyleClass().add("menu-button-selected");
            currentSelectedButton = btnParking;
        }

        else if (event.getSource() == btnDashboard) {
            loadView("/InterfaceUtilisateur/Dashboard.fxml");
            
            // Rendre le header visible si nécessaire
            if (mainHeader != null) {
                mainHeader.setVisible(true);
                mainHeader.setManaged(true);
            }
            
            // Ajout de la couleur au btn
            if (currentSelectedButton != null) {
                currentSelectedButton.getStyleClass().remove("menu-button-selected");
            }
            btnDashboard.getStyleClass().add("menu-button-selected");
            currentSelectedButton = btnDashboard;
        }
        else if (event.getSource() == btnParametres) {
            loadView("/InterfaceUtilisateur/Parametres.fxml");
            mainHeader.setVisible(false);
            mainHeader.setManaged(false);

            // Ajout de la couleur au btn
            // Remove selected style from previously selected button
            if (currentSelectedButton != null) {
                currentSelectedButton.getStyleClass().remove("menu-button-selected");
            }
            // Add selected style to current button
            btnParametres.getStyleClass().add("menu-button-selected");
            currentSelectedButton = btnParametres;
       
        }
        else if (event.getSource() == btnStatistiques) {
            loadView("/InterfaceUtilisateur/Statistiques.fxml");
            mainHeader.setVisible(false);
            mainHeader.setManaged(false);

            // Ajout de la couleur au btn
            // Remove selected style from previously selected button
            if (currentSelectedButton != null) {
                currentSelectedButton.getStyleClass().remove("menu-button-selected");
            }
            // Add selected style to current button
            btnStatistiques.getStyleClass().add("menu-button-selected");
            currentSelectedButton = btnStatistiques;
        }
        else if (event.getSource() == btnHistorique){
            loadView("/InterfaceUtilisateur/Historiques.fxml");
            if (mainHeader != null) {
                mainHeader.setVisible(false);
                mainHeader.setManaged(false);
            }

            // Ajout de la couleur au btn
            // Remove selected style from previously selected button
            if (currentSelectedButton != null) {
                currentSelectedButton.getStyleClass().remove("menu-button-selected");
            }
            // Add selected style to current button
            btnHistorique.getStyleClass().add("menu-button-selected");
            currentSelectedButton = btnHistorique;
        }
    }

    // Méthode pour gérer le clic sur le bouton de déconnexion
    private void loadUserView(ActionEvent event) {
    try {
        // Vérification préalable de l'existence du fichier FXML
        String fxmlPath = "/InterfaceUtilisateur/UserDashboard/userDashboard.fxml";
        java.net.URL fxmlUrl = getClass().getResource(fxmlPath);
        
        if (fxmlUrl == null) {
            System.err.println("ERREUR: Fichier FXML non trouvé: " + fxmlPath);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setHeaderText("Fichier de vue introuvable");
            alert.setContentText("Le fichier de vue utilisateur n'a pas été trouvé. Veuillez contacter l'administrateur système.");
            alert.showAndWait();
            return;
        }
        
        // Chargement du FXML
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        // Récupération du Stage actuel
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        // Configuration de la nouvelle scène
        currentStage.setScene(scene);
        currentStage.setTitle("ParkoNova - Accueil");
        
        // Restaurer le comportement normal pour la fermeture de la fenêtre
        currentStage.setOnCloseRequest(e -> Platform.exit());
        
        // Afficher et centrer la fenêtre
        currentStage.centerOnScreen();
        currentStage.show();
        
        System.out.println("Déconnexion réussie - Retour à l'écran d'accueil utilisateur");
        
    } catch (IOException e) {
        System.err.println("ERREUR lors de la déconnexion: " + e.getMessage());
        e.printStackTrace();
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erreur de déconnexion");
        alert.setHeaderText("Impossible de se déconnecter");
        alert.setContentText("Détails de l'erreur: " + e.getMessage());
        
        // Ajouter les détails de la pile d'appels pour le débogage
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        
        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setMaxWidth(Double.MAX_VALUE);
        
        javafx.scene.layout.GridPane expContent = new javafx.scene.layout.GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new javafx.scene.control.Label("La pile d'exception était:"), 0, 0);
        expContent.add(textArea, 0, 1);
        
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}
/*
    // Méthode pour configurer le graphique à barres
    private void configuresChart(){
        // Configuration du graphique à barres
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Taux moyen d'occupation");
        barSeries.getData().add(new XYChart.Data<>("Lundi", 55));
        barSeries.getData().add(new XYChart.Data<>("Mardi", 60));
        barSeries.getData().add(new XYChart.Data<>("Mercredi", 65));
        barSeries.getData().add(new XYChart.Data<>("Jeudi", 70));
        barSeries.getData().add(new XYChart.Data<>("Vendredi", 85));
        barSeries.getData().add(new XYChart.Data<>("Samedi", 90));
        barSeries.getData().add(new XYChart.Data<>("Dimanche", 40));
        comparisonChart.getData().add(barSeries);
    }
*/
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxml)));
            Parent root = loader.load();

            // Configuration du ScrollPane pour qu'il s'adapte
            contentScrollPane.setFitToWidth(true);
            contentScrollPane.setFitToHeight(true);

            // Chargement du contenu
            contentScrollPane.setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}