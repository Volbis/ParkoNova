package org.parko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TarificationDb {
    private final Connection connection;

    public TarificationDb() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void ajouterTarification(String typeVehicule, double tarif) {
        try {
            String query = "CALL ajouterTarification(?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, typeVehicule);
            statement.setDouble(2, tarif);
            statement.executeUpdate();
            statement.close();
            System.out.println("Tarification ajoutée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierTarification(int tarificationId, double nouveauTarif) {
        try {
            String query = "CALL modifierTarification(?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, tarificationId);
            statement.setDouble(2, nouveauTarif);
            statement.executeUpdate();
            statement.close();
            System.out.println("Tarification modifiée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

