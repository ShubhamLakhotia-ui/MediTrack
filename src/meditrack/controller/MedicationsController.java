package meditrack.controller;
import javafx.util.Callback;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import meditrack.model.Medication;
import meditrack.model.MedicationReminder;
import meditrack.datastructure.bag.BagInterface;
import meditrack.datastructure.bag.ResizableArrayBag;
import meditrack.datastructure.heap.MedicationReminderHeap;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;

/**
 * Controller for the medications view
 * Demonstrates heap data structure for priority queue
 */
public class MedicationsController implements Initializable {

    // Search and filter components
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> sortField;
    
    // Medications table and columns
    @FXML private TableView<Medication> medicationsTable;
    @FXML private TableColumn<Medication, String> nameColumn;
    @FXML private TableColumn<Medication, String> dosageColumn;
    @FXML private TableColumn<Medication, String> instructionsColumn;
    @FXML private TableColumn<Medication, String> prescriberColumn;
    @FXML private TableColumn<Medication, Integer> refillsColumn;
    @FXML private TableColumn<Medication, String> statusColumn;
    @FXML private ChoiceBox<Medication> reminderMedicationChoice;
    // Reminders components
    @FXML private ListView<MedicationReminder> remindersList;
//    @FXML private ComboBox<Medication> reminderMedication;
    @FXML private DatePicker reminderDate;
    @FXML private TextField reminderTime;
    @FXML private Slider prioritySlider;
    @FXML private Label priorityLabel;
    
    // Heap visualization
    @FXML private TextArea heapVisualization;
    
    // Data structures
    private BagInterface<Medication> medicationsBag;
    private MedicationReminderHeap reminderHeap;
    private ObservableList<Medication> medicationsList;
    private FilteredList<Medication> filteredMedications;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize data structures
        medicationsBag = new ResizableArrayBag<>();
        reminderHeap = new MedicationReminderHeap();
        
        // Create sample data
        createSampleData();
        
        // Setup medications table
        setupMedicationsTable();
        
        // Setup filter controls
        setupFilterControls();
        
        // Load medications data
        loadMedicationsData();
        
        // Setup reminders list
        setupRemindersList();
        
        // Setup reminder controls
        setupReminderControls();
        
