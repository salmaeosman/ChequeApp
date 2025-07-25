package vues;

import entities.Cheque;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ChequePrintView {

    public static void showCheque(Stage parentStage, Cheque cheque, String montantLettre) {
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Impression Chèque");

        BorderPane rootLayout = new BorderPane();
        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        StackPane chequePane = new StackPane();
        chequePane.setPrefSize(800, 400);
        Image chequeImage = new Image(ChequePrintView.class.getResourceAsStream("/images/cheque_bg.png"));
        ImageView chequeView = new ImageView(chequeImage);
        chequeView.setPreserveRatio(true);
        chequeView.setFitWidth(800);

        Pane overlay = new Pane();
        overlay.setPrefSize(800, 400);

        boolean isArabic = "ar".equalsIgnoreCase(cheque.getLangue());

        // Montant en chiffres (format avec espace comme séparateur de milliers)
        overlay.getChildren().add(createLabel(formatMontant(cheque.getMontant()), 690, 52, 14, false));

        // Montant en lettres avec découpage en deux lignes
        String[] lignes = splitMontantLettre(montantLettre, cheque.getLangue());
        if (isArabic) {
            Label ligne1 = createLabel(lignes[0], 400, 112, 16, true);
            ligne1.setPrefWidth(400);
            ligne1.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            ligne1.setAlignment(Pos.CENTER_RIGHT);
            
            Label ligne2 = createLabel(lignes[1], 740, 140, 16, true);
            ligne2.setPrefWidth(400);
            ligne2.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            ligne2.setAlignment(Pos.CENTER_RIGHT);
            
            overlay.getChildren().addAll(ligne1, ligne2);
        } else {
            overlay.getChildren().add(createLabel(lignes[0], 400, 110, 16, false));
            overlay.getChildren().add(createLabel(lignes[1], 60, 140, 16, false));
        }

        // Bénéficiaire (alignement à droite pour l'arabe)
        if (isArabic) {
            Label arBenef = createLabel(cheque.getBeneficiaire(), 770, 166, 16, true);
            arBenef.setPrefWidth(270);
            arBenef.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            arBenef.setAlignment(Pos.CENTER_RIGHT);
            overlay.getChildren().add(arBenef);
        } else {
            overlay.getChildren().add(createLabel(cheque.getBeneficiaire(), 160, 165, 16, false));
        }

        // Autres champs (positions inchangées)
        overlay.getChildren().addAll(
                createLabel(cheque.getNomCheque(), 135, 294, 16, false),
                createLabel(cheque.getNomSerie(), 195, 294, 16, false),
                createLabel(cheque.getVille(), 460, 190, 16, false),
                createLabel(cheque.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 670, 190, 16, false),
                createLabel(String.valueOf(cheque.getNumeroSerie()), 250, 294, 16, false)
        );

        chequePane.getChildren().addAll(chequeView, overlay);

        // Boutons
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button printBtn = new Button("🖨 Imprimer");
        printBtn.setStyle(orangeBtnStyle());
        printBtn.setOnAction(e -> printCheque(cheque, montantLettre));

        Button backBtn = new Button("↩ Retour");
        backBtn.setStyle(orangeBtnStyle());
        backBtn.setOnAction(e -> {
            stage.close();
            if (parentStage != null) parentStage.show();
        });

        Button refreshBtn = new Button("🔁 Rafraîchir");
        refreshBtn.setStyle(orangeBtnStyle());
        refreshBtn.setOnAction(e -> {
            stage.close();
            showCheque(parentStage, cheque, montantLettre);
        });

        buttons.getChildren().addAll(printBtn, refreshBtn, backBtn);
        mainLayout.getChildren().addAll(chequePane, buttons);
        rootLayout.setCenter(mainLayout);

        Scene scene = new Scene(rootLayout, 900, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void showChequePrint(Cheque cheque, String montantLettre) {
        showCheque(null, cheque, montantLettre);
    }

    private static Label createLabel(String text, double x, double y, int fontSize, boolean isRTL) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        label.setWrapText(true);
        if (isRTL) {
            label.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            label.setAlignment(Pos.CENTER_RIGHT);
        }
        return label;
    }

    // Formatage du montant avec espace comme séparateur de milliers
    private static String formatMontant(Double montant) {
        if (montant == null) return "";
        return String.format(Locale.FRANCE, "%,.2f", montant)
                .replace(",", " ")
                .replace('.', ',');
    }

    // Découpage du montant en lettres en deux lignes
    private static String[] splitMontantLettre(String montantLettre, String langue) {
        int maxChars = 60;
        String ligne1 = "", ligne2 = "", centimes = "";

        if ("ar".equalsIgnoreCase(langue)) {
            // Séparation de la partie principale et des centimes
            String[] parts = montantLettre.split("\\s+و\\s+");
            ligne1 = parts[0];
            centimes = parts.length > 1 ? "و " + parts[1] : "";
        } else {
            String[] parts = montantLettre.split("\\s+et\\s+");
            ligne1 = parts[0];
            centimes = parts.length > 1 ? "et " + parts[1] : "";
        }

        // Découpage en deux lignes si nécessaire
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

    private static String orangeBtnStyle() {
        return "-fx-background-color: linear-gradient(to right, #f57c00, #ef6c00);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10 25;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 30;" +
                "-fx-cursor: hand;";
    }

    private static void printCheque(Cheque cheque, String montantLettre) {
        double printWidth = 496;
        double printHeight = 227;

        StackPane printPane = new StackPane();
        Image chequeImage = new Image(ChequePrintView.class.getResourceAsStream("/images/cheque_bg.png"));
        ImageView chequeView = new ImageView(chequeImage);
        chequeView.setFitWidth(printWidth);
        chequeView.setFitHeight(printHeight);

        Pane overlay = new Pane();
        overlay.setPrefSize(printWidth, printHeight);

        boolean isArabic = "ar".equalsIgnoreCase(cheque.getLangue());

        // Montant en chiffres (même formatage)
        overlay.getChildren().add(createLabel(formatMontant(cheque.getMontant()), 430, 25, 12, false));

        // Montant en lettres avec alignement arabe
        String[] lignes = splitMontantLettre(montantLettre, cheque.getLangue());
        if (isArabic) {
            Label ligne1 = createLabel(lignes[0], 270, 65, 14, true);
            ligne1.setPrefWidth(200);
            ligne1.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            ligne1.setAlignment(Pos.CENTER_RIGHT);
            
            Label ligne2 = createLabel(lignes[1], 270, 90, 14, true);
            ligne2.setPrefWidth(200);
            ligne2.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            ligne2.setAlignment(Pos.CENTER_RIGHT);
            
            overlay.getChildren().addAll(ligne1, ligne2);
        } else {
            overlay.getChildren().add(createLabel(lignes[0], 250, 65, 12, false));
            overlay.getChildren().add(createLabel(lignes[1], 30, 90, 12, false));
        }

        // Bénéficiaire avec alignement arabe
        if (isArabic) {
            Label arBenef = createLabel(cheque.getBeneficiaire(), 270, 110, 14, true);
            arBenef.setPrefWidth(200);
            arBenef.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            arBenef.setAlignment(Pos.CENTER_RIGHT);
            overlay.getChildren().add(arBenef);
        } else {
            overlay.getChildren().add(createLabel(cheque.getBeneficiaire(), 100, 100, 12, false));
        }

        // Autres champs (positions inchangées)
        overlay.getChildren().addAll(
                createLabel(cheque.getNomCheque(), 80, 190, 12, false),
                createLabel(cheque.getNomSerie(), 120, 190, 12, false),
                createLabel(cheque.getVille(), 280, 115, 12, false),
                createLabel(cheque.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 400, 115, 12, false),
                createLabel(String.valueOf(cheque.getNumeroSerie()), 160, 190, 12, false)
        );

        printPane.getChildren().addAll(chequeView, overlay);

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            boolean success = job.printPage(printPane);
            if (success) {
                job.endJob();
            }
        }
    }
}