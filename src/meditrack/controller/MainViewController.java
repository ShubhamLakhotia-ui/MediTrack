package meditrack.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * Controller for the main application view
 * Demonstrates the use of a Stack data structure for navigation history
 */
public class MainViewController implements Initializable {

    @FXML private BorderPane mainBorderPane;
    @FXML private Button dashboardButton;
    @FXML private Button expensesButton;
    @FXML private Button medicationsButton;
    @FXML private Button appointmentsButton;
    @FXML private Button analyticsButton;
    @FXML private StackPane contentArea;
    
    // Stack for navigation history - demonstration of the Stack data structure
    private Stack<String> navigationHistory = new Stack<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up event handlers for navigation buttons
        dashboardButton.setOnAction(e -> navigateTo("Dashboard"));
        expensesButton.setOnAction(e -> navigateTo("Expenses"));
        medicationsButton.setOnAction(e -> navigateTo("Medications"));
        appointmentsButton.setOnAction(e -> navigateTo("Appointment"));
//        analyticsButton.setOnAction(e -> navigateTo("Analytics"));
        
        // Load the dashboard by default
        navigateTo("Dashboard");
    }
    
    /**
     * Navigates to the specified view and pushes it onto the navigation stack
     * @param viewName The name of the view to navigate to
     */
//    private void navigateTo(String viewName) {
//        try {
//            // Push the current view onto the navigation stack
//            navigationHistory.push(viewName);
//            
//            // Use file-based approach that worked for MainView
//            File fxmlFile = new File("src/resources/fxml/" + viewName + "View.fxml");
//            
//            // Debug
//            System.out.println("File exists: " + fxmlFile.exists());
//            System.out.println("Trying to load: " + fxmlFile.getAbsolutePath());
//            
//            if (fxmlFile.exists()) {
//                // Load using file URL
//                URL url = fxmlFile.toURI().toURL();
//                Parent view = FXMLLoader.load(url);
//                
//                // Clear the content area and add the new view
//                contentArea.getChildren().clear();
//                contentArea.getChildren().add(view);
//                
//                System.out.println("Navigation successful to: " + viewName);
//            } else {
//                // Show placeholder when file doesn't exist
//                Label placeholder = new Label("The " + viewName + " view is not available yet.");
//                placeholder.setStyle("-fx-font-size: 18px;");
//                
//                contentArea.getChildren().clear();
//                contentArea.getChildren().add(placeholder);
//                
//                System.out.println("View file not found: " + fxmlFile.getAbsolutePath());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Error loading view: " + e.getMessage());
//            
//            // Show error message in the UI
//            Label errorMessage = new Label("Error loading " + viewName + " view: " + e.getMessage());
//            errorMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
//            
//            contentArea.getChildren().clear();
//            contentArea.getChildren().add(errorMessage);
//        }
//    }
//   

    
    
    
    private void navigateTo(String viewName) {
        try {
            // Push the current view onto the navigation stack
            navigationHistory.push(viewName);
            
            // Try different paths to find the FXML file
            String[] pathsToTry = {
                "src/resources/fxml/" + viewName + "View.fxml",
                "resources/fxml/" + viewName + "View.fxml",
                "fxml/" + viewName + "View.fxml"
            };
            
            File fxmlFile = null;
            for (String path : pathsToTry) {
                File tempFile = new File(path);
                if (tempFile.exists()) {
                    fxmlFile = tempFile;
                    System.out.println("Found file at: " + path);
                    break;
                }
            }
            
            if (fxmlFile != null && fxmlFile.exists()) {
                // Load using file URL
                URL url = fxmlFile.toURI().toURL();
                Parent view = FXMLLoader.load(url);
                
                // Clear the content area and add the new view
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
                
                System.out.println("Navigation successful to: " + viewName);
            } else {
                System.err.println("Could not find FXML file for view: " + viewName);
                
                // Show placeholder when file doesn't exist
                Label placeholder = new Label("The " + viewName + " view is not available yet.");
                placeholder.setStyle("-fx-font-size: 18px;");
                
                contentArea.getChildren().clear();
                contentArea.getChildren().add(placeholder);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + e.getMessage());
            
            // Show error message in the UI
            Label errorMessage = new Label("Error loading " + viewName + " view: " + e.getMessage());
            errorMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(errorMessage);
        }
    }

    /**
     * Navigates back to the previous view in the navigation history
     * Demonstrates popping from a Stack
     */
    @FXML
    private void navigateBack() {
        if (navigationHistory.size() > 1) {
            // Pop the current view
            navigationHistory.pop();
            
            // Get the previous view
            String previousView = navigationHistory.peek();
            
            // Navigate to the previous view without pushing to the stack
            try {
                File fxmlFile = new File("src/resources/fxml/" + previousView + "View.fxml");
                
                if (fxmlFile.exists()) {
                    URL url = fxmlFile.toURI().toURL();
                    Parent view = FXMLLoader.load(url);
                    
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(view);
                    
                    System.out.println("Navigation back to: " + previousView);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error navigating back: " + e.getMessage());
            }
        }
    }
}