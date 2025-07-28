package vues;

import controllers.ChequeController;
import entities.Cheque;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChequeVisualisationView {

    // Méthode publique appelée depuis ChequeFiltreView
    public static void afficher(Cheque cheque, ChequeController controller) {
        String montantLettre = controller.convertirMontantEnLettre(cheque.getMontant(), cheque.getLangue());
        showChequePrint(cheque, montantLettre, controller);
    }

    public static void showChequePrint(Cheque cheque, String montantLettre, ChequeController controller) {
        Stage stage = new Stage();
        stage.setTitle("Visualisation du chèque");

        Image chequeImage = new Image(ChequeVisualisationView.class.getResourceAsStream("/images/cheque_bg.png"));
        ImageView chequeView = new ImageView(chequeImage);
        chequeView.setPreserveRatio(true);
        chequeView.setFitWidth(800);

        Pane overlay = new Pane();
        overlay.setPrefSize(800, 400);

        Label montantChiffres = creerChamp(String.format("%.2f", cheque.getMontant()), 680, 146, 20);

        Label ligne1 = new Label(montantLettre);
        if (cheque.getLangue().equals("ar")) {
            ligne1.setFont(new Font(16));
            ligne1.setLayoutX(95);
            ligne1.setPrefWidth(670);
            ligne1.setAlignment(Pos.CENTER_RIGHT);
        } else {
            ligne1.setFont(new Font(16));
            ligne1.setLayoutX(400);
            ligne1.setPrefWidth(650);
            ligne1.setAlignment(Pos.CENTER_LEFT);
        }
        ligne1.setLayoutY(209);
        ligne1.setWrapText(true);

        Label ligne2 = new Label("");
        ligne2.setLayoutY(155);
        if (cheque.getLangue().equals("ar")) {
            ligne2.setLayoutX(50);
            ligne2.setFont(new Font(25));
            ligne2.setAlignment(Pos.CENTER_RIGHT);
        } else {
            ligne2.setLayoutX(60);
            ligne2.setFont(new Font(19));
            ligne2.setAlignment(Pos.CENTER_LEFT);
        }
        ligne2.setPrefWidth(700);
        ligne2.setWrapText(true);

        Label beneficiaire;
        if (cheque.getLangue().equals("ar")) {
            beneficiaire = creerChamp(cheque.getBeneficiaire(), 195, 260, 18);
            beneficiaire.setPrefWidth(600);
            beneficiaire.setAlignment(Pos.CENTER_RIGHT);
        } else {
            beneficiaire = creerChamp(cheque.getBeneficiaire(), 153, 260, 18);
            beneficiaire.setPrefWidth(600);
            beneficiaire.setAlignment(Pos.CENTER_LEFT);
        }

        Label nomCheque = creerChamp(cheque.getNomCheque(), 135, 390, 18);
        Label nomSerie = creerChamp(cheque.getNomSerie(), 195, 390, 18);
        Label numeroSerie = creerChamp(String.valueOf(cheque.getNumeroSerie()), 240, 390, 18);
        Label ville = creerChamp(cheque.getVille(), 460, 287, 18);
        Label date = creerChamp(cheque.getDate().toString(), 650, 287, 18);

        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(900, 600);
        stackPane.getChildren().addAll(chequeView, overlay);

        Button boutonRafraichir = new Button("Rafraîchir");
        boutonRafraichir.setFont(new Font(14));
        boutonRafraichir.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
        boutonRafraichir.setLayoutX(530);
        boutonRafraichir.setLayoutY(500);
        boutonRafraichir.setPrefWidth(150);
        boutonRafraichir.setOnAction(e -> {
            stage.close();
            showChequePrint(cheque, montantLettre, controller);
        });

        Button boutonModifier = new Button("Modifier");
        boutonModifier.setFont(new Font(14));
        boutonModifier.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
        boutonModifier.setLayoutX(200);
        boutonModifier.setLayoutY(500);
        boutonModifier.setPrefWidth(150);
        boutonModifier.setOnAction(e -> {
            stage.close();
            ChequeEditView.afficher(cheque, controller, () -> {
                afficher(cheque, controller);
            });
        });

        // 🔍 BOUTON POUR SCANNER LE CHÈQUE
        Button boutonScanner = new Button("Scanner");
        boutonScanner.setFont(new Font(14));
        boutonScanner.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
        boutonScanner.setLayoutX(365);
        boutonScanner.setLayoutY(500);
        boutonScanner.setPrefWidth(150);
        boutonScanner.setOnAction(e -> {
            stage.close();
            // Ouvre la vue de scan avec le chèque courant
            ScanView.scanCheque(cheque);
        });

        overlay.getChildren().addAll(
                montantChiffres, ligne1, ligne2, beneficiaire,
                nomCheque, nomSerie, numeroSerie, ville, date,
                boutonRafraichir, boutonModifier, boutonScanner
        );

        Scene scene = new Scene(stackPane, 900, 600);
        stage.setScene(scene);
        stage.setResizable(false);
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