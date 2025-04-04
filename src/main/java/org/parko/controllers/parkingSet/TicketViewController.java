package org.parko.controllers.parkingSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TicketViewController {

    @FXML
    private TextArea ticketTextArea;

    @FXML
    private Button downloadButton;

    @FXML
    private Button printButton;

    private String numeroTicket;
    private Stage stage;

    public void initialize(String ticketContent, String numeroTicket, Stage stage) {
        this.numeroTicket = numeroTicket;
        this.stage = stage;
        
        // Configurer la police pour l'affichage correct de l'ASCII art
        ticketTextArea.setFont(Font.font("Monospace", FontWeight.NORMAL, 14));
        ticketTextArea.setEditable(false);
        
        // Vérifier si le contenu contient le format de la base de données
        if (ticketContent != null && ticketContent.contains("╔══════════════════════════════════╗")) {
            // Définir le contenu du ticket dans la zone de texte - format de la base de données
            ticketTextArea.setText(ticketContent);
        } else {
            // Format de secours structuré en cas d'erreur ou de problème de formatage
            StringBuilder formattedTicket = new StringBuilder();
            formattedTicket.append("----------------------------------\n");
            formattedTicket.append("         PARKO NOVA       \n");
            formattedTicket.append("   Treichville, Rue 12 Avenue 11   \n");
            formattedTicket.append("----------------------------------\n");
            
            // Extraire les informations essentielles du texte brut si possible
            String[] lignes = ticketContent != null ? ticketContent.split("\n") : new String[0];
            
            // Numéro de ticket
            formattedTicket.append("Ticket N° : ").append(numeroTicket).append("\n");
            
            // Ajouter la date et l'heure actuelles
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            formattedTicket.append("Date       : ").append(now.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            formattedTicket.append("Heure      : ").append(now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))).append("\n");
            formattedTicket.append("----------------------------------\n");
            
            // Chercher des informations sur le véhicule dans le texte
            String immatriculation = "N/A";
            String type = "VOITURE";
            String montant = "N/A";
            
            // Chercher le "MONTANT" dans le texte
            for (String ligne : lignes) {
                if (ligne.contains("VÉHICULE") || ligne.contains("Véhicule")) {
                    immatriculation = ligne.replaceAll(".*:[\\s]*", "").trim();
                }
                if (ligne.contains("TYPE") || ligne.contains("Type")) {
                    type = ligne.replaceAll(".*:[\\s]*", "").trim();
                }
                if (ligne.contains("MONTANT") || ligne.contains("Montant")) {
                    montant = ligne.replaceAll(".*:[\\s]*", "").trim();
                    // S'assurer que le montant est bien visible
                    montant = montant.toUpperCase();
                }
            }
            
            formattedTicket.append("Véhicule : ").append(immatriculation).append("\n");
            formattedTicket.append("Type     : ").append(type).append("\n");
            formattedTicket.append("----------------------------------\n");
            
            // Mettre en évidence le montant à payer s'il est disponible
            if (!montant.equals("N/A")) {
                formattedTicket.append("MONTANT À PAYER : ").append(montant).append("\n");
            } else {
                formattedTicket.append("Paiement à la sortie\n");
            }
            
            formattedTicket.append("----------------------------------\n");
            formattedTicket.append("⚠ Conservez ce ticket ⚠\n");
            formattedTicket.append("Présentez-le à la sortie\n");
            formattedTicket.append("Le parking décline toute responsabilité\n");
            formattedTicket.append("en cas de vol ou de dommage\n");
            formattedTicket.append("----------------------------------\n");
            formattedTicket.append("Merci et bonne journée ! 🚗");
            
            ticketTextArea.setText(formattedTicket.toString());
        }
        
        // Afficher les dimensions du TextArea pour le débogage
        System.out.println("Dimensions du TextArea: " + ticketTextArea.getWidth() + "x" + ticketTextArea.getHeight());
        System.out.println("Contenu du ticket:\n" + ticketTextArea.getText());
    }

    @FXML
    private void downloadTicket() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le ticket");

        // Définir les extensions de fichier
        FileChooser.ExtensionFilter txtFilter =
                new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt");
        FileChooser.ExtensionFilter pdfFilter =
                new FileChooser.ExtensionFilter("Documents PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().addAll(txtFilter, pdfFilter);

        // Définir le nom de fichier par défaut
        fileChooser.setInitialFileName("Ticket_" + numeroTicket + ".txt");

        // Ouvrir le dialogue de sauvegarde
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // Vérifier si le fichier est de type PDF
                if (file.getName().toLowerCase().endsWith(".pdf")) {
                    // Créer un dossier "tickets" s'il n'existe pas
                    Path ticketsDir = Paths.get(System.getProperty("user.home"), "ParkoTickets");
                    if (!Files.exists(ticketsDir)) {
                        Files.createDirectories(ticketsDir);
                    }

                    // Code pour générer un PDF (nécessite une bibliothèque externe comme iText)
                    // Pour ce cours, nous allons simplement créer un fichier texte
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(ticketTextArea.getText());
                    }

                    showSuccessMessage("Ticket enregistré avec succès sous : " + file.getAbsolutePath());
                } else {
                    // Enregistrer comme fichier texte
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(ticketTextArea.getText());
                    }

                    showSuccessMessage("Ticket enregistré avec succès sous : " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                showErrorMessage("Erreur lors de l'enregistrement du ticket", e.getMessage());
            }
        }
    }

    @FXML
    private void printTicket() {
        Printer defaultPrinter = Printer.getDefaultPrinter();

        if (defaultPrinter == null) {
            showErrorMessage("Aucune imprimante trouvée",
                    "Aucune imprimante n'est configurée sur ce système.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null) {
            // Configurer le travail d'impression
            boolean proceed = job.showPrintDialog(stage);

            if (proceed) {
                // Imprimer le contenu de la zone de texte
                boolean printed = job.printPage(ticketTextArea);

                if (printed) {
                    job.endJob();
                    showSuccessMessage("Le ticket est OK my G :)");
                } else {
                    showErrorMessage("Échec de l'impression",
                            "L'impression n'a pas pu être effectuée.");
                }
            }
        } else {
            showErrorMessage("Impossible de créer un travail d'impression",
                    "Le système n'a pas pu initialiser l'impression.");
        }
    }

    @FXML
    private void closeWindow() {
        stage.close();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String header, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}