package org.parko.services;

import org.parko.classes.Statistique;
import org.parko.database.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class StatistiqueService {

    /* ========= LES ENTREES ET SORTIES par periodes ============= */

    public Map<String, Map<String, Integer>> getDureeStationnementParType(LocalDate dateDebut, LocalDate dateFin) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT " +
                "CASE " +
                "  WHEN TIMESTAMPDIFF(MINUTE, es.dateEntree, es.dateSortie) < 60 THEN '< 1 heure' " +
                "  WHEN TIMESTAMPDIFF(MINUTE, es.dateEntree, es.dateSortie) BETWEEN 60 AND 180 THEN '1 - 3 heures' " +
                "  WHEN TIMESTAMPDIFF(MINUTE, es.dateEntree, es.dateSortie) BETWEEN 181 AND 360 THEN '3 - 6 heures' " +
                "  ELSE '> 6 heures' " +
                "END AS categorie, " +
                "tv.libelle AS type_vehicule, " +
                "COUNT(*) AS nombre " +
                "FROM EntreeSortie es " +
                "JOIN TypeVehicule tv ON es.type_vehicule_id = tv.id " +
                "WHERE DATE(es.dateEntree) BETWEEN ? AND ? " +
                "AND es.dateSortie IS NOT NULL " +
                "GROUP BY categorie, tv.libelle " +
                "ORDER BY categorie";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(dateDebut));
                stmt.setDate(2, Date.valueOf(dateFin));
                
                ResultSet rs = stmt.executeQuery();
                
                // Initialiser les catégories
                String[] categories = {"< 1 heure", "1 - 3 heures", "3 - 6 heures", "> 6 heures"};
                String[] types = {"VOITURE", "MOTO"};
                
                // Initialiser la structure avec des zéros
                for (String categorie : categories) {
                    HashMap<String, Integer> typeMap = new HashMap<>();
                    for (String type : types) {
                        typeMap.put(type, 0);
                    }
                    result.put(categorie, typeMap);
                }
                
                // Remplir avec les données
                while (rs.next()) {
                    String categorie = rs.getString("categorie");
                    String typeVehicule = rs.getString("type_vehicule");
                    int nombre = rs.getInt("nombre");
                    
                    result.get(categorie).put(typeVehicule, nombre);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des durées de stationnement: " + e.getMessage());
        }
        
        return result;
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
        Map<String, Integer> result = new HashMap<>();

        // Initialiser les catégories avec des zéros
        result.put("< 1 heure", 0);
        result.put("1 - 3 heures", 0);
        result.put("3 - 6 heures", 0);
        result.put("> 6 heures", 0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT " +
                "CASE " +
                "  WHEN TIMESTAMPDIFF(MINUTE, es.dateEntree, es.dateSortie) < 60 THEN '< 1 heure' " +
                "  WHEN TIMESTAMPDIFF(MINUTE, es.dateEntree, es.dateSortie) BETWEEN 60 AND 180 THEN '1 - 3 heures' " +
                "  WHEN TIMESTAMPDIFF(MINUTE, es.dateEntree, es.dateSortie) BETWEEN 181 AND 360 THEN '3 - 6 heures' " +
                "  ELSE '> 6 heures' " +
                "END AS categorie, " +
                "COUNT(*) AS nombre " +
                "FROM EntreeSortie es " +
                "WHERE DATE(es.dateSortie) BETWEEN ? AND ? " +  // Utiliser dateSortie au lieu de dateEntree
                "AND es.dateSortie IS NOT NULL " +
                "GROUP BY categorie " +
                "ORDER BY categorie";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(dateDebut));
                stmt.setDate(2, Date.valueOf(dateFin));

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String categorie = rs.getString("categorie");
                    int nombre = rs.getInt("nombre");

                    result.put(categorie, nombre);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des durées de stationnement: " + e.getMessage());
        }

        return result;
    }

    public List<Map<String, Object>> getStatistiqueTable(LocalDate startDate, LocalDate endDate, int parkingId) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // Requête corrigée selon la structure réelle de la base de données
        String sql = """
                SELECT 
                    es.dateEntree as date_entree, 
                    es.dateSortie as date_sortie, 
                    es.vehicule_immatriculation as immatriculation, 
                    tv.libelle as type_vehicule,
                    es.montant
                FROM 
                    EntreeSortie es
                JOIN 
                    TypeVehicule tv ON es.type_vehicule_id = tv.id
                WHERE 
                    DATE(es.dateEntree) BETWEEN ? AND ?
                ORDER BY 
                    es.dateEntree DESC
                """;
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Paramètres de la requête
            pstmt.setDate(1, java.sql.Date.valueOf(startDate));
            pstmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    
                    Timestamp dateEntree = rs.getTimestamp("date_entree");
                    Timestamp dateSortie = rs.getTimestamp("date_sortie");
                    
                    row.put("dateEntree", dateEntree);
                    row.put("dateSortie", dateSortie);
                    row.put("immatriculation", rs.getString("immatriculation"));
                    row.put("typeVehicule", rs.getString("type_vehicule"));
                    
                    // Ajouter le montant s'il est disponible
                    BigDecimal montant = rs.getBigDecimal("montant");
                    if (montant != null) {
                        row.put("montant", montant.toString() + " €");
                    } else {
                        row.put("montant", "N/A");
                    }
                    
                    // Calculer la durée de stationnement
                    String dureeStr = "En cours";
                    if (dateSortie != null) {
                        long differenceMillis = dateSortie.getTime() - dateEntree.getTime();
                        long minutes = differenceMillis / (60 * 1000);
                        dureeStr = String.format("%dh %02dmin", minutes / 60, minutes % 60);
                    }
                    row.put("dureeStationnement", dureeStr);
                    
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des données de stationnement: " + e.getMessage());
        }
        
        return results;
    }

    /**
 * Récupère le nombre de véhicules stationnés par type et par date
 * @return Map avec les dates comme clés et une Map de types de véhicules comme valeurs
 */
