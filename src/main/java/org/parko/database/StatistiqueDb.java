package org.parko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatistiqueDb {
    private final Connection connection;

    public StatistiqueDb () {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
        }
    }

    public void afficherStatistiques() {
        try {
            String query = "CALL afficherStatistiques();";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Statistiques :");
            while (resultSet.next()) {
                System.out.println("Type de véhicule : " + resultSet.getString("typeVehicule") +
                        " | Nombre : " + resultSet.getInt("nombre"));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

