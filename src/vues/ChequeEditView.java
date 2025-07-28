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
import javafx.stage.Stage;
import services.MontantEnLettresService;

import java.util.HashMap;
import java.util.Map;

public class ChequeEditView {

    private static TextField ligne1;
    private static Label ligne2;

    private static final Map<String, String> arabicMap = Map.ofEntries(
            Map.entry("a", "ش"), Map.entry("b", "لا"), Map.entry("c", "ؤ"), Map.entry("d", "ي"),
            Map.entry("e", "ث"), Map.entry("f", "ب"), Map.entry("g", "ل"), Map.entry("h", "ا"),
            Map.entry("i", "ه"), Map.entry("j", "ت"), Map.entry("k", "ن"), Map.entry("l", "م"),
            Map.entry("m", "ة"), Map.entry("n", "ى"), Map.entry("o", "خ"), Map.entry("p", "ح"),
            Map.entry("q", "ض"), Map.entry("r", "ق"), Map.entry("s", "س"), Map.entry("t", "ف"),
            Map.entry("u", "ع"), Map.entry("v", "ر"), Map.entry("w", "ص"), Map.entry("x", "ء"),
            Map.entry("y", "غ"), Map.entry("z", "ئ")
    );

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

    public static void afficher(Cheque cheque, ChequeController controller, Runnable onSaveCallback) {
        Stage stage = new Stage();
        stage.setTitle("Modifier le chèque");

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
        addChamp("montant", String.valueOf(cheque.getMontant()), 630, 45, 160, fields, errors, overlay, langue);
        ligne1 = createDisplayLine(385, 105, 600);
        ligne1.setFont(new Font(16));
        ligne2 = createDisplayLabel(100, 135, 600);
        ligne2.setFont(new Font(16));

        overlay.getChildren().addAll(ligne1, ligne2);

        // Appliquer orientation RTL si langue arabe
        updateDirectionForArabic(langue);

        addChamp("beneficiaire", cheque.getBeneficiaire(), 150, 160, 600, fields, errors, overlay, langue);
        addChamp("ville", cheque.getVille(), 400, 190, 180, fields, errors, overlay, langue);
        addChamp("nomCheque", cheque.getNomCheque(), 123, 293, 30, fields, errors, overlay, langue);
        addChamp("nomSerie", cheque.getNomSerie(), 185, 293, 30, fields, errors, overlay, langue);
        addChamp("numeroSerie", String.valueOf(cheque.getNumeroSerie()), 240, 293, 100, fields, errors, overlay, langue);

        DatePicker dateField = new DatePicker(cheque.getDate());
        dateField.setLayoutX(620);
        dateField.setLayoutY(190);
        dateField.setPrefWidth(150);
        overlay.getChildren().add(dateField);

        // Conversion initiale
        updateMontantEnLettres(fields.get("montant").getText(), langue);

        fields.get("montant").setOnKeyReleased(e -> updateMontantEnLettres(fields.get("montant").getText(), langue));

        // Boutons
        Button retour = bouton("Retour", 100, 400);
        retour.setOnAction(e -> {
            stage.close();
            ChequeVisualisationView.afficher(cheque, controller);
        });

        Button enregistrer = bouton("Enregistrer", 370, 400);
        enregistrer.setOnAction(e -> {
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

                if (controller.modifierCheque(cheque.getId(), maj)) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Chèque modifié.");
                    stage.close();
                    if (onSaveCallback != null) onSaveCallback.run();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Modification échouée.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Exception : " + ex.getMessage());
            }
        });

