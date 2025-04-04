package org.parko.database;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import java.sql.ResultSetMetaData;


public class VehiculeDb implements Closeable {
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
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Vérification de la connexion
            if (connection == null || connection.isClosed()) {
                connection = DatabaseConnection.getConnection();
            }

            String query = "CALL ajouterVehicule(?, ?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, immatriculation);
            statement.setString(2, type);
            statement.setInt(3, parkingId);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Récupérer le message de réussite ou d'échec
                String message = resultSet.getString("message");
                System.out.println(message);
                succes = message != null && message.toLowerCase().contains("succès");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Afficher une alerte en cas d'erreur SQL
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de base de données");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ajout du véhicule : " + e.getMessage());
            alert.showAndWait();
        } finally {
            // Fermer les ressources dans le bon ordre
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                // Ne pas fermer la connexion ici pour assurer la cohérence entre les méthodes
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return succes;
    }

    public boolean sortirVehicule(String immatriculation, double tarifHoraire) {
        boolean success = false;
        String numeroTicket = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Vérification de la connexion
            if (connection == null || connection.isClosed()) {
                connection = DatabaseConnection.getConnection();
            }

            // Appel de la procédure stockée
            String query = "CALL sortirVehicule(?, ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, immatriculation);
            statement.setDouble(2, tarifHoraire);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String message = resultSet.getString("message");
                double montant = 0;
                
                // Vérifier si la colonne 'montant' existe avant de tenter de la lire
                try {
                    montant = resultSet.getDouble("montant");
                } catch (SQLException e) {
                    // Si la colonne n'existe pas, utiliser une valeur par défaut
                    System.out.println("Colonne 'montant' non présente dans les résultats");
                }
                
                // Vérifier si la colonne 'numeroTicket' existe
                try {
                    numeroTicket = resultSet.getString("numeroTicket");
                } catch (SQLException e) {
                    // Si la colonne n'existe pas, numeroTicket reste null
                    System.out.println("Colonne 'numeroTicket' non présente dans les résultats");
                }

                // Si le message contient "succès", considérer l'opération comme réussie
                if (message != null && message.toLowerCase().contains("succès")) {
                    success = true;

                    // Créer un ticket de sortie si disponible
                    if (numeroTicket != null) {
                        try {
                            TicketDb ticketDb = new TicketDb();
                            
                            // Affichage succinct du résultat
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Véhicule retiré");
                            alert.setHeaderText("Opération réussie");
                            alert.setContentText(message + "\nMontant à payer : " + montant + " FCFA");
                            alert.showAndWait();
                            
                            // Afficher le ticket dans une fenêtre séparée
                            ticketDb.afficherTicket(numeroTicket);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Erreur lors de l'affichage du ticket: " + e.getMessage());
                        }
                    } else {
                        // Cas où aucun ticket n'a été généré
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Véhicule retiré");
                        alert.setHeaderText("Opération réussie");
                        alert.setContentText(message + "\nMontant à payer : " + montant + " FCFA");
                        alert.showAndWait();
                    }
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
        } finally {
            // Fermer les ressources dans le bon ordre
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                // Ne pas fermer la connexion ici pour assurer la cohérence
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }
    
    // Ajout d'une méthode pour fermer la connexion lorsque l'objet n'est plus utilisé
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Cette méthode sera appelée automatiquement lors de la finalisation de l'objet
    @Override
    public void close() {
        closeConnection();
    }
}