package org.parko.database;

// Elle permet d'exécuter les procédures stockées initialiserParking et getStatistiques

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParkingDb {
    private final Connection connection;

    public ParkingDb() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
        }
    }

    public void initialiserParking(String nom, int nbPlacesStandard, int nbPlacesMoto) {
        try {
            String query = "CALL initialiserParking(?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nom);
            statement.setInt(2, nbPlacesStandard);
            statement.setInt(3, nbPlacesMoto);
            statement.executeUpdate();
            statement.close();
            System.out.println("Parking initialisé avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean estPlaceDisponible(int parkingId, String typePlace) {
        boolean disponible = false;
        try {
            String query = """
                SELECT COUNT(*) AS places_disponibles
                FROM Place p
                JOIN TypePlace tp ON p.type_id = tp.id
                WHERE p.occupee = FALSE AND p.parking_id = ? AND tp.libelle = ?
                """;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, parkingId);
            statement.setString(2, typePlace);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int placesDisponibles = resultSet.getInt("places_disponibles");
                disponible = placesDisponibles > 0;
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return disponible;
    }

    public ResultSet getStatistiques(String date, int parkingId) {
        try {
            String query = "CALL getStatistiques(?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, date);
            statement.setInt(2, parkingId);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
