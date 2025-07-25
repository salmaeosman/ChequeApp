package vues;

import entities.Cheque;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.format.DateTimeFormatter;

public class ChequePrintView {

    public static void showCheque(Stage parentStage, Cheque cheque, String montantLettre) {
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Impression Chèque");

        Pane root = new Pane();
        root.setStyle("""
            -fx-background-color: white;
            -fx-background-image: url('/images/cheque_bg.png');
            -fx-background-repeat: no-repeat;
            -fx-background-size: 800;
            -fx-font-family: Arial;
        """);

        // Montant chiffres (top right)
        Label montantChiffres = createLabel(formatMontant(cheque.getMontant()), 930, 37, 20);
        root.getChildren().add(montantChiffres);

        // Montant lettres ligne 1 et 2
        String[] lignes = splitMontantLettre(montantLettre, cheque.getLangue());
        Label ligne1 = createLabel(lignes[0], 475, 120, 18);
        Label ligne2 = createLabel(lignes[1], 60, 155, 19);
        root.getChildren().addAll(ligne1, ligne2);

        // Bénéficiaire
        root.getChildren().add(createLabel(cheque.getBeneficiaire(), 140, 190, 20));

        // Autres champs
        root.getChildren().addAll(
                createLabel(cheque.getNomCheque(), 110, 371, 20),
                createLabel(cheque.getNomSerie(), 195, 371, 20),
                createLabel(cheque.getVille(), 560, 228, 20),
                createLabel(cheque.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 825, 228, 20),
                createLabel(String.valueOf(cheque.getNumeroSerie()), 270, 371, 20)
        );

        // Bouton imprimer
        Button printBtn = new Button("🖨 Imprimer le chèque");
        printBtn.setStyle(styleBtn());
        printBtn.setLayoutX(420);
        printBtn.setLayoutY(20);
        printBtn.setOnAction(e -> javafx.print.PrinterJob.createPrinterJob().printPage(root));
        root.getChildren().add(printBtn);

        // Bouton retour
        Button backBtn = new Button("Retour");
        backBtn.setStyle(styleBtn());
        backBtn.setLayoutX(450);
        backBtn.setLayoutY(550);
        backBtn.setOnAction(e -> {
            stage.close();
            if (parentStage != null) parentStage.show(); // revenir à la fenêtre précédente si dispo
        });
        root.getChildren().add(backBtn);

        Scene scene = new Scene(root, 1100, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void showChequePrint(Cheque cheque, String montantLettre) {
        showCheque(null, cheque, montantLettre);
    }

    private static Label createLabel(String text, double x, double y, int fontSize) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        return label;
    }

    private static String formatMontant(Double montant) {
        if (montant == null) return "";
        return String.format("%.2f", montant).replace(",", " ");
    }

    private static String[] splitMontantLettre(String montantLettre, String langue) {
        int maxChars = 60;
        String ligne1 = "", ligne2 = "";
        String centimes = "";

        if (langue != null && langue.equals("ar")) {
            String[] parts = montantLettre.split("\\s+و\\s+");
            ligne1 = parts[0];
            centimes = parts.length > 1 ? "و " + parts[1] : "";
        } else {
            String[] parts = montantLettre.split("\\s+et\\s+");
            ligne1 = parts[0];
            centimes = parts.length > 1 ? "et " + parts[1] : "";
        }

        if (ligne1.length() > maxChars) {
            int index = ligne1.lastIndexOf(' ', maxChars);
            if (index != -1) {
                ligne2 = ligne1.substring(index + 1) + " " + centimes;
                ligne1 = ligne1.substring(0, index);
            } else {
                ligne2 = centimes;
            }
        } else {
            ligne2 = centimes;
        }

        return new String[]{ligne1.trim(), ligne2.trim()};
    }

    private static String styleBtn() {
        return "-fx-background-color: linear-gradient(to right, #28a745, #218838);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 12 30;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 30;" +
                "-fx-cursor: hand;";
    }
}
