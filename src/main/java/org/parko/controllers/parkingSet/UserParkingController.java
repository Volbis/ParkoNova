package org.parko.controllers.parkingSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.parko.database.DatabaseConnection;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserParkingController extends ParkingController {
    @FXML
    private Button btnAjouterMoto;

    @FXML
    private Button btnSortirMoto;

    @FXML
    private Button btnSortirVoiture;

    @FXML
    private Button btnAjouterVoiture;

    // Les labels à mettre à jour pour les places de motos et de voitures
    @FXML
    private Label lblPlacesMotos;

    @FXML
    private Label lblPlacesVoitures;


    // Les places de voitures
    @FXML
    private ImageView place_1;
    @FXML
    private ImageView place_2;
    @FXML
    private ImageView place_3;
    @FXML
    private ImageView place_4;
    @FXML
    private ImageView place_5;
    @FXML
    private ImageView place_6;
    @FXML
    private ImageView place_7;
    @FXML
    private ImageView place_8;
    @FXML
    private ImageView place_9;
    @FXML
    private ImageView place_10;
    @FXML
    private ImageView place_11;
    @FXML
    private ImageView place_12;
    @FXML
    private ImageView place_13;
    @FXML
    private ImageView place_14;
    @FXML
    private ImageView place_15;
    @FXML
    private ImageView place_16;
    @FXML
    private ImageView place_17;
    @FXML
    private ImageView place_18;
    @FXML
    private ImageView place_19;
    @FXML
    private ImageView place_20;


    // ============ Les places de motos ===================

    @FXML
    private ImageView placem_1;
    @FXML
    private ImageView placem_2;
    @FXML
    private ImageView placem_3;
    @FXML
    private ImageView placem_4;
    @FXML
    private ImageView placem_5;
    @FXML
    private ImageView placem_6;
    @FXML
    private ImageView placem_7;
    @FXML
    private ImageView placem_8;
    @FXML
    private ImageView placem_9;
    @FXML
    private ImageView placem_10;
    @FXML
    private ImageView placem_11;
    @FXML
    private ImageView placem_12;
    @FXML
    private ImageView placem_13;
    @FXML
    private ImageView placem_14;
    @FXML
    private ImageView placem_15;
    @FXML
    private ImageView placem_16;
    @FXML
    private ImageView placem_17;
    @FXML
    private ImageView placem_18;
    @FXML
    private ImageView placem_19;
    @FXML
    private ImageView placem_20;


    @Override
    public void initialize() {
        chargerEtatPlacesDepuisDB();
        chargerEtatPlacesDepuisDBMotos();
        updateMotorcyclePlacesLabel();
        updateCarPlacesLabel();
    }

}