        // Initialize heap visualization
        updateHeapVisualization();
    }
    
    /**
     * Creates sample medication and reminder data
     */
    private void createSampleData() {
        // Sample medications
        Medication med1 = new Medication(
            "Lisinopril", "10mg", 
            "Take one tablet daily with water", 
            "Dr. Smith", LocalDate.now().minusMonths(1), 
            3, 25.99);
            
        Medication med2 = new Medication(
            "Metformin", "500mg", 
            "Take one tablet twice daily with meals", 
            "Dr. Johnson", LocalDate.now().minusMonths(2), 
            2, 18.50);
            
        Medication med3 = new Medication(
            "Atorvastatin", "20mg", 
            "Take one tablet at bedtime", 
            "Dr. Wilson", LocalDate.now().minusWeeks(3), 
            5, 45.75);
            
        Medication med4 = new Medication(
            "Ibuprofen", "200mg", 
            "Take two tablets every 6 hours as needed for pain", 
            "Dr. Brown", LocalDate.now().minusDays(10), 
            0, 8.99);
            
        Medication med5 = new Medication(
            "Fluticasone", "50mcg", 
            "Use 2 sprays in each nostril once daily", 
            "Dr. Lee", LocalDate.now().minusWeeks(6), 
            1, 32.50);
        
        // Set one medication as inactive
        med4.setActive(false);
        
        // Add medications to bag
        medicationsBag.add(med1);
        medicationsBag.add(med2);
        medicationsBag.add(med3);
        medicationsBag.add(med4);
        medicationsBag.add(med5);
        
        // Sample reminders with varied priorities and times
//        MedicationReminder reminder1 = new MedicationReminder(
//            med1, LocalDateTime.now().plusHours(1), 3);
//            
//        MedicationReminder reminder2 = new MedicationReminder(
//            med2, LocalDateTime.now().plusHours(2), 4);
//            
//        MedicationReminder reminder3 = new MedicationReminder(
//            med3, LocalDateTime.now().plusHours(4), 2);
//            
//        MedicationReminder reminder4 = new MedicationReminder(
//            med5, LocalDateTime.now().plusHours(6), 1);
//            
//        MedicationReminder reminder5 = new MedicationReminder(
//            med2, LocalDateTime.now().plusHours(12), 3);
//        
//        // Add reminders to heap
//        reminderHeap.add(reminder1);
//        reminderHeap.add(reminder2);
//        reminderHeap.add(reminder3);
//        reminderHeap.add(reminder4);
//        reminderHeap.add(reminder5);
        
     // Sample reminders with varied priorities and times
        MedicationReminder reminder1 = new MedicationReminder(
            med1, LocalDateTime.now().plusHours(1), 3);
            
        MedicationReminder reminder2 = new MedicationReminder(
            med2, LocalDateTime.now().plusHours(2), 4);

        // Add reminders to heap
        reminderHeap.add(reminder1);
        reminderHeap.add(reminder2);
    }
    
    /**
     * Sets up the medications table columns
     */
    private void setupMedicationsTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        instructionsColumn.setCellValueFactory(new PropertyValueFactory<>("instructions"));
        prescriberColumn.setCellValueFactory(new PropertyValueFactory<>("prescriber"));
        refillsColumn.setCellValueFactory(new PropertyValueFactory<>("refillsRemaining"));
        
        // Status column with custom formatting
        statusColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().isActive() ? "Active" : "Inactive",
                new javafx.beans.Observable[]{}));
                
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    
                    if (status.equals("Active")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: gray;");
                    }
                }
            }
        });
    }
    
    /**
     * Sets up the filter controls
     */
    private void setupFilterControls() {
        // Status filter options
        statusFilter.getItems().addAll("All", "Active", "Inactive");
        statusFilter.getSelectionModel().selectFirst();
        
        // Sort field options
        sortField.getItems().addAll("Name", "Prescriber", "Refills Remaining");
        sortField.getSelectionModel().selectFirst();
    }
    
    /**
     * Loads medications data from the bag into the table
     */
    private void loadMedicationsData() {
        // Convert the bag to an array and then to an observable list
        // Medication[] medications = medicationsBag.toArray(); // This line is causing the error
        
        // Instead, use this approach:
        Object[] objectArray = medicationsBag.toArray();
        List<Medication> medicationList = new ArrayList<>();
        
        for (Object obj : objectArray) {
            if (obj instanceof Medication) {
                medicationList.add((Medication)obj);
            }
        }
        
        medicationsList = FXCollections.observableArrayList(medicationList);
        
        // Create a filtered list
        filteredMedications = new FilteredList<>(medicationsList);
        
        // Set the filter predicate
        applyFilters();
        
        // Set the filtered list as the table items
        medicationsTable.setItems(filteredMedications);
    }
    
    /**
     * Sets up the reminders list view
     */
    private void setupRemindersList() {
        // Get all reminders from the heap in priority order
        MedicationReminder[] reminders = reminderHeap.toSortedArray();
        ObservableList<MedicationReminder> reminderItems = FXCollections.observableArrayList(reminders);
        
        // Set cell factory for custom display
        remindersList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(MedicationReminder reminder, boolean empty) {
                super.updateItem(reminder, empty);
                
                if (empty || reminder == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Format the reminder for display
                    String medicationName = reminder.getMedication().getName();
                    String dosage = reminder.getMedication().getDosage();
                    String time = reminder.getDueDateTime().format(DateTimeFormatter.ofPattern("MMM d, h:mm a"));
                    String priority = "Priority: " + reminder.getPriority();
                    
                    // Create a VBox for the reminder display
                    javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(5);
                    
                    Label nameLabel = new Label(medicationName + " - " + dosage);
                    nameLabel.setStyle("-fx-font-weight: bold;");
                    
                    Label timeLabel = new Label("Due: " + time);
                    
                    Label priorityLabel = new Label(priority);
                    priorityLabel.setStyle("-fx-font-size: 11px;");
                    
                    // Style based on priority
                    String priorityStyle = switch (reminder.getPriority()) {
                        case 5 -> "priority-high";
                        case 4 -> "priority-medium";
                        case 3 -> "priority-medium";
                        default -> "priority-low";
                    };
                    
                    // Set border color based on priority
                    vbox.setStyle("-fx-border-color: " + getPriorityColor(reminder.getPriority()) + 
                                 "; -fx-border-width: 0 0 0 3px; -fx-padding: 5 5 5 10;");
                    
                    // Add labels to VBox
                    vbox.getChildren().addAll(nameLabel, timeLabel, priorityLabel);
                    
                    // Set the VBox as the cell's graphic
                    setGraphic(vbox);
                }
            }
            
            private String getPriorityColor(int priority) {
                return switch (priority) {
                    case 5 -> "#e74c3c";
                    case 4 -> "#f39c12";
                    case 3 -> "#f1c40f";
                    case 2 -> "#2ecc71";
                    default -> "#3498db";
                };
            }
        });
        
        // Set the items
        remindersList.setItems(reminderItems);
    }
    
    /**
     * Sets up the reminder control components
     */
    /**
     * Sets up the reminder control components
     */
