package org.parko.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
//import org.parko.model.Abonnement;
import org.parko.classes.Vehicule;
import org.parko.services.ParkingService;
import org.parko.services.StatistiqueService;
import org.parko.services.VehiculeService;
//import org.parko.util.ParkingEventListener;
//import org.parko.util.ParkingEventManager;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // Éléments FXML
    @FXML private ComboBox<String> cbxPeriode;
    @FXML private DatePicker dpDebut;
    @FXML private DatePicker dpFin;
    @FXML private Button btnActualiser;
    @FXML private Label lblPlacesDisponibles;
    @FXML private Label lblVehiculesPresents;
    @FXML private Label lblRevenusTotaux;
    @FXML private Label lblTauxOccupation;
    @FXML private BarChart<String, Number> barChart;

    // ID du parking actuel (à récupérer depuis la configuration ou la session)
    private int parkingId = 1; // Par défaut

    // Services contenant les procédures stockés de la bd
    private StatistiqueService statistiqueService;
    private VehiculeService vehiculeService;
    private ParkingService parkingService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialisation des services avec les procédures stockées
        statistiqueService = new StatistiqueService();
        vehiculeService = new VehiculeService();
        parkingService = new ParkingService();

        // Configuration des périodes prédéfinies
        configurationPeriodes();

        // Configuration des DatePickers
        configurationDatePickers();

        // Configuration du bouton d'actualisation
        btnActualiser.setOnAction(e -> actualiserDashboard());
