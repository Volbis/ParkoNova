package org.parko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketDb {
    private final Connection connection;

    public TicketDb() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void genererTicket(String immatriculation) {
        try {
            String query = "CALL genererTicket(?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, immatriculation);
            statement.executeUpdate();
            System.out.println("Ticket généré pour le véhicule : " + immatriculation);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
