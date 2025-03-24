package org.parko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationsDb {
    private final Connection connection;

    public ReservationsDb () {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void afficherPlacesDisponibles(int parkingId) {
        try {
            String query = "CALL getPlacesDisponibles(?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, parkingId);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Places disponibles :");
            while (resultSet.next()) {
                System.out.println("Numéro : " + resultSet.getInt("numero") + " | Type : " + resultSet.getString("type"));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
