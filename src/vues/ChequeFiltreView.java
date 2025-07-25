package vues;

import controllers.FiltreController;
import controllers.ChequeController;
import db.H2Database;
import entities.Cheque;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    @Override
    public void start(Stage stage) {
        stage.setTitle("Recherche dans la base des chèques");

        // Initialisation avec H2
        H2Database db = new H2Database();
        ChequeRepository repo = new ChequeRepository(db);
        filtreController = new FiltreController(repo);

        // Formulaire
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));

        form.add(new Label("Chèque (nom + série + N°)"), 0, 0);
        form.add(chequeField, 1, 0);

        form.add(new Label("Date"), 0, 1);
        form.add(datePicker, 1, 1);

        form.add(new Label("Bénéficiaire"), 0, 2);
        form.add(beneficiaireField, 1, 2);

        form.add(new Label("Montant"), 0, 3);
        form.add(montantField, 1, 3);

        Button rechercherBtn = new Button("Rechercher");
        Button retourBtn = new Button("Retour");
        HBox buttons = new HBox(10, rechercherBtn, retourBtn);
        form.add(buttons, 1, 4);

        // Table
        TableColumn<Cheque, String> chequeCol = new TableColumn<>("Chèque");
        chequeCol.setCellValueFactory(data -> new SimpleStringProperty(
                (data.getValue().getNomCheque() != null ? data.getValue().getNomCheque() : "") +
                (data.getValue().getNomSerie() != null ? data.getValue().getNomSerie() : "") +
                (data.getValue().getNumeroSerie() != null ? data.getValue().getNumeroSerie().toString() : "")
        ));

        TableColumn<Cheque, String> beneficiaireCol = new TableColumn<>("Bénéficiaire");
        beneficiaireCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBeneficiaire())
        );

        TableColumn<Cheque, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getDate())
        );

        TableColumn<Cheque, String> montantCol = new TableColumn<>("Montant");
        montantCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f", data.getValue().getMontant()))
        );

        tableView.getColumns().addAll(chequeCol, beneficiaireCol, dateCol, montantCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox tableBox = new VBox(10, new Label("Résultats trouvés :"), tableView);
        tableBox.setPadding(new Insets(15));

        // Double clic pour ouvrir la visualisation avec controller
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
        retourBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(20, form, tableBox);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
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

    public static void main(String[] args) {
        launch();
    }
}
