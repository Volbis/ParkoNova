package org.parko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HistoriqueDb {
    private final Connection connection;

    public HistoriqueDb() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void afficherHistorique() {
        try {
            String query = "CALL afficherHistorique();";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Historique :");
            while (resultSet.next()) {
                System.out.println("Immatriculation : " + resultSet.getString("immatriculation") +
                        " | Date d'entrée : " + resultSet.getTimestamp("dateEntree") +
                        " | Date de sortie : " + resultSet.getTimestamp("dateSortie") +
                        " | Montant : " + resultSet.getDouble("montant") + " FCFA");
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
