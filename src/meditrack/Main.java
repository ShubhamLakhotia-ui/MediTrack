package meditrack;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Print the working directory for debugging
            System.out.println("Working directory: " + System.getProperty("user.dir"));
            
            // Try to find the FXML file directly
            File fxmlFile = new File("src/resources/fxml/MainView.fxml");
            System.out.println("File exists: " + fxmlFile.exists());
            System.out.println("Absolute path: " + fxmlFile.getAbsolutePath());
            
            if (fxmlFile.exists()) {
                // Load using file URL
                URL url = fxmlFile.toURI().toURL();
                System.out.println("Loading from: " + url);
                Parent root = FXMLLoader.load(url);
                
                Scene scene = new Scene(root, 1000, 700);
                primaryStage.setTitle("MediTrack - Healthcare Expense Tracker");
                primaryStage.setScene(scene);
                primaryStage.show();
            } else {
                // Fall back to a programmatically created UI
                createProgrammaticUI(primaryStage);
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error details: " + e.getMessage());
            
            // Fall back to a programmatically created UI
            createProgrammaticUI(primaryStage);
        }
    }
    
    private void createProgrammaticUI(Stage primaryStage) {
        // Create a simple UI programmatically as a fallback
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        javafx.scene.layout.VBox top = new javafx.scene.layout.VBox(10);
        javafx.scene.control.Label title = new javafx.scene.control.Label("MediTrack");
        title.setStyle("-fx-font-size: 24px; -fx-padding: 10px;");
        
        javafx.scene.control.Button btn = new javafx.scene.control.Button("Click Me");
        btn.setOnAction(e -> System.out.println("Button clicked!"));
        
        top.getChildren().addAll(title, btn);
        root.setTop(top);
        
        javafx.scene.control.Label center = new javafx.scene.control.Label("FXML loading failed. This is a fallback UI.");
        center.setStyle("-fx-font-size: 18px;");
        root.setCenter(center);
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("MediTrack (Fallback UI)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}