//    private void setupReminderControls() {
//        // Convert Object[] to List<Medication> safely
//        Object[] objectArray = medicationsBag.toArray();
//        List<Medication> medicationList = new ArrayList<>();
//        
//        for (Object obj : objectArray) {
//            if (obj instanceof Medication) {
//                medicationList.add((Medication)obj);
//            }
//        }
//        
//        // Now set the items with the properly typed list
//        reminderMedication.setItems(FXCollections.observableArrayList(medicationList));
//        
//        // Custom cell factory for displaying medications
//        reminderMedication.setCellFactory(listView -> new ListCell<>() {
//            @Override
//            protected void updateItem(Medication medication, boolean empty) {
//                super.updateItem(medication, empty);
//                
//                if (empty || medication == null) {
//                    setText(null);
//                } else {
//                    setText(medication.getName() + " - " + medication.getDosage());
//                }
//            }
//        });
//        
//        // Custom string converter for the selected value
//        reminderMedication.setConverter(new StringConverter<>() {
//            @Override
//            public String toString(Medication medication) {
//                return medication == null ? "" : medication.getName() + " - " + medication.getDosage();
//            }
//            
//            @Override
//            public Medication fromString(String string) {
//                return null; // Not needed for our use case
//            }
//        });
//        
//        // Setup date picker
//        reminderDate.setValue(LocalDate.now());
//        
//        // Setup time field default
//        reminderTime.setText("08:00");
//        
//        // Setup priority slider listener
//        prioritySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
//            int priority = newVal.intValue();
//            priorityLabel.setText(String.valueOf(priority));
//        });
//    }
    
    /**
     * Sets up the reminder control components
     */
