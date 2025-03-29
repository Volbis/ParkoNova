package org.parko.controllers.parkingSet;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.parko.database.VehiculeDb;

public class FormRemoveMotoController {

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnSupprimer;

    @FXML
    private TextField txtImmatriculation;

    private String immatriculation;
    private final double tarifHoraire = 500;

    private boolean estSupprimé = false;

    @FXML
    private void supprimerVehicule() {
        // Récupérez l'immatriculation au moment où vous en avez besoin
        immatriculation = getImmatriculation();

        if (isFormValide()) {
            try {
                boolean supVoiture = supprimerVehicule(immatriculation, tarifHoraire);

                this.estSupprimé = supVoiture;
                // Message de réussite de sortie
                if (supVoiture) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succès");
                    alert.setHeaderText(null);
                    alert.setContentText("Le véhicule a été retiré du parking avec succès.");
                    alert.showAndWait();
                    Stage stage = (Stage) btnAnnuler.getScene().getWindow();
                    stage.close();
                }
            } catch (Exception e) {
                // Gestion des erreurs lors de la suppression
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Échec de la sortie du véhicule");
                alert.setContentText("Une erreur s'est produite: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    // Méthode pour ajouter un véhicule dans la base de données
    private boolean supprimerVehicule(String immatriculation, double tarifHoraire) {
        VehiculeDb vehiculeAdd = new VehiculeDb();
        return vehiculeAdd.sortirVehicule(immatriculation, tarifHoraire);
    }

    private boolean isFormValide() {
        if (immatriculation == null || immatriculation.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Formulaire incomplet");
            alert.setHeaderText("Champ requis");
            alert.setContentText("Veuillez saisir l'immatriculation du véhicule.");
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

    // Getters
    public String getImmatriculation() {
        return txtImmatriculation.getText();
    }

    // Event de mise à jour
    public boolean estSupprimé() {
        return estSupprimé;
    }
}
