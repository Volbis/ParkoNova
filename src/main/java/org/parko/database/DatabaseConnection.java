package org.parko.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/parkodb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion réussie à la base de données");
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
                throw e;
            }
        } else {
            // Vérifier si la connexion est toujours valide
            try {
                if (!connection.isValid(2)) { // Timeout de 2 secondes
                    connection.close();
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Reconnexion réussie à la base de données");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la vérification de la connexion: " + e.getMessage());
                connection = null;
                throw e;
            }
        }
        return connection;
    }

    // Méthode pour fermer la connexion
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion fermée");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
