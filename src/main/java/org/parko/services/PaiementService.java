package org.parko.services;

import org.parko.classes.Paiement;
import org.parko.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaiementService {
    private final int parkingId; // ID du parking actuel

    public PaiementService(int parkingId) {
        this.parkingId = parkingId;
    }

    public double getRevenusPeriode(LocalDate debut, LocalDate fin) {
        double revenus = 0.0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT SUM(revenusTotal) as revenus " +
                             "FROM Statistiques " +
                             "WHERE date BETWEEN ? AND ? " +
                             "AND parking_id = ?")) {

            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));
            stmt.setInt(3, parkingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                revenus = rs.getDouble("revenus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenus;
    }

    public double getRevenusJour(LocalDate date) {
        double revenus = 0.0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT revenusTotal as revenus " +
                             "FROM Statistiques " +
                             "WHERE date = ? AND parking_id = ?")) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, parkingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                revenus = rs.getDouble("revenus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenus;
    }

    public List<Paiement> getPaiements(LocalDate debut, LocalDate fin) {
        List<Paiement> paiements = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT t.id, t.vehicule_immatriculation, t.montant, t.dateSortie " +
                             "FROM Ticket t " +
                             "JOIN EntreeSortie es ON t.vehicule_immatriculation = es.vehicule_immatriculation AND t.dateSortie = es.dateSortie " +
                             "WHERE DATE(t.dateSortie) BETWEEN ? AND ?")) {

            stmt.setDate(1, Date.valueOf(debut));
            stmt.setDate(2, Date.valueOf(fin));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Paiement p = new Paiement();
                p.setId(rs.getInt("id"));
                p.setImmatriculation(rs.getString("vehicule_immatriculation"));
                p.setMontant(rs.getDouble("montant"));
                p.setDatePaiement(rs.getTimestamp("dateSortie").toLocalDateTime());
                paiements.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paiements;
    }
}