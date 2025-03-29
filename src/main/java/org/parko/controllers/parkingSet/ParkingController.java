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
    private Button btnSortirMoto;

    @FXML
    private Button btnSortirVoiture;

    @FXML
    private Button btnAjouterVoiture;

    // Les labels à mettre à jour pour les places de motos et de voitures
    @FXML
    private Label lblPlacesMotos;

    @FXML
    private Label lblPlacesVoitures;


    // Les places de voitures
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


    // ============ Les places de motos ===================

    @FXML
    private ImageView placem_1;
    @FXML
    private ImageView placem_2;
    @FXML
    private ImageView placem_3;
    @FXML
    private ImageView placem_4;
    @FXML
    private ImageView placem_5;
    @FXML
    private ImageView placem_6;
    @FXML
    private ImageView placem_7;
    @FXML
    private ImageView placem_8;
    @FXML
    private ImageView placem_9;
    @FXML
    private ImageView placem_10;
    @FXML
    private ImageView placem_11;
    @FXML
    private ImageView placem_12;
    @FXML
    private ImageView placem_13;
    @FXML
    private ImageView placem_14;
    @FXML
    private ImageView placem_15;
    @FXML
    private ImageView placem_16;
    @FXML
    private ImageView placem_17;
    @FXML
    private ImageView placem_18;
    @FXML
    private ImageView placem_19;
    @FXML
    private ImageView placem_20;


    public void initialize() {
        if (btnAjouterMoto != null) {
            btnAjouterMoto.setOnAction(event -> loadAjouterMoto());
        }

        if (btnSortirMoto != null) {
            btnSortirMoto.setOnAction(event -> loadSortirMoto());
        }

        if (btnAjouterVoiture != null) {
            btnAjouterVoiture.setOnAction(event -> loadAjouterVoiture());
        }

        if (btnSortirVoiture != null) {
            btnSortirVoiture.setOnAction(event -> loadSortirVoiture ());
        }


        chargerEtatPlacesDepuisDB();
        chargerEtatPlacesDepuisDBMotos();
        //initialiserImagesVoitures();

        updateMotorcyclePlacesLabel();
        updateCarPlacesLabel();

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
                    updateCarPlacesLabel();
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
                    updateCarPlacesLabel();
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
    // Méthode pour charger les images des voitures
    private Map<Integer, Image> placeImagesMap = new HashMap<>();

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

    // Variable de classe pour suivre l'index de la prochaine image à afficher
    private int currentImageIndex = 0;

    private Image getNextVoitureImage() {
        if (voitureImages == null) {
            initialiserImagesVoitures();
        }
        // Vérifier que la liste n'est pas vide
        if (voitureImages.isEmpty()) {
            return null;
        }
        // Sélectionner l'image selon l'index actuel
        Image selectedImage = voitureImages.get(currentImageIndex);

        // Incrémenter l'index pour la prochaine fois
        currentImageIndex = (currentImageIndex + 1) % voitureImages.size();

        return selectedImage;
    }

    public void chargerEtatPlacesDepuisDB() {
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
          connection = DatabaseConnection.getConnection();
        String query = "SELECT id, occupee FROM place WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)";
        statement = connection.prepareStatement(query);
        resultSet = statement.executeQuery();

        // Créer une liste temporaire pour suivre les places actuellement occupées
        List<Integer> placesActuellementOccupees = new ArrayList<>();

        while (resultSet.next()) {
            int placeId = resultSet.getInt("id");
            int estOccupee = resultSet.getInt("occupee");
            
            // Ajouter à la liste des places occupées
            if (estOccupee == 1) {
                placesActuellementOccupees.add(placeId);
            }

            ImageView placeView = placesMap.get(placeId);
            if (placeView != null) {
                if (estOccupee == 1) {
                    // Vérifier si cette place a déjà une image attribuée
                    if (!placeImagesMap.containsKey(placeId)) {
                        // Seulement si c'est une nouvelle occupation, attribuer une nouvelle image
                        Image voitureImage = getNextVoitureImage();
                        placeImagesMap.put(placeId, voitureImage);
                    }
                    // Utiliser l'image mémorisée
                    placeView.setImage(placeImagesMap.get(placeId));
                } else {
                    // Place libre
                    placeView.setImage(null);
                    // Retirer l'image mémorisée si elle existe
                    placeImagesMap.remove(placeId);
                }
            }
        }

        // Nettoyer les images des places qui ne sont plus occupées
        // (Si une place était occupée avant mais ne l'est plus)
        List<Integer> placesANettoyer = new ArrayList<>(placeImagesMap.keySet());
        placesANettoyer.removeAll(placesActuellementOccupees);
        for (Integer placeId : placesANettoyer) {
            placeImagesMap.remove(placeId);
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
        updateCarPlacesLabel();
    }

    public void updateCarPlacesLabel() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT " +
                        "SUM(CASE WHEN occupee = TRUE THEN 1 ELSE 0 END) AS places_occupees, " +
                        "COUNT(*) AS places_totales " +
                        "FROM Place WHERE id BETWEEN 1 AND 20";
            
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int placesOccupees = resultSet.getInt("places_occupees");
        
                // Mise à jour du texte du label avec le nombre de places occupées
                lblPlacesVoitures.setText(String.valueOf(placesOccupees));
            } else {
                // Texte par défaut si pas de résultats
                lblPlacesVoitures.setText("0");
            }
            
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de base de données");
            alert.setHeaderText("Impossible de charger le nombre de places de voitures");
            alert.setContentText("Une erreur est survenue : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            // Fermeture des ressources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
        

    /* =================== LES METHODES DE MOTO ==========================*/

    // Connexion aux formulaires déjà faites
    // Configure maintenant ce fichier pour charger le formulaire
    private void loadAjouterMoto() {
        try {
            // Chemin vers le fichier FXML du formulaire d'ajout de voiture
            String fxmlPath = "/InterfaceUtilisateur/formAddMoto.fxml";
            if (getClass().getResource(fxmlPath) == null) {
                throw new IOException("Le fichier FXML n'a pas été trouvé: " + fxmlPath);
            }

            // Récupérer la fenêtre actuelle pour l'utiliser comme parent
            Stage currentStage = (Stage) btnAjouterMoto.getScene().getWindow();

            // Créer et configurer la boîte de dialogue
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(currentStage);
            dialogStage.setTitle("Ajouter une voiture");

            // Charger le FXML et obtenir le contrôleur
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            FormAddMotoController controller = loader.getController();

            // Configurer la scène
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Configurer la mise à jour en temps réel
            dialogStage.setOnHidden(event -> {
                // Vérifier si l'enregistrement a été effectué avec succès
                if (controller.isEnregistrementReussi()) {
                    // Recharger l'état des places depuis la base de données
                    chargerEtatPlacesDepuisDBMotos();
                    updateMotorcyclePlacesLabel();
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

    //      2. loadSortirMoto
    private void loadSortirMoto() {
        try {
            // Chemin vers le fichier FXML du formulaire de sortie de voiture
            String fxmlPath = "/InterfaceUtilisateur/formRemoveMoto.fxml";
            if (getClass().getResource(fxmlPath) == null) {
                throw new IOException("Le fichier FXML n'a pas été trouvé: " + fxmlPath);
            }

            // Configuration de la fenêtre modale
            Stage currentStage = (Stage) btnSortirMoto.getScene().getWindow();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(currentStage);
            dialogStage.setTitle("Sortir une voiture");

            // Chargement du FXML et récupération du contrôleur
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            FormRemoveMotoController controller = loader.getController();

            // Configuration de la scène
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Configurer la mise à jour en temps réel
            dialogStage.setOnHidden(event -> {
                // Vérifier si la suppression a été effectuée avec succès
                if (controller.estSupprimé()) {
                    // Recharger l'état des places depuis la base de données
                    chargerEtatPlacesDepuisDBMotos();
                    updateMotorcyclePlacesLabel();
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

    /* AJOUT DE L'ICON DE MOTO DANS LE PERKING */

    // Utiliser une Map pour associer les numéros de place aux ImageView
    private Map<Integer, ImageView> placesMapMoto = new HashMap<>();
    // Déclaration de la variable membre pour stocker les images
    private List<Image> MotoImages;
    // Méthode pour charger les images des voitures
    private Map<Integer, Image> placeImagesMapMoto = new HashMap<>();

    private void initialiserImagesMotos() {
        MotoImages = new ArrayList<>();

        // Chargement des images (ajustez le nombre selon vos fichiers disponibles)
        for (int i = 0; i <= 5; i++) {
            String imagePath = "/vehicule/Moto/moto" + i + ".png";
            try {
                Image voitureImage = new Image(getClass().getResourceAsStream(imagePath));
                MotoImages.add(voitureImage);
            } catch (Exception e) {
                System.err.println("Impossible de charger l'image: " + imagePath);
            }
        }

        // Vérification qu'au moins une image a été chargée
        if (MotoImages.isEmpty()) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/vehicule/Moto/moto0.png"));
                MotoImages.add(defaultImage);
            } catch (Exception e) {
                System.err.println("Impossible de charger l'image par défaut");
            }
        }
    }

    // Variable de classe pour suivre l'index de la prochaine image à afficher
    private int currentImageIndexMoto = 0;

    private Image getNextMotoImage() {
        if (MotoImages == null) {
            initialiserImagesMotos();
        }
        // Vérifier que la liste n'est pas vide
        if (MotoImages.isEmpty()) {
            return null;
        }
        // Sélectionner l'image selon l'index actuel
        Image selectedImage = MotoImages.get(currentImageIndexMoto);

        // Incrémenter l'index pour la prochaine fois
        currentImageIndexMoto = (currentImageIndexMoto + 1) % MotoImages.size();

        return selectedImage;
    }

    public void chargerEtatPlacesDepuisDBMotos() {
        // Initialisation de la map des places de motos avec leurs propres ImageView
        placesMapMoto.put(21, placem_1);
        placesMapMoto.put(22, placem_2);
        placesMapMoto.put(23, placem_3);
        placesMapMoto.put(24, placem_4);
        placesMapMoto.put(25, placem_5);
        placesMapMoto.put(26, placem_6);
        placesMapMoto.put(27, placem_7);
        placesMapMoto.put(28, placem_8);
        placesMapMoto.put(29, placem_9);
        placesMapMoto.put(30, placem_10);
        placesMapMoto.put(31, placem_11);
        placesMapMoto.put(32, placem_12);
        placesMapMoto.put(33, placem_13);
        placesMapMoto.put(34, placem_14);
        placesMapMoto.put(35, placem_15);
        placesMapMoto.put(36, placem_16);
        placesMapMoto.put(37, placem_17);
        placesMapMoto.put(38, placem_18);
        placesMapMoto.put(39, placem_19);
        placesMapMoto.put(40, placem_20);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        // Connexion à la base de données et vérification de l'état des places
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT id, occupee FROM place WHERE id IN (21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40)";            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            // Créer une liste temporaire pour suivre les places actuellement occupées
            List<Integer> placesActuellementOccupees = new ArrayList<>();

            while (resultSet.next()) {
                int placeId = resultSet.getInt("id");
                int estOccupee = resultSet.getInt("occupee");

                // Ajouter à la liste des places occupées
                if (estOccupee == 1) {
                    placesActuellementOccupees.add(placeId);
                }

                ImageView placeView = placesMapMoto.get(placeId);
                if (placeView != null) {
                    if (estOccupee == 1) {
                        // Vérifier si cette place a déjà une image attribuée
                        if (!placeImagesMapMoto.containsKey(placeId)) {
                            // Seulement si c'est une nouvelle occupation, attribuer une nouvelle image
                            Image motoImage = getNextMotoImage();
                            placeImagesMapMoto.put(placeId, motoImage);
                        }
                        // Utiliser l'image mémorisée
                        placeView.setImage(placeImagesMapMoto.get(placeId));
                    } else {
                        // Place libre
                        placeView.setImage(null);
                        // Retirer l'image mémorisée si elle existe
                        placeImagesMapMoto.remove(placeId);
                    }
                }
            }

            // Nettoyer les images des places qui ne sont plus occupées
            List<Integer> placesANettoyer = new ArrayList<>(placeImagesMapMoto.keySet());
            placesANettoyer.removeAll(placesActuellementOccupees);
            for (Integer placeId : placesANettoyer) {
                placeImagesMapMoto.remove(placeId);
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de base de données");
            alert.setHeaderText("Impossible de charger l'état des places");
            alert.setContentText("Une erreur est survenue lors de la connexion à la base de données : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        updateMotorcyclePlacesLabel();
    }

    // Mise à jour du label pour les places de motos
    public void updateMotorcyclePlacesLabel() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            
            String query = "SELECT " +
                        "SUM(CASE WHEN occupee = TRUE THEN 1 ELSE 0 END) AS places_occupees, " +
                        "COUNT(*) AS places_totales " +
                        "FROM place WHERE id BETWEEN 21 AND 40";
            
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int placesOccupees = resultSet.getInt("places_occupees");
                //int placesTotales = resultSet.getInt("places_totales");
                
                // Mise à jour du texte du label
                lblPlacesMotos.setText(String.valueOf(placesOccupees));
            } else {
                // Texte par défaut si pas de résultats
                lblPlacesMotos.setText("0");
            }
            
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de base de données");
            alert.setHeaderText("Impossible de charger le nombre de places de motos");
            alert.setContentText("Une erreur est survenue : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            // Fermeture des ressources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}