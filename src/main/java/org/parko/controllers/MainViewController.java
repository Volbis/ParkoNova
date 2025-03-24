package org.parko.controllers;

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

import javafx.geometry.Insets;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;



public class MainViewController {

    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private Button btnStatistiques;

    @FXML
    private Button btnParking;

    @FXML
    private Button btnDashboard;

    @FXML
    private Node mainHeader;

    @FXML
    private Button btnHistorique;

    @FXML
    private BarChart<String, Number> comparisonChart;






    public void initialize() {
        // Charge par défaut la vue Dashboard
        configuresChart();

    }

    // Méthode pour changer de page au clique d'un boutton
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == btnParking) {
            loadView("/InterfaceUtilisateur/Parking.fxml");
            // Masquer le header
            if (mainHeader != null) {
                mainHeader.setVisible(false);
                mainHeader.setManaged(false);
            }
        }
        else if (event.getSource() == btnDashboard) {
            handleMainButtonAction(event);
        }
        else if (event.getSource() == btnStatistiques) {
            loadView("/InterfaceUtilisateur/Statistiques.fxml");
            mainHeader.setVisible(false);
            mainHeader.setManaged(false);
        }
        else if (event.getSource() == btnHistorique){
            loadView("/InterfaceUtilisateur/Historiques.fxml");
            if (mainHeader != null) {
                mainHeader.setVisible(false);
                mainHeader.setManaged(false);
            }
        }

    }

    // Méthode pour revenir à l'état initial : La vue orincipale
    @FXML
    private void handleMainButtonAction(ActionEvent event) {
        try {
            // Récupération du stage actuel à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceUtilisateur/MainView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 695);

            // Application de la nouvelle scène au stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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