package org.parko.services;

import org.parko.classes.Statistique;
import org.parko.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class StatistiqueService {

    /* Deja definit dans DashBoardController */
/*
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
*/

    /* ========= LES ENTREES ET SORTIES par periodes ============= */
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

    /* ========= LES REVENUS PAR PERIODES ============= */
    public double getRevenusTotauxPeriode(LocalDate dateDebut, LocalDate dateFin, int parkingId) {
        double revenusTotaux = 0.0;
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT SUM(montant) as totalRevenus " +
                    "FROM EntreeSortie " +
                    "WHERE DATE(dateSortie) BETWEEN ? AND ? " +
                    "AND dateSortie IS NOT NULL ")) {
            
            stmt.setDate(1, Date.valueOf(dateDebut));
            stmt.setDate(2, Date.valueOf(dateFin));
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                revenusTotaux = rs.getDouble("totalRevenus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du calcul des revenus totaux: " + e.getMessage());
        }
        
        return revenusTotaux;
    }

    public Map<String, Double> getRevenusParHeure(LocalDate dateDebut, int parkingId) {
        Map<String, Double> revenusParHeure = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT HOUR(dateSortie) as heure, SUM(montant) as revenus " +
                            "FROM EntreeSortie " +
                            "WHERE DATE(dateSortie) = ? AND dateSortie IS NOT NULL " +  // Utiliser dateSortie au lieu de dateEntree
                            "GROUP BY HOUR(dateSortie) " +
                            "ORDER BY heure")) {

            stmt.setDate(1, Date.valueOf(dateDebut));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String heure = String.format("%02d:00", rs.getInt("heure"));
                revenusParHeure.put(heure, rs.getDouble("revenus"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenusParHeure;
    }

    public Map<LocalDate, Double> getRevenusParJour(LocalDate dateDebut, LocalDate dateFin, int parkingId) {
        Map<LocalDate, Double> revenusParJour = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DATE(dateSortie) as jour, SUM(montant) as revenus " +
                            "FROM EntreeSortie " +
                            "WHERE DATE(dateSortie) BETWEEN ? AND ? AND dateSortie IS NOT NULL " +
                            "GROUP BY DATE(dateSortie) " +
                            "ORDER BY jour")) {

            stmt.setDate(1, Date.valueOf(dateDebut));
            stmt.setDate(2, Date.valueOf(dateFin));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("jour").toLocalDate();
                revenusParJour.put(date, rs.getDouble("revenus"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Initialiser toutes les dates dans la période avec 0 pour éviter les valeurs null
        for (LocalDate date = dateDebut; !date.isAfter(dateFin); date = date.plusDays(1)) {
            if (!revenusParJour.containsKey(date)) {
                revenusParJour.put(date, 0.0);
            }
        }

        return revenusParJour;
    }

    // DUREE MOYENNE DE STATIONNEMENT
    public Map<String, Integer> getDureeStationnement(LocalDate dateDebut, LocalDate dateFin, int parkingId) {
        Map<String, Integer> dureeStationnement = new HashMap<>();
        
        // Initialiser toutes les catégories à 0
        dureeStationnement.put("< 1 heure", 0);
        dureeStationnement.put("1 - 3 heures", 0);
        dureeStationnement.put("3 - 6 heures", 0);
        dureeStationnement.put("> 6 heures", 0);
        
        try (Connection conn = DatabaseConnection.getConnection();
            CallableStatement stmt = conn.prepareCall("{CALL getDureeStationnement(?, ?, ?)}")) {
            
            stmt.setDate(1, Date.valueOf(dateDebut));
            stmt.setDate(2, Date.valueOf(dateFin));
            stmt.setInt(3, parkingId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String categorie = rs.getString("categorie");
                int nombre = rs.getInt("nombre");
                
                // Mettre à jour la catégorie correspondante
                dureeStationnement.put(categorie, nombre);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return dureeStationnement;
    }

}