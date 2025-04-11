package meditrack.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import meditrack.model.*;
import meditrack.datastructure.bag.BagInterface;
import meditrack.datastructure.bag.ResizableArrayBag;
import meditrack.datastructure.heap.MedicationReminderHeap;
import meditrack.datastructure.recursion.RecursiveExpenseAnalyzer;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label totalExpensesLabel;
    @FXML private Label monthExpensesLabel;
    @FXML private Label reimbursedLabel;
    @FXML private Label activeMedsLabel;
    @FXML private Label pendingRefillsLabel;
    @FXML private Label todayRemindersLabel;

    @FXML private TableView<Appointment> upcomingAppointmentsTable;
    @FXML private TableColumn<Appointment, String> appointmentDateColumn;
    @FXML private TableColumn<Appointment, String> appointmentTimeColumn;
    @FXML private TableColumn<Appointment, String> appointmentProviderColumn;
    @FXML private TableColumn<Appointment, String> appointmentReasonColumn;

    @FXML private TableView<MedicationReminder> medicationRemindersTable;
    @FXML private TableColumn<MedicationReminder, String> reminderTimeColumn;
    @FXML private TableColumn<MedicationReminder, String> reminderMedicationColumn;
    @FXML private TableColumn<MedicationReminder, String> reminderDosageColumn;
    @FXML private TableColumn<MedicationReminder, Integer> reminderPriorityColumn;

    @FXML private TableView<MedicalExpense> recentExpensesTable;
    @FXML private TableColumn<MedicalExpense, String> expenseDateColumn;
    @FXML private TableColumn<MedicalExpense, String> expenseDescriptionColumn;
    @FXML private TableColumn<MedicalExpense, String> expenseCategoryColumn;
    @FXML private TableColumn<MedicalExpense, Double> expenseAmountColumn;
    @FXML private TableColumn<MedicalExpense, String> expenseProviderColumn;

    @FXML private PieChart expenseCategoryChart;
    @FXML private LineChart<String, Number> monthlyExpenseChart;

    private BagInterface<MedicalExpense> expensesBag;
    private BagInterface<Medication> medicationsBag;
    private MedicationReminderHeap remindersHeap;
    private List<Appointment> appointments;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        expensesBag = new ResizableArrayBag<>();
        medicationsBag = new ResizableArrayBag<>();
        remindersHeap = new MedicationReminderHeap();
        appointments = new ArrayList<>();

        createSampleData();
        setupTables();
        updateSummaryLabels();
        setupCharts();
    }

    private void createSampleData() {
        expensesBag.add(new MedicalExpense("Annual Checkup", 150.00, LocalDate.now().minusDays(2), MedicalExpense.ExpenseCategory.CONSULTATION, "Dr. Smith"));
        expensesBag.add(new MedicalExpense("Blood Test", 75.50, LocalDate.now().minusDays(2), MedicalExpense.ExpenseCategory.LABORATORY, "City Lab"));
        expensesBag.add(new MedicalExpense("Prescription - Lisinopril", 45.99, LocalDate.now().minusDays(1), MedicalExpense.ExpenseCategory.MEDICATION, "City Pharmacy"));
        expensesBag.add(new MedicalExpense("X-Ray - Chest", 250.00, LocalDate.now().minusWeeks(2), MedicalExpense.ExpenseCategory.IMAGING, "Memorial Hospital"));
        expensesBag.add(new MedicalExpense("Therapy Session", 120.00, LocalDate.now().minusWeeks(1), MedicalExpense.ExpenseCategory.THERAPY, "Dr. Johnson"));

        Medication med1 = new Medication("Lisinopril", "10mg", "Take one tablet daily", "Dr. Smith", LocalDate.now().minusMonths(1), 3, 45.99);
        Medication med2 = new Medication("Metformin", "500mg", "Take one tablet twice daily with meals", "Dr. Johnson", LocalDate.now().minusMonths(2), 2, 35.50);
        Medication med3 = new Medication("Ibuprofen", "200mg", "Take two tablets every 6 hours as needed for pain", "Dr. Smith", LocalDate.now().minusDays(10), 1, 12.99);

        medicationsBag.add(med1);
        medicationsBag.add(med2);
        medicationsBag.add(med3);

        remindersHeap.add(new MedicationReminder(med1, LocalDateTime.now().plusHours(1), 3));
        remindersHeap.add(new MedicationReminder(med2, LocalDateTime.now().plusHours(2), 4));
        remindersHeap.add(new MedicationReminder(med3, LocalDateTime.now().plusHours(4), 2));

        appointments.add(new Appointment(LocalDateTime.now().plusDays(3), "Dr. Smith", "Follow-up Consultation", "General Practitioner"));
        appointments.add(new Appointment(LocalDateTime.now().plusDays(7), "Dr. Johnson", "Therapy Session", "Therapist"));
        appointments.add(new Appointment(LocalDateTime.now().plusWeeks(2), "City Lab", "Blood Test", "Laboratory"));
    }

    private void setupTables() {
        expenseDateColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
            () -> cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
        expenseDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        expenseCategoryColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
            () -> cellData.getValue().getCategory().getDisplayName()));
        expenseAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        expenseProviderColumn.setCellValueFactory(new PropertyValueFactory<>("provider"));

        Object[] expenseObjects = expensesBag.toArray();
        List<MedicalExpense> expenseList = new ArrayList<>();
        for (Object obj : expenseObjects) {
            if (obj instanceof MedicalExpense) {
                expenseList.add((MedicalExpense) obj);
            }
        }
        recentExpensesTable.setItems(FXCollections.observableArrayList(expenseList));

        appointmentDateColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
            () -> cellData.getValue().getDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
        appointmentTimeColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
            () -> cellData.getValue().getDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"))));
        appointmentProviderColumn.setCellValueFactory(new PropertyValueFactory<>("provider"));
        appointmentReasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        upcomingAppointmentsTable.setItems(FXCollections.observableArrayList(appointments));

        reminderTimeColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
            () -> cellData.getValue().getDueDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"))));
        reminderMedicationColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
            () -> cellData.getValue().getMedication().getName()));
        reminderDosageColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
            () -> cellData.getValue().getMedication().getDosage()));
        reminderPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        medicationRemindersTable.setItems(FXCollections.observableArrayList(remindersHeap.toSortedArray()));
    }

    private void updateSummaryLabels() {
        Object[] medObjectArray = medicationsBag.toArray();
        List<Medication> medicationList = new ArrayList<>();
        for (Object obj : medObjectArray) {
            if (obj instanceof Medication) {
                medicationList.add((Medication) obj);
            }
        }

        Object[] expenseObjectArray = expensesBag.toArray();
        List<MedicalExpense> expenseList = new ArrayList<>();
        for (Object obj : expenseObjectArray) {
            if (obj instanceof MedicalExpense) {
                expenseList.add((MedicalExpense) obj);
            }
        }

        double totalExpenses = RecursiveExpenseAnalyzer.calculateTotalExpenses(expenseList);
        totalExpensesLabel.setText(String.format("$%.2f", totalExpenses));

        LocalDate now = LocalDate.now();
        LocalDate firstOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        List<MedicalExpense> currentMonthExpenses = RecursiveExpenseAnalyzer.findExpensesInDateRange(expenseList, firstOfMonth, now);
        double monthExpenses = RecursiveExpenseAnalyzer.calculateTotalExpenses(currentMonthExpenses);
        monthExpensesLabel.setText(String.format("$%.2f", monthExpenses));

        reimbursedLabel.setText("$150.00");

        int activeMeds = 0;
        int pendingRefills = 0;
        for (Medication med : medicationList) {
            if (med.isActive()) {
                activeMeds++;
                if (med.getRefillsRemaining() <= 1) {
                    pendingRefills++;
                }
            }
        }
        activeMedsLabel.setText(String.valueOf(activeMeds));
        pendingRefillsLabel.setText(String.valueOf(pendingRefills));
        todayRemindersLabel.setText(String.valueOf(remindersHeap.getSize()));
    }

    private void setupCharts() {
        Object[] expenseObjectArray = expensesBag.toArray();
        List<MedicalExpense> expenseList = new ArrayList<>();
        for (Object obj : expenseObjectArray) {
            if (obj instanceof MedicalExpense) {
                expenseList.add((MedicalExpense) obj);
            }
        }

        Map<MedicalExpense.ExpenseCategory, Double> categoryTotals = RecursiveExpenseAnalyzer.calculateExpensesByCategory(expenseList);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<MedicalExpense.ExpenseCategory, Double> entry : categoryTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey().getDisplayName(), entry.getValue()));
        }
        expenseCategoryChart.setData(pieChartData);

        LineChart.Series<String, Number> series = new LineChart.Series<>();
        series.setName("Monthly Expenses");
        series.getData().add(new LineChart.Data<>("Jan", 420.00));
        series.getData().add(new LineChart.Data<>("Feb", 350.50));
        series.getData().add(new LineChart.Data<>("Mar", 275.00));
        series.getData().add(new LineChart.Data<>("Apr", 641.49));
        series.getData().add(new LineChart.Data<>("May", 0));
        monthlyExpenseChart.getData().add(series);
    }

    @FXML
    private void handleAddExpense() {
        System.out.println("Add Expense button clicked");
    }

    @FXML
    private void handleAddAppointment() {
        System.out.println("Add Appointment button clicked");
    }

    @FXML
    private void handleTakeMedication() {
        MedicationReminder selectedReminder = medicationRemindersTable.getSelectionModel().getSelectedItem();
        if (selectedReminder != null) {
            selectedReminder.markAsTaken();
            remindersHeap.remove();
            medicationRemindersTable.getItems().remove(selectedReminder);
            todayRemindersLabel.setText(String.valueOf(remindersHeap.getSize()));
        }
    }
} 