public Map<LocalDate, Map<String, Integer>> getStationnementParTypeEtDate(LocalDate startDate, LocalDate endDate, int parkingId) {
    Map<LocalDate, Map<String, Integer>> result = new LinkedHashMap<>();
    
    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT " +
                       "DATE(es.dateEntree) AS date, " +
                       "tv.libelle AS type_vehicule, " +
                       "COUNT(*) AS nombre " +
                       "FROM EntreeSortie es " +
                       "JOIN TypeVehicule tv ON es.type_vehicule_id = tv.id " +
                       "WHERE DATE(es.dateEntree) BETWEEN ? AND ? " +
                       "GROUP BY DATE(es.dateEntree), tv.libelle " +
                       "ORDER BY DATE(es.dateEntree)";
        
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setDate(1, java.sql.Date.valueOf(startDate));
        stmt.setDate(2, java.sql.Date.valueOf(endDate));
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            LocalDate date = rs.getDate("date").toLocalDate();
            String typeVehicule = rs.getString("type_vehicule");
            int nombre = rs.getInt("nombre");
            
            // Initialiser la Map pour cette date si elle n'existe pas
            if (!result.containsKey(date)) {
                result.put(date, new HashMap<>());
            }
            
            // Ajouter le nombre pour ce type de véhicule à cette date
            result.get(date).put(typeVehicule, nombre);
        }
        
        // Assurer que toutes les dates entre startDate et endDate sont présentes
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (!result.containsKey(currentDate)) {
                result.put(currentDate, new HashMap<>());
            }
            
            // S'assurer que chaque type a une entrée (même si c'est 0)
            Map<String, Integer> typesForDate = result.get(currentDate);
            if (!typesForDate.containsKey("VOITURE")) {
                typesForDate.put("VOITURE", 0);
            }
            if (!typesForDate.containsKey("MOTO")) {
                typesForDate.put("MOTO", 0);
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Erreur lors de la récupération des statistiques de stationnement: " + e.getMessage());
    }
    
    return result;
}
}