/*
        // S'abonner aux événements du parking
        ParkingEventManager.getInstance().addListener(new ParkingEventListener() {
            @Override
            public void onVehiculeEntre(Vehicule vehicule) {
                // Si la période actuelle est "Aujourd'hui", mettre à jour immédiatement
                if ("Aujourd'hui".equals(cbxPeriode.getValue())) {
                    Platform.runLater(() -> actualiserDashboard());
                }
            }

            @Override
            public void onVehiculeSorti(Vehicule vehicule, double montantPaye) {
                // Si la période actuelle est "Aujourd'hui", mettre à jour immédiatement
                if ("Aujourd'hui".equals(cbxPeriode.getValue())) {
                    Platform.runLater(() -> actualiserDashboard());
                }
            }

            @Override
            public void onAbonnementCree(Abonnement abonnement) {
                // Mettre à jour si nécessaire pour les abonnements
            }
        });
*/
        // Chargement des données par défaut (aujourd'hui)
        chargerDonneesParDefaut();
    }

    private void configurationPeriodes() {
        // Ajout des périodes prédéfinies dans la ComboBox
        cbxPeriode.getItems().addAll(
                "Aujourd'hui",
                "Cette semaine",
                "Ce mois",
                "Ce trimestre",
                "Cette année",
                "Personnalisé"
        );

        // Sélection par défaut
        cbxPeriode.setValue("Aujourd'hui");

        // Listener pour gérer le changement de période
        cbxPeriode.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if ("Personnalisé".equals(newVal)) {
                    // Activer les DatePickers pour une période personnalisée
                    dpDebut.setDisable(false);
                    dpFin.setDisable(false);
                } else {
                    // Désactiver les DatePickers et définir automatiquement les dates
                    dpDebut.setDisable(true);
                    dpFin.setDisable(true);

                    // Définir les dates selon la période sélectionnée
                    LocalDate dateDebut = LocalDate.now();
                    LocalDate dateFin = LocalDate.now();

                    switch (newVal) {
                        case "Aujourd'hui":
                            // Déjà défini par défaut
                            break;
                        case "Cette semaine":
                            dateDebut = dateDebut.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                            dateFin = dateDebut.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                            break;
                        case "Ce mois":
                            dateDebut = dateDebut.withDayOfMonth(1);
                            dateFin = dateDebut.with(TemporalAdjusters.lastDayOfMonth());
                            break;
                        case "Ce trimestre":
                            int currentMonth = dateDebut.getMonthValue();
                            int startMonth = ((currentMonth - 1) / 3) * 3 + 1;
                            dateDebut = LocalDate.of(dateDebut.getYear(), startMonth, 1);
                            dateFin = dateDebut.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                            break;
                        case "Cette année":
                            dateDebut = LocalDate.of(dateDebut.getYear(), 1, 1);
                            dateFin = LocalDate.of(dateDebut.getYear(), 12, 31);
                            break;
                    }

                    dpDebut.setValue(dateDebut);
                    dpFin.setValue(dateFin);

                    // Actualiser le dashboard avec les nouvelles dates
                    actualiserDashboard();
                }
            }
        });
    }

    private void configurationDatePickers() {
        // Configuration initiale
        dpDebut.setValue(LocalDate.now());
        dpFin.setValue(LocalDate.now());

        // Désactivés par défaut (mode "Aujourd'hui")
        dpDebut.setDisable(true);
        dpFin.setDisable(true);

        // Validation des dates
        dpDebut.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dpFin.getValue() != null && newVal.isAfter(dpFin.getValue())) {
                dpFin.setValue(newVal);
            }
        });

        dpFin.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dpDebut.getValue() != null && newVal.isBefore(dpDebut.getValue())) {
                dpDebut.setValue(newVal);
            }
        });
    }

    private void chargerDonneesParDefaut() {
        // Définir la période par défaut
        cbxPeriode.setValue("Aujourd'hui");

        // Actualiser le dashboard
        actualiserDashboard();
    }

    /* ========= Methode principale ============= */
    private void actualiserDashboard() {
        LocalDate dateDebut = dpDebut.getValue();
        LocalDate dateFin = dpFin.getValue();

        if (dateDebut == null || dateFin == null) {
            // Gérer le cas où les dates ne sont pas sélectionnées
            return;
        }

        // Mise à jour des statistiques avec appel aux procédures stockées
        mettreAJourPlacesDisponibles();
        mettreAJourVehiculesPresents();
        mettreAJourRevenus(dateDebut, dateFin);
        mettreAJourTauxOccupation();

        // Mise à jour des graphiques
        mettreAJourGraphiqueActivite(dateDebut, dateFin);
    }

    private void mettreAJourPlacesDisponibles() {
        // Utilise directement la procédure stockée getPlacesDisponibles
        try {
            /* Pas encore configuré*/
            int placesDisponibles = parkingService.getPlacesDisponibles(parkingId);

            // Mettre à jour l'affichage
            lblPlacesDisponibles.setText(String.valueOf(placesDisponibles));
        } catch (Exception e) {
            e.printStackTrace();
            lblPlacesDisponibles.setText("N/A");
        }
    }

    private void mettreAJourVehiculesPresents() {
        try {
            // Utilise directement la procédure stockée getVehiculesPresents
            /* Problème ici à resoudre */
            int vehiculesPresents = vehiculeService.getNombreVehiculesPresents();

            // Mettre à jour l'affichage
            lblVehiculesPresents.setText(String.valueOf(vehiculesPresents));
        } catch (Exception e) {
            e.printStackTrace();
            lblVehiculesPresents.setText("N/A");
        }
    }

    private void mettreAJourRevenus(LocalDate dateDebut, LocalDate dateFin) {
        try {
            // Récupérer les statistiques pour la période spécifiée
            /* Pas encore implémenté dans les services */
            double revenus = statistiqueService.getRevenusTotauxPeriode(dateDebut, dateFin, parkingId);

            // Formater les revenus (par exemple: 15000 FCFA)
            DecimalFormat formatter = new DecimalFormat("#,###");
            String revenusFormates = formatter.format(revenus);

            // Mettre à jour l'affichage
            lblRevenusTotaux.setText(revenusFormates);
        } catch (Exception e) {
            e.printStackTrace();
            lblRevenusTotaux.setText("N/A");
        }
    }


    /* Selon doit etre en fonction du temps passé par chaque véhicule dans le parking */
    private void mettreAJourTauxOccupation() {
        try {
            /*  Logique incompréhensible */
            // Récupérer les données d'occupation via les procédures
            int capaciteParking = parkingService.getCapaciteTotal(parkingId);
            int vehiculesPresents = vehiculeService.getNombreVehiculesPresents();

            // Calculer le taux d'occupation
            double tauxOccupation = (capaciteParking > 0) ?
                    (double) vehiculesPresents / capaciteParking * 100 : 0;

            // Mettre à jour l'affichage
            lblTauxOccupation.setText(String.format("%.1f%%", tauxOccupation));
        } catch (Exception e) {
            e.printStackTrace();
            lblTauxOccupation.setText("N/A");
        }
    }

    private void mettreAJourGraphiqueActivite(LocalDate dateDebut, LocalDate dateFin) {
        try {
            // Vider le graphique
            barChart.getData().clear();

            // Créer une série de données pour les entrées et les sorties
            XYChart.Series<String, Number> entrees = new XYChart.Series<>();
            entrees.setName("Entrées");

            XYChart.Series<String, Number> revenus = new XYChart.Series<>();
            revenus.setName("Revenus");

            // Déterminer l'échelle de temps appropriée en fonction de la période
            if (dateDebut.equals(dateFin)) {
                // Affichage par heure pour une journée
                Map<String, Integer> entreesParHeure = statistiqueService.getEntreesParHeure(dateDebut, parkingId);
                Map<String, Double> revenusParHeure = statistiqueService.getRevenusParHeure(dateDebut, parkingId);

                for (int heure = 0; heure < 24; heure++) {
                    String heureStr = String.format("%02d:00", heure);
                    int nombreEntrees = entreesParHeure.getOrDefault(heureStr, 0);
                    entrees.getData().add(new XYChart.Data<>(heureStr, nombreEntrees));

                    double revenusHeure = revenusParHeure.getOrDefault(heureStr, 0.0);
                    revenus.getData().add(new XYChart.Data<>(heureStr, revenusHeure));
                }
            } else {
                // Pour des périodes plus longues, utiliser les données agrégées par jour
                Map<LocalDate, Integer> entreesParJour = statistiqueService.getEntreesParPeriode(dateDebut, dateFin);
                Map<LocalDate, Double> revenusParJour = statistiqueService.getRevenusParJour(dateDebut, dateFin, parkingId);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
                for (LocalDate date = dateDebut; !date.isAfter(dateFin); date = date.plusDays(1)) {
                    String dateStr = date.format(formatter);

                    int nombreEntrees = entreesParJour.getOrDefault(date, 0);
                    entrees.getData().add(new XYChart.Data<>(dateStr, nombreEntrees));

                    double revenusJour = revenusParJour.getOrDefault(date, 0.0);
                    revenus.getData().add(new XYChart.Data<>(dateStr, revenusJour));
                }
            }

            // Ajouter les séries au graphique
            barChart.getData().add(entrees);
            barChart.getData().add(revenus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    // Méthode appelée lors de la fermeture de la fenêtre
    public void onClose() {
        // Se désabonner des événements pour éviter les fuites mémoire
        ParkingEventManager.getInstance().removeListener(this);
    }
*/
}