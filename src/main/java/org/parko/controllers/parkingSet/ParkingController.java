package org.parko.controllers.parkingSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.parko.database.DatabaseConnection;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParkingController {

    @FXML
    private Button btnAjouterMoto;

    @FXML
    private Button btnSortirVoiture;

    @FXML
    private Button btnAjouterVoiture;

    @FXML
    private ImageView place_1;
    @FXML
    private ImageView place_2;
    @FXML
    private ImageView place_3;
    @FXML
    private ImageView place_4;
    @FXML
    private ImageView place_5;
    @FXML
    private ImageView place_6;
    @FXML
    private ImageView place_7;
    @FXML
    private ImageView place_8;
    @FXML
    private ImageView place_9;
    @FXML
    private ImageView place_10;
    @FXML
    private ImageView place_11;
    @FXML
    private ImageView place_12;
    @FXML
    private ImageView place_13;
    @FXML
    private ImageView place_14;
    @FXML
    private ImageView place_15;
    @FXML
    private ImageView place_16;
    @FXML
    private ImageView place_17;
    @FXML
    private ImageView place_18;
    @FXML
    private ImageView place_19;
    @FXML
    private ImageView place_20;

    public void initialize() {
        if (btnAjouterMoto != null) {
            btnAjouterMoto.setOnAction(event -> handleAjouterMoto());
        }

        if (btnAjouterVoiture != null) {
            btnAjouterVoiture.setOnAction(event -> loadAjouterVoiture());
        }

        if (btnSortirVoiture != null) {
            btnSortirVoiture.setOnAction(event -> loadSortirVoiture ());
        }
        chargerEtatPlacesDepuisDB();
        initialiserImagesVoitures();



    }

    /* =================== LES METHODES DE VOITURES ==========================*/

    // Pour charger le formulaire & Maj les voitures déjà enregistrés
    @FXML
    private void loadAjouterVoiture() {
        try {
            // Chemin vers le fichier FXML du formulaire d'ajout de voiture
            String fxmlPath = "/InterfaceUtilisateur/formAddVoiture.fxml";
            if (getClass().getResource(fxmlPath) == null) {
                throw new IOException("Le fichier FXML n'a pas été trouvé: " + fxmlPath);
            }

            // Récupérer la fenêtre actuelle pour l'utiliser comme parent
            Stage currentStage = (Stage) btnAjouterVoiture.getScene().getWindow();

            // Créer et configurer la boîte de dialogue
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(currentStage);
            dialogStage.setTitle("Ajouter une voiture");

            // Charger le FXML et obtenir le contrôleur
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            FormAddVoitureController controller = loader.getController();

            // Configurer la scène
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Configurer la mise à jour en temps réel
            dialogStage.setOnHidden(event -> {
                // Vérifier si l'enregistrement a été effectué avec succès
                if (controller.isEnregistrementReussi()) {
                    // Recharger l'état des places depuis la base de données
                    chargerEtatPlacesDepuisDB();
                }
            });

            // Afficher la boîte de dialogue et attendre
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir le formulaire");
            alert.setContentText("Une erreur s'est produite: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadSortirVoiture() {
        try {
            // Chemin vers le fichier FXML du formulaire de sortie de voiture
            String fxmlPath = "/InterfaceUtilisateur/formRemoveVoiture.fxml";
            if (getClass().getResource(fxmlPath) == null) {
                throw new IOException("Le fichier FXML n'a pas été trouvé: " + fxmlPath);
            }

            // Configuration de la fenêtre modale
            Stage currentStage = (Stage) btnSortirVoiture.getScene().getWindow();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(currentStage);
            dialogStage.setTitle("Sortir une voiture");

            // Chargement du FXML et récupération du contrôleur
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            FormRemoveVoitureController controller = loader.getController();

            // Configuration de la scène
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Configurer la mise à jour en temps réel
            dialogStage.setOnHidden(event -> {
                // Vérifier si la suppression a été effectuée avec succès
                if (controller.estSupprimé()) {
                    // Recharger l'état des places depuis la base de données
                    chargerEtatPlacesDepuisDB();
                }
            });

            // Afficher la boîte de dialogue
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir le formulaire");
            alert.setContentText("Une erreur s'est produite: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /* AJOUT DE L'ICON DE VOITURE DANS LE PERKING */

    // Utiliser une Map pour associer les numéros de place aux ImageView
    private Map<Integer, ImageView> placesMap = new HashMap<>();
    // Déclaration de la variable membre pour stocker les images
    private List<Image> voitureImages;

    private void initialiserImagesVoitures() {
        voitureImages = new ArrayList<>();

        // Chargement des images (ajustez le nombre selon vos fichiers disponibles)
        for (int i = 0; i <= 5; i++) {
            String imagePath = "/vehicule/Voitures/car" + i + ".png";
            try {
                Image voitureImage = new Image(getClass().getResourceAsStream(imagePath));
                voitureImages.add(voitureImage);
            } catch (Exception e) {
                System.err.println("Impossible de charger l'image: " + imagePath);
            }
        }

        // Vérification qu'au moins une image a été chargée
        if (voitureImages.isEmpty()) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/vehicule/Voitures/car0.png"));
                voitureImages.add(defaultImage);
            } catch (Exception e) {
                System.err.println("Impossible de charger l'image par défaut");
            }
        }
    }

    private Image getRandomVoitureImage() {
        // Si la liste n'est pas encore initialisée, l'initialiser
        if (voitureImages == null) {
            initialiserImagesVoitures();
        }

        // Vérifier que la liste n'est pas vide
        if (voitureImages.isEmpty()) {
            return null;
        }
        // Sélectionner une image aléatoire
        Random random = new Random();
        int randomIndex = random.nextInt(voitureImages.size());
        return voitureImages.get(randomIndex);
    }

    private void chargerEtatPlacesDepuisDB() {
        // Initialisation de la map des places
        placesMap.put(1, place_1);
        placesMap.put(2, place_2);
        placesMap.put(3, place_3);
        placesMap.put(4, place_4);
        placesMap.put(5, place_5);
        placesMap.put(6, place_6);
        placesMap.put(7, place_7);
        placesMap.put(8, place_8);
        placesMap.put(9, place_9);
        placesMap.put(10, place_10);
        placesMap.put(11, place_11);
        placesMap.put(12, place_12);
        placesMap.put(13, place_13);
        placesMap.put(14, place_14);
        placesMap.put(15, place_15);
        placesMap.put(16, place_16);
        placesMap.put(17, place_17);
        placesMap.put(18, place_18);
        placesMap.put(19, place_19);
        placesMap.put(20, place_20);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        // Connexion à la base de données et vérification de l'état des places
        try {
            // Établir la connexion à la base de données en utilisant DatabaseConnection
            connection = DatabaseConnection.getConnection();

            // Préparer la requête SQL
            String query = "SELECT id, occupee " +
                    "FROM place WHERE id " +
                    "IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)";

            statement = connection.prepareStatement(query);

            // Exécuter la requête
            resultSet = statement.executeQuery();

            // Créer une image pour les places occupées (à charger une seule fois)
           // Image voitureImage = getRandomVoitureImage();

            while (resultSet.next()) {
                int placeId = resultSet.getInt("id");
                int estOccupee = resultSet.getInt("occupee");

                // Récupérer l'ImageView correspondant à cet ID
                ImageView placeView = placesMap.get(placeId);

                // Vérifie si la place trouvé grace à l'Id correspond à celle enregistré dans le HashMap
                if (placeView != null) {
                    if (estOccupee == 1) {
                        // Obtenir une nouvelle image aléatoire pour chaque place
                        Image voitureImage = getRandomVoitureImage();
                        placeView.setImage(voitureImage);

                    } else {
                        placeView.setImage(null);
                    }
                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de base de données");
            alert.setHeaderText("Impossible de charger l'état des places");
            alert.setContentText("Une erreur est survenue lors de la connexion à la base de données : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            // Fermer les ressources dans le bloc finally pour s'assurer qu'elles sont toujours fermées
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*------------- SUPRIMER VOITURE ------------*/

    /* =================== LES METHODES DE MOTO ==========================*/

    @FXML
    private void handleAjouterMoto() {
        // ajouterVehicule("XY456ZW", "MOTO", 1); // Remplacez par les valeurs appropriées
    }


}