package org.parko.utilitaire;

public class TarifUtils {
    public static double calculerTarif(String type, long dureeHeures) {
        if (type.equals("VOITURE")) {
            return dureeHeures * 500; // 500 FCFA/heure pour les voitures
        } else if (type.equals("MOTO")) {
            return dureeHeures * 300; // 300 FCFA/heure pour les motos
        }
        return 0;
    }
}