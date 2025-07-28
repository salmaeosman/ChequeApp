package vues;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class HomePage extends Application {

    private BorderPane root;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.root = new BorderPane();
        root.setStyle("-fx-background-color: #f2f3f4;");

        buildInterface();

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Banque Populaire");
        stage.setResizable(false);
        stage.show();
    }

    private void buildInterface() {
        root.setTop(null);
        root.setCenter(null);

        // ---------- BARRE SUPÉRIEURE ----------
        HBox topBar = new HBox(40);
        topBar.setPadding(new Insets(20, 40, 10, 40));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label menuCheque = new Label("Chèques");
        Label menuAjouter = new Label("Ajouter");
        Label menuFiltre = new Label("Filtre");

        for (Label item : new Label[]{menuCheque, menuAjouter, menuFiltre}) {
            item.setFont(Font.font("Arial", 18));
            item.setTextFill(Color.web("#541B2D"));
            item.setCursor(Cursor.HAND);
        }

        menuAjouter.setOnMouseClicked(e -> {
            try {
                new ChequeFormApp().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        menuFiltre.setOnMouseClicked(e -> {
            try {
                new ChequeFiltreView().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Logo banque
        Image logoImg = new Image(getClass().getResourceAsStream("/images/bp_logo.png"));
        ImageView logoView = new ImageView(logoImg);
        logoView.setFitHeight(40);
        logoView.setPreserveRatio(true);

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        Button refreshBtn = new Button("Rafraîchir");
        refreshBtn.setCursor(Cursor.HAND);
        refreshBtn.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
        refreshBtn.setOnAction(e -> buildInterface());

        topBar.getChildren().addAll(menuCheque, menuAjouter, menuFiltre, spacerLeft, logoView, spacerRight, refreshBtn);
        root.setTop(topBar);

        // ---------- PARTIE GAUCHE ----------
        VBox leftPane = new VBox(20);
        leftPane.setPadding(new Insets(120, 40, 20, 60));
        leftPane.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Gestion de Chèques");
        title.setFont(Font.font("Arial", 42));
        title.setTextFill(Color.web("#e78212"));

        Label subtitle = new Label("Contrôlez et organisez vos transactions\nfinancières sans effort.");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setTextFill(Color.web("#000"));

        VBox interfaceBox = new VBox(5);
        interfaceBox.setPadding(new Insets(15));
        interfaceBox.setStyle("-fx-background-color: white; -fx-border-color: #854e56; -fx-border-width: 3px; -fx-background-radius: 12; -fx-border-radius: 12;");
        Label featureTitle = new Label("Interface Admin Intuitive");
        featureTitle.setFont(Font.font("Arial", 16));
        featureTitle.setTextFill(Color.web("#854e56"));
        Label featureDesc = new Label("La gestion de vos chèques simplifiée.");
        featureDesc.setFont(Font.font("Arial", 14));
        interfaceBox.setAlignment(Pos.CENTER_LEFT);
        interfaceBox.getChildren().addAll(featureTitle, featureDesc);

        Button startButton = new Button("Commencer →");
        startButton.setCursor(Cursor.HAND);
        startButton.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 20;");
        startButton.setPrefSize(180, 45);
        startButton.setEffect(new DropShadow());

        startButton.setOnAction(e -> {
            try {
                new ChequeFormApp().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        leftPane.getChildren().addAll(title, subtitle, interfaceBox, startButton);

        // ---------- ESPACE VIDE À DROITE ----------
        Region rightSpacer = new Region();
        rightSpacer.setPrefWidth(300);

        HBox content = new HBox(leftPane, rightSpacer);
        content.setSpacing(40);
        content.setAlignment(Pos.CENTER_LEFT);

        root.setCenter(content);
    }

    public static void main(String[] args) {
        launch(args);
    }
}