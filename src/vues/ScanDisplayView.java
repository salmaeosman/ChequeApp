package vues;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
        stage.setTitle("Affichage du Scan");

        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: #f8f9fa;");

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 0);");

        Text title = new Text("Image du Scan");
        title.setFont(Font.font("Arial", 24));

        ImageView imageView = new ImageView();
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists() || !Files.isReadable(imageFile.toPath())) {
                title.setText("Image non disponible");
            } else {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(600);
                imageView.setSmooth(true);
                imageView.setStyle("-fx-border-color: #ccc;");
            }
        } catch (Exception e) {
            title.setText("Image non disponible");
        }

        Button retourBtn = new Button("Retour au filtre");
        retourBtn.setFont(Font.font("Arial", 14));
        retourBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5;");
        retourBtn.setOnMouseEntered(ev -> retourBtn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white; -fx-background-radius: 5;"));
        retourBtn.setOnMouseExited(ev -> retourBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5;"));

        retourBtn.setOnAction(e -> {
            stage.close();
            try {
                new ChequeFiltreView().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        content.getChildren().addAll(title, imageView, retourBtn);
        container.getChildren().add(content);

        Scene scene = new Scene(container, 750, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
