package vues;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;

public class ScanDisplayView {

    public static void afficher(String imagePath) {
        Stage stage = new Stage();
        stage.setTitle("Aperçu du scan");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f2f5;");

        VBox topSection = new VBox();
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(20, 0, 10, 0));

        Text title = new Text("Aperçu de l'image scannée");
        title.setFont(Font.font("Arial", 26));
        topSection.getChildren().add(title);

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists() || !Files.isReadable(imageFile.toPath())) {
                title.setText("Image non disponible");
            } else {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);

                // Adapter à la largeur de l'écran pour l'aperçu (non imprimé)
                imageView.setFitWidth(800);
            }
        } catch (Exception e) {
            title.setText("Erreur lors du chargement de l'image");
        }

        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-padding: 10;");

        HBox buttonBar = new HBox(20);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(20));

        Button imprimerBtn = new Button("Imprimer");
        imprimerBtn.setFont(Font.font("Arial", 14));
        imprimerBtn.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
        imprimerBtn.setOnMouseEntered(e -> imprimerBtn.setStyle("-fx-background-color: #157347; -fx-text-fill: white; -fx-background-radius: 5;"));
        imprimerBtn.setOnMouseExited(e -> imprimerBtn.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-background-radius: 5;"));

        imprimerBtn.setOnAction(ev -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(stage)) {
                PageLayout pageLayout = job.getJobSettings().getPageLayout();

                // Zone imprimable (A4 portrait par défaut : ~595x842 points)
                double maxWidth = pageLayout.getPrintableWidth();
                double maxHeight = pageLayout.getPrintableHeight();

                // Redimensionner temporairement l'imageView pour l'impression
                Image image = imageView.getImage();
                double imgWidth = image.getWidth();
                double imgHeight = image.getHeight();
                double scale = Math.min(maxWidth / imgWidth, maxHeight / imgHeight);

                ImageView imageToPrint = new ImageView(image);
                imageToPrint.setPreserveRatio(true);
                imageToPrint.setFitWidth(imgWidth * scale);
                imageToPrint.setSmooth(true);

                StackPane printablePane = new StackPane(imageToPrint);
                printablePane.setPrefSize(maxWidth, maxHeight);
                printablePane.setPadding(new Insets(20));
                printablePane.setStyle("-fx-background-color: white;");

                boolean success = job.printPage(printablePane);
                if (success) {
                    job.endJob();
                }
            }
        });

        Button retourBtn = new Button("Retour au filtre");
        retourBtn.setFont(Font.font("Arial", 14));
        retourBtn.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;");
        retourBtn.setOnMouseEntered(e -> retourBtn.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;"));
        retourBtn.setOnMouseExited(e -> retourBtn.setStyle("-fx-background-color: #854e56; -fx-text-fill: white; -fx-font-size: 16px;"));

        retourBtn.setOnAction(e -> {
            stage.close();
            try {
                new ChequeFiltreView().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        buttonBar.getChildren().addAll(imprimerBtn, retourBtn);

        root.setTop(topSection);
        root.setCenter(scrollPane);
        root.setBottom(buttonBar);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
}
