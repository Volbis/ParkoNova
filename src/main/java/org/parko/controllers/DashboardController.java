package org.parko.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
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
    @FXML private PieChart vehiculeTypeChart;

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

        // Mise à jour du graphique des types de véhicules
        mettreAJourGraphiqueTypesVehicules();

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
            revenus.setName("Revenus (FCFA)"); // Ajout de l'unité monétaire pour clarifier

            System.out.println("Préparation des données pour le graphique...");

            // Déterminer l'échelle de temps appropriée en fonction de la période
            if (dateDebut.equals(dateFin)) {
                // Affichage par heure pour une journée
                Map<String, Integer> entreesParHeure = statistiqueService.getEntreesParHeure(dateDebut, parkingId);
                Map<String, Double> revenusParHeure = statistiqueService.getRevenusParHeure(dateDebut, parkingId);

                System.out.println("Entrées par heure: " + entreesParHeure);
                System.out.println("Revenus par heure: " + revenusParHeure);

                // Ajouter toutes les heures, même celles sans données
                for (int heure = 0; heure < 24; heure++) {
                    String heureStr = String.format("%02d:00", heure);
                    int nombreEntrees = entreesParHeure.getOrDefault(heureStr, 0);
                    double revenusHeure = revenusParHeure.getOrDefault(heureStr, 0.0);

                    // Toujours ajouter des données, même à zéro
                    entrees.getData().add(new XYChart.Data<>(heureStr, nombreEntrees));
                    revenus.getData().add(new XYChart.Data<>(heureStr, revenusHeure));
                }
            } else {
                // Pour des périodes plus longues, utiliser les données agrégées par jour
                Map<LocalDate, Integer> entreesParJour = statistiqueService.getEntreesParPeriode(dateDebut, dateFin);
                Map<LocalDate, Double> revenusParJour = statistiqueService.getRevenusParJour(dateDebut, dateFin, parkingId);

                System.out.println("Entrées par jour: " + entreesParJour);
                System.out.println("Revenus par jour: " + revenusParJour);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

                // Calculer l'intervalle approprié selon la durée de la période
                long joursDifference = ChronoUnit.DAYS.between(dateDebut, dateFin);
                int intervalle = determinerIntervalle(joursDifference);

                for (LocalDate date = dateDebut; !date.isAfter(dateFin); date = date.plusDays(1)) {
                    // N'afficher que les jours correspondant à l'intervalle pour les longues périodes
                    if (joursDifference <= 31 || date.getDayOfMonth() % intervalle == 1) {
                        String dateStr = date.format(formatter);
                        int nombreEntrees = entreesParJour.getOrDefault(date, 0);
                        double revenusJour = revenusParJour.getOrDefault(date, 0.0);

                        // Toujours ajouter des données, même à zéro
                        entrees.getData().add(new XYChart.Data<>(dateStr, nombreEntrees));
                        revenus.getData().add(new XYChart.Data<>(dateStr, revenusJour));
                    }
                }
            }

            System.out.println("Entrées - Points de données: " + entrees.getData().size());
            System.out.println("Revenus - Points de données: " + revenus.getData().size());

            // Ajouter les séries au graphique
            barChart.getData().addAll(entrees, revenus);
            System.out.println("Séries ajoutées au graphique: " + barChart.getData().size());

            // Forcer le rendu du graphique
            barChart.layout();
            barChart.applyCss();

            // Appliquer un style différent pour chaque série avec un délai pour s'assurer que les nœuds sont créés
            Platform.runLater(() -> {
                for (int i = 0; i < barChart.getData().size(); i++) {
                    XYChart.Series<String, Number> series = barChart.getData().get(i);
                    final String color = i == 0 ? "#3498db" : "#e74c3c"; // Bleu pour entrées, rouge pour revenus
                    final int seriesIndex = i;

                    System.out.println("Application du style à la série " + (i+1) + " (" + series.getName() + ")");

                    for (XYChart.Data<String, Number> data : series.getData()) {
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-bar-fill: " + color + ";");

                            // Ajouter un tooltip
                            String tooltipText = seriesIndex == 0 ?
                                    data.getYValue() + " entrées" :
                                    String.format("%,.0f FCFA", data.getYValue().doubleValue());

                            Tooltip tooltip = new Tooltip(tooltipText);
                            Tooltip.install(data.getNode(), tooltip);
                        } else {
                            System.out.println("Nœud non disponible pour un point de données");
                        }
                    }
                }
            });

            // Vérification supplémentaire après l'application des styles
            Platform.runLater(() -> {
                System.out.println("Vérification post-rendu - Nombre de séries: " + barChart.getData().size());
                for (XYChart.Series<String, Number> serie : barChart.getData()) {
                    System.out.println("Série: " + serie.getName() + " - " + serie.getData().size() + " points");

                    // Vérifier si les nœuds sont bien créés
                    int nodesCount = 0;
                    for (XYChart.Data<String, Number> data : serie.getData()) {
                        if (data.getNode() != null) nodesCount++;
                    }
                    System.out.println("  Nœuds disponibles: " + nodesCount + "/" + serie.getData().size());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la mise à jour du graphique: " + e.getMessage());
        }
    }

    // Méthode utilitaire pour déterminer l'intervalle d'affichage selon la durée
    private int determinerIntervalle(long joursDifference) {
        if (joursDifference <= 31) return 1;      // Afficher tous les jours (jusqu'à 1 mois)
        if (joursDifference <= 92) return 3;      // Tous les 3 jours (1-3 mois)
        if (joursDifference <= 183) return 7;     // Toutes les semaines (3-6 mois)
        if (joursDifference <= 365) return 15;    // Tous les 15 jours (6 mois-1 an)
        return 30;                               // Tous les mois (> 1 an)
    }

    private void mettreAJourGraphiqueTypesVehicules() {
        try {
            // Vider le graphique
            vehiculeTypeChart.getData().clear();
            
            // Récupérer le nombre de véhicules par type
            Map<String, Integer> vehiculesParType = vehiculeService.getVehiculesParType(parkingId);
            
            // Si aucune donnée n'est disponible, afficher un message
            if (vehiculesParType.isEmpty()) {
                // Option 1: Ajouter des données factices pour montrer un graphique vide
                vehiculeTypeChart.getData().add(new PieChart.Data("Aucun véhicule", 1));
                return;
            }
            
            // Créer les sections du graphique
            for (Map.Entry<String, Integer> entry : vehiculesParType.entrySet()) {
                String typeVehicule = entry.getKey();
                int nombre = entry.getValue();
                
                if (nombre > 0) { // Ne pas ajouter de section pour les types qui ont 0 véhicule
                    PieChart.Data slice = new PieChart.Data(typeVehicule + " (" + nombre + ")", nombre);
                    vehiculeTypeChart.getData().add(slice);
                }
            }
            
            // Ajouter des couleurs personnalisées (optionnel)
            int colorIndex = 0;
            String[] colors = {"#3498db", "#e74c3c", "#2ecc71", "#f1c40f", "#9b59b6"};
            
            for (PieChart.Data data : vehiculeTypeChart.getData()) {
                String color = colors[colorIndex % colors.length];
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
                colorIndex++;
            }
            
            int total = vehiculesParType.values().stream().mapToInt(Integer::intValue).sum();
            // Ajouter des tooltips montrant le pourcentage
            for (PieChart.Data data : vehiculeTypeChart.getData()) {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        double percentage = (double) data.getPieValue() / total * 100;
                        String text = String.format("%.1f%%", percentage);
                        
                        Tooltip tooltip = new Tooltip(text);
                        Tooltip.install(newNode, tooltip);
                    }
                });
            }
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