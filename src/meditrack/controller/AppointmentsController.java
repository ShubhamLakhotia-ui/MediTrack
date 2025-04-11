package meditrack.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import meditrack.model.Appointment;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;

/**
 * Controller for the appointments view
 */
public class AppointmentsController implements Initializable {

    // Search and filter components
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> sortField;
    
    // Appointments table and columns
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, LocalDateTime> dateTimeColumn;
    @FXML private TableColumn<Appointment, String> providerColumn;
    @FXML private TableColumn<Appointment, String> specialtyColumn;
    @FXML private TableColumn<Appointment, String> reasonColumn;
    @FXML private TableColumn<Appointment, String> locationColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    
    // Details panel
    @FXML private Label detailProviderLabel;
    @FXML private Label detailSpecialtyLabel;
    @FXML private Label detailDateTimeLabel;
    @FXML private Label detailLocationLabel;
    @FXML private Label detailReasonLabel;
    @FXML private TextArea detailNotesArea;
    @FXML private CheckBox reminderCheckbox;
    @FXML private TextField reminderMinutesField;
    @FXML private Button updateReminderButton;
    
    // New appointment form
    @FXML private TextField newProviderField;
    @FXML private TextField newSpecialtyField;
    @FXML private DatePicker newDateField;
    @FXML private TextField newTimeField;
    @FXML private TextField newLocationField;
    @FXML private TextField newReasonField;
    
    // Upcoming appointments
    @FXML private ListView<Appointment> upcomingAppointmentsListView;
    
    // Data structures
    private ObservableList<Appointment> appointmentsList;
    private FilteredList<Appointment> filteredAppointments;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup appointments table
        setupAppointmentsTable();
        
        // Setup filter controls
        setupFilterControls();
        
        // Create sample data
        createSampleData();
        
        // Load appointments data
        loadAppointmentsData();
        
        // Setup details panel
        setupDetailsPanel();
        
        // Setup upcoming appointments list
        updateUpcomingRemindersList();
        