        Button imprimer = bouton("Imprimer", 620, 400);
        imprimer.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(stage)) {
                if (job.printPage(root)) job.endJob();
            }
        });

        overlay.getChildren().addAll(retour, enregistrer, imprimer);
        chequePane.getChildren().addAll(chequeView, overlay);
        layout.getChildren().addAll(title, chequePane);
        root.setCenter(layout);

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private static void updateDirectionForArabic(String langue) {
        ligne1.setFont(new Font(16));
        ligne2.setFont(new Font(16));

        if ("ar".equals(langue)) {
            // Pour l'arabe (droite à gauche)
            ligne1.setLayoutX(95);
            ligne1.setPrefWidth(670);
            ligne1.setAlignment(Pos.CENTER_RIGHT);

            ligne2.setLayoutX(155);
            ligne2.setPrefWidth(670);
            ligne2.setAlignment(Pos.CENTER_RIGHT);
        } else {
            // Pour les autres langues (gauche à droite)
            ligne1.setLayoutX(380);
            ligne1.setPrefWidth(650);
            ligne1.setAlignment(Pos.CENTER_LEFT);

            ligne2.setLayoutX(100);
            ligne2.setPrefWidth(650);
            ligne2.setAlignment(Pos.CENTER_LEFT);
        }
    }

    private static void updateMontantEnLettres(String montantStr, String langue) {
        try {
            double montant = Double.parseDouble(montantStr);
            String lettres = MontantEnLettresService.convertirMontant(montant, langue);

            javafx.scene.text.Text textMeasurer = new javafx.scene.text.Text();
            textMeasurer.setFont(Font.font("Arial", 16));

            double maxWidth;
            if ("ar".equals(langue)) {
                maxWidth = 300; // Plus large pour l’arabe
            } else {
                maxWidth = 370;
            }

            String[] words = lettres.split("\\s+");
            StringBuilder ligne1Text = new StringBuilder();
            StringBuilder ligne2Text = new StringBuilder();

            for (String word : words) {
                String tentative;
                if (ligne1Text.length() > 0) {
                    tentative = ligne1Text + " " + word;
                } else {
                    tentative = word;
                }

                textMeasurer.setText(tentative);
                double width = textMeasurer.getLayoutBounds().getWidth();

                if (width <= maxWidth) {
                    if (ligne1Text.length() > 0) {
                        ligne1Text.append(" ");
                    }
                    ligne1Text.append(word);
                } else {
                    if (ligne2Text.length() > 0) {
                        ligne2Text.append(" ");
                    }
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
    private static void addChamp(String name, String valeur, double x, double y, double largeur,
                                 Map<String, TextField> fields, Map<String, Label> errors, Pane overlay, String langue) {
        TextField tf = new TextField(valeur);
        tf.setLayoutX(x);
        tf.setLayoutY(y);
        tf.setPrefWidth(largeur);
        tf.setFont(Font.font("Arial", 12));
        tf.setPromptText(messages.get(langue).get(name));
        tf.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: black;");

        Label err = new Label();
        err.setTextFill(Color.RED);
        err.setFont(Font.font(10));
        err.setLayoutX(x);
        err.setLayoutY(y + 25);

        fields.put(name, tf);
        errors.put(name, err);
        overlay.getChildren().addAll(tf, err);

        tf.textProperty().addListener((obs, old, val) -> {
            if (name.equals("montant")) val = val.replaceAll("[^\\d.]", "");
            if (name.equals("beneficiaire")) {
                val = val.replaceAll("[^\\p{L}\\s']", "").toUpperCase();
                if (val.length() > 75) val = val.substring(0, 75);
            }
            if (name.equals("ville") && langue.equals("fr")) val = val.replaceAll("\\d|[^\\p{L}\\s]", "");
            if (name.equals("nomCheque") || name.equals("nomSerie")) {
                val = val.toUpperCase();
                if (val.length() > 1) val = val.substring(0, 1);
            }
            tf.setText(val);
            tf.positionCaret(val.length());
            validateField(name, tf, err, langue);
        });

        tf.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (name.equals("montant") && !e.getCharacter().matches("[0-9\\.]")) e.consume();
            if ((name.equals("nomCheque") || name.equals("nomSerie")) && tf.getText().length() >= 1) e.consume();
            if (langue.equals("ar") && (name.equals("beneficiaire") || name.equals("ville"))) {
                String mapped = arabicMap.get(e.getCharacter().toLowerCase());
                if (mapped != null) {
                    e.consume();
                    tf.insertText(tf.getCaretPosition(), mapped);
                }
            }
        });

        tf.focusedProperty().addListener((obs, old, now) -> {
            if (langue.equals("ar") && (name.equals("beneficiaire") || name.equals("ville"))) {
                tf.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                tf.setStyle("-fx-text-alignment: right;");
            } else {
                tf.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                tf.setStyle("-fx-text-alignment: left;");
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

    private static TextField createDisplayLine(double x, double y, double w) {
        TextField tf = new TextField();
        tf.setLayoutX(x);
        tf.setLayoutY(y);
        tf.setPrefWidth(w);
        tf.setEditable(false);
        tf.setFont(Font.font("Arial", 12));
        tf.setStyle("-fx-background-color: transparent; -fx-text-fill: black;");
        return tf;
    }

    private static Label createDisplayLabel(double x, double y, double w) {
        Label label = new Label();
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setPrefWidth(w);
        label.setFont(Font.font("Arial", 12));
        return label;
    }

    private static Button bouton(String texte, double x, double y) {
        Button btn = new Button(texte);
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;"
                + "-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
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