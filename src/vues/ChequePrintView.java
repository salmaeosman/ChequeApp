package vues;

import java.util.ArrayList;
import java.util.List;

import controllers.ChequeController;
import entities.Cheque;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class ChequePrintView {

    private static Label ligne1;
    private static Label ligne2;

    public static void showChequePrint(Cheque cheque, String montantLettre, ChequeController controller) {
        Stage stage = new Stage();
        stage.setTitle("Visualisation du chèque");

        double dpi = 98;
        double widthPx = 17.5 * dpi / 2.54;
        double heightPx = 8.0 * dpi / 2.54;

        Image chequeImage = new Image(ChequePrintView.class.getResourceAsStream("/images/cheque_bg.png"));
        ImageView chequeView = new ImageView(chequeImage);
        chequeView.setFitWidth(widthPx);
        chequeView.setFitHeight(heightPx);

        Pane overlay = new Pane();
        overlay.setPrefSize(widthPx, heightPx);

        Label montantChiffres = creerChamp(String.format("%.2f", cheque.getMontant()), 500, 20, 13);
        montantChiffres.setPrefWidth(120);
        montantChiffres.setAlignment(Pos.CENTER_RIGHT);

        ligne1 = new Label();
        ligne2 = new Label();
        ligne1.setFont(new Font(12));
        ligne2.setFont(new Font(12));
        updateDirectionForArabic(cheque.getLangue());
        updateMontantEnLettres(montantLettre, cheque.getLangue());

        Label beneficiaire;
        if ("ar".equals(cheque.getLangue())) {
            beneficiaire = creerChamp(cheque.getBeneficiaire(), 40, 123, 12);
            beneficiaire.setPrefWidth(600);
            beneficiaire.setAlignment(Pos.CENTER_RIGHT);
        } else {
            beneficiaire = creerChamp(cheque.getBeneficiaire(), 115, 123, 12);
            beneficiaire.setPrefWidth(600);
            beneficiaire.setAlignment(Pos.CENTER_LEFT);
        }

        Label ville = creerChamp(cheque.getVille(), 380, 143, 16);
        Label date = creerChamp(cheque.getDate().toString(), 540, 143, 16);

        Label nomCheque = creerChamp(cheque.getNomCheque(), 95, 238, 14);
        Label nomSerie = creerChamp(cheque.getNomSerie(), 145, 238, 14);
        Label numeroSerie = creerChamp(String.valueOf(cheque.getNumeroSerie()), 185, 238, 14);

        overlay.getChildren().addAll(montantChiffres, ligne1, ligne2, beneficiaire, ville, date, nomCheque, nomSerie, numeroSerie);

        StackPane impressionPane = new StackPane(chequeView, overlay);
        impressionPane.setPrefSize(widthPx, heightPx);

        // Décalage léger vers la gauche pour éviter la coupe à droite
        impressionPane.setTranslateX(-10); // décalage vers la gauche de 10 pixels

        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(10);
        root.getChildren().add(impressionPane);

        Pane boutonsPane = new Pane();

        Button boutonImprimer = new Button("Imprimer");
        boutonImprimer.setFont(new Font(14));
        boutonImprimer.setStyle("-fx-background-color: #854e56; -fx-text-fill: white;");
        boutonImprimer.setLayoutX(100);
        boutonImprimer.setLayoutY(10);
        boutonImprimer.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(stage)) {
                PageLayout layout = job.getJobSettings().getPageLayout();

                double scaleX = layout.getPrintableWidth() / widthPx;
                double scaleY = layout.getPrintableHeight() / heightPx;
                double scale = Math.min(scaleX, scaleY);

                impressionPane.getTransforms().add(new Scale(scale, scale));
                boolean success = job.printPage(impressionPane);
                if (success) {
                    job.endJob();
                }
                impressionPane.getTransforms().clear();
            }
        });

        Button boutonRetour = new Button("Retour au formulaire");
        boutonRetour.setFont(new Font(14));
        boutonRetour.setStyle("-fx-background-color: #854e56; -fx-text-fill: white;");
        boutonRetour.setLayoutX(250);
        boutonRetour.setLayoutY(10);
        boutonRetour.setOnAction(e -> {
            stage.close();
            ChequeEditView.afficher(cheque, controller, () -> {
                ChequeVisualisationView.afficher(cheque, controller);
            });
        });

        Button boutonRafraichir = new Button("Rafraîchir");
        boutonRafraichir.setFont(new Font(14));
        boutonRafraichir.setStyle("-fx-background-color: #854e56; -fx-text-fill: white;");
        boutonRafraichir.setLayoutX(450);
        boutonRafraichir.setLayoutY(10);
        boutonRafraichir.setOnAction(e -> {
            stage.close();
            showChequePrint(cheque, montantLettre, controller);
        });

        boutonsPane.getChildren().addAll(boutonImprimer, boutonRetour, boutonRafraichir);
        root.getChildren().add(boutonsPane);

        Scene scene = new Scene(root, widthPx + 50, heightPx + 80);
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

    private static void updateDirectionForArabic(String langue) {
        ligne1.setFont(new Font(12));
        ligne2.setFont(new Font(12));

        if ("ar".equals(langue)) {
            ligne1.setLayoutX(-70);
            ligne1.setPrefWidth(690);
            ligne1.setAlignment(Pos.CENTER_RIGHT);

            ligne2.setLayoutX(-2);
            ligne2.setPrefWidth(670);
            ligne2.setAlignment(Pos.CENTER_RIGHT);
        } else {
            ligne1.setLayoutX(320);
            ligne1.setPrefWidth(650);
            ligne1.setAlignment(Pos.CENTER_LEFT);

            ligne2.setLayoutX(60);
            ligne2.setPrefWidth(650);
            ligne2.setAlignment(Pos.CENTER_LEFT);
        }

        ligne1.setLayoutY(70);
        ligne1.setWrapText(true);

        ligne2.setLayoutY(94);
        ligne2.setWrapText(true);
    }

    private static void updateMontantEnLettres(String montantLettreDirect, String langue) {
        try {
            String lettres = montantLettreDirect;
            javafx.scene.text.Text textMeasurer = new javafx.scene.text.Text();
            textMeasurer.setFont(Font.font("Arial", 12));
            double maxWidth = "ar".equals(langue) ? 250 : 300;

            String mainPart = lettres;
            String centimesPart = "";

            boolean isAr = "ar".equals(langue);
            String centimesKeyword = isAr ? "سنتيم" : "centime";
            int centimesIndex = lettres.indexOf(centimesKeyword);
            if (centimesIndex != -1) {
                int start = lettres.lastIndexOf(" ", centimesIndex - 1);
                centimesPart = lettres.substring(start).trim();
                mainPart = lettres.substring(0, start).trim();

                if (!isAr && mainPart.endsWith(" et")) {
                    centimesPart = "et " + centimesPart;
                    mainPart = mainPart.substring(0, mainPart.length() - 3).trim();
                }
            }

            List<String> blocks = new ArrayList<>();

            if (isAr) {
                // Découpage intelligent en arabe, regrouper autour de "و"
                String[] rawTokens = mainPart.split(" ");
                List<String> tokens = new ArrayList<>();
                for (String token : rawTokens) {
                    if (!token.isBlank()) tokens.add(token);
                }

                for (int i = 0; i < tokens.size(); i++) {
                    if (tokens.get(i).equals("و") && i > 0 && i < tokens.size() - 1) {
                        String bloc = tokens.get(i - 1) + " و " + tokens.get(i + 1);
                        blocks.remove(blocks.size() - 1); // enlever le mot précédent
                        blocks.add(bloc);
                        i++; // sauter le mot suivant
                    } else {
                        blocks.add(tokens.get(i));
                    }
                }
            } else {
                // Français : découpage classique par mot
                String[] words = mainPart.split(" ");
                for (String word : words) {
                    if (!word.isBlank()) blocks.add(word);
                }
            }

            StringBuilder l1 = new StringBuilder();
            StringBuilder l2 = new StringBuilder();
            StringBuilder currentLine = new StringBuilder();
            boolean onFirstLine = true;

            for (String block : blocks) {
                String tentative = currentLine.length() > 0 ? currentLine + " " + block : block;
                textMeasurer.setText(tentative);
                if (textMeasurer.getLayoutBounds().getWidth() <= maxWidth) {
                    if (currentLine.length() > 0) currentLine.append(" ");
                    currentLine.append(block);
                } else {
                    if (onFirstLine) {
                        l1 = new StringBuilder(currentLine.toString());
                        currentLine = new StringBuilder(block);
                        onFirstLine = false;
                    } else {
                        if (currentLine.length() > 0) currentLine.append(" ");
                        currentLine.append(block);
                    }
                }
            }

            if (onFirstLine) {
                l1 = new StringBuilder(currentLine.toString());
            } else {
                l2 = new StringBuilder(currentLine.toString());
            }

            // Ajouter les centimes à la fin de la ligne 2
            if (!centimesPart.isEmpty()) {
                if (l2.length() > 0) l2.append(" ");
                l2.append(centimesPart);
            }

            // Appliquer le texte
            ligne1.setText(l1.toString());
            ligne2.setText(l2.toString());

            // Mise en page en fonction de la langue
            ligne1.setFont(new Font(12));
            ligne2.setFont(new Font(12));

            ligne1.setWrapText(true);
            ligne2.setWrapText(true);

            ligne1.setLayoutY(75);
            ligne2.setLayoutY(99);

            if (isAr) {
                ligne1.setLayoutX(-70);
                ligne1.setPrefWidth(690);
                ligne1.setAlignment(Pos.CENTER_RIGHT);

                ligne2.setLayoutX(-2);
                ligne2.setPrefWidth(670);
                ligne2.setAlignment(Pos.CENTER_RIGHT);
            } else {
                ligne1.setLayoutX(320);
                ligne1.setPrefWidth(650);
                ligne1.setAlignment(Pos.CENTER_LEFT);

                ligne2.setLayoutX(60);
                ligne2.setPrefWidth(650);
                ligne2.setAlignment(Pos.CENTER_LEFT);
            }

        } catch (Exception ex) {
            ligne1.setText("");
            ligne2.setText("");
        }
    }
}