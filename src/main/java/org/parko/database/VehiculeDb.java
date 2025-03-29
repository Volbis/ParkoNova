package org.parko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import java.sql.ResultSetMetaData;


public class VehiculeDb {
    private Connection connection;

    public VehiculeDb() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean ajouterVehicule(String immatriculation, String type, int parkingId) {
        boolean succes = false;

        try {
            String query = "CALL ajouterVehicule(?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, immatriculation);
            statement.setString(2, type);
            statement.setInt(3, parkingId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Récupérer le message de réussite ou d'échec
                String message = resultSet.getString("message");
                System.out.println(message);
                succes = message != null && message.toLowerCase().contains("succès");
            }

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            // Afficher une alerte en cas d'erreur SQL
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de base de données");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ajout du véhicule : " + e.getMessage());
            alert.showAndWait();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return succes;
    }

    public boolean sortirVehicule(String immatriculation, double tarifHoraire) {
        boolean success = false;

        try {
            // Vérification de la connexion
            if (connection == null || connection.isClosed()) {
                connection = DatabaseConnection.getConnection();
            }

            // Appel de la procédure stockée
            String query = "CALL sortirVehicule(?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, immatriculation);
            statement.setDouble(2, tarifHoraire);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String message = resultSet.getString("message");
                double montant = resultSet.getDouble("montant");

                // Si le montant est positif, considérer l'opération comme réussie
                if (montant >= 0) {
                    success = true;

                    // Affichage du résultat dans une boîte de dialogue
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Véhicule retiré");
                    alert.setHeaderText("Opération réussie");
                    alert.setContentText(message + "\nMontant à payer : " + montant + " €");
                    alert.showAndWait();
                } else {
                    // Cas où la procédure retourne un message d'erreur
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Problème détecté");
                    alert.setHeaderText("Impossible de retirer le véhicule");
                    alert.setContentText(message);
                    alert.showAndWait();
                }
            } else {
                // Aucun résultat retourné par la procédure
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Aucun résultat reçu");
                alert.setContentText("La procédure n'a retourné aucun résultat.");
                alert.showAndWait();
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();

            // Afficher une alerte avec les détails de l'erreur SQL
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de base de données");
            alert.setHeaderText("Impossible de retirer le véhicule");

            // Message d'erreur détaillé pour faciliter le débogage
            String errorMessage = "Une erreur SQL s'est produite : " + e.getMessage();
            if (e.getSQLState() != null) {
                errorMessage += "\nCode d'état SQL : " + e.getSQLState();
            }
            if (e.getErrorCode() != 0) {
                errorMessage += "\nCode d'erreur : " + e.getErrorCode();
            }

            alert.setContentText(errorMessage);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();

            // Afficher une alerte avec les détails de l'erreur générale
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de retirer le véhicule");
            alert.setContentText("Une erreur inattendue s'est produite : " + e.getMessage());
            alert.showAndWait();
        }

        return success;
    }
}
