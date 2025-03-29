package org.parko.controllers.parkingSet;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.parko.database.VehiculeDb;


public class FormAddMotoController {

    @FXML
    private TextField txtImmatriculation;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAnnuler;


    private String immatriculation;
    private final String type = "MOTO";
    private final int parkingId = 1;
    private boolean enregistrementReussi = false;

    @FXML
    public void initialize() {
        btnAjouter.setOnAction(event -> enregistrerVehicule());
    }

    @FXML
    private void enregistrerVehicule() {
        // Récupérez l'immatriculation au moment où vous en avez besoin
        immatriculation = getImmatriculation();

        if (isFormValide()) {
            try {
                boolean succesMoto = ajouterVehicule(immatriculation, type, parkingId);

                this.enregistrementReussi = succesMoto;
                // Méssage de réuissite d'enregistrement
                if (succesMoto) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succès");
                    alert.setHeaderText(null);
                    alert.setContentText("Le véhicule a été ajouté avec succès.");
                    alert.showAndWait();
                    Stage stage = (Stage) btnAjouter.getScene().getWindow();
                    stage.close();
                }
            } catch (Exception e) {
                // Gestion des erreurs lors de l'ajout
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Échec de l'ajout du véhicule");
                alert.setContentText("Une erreur s'est produite: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    // Méthode pour ajouter un véhicule dans la base de données
    private boolean ajouterVehicule(String immatriculation, String type, int parkingId) {
        VehiculeDb vehiculeAdd = new VehiculeDb();
        return vehiculeAdd.ajouterVehicule(immatriculation, type, parkingId);
    }

    // Getters
    public String getImmatriculation() {
        return txtImmatriculation.getText();
    }

    // Vérifier si l'enregistrement a été effectué avec succès à ajouter dans ParkingController
    public boolean isEnregistrementReussi() {
        return enregistrementReussi;
    }

    public int getParkingId() {
        return parkingId;
    }

    public boolean isImmatriculationValide() {
        String immatriculation = getImmatriculation();
        return immatriculation != null && !immatriculation.trim().isEmpty();
    }

    public boolean isFormValide() {
        if (!isImmatriculationValide()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("L'immatriculation ne peut pas être vide.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    private void annulerEnregistrement() {
        try {
            // Récupérer la fenêtre actuelle à partir du bouton d'annulation
            Stage stage = (Stage) btnAnnuler.getScene().getWindow();
            // Fermer la fenêtre
            stage.close();
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture du formulaire: " + e.getMessage());
            e.printStackTrace();
        }
    }
}