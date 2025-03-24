package org.parko.exception;

public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message) {
        super("Erreur de connexion à la base de données : " + message);
    }
}