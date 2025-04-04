package org.parko.database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.parko.controllers.parkingSet.TicketViewController;
import org.parko.Main; // Assurez-vous d'avoir une classe App avec les ressources

public class TicketDb implements Closeable {
    private Connection connection;

    public TicketDb() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void afficherTicket(String numeroTicket) {
        String ticketContent = imprimerTicket(numeroTicket);
        
        if (ticketContent != null && !ticketContent.isEmpty()) {
            try {
                // Charger la vue FXML
                FXMLLoader loader = new FXMLLoader(TicketDb.class.getResource("/InterfaceUtilisateur/tickets/TicketCli.fxml"));
                Parent root = loader.load();
                
                // Configurer le contrôleur
                TicketViewController controller = loader.getController();
                
                // Créer une nouvelle fenêtre
                Stage ticketStage = new Stage();
                ticketStage.setTitle("Ticket de Parking - " + numeroTicket);
                ticketStage.initModality(Modality.APPLICATION_MODAL);
                
                // Initialiser le contrôleur avec les données du ticket
                controller.initialize(ticketContent, numeroTicket, ticketStage);
                
                // Afficher la fenêtre
                Scene scene = new Scene(root);
                ticketStage.setScene(scene);
                ticketStage.show();
                
            } catch (IOException e) {
                e.printStackTrace();
                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Impossible d'afficher le ticket");
                alert.setContentText("Une erreur s'est produite : " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun contenu de ticket");
            alert.setContentText("Le ticket n° " + numeroTicket + " n'a pas pu être récupéré ou est vide.");
            alert.showAndWait();
        }
    }


    public String imprimerTicket(String numeroTicket) {
        String ticketContent = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            // Vérification de la connexion
            if (connection == null || connection.isClosed()) {
                connection = DatabaseConnection.getConnection();
            }

            // Appel de la procédure stockée
            String query = "CALL imprimerTicket(?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, numeroTicket);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                ticketContent = resultSet.getString("ticket_format");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            
            // Afficher une alerte en cas d'erreur SQL
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de base de données");
            alert.setHeaderText("Impossible d'imprimer le ticket");
            alert.setContentText("Une erreur SQL s'est produite : " + e.getMessage());
            alert.showAndWait();
        } finally {
            // Fermer les ressources dans le bon ordre
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                // Ne pas fermer la connexion ici
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return ticketContent;
    }

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