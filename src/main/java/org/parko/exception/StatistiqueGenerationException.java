package org.parko.exception;

public class StatistiqueGenerationException extends Exception {
    public StatistiqueGenerationException(String message) {
        super("Erreur lors de la génération des statistiques : " + message);
    }
}