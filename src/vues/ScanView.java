package vues;
import entities.Cheque;
import entities.Scan;
import repositories.ChequeRepository;
import repositories.ScanRepository;
import services.ScanService;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import controllers.ScanController;
import db.H2Database;

public class ScanView extends Application {
	 H2Database db = new H2Database();
     ChequeRepository chequeRepo = new ChequeRepository(db);
     ScanRepository scanRepo = new ScanRepository(db);
     ScanService scanService = new ScanService(scanRepo, chequeRepo);
     ScanController scanController = new ScanController(scanService);

    private Label messageLabel = new Label();
    private ImageView imageView = new ImageView();
    private TextField profileField = new TextField("HP300");

    private Long chequeId = 1L; // À adapter selon le chèque sélectionné

    @Override
    public void start(Stage primaryStage) {
        Cheque cheque = chequeRepo.findById(chequeId)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        // Layout principal
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Scanner / Importer un chèque");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label chequeLabel = new Label("Chèque : " +
                cheque.getNomCheque() + " " +
                cheque.getNomSerie() + " " +
                cheque.getNumeroSerie());

        profileField.setPromptText("Ex. HP300");
        profileField.setMaxWidth(300);

        Button scanButton = new Button("📠 Lancer le scan");
        scanButton.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white;");
        scanButton.setOnAction(e -> lancerScan(cheque));

        messageLabel.setWrapText(true);

        imageView.setPreserveRatio(true);
        imageView.setFitHeight(300);
        imageView.setFitWidth(600);

        VBox.setMargin(imageView, new Insets(10, 0, 0, 0));

        root.getChildren().addAll(title, chequeLabel,
                new Label("Profil de numérisation NAPS2 :"), profileField,
                scanButton, messageLabel, imageView);

        primaryStage.setTitle("Scanner un chèque");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    private void lancerScan(Cheque cheque) {
        messageLabel.setText("⏳ Scan en cours...");
        imageView.setImage(null);

        new Thread(() -> {
            try {
                String profile = profileField.getText();
                Scan scan = scanService.launchRealScan(cheque.getId(), profile);

                String base64 = Base64.getEncoder().encodeToString(scan.getImage());

                javafx.application.Platform.runLater(() -> {
                    messageLabel.setText("✅ Scan réussi : " + scan.getFileName());

                    byte[] imageBytes = scan.getImage();
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    imageView.setImage(image);
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    messageLabel.setText("❌ Erreur de scan : " + ex.getMessage());
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}