        // Add selection listener to update details panel
        appointmentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateDetailsPanel(newSelection);
            updateReminderUI(newSelection);
        });
    }
    
    /**
     * Sets up the appointments table columns
     */
    private void setupAppointmentsTable() {
        // Setup column cell factories
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateTimeColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
            
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                
                if (empty || dateTime == null) {
                    setText(null);
                } else {
                    setText(formatter.format(dateTime));
                }
            }
        });
        
        providerColumn.setCellValueFactory(new PropertyValueFactory<>("provider"));
        specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("providerSpecialty"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        // Status column with custom formatting
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    
                    if (appointment.hasPassed()) {
                        setText("Past");
                        setStyle("-fx-text-fill: gray;");
                    } else if (appointment.isUpcoming()) {
                        setText("Upcoming");
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setText("Future");
                        setStyle("-fx-text-fill: blue;");
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
        statusFilter.getItems().addAll("All", "Upcoming", "Future", "Past");
        statusFilter.getSelectionModel().selectFirst();
        
        // Sort field options
        sortField.getItems().addAll("Date & Time", "Provider", "Specialty");
        sortField.getSelectionModel().selectFirst();
    }
    
    /**
     * Creates sample appointment data
     */
    private void createSampleData() {
        // Create an observable list for appointments
        appointmentsList = FXCollections.observableArrayList();
        
        // Add sample appointments
        appointmentsList.add(new Appointment(
            LocalDateTime.now().plusDays(1), 
            "Dr. Smith", 
            "Annual Check-up", 
            "Primary Care",
            "123 Medical Center, Suite 100"
        ));
        
        appointmentsList.add(new Appointment(
            LocalDateTime.now().plusDays(7), 
            "Dr. Johnson", 
            "Dental Cleaning", 
            "Dentistry",
            "456 Dental Plaza, Room 205"
        ));
        
        appointmentsList.add(new Appointment(
            LocalDateTime.now().plusDays(14), 
            "Dr. Williams", 
            "Eye Exam", 
            "Ophthalmology",
            "789 Vision Center"
        ));
        
        appointmentsList.add(new Appointment(
            LocalDateTime.now().minusDays(7), 
            "Dr. Brown", 
            "Follow-up Appointment", 
            "Cardiology",
            "Heart Health Building, 3rd Floor"
        ));
        
        // Set reminders for some appointments
        appointmentsList.get(0).setReminder(60); // 1 hour before
        appointmentsList.get(1).setReminder(120); // 2 hours before
    }
    
    /**
     * Loads appointments data into the table
     */
    private void loadAppointmentsData() {
        // Create a filtered list
        filteredAppointments = new FilteredList<>(appointmentsList);
        
        // Set the filter predicate
        applyFilters();
        
        // Set the filtered list as the table items
        appointmentsTable.setItems(filteredAppointments);
        
        // Select the first item if available
        if (!appointmentsList.isEmpty()) {
            appointmentsTable.getSelectionModel().selectFirst();
        }
    }
    
    /**
     * Sets up the details panel
     */
    private void setupDetailsPanel() {
        // Setup reminder checkbox listener
        reminderCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            reminderMinutesField.setDisable(!newVal);
            updateReminderButton.setDisable(!newVal);
        });
    }
    
    /**
     * Updates the details panel with the selected appointment
     * @param appointment The selected appointment
     */
    private void updateDetailsPanel(Appointment appointment) {
        if (appointment == null) {
            // Clear all fields
            detailProviderLabel.setText("");
            detailSpecialtyLabel.setText("");
            detailDateTimeLabel.setText("");
            detailLocationLabel.setText("");
            detailReasonLabel.setText("");
            detailNotesArea.setText("");
            reminderCheckbox.setSelected(false);
            reminderMinutesField.setText("");
            reminderMinutesField.setDisable(true);
            updateReminderButton.setDisable(true);
        } else {
            // Format the date/time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy h:mm a");
            
            // Update all fields
            detailProviderLabel.setText(appointment.getProvider());
            detailSpecialtyLabel.setText(appointment.getProviderSpecialty());
            detailDateTimeLabel.setText(formatter.format(appointment.getDateTime()));
            detailLocationLabel.setText(appointment.getLocation());
            detailReasonLabel.setText(appointment.getReason());
            detailNotesArea.setText(appointment.getNotes());
        }
    }
    
    /**
     * Updates the reminder UI when an appointment is selected
     */
    private void updateReminderUI(Appointment selectedAppointment) {
        if (selectedAppointment == null) {
            reminderCheckbox.setSelected(false);
            reminderMinutesField.setDisable(true);
            updateReminderButton.setDisable(true);
            return;
        }
        
        // Update reminder fields based on the selected appointment
        reminderCheckbox.setSelected(selectedAppointment.isReminderSet());
        reminderMinutesField.setText(String.valueOf(selectedAppointment.getReminderMinutesBefore()));
        reminderMinutesField.setDisable(!selectedAppointment.isReminderSet());
        updateReminderButton.setDisable(!selectedAppointment.isReminderSet());
    }
    
    /**
     * Updates the upcoming reminders list
     */
    private void updateUpcomingRemindersList() {
        // Filter appointments with reminders set
        List<Appointment> appointmentsWithReminders = new ArrayList<>();
        
        for (Appointment appointment : appointmentsList) {
            if (appointment.isReminderSet() && !appointment.hasPassed()) {
                appointmentsWithReminders.add(appointment);
            }
        }
        
        // Sort by reminder time
        appointmentsWithReminders.sort(Comparator.comparing(Appointment::getReminderDateTime));
        
        // Update the list view
        upcomingAppointmentsListView.setItems(FXCollections.observableArrayList(appointmentsWithReminders));
        
        // Set cell factory to display reminder info
        upcomingAppointmentsListView.setCellFactory(listView -> new ListCell<Appointment>() {
            @Override
            protected void updateItem(Appointment appointment, boolean empty) {
                super.updateItem(appointment, empty);
                
                if (empty || appointment == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, h:mm a");
                    
                    String reminderTime = appointment.getReminderDateTime().format(formatter);
                    String appointmentTime = appointment.getDateTime().format(formatter);
                    
                    setText("Reminder at " + reminderTime + " for appointment with " + 
                          appointment.getProvider() + " at " + appointmentTime);
                    
                    // Style based on how soon the reminder is
                    long minutesUntil = java.time.Duration.between(
                        LocalDateTime.now(), appointment.getReminderDateTime()).toMinutes();
                    
                    if (minutesUntil < 0) {
                        // Past reminder
                        setStyle("-fx-text-fill: gray;");
                    } else if (minutesUntil < 60) {
                        // Due within an hour
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if (minutesUntil < 24 * 60) {
                        // Due within 24 hours
                        setStyle("-fx-text-fill: orange;");
                    } else {
                        // Due later
                        setStyle("");
                    }
                }
            }
        });
    }
    
    /**
     * Applies the current filters to the filtered list
     */
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        
        // Create a predicate based on the filters
        Predicate<Appointment> predicate = appointment -> {
            // Search text filter
            boolean matchesSearch = searchText.isEmpty() || 
                                  appointment.getProvider().toLowerCase().contains(searchText) ||
                                  appointment.getReason().toLowerCase().contains(searchText) ||
                                  appointment.getProviderSpecialty().toLowerCase().contains(searchText) ||
                                  appointment.getLocation().toLowerCase().contains(searchText);
            
            // Status filter
            boolean matchesStatus;
            
            switch (status) {
                case "Upcoming":
                    matchesStatus = appointment.isUpcoming();
                    break;
                case "Past":
                    matchesStatus = appointment.hasPassed();
                    break;
                case "Future":
                    matchesStatus = !appointment.hasPassed() && !appointment.isUpcoming();
                    break;
                default: // "All"
                    matchesStatus = true;
                    break;
            }
            
            return matchesSearch && matchesStatus;
        };
        
        // Apply the predicate
        filteredAppointments.setPredicate(predicate);
        
        // Sort the appointments based on selected field
        String sortOption = sortField.getValue();
        
        if (sortOption != null) {
            Comparator<Appointment> comparator;
            
            switch (sortOption) {
                case "Provider":
                    comparator = Comparator.comparing(Appointment::getProvider);
                    break;
                case "Specialty":
                    comparator = Comparator.comparing(Appointment::getProviderSpecialty);
                    break;
                default: // "Date & Time"
                    comparator = Comparator.comparing(Appointment::getDateTime);
                    break;
            }
            
            // Create a sorted copy of the data
            List<Appointment> sorted = new ArrayList<>(appointmentsList);
            sorted.sort(comparator);
            
            // Update the observable list with the sorted data
            appointmentsList.clear();
            appointmentsList.addAll(sorted);
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
     * Handles the add appointment button
     */
    @FXML
    private void handleAddAppointment() {
        try {
            // Validate input
            String provider = newProviderField.getText();
            if (provider.trim().isEmpty()) {
                showAlert("Please enter a provider name");
                return;
            }
            
            String specialty = newSpecialtyField.getText();
            if (specialty.trim().isEmpty()) {
                showAlert("Please enter a provider specialty");
                return;
            }
            
            LocalDate date = newDateField.getValue();
            if (date == null) {
                showAlert("Please select a date");
                return;
            }
            
            String timeText = newTimeField.getText();
            if (timeText.trim().isEmpty()) {
                showAlert("Please enter a time");
                return;
            }
            
            // Parse time
            LocalTime time;
            try {
                time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                showAlert("Please enter a valid time in HH:MM format");
                return;
            }
            
            String reason = newReasonField.getText();
            if (reason.trim().isEmpty()) {
                showAlert("Please enter a reason for the appointment");
                return;
            }
            
            String location = newLocationField.getText();
            
            // Create date time from date and time
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            
            // Create the appointment
            Appointment appointment = new Appointment(dateTime, provider, reason, specialty, location);
            
            // Add to the list
            appointmentsList.add(appointment);
            
            // Refresh the table
            applyFilters();
            
            // Clear form fields
            newProviderField.clear();
            newSpecialtyField.clear();
            newDateField.setValue(null);
            newTimeField.clear();
            newLocationField.clear();
            newReasonField.clear();
            
            // Select the new appointment
            appointmentsTable.getSelectionModel().select(appointment);
            
            // Update reminders
            updateUpcomingRemindersList();
            
            // Show confirmation
            showAlert("Appointment added successfully");
            
        } catch (Exception e) {
            showAlert("Error adding appointment: " + e.getMessage());
        }
    }
    
    /**
     * Handles the edit appointment button
     */
    @FXML
    private void handleEditAppointment() {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        
        if (selectedAppointment == null) {
            showAlert("Please select an appointment to edit");
            return;
        }
        
        // In a real application, this would open a dialog to edit the appointment
        // For demonstration, we'll just update the form fields
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        newProviderField.setText(selectedAppointment.getProvider());
        newSpecialtyField.setText(selectedAppointment.getProviderSpecialty());
        newDateField.setValue(selectedAppointment.getDateTime().toLocalDate());
        newTimeField.setText(selectedAppointment.getDateTime().format(timeFormatter));
        newLocationField.setText(selectedAppointment.getLocation());
        newReasonField.setText(selectedAppointment.getReason());
        
        // Show message
        showAlert("Please update the appointment details and click 'Add Appointment' to save changes");
    }
    
    /**
     * Handles the delete appointment button
     */
    @FXML
    private void handleDeleteAppointment() {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        
        if (selectedAppointment == null) {
            showAlert("Please select an appointment to delete");
            return;
        }
        
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this appointment?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Remove from the list
            appointmentsList.remove(selectedAppointment);
            
            // Refresh the table
            applyFilters();
            
            // Update reminders
            updateUpcomingRemindersList();
            
            // Show confirmation
            showAlert("Appointment deleted successfully");
        }
    }
    
    /**
     * Handles the toggle reminder checkbox
     */
    @FXML
    private void handleToggleReminder() {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        
        if (selectedAppointment == null) {
            reminderCheckbox.setSelected(false);
            return;
        }
        
        boolean isSelected = reminderCheckbox.isSelected();
        
        reminderMinutesField.setDisable(!isSelected);
        updateReminderButton.setDisable(!isSelected);
        
        if (isSelected) {
            reminderMinutesField.setText(String.valueOf(selectedAppointment.getReminderMinutesBefore()));
        } else {
            selectedAppointment.disableReminder();
            updateUpcomingRemindersList(); // Update the reminder list
        }
    }
    
    /**
     * Handles the update reminder button
     */
    @FXML
    private void handleUpdateReminder() {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        
        if (selectedAppointment == null) {
            return;
        }
        
        try {
            int minutes = Integer.parseInt(reminderMinutesField.getText());
            
            if (minutes <= 0) {
                showAlert("Please enter a positive number of minutes");
                return;
            }
            
            selectedAppointment.setReminder(minutes);
            updateUpcomingRemindersList(); // Update the reminder list
            showAlert("Reminder updated successfully");
            
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid number of minutes");
        }
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