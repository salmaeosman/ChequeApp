package vues;

import controllers.ChequeController;
import db.H2Database;
import entities.Cheque;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import repositories.ChequeRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ChequeFormApp extends Application {

    private String selectedLang = "fr";
    private final Map<String, TextField> fields = new HashMap<>();
    private final Map<String, Label> errorLabels = new HashMap<>();
    private ChequeController chequeController;

    private final Map<String, String> arabicMap = Map.ofEntries(
            Map.entry("a", "ش"), Map.entry("b", "لا"), Map.entry("c", "ؤ"), Map.entry("d", "ي"),
            Map.entry("e", "ث"), Map.entry("f", "ب"), Map.entry("g", "ل"), Map.entry("h", "ا"),
            Map.entry("i", "ه"), Map.entry("j", "ت"), Map.entry("k", "ن"), Map.entry("l", "م"),
            Map.entry("m", "ة"), Map.entry("n", "ى"), Map.entry("o", "خ"), Map.entry("p", "ح"),
            Map.entry("q", "ض"), Map.entry("r", "ق"), Map.entry("s", "س"), Map.entry("t", "ف"),
            Map.entry("u", "ع"), Map.entry("v", "ر"), Map.entry("w", "ص"), Map.entry("x", "ء"),
            Map.entry("y", "غ"), Map.entry("z", "ئ")
    );

    private final Map<String, Map<String, String>> messages = Map.of(
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

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Formulaire de chèque");
        title.setFont(new Font(20));

        HBox langSelection = new HBox(10);
        ToggleGroup langGroup = new ToggleGroup();
        RadioButton frBtn = new RadioButton("Français");
        RadioButton arBtn = new RadioButton("Arabe");
        frBtn.setToggleGroup(langGroup);
        arBtn.setToggleGroup(langGroup);
        langSelection.getChildren().addAll(new Label("Langue :"), frBtn, arBtn);
        langSelection.setAlignment(Pos.CENTER);

        StackPane chequePane = new StackPane();
        Image chequeImage = new Image(getClass().getResourceAsStream("/images/cheque_bg.png"));
        ImageView chequeView = new ImageView(chequeImage);
        chequeView.setPreserveRatio(true);
        chequeView.setFitWidth(800);

        Pane formOverlay = new Pane();
        formOverlay.setPrefSize(800, 400);

        addField("montant", 630, 20, 100, formOverlay);
        addField("beneficiaire", 100, 120, 600, formOverlay);
        addField("ville", 360, 150, 180, formOverlay);
        addField("nomCheque", 70, 230, 30, formOverlay);
        addField("nomSerie", 130, 230, 30, formOverlay);
        addField("numeroSerie", 190, 230, 100, formOverlay);

        DatePicker datePicker = new DatePicker();
        datePicker.setLayoutX(580);
        datePicker.setLayoutY(150);
        datePicker.setDisable(true);
        formOverlay.getChildren().add(datePicker);

        Label dateError = new Label();
        dateError.setTextFill(Color.RED);
        dateError.setFont(Font.font(10));
        dateError.setLayoutX(580);
        dateError.setLayoutY(180);
        errorLabels.put("date", dateError);
        formOverlay.getChildren().add(dateError);

        chequePane.getChildren().addAll(chequeView, formOverlay);

        Button submitBtn = new Button("Enregistrer le chèque");
        submitBtn.setDisable(true);
        submitBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 16px;");
        submitBtn.setOnAction(e -> {
            if (validateForm(datePicker)) {
                try {
                    String beneficiaire = fields.get("beneficiaire").getText().trim();
                    double montant = Double.parseDouble(fields.get("montant").getText().trim());
                    String ville = fields.get("ville").getText().trim();
                    String nomCheque = fields.get("nomCheque").getText().trim();
                    String nomSerie = fields.get("nomSerie").getText().trim();
                    Long numeroSerie = Long.parseLong(fields.get("numeroSerie").getText().trim());
                    String dateStr = datePicker.getValue().toString();

                    String result = chequeController.enregistrerCheque(
                            beneficiaire, montant, ville, selectedLang,
                            nomCheque, nomSerie, numeroSerie, dateStr
                    );

                    switch (result) {
                        case "success" -> {
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Chèque enregistré !");

                            // Créer l’objet cheque pour l’impression
                            Cheque cheque = new Cheque(null, nomCheque, nomSerie, montant,
                                    datePicker.getValue(), ville, numeroSerie, beneficiaire, selectedLang);

                            // Obtenir le montant en lettres
                            String montantLettre = chequeController.getMontantLettre(montant, selectedLang);

                            // Affichage de l’interface d’impression avec le montant en lettres
                            ChequePrintView.showChequePrint(cheque, montantLettre);
                        }
                        case "existedeja" -> showAlert(Alert.AlertType.WARNING, "Doublon", "Ce chèque existe déjà.");
                        case "beneficiaire" -> showAlert(Alert.AlertType.WARNING, "Erreur", "Le bénéficiaire est vide.");
                        default -> showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue.");
                    }
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur : " + ex.getMessage());
                }
            }
        });

        langGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null) {
                selectedLang = ((RadioButton) val).getText().equals("Arabe") ? "ar" : "fr";
                enableFields(datePicker);
                submitBtn.setDisable(false);
            }
        });

        mainLayout.getChildren().addAll(title, langSelection, chequePane, submitBtn);
        root.setCenter(mainLayout);

        // Initialisation DB et contrôleur
        H2Database h2 = new H2Database();
        ChequeRepository chequeRepo = new ChequeRepository(h2);
        chequeController = new ChequeController(chequeRepo);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Formulaire de chèque - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addField(String name, double x, double y, double width, Pane parent) {
        TextField tf = new TextField();
        tf.setLayoutX(x);
        tf.setLayoutY(y);
        tf.setPrefWidth(width);
        tf.setDisable(true);

        Label error = new Label();
        error.setTextFill(Color.RED);
        error.setFont(Font.font(10));
        error.setLayoutX(x);
        error.setLayoutY(y + 25);

        fields.put(name, tf);
        errorLabels.put(name, error);
        parent.getChildren().addAll(tf, error);

        tf.textProperty().addListener((obs, old, val) -> {
            if (selectedLang.equals("fr")) {
                if (name.equals("beneficiaire")) {
                    val = val.toUpperCase();
                    if (val.length() > 50) val = val.substring(0, 50);
                    tf.setText(val);
                    tf.positionCaret(val.length());
                } else if (name.equals("nomCheque") || name.equals("nomSerie")) {
                    val = val.toUpperCase();
                    if (val.length() > 1) val = val.substring(0, 1);
                    tf.setText(val);
                    tf.positionCaret(val.length());
                }
            }
            validateField(name);
        });

        tf.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            String current = tf.getText();
            if (selectedLang.equals("fr")) {
                if (name.equals("beneficiaire") && current.length() >= 50) e.consume();
                if ((name.equals("nomCheque") || name.equals("nomSerie")) && current.length() >= 1) e.consume();
            }

            if (selectedLang.equals("ar") && (name.equals("beneficiaire") || name.equals("ville"))) {
                String mapped = arabicMap.get(e.getCharacter().toLowerCase());
                if (mapped != null) {
                    e.consume();
                    tf.insertText(tf.getCaretPosition(), mapped);
                }
            }
        });
    }

    private void enableFields(DatePicker datePicker) {
        fields.values().forEach(f -> {
            f.setDisable(false);
            f.setPromptText(getPlaceholder(f));
            f.setFont(Font.font("Arial", 12));
        });

        fields.get("ville").setText(selectedLang.equals("ar") ? "وجدة" : "Oujda");
        datePicker.setValue(LocalDate.now());
        datePicker.setDisable(false);
    }

    private String getPlaceholder(TextField field) {
        String id = fields.entrySet().stream()
                .filter(e -> e.getValue() == field)
                .map(Map.Entry::getKey)
                .findFirst().orElse("");
        return messages.get(selectedLang).getOrDefault(id, "");
    }

    private boolean validateField(String id) {
        TextField tf = fields.get(id);
        String val = tf.getText().trim();
        boolean valid = switch (id) {
            case "montant" -> val.matches("\\d+(\\.\\d{1,2})?");
            case "beneficiaire", "ville" -> !val.isEmpty();
            case "nomCheque", "nomSerie" -> val.matches("[A-Z]");
            case "numeroSerie" -> val.matches("\\d{1,10}");
            default -> true;
        };
        errorLabels.get(id).setText(valid ? "" : messages.get(selectedLang).get(id));
        return valid;
    }

    private boolean validateForm(DatePicker datePicker) {
        boolean valid = fields.keySet().stream().allMatch(this::validateField);
        if (datePicker.getValue() == null) {
            errorLabels.get("date").setText(messages.get(selectedLang).get("date"));
            valid = false;
        } else {
            errorLabels.get("date").setText("");
        }
        return valid;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
