package vues;

import controllers.ChequeController;
import entities.Cheque;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import services.MontantEnLettresService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChequeEditView {
     	private static Stage currentStage;
	    private static Label ligne1;
	    private static Label ligne2;

	    private static final Map<String, String> arabicMapAZERTY = Map.ofEntries(
	    	    Map.entry("a", "ش"), Map.entry("z", "ئ"), Map.entry("e", "ث"), Map.entry("r", "ق"),
	    	    Map.entry("t", "ف"), Map.entry("y", "غ"), Map.entry("u", "ع"), Map.entry("i", "ه"),
	    	    Map.entry("o", "خ"), Map.entry("p", "ح"), Map.entry("q", "ض"), Map.entry("s", "س"),
	    	    Map.entry("d", "ي"), Map.entry("f", "ب"), Map.entry("g", "ل"), Map.entry("h", "ا"),
	    	    Map.entry("j", "ت"), Map.entry("k", "ن"), Map.entry("l", "م"), Map.entry("m", "ة"),
	    	    Map.entry("w", "ص"), Map.entry("x", "ء"), Map.entry("c", "ؤ"), Map.entry("v", "ر"),
	    	    Map.entry("b", "لا"), Map.entry("n", "ى"), Map.entry(",", "و"), Map.entry(";", "ز"),
	    	    Map.entry(":", "ط"), Map.entry("!", "ظ"), Map.entry(")", "ك"), Map.entry("=", "ذ"),
	    	    Map.entry("-", "د"), Map.entry("'", "ج")
	    	);

	    	private static final Map<String, String> arabicMapQWERTY = Map.ofEntries(
	    	    Map.entry("q", "ض"), Map.entry("w", "ص"), Map.entry("e", "ث"), Map.entry("r", "ق"),
	    	    Map.entry("t", "ف"), Map.entry("y", "غ"), Map.entry("u", "ع"), Map.entry("i", "ه"),
	    	    Map.entry("o", "خ"), Map.entry("p", "ح"), Map.entry("[", "ج"), Map.entry("]", "د"),
	    	    Map.entry("\\", "ذ"),

	    	    Map.entry("a", "ش"), Map.entry("s", "س"), Map.entry("d", "ي"), Map.entry("f", "ب"),
	    	    Map.entry("g", "ل"), Map.entry("h", "ا"), Map.entry("j", "ت"), Map.entry("k", "ن"),
	    	    Map.entry("l", "م"), Map.entry(";", "ك"), Map.entry("'", "ط"),

	    	    Map.entry("z", "ئ"), Map.entry("x", "ء"), Map.entry("c", "ؤ"), Map.entry("v", "ر"),
	    	    Map.entry("b", "لا"), Map.entry("n", "ى"), Map.entry("m", "ة"), Map.entry(",", "و"),
	    	    Map.entry(".", "ز"), Map.entry("/", "ظ")
	    	);

	    	private static Map<String, String> arabicMap;

	    	static {
	    	    String layout = System.getProperty("user.language") + "_" + System.getProperty("user.country");
	    	    if (layout.startsWith("fr")) {
	    	        arabicMap = arabicMapAZERTY;
	    	    } else if (layout.startsWith("en")) {
	    	        arabicMap = arabicMapQWERTY;
	    	    } else {
	    	        arabicMap = arabicMapAZERTY;
	    	    }
	    	}

	    private static final Map<String, Map<String, String>> messages = Map.of(
	            "fr", Map.of(
	                    "montant", "Montant invalide.",
	                    "beneficiaire", "Champ obligatoire.",
	                    "ville", "Champ obligatoire.",
	                    "nomCheque", "Lettre majuscule obligatoire.",
	                    "nomSerie", "Lettre majuscule obligatoire.",
	                    "numeroSerie", "Numéro invalide.",
	                    "date", "Date obligatoire."
	            ),
	            "ar", Map.of(
	                    "montant", "المبلغ غير صالح.",
	                    "beneficiaire", "حقل إلزامي.",
	                    "ville", "حقل إلزامي.",
	                    "nomCheque", "حرف كبير فقط.",
	                    "nomSerie", "حرف كبير فقط.",
	                    "numeroSerie", "رقم غير صالح.",
	                    "date", "التاريخ إلزامي."
	            )
	    );

	    private static Button enregistrerBtn;
	    private static Button imprimerBtn;

	    public static void afficher(Cheque cheque, ChequeController controller, Runnable onSaveCallback) {
	    	 if (currentStage != null) {
	    	        currentStage.close();
	    	    }
	        Stage stage = new Stage();
	        stage.setTitle("Modifier le chèque");

	        final Cheque[] chequeCourant = new Cheque[]{cheque};
	        String langue = cheque.getLangue();
	        Map<String, TextField> fields = new HashMap<>();
	        Map<String, Label> errors = new HashMap<>();

	        BorderPane root = new BorderPane();
	        VBox layout = new VBox(10);
	        layout.setAlignment(Pos.TOP_CENTER);

	        Label title = new Label("Modifier le chèque");
	        title.setFont(Font.font("Arial", 36));
	        title.setStyle("-fx-text-fill: #e78212;");

	        StackPane chequePane = new StackPane();
	        Image chequeImage = new Image(ChequeEditView.class.getResourceAsStream("/images/cheque_bg.png"));
	        ImageView chequeView = new ImageView(chequeImage);
	        chequeView.setPreserveRatio(true);
	        chequeView.setFitWidth(800);

	        Pane overlay = new Pane();
	        overlay.setPrefSize(800, 400);

	        // Champs
	        addChamp("montant", String.format(Locale.US, "%.2f", cheque.getMontant()), 630, 45, 160, fields, errors, overlay, langue);
	        ligne1 = createDisplayLabel(395, 209, 600);
	        ligne2 = createDisplayLabel(100, 235, 600);
	        overlay.getChildren().addAll(ligne1, ligne2);

	        updateDirectionForArabic(langue);
	        updateMontantEnLettres(fields.get("montant").getText(), langue);
	        fields.get("montant").setOnKeyReleased(e -> updateMontantEnLettres(fields.get("montant").getText(), langue));

	        if ("ar".equals(langue)) {
	            addChamp("beneficiaire", cheque.getBeneficiaire(), 735, 165, 600, fields, errors, overlay, langue);
	            addChamp("ville", cheque.getVille(), 470, 190, 600, fields, errors, overlay, langue);
	        } else {
	            addChamp("beneficiaire", cheque.getBeneficiaire(), 150, 163, 600, fields, errors, overlay, langue);
	            addChamp("ville", cheque.getVille(), 470, 190, 180, fields, errors, overlay, langue);
	        }

	        addChamp("nomCheque", cheque.getNomCheque(), 135, 293, 30, fields, errors, overlay, langue);
	        addChamp("nomSerie", cheque.getNomSerie(), 185, 293, 30, fields, errors, overlay, langue);
	        addChamp("numeroSerie", String.valueOf(cheque.getNumeroSerie()), 240, 293, 100, fields, errors, overlay, langue);

	        DatePicker dateField = new DatePicker(cheque.getDate());
	        dateField.setLayoutX(620);
	        dateField.setLayoutY(190);
	        dateField.setPrefWidth(150);
	        overlay.getChildren().add(dateField);

	        // 🔁 Validation dynamique à chaque modification
	        Runnable validateAll = () -> {
	            boolean isValid = validateForm(fields, errors, dateField, langue);
	            enregistrerBtn.setDisable(!isValid);
	            imprimerBtn.setDisable(!isValid);
	        };
	        fields.values().forEach(tf -> tf.textProperty().addListener((obs, o, n) -> validateAll.run()));
	        dateField.valueProperty().addListener((obs, o, n) -> validateAll.run());

	       

	        enregistrerBtn = bouton("💾 Enregistrer", 300, 400);
	        enregistrerBtn.setDisable(true);
	        enregistrerBtn.setOnAction(e -> {
	            if (!validateForm(fields, errors, dateField, langue)) return;

	            try {
	                Cheque maj = new Cheque();
	                maj.setId(cheque.getId());
	                maj.setMontant(Double.parseDouble(fields.get("montant").getText()));
	                maj.setBeneficiaire(fields.get("beneficiaire").getText());
	                maj.setVille(fields.get("ville").getText());
	                maj.setDate(dateField.getValue());
	                maj.setNomCheque(fields.get("nomCheque").getText());
	                maj.setNomSerie(fields.get("nomSerie").getText());
	                maj.setNumeroSerie(Long.parseLong(fields.get("numeroSerie").getText()));
	                maj.setLangue(langue);

	                // Modification du chèque
	                if (controller.modifierCheque(cheque.getId(), maj)) {
	                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Chèque modifié.");

	                    chequeCourant[0] = maj; // ✅ Mettre à jour le chèque courant
	                    stage.close(); // ✅ Fermer avant d'ouvrir la nouvelle vue

	                    // ✅ Affichage de la version modifiée
	                    ChequeVisualisationView.afficher(maj, controller);

	                    // Callback éventuel
	                    if (onSaveCallback != null) onSaveCallback.run();
	                } else {
	                    showAlert(Alert.AlertType.ERROR, "Erreur", "Modification échouée.");
	                }
	            } catch (Exception ex) {
	                ex.printStackTrace();
	                showAlert(Alert.AlertType.ERROR, "Erreur", "Exception : " + ex.getMessage());
	            }
	        });
	        imprimerBtn = bouton("Imprimer", 500, 400);
	        imprimerBtn.setDisable(true);
	        imprimerBtn.setOnAction(e -> {
	            // ✅ Avant impression : Affichage d’un aperçu
	            Stage previewStage = new Stage();
	            previewStage.setTitle("Aperçu du chèque");

	            StackPane impressionPane = createChequePreview(fields, dateField, langue);
	            ScrollPane scrollPane = new ScrollPane(impressionPane);
	            scrollPane.setFitToWidth(true);
	            scrollPane.setFitToHeight(true);

	            Button lancerImpression = new Button("Imprimer");
	            lancerImpression.setOnAction(ev -> {
	                PrinterJob job = PrinterJob.createPrinterJob();
	                if (job != null && job.showPrintDialog(previewStage)) {
	                    double scaleX = job.getJobSettings().getPageLayout().getPrintableWidth() / impressionPane.getWidth();
	                    double scaleY = job.getJobSettings().getPageLayout().getPrintableHeight() / impressionPane.getHeight();
	                    double scale = Math.min(scaleX, scaleY);
	                    impressionPane.getTransforms().add(new Scale(scale, scale));
	                    boolean success = job.printPage(impressionPane);
	                    if (success) job.endJob();
	                    impressionPane.getTransforms().clear();
	                }
	            });

	            VBox previewLayout = new VBox(scrollPane, lancerImpression);
	            Scene previewScene = new Scene(previewLayout, 900, 500);
	            previewStage.setScene(previewScene);
	            previewStage.show();
	        });

	        overlay.getChildren().addAll(enregistrerBtn, imprimerBtn);
	        chequePane.getChildren().addAll(chequeView, overlay);
	        layout.getChildren().addAll(title, chequePane);
	        root.setCenter(layout);

	        Scene scene = new Scene(root, 900, 600);
	        stage.setScene(scene);
	        stage.setResizable(false);
	        stage.show();

	        validateAll.run();
	    }
	    private static StackPane createChequePreview(Map<String, TextField> fields, DatePicker dateField, String langue) {
	        // Dimensions du chèque en pixels pour impression à 96 DPI (17.5 cm x 8 cm)
	        double dpi = 96.0;
	        double widthPx = 17.5 * dpi / 2.54; // ≈ 661 pixels
	        double heightPx = 8.0 * dpi / 2.54; // ≈ 302 pixels

	        // Image du fond de chèque
	        Image chequeImage = new Image(ChequeEditView.class.getResourceAsStream("/images/cheque_bg.png"));
	        ImageView chequeView = new ImageView(chequeImage);
	        chequeView.setFitWidth(widthPx);
	        chequeView.setFitHeight(heightPx);

	        // Calque pour les textes
	        Pane overlay = new Pane();
	        overlay.setPrefSize(widthPx, heightPx);

	        // Montant en chiffres
	        Label montantChiffres = new Label(String.format("%.2f", Double.parseDouble(fields.get("montant").getText())));
	        montantChiffres.setFont(Font.font(16));
	        montantChiffres.setLayoutX(460); // aligné à droite
	        montantChiffres.setLayoutY(18);
	        montantChiffres.setPrefWidth(120);
	        montantChiffres.setAlignment(Pos.CENTER_RIGHT);

	        // Montant en lettres ligne 1 et 2
	        Label l1 = new Label();
	        Label l2 = new Label();
	        l1.setFont(new Font(16));
	        l2.setFont(new Font(16));
	        l1.setLayoutY(70);
	        l2.setLayoutY(94);
	        l1.setWrapText(true);
	        l2.setWrapText(true);

	        String montantLettre = MontantEnLettresService.convertirMontant(
	            Double.parseDouble(fields.get("montant").getText()), langue);

	        javafx.scene.text.Text textMeasurer = new javafx.scene.text.Text();
	        textMeasurer.setFont(Font.font("Arial", 16));
	        double maxWidth = langue.equals("ar") ? 260 : 300;
	        String[] words = montantLettre.split("\\s+");
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
	        l1.setText(ligne1Text.toString());
	        l2.setText(ligne2Text.toString());

	        if ("ar".equals(langue)) {
	            l1.setLayoutX(-35);
	            l1.setPrefWidth(widthPx - 40);
	            l1.setAlignment(Pos.CENTER_RIGHT);
	            l2.setLayoutX(20);
	            l2.setPrefWidth(widthPx - 40);
	            l2.setAlignment(Pos.CENTER_RIGHT);
	        } else {
	            l1.setLayoutX(280);
	            l1.setPrefWidth(widthPx - 80);
	            l1.setAlignment(Pos.CENTER_LEFT);
	            l2.setLayoutX(35);
	            l2.setPrefWidth(widthPx - 80);
	            l2.setAlignment(Pos.CENTER_LEFT);
	        }

	     // ✅ Bénéficiaire affiché correctement selon la langue
	        Label beneficiaire = new Label(fields.get("beneficiaire").getText());
	        beneficiaire.setFont(Font.font("Arial", 14));
	        beneficiaire.setLayoutY(116);
	        beneficiaire.setPrefHeight(25);
	        beneficiaire.setPrefWidth(500); // largeur commune aux deux cas

	        if ("ar".equals(langue)) {
	            beneficiaire.setLayoutX(115); // largeur champ + marge droite
	            beneficiaire.setAlignment(Pos.BASELINE_LEFT);
	            beneficiaire.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
	            beneficiaire.setStyle("-fx-text-alignment: right; -fx-label-padding: 0;");
	        } else {
	            beneficiaire.setLayoutX(90);
	            beneficiaire.setAlignment(Pos.BASELINE_LEFT);
	            beneficiaire.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
	            beneficiaire.setStyle("-fx-text-alignment: left;");
	        }

	        // Ville
	        Label ville = new Label(fields.get("ville").getText());
	        ville.setFont(Font.font(14));
	        ville.setLayoutX(350); // environ 460
	        ville.setLayoutY(143);

	        // Date
	        Label date = new Label(dateField.getValue().toString());
	        date.setFont(Font.font(16));
	        date.setLayoutX(500); // environ 620
	        date.setLayoutY(140);

	        // Série chèque
	        Label nomCheque = new Label(fields.get("nomCheque").getText());
	        nomCheque.setFont(Font.font(14));
	        nomCheque.setLayoutX(70);
	        nomCheque.setLayoutY(233); // ≈ 238

	        Label nomSerie = new Label(fields.get("nomSerie").getText());
	        nomSerie.setFont(Font.font(14));
	        nomSerie.setLayoutX(120);
	        nomSerie.setLayoutY(233);

	        Label numeroSerie = new Label(fields.get("numeroSerie").getText());
	        numeroSerie.setFont(Font.font(14));
	        numeroSerie.setLayoutX(180);
	        numeroSerie.setLayoutY(233);

	        // Ajouter tous les éléments
	        overlay.getChildren().addAll(
	            montantChiffres, l1, l2, beneficiaire, ville, date,
	            nomCheque, nomSerie, numeroSerie
	        );

	        StackPane impressionPane = new StackPane(chequeView, overlay);
	        impressionPane.setPrefSize(widthPx, heightPx);
	        impressionPane.setMaxSize(widthPx, heightPx);
	        impressionPane.setMinSize(widthPx, heightPx);
	        impressionPane.setTranslateX(0); // plus de décalage inutile

	        return impressionPane;
	    }
	    private static void updateDirectionForArabic(String langue) {
	        ligne1.setFont(Font.font("Arial", 16));
	        ligne2.setFont(Font.font("Arial", 16));
	        ligne1.setWrapText(true);
	        ligne2.setWrapText(true);

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

	        ligne1.setLayoutY(111);
	        ligne2.setLayoutY(139);
	    }

	    private static void updateMontantEnLettres(String montantStr, String langue) {
	        try {
	            double montant = Double.parseDouble(montantStr);
	            String lettres = MontantEnLettresService.convertirMontant(montant, langue);

	            javafx.scene.text.Text textMeasurer = new javafx.scene.text.Text();
	            textMeasurer.setFont(Font.font("Arial", 16));

	            double maxWidth = "ar".equals(langue) ? 370 : 370;

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
	                // Regrouper les blocs logiques en arabe (ex: "مائة و عشرون")
	                String[] rawTokens = mainPart.split(" ");
	                List<String> tokens = new ArrayList<>();
	                for (String token : rawTokens) {
	                    if (!token.isBlank()) tokens.add(token);
	                }

	                for (int i = 0; i < tokens.size(); i++) {
	                    if (tokens.get(i).equals("و") && i > 0 && i < tokens.size() - 1) {
	                        String bloc = tokens.get(i - 1) + " و " + tokens.get(i + 1);
	                        blocks.remove(blocks.size() - 1); // Supprimer le mot précédent
	                        blocks.add(bloc);
	                        i++; // Sauter le mot suivant
	                    } else {
	                        blocks.add(tokens.get(i));
	                    }
	                }
	            } else {
	                // Français : découper normalement
	                String[] words = mainPart.split(" ");
	                for (String word : words) {
	                    if (!word.isBlank()) blocks.add(word);
	                }
	            }

	            StringBuilder ligne1Text = new StringBuilder();
	            StringBuilder ligne2Text = new StringBuilder();

	            StringBuilder currentLine = new StringBuilder();
	            boolean onFirstLine = true;

	            for (String block : blocks) {
	                String tentative = currentLine.length() > 0 ? currentLine + " " + block : block;
	                textMeasurer.setText(tentative);
	                double width = textMeasurer.getLayoutBounds().getWidth();

	                if (width <= maxWidth) {
	                    if (currentLine.length() > 0) currentLine.append(" ");
	                    currentLine.append(block);
	                } else {
	                    if (onFirstLine) {
	                        ligne1Text = new StringBuilder(currentLine.toString());
	                        currentLine = new StringBuilder(block);
	                        onFirstLine = false;
	                    } else {
	                        if (currentLine.length() > 0) currentLine.append(" ");
	                        currentLine.append(block);
	                    }
	                }
	            }

	            if (onFirstLine) {
	                ligne1Text = new StringBuilder(currentLine.toString());
	            } else {
	                ligne2Text = new StringBuilder(currentLine.toString());
	            }

	            // Ajouter les centimes à la fin de la ligne 2
	            if (!centimesPart.isEmpty()) {
	                if (ligne2Text.length() > 0) ligne2Text.append(" ");
	                ligne2Text.append(centimesPart);
	            }

	            ligne1.setText(ligne1Text.toString());
	            ligne2.setText(ligne2Text.toString());

	            // Configuration visuelle (alignement, position, largeur, etc.)
	            ligne1.setFont(Font.font("Arial", 16));
	            ligne2.setFont(Font.font("Arial", 16));
	            ligne1.setWrapText(true);
	            ligne2.setWrapText(true);

	            ligne1.setLayoutY(111);
	            ligne2.setLayoutY(139);

	            if (isAr) {
	                ligne1.setLayoutX(95);
	                ligne2.setLayoutX(155);
	                ligne1.setPrefWidth(670);
	                ligne2.setPrefWidth(670);
	                ligne1.setAlignment(Pos.CENTER_RIGHT);
	                ligne2.setAlignment(Pos.CENTER_RIGHT);
	            } else {
	                ligne1.setLayoutX(395);
	                ligne2.setLayoutX(100);
	                ligne1.setPrefWidth(650);
	                ligne2.setPrefWidth(650);
	                ligne1.setAlignment(Pos.CENTER_LEFT);
	                ligne2.setAlignment(Pos.CENTER_LEFT);
	            }

	        } catch (Exception ex) {
	            ligne1.setText("");
	            ligne2.setText("");
	        }
	    }

	    private static Label createDisplayLabel(double x, double y, double w) {
	        Label label = new Label();
	        label.setLayoutX(x);
	        label.setLayoutY(y);
	        label.setPrefWidth(w);
	        label.setFont(Font.font("Arial", 12));
	        label.setStyle("-fx-background-color: transparent; -fx-text-fill: black;");
	        label.setWrapText(true);
	        return label;
	    }
	    private static void addChamp(String name, String valeur, double x, double y, double largeur,
                Map<String, TextField> fields, Map<String, Label> errors,
                Pane overlay, String langue) {

TextField tf = new TextField(valeur);
tf.setFont(Font.font("Arial", 12));
tf.setPromptText(messages.get(langue).get(name));
tf.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: black;");

Label err = new Label();
err.setTextFill(Color.RED);
err.setFont(Font.font(10));

// Appliquer immédiatement l'orientation et la position selon la langue
if (langue.equals("ar") && (name.equals("beneficiaire") || name.equals("ville"))) {
tf.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
tf.setStyle("-fx-text-alignment: right; -fx-prompt-text-fill: derive(gray, 30%);");

if (name.equals("beneficiaire")) {
tf.setLayoutX(160);          // collé à droite
tf.setPrefWidth(630);        // remplissage vers la gauche
} else if (name.equals("ville")) {
tf.setLayoutX(400);
tf.setPrefWidth(180);
} else {
tf.setLayoutX(x);
tf.setPrefWidth(largeur);
}

} else {
tf.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
tf.setStyle("-fx-text-alignment: left; -fx-prompt-text-fill: derive(gray, -30%);");

if (name.equals("beneficiaire")) {
tf.setLayoutX(150);
tf.setPrefWidth(630);
} else if (name.equals("ville")) {
tf.setLayoutX(400);
tf.setPrefWidth(180);
} else {
tf.setLayoutX(x);
tf.setPrefWidth(largeur);
}
}

tf.setLayoutY(y);
err.setLayoutX(tf.getLayoutX());
err.setLayoutY(y + 25);

fields.put(name, tf);
errors.put(name, err);
overlay.getChildren().addAll(tf, err);

// Focus listener pour réappliquer styles au besoin
tf.focusedProperty().addListener((obs, old, now) -> {
if (langue.equals("ar") && (name.equals("beneficiaire") || name.equals("ville"))) {
tf.setStyle("-fx-text-alignment: right; -fx-prompt-text-fill: derive(gray, 30%);");
tf.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

if (name.equals("beneficiaire")) {
   tf.setLayoutX(160);
   tf.setPrefWidth(630);
} else if (name.equals("ville")) {
   tf.setLayoutX(400);
   tf.setPrefWidth(180);
}

} else {
tf.setStyle("-fx-text-alignment: left; -fx-prompt-text-fill: derive(gray, -30%);");
tf.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

if (name.equals("beneficiaire")) {
   tf.setLayoutX(150);
   tf.setPrefWidth(630);
} else if (name.equals("ville")) {
   tf.setLayoutX(400);
   tf.setPrefWidth(180);
}
}

err.setLayoutX(tf.getLayoutX());
});

// Validation
tf.textProperty().addListener((obs, oldVal, newVal) -> {
String val = newVal;

if (name.equals("montant")) {
val = val.replaceAll("[^\\d.]", "");
}

if (name.equals("beneficiaire")) {
val = val.replaceAll("[^\\p{L}\\s']", "");
val = val.toUpperCase(); // majuscules
if (val.length() > 75) val = val.substring(0, 75);
}

if (name.equals("ville") && langue.equals("fr")) {
val = val.replaceAll("\\d|[^\\p{L}\\s]", "");
}

if (name.equals("nomCheque") || name.equals("nomSerie")) {
val = val.toUpperCase();
if (val.length() > 1) val = val.substring(0, 1);
}

if (!tf.getText().equals(val)) {
int caretPos = tf.getCaretPosition();
tf.setText(val);
tf.positionCaret(Math.min(caretPos, val.length()));
}

validateField(name, tf, err, langue);
});

// Mappage clavier arabe
tf.addEventFilter(KeyEvent.KEY_TYPED, e -> {
String character = e.getCharacter();
if (character == null || character.isEmpty()) {
e.consume();
return;
}

if (name.equals("montant") && !character.matches("[0-9\\.]")) {
e.consume();
return;
}

if ((name.equals("nomCheque") || name.equals("nomSerie")) && tf.getText().length() >= 1) {
e.consume();
return;
}
//// Arabe : autoriser lettres mappées + espaces
if (langue.equals("ar") && (name.equals("beneficiaire") || name.equals("ville"))) {
    if (character.equals(" ")) {
        // Autoriser l’espace
        return;
    }

    String mapped = arabicMap.get(character.toLowerCase());
    if (mapped != null) {
        e.consume();
        tf.insertText(tf.getCaretPosition(), mapped);
    } else {
        // Bloquer tout autre caractère
        e.consume();
    }
}
});
}
	       private static boolean validateForm(Map<String, TextField> fields, Map<String, Label> errors, DatePicker date, String lang) {
        boolean valid = true;
        for (String key : fields.keySet()) {
            if (!validateField(key, fields.get(key), errors.get(key), lang)) valid = false;
        }
        if (date.getValue() == null) {
            Label dateErr = new Label(messages.get(lang).get("date"));
            dateErr.setTextFill(Color.RED);
            dateErr.setFont(Font.font(10));
            dateErr.setLayoutX(620);
            dateErr.setLayoutY(215);
            errors.put("date", dateErr);
            valid = false;
        }
        return valid;
    }

    private static boolean validateField(String id, TextField tf, Label errorLabel, String lang) {
        String val = tf.getText().trim();
        boolean valid = switch (id) {
            case "montant" -> val.matches("\\d+(\\.\\d{1,2})?");
            case "beneficiaire", "ville" -> !val.isEmpty();
            case "nomCheque", "nomSerie" -> val.matches("[A-Z]");
            case "numeroSerie" -> val.matches("\\d{1,10}");
            default -> true;
        };
        errorLabel.setText(valid ? "" : messages.get(lang).get(id));
        return valid;
    }
    private static Button bouton(String texte, double x, double y) {
        Button btn = new Button(texte);
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
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