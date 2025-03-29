package org.parko.services;

import org.parko.classes.Statistique;
import org.parko.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class StatistiqueService {
    private final int parkingId = 1; // ID du parking actuel


    public int getPlacesTotales() {
        int places = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT capaciteTotal FROM Parking WHERE id = ?")) {

            stmt.setInt(1, parkingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                places = rs.getInt("capaciteTotal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return places;
    }

    // Retourne un objet stats qui contient la date, le nbVehicules, revenusTotal, frequentation
    public Statistique getStatistiquesDuJour(LocalDate date) {
        Statistique stats = new Statistique();
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL getStatistiques(?, ?)}")) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, parkingId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats.setDate(rs.getDate("date").toLocalDate());
                stats.setNbVehicules(rs.getInt("nbVehicules"));
                stats.setRevenusTotal(rs.getDouble("revenusTotal"));
                stats.setFrequentation(rs.getInt("frequentation"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Taux d'occupations
    public double getTauxOccupationJour(LocalDate date) {
        Statistique stats = getStatistiquesDuJour(date);
        int placesTotal = getPlacesTotales();

        if (placesTotal == 0) return 0;
        return (double) stats.getNbVehicules() / placesTotal * 100;
    }

    // Entrées par heures
    public Map<String, Integer> getEntreesParHeure(LocalDate date, int parkingId) {
        Map<String, Integer> entreesParHeure = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT HOUR(dateEntree) as heure, COUNT(*) as nombre " +
                             "FROM EntreeSortie " +
                             "WHERE DATE(dateEntree) = ? " +
                             "GROUP BY HOUR(dateEntree) " +
                             "ORDER BY heure")) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String heure = String.format("%02d:00", rs.getInt("heure"));
                entreesParHeure.put(heure, rs.getInt("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entreesParHeure;
    }

    // Entrées par jour
    public int getEntreesJour(LocalDate date) {
        int entrees = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) as nombre FROM EntreeSortie WHERE DATE(dateEntree) = ?")) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                entrees = rs.getInt("nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entrees;
    }
    // Entrées par mois
    public Map<YearMonth, Integer> getEntreesParMois(LocalDate debut, LocalDate fin) {
        Map<YearMonth, Integer> entreesParMois = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT YEAR(dateEntree) as annee, MONTH(dateEntree) as mois, COUNT(*) as nombre " +
                             "FROM EntreeSortie " +
                             "WHERE DATE(dateEntree) BETWEEN ? AND ? " +
                             "GROUP BY YEAR(dateEntree), MONTH(dateEntree) " +
                             "ORDER BY annee, mois")) {

            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                YearMonth yearMonth = YearMonth.of(rs.getInt("annee"), rs.getInt("mois"));
                entreesParMois.put(yearMonth, rs.getInt("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entreesParMois;
    }

    /* Entrées par période */
    // Ajouter une méthode dans StatistiqueService qui retourne une Map
    public Map<LocalDate, Integer> getEntreesParPeriode(LocalDate dateDebut, LocalDate dateFin) {
        Map<LocalDate, Integer> resultMap = new HashMap<>();
        for (LocalDate date = dateDebut; !date.isAfter(dateFin); date = date.plusDays(1)) {
            resultMap.put(date, getEntreesJour(date));
        }
        return resultMap;
    }

    public double getRevenusTotauxPeriode(LocalDate dateDebut, LocalDate dateFin, int parkingId) {
        /* Méthode à implémenter */
        return 0;
    }

    public Map<String, Double> getRevenusParHeure(LocalDate dateDebut, int parkingId) {
        /* Méthode à implémenter */
        return null;
    }

    public Map<LocalDate, Double> getRevenusParJour(LocalDate dateDebut, LocalDate dateFin, int parkingId) {
        /* Méthode à implémenter */
        return null;
    }
}