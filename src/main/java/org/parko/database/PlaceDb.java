package org.parko.database;

//  Elle permet d'exécuter les procédures ajouterPlace et supprimerPlace.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlaceDb {
    private final Connection connection;

    public PlaceDb() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
        }
    }

    public void ajouterPlace(int parkingId, String typePlace) {
        try {
            String query = "CALL ajouterPlace(?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, parkingId);
            statement.setString(2, typePlace);
            statement.executeUpdate();
            statement.close();
            System.out.println("Place ajoutée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerPlace(int placeId) {
        try {
            String query = "CALL supprimerPlace(?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, placeId);
            statement.executeUpdate();
            statement.close();
            System.out.println("Place supprimée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
