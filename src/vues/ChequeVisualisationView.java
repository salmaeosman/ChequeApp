package vues;

import controllers.ChequeController;
import entities.Cheque;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import services.MontantEnLettresService;

public class ChequeVisualisationView {

    public static void afficher(Cheque cheque, ChequeController controller) {
        Stage stage = new Stage();
        stage.setTitle("Visualisation du chèque");

        Pane root = new Pane();
        root.setPrefSize(1100, 450);
        root.setStyle("-fx-background-image: url('/images/cheque_bg.png');" +
                      "-fx-background-size: cover;" +
                      "-fx-background-repeat: no-repeat;");

        // Montant en chiffres
        Label montantChiffres = creerChamp(String.format("%.2f", cheque.getMontant()), 850, 37, 20);

        // Montant en lettres
        String montantLettre = MontantEnLettresService.convertirMontant(cheque.getMontant(), cheque.getLangue());

        Label ligne1 = new Label(montantLettre);
        ligne1.setFont(new Font(cheque.getLangue().equals("ar") ? 25 : 18));
        ligne1.setLayoutX(cheque.getLangue().equals("ar") ? 125 : 475);
        ligne1.setLayoutY(cheque.getLangue().equals("ar") ? 115 : 120);
        ligne1.setPrefWidth(cheque.getLangue().equals("ar") ? 880 : 650);
        ligne1.setWrapText(true);
        ligne1.setAlignment(cheque.getLangue().equals("ar") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        Label ligne2 = new Label(""); // champ vide au besoin
        ligne2.setLayoutY(155);
        ligne2.setLayoutX(cheque.getLangue().equals("ar") ? 50 : 60);
        ligne2.setPrefWidth(880);
        ligne2.setFont(new Font(cheque.getLangue().equals("ar") ? 25 : 19));
        ligne2.setWrapText(true);
        ligne2.setAlignment(cheque.getLangue().equals("ar") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        // Bénéficiaire
        Label beneficiaire = creerChamp(cheque.getBeneficiaire(),
                cheque.getLangue().equals("ar") ? 80 : 140, 190,
                cheque.getLangue().equals("ar") ? 25 : 20);
        beneficiaire.setPrefWidth(880);
        beneficiaire.setAlignment(cheque.getLangue().equals("ar") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        // Autres champs du chèque
        Label nomCheque = creerChamp(cheque.getNomCheque(), 110, 371, 20);
        Label nomSerie = creerChamp(cheque.getNomSerie(), 195, 371, 20);
        Label numeroSerie = creerChamp(String.valueOf(cheque.getNumeroSerie()), 270, 371, 20);
        Label ville = creerChamp(cheque.getVille(), 560, 228, 20);
        Label date = creerChamp(cheque.getDate().toString(), 825, 228, 20);

        // Bouton Modifier
        Button boutonModifier = new Button("✏️ Modifier");
        boutonModifier.setFont(new Font(15));
        boutonModifier.setStyle("-fx-background-color: linear-gradient(to right, #0D8BFF, #0052D4);" +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25px;");
        boutonModifier.setLayoutX(470);
        boutonModifier.setLayoutY(400);
        boutonModifier.setPrefWidth(150);
        boutonModifier.setOnAction(e -> {
            ChequeEditView.afficher(cheque, controller); // ✅ Appel correct avec controller
            stage.close(); // optionnel
        });

        root.getChildren().addAll(
                montantChiffres, ligne1, ligne2, beneficiaire,
                nomCheque, nomSerie, numeroSerie, ville, date,
                boutonModifier
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private static Label creerChamp(String texte, double x, double y, int fontSize) {
        Label label = new Label(texte);
        label.setFont(new Font(fontSize));
        label.setLayoutX(x);
        label.setLayoutY(y);
        return label;
    }
}
