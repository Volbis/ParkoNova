package org.parko.controllers;
import org.apache.poi.ss.usermodel.Cell;
import org.parko.interfaces.ParkingEventListener;
import org.parko.services.StatistiqueService;
import org.parko.services.VehiculeService;
import org.parko.services.ParkingService;
import org.parko.services.PaiementService;
import org.parko.interfaces.ParkingEventManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import java.io.FileOutputStream;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

import javafx.application.Platform;
import javax.imageio.ImageIO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class StatistiquesController implements Initializable, ParkingEventListener {

    // Déclaration des éléments FXML
    @FXML private ComboBox<String> periodComboBox;
    @FXML private Text occupiedSpotsText;
    @FXML private Label occupiedSpotsDetails;
    @FXML private Text dailyRevenueText;
    @FXML private Label revenueChange;
    @FXML private Text avgParkingDurationText;
    @FXML private AreaChart<String, Number> myAreaChart;

    @FXML private TableView<StatistiqueEntry> detailedDataTable;
    @FXML private TableColumn<StatistiqueEntry, String> dateColumn;
    @FXML private TableColumn<StatistiqueEntry, String> sectionColumn;
    @FXML private TableColumn<StatistiqueEntry, String> occupancyColumn;
    @FXML private TableColumn<StatistiqueEntry, String> revenueColumn;
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
        ObservableList<String> periods = FXCollections.observableArrayList(
            "Aujourd'hui", "Cette semaine", "Ce mois", "Cette année"
        );
        periodComboBox.setItems(periods);
        periodComboBox.setValue("Cette semaine");

        // Configuration des colonnes du tableau
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        sectionColumn.setCellValueFactory(data -> data.getValue().sectionProperty());
        occupancyColumn.setCellValueFactory(data -> data.getValue().occupancyProperty());
        revenueColumn.setCellValueFactory(data -> data.getValue().revenueProperty());
        
        // Initialiser l'AreaChart
        setupAreaChart();
        //Ecouter les activité
        ParkingEventManager.getInstance().addListener(this);
        
        // Charger les données initiales avec la période "Cette semaine"
        loadStatisticsData("Cette semaine");
        
        // Ajouter des écouteurs d'événements pour les ComboBox
        periodComboBox.setOnAction(event -> {
            loadStatisticsData(periodComboBox.getValue());
        });
    }
    
    /**
     * Configure l'AreaChart
     */
    private void setupAreaChart() {
        myAreaChart.setTitle("Stationnement par type de véhicule");
        myAreaChart.getXAxis().setLabel("Date");
        myAreaChart.getYAxis().setLabel("Nombre de véhicules");
        
        // Vider les séries existantes
        myAreaChart.getData().clear();
        
        // Créer deux séries pour les voitures et les motos
        XYChart.Series<String, Number> voitureSeries = new XYChart.Series<>();
        voitureSeries.setName("Voitures");
        
        XYChart.Series<String, Number> motoSeries = new XYChart.Series<>();
        motoSeries.setName("Motos");
        
        // Ajouter les séries au graphique
        myAreaChart.getData().addAll(voitureSeries, motoSeries);
    }

    // Ajoutez ces méthodes à votre classe StatistiquesController

    @Override
    public void onVehiculeEntree(String immatriculation, String type) {
        System.out.println("StatistiquesController: Entrée détectée - " + immatriculation + " (" + type + ")");
        
        // Mettre à jour l'interface sur le thread JavaFX
        Platform.runLater(() -> {
            // Rafraîchir le graphique avec la période actuellement sélectionnée
            String selectedPeriod = periodComboBox.getValue();
            if (selectedPeriod != null) {
                System.out.println("Mise à jour du graphique suite à l'entrée d'un véhicule");
                loadStatisticsData(selectedPeriod);
            }
        });
    }

    @Override
    public void onVehiculeSortie(String immatriculation, String type) {
        System.out.println("StatistiquesController: Sortie détectée - " + immatriculation + " (" + type + ")");
        
        // Mettre à jour l'interface sur le thread JavaFX
        Platform.runLater(() -> {
            // Rafraîchir le graphique avec la période actuellement sélectionnée
            String selectedPeriod = periodComboBox.getValue();
            if (selectedPeriod != null) {
                System.out.println("Mise à jour du graphique suite à la sortie d'un véhicule");
                loadStatisticsData(selectedPeriod);
            }
        });
    }

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

    @FXML
    private void handleExportPdf(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File selectedFile = fileChooser.showSaveDialog(null);
        
        if (selectedFile != null) {
            try {
                // Créer le document PDF
                Document document = new Document(PageSize.A4);
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(selectedFile));
                document.open();
                
                // Ajouter le titre
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Paragraph title = new Paragraph("Rapport de Statistiques - ParkoNova", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                
                document.add(new Paragraph(" ")); // Espace
                
                // Ajouter la date et la période
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
                Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                Paragraph datePara = new Paragraph("Date du rapport: " + LocalDateTime.now().format(dtf), normalFont);
                document.add(datePara);
                
                Paragraph periodPara = new Paragraph("Période analysée: " + periodComboBox.getValue(), normalFont);
                document.add(periodPara);
                
                document.add(new Paragraph(" ")); // Espace
                
                // Ajouter les statistiques principales
                PdfPTable statsTable = new PdfPTable(3); // 3 colonnes
                statsTable.setWidthPercentage(100);
                
                // En-têtes
                statsTable.addCell(createHeaderCell("Places occupées"));
                statsTable.addCell(createHeaderCell("Revenus"));
                statsTable.addCell(createHeaderCell("Durée moyenne"));
                
                // Valeurs
                statsTable.addCell(createValueCell(occupiedSpotsText.getText() + "\n" + occupiedSpotsDetails.getText()));
                statsTable.addCell(createValueCell(dailyRevenueText.getText()));
                statsTable.addCell(createValueCell(avgParkingDurationText.getText()));
                
                document.add(statsTable);
                
                document.add(new Paragraph(" ")); // Espace
                document.add(new Paragraph(" ")); // Espace
                
                // Ajouter le graphique
                Paragraph graphTitle = new Paragraph("Stationnement par type de véhicule", boldFont);
                document.add(graphTitle);
                
                // Capturer l'image du graphique
                WritableImage image = myAreaChart.snapshot(new SnapshotParameters(), null);
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", byteOutput);
                com.itextpdf.text.Image chartImage = com.itextpdf.text.Image.getInstance(byteOutput.toByteArray());
                chartImage.scaleToFit(500, 300);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
                
                document.add(new Paragraph(" ")); // Espace
                document.add(new Paragraph(" ")); // Espace
                
                // Ajouter le tableau d'historique
                Paragraph tableTitle = new Paragraph("Historique des stationnements", boldFont);
                document.add(tableTitle);
                
                PdfPTable historyTable = new PdfPTable(4);
                historyTable.setWidthPercentage(100);
                
                // En-têtes
                historyTable.addCell(createHeaderCell("Date/Heure"));
                historyTable.addCell(createHeaderCell("Immatriculation"));
                historyTable.addCell(createHeaderCell("Type de véhicule"));
                historyTable.addCell(createHeaderCell("Durée stationnement"));
                
                // Données
                for (StatistiqueEntry entry : detailedDataTable.getItems()) {
                    historyTable.addCell(entry.dateProperty().get());
                    historyTable.addCell(entry.sectionProperty().get());
                    historyTable.addCell(entry.occupancyProperty().get());
                    historyTable.addCell(entry.revenueProperty().get());
                }
                
                document.add(historyTable);
                
                // Pied de page
                Paragraph footer = new Paragraph("Rapport généré par ParkoNova © " + LocalDate.now().getYear(), normalFont);
                footer.setAlignment(Element.ALIGN_RIGHT);
                document.add(footer);
                
                document.close();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export PDF");
                alert.setHeaderText(null);
                alert.setContentText("Export en PDF réussi : " + selectedFile.getAbsolutePath());
                alert.showAndWait();
                
                // Ouvrir le fichier PDF
                Desktop.getDesktop().open(selectedFile);
                
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'export");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de l'export du PDF : " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    // Méthodes utilitaires pour créer les cellules du tableau PDF
    private PdfPCell createHeaderCell(String text) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setBackgroundColor(new BaseColor(41, 128, 185)); // Bleu
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createValueCell(String text) {
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 12);
        PdfPCell cell = new PdfPCell(new Phrase(text, valueFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    @FXML
    private void handleExportExcel(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter en Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        File selectedFile = fileChooser.showSaveDialog(null);
        
        if (selectedFile != null) {
            try {
                // Créer un nouveau classeur Excel
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("Statistiques ParkoNova");
                
                // Créer les styles pour l'en-tête et les cellules
                XSSFCellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{41, (byte)128, (byte)185}, null));
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                
                XSSFFont headerFont = workbook.createFont();
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                
                // Créer la ligne d'en-tête avec les titres des colonnes
                Row headerRow = sheet.createRow(0);
                String[] columnTitles = {
                    "Date/Heure", 
                    "Immatriculation",
                    "Type de véhicule",
                    "Durée stationnement"
                };
                
                for (int i = 0; i < columnTitles.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columnTitles[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                // Récupérer les données du tableau
                ObservableList<StatistiqueEntry> items = detailedDataTable.getItems();
                
                // Créer les lignes avec les données
                int rowNum = 1;
                for (StatistiqueEntry entry : items) {
                    Row row = sheet.createRow(rowNum++);
                    
                    // Date/Heure
                    Cell cell0 = row.createCell(0);
                    cell0.setCellValue(entry.dateProperty().get());
                    
                    // Immatriculation
                    Cell cell1 = row.createCell(1);
                    cell1.setCellValue(entry.sectionProperty().get());
                    
                    // Type de véhicule
                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(entry.occupancyProperty().get());
                    
                    // Durée de stationnement
                    Cell cell3 = row.createCell(3);
                    cell3.setCellValue(entry.revenueProperty().get());
                }
                
                // Ajustement automatique de la largeur des colonnes
                for (int i = 0; i < columnTitles.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // Enregistrer le classeur dans le fichier sélectionné
                try (FileOutputStream outputStream = new FileOutputStream(selectedFile)) {
                    workbook.write(outputStream);
                }
                
                // Fermer le classeur
                workbook.close();
                
                // Afficher un message de confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Excel");
                alert.setHeaderText(null);
                alert.setContentText("Export en Excel réussi : " + selectedFile.getAbsolutePath());
                alert.showAndWait();
                
                // Ouvrir le fichier Excel
                Desktop.getDesktop().open(selectedFile);
                
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'export");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de l'export Excel : " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handlePrint(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Impression");
        alert.setHeaderText(null);
        alert.setContentText("Impression des statistiques en cours...");
        alert.showAndWait();
    }

    /**
     * Met à jour l'AreaChart avec les données de stationnement
     */
    private void updateAreaChart(String period) {
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
        
        System.out.println("Période sélectionnée: " + period);
        System.out.println("Date de début: " + startDate + ", Date de fin: " + endDate);
        
        try {
            // Récupérer les données de stationnement depuis la base de données
            Map<LocalDate, Map<String, Integer>> stationnementData = 
                statistiqueService.getStationnementParTypeEtDate(startDate, endDate, parkingId);
            
            // Afficher les données récupérées dans la console
            System.out.println("===== DONNÉES RÉCUPÉRÉES DE LA BASE DE DONNÉES =====");
            int totalVoitures = 0;
            int totalMotos = 0;
            
            for (Map.Entry<LocalDate, Map<String, Integer>> entry : stationnementData.entrySet()) {
                LocalDate date = entry.getKey();
                Map<String, Integer> typesCount = entry.getValue();
                
                int voituresCount = typesCount.getOrDefault("VOITURE", 0);
                int motosCount = typesCount.getOrDefault("MOTO", 0);
                
                totalVoitures += voituresCount;
                totalMotos += motosCount;
                
                System.out.println(date + " | Voitures: " + voituresCount + " | Motos: " + motosCount);
            }
            
            System.out.println("---------------------------------------------");
            System.out.println("TOTAL: Voitures: " + totalVoitures + " | Motos: " + totalMotos);
            System.out.println("TOTAL GÉNÉRAL: " + (totalVoitures + totalMotos) + " véhicules");
            System.out.println("=============================================");
            
            // Vider les séries existantes
            myAreaChart.getData().clear();
            
            // Créer deux nouvelles séries pour les voitures et les motos
            XYChart.Series<String, Number> voitureSeries = new XYChart.Series<>();
            voitureSeries.setName("Voitures");
            
            XYChart.Series<String, Number> motoSeries = new XYChart.Series<>();
            motoSeries.setName("Motos");
            
            // Format de date pour l'affichage
            DateTimeFormatter formatter;
            if (period.equals("Cette année")) {
                formatter = DateTimeFormatter.ofPattern("MMM");
            } else if (period.equals("Ce mois")) {
                formatter = DateTimeFormatter.ofPattern("dd MMM");
            } else {
                formatter = DateTimeFormatter.ofPattern("dd/MM");
            }
            
            // Remplir les séries avec les données
            List<LocalDate> dates = new ArrayList<>(stationnementData.keySet());
            dates.sort(LocalDate::compareTo); // Trier les dates par ordre chronologique
            
            System.out.println("===== DONNÉES AJOUTÉES AU GRAPHIQUE =====");
            for (LocalDate date : dates) {
                Map<String, Integer> typesCountForDate = stationnementData.get(date);
                
                // Ajouter le point pour les voitures
                int voitureCount = typesCountForDate.getOrDefault("VOITURE", 0);
                voitureSeries.getData().add(new XYChart.Data<>(date.format(formatter), voitureCount));
                
                // Ajouter le point pour les motos
                int motoCount = typesCountForDate.getOrDefault("MOTO", 0);
                motoSeries.getData().add(new XYChart.Data<>(date.format(formatter), motoCount));
                
                System.out.println("Ajout au graphique - " + date.format(formatter) + 
                                " | Voitures: " + voitureCount + 
                                " | Motos: " + motoCount);
            }
            System.out.println("=========================================");
            
            // Ajouter les séries au graphique
            myAreaChart.getData().addAll(voitureSeries, motoSeries);
            
            // Ajouter une classe CSS pour le style
            voitureSeries.getNode().getStyleClass().add("voiture-series");
            motoSeries.getNode().getStyleClass().add("moto-series");
            
            System.out.println("Graphique mis à jour avec succès");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERREUR lors de la mise à jour du graphique: " + e.getMessage());
            afficherErreur("Erreur de graphique", "Impossible de charger les données du graphique: " + e.getMessage());
        }
    }

    private void configureTable() {
        // Configuration des colonnes
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        sectionColumn.setCellValueFactory(data -> data.getValue().sectionProperty());
        occupancyColumn.setCellValueFactory(data -> data.getValue().occupancyProperty());
        revenueColumn.setCellValueFactory(data -> data.getValue().revenueProperty());
        
        // Charger les données réelles
        try {
            // Récupérer les données des 5 derniers jours
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusDays(5);
            
            // Récupérer les statistiques depuis le service
            List<Map<String, Object>> statistiques = statistiqueService.getStatistiqueTable(startDate, today, parkingId);
            
            // Préparer les données pour l'affichage
            ObservableList<StatistiqueEntry> data = FXCollections.observableArrayList();
            
            // Conversion et ajout des entrées au tableau
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Map<String, Object> stat : statistiques) {
                Timestamp dateEntree = (Timestamp) stat.get("dateEntree");
                String immatriculation = (String) stat.get("immatriculation");
                String typeVehicule = (String) stat.get("typeVehicule");
                String dureeStationnement = (String) stat.get("dureeStationnement");
                
                // Formater la date et l'heure d'entrée
                String dateHeureFormatee = dateEntree.toLocalDateTime().format(formatter);
                
                // Ajouter l'entrée au tableau
                data.add(new StatistiqueEntry(
                    dateHeureFormatee,
                    immatriculation,
                    typeVehicule,
                    dureeStationnement
                ));
            }
            
            // Trier les données par date (plus récente d'abord)
            data.sort((a, b) -> b.dateProperty().get().compareTo(a.dateProperty().get()));
            
            detailedDataTable.setItems(data);
            
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, ajouter des données par défaut
            ObservableList<StatistiqueEntry> defaultData = FXCollections.observableArrayList(
                new StatistiqueEntry("N/A", "Erreur", "Erreur", e.getMessage())
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
        
        // Mettre à jour le tableau de données
        try {
            // Récupérer les statistiques depuis le service
            List<Map<String, Object>> statistiques = statistiqueService.getStatistiqueTable(startDate, endDate, parkingId);
            
            // Préparer les données pour l'affichage
            ObservableList<StatistiqueEntry> data = FXCollections.observableArrayList();
            
            // Conversion et ajout des entrées au tableau
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Map<String, Object> stat : statistiques) {
                Timestamp dateEntree = (Timestamp) stat.get("dateEntree");
                String immatriculation = (String) stat.get("immatriculation");
                String typeVehicule = (String) stat.get("typeVehicule");
                String dureeStationnement = (String) stat.get("dureeStationnement");
                
                // Formater la date et l'heure d'entrée
                String dateHeureFormatee = dateEntree.toLocalDateTime().format(formatter);
                
                // Ajouter l'entrée au tableau
                data.add(new StatistiqueEntry(
                    dateHeureFormatee,
                    immatriculation,
                    typeVehicule,
                    dureeStationnement
                ));
            }
            
            // Trier les données par date (plus récente d'abord)
            data.sort((a, b) -> b.dateProperty().get().compareTo(a.dateProperty().get()));
            
            detailedDataTable.setItems(data);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Mettre à jour le graphique
        updateAreaChart(period);
        
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
                dailyRevenueText.setText(String.format("%,.0f FCFA", revenus));
            } else if (period.equals("Ce mois") || period.equals("Cette semaine")) {
                dailyRevenueText.setText(String.format("%,.0f FCFA", revenus));
            } else {
                dailyRevenueText.setText(String.format("%,.2f FCFA", revenus));
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
            } else {
                avgParkingDurationText.setText("N/A");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur de chargement", "Impossible de charger les statistiques: " + e.getMessage());
        }
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
        private final javafx.beans.property.StringProperty dateHeure;
        private final javafx.beans.property.StringProperty immatriculation;
        private final javafx.beans.property.StringProperty typeVehicule;
        private final javafx.beans.property.StringProperty tempsStationnement;

        public StatistiqueEntry(String dateHeure, String immatriculation, String typeVehicule, 
                            String tempsStationnement) {
            this.dateHeure = new javafx.beans.property.SimpleStringProperty(dateHeure);
            this.immatriculation = new javafx.beans.property.SimpleStringProperty(immatriculation);
            this.typeVehicule = new javafx.beans.property.SimpleStringProperty(typeVehicule);
            this.tempsStationnement = new javafx.beans.property.SimpleStringProperty(tempsStationnement);
        }

        public javafx.beans.property.StringProperty dateProperty() { return dateHeure; }
        public javafx.beans.property.StringProperty sectionProperty() { return immatriculation; }
        public javafx.beans.property.StringProperty occupancyProperty() { return typeVehicule; }
        public javafx.beans.property.StringProperty revenueProperty() { return tempsStationnement; }
        
        // Ces propriétés ne sont plus utilisées mais je les garde pour la compatibilité
        public javafx.beans.property.StringProperty avgDurationProperty() { return new javafx.beans.property.SimpleStringProperty(""); }
        public javafx.beans.property.StringProperty notesProperty() { return new javafx.beans.property.SimpleStringProperty(""); }
    }
}