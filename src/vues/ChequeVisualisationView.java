package vues;

import controllers.ChequeController;
import controllers.ScanController;
import db.H2Database;
import entities.Cheque;
import entities.Scan;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import repositories.ChequeRepository;
import repositories.ScanRepository;
import services.ScanService;

import java.io.File;

public class ChequeVisualisationView {

    private static Label ligne1;
    private static Label ligne2;

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

        Label montantChiffres = creerChamp(String.format("%.2f", cheque.getMontant()), 660, 146, 20);

        ligne1 = new Label();
        ligne2 = new Label();

        updateDirectionForArabic(cheque.getLangue());
        updateMontantEnLettres(montantLettre, cheque.getLangue());

        Label beneficiaire;
        if ("ar".equals(cheque.getLangue())) {
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

        Button boutonModifier = createButton("Modifier", 200, e -> {
            stage.close();
            ChequeEditView.afficher(cheque, controller, () -> {
                afficher(cheque, controller);
            });
        });

        Button boutonScanner = createButton("Scanner", 365, e -> {
            stage.close();
            ScanView.scanCheque(cheque);
        });

        // "Voir le scan" remplace maintenant "Rafraîchir" (position X = 530)
        Button boutonVoirScan = createButton("Voir le scan", 530, e -> {
            File scanFile = getScanFileFromDatabase(cheque.getId());
            if (scanFile != null && scanFile.exists()) {
                ScanDisplayView.afficher(scanFile.getAbsolutePath());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Scan introuvable");
                alert.setHeaderText(null);
                alert.setContentText("Ce chèque n’a pas encore été scanné. Veuillez le scanner maintenant.");
                alert.showAndWait();
                stage.close();
                ScanView.scanCheque(cheque);
            }
        });

        Button boutonRetour = createButton("Retour au filtre", 20, e -> {
            stage.close();
            try {
                new ChequeFiltreView().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        boutonRetour.setLayoutY(20);

        overlay.getChildren().addAll(
                montantChiffres, ligne1, ligne2, beneficiaire,
                nomCheque, nomSerie, numeroSerie, ville, date,
                boutonModifier, boutonScanner, boutonRetour, boutonVoirScan
        );

        Scene scene = new Scene(stackPane, 900, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private static Button createButton(String text, double layoutX, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setFont(new Font(14));
        button.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
        button.setLayoutX(layoutX);
        button.setLayoutY(500);
        button.setPrefWidth(150);
        button.setOnAction(action);
        return button;
    }

    private static Label creerChamp(String texte, double x, double y, int fontSize) {
        Label label = new Label(texte);
        label.setFont(new Font(fontSize));
        label.setLayoutX(x);
        label.setLayoutY(y);
        return label;
    }

    private static void updateDirectionForArabic(String langue) {
        ligne1.setFont(new Font(16));
        ligne2.setFont(new Font(16));

        if ("ar".equals(langue)) {
            ligne1.setLayoutX(95);
            ligne1.setPrefWidth(670);
            ligne1.setAlignment(Pos.CENTER_RIGHT);

            ligne2.setLayoutX(155);
            ligne2.setPrefWidth(670);
            ligne2.setAlignment(Pos.CENTER_RIGHT);
        } else {
            ligne1.setLayoutX(395);
            ligne1.setPrefWidth(650);
            ligne1.setAlignment(Pos.CENTER_LEFT);

            ligne2.setLayoutX(100);
            ligne2.setPrefWidth(650);
            ligne2.setAlignment(Pos.CENTER_LEFT);
        }

        ligne1.setLayoutY(209);
        ligne1.setWrapText(true);

        ligne2.setLayoutY(235);
        ligne2.setWrapText(true);
    }

    private static void updateMontantEnLettres(String montantLettreDirect, String langue) {
        try {
            String lettres = montantLettreDirect;

            javafx.scene.text.Text textMeasurer = new javafx.scene.text.Text();
            textMeasurer.setFont(Font.font("Arial", 16));

            double maxWidth = "ar".equals(langue) ? 300 : 370;

            String[] words = lettres.split("\\s+");
            StringBuilder ligne1Text = new StringBuilder();
            StringBuilder ligne2Text = new StringBuilder();

            for (String word : words) {
                String tentative = ligne1Text.length() > 0 ? ligne1Text + " " + word : word;
                textMeasurer.setText(tentative);
                double width = textMeasurer.getLayoutBounds().getWidth();

                if (width <= maxWidth) {
                    if (ligne1Text.length() > 0) ligne1Text.append(" ");
                    ligne1Text.append(word);
                } else {
                    if (ligne2Text.length() > 0) ligne2Text.append(" ");
                    ligne2Text.append(word);
                }
            }

            ligne1.setText(ligne1Text.toString());
            ligne2.setText(ligne2Text.toString());
        } catch (Exception ex) {
            ligne1.setText("");
            ligne2.setText("");
        }
    }

    private static File getScanFileFromDatabase(Long chequeId) {
        H2Database db = new H2Database();
        ScanRepository scanRepository = new ScanRepository(db);
        ChequeRepository chequeRepository = new ChequeRepository(db);
        ScanService scanService = new ScanService(scanRepository, chequeRepository);
        ScanController scanController = new ScanController(scanService);

        try {
            Scan scan = scanController.getScanByChequeId(chequeId);
            if (scan != null) {
                String fileName = scan.getFileName();
                File file = new File("scans/" + fileName);
                return file.exists() ? file : null;
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}
