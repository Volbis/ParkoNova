package org.parko.services;

import org.parko.classes.Statistique;
import org.parko.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiqueService {

    /* ========= LES ENTREES ET SORTIES par periodes ============= */

    // Entrées et sorties par type de véhicule et période
    public Map<String, Map<String, Integer>> getVehiculesParTypeEtPeriode(LocalDate dateDebut, LocalDate dateFin, int parkingId, String periode) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql;
            
            // Requête SQL différente selon la période
            switch (periode) {
                case "Aujourd'hui":
                    // Grouper par heure pour aujourd'hui
                    sql = "SELECT HOUR(es.dateEntree) AS point_temporel, tv.libelle AS type_vehicule, COUNT(*) AS nombre " +
                        "FROM EntreeSortie es " +
                        "JOIN Vehicule v ON es.vehicule_immatriculation = v.immatriculation " +
                        "JOIN TypeVehicule tv ON v.type_id = tv.id " +  // Jointure avec TypeVehicule
                        "JOIN Place p ON v.place_id = p.id " +  // Jointure avec Place pour obtenir le parking_id
                        "WHERE DATE(es.dateEntree) = ? AND p.parking_id = ? " +
                        "GROUP BY HOUR(es.dateEntree), tv.libelle " +
                        "ORDER BY point_temporel";
                    break;
                    
                case "Cette semaine":
                    // Grouper par jour pour la semaine
                    sql = "SELECT DAYOFWEEK(es.dateEntree) AS point_temporel, tv.libelle AS type_vehicule, COUNT(*) AS nombre " +
                        "FROM EntreeSortie es " +
                        "JOIN Vehicule v ON es.vehicule_immatriculation = v.immatriculation " +
                        "JOIN TypeVehicule tv ON v.type_id = tv.id " +  // Jointure avec TypeVehicule
                        "JOIN Place p ON v.place_id = p.id " +  // Jointure avec Place pour obtenir le parking_id
                        "WHERE es.dateEntree BETWEEN ? AND ? AND p.parking_id = ? " +
                        "GROUP BY DAYOFWEEK(es.dateEntree), tv.libelle " +
                        "ORDER BY point_temporel";
                    break;
                    
                case "Ce mois":
                    // Grouper par semaine pour le mois
                    sql = "SELECT FLOOR((DAYOFMONTH(es.dateEntree)-1)/7) + 1 AS point_temporel, tv.libelle AS type_vehicule, COUNT(*) AS nombre " +
                        "FROM EntreeSortie es " +
                        "JOIN Vehicule v ON es.vehicule_immatriculation = v.immatriculation " +
                        "JOIN TypeVehicule tv ON v.type_id = tv.id " +  // Jointure avec TypeVehicule
                        "JOIN Place p ON v.place_id = p.id " +  // Jointure avec Place pour obtenir le parking_id
                        "WHERE es.dateEntree BETWEEN ? AND ? AND p.parking_id = ? " +
                        "GROUP BY FLOOR((DAYOFMONTH(es.dateEntree)-1)/7) + 1, tv.libelle " +
                        "ORDER BY point_temporel";
                    break;
                    
                case "Cette année":
                    // Grouper par mois pour l'année
                    sql = "SELECT MONTH(es.dateEntree) AS point_temporel, tv.libelle AS type_vehicule, COUNT(*) AS nombre " +
                        "FROM EntreeSortie es " +
                        "JOIN Vehicule v ON es.vehicule_immatriculation = v.immatriculation " +
                        "JOIN TypeVehicule tv ON v.type_id = tv.id " +  // Jointure avec TypeVehicule
                        "JOIN Place p ON v.place_id = p.id " +  // Jointure avec Place pour obtenir le parking_id
                        "WHERE es.dateEntree BETWEEN ? AND ? AND p.parking_id = ? " +
                        "GROUP BY MONTH(es.dateEntree), tv.libelle " +
                        "ORDER BY point_temporel";
                    break;
                    
                default:
                    // Par défaut, grouper par jour
                    sql = "SELECT DATE_FORMAT(es.dateEntree, '%Y-%m-%d') AS point_temporel, tv.libelle AS type_vehicule, COUNT(*) AS nombre " +
                        "FROM EntreeSortie es " +
                        "JOIN Vehicule v ON es.vehicule_immatriculation = v.immatriculation " +
                        "JOIN TypeVehicule tv ON v.type_id = tv.id " +  // Jointure avec TypeVehicule
                        "JOIN Place p ON v.place_id = p.id " +  // Jointure avec Place pour obtenir le parking_id
                        "WHERE es.dateEntree BETWEEN ? AND ? AND p.parking_id = ? " +
                        "GROUP BY DATE_FORMAT(es.dateEntree, '%Y-%m-%d'), tv.libelle " +
                        "ORDER BY point_temporel";
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Ajout du débogage pour vérifier la requête SQL et les paramètres
                System.out.println("Requête SQL: " + sql);
                System.out.println("Date début: " + dateDebut);
                System.out.println("Date fin: " + dateFin);
                System.out.println("Parking ID: " + parkingId);
                
                // Paramétrer la requête selon la période
                if (periode.equals("Aujourd'hui")) {
                    stmt.setDate(1, java.sql.Date.valueOf(dateDebut));
                    stmt.setInt(2, parkingId);
                } else {
                    stmt.setDate(1, java.sql.Date.valueOf(dateDebut));
                    stmt.setDate(2, java.sql.Date.valueOf(dateFin));
                    stmt.setInt(3, parkingId);
                }
                
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    int pointTemporelRaw = rs.getInt("point_temporel");
                    String typeVehicule = rs.getString("type_vehicule");
                    int nombre = rs.getInt("nombre");
                    
                    System.out.println("Résultat: Point=" + pointTemporelRaw + ", Type=" + typeVehicule + ", Nombre=" + nombre);
                    
                    // Formater le point temporel selon la période
                    String pointTemporel;
                    if (periode.equals("Aujourd'hui")) {
                        pointTemporel = String.format("%02d:00", pointTemporelRaw);
                    } else if (periode.equals("Cette semaine")) {
                        // Convertir le numéro du jour (1-7) en nom du jour (Dimanche à Samedi en SQL, donc +1 pour avoir Lundi à Dimanche)
                        String[] joursEnFrancais = {"Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
                        pointTemporel = joursEnFrancais[pointTemporelRaw - 1];
                    } else if (periode.equals("Ce mois")) {
                        pointTemporel = "Semaine " + pointTemporelRaw;
                    } else if (periode.equals("Cette année")) {
                        String[] moisEnFrancais = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul", "Août", "Sep", "Oct", "Nov", "Déc"};
                        pointTemporel = moisEnFrancais[pointTemporelRaw - 1];
                    } else {
                        pointTemporel = rs.getString("point_temporel");
                    }
                    
                    // Ajouter ou mettre à jour l'entrée dans notre Map résultat
                    if (!result.containsKey(pointTemporel)) {
                        result.put(pointTemporel, new HashMap<>());
                    }
                    
                    Map<String, Integer> vehiculesTypeMap = result.get(pointTemporel);
                    vehiculesTypeMap.put(typeVehicule, nombre);
                }
                
                // Assurez-vous que pour chaque point temporel, tous les types de véhicules sont représentés
                List<String> pointsTemporels = new ArrayList<>(result.keySet());
                for (String pointTemporel : pointsTemporels) {
                    Map<String, Integer> vehiculesTypeMap = result.get(pointTemporel);
                    if (!vehiculesTypeMap.containsKey("VOITURE")) {
                        vehiculesTypeMap.put("VOITURE", 0);
                    }
                    if (!vehiculesTypeMap.containsKey("MOTO")) {
                        vehiculesTypeMap.put("MOTO", 0);
                    }
                }
                
                // Pour la période "Aujourd'hui", s'assurer que toutes les heures sont représentées
                if (periode.equals("Aujourd'hui")) {
                    for (int heure = 8; heure <= 20; heure += 2) {
                        String heureStr = String.format("%02d:00", heure);
                        if (!result.containsKey(heureStr)) {
                            Map<String, Integer> emptyMap = new HashMap<>();
                            emptyMap.put("VOITURE", 0);
                            emptyMap.put("MOTO", 0);
                            result.put(heureStr, emptyMap);
                        }
                    }
                }
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des véhicules par type et période: " + e.getMessage());
        }
        
        System.out.println("Résultat final: " + result);
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