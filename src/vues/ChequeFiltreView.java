package vues;

import controllers.FiltreController;
import db.H2Database;
import entities.Cheque;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import repositories.ChequeRepository;

import java.time.LocalDate;
import java.util.List;

public class ChequeFiltreView extends Application {

    private final TextField chequeField = new TextField();
    private final TextField beneficiaireField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final TextField montantField = new TextField();
    private final TableView<Cheque> tableView = new TableView<>();
    private FiltreController filtreController;
    private VBox root;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Recherche des Chèques");

        H2Database db = new H2Database();
        ChequeRepository repo = new ChequeRepository(db);
        filtreController = new FiltreController(repo);

        root = new VBox(20);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #ffffff;");

        buildUI();

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void buildUI() {
        root.getChildren().clear();

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-border-color: #854e56; -fx-border-width: 3px; -fx-background-radius: 12; -fx-border-radius: 12;");

        Label title = new Label("🔎 Rechercher un chèque");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #e78212;");
        GridPane.setColumnSpan(title, 2);
        form.add(title, 0, 0);

        form.add(new Label("Chèque (nom + série + N°)"), 0, 1);
        form.add(chequeField, 1, 1);

        form.add(new Label("Date"), 0, 2);
        form.add(datePicker, 1, 2);

        form.add(new Label("Bénéficiaire"), 0, 3);
        form.add(beneficiaireField, 1, 3);

        form.add(new Label("Montant"), 0, 4);
        form.add(montantField, 1, 4);

        Button rechercherBtn = createStyledButton("Rechercher");
        Button resetBtn = createStyledButton("Vider les champs");
        Button retourBtn = createStyledButton("Retour");

        HBox buttons = new HBox(10, rechercherBtn, resetBtn, retourBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        form.add(buttons, 1, 5);

        TableColumn<Cheque, String> chequeCol = new TableColumn<>("Chèque");
        chequeCol.setCellValueFactory(data -> new SimpleStringProperty(
                (data.getValue().getNomCheque() != null ? data.getValue().getNomCheque() + " " : "") +
                (data.getValue().getNomSerie() != null ? data.getValue().getNomSerie() + " " : "") +
                (data.getValue().getNumeroSerie() != null ? data.getValue().getNumeroSerie().toString() : "")
        ));

        TableColumn<Cheque, String> beneficiaireCol = new TableColumn<>("Bénéficiaire");
        beneficiaireCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBeneficiaire()));

        TableColumn<Cheque, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDate()));

        TableColumn<Cheque, String> montantCol = new TableColumn<>("Montant");
        montantCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getMontant())));

        tableView.getColumns().setAll(chequeCol, beneficiaireCol, dateCol, montantCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(300);
        tableView.setStyle("-fx-control-inner-background: #fff7f0; -fx-table-cell-border-color: #e78212; -fx-border-color: #e78212;");

        VBox tableBox = new VBox(10, new Label("📋 Résultats trouvés :"), tableView);
        tableBox.setPadding(new Insets(10));

        tableView.setRowFactory(tv -> {
            TableRow<Cheque> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Cheque selectedCheque = row.getItem();
                    ChequeVisualisationView.afficher(selectedCheque, filtreController.getChequeController());
                }
            });
            return row;
        });

        rechercherBtn.setOnAction(e -> rechercher());
        resetBtn.setOnAction(e -> refreshTable());

        // ✅ Nouveau comportement du bouton retour : ouvre la HomePage
        retourBtn.setOnAction(e -> {
            primaryStage.close();
            try {
                new HomePage().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(form, tableBox);
        chargerTousLesCheques();
    }

    private void rechercher() {
        String cheque = chequeField.getText().trim();
        String beneficiaire = beneficiaireField.getText().trim();
        LocalDate date = datePicker.getValue();

        Double montant = null;
        try {
            if (!montantField.getText().isBlank()) {
                montant = Double.parseDouble(montantField.getText().trim());
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Montant invalide").show();
            return;
        }

        List<Cheque> resultats = filtreController.filtrerCheques(
                cheque.isEmpty() ? null : cheque,
                beneficiaire.isEmpty() ? null : beneficiaire,
                date,
                montant
        );
        tableView.getItems().setAll(resultats);
    }

    private void refreshTable() {
        chequeField.clear();
        beneficiaireField.clear();
        datePicker.setValue(null);
        montantField.clear();
        chargerTousLesCheques();
    }

    private void chargerTousLesCheques() {
        List<Cheque> tousLesCheques = filtreController.getChequeController().getAllCheques();
        tableView.getItems().setAll(tousLesCheques);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> button.setCursor(Cursor.HAND));
        return button;
    }

    public static void main(String[] args) {
        launch();
    }
}
