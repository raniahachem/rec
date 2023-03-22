/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gestionreclamation.gui;

import edu.gestionreclamation.entities.Avis;
import edu.gestionreclamation.services.AvisCRUD;
import edu.gestionreclamation.utils.MyConnection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

/**
 * FXML Controller class
 *
 * @author rania
 */
public class AjoutaviController implements Initializable {

    @FXML
    private Button btnajouternote;
    @FXML
    private TextArea txtMoyennes;
    @FXML
    private Button btnnote;
    @FXML
    private Rating rating;
    @FXML
    private Label message;
    @FXML
    private TextField idConducteur;
    @FXML
    private Button btnreclamation;
    @FXML
    private Button btnAvis;
    private AvisCRUD acrd = new AvisCRUD();
    @FXML
    private ImageView image1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        rating.ratingProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ob, Number old, Number newT) {
            message.setText("Rating:" +newT);
            }
        });
        File file = new File("C:/Users/rania/OneDrive/Documents/NetBeansProjects/gestionreclamation/src/image/logoauto2.png");
        String localURL = "";
        try {
            localURL = file.toURI().toURL().toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        image1.setImage(new Image(localURL));
    }   

    @FXML
    private void ajouterNote2(ActionEvent event) {
try {
        // Récupérer l'id_client depuis la zone de texte idConducteur
        int id_c = Integer.parseInt(idConducteur.getText());

        // Vérifier si le conducteur existe dans la base de données
        String selectQuery = "SELECT COUNT(*) FROM conducteur WHERE id_conducteur = ?";
        PreparedStatement selectStmt = MyConnection.getInstance().getCnx().prepareStatement(selectQuery);
        selectStmt.setInt(1, id_c);
        ResultSet selectResult = selectStmt.executeQuery();

        selectResult.next();
        int conducteurCount = selectResult.getInt(1);
        
        if (conducteurCount == 0) {
            // Afficher une alerte avec le message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Le conducteur avec l'id " + id_c + " n'existe pas, veuillez vérifier l'id entré");
            alert.showAndWait();
            return;
        }

        // Récupérer la note depuis le label message et la convertir en entier
        int notec = (int) rating.getRating();
        // Ajouter la note à la base de données
        acrd.ajouterAvis(new Avis(id_c, notec));

        // Afficher un message de confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("La note a été ajoutée avec succès");
        alert.showAndWait();

        // Effacer les zones de texte
        idConducteur.setText("");
    } catch (SQLException ex) {
        System.out.println(ex.getMessage());
    } catch (NumberFormatException ex) {
        // Afficher une alerte si la zone de texte idConducteur ne contient pas un entier
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("L'id du conducteur doit être un entier.");
        alert.showAndWait();
    }
    }

    @FXML
    private void afficherMoyennes(ActionEvent event) {
// Calculer les moyennes des notes pour chaque id_conducteur
    Map<Integer, Double> moyennes = acrd.calculerMoyenne();

    // Récupérer les noms et prénoms des conducteurs
    Map<Integer, String> nomsPrenoms = new HashMap<>();
    try {
        String requete = "SELECT id_conducteur, nom_conducteur, prenom_conducteur FROM conducteur";
        PreparedStatement selectStmt = MyConnection.getInstance().getCnx().prepareStatement(requete);
        ResultSet result = selectStmt.executeQuery();
        while (result.next()) {
            int id_conducteur = result.getInt("id_conducteur");
            String nom = result.getString("nom_conducteur");
            String prenom = result.getString("prenom_conducteur");
            nomsPrenoms.put(id_conducteur, nom + " " + prenom);
        }
    } catch (SQLException ex) {
        System.out.println(ex.getMessage());
    }

    // Afficher les moyennes et les noms/prénoms pour chaque id_conducteur dans la zone de texte "txtMoyennes"
    StringBuilder sb = new StringBuilder();
    for (int id_conducteur : moyennes.keySet()) {
        double moyenne = moyennes.get(id_conducteur);
        String nomPrenom = nomsPrenoms.getOrDefault(id_conducteur, "");
        sb.append("id_conducteur: ").append(id_conducteur);
        if (!nomPrenom.isEmpty()) {
            sb.append(" -> (").append(nomPrenom).append(")").append(" : ").append(moyenne).append(" / 5");
        }
        sb.append("\n");
    }
    txtMoyennes.setText(sb.toString());
    }

    @FXML
    private void gererReclamations(ActionEvent event) {
           try {
        Parent reclamationsParent = FXMLLoader.load(getClass().getResource("Ajout.fxml"));
        Scene reclamationsScene = new Scene(reclamationsParent);
        Stage window = (Stage)(((Button)event.getSource()).getScene().getWindow());
        window.setScene(reclamationsScene);
        window.show();
    } catch (IOException e) {
    }
    }

    @FXML
    private void gererAvis(ActionEvent event) {
           try {
        Parent reclamationsParent = FXMLLoader.load(getClass().getResource("Ajoutavi.fxml"));
        Scene reclamationsScene = new Scene(reclamationsParent);
        Stage window = (Stage)(((Button)event.getSource()).getScene().getWindow());
        window.setScene(reclamationsScene);
        window.show();
    } catch (IOException e) {
    }


    }
    
}
