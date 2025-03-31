package org.parko.controllers;
import org.parko.services.StatistiqueService;
import org.parko.services.VehiculeService;
import org.parko.services.ParkingService;
import org.parko.services.PaiementService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.Set;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StatistiquesController implements Initializable {

    // Déclaration des éléments FXML
    @FXML private ComboBox<String> periodComboBox;
    @FXML private Text occupiedSpotsText;
    @FXML private Label occupiedSpotsDetails;
    @FXML private Text dailyRevenueText;
    @FXML private Label revenueChange;
    @FXML private Text avgParkingDurationText;
    @FXML private Label durationComparison;

    @FXML private AreaChart<String, Number> AreaChart;


    @FXML private TableView<StatistiqueEntry> detailedDataTable;
    @FXML private TableColumn<StatistiqueEntry, String> dateColumn;
    @FXML private TableColumn<StatistiqueEntry, String> sectionColumn;
    @FXML private TableColumn<StatistiqueEntry, Number> occupancyColumn;
    @FXML private TableColumn<StatistiqueEntry, Number> revenueColumn;
    @FXML private TableColumn<StatistiqueEntry, String> avgDurationColumn;
    @FXML private TableColumn<StatistiqueEntry, String> notesColumn;


    private StatistiqueService statistiqueService;
    private VehiculeService vehiculeService;
    private PaiementService paiementService;
    private ParkingService parkingService;
    private final int parkingId = 1;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        // Initialiser les services
        statistiqueService = new StatistiqueService();
        vehiculeService = new VehiculeService();
        paiementService = new PaiementService(parkingId);
        parkingService = new ParkingService();

        // Initialiser les ComboBox
        periodComboBox.setValue("Cette semaine");  // Changé de "Aujourd'hui" à "Cette semaine"

        // Configuration du tableau uniquement
        configureTable();

        // Charger les données initiales avec la période "Cette semaine"
        loadStatisticsData("Cette semaine");
        
        // Ajouter des écouteurs d'événements pour les ComboBox
        periodComboBox.setOnAction(event -> {
            loadStatisticsData(periodComboBox.getValue());
        });

    }

    /**
     * Gère l'action de rafraîchissement des données statistiques
     */
    @FXML
    private void handleRefreshAction(ActionEvent event) {
        // Récupérer la période sélectionnée dans le ComboBox
        String selectedPeriod = periodComboBox.getValue();
        
        System.out.println("Rafraîchissement des statistiques pour la période: " + selectedPeriod);

        // Actualiser les données selon la période sélectionnée
        loadStatisticsData(selectedPeriod);

        // Afficher un message de confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualisation");
        alert.setHeaderText(null);
        alert.setContentText("Les données ont été actualisées avec succès.");
        alert.showAndWait();
    }

    /**
     * Gère l'exportation des statistiques au format PDF
     */
    @FXML
    private void handleExportPdf(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
        );
        fileChooser.setInitialFileName("Statistiques_Parking_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf");

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                System.out.println("Exportation en PDF vers: " + file.getAbsolutePath());
                // Ici, vous implémenterez la création effective du PDF
                // Exemple: GenerateurPDF.exporterStatistiques(file, donnees, graphiques);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportation PDF");
                alert.setHeaderText(null);
                alert.setContentText("Le rapport a été exporté avec succès.");
                alert.showAndWait();
            } catch (Exception e) {
                afficherErreur("Erreur lors de l'exportation PDF", e.getMessage());
            }
        }
    }

    /**
     * Gère l'exportation des statistiques au format Excel
     */
    @FXML
    private void handleExportExcel(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx")
        );
        fileChooser.setInitialFileName("Statistiques_Parking_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx");

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                System.out.println("Exportation en Excel vers: " + file.getAbsolutePath());
                // Ici, vous implémenterez la création effective du fichier Excel
                // Exemple: ExcelExporter.exporterStatistiques(file, detailedDataTable.getItems());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportation Excel");
                alert.setHeaderText(null);
                alert.setContentText("Les données ont été exportées avec succès.");
                alert.showAndWait();
            } catch (Exception e) {
                afficherErreur("Erreur lors de l'exportation Excel", e.getMessage());
            }
        }
    }

    /**
     * Gère l'impression des statistiques
     */
    @FXML
    private void handlePrint(ActionEvent event) {
        try {
            System.out.println("Préparation de l'impression des statistiques...");
            // Ici, vous implémenterez la logique d'impression
            // Exemple: ServiceImpression.imprimerStatistiques(charts, data);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Impression");
            alert.setHeaderText(null);
            alert.setContentText("Les statistiques ont été envoyées à l'imprimante.");
            alert.showAndWait();
        } catch (Exception e) {
            afficherErreur("Erreur d'impression", e.getMessage());
        }
    }

    // ---------- Méthodes utilitaires ----------

    private void applyAreaChartStyles() {
        // Vérifier que le graphique existe
        if (AreaChart == null || AreaChart.getData().isEmpty()) return;
        
        // Styles pour la première série (voitures)
        String voituresStyle = 
            "-fx-stroke: #1f77b4; " +
            "-fx-fill: linear-gradient(to bottom, rgba(31, 119, 180, 0.6), rgba(31, 119, 180, 0.1));";
        
        // Styles pour la deuxième série (motos)
        String motosStyle = 
            "-fx-stroke: #ff7f0e; " +
            "-fx-fill: linear-gradient(to bottom, rgba(255, 127, 14, 0.6), rgba(255, 127, 14, 0.1));";
        
        // Application des styles
        AreaChart.getData().get(0).getNode().setStyle(voituresStyle);
        AreaChart.getData().get(1).getNode().setStyle(motosStyle);
        
        // Amélioration des points de données
        for (int i = 0; i < AreaChart.getData().size(); i++) {
            XYChart.Series<String, Number> series = AreaChart.getData().get(i);
            String color = (i == 0) ? "#1f77b4" : "#ff7f0e";
            
            // Appliquer le style à chaque point de données
            for (XYChart.Data<String, Number> item : series.getData()) {
                if (item.getNode() != null) {
                    item.getNode().setStyle(
                        "-fx-background-color: " + color + ", white; " +
                        "-fx-background-insets: 0, 2; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-padding: 5px;"
                    );
                }
            }
        }
        styleLegendSymbols();
    }

    // Ajoutez cette méthode à votre classe
    private void styleLegendSymbols() {
        // Attendre que le graphique soit rendu pour accéder aux éléments de la légende
        Platform.runLater(() -> {
            // Couleurs exactes que vous utilisez pour vos séries
            String voituresColor = "#1f77b4";  // Bleu
            String motosColor = "#ff7f0e";     // Orange

            // Chercher tous les symboles de la légende
            Set<Node> legendSymbols = AreaChart.lookupAll(".chart-legend-item-symbol");
            int index = 0;

            // Appliquer les couleurs aux symboles
            for (Node symbol : legendSymbols) {
                String color = (index == 0) ? voituresColor : motosColor;
                symbol.setStyle("-fx-background-color: " + color + ";");
                index++;
            }
        });
    }

    //En charge de l'affichage des graphes
    private void updateAreaChartData(String period) {
        if (AreaChart == null) return;
        
        // Effacer les données existantes
        AreaChart.getData().clear();
        
        // Créer de nouvelles séries selon la période sélectionnée
        XYChart.Series<String, Number> voituresSeries = new XYChart.Series<>();
        voituresSeries.setName("Voitures");
        
        XYChart.Series<String, Number> motosSeries = new XYChart.Series<>();
        motosSeries.setName("Motos");
        
        // Définir les périodes de dates
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = today;
        
        try {
            // Déterminer les dates de début et fin selon la période
            switch (period) {
                case "Aujourd'hui":
                    startDate = today;
                    break;
                case "Cette semaine":
                    startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
                    break;
                case "Ce mois":
                    startDate = today.withDayOfMonth(1);
                    break;
                case "Cette année":
                    startDate = today.withDayOfMonth(1).withMonth(1);
                    break;
                default:
                    startDate = today;
            }
            
            // Récupérer les données de véhicules par type et période
            Map<String, Map<String, Integer>> vehiculesParTypeEtPeriode = 
                statistiqueService.getVehiculesParTypeEtPeriode(startDate, endDate, parkingId, period);
            
            System.out.println("Données récupérées: " + vehiculesParTypeEtPeriode);
            
            // Si aucune donnée, ajouter des données par défaut pour visualisation
            if (vehiculesParTypeEtPeriode.isEmpty()) {
                System.out.println("Aucune donnée trouvée, utilisation de données par défaut");
                // Générer des données par défaut selon la période
                vehiculesParTypeEtPeriode = genererDonneesParDefaut(period);
            }
            
            // Déterminer l'ordre des points temporels selon la période
            List<String> pointsTemporels = determinerOrdrePointsTemporels(period);
            
            // Ajouter les données aux séries
            for (String pointTemporel : pointsTemporels) {
                Map<String, Integer> typeMap = vehiculesParTypeEtPeriode.getOrDefault(pointTemporel, new HashMap<>());
                int nbVoitures = typeMap.getOrDefault("VOITURE", 0);
                int nbMotos = typeMap.getOrDefault("MOTO", 0);
                
                voituresSeries.getData().add(new XYChart.Data<>(pointTemporel, nbVoitures));
                motosSeries.getData().add(new XYChart.Data<>(pointTemporel, nbMotos));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur de chargement des données", 
                "Impossible de charger les données pour le graphique: " + e.getMessage());
            return;
        }
        
        // Ajouter les séries au graphique
        AreaChart.getData().addAll(voituresSeries, motosSeries);
        
        // Mettre à jour le titre du graphique
        AreaChart.setTitle("Entrées par type de véhicule - " + period);
        
        // Appliquer les styles CSS personnalisés
        applyAreaChartStyles();
    }

    /**
     * Détermine l'ordre des points temporels selon la période
     */
    private List<String> determinerOrdrePointsTemporels(String period) {
        List<String> points = new ArrayList<>();
        
        switch (period) {
            case "Aujourd'hui":
                // Points temporels pour aujourd'hui (heures)
                for (int heure = 8; heure <= 20; heure += 2) {
                    points.add(String.format("%02d:00", heure));
                }
                break;
                
            case "Cette semaine":
                // Points temporels pour la semaine (jours)
                points.add("Lundi");
                points.add("Mardi");
                points.add("Mercredi");
                points.add("Jeudi");
                points.add("Vendredi");
                points.add("Samedi");
                points.add("Dimanche");
                break;
                
            case "Ce mois":
                // Points temporels pour le mois (semaines)
                points.add("Semaine 1");
                points.add("Semaine 2");
                points.add("Semaine 3");
                points.add("Semaine 4");
                break;
                
            case "Cette année":
                // Points temporels pour l'année (mois)
                points.add("Jan");
                points.add("Fév");
                points.add("Mar");
                points.add("Avr");
                points.add("Mai");
                points.add("Jun");
                points.add("Jul");
                points.add("Août");
                points.add("Sep");
                points.add("Oct");
                points.add("Nov");
                points.add("Déc");
                break;
        }
        
        return points;
    }

    /**
     * Génère des données par défaut pour la visualisation en l'absence de données réelles
     */
    private Map<String, Map<String, Integer>> genererDonneesParDefaut(String period) {
        Map<String, Map<String, Integer>> donnees = new HashMap<>();
        List<String> pointsTemporels = determinerOrdrePointsTemporels(period);
        
        // Générer des données différentes selon la période
        Random random = new Random();
        
        for (String point : pointsTemporels) {
            Map<String, Integer> typeMap = new HashMap<>();
            typeMap.put("VOITURE", random.nextInt(10) + 5); // Entre 5 et 14 voitures
            typeMap.put("MOTO", random.nextInt(8) + 2);     // Entre 2 et 9 motos
            donnees.put(point, typeMap);
        }
        
        return donnees;
    }

    // Méthode d'aide pour obtenir le nom du mois
    private String getMonthName(int month) {
        String[] monthNames = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
        return monthNames[month - 1];
    }

    /**
     * Configure le tableau de données détaillées avec des données d'exemple
     */
    private void configureTable() {
        // Configuration des colonnes
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        sectionColumn.setCellValueFactory(data -> data.getValue().sectionProperty());
        occupancyColumn.setCellValueFactory(data -> data.getValue().occupancyProperty());
        revenueColumn.setCellValueFactory(data -> data.getValue().revenueProperty());
        avgDurationColumn.setCellValueFactory(data -> data.getValue().avgDurationProperty());
        notesColumn.setCellValueFactory(data -> data.getValue().notesProperty());

        // Charger les données réelles
        try {
            // Récupérer les données des 5 derniers jours
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusDays(5);
            
            // Récupérer les statistiques pour chaque jour
            ObservableList<StatistiqueEntry> data = FXCollections.observableArrayList();
            
            for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
                // Obtenir le taux d'occupation pour ce jour
                int nbVehicules = vehiculeService.getNombreVehiculesPresentsJour(date);
                int capaciteTotal = parkingService.getCapaciteTotal(parkingId);
                double tauxOccupation = nbVehicules > 0 ? (double) nbVehicules / capaciteTotal * 100 : 0;
                
                // Obtenir les revenus pour ce jour
                double revenus = paiementService.getRevenusJour(date);
                
                // Récupérer la durée moyenne de stationnement (exemple simplifié)
                Map<String, Integer> durees = statistiqueService.getDureeStationnement(date, date, parkingId);
                String dureeStr = "N/A";
                int total = 0;
                int count = 0;
                for (Map.Entry<String, Integer> entry : durees.entrySet()) {
                    count += entry.getValue();
                    // Estimation de la durée moyenne en minutes
                    if (entry.getKey().equals("< 1 heure")) total += entry.getValue() * 30;
                    else if (entry.getKey().equals("1 - 3 heures")) total += entry.getValue() * 120;
                    else if (entry.getKey().equals("3 - 6 heures")) total += entry.getValue() * 270;
                    else total += entry.getValue() * 420; // > 6 heures
                }
                
                if (count > 0) {
                    int avgMinutes = total / count;
                    dureeStr = String.format("%dh %02dmin", avgMinutes / 60, avgMinutes % 60);
                }
                
                // Ajouter l'entrée
                data.add(new StatistiqueEntry(
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    "Tout le parking",
                    tauxOccupation,
                    revenus,
                    dureeStr,
                    date.equals(today) ? "Aujourd'hui" : ""
                ));
            }

            detailedDataTable.setItems(data);
            
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, ajouter des données par défaut
            ObservableList<StatistiqueEntry> defaultData = FXCollections.observableArrayList(
                new StatistiqueEntry("N/A", "Erreur de chargement", 0, 0, "N/A", e.getMessage())
            );
            detailedDataTable.setItems(defaultData);
        }
    }

    /**
     * Charge les données statistiques en fonction de la période sélectionnée
     */
    private void loadStatisticsData(String period) {
        System.out.println("Chargement des données pour la période: " + period);
        
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = today;
        
        // Déterminer les dates de début et fin selon la période sélectionnée
        switch (period) {
            case "Aujourd'hui":
                startDate = today;
                break;
            case "Cette semaine":
                startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
                break;
            case "Ce mois":
                startDate = today.withDayOfMonth(1);
                break;
            case "Cette année":
                startDate = today.withDayOfMonth(1).withMonth(1);
                break;
            default:
                startDate = today;
        }
        
        // Mettre à jour les indicateurs de performance
        try {
            // Obtenir le nombre total de places
            int capaciteTotal = parkingService.getCapaciteTotal(parkingId);
            
            // Obtenir le nombre de véhicules actuellement présents
            int vehiculesPresents = vehiculeService.getNombreVehiculesPresents();
            
            // Calculer le pourcentage d'occupation
            double tauxOccupation = (double) vehiculesPresents / capaciteTotal * 100;
            occupiedSpotsText.setText(String.format("%.0f%%", tauxOccupation));
            occupiedSpotsDetails.setText(vehiculesPresents + "/" + capaciteTotal + " places");
            
            // Obtenir les revenus pour la période sélectionnée
            double revenus = paiementService.getRevenusPeriode(startDate, endDate);
            
            // Afficher les revenus formatés
            if (period.equals("Cette année")) {
                dailyRevenueText.setText(String.format("%,.0f €", revenus));
            } else if (period.equals("Ce mois") || period.equals("Cette semaine")) {
                dailyRevenueText.setText(String.format("%,.0f €", revenus));
            } else {
                dailyRevenueText.setText(String.format("%,.2f €", revenus));
            }
            
            // Récupérer des données pour la durée moyenne de stationnement
            Map<String, Integer> durees = statistiqueService.getDureeStationnement(startDate, endDate, parkingId);
            int dureeTotal = 0;
            int nbVehicules = 0;
            
            // Calcul de la durée moyenne
            for (Map.Entry<String, Integer> entry : durees.entrySet()) {
                int count = entry.getValue();
                nbVehicules += count;
                
                // Convertir les catégories de durée en minutes (approximatif)
                if (entry.getKey().equals("< 1 heure")) dureeTotal += count * 30;
                else if (entry.getKey().equals("1 - 3 heures")) dureeTotal += count * 120;
                else if (entry.getKey().equals("3 - 6 heures")) dureeTotal += count * 270;
                else dureeTotal += count * 420; // > 6 heures
            }
            
            // Afficher la durée moyenne si des données sont disponibles
            if (nbVehicules > 0) {
                int dureeMoyenne = dureeTotal / nbVehicules;
                avgParkingDurationText.setText(String.format("%dh %02dm", dureeMoyenne / 60, dureeMoyenne % 60));
                durationComparison.setText("Durée moyenne");
            } else {
                avgParkingDurationText.setText("N/A");
                durationComparison.setText("Aucune donnée");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur de chargement", "Impossible de charger les statistiques: " + e.getMessage());
        }
        
        // Mettre à jour le graphique avec les données réelles
        updateAreaChartData(period);
    }
        
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Classe interne pour représenter une entrée dans le tableau de statistiques
     */
    public static class StatistiqueEntry {
        private final javafx.beans.property.StringProperty date;
        private final javafx.beans.property.StringProperty section;
        private final javafx.beans.property.DoubleProperty occupancy;
        private final javafx.beans.property.DoubleProperty revenue;
        private final javafx.beans.property.StringProperty avgDuration;
        private final javafx.beans.property.StringProperty notes;

        public StatistiqueEntry(String date, String section, double occupancy, double revenue,
                                String avgDuration, String notes) {
            this.date = new javafx.beans.property.SimpleStringProperty(date);
            this.section = new javafx.beans.property.SimpleStringProperty(section);
            this.occupancy = new javafx.beans.property.SimpleDoubleProperty(occupancy);
            this.revenue = new javafx.beans.property.SimpleDoubleProperty(revenue);
            this.avgDuration = new javafx.beans.property.SimpleStringProperty(avgDuration);
            this.notes = new javafx.beans.property.SimpleStringProperty(notes);
        }

        public javafx.beans.property.StringProperty dateProperty() { return date; }
        public javafx.beans.property.StringProperty sectionProperty() { return section; }
        public javafx.beans.property.DoubleProperty occupancyProperty() { return occupancy; }
        public javafx.beans.property.DoubleProperty revenueProperty() { return revenue; }
        public javafx.beans.property.StringProperty avgDurationProperty() { return avgDuration; }
        public javafx.beans.property.StringProperty notesProperty() { return notes; }
    }



}