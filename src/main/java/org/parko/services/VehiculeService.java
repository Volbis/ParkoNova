package org.parko.services;

import org.parko.classes.Vehicule;
import org.parko.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VehiculeService {
    private final int parkingId = 1; // ID du parking actuel

    public int getNombreVehiculesPresents() {
        int nombre = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) as nombre FROM Vehicule v " +
                             "JOIN Place p ON v.place_id = p.id " +
                             "WHERE p.parking_id = ?")) {

            stmt.setInt(1, parkingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombre = rs.getInt("nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombre;
    }

    public Map<String, Integer> getVehiculesParType(int parkingId) {
        Map<String, Integer> vehiculesParType = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
            CallableStatement stmt = conn.prepareCall("{CALL getVehiculeParType(?)}")) {
            
            stmt.setInt(1, parkingId);
            ResultSet rs = stmt.executeQuery();
            
            // Initialiser les types communs avec 0 pour qu'ils apparaissent même s'il n'y en a pas
            vehiculesParType.put("Voiture", 0);
            vehiculesParType.put("Moto", 0);
            
            while (rs.next()) {
                String type = rs.getString("type_vehicule");
                int nombre = rs.getInt("nombre");
                vehiculesParType.put(type, nombre);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return vehiculesParType;
    }

    // Mise à des dates précises
    public int getNombreVehiculesPresentsJour(LocalDate date) {
        int nombre = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT nbVehicules FROM Statistiques " +
                             "WHERE date = ? AND parking_id = ?")) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, parkingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombre = rs.getInt("nbVehicules");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombre;
    }

    public List<Vehicule> getVehiculesPresents() {
        List<Vehicule> vehicules = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL getVehiculesPresents(?)}")) {

            stmt.setInt(1, parkingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vehicule v = new Vehicule();
                v.setImmatriculation(rs.getString("immatriculation"));
                v.setType(rs.getString("type"));
                v.setDateEntree(rs.getTimestamp("dateEntree").toLocalDateTime());
                v.setNumeroPlace(rs.getInt("place_numero"));
                vehicules.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehicules;
    }

    public List<Vehicule> getVehiculesEntres(LocalDate debut, LocalDate fin) {
        List<Vehicule> vehicules = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT es.vehicule_immatriculation as immatriculation, " +
                             "       tv.libelle as type, " +
                             "       es.dateEntree, " +
                             "       es.dateSortie " +
                             "FROM EntreeSortie es " +
                             "JOIN Vehicule v ON es.vehicule_immatriculation = v.immatriculation " +
                             "JOIN TypeVehicule tv ON v.type_id = tv.id " +
                             "WHERE DATE(es.dateEntree) BETWEEN ? AND ?")) {

            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vehicule v = new Vehicule();
                v.setImmatriculation(rs.getString("immatriculation"));
                v.setType(rs.getString("type"));
                v.setDateEntree(rs.getTimestamp("dateEntree").toLocalDateTime());
                // La date de sortie peut être null
                Timestamp dateSortie = rs.getTimestamp("dateSortie");
                if (dateSortie != null) {
                    v.setDateSortie(dateSortie.toLocalDateTime());
                }
                vehicules.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehicules;
    }
/*
    public boolean ajouterVehicule(String immatriculation, String typeVehicule) {
        boolean success = false;
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL ajouterVehicule(?, ?, ?)}")) {

            stmt.setString(1, immatriculation);
            stmt.setString(2, typeVehicule);
            stmt.setInt(3, parkingId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String message = rs.getString("message");
                success = message.contains("succès");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public double sortirVehicule(String immatriculation) {
        double montant = 0.0;
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL sortirVehicule(?, ?)}")) {

            stmt.setString(1, immatriculation);
            stmt.setDouble(2, tarifHoraire);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                montant = rs.getDouble("montant");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return montant;
    }

 */
}