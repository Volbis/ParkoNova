package org.parko.services;

import org.parko.database.DatabaseConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParkingService {
    public int getPlacesDisponibles(int parkingId) {
        int placesDisponibles = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
            CallableStatement stmt = conn.prepareCall("{CALL getPlacesDisponibles(?)}")) {
            
            stmt.setInt(1, parkingId);
            boolean hasResults = stmt.execute();
            
            if (hasResults) {
                ResultSet rs = stmt.getResultSet();
                // Compter le nombre de lignes dans le resultSet (une ligne = une place disponible)
                while (rs.next()) {
                    placesDisponibles++;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'appel à la procédure getPlacesDisponibles: " + e.getMessage());
        }
        
        return placesDisponibles;
    }

    public int getCapaciteTotal(int parkingId) {
        int capaciteTotal = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                    "SELECT capaciteTotal FROM Parking WHERE id = ?")) {
                
            stmt.setInt(1, parkingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                capaciteTotal = rs.getInt("capaciteTotal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération de la capacité totale du parking: " + e.getMessage());
        }
        
        return capaciteTotal;
}

}