//    private void setupReminderControls() {
//        // Convert Object[] to List<Medication> safely
//        Object[] objectArray = medicationsBag.toArray();
//        List<Medication> medicationList = new ArrayList<>();
//        
//        for (Object obj : objectArray) {
//            if (obj instanceof Medication) {
//                medicationList.add((Medication)obj);
//            }
//        }
//        
//        // Create and set items
//        ObservableList<Medication> medicationOptions = FXCollections.observableArrayList(medicationList);
//        reminderMedication.setItems(medicationOptions);
//        
//        // Define a string formatter for medications
//        final java.util.function.Function<Medication, String> formatter = 
//            (med) -> med == null ? "" : med.getName() + " - " + med.getDosage();
//        
//        // Create a custom cell factory
//        reminderMedication.setCellFactory(lv -> new ListCell<Medication>() {
//            @Override
//            protected void updateItem(Medication item, boolean empty) {
//                super.updateItem(item, empty);
//                setText(empty || item == null ? "" : formatter.apply(item));
//            }
//        });
//        
//        // The key fix: create a custom button cell that forces an update
//        reminderMedication.setButtonCell(new ListCell<Medication>() {
//            {
//                // Add a listener to the ComboBox's selected item property
//                reminderMedication.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
//                    if (newVal != null) {
//                        setText(formatter.apply(newVal));
//                    } else {
//                        setText("");
//                    }
//                });
//            }
//            
//            @Override
//            protected void updateItem(Medication item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null) {
//                    setText("");
//                } else {
//                    setText(formatter.apply(item));
//                }
//            }
//        });
//        
//        // Add a direct value change listener for debugging
//        reminderMedication.valueProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("ComboBox value changed: " + 
//                (oldVal == null ? "null" : formatter.apply(oldVal)) + " -> " + 
//                (newVal == null ? "null" : formatter.apply(newVal)));
//        });
//        
//        // Setup date picker
//        reminderDate.setValue(LocalDate.now());
//        
//        // Setup time field default
//        reminderTime.setText("08:00");
//        
//        // Setup priority slider listener
//        prioritySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
//            int priority = newVal.intValue();
//            priorityLabel.setText(String.valueOf(priority));
//        });
//    }
    /**
     * Sets up the reminder control components
     */
    /**
     * Sets up the reminder control components
     */
    private void setupReminderControls() {
        // Convert Object[] to List<Medication> safely
        Object[] objectArray = medicationsBag.toArray();
        List<Medication> medicationList = new ArrayList<>();
        
        for (Object obj : objectArray) {
            if (obj instanceof Medication) {
                medicationList.add((Medication)obj);
            }
        }
        
        // Debug information
        System.out.println("Setting up ChoiceBox with " + medicationList.size() + " medications");
        
        // Create a special wrapper for the ChoiceBox 
        ObservableList<Medication> items = FXCollections.observableArrayList();
        
        // Add items to the ObservableList
        for (Medication med : medicationList) {
            items.add(med);
            System.out.println("Added to items: " + med.getName() + " - " + med.getDosage() + " (" + med.getId() + ")");
        }
        
        // Set the items in the ChoiceBox
        reminderMedicationChoice.setItems(items);
        
        // Setup the StringConverter
        reminderMedicationChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Medication med) {
                if (med == null) return "";
                return med.getName() + " - " + med.getDosage();
            }
            
            @Override
            public Medication fromString(String string) {
                // Not needed for ChoiceBox
                return null;
            }
        });
        
        // Explicitly handle selection changes
        reminderMedicationChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Selected: " + newVal.getName() + " - " + newVal.getDosage() + " (" + newVal.getId() + ")");
            }
        });
        
        // Setup date picker
        reminderDate.setValue(LocalDate.now());
        
        // Setup time field default
        reminderTime.setText("08:00");
        
        // Setup priority slider listener
        prioritySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int priority = newVal.intValue();
            priorityLabel.setText(String.valueOf(priority));
        });
    }
    /**
     * Updates the heap visualization text area
     */
    private void updateHeapVisualization() {
        // Get a copy of the heap for visualization
        MedicationReminderHeap heapCopy = new MedicationReminderHeap();
        MedicationReminder[] reminders = reminderHeap.toSortedArray();
        
        for (MedicationReminder reminder : reminders) {
            heapCopy.add(reminder);
        }
        
        StringBuilder visualization = new StringBuilder();
        visualization.append("Heap Size: ").append(heapCopy.getSize()).append("\n\n");
        visualization.append("Priority Queue (in priority order):\n");
        
        // Extract reminders in priority order
            
            // Extract reminders in priority order with more detailed info
            while (!heapCopy.isEmpty()) {
                MedicationReminder reminder = heapCopy.remove();
                String medName = reminder.getMedication().getName();
                String medDosage = reminder.getMedication().getDosage();
                String time = reminder.getDueDateTime().format(DateTimeFormatter.ofPattern("h:mm a"));
                int effectivePriority = reminder.getEffectivePriority();
                int basePriority = reminder.getPriority();
                
                visualization.append("Priority ").append(basePriority)
                            .append(" (Effective: ").append(effectivePriority).append("): ")
                            .append(medName).append(" ").append(medDosage)
                            .append(" at ").append(time).append("\n");
            }
        
        heapVisualization.setText(visualization.toString());
    }
    
    /**
     * Applies the current filters to the filtered list
     */
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        
        // Create a predicate based on the filters
        Predicate<Medication> predicate = medication -> {
            // Search text filter
            boolean matchesSearch = searchText.isEmpty() || 
                                  medication.getName().toLowerCase().contains(searchText) ||
                                  medication.getPrescriber().toLowerCase().contains(searchText) ||
                                  medication.getDosage().toLowerCase().contains(searchText);
            
            // Status filter
            boolean matchesStatus = status.equals("All") || 
                                  (status.equals("Active") && medication.isActive()) ||
                                  (status.equals("Inactive") && !medication.isActive());
            
            return matchesSearch && matchesStatus;
        };
        
        // Apply the predicate
        filteredMedications.setPredicate(predicate);
        
        // Sort the medications based on selected field
        // You can't sort filteredMedications directly, so sort the source list instead
        String sortOption = sortField.getValue();
        
        if (sortOption != null) {
            Comparator<Medication> comparator = switch (sortOption) {
                case "Name" -> Comparator.comparing(Medication::getName);
                case "Prescriber" -> Comparator.comparing(Medication::getPrescriber);
                case "Refills Remaining" -> Comparator.comparing(Medication::getRefillsRemaining).reversed();
                default -> Comparator.comparing(Medication::getName);
            };
            
            // Create a sorted copy of the data
            List<Medication> sorted = new ArrayList<>(medicationsList);
            sorted.sort(comparator);
            
            // Update the observable list with the sorted data
            medicationsList.clear();
            medicationsList.addAll(sorted);
        }
    }
    /**
     * Handles the apply filters button
     */
    @FXML
    private void handleApplyFilters() {
        applyFilters();
    }
    
    /**
     * Handles the clear filters button
     */
    @FXML
    private void handleClearFilters() {
        // Clear filters
        searchField.clear();
        statusFilter.getSelectionModel().selectFirst();
        sortField.getSelectionModel().selectFirst();
        
        // Apply cleared filters
        applyFilters();
    }
    
    /**
     * Handles the add medication button
     */
    @FXML
    private void handleAddMedication() {
        // Create a dialog to get medication information
        Dialog<Medication> dialog = new Dialog<>();
        dialog.setTitle("Add Medication");
        dialog.setHeaderText("Enter medication details:");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form with labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Medication name");
        TextField dosageField = new TextField();
        dosageField.setPromptText("e.g., 10mg");
        TextField instructionsField = new TextField();
        instructionsField.setPromptText("e.g., Take one tablet daily");
        TextField prescriberField = new TextField();
        prescriberField.setPromptText("Doctor's name");
        TextField refillsField = new TextField();
        refillsField.setPromptText("Number of refills");
        TextField costField = new TextField();
        costField.setPromptText("Cost per refill");
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Dosage:"), 0, 1);
        grid.add(dosageField, 1, 1);
        grid.add(new Label("Instructions:"), 0, 2);
        grid.add(instructionsField, 1, 2);
        grid.add(new Label("Prescriber:"), 0, 3);
        grid.add(prescriberField, 1, 3);
        grid.add(new Label("Refills:"), 0, 4);
        grid.add(refillsField, 1, 4);
        grid.add(new Label("Cost:"), 0, 5);
        grid.add(costField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the name field by default
        Platform.runLater(() -> nameField.requestFocus());
        
        // Convert the result to a medication when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    String dosage = dosageField.getText();
                    String instructions = instructionsField.getText();
                    String prescriber = prescriberField.getText();
                    int refills = Integer.parseInt(refillsField.getText());
                    double cost = Double.parseDouble(costField.getText());
                    
                    return new Medication(
                        name, dosage, instructions, prescriber,
                        LocalDate.now(), refills, cost
                    );
                } catch (NumberFormatException e) {
                    showAlert("Please enter valid numbers for refills and cost");
                    return null;
                }
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<Medication> result = dialog.showAndWait();
        
        result.ifPresent(medication -> {
            // Add the medication to the bag
            medicationsBag.add(medication);
            
            // Refresh the table
            loadMedicationsData();
         // Add the medication to the reminder dropdown
            reminderMedicationChoice.getItems().add(medication);
            // Show confirmation
            showAlert("Medication '" + medication.getName() + "' has been added");
        });
    }
    
    /**
     * Handles the edit medication button
     */
    @FXML
    private void handleEditMedication() {
        // Get the selected medication
        Medication selectedMedication = medicationsTable.getSelectionModel().getSelectedItem();
        
        if (selectedMedication != null) {
            // In a real application, this would open a dialog to edit the medication
            System.out.println("Edit Medication button clicked for: " + selectedMedication.getName());
        } else {
            // Show alert if no medication is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a medication to edit.");
            alert.showAndWait();
        }
    }
    
    /**
     * Handles the set inactive button
     */
    @FXML
    private void handleSetInactive() {
        // Get the selected medication
        Medication selectedMedication = medicationsTable.getSelectionModel().getSelectedItem();
        
        if (selectedMedication != null) {
            // Toggle active state
            boolean newState = !selectedMedication.isActive();
            selectedMedication.setActive(newState);
            
            // Update the table
            medicationsTable.refresh();
        } else {
            // Show alert if no medication is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a medication to change its status.");
            alert.showAndWait();
        }
    }
    
    /**
     * Handles the request refill button
     */
    @FXML
    private void handleRequestRefill() {
        // Get the selected medication
        Medication selectedMedication = medicationsTable.getSelectionModel().getSelectedItem();
        
        if (selectedMedication != null) {
            // Add a refill (in a real app, this might send a request to a provider)
            selectedMedication.setRefillsRemaining(selectedMedication.getRefillsRemaining() + 1);
            
            // Update the table
            medicationsTable.refresh();
            
            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Refill Requested");
            alert.setHeaderText(null);
            alert.setContentText("A refill has been requested for " + selectedMedication.getName());
            alert.showAndWait();
        } else {
            // Show alert if no medication is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a medication to request a refill.");
            alert.showAndWait();
        }
    }
    
    /**
     * Handles the add reminder button
     */
    /**
     * Handles the add reminder button
     */
    /**
     * Handles the add reminder button
     */
    @FXML
    private void handleAddReminder() {
        try {
            // Get form values with extra validation
        	Medication medication = reminderMedicationChoice.getValue();
            System.out.println("Selected medication: " + 
                (medication != null ? medication.getName() + " - " + medication.getDosage() + " (" + medication.getId() + ")" : "null"));
            
            // Try alternate methods if value is null
            if (medication == null) {
                int selectedIndex = reminderMedicationChoice.getSelectionModel().getSelectedIndex();
                System.out.println("Selected index: " + selectedIndex);
                
                if (selectedIndex >= 0) {
                    medication = reminderMedicationChoice.getItems().get(selectedIndex);
                    System.out.println("Retrieved by index: " + 
                        (medication != null ? medication.getName() + " - " + medication.getDosage() : "still null"));
                }
            }

            
            LocalDate date = reminderDate.getValue();
            String timeStr = reminderTime.getText();
            int priority = (int) prioritySlider.getValue();
            
            // Validate input
            if (medication == null) {
                showAlert("Please select a medication");
                return;
            }
            
            if (date == null) {
                showAlert("Please select a date");
                return;
            }
            
            // Parse time
            LocalTime time;
            try {
                time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                showAlert("Please enter a valid time in HH:MM format");
                return;
            }
            
            // Create date time from date and time
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            
            // Create the reminder
            MedicationReminder reminder = new MedicationReminder(medication, dateTime, priority);
            
            // Add to heap
            reminderHeap.add(reminder);
            
            // Update the list view
            setupRemindersList();
            
            // Update heap visualization
            updateHeapVisualization();
            
            // Clear form fields
            reminderDate.setValue(LocalDate.now());
            reminderTime.setText("08:00");
            prioritySlider.setValue(3);
            
            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reminder Added");
            alert.setHeaderText(null);
            alert.setContentText("The reminder has been added successfully: " + 
                                medication.getName() + " - " + medication.getDosage() + 
                                " at " + time.format(DateTimeFormatter.ofPattern("HH:mm")));
            alert.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            showAlert("Error adding reminder: " + e.getMessage());
        }
    }
    /**
     * Handles the mark highest priority reminder as taken
     */
    @FXML
    private void handleMarkHighestPriority() {
        if (reminderHeap.isEmpty()) {
            showAlert("No reminders in the queue");
            return;
        }
        System.out.println("----- REMINDERS IN HEAP BEFORE REMOVAL -----");
        MedicationReminder[] reminders = reminderHeap.toSortedArray();
        for (MedicationReminder r : reminders) {
            System.out.println("Name: " + r.getMedication().getName() 
                             + ", Base Priority: " + r.getPriority() 
                             + ", Effective Priority: " + r.getEffectivePriority()
                             + ", Minutes until due: " + r.minutesUntilDue());
        }
        
        // Get and remove the highest priority reminder
        MedicationReminder highestPriority = reminderHeap.remove();
        
        System.out.println("----- REMOVED REMINDER -----");
        System.out.println("Name: " + highestPriority.getMedication().getName() 
                         + ", Base Priority: " + highestPriority.getPriority() 
                         + ", Effective Priority: " + highestPriority.getEffectivePriority()
                         + ", Minutes until due: " + highestPriority.minutesUntilDue());

        
        // Show confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Medication Taken");
        alert.setHeaderText(null);
        alert.setContentText("Marked " + highestPriority.getMedication().getName() + " as taken.");
        alert.showAndWait();
        
        // Update the list view
        setupRemindersList();
        
        // Update heap visualization
        updateHeapVisualization();
    }/**
     * Handles the refresh heap visualization button
     */
    @FXML
    private void handleRefreshHeap() {
        updateHeapVisualization();
    }
    
    /**
     * Shows an alert with the given message
     * @param message The message to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}