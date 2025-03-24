package org.parko.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StatistiquesController implements Initializable {

    // Déclaration des éléments FXML
    @FXML private ComboBox<String> periodComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Text occupiedSpotsText;
    @FXML private Label occupiedSpotsDetails;
    @FXML private Text dailyRevenueText;
    @FXML private Label revenueChange;
    @FXML private Text avgParkingDurationText;
    @FXML private Label durationComparison;

    @FXML private LineChart<String, Number> timeSeriesChart;
    @FXML private PieChart distributionChart;


    @FXML private TableView<StatistiqueEntry> detailedDataTable;
    @FXML private TableColumn<StatistiqueEntry, String> dateColumn;
    @FXML private TableColumn<StatistiqueEntry, String> sectionColumn;
    @FXML private TableColumn<StatistiqueEntry, Number> occupancyColumn;
    @FXML private TableColumn<StatistiqueEntry, Number> revenueColumn;
    @FXML private TableColumn<StatistiqueEntry, String> avgDurationColumn;
    @FXML private TableColumn<StatistiqueEntry, String> notesColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les ComboBox
        periodComboBox.setValue("Aujourd'hui");
        typeComboBox.setValue("Occupation");

        // Configuration des graphiques et tableaux avec des données initiales
        configureCharts();
        configureTable();

        // Charger les données initiales
        loadStatisticsData("Aujourd'hui", "Occupation");
    }

    /**
     * Gère l'action de rafraîchissement des données statistiques
     */
    @FXML
    private void handleRefreshAction(ActionEvent event) {
        // Récupérer les valeurs sélectionnées dans les ComboBox
        String selectedPeriod = periodComboBox.getValue();
        String selectedType = typeComboBox.getValue();

        System.out.println("Rafraîchissement des statistiques pour la période: " + selectedPeriod +
                " et le type: " + selectedType);

        // Actualiser les données selon les filtres sélectionnés
        loadStatisticsData(selectedPeriod, selectedType);

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

    /**
     * Configure les graphiques avec des données d'exemple
     */
    private void configureCharts() {
        // Configuration du graphique linéaire
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Taux d'occupation");
        series.getData().add(new XYChart.Data<>("8h", 25));
        series.getData().add(new XYChart.Data<>("10h", 45));
        series.getData().add(new XYChart.Data<>("12h", 70));
        series.getData().add(new XYChart.Data<>("14h", 60));
        series.getData().add(new XYChart.Data<>("16h", 80));
        series.getData().add(new XYChart.Data<>("18h", 55));
        series.getData().add(new XYChart.Data<>("20h", 30));
        timeSeriesChart.getData().add(series);

        // Configuration du graphique circulaire
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Standard", 60),
                new PieChart.Data("Handicapé", 5),
                new PieChart.Data("Famille", 15),
                new PieChart.Data("Électrique", 20)
        );
        distributionChart.setData(pieChartData);

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

        // Ajouter des données d'exemple
        ObservableList<StatistiqueEntry> data = FXCollections.observableArrayList(
                new StatistiqueEntry("2023-07-10 10:00", "Niveau -1", 65, 320.50, "1h 45min", "Période normale"),
                new StatistiqueEntry("2023-07-10 12:00", "Niveau -1", 78, 420.75, "2h 10min", "Heure de pointe"),
                new StatistiqueEntry("2023-07-10 14:00", "Niveau -2", 45, 210.25, "1h 30min", ""),
                new StatistiqueEntry("2023-07-10 16:00", "Niveau -2", 80, 450.00, "2h 25min", "Événement spécial"),
                new StatistiqueEntry("2023-07-10 18:00", "Extérieur", 90, 520.50, "3h 05min", "Heure de pointe")
        );

        detailedDataTable.setItems(data);
    }

    /**
     * Charge les données statistiques en fonction de la période et du type sélectionnés
     */
    private void loadStatisticsData(String period, String type) {
        System.out.println("Chargement des données pour " + period + " et " + type);

        // Dans une vraie application, vous récupéreriez les données de votre base de données
        // Pour l'exemple, nous mettons simplement à jour les valeurs affichées

        switch (period) {
            case "Aujourd'hui":
                occupiedSpotsText.setText("67%");
                occupiedSpotsDetails.setText("134/200 places");
                dailyRevenueText.setText("1 250 €");
                revenueChange.setText("+12% vs hier");
                break;
            case "Cette semaine":
                occupiedSpotsText.setText("73%");
                occupiedSpotsDetails.setText("146/200 places");
                dailyRevenueText.setText("8 750 €");
                revenueChange.setText("+8% vs semaine dernière");
                break;
            case "Ce mois":
                occupiedSpotsText.setText("70%");
                occupiedSpotsDetails.setText("140/200 places");
                dailyRevenueText.setText("37 500 €");
                revenueChange.setText("+15% vs mois dernier");
                break;
            case "Cette année":
                occupiedSpotsText.setText("65%");
                occupiedSpotsDetails.setText("130/200 places");
                dailyRevenueText.setText("450 000 €");
                revenueChange.setText("+25% vs année dernière");
                break;
        }
    }

    /**
     * Affiche une boîte de dialogue d'erreur
     */
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