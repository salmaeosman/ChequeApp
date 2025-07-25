package vues;

import controllers.ChequeController;
import entities.Cheque;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import services.MontantEnLettresService;

public class ChequeEditView {

    private static TextField ligne1;
    private static Label ligne2;

    public static void afficher(Cheque cheque, ChequeController controller) {
        Stage stage = new Stage();
        stage.setTitle("Modifier le chèque");

        Pane root = new Pane();
        root.setPrefSize(1100, 450);
        root.setStyle("-fx-background-image: url('/images/cheque_bg.png');" +
                "-fx-background-size: cover;" +
                "-fx-background-repeat: no-repeat;");

        // Montant
        TextField montantField = champ(cheque.getMontant() + "", 850, 25, 160);
        montantField.setOnKeyReleased(e -> {
            try {
                double montant = Double.parseDouble(montantField.getText());
                String lettres = MontantEnLettresService.convertirMontant(montant, cheque.getLangue());
                String[] parts = lettres.split("(?=\\s+et\\s+.*centime?s?)", 2);
                ligne1.setText((parts.length > 0 ? parts[0] : lettres).trim());
                ligne2.setText((parts.length > 1 ? parts[1] : "").trim());
            } catch (Exception ex) {
                ligne1.setText("");
                ligne2.setText("");
            }
        });

        ligne1 = champ("", 500, 100, 400);
        ligne2 = new Label();
        ligne2.setFont(new Font("Arial", 13));
        ligne2.setLayoutX(90);
        ligne2.setLayoutY(130);
        ligne2.setPrefWidth(920);

        // Champs du chèque
        TextField beneficiaireField = champ(cheque.getBeneficiaire(), 145, 170, 850);
        TextField villeField = champ(cheque.getVille(), 500, 210, 200);
        DatePicker dateField = new DatePicker(cheque.getDate());
        dateField.setLayoutX(790);
        dateField.setLayoutY(210);
        dateField.setPrefWidth(200);
        TextField nomChequeField = champ(cheque.getNomCheque(), 105, 340, 40);
        TextField nomSerieField = champ(cheque.getNomSerie(), 190, 340, 40);
        TextField numeroSerieField = champ(cheque.getNumeroSerie() + "", 260, 340, 150);

        // Boutons
        Button retourBtn = bouton("Retour", 300, 410);
        retourBtn.setOnAction(e -> stage.close());

        Button enregistrerBtn = bouton("💾 Enregistrer", 500, 410);
        enregistrerBtn.setOnAction(e -> {
            try {
                Cheque chequeMaj = new Cheque();
                chequeMaj.setId(cheque.getId());
                chequeMaj.setMontant(Double.parseDouble(montantField.getText()));
                chequeMaj.setBeneficiaire(beneficiaireField.getText());
                chequeMaj.setVille(villeField.getText());
                chequeMaj.setDate(dateField.getValue());
                chequeMaj.setNomCheque(nomChequeField.getText());
                chequeMaj.setNomSerie(nomSerieField.getText());
                chequeMaj.setNumeroSerie(Long.parseLong(numeroSerieField.getText()));
                chequeMaj.setLangue(cheque.getLangue());

                boolean updated = controller.modifierCheque(cheque.getId(), chequeMaj);
                if (updated) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Le chèque a été modifié avec succès.");
                    stage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier le chèque.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide : " + ex.getMessage());
            }
        });

        Button imprimerBtn = bouton("🖨️ Imprimer", 730, 410);
        imprimerBtn.setOnAction(e -> {
            // À implémenter (PrinterJob)
        });

        root.getChildren().addAll(
                montantField, ligne1, ligne2,
                beneficiaireField, villeField, dateField,
                nomChequeField, nomSerieField, numeroSerieField,
                retourBtn, enregistrerBtn, imprimerBtn
        );

        stage.setScene(new Scene(root));
        stage.show();
    }

    private static TextField champ(String texte, double x, double y, double largeur) {
        TextField field = new TextField(texte);
        field.setFont(new Font("Arial", 13));
        field.setLayoutX(x);
        field.setLayoutY(y);
        field.setPrefWidth(largeur);
        field.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: black;");
        return field;
    }

    private static Button bouton(String texte, double x, double y) {
        Button btn = new Button(texte);
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setStyle("-fx-background-radius: 50px; -fx-background-color: linear-gradient(to right, #0D8BFF, #0052D4);" +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8px 20px;");
        return btn;
    }

    private static void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
