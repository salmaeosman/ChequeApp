package vues;

import entities.Cheque;
import entities.Scan;
import repositories.ChequeRepository;
import repositories.ScanRepository;
import services.ScanService;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;

import controllers.ChequeController;
import controllers.ScanController;
import db.H2Database;

public class ScanView extends Application {

    H2Database db = new H2Database();
    ChequeRepository chequeRepo = new ChequeRepository(db);
    ScanRepository scanRepo = new ScanRepository(db);
    ScanService scanService = new ScanService(scanRepo, chequeRepo);
    ScanController scanController = new ScanController(scanService);
    ChequeController chequeController = new ChequeController(chequeRepo);

    private Label messageLabel = new Label();
    private ImageView imageView = new ImageView();
    private TextField profileField = new TextField("HP300");

    private Cheque chequeFromVisualisation = null;
    private Long chequeId = 1L; // Utilisé si aucun chèque n'est passé

    // ➕ Constructeurs
    public ScanView() {}

    public ScanView(Cheque cheque) {
        this.chequeFromVisualisation = cheque;
    }

    @Override
    public void start(Stage primaryStage) {
        Cheque cheque = chequeFromVisualisation != null
                ? chequeFromVisualisation
                : chequeRepo.findById(chequeId)
                    .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f2f3f4;");

        Label title = new Label("Scanner un Chèque");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setTextFill(Color.web("#e78212"));

        Label chequeLabel = new Label("Chèque : " +
                cheque.getNomCheque() + " " +
                cheque.getNomSerie() + " " +
                cheque.getNumeroSerie());
        chequeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
        chequeLabel.setTextFill(Color.web("#343a40"));

        VBox profileSection = new VBox(5);
        Label profileLabel = new Label("Profil de numérisation NAPS2");
        profileLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        profileField.setPromptText("Ex. HP300");
        profileField.setMaxWidth(250);
        profileField.setStyle("-fx-background-color: white; -fx-border-color: #ced4da; -fx-border-radius: 5; -fx-padding: 5;");
        profileSection.getChildren().addAll(profileLabel, profileField);

        Button scanButton = new Button("Lancer le scan");
        scanButton.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
        scanButton.setPrefWidth(180);
        scanButton.setPrefHeight(40);
        scanButton.setOnAction(e -> lancerScan(cheque));

        Button retourButton = new Button("Retour à la visualisation");
        retourButton.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
        retourButton.setPrefWidth(220);
        retourButton.setPrefHeight(40);
        retourButton.setDisable(chequeFromVisualisation == null); // désactive si on ne vient pas de visualisation
        retourButton.setOnAction(e -> {
            primaryStage.close();
            if (chequeFromVisualisation != null) {
                ChequeVisualisationView.afficher(cheque, chequeController);
            }
        });

        HBox buttonBox = new HBox(20, scanButton, retourButton);
        buttonBox.setAlignment(Pos.CENTER);

        messageLabel.setWrapText(true);
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.setTextFill(Color.web("#495057"));

        imageView.setPreserveRatio(true);
        imageView.setFitHeight(320);
        imageView.setFitWidth(600);
        imageView.setSmooth(true);
        imageView.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.15), 8, 0.0, 0, 4);");

        VBox.setMargin(imageView, new Insets(20, 0, 0, 0));

        root.getChildren().addAll(
                title,
                chequeLabel,
                profileSection,
                buttonBox,
                messageLabel,
                imageView
        );

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Scanner un Chèque");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void lancerScan(Cheque cheque) {
        messageLabel.setText("⏳ Scan en cours...");
        imageView.setImage(null);

        new Thread(() -> {
            try {
                String profile = profileField.getText();
                Scan scan = scanService.launchRealScan(cheque.getId(), profile);

                Platform.runLater(() -> {
                    messageLabel.setText("✅ Scan réussi : " + scan.getFileName());
                    byte[] imageBytes = scan.getImage();
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    imageView.setImage(image);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    messageLabel.setText("❌ " + ex.getMessage());
                });
            }
        }).start();
    }

    // 🔁 Méthode pour lancer directement la vue avec un chèque
    public static void scanCheque(Cheque cheque) {
        Platform.runLater(() -> {
            try {
                ScanView scanView = new ScanView(cheque);
                Stage scanStage = new Stage();
                scanView.start(scanStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
