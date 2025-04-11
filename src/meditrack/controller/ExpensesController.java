package meditrack.controller;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import meditrack.model.MedicalExpense;
import meditrack.model.MedicalExpense.ExpenseCategory;
import meditrack.datastructure.bag.BagInterface;
import meditrack.datastructure.bag.ResizableArrayBag;
import meditrack.datastructure.sort.BubbleSort;
import meditrack.datastructure.sort.QuickSort;
import meditrack.datastructure.sort.SortingAlgorithm;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Controller for the expenses view
 * Demonstrates both Bag data structures and Sorting algorithms
 */
public class ExpensesController implements Initializable {

    // Search and filter components
    @FXML private TextField searchField;
    @FXML private ComboBox<ExpenseCategory> categoryFilter;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    // Sorting components
    @FXML private ComboBox<String> sortField;
    @FXML private ComboBox<String> sortAlgorithm;
    
    // Expense table and columns
    @FXML private TableView<MedicalExpense> expensesTable;
    @FXML private TableColumn<MedicalExpense, String> dateColumn;
    @FXML private TableColumn<MedicalExpense, String> descriptionColumn;
    @FXML private TableColumn<MedicalExpense, String> categoryColumn;
    @FXML private TableColumn<MedicalExpense, String> providerColumn;
    @FXML private TableColumn<MedicalExpense, Double> amountColumn;
    @FXML private TableColumn<MedicalExpense, Double> reimbursedColumn;
    @FXML private TableColumn<MedicalExpense, Double> outOfPocketColumn;
    
    // Summary labels
    @FXML private Label totalLabel;
    @FXML private Label totalReimbursedLabel;
    @FXML private Label totalOutOfPocketLabel;
    
    // Performance metrics labels
    @FXML private Label sortTimeLabel;
    @FXML private Label comparisonsLabel;
    @FXML private Label swapsLabel;
    
    // Data structures
    private BagInterface<MedicalExpense> expensesBag;
    private ObservableList<MedicalExpense> expensesList;
    private FilteredList<MedicalExpense> filteredExpenses;
    
    // Available sorting algorithms
    private Map<String, SortingAlgorithm<ComparableExpense>> sortingAlgorithms;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize data structures
        expensesBag = new ResizableArrayBag<>();
        createSampleData();
        
        // Initialize sorting algorithms
        sortingAlgorithms = new HashMap<>();
        sortingAlgorithms.put("Bubble Sort", new BubbleSort<>());
        sortingAlgorithms.put("Quick Sort", new QuickSort<>());
        
        // Setup table columns
        setupTableColumns();
        
        // Setup filter controls
        setupFilterControls();
        
        // Setup sorting controls
        setupSortingControls();
        
        // Load data into table
        loadExpensesData();
        
        // Update summary
        updateSummary();
    }
    
    /**
     * Creates sample expense data for demonstration
     */
    private void createSampleData() {
        // Sample expenses from past 3 months
        Random random = new Random(123); // Fixed seed for consistent results
        
        for (int i = 0; i < 10; i++) {
            // Random date within past 90 days
            LocalDate date = LocalDate.now().minusDays(random.nextInt(90));
            
            // Random expense category
            ExpenseCategory[] categories = ExpenseCategory.values();
            ExpenseCategory category = categories[random.nextInt(categories.length)];
            
            // Random amount based on category
            double baseAmount = switch (category) {
                case CONSULTATION -> 75 + random.nextDouble() * 150;
                case MEDICATION -> 15 + random.nextDouble() * 85;
                case LABORATORY -> 50 + random.nextDouble() * 200;
                case IMAGING -> 100 + random.nextDouble() * 500;
                case PROCEDURE -> 200 + random.nextDouble() * 1000;
                case THERAPY -> 50 + random.nextDouble() * 150;
                case HOSPITALIZATION -> 500 + random.nextDouble() * 5000;
                case EQUIPMENT -> 20 + random.nextDouble() * 400;
                case INSURANCE -> 100 + random.nextDouble() * 400;
                default -> 20 + random.nextDouble() * 100;
            };
            
            // Round to 2 decimal places
            double amount = Math.round(baseAmount * 100) / 100.0;
            
            // Create expense description based on category
            String description = "Expense for " + category.getDisplayName();
            
            // Create provider based on category
            String provider = switch (category) {
                case CONSULTATION -> "Dr. " + getRandomName(random);
                case MEDICATION -> getRandomPharmacy(random);
                case LABORATORY -> getRandomLab(random);
                case IMAGING -> getRandomImagingCenter(random);
                case PROCEDURE, HOSPITALIZATION -> getRandomHospital(random);
                case THERAPY -> "Therapist " + getRandomName(random);
                default -> "Provider " + (random.nextInt(10) + 1);
            };
            
            // Create the expense
            MedicalExpense expense = new MedicalExpense(description, amount, date, category, provider);
            
            // Random reimbursement (30% chance of being reimbursed)
            if (random.nextDouble() < 0.3) {
                double reimbursementPercent = 0.5 + random.nextDouble() * 0.4; // 50-90% reimbursement
                double reimbursedAmount = Math.round(amount * reimbursementPercent * 100) / 100.0;
                expense.setReimbursedAmount(reimbursedAmount);
            }
            
            // Add expense to bag
            expensesBag.add(expense);
        }
    }
    
    // Helper methods for generating random data
    private String getRandomName(Random random) {
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Wilson", "Taylor", "Clark"};
        return lastNames[random.nextInt(lastNames.length)];
    }
    
    private String getRandomPharmacy(Random random) {
        String[] pharmacies = {"CVS Pharmacy", "Walgreens", "Rite Aid", "Community Pharmacy", "City Drugs", "MedExpress"};
        return pharmacies[random.nextInt(pharmacies.length)];
    }
    
    private String getRandomLab(Random random) {
        String[] labs = {"Quest Diagnostics", "LabCorp", "BioReference", "Metropolis Labs", "City Medical Lab", "Central Testing"};
        return labs[random.nextInt(labs.length)];
    }
    
    private String getRandomImagingCenter(Random random) {
        String[] centers = {"Advanced Imaging", "City Radiology", "MRI Center", "Diagnostic Imaging", "Metropolis Imaging"};
        return centers[random.nextInt(centers.length)];
    }
    
    private String getRandomHospital(Random random) {
        String[] hospitals = {"Memorial Hospital", "City Medical Center", "University Hospital", "General Hospital", "Community Medical"};
        return hospitals[random.nextInt(hospitals.length)];
    }
    
    /**
     * Sets up the table columns for the expenses table
     */
    private void setupTableColumns() {
        // Setup date column with formatter
        dateColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                new javafx.beans.Observable[]{}));
        
        // Setup other columns
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        categoryColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getCategory().getDisplayName(),
                new javafx.beans.Observable[]{}));
                
        providerColumn.setCellValueFactory(new PropertyValueFactory<>("provider"));
        
        // Setup amount column with currency formatter
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });
        
        // Setup reimbursed column with currency formatter
        reimbursedColumn.setCellValueFactory(new PropertyValueFactory<>("reimbursedAmount"));
        reimbursedColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });
        
        // Setup out-of-pocket column
        outOfPocketColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getOutOfPocketCost(),
                new javafx.beans.Observable[]{}));
                
        outOfPocketColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });
    }
    
    /**
     * Sets up the filter controls
     */
    private void setupFilterControls() {
        // Setup category filter
        categoryFilter.getItems().add(null); // Add "All Categories" option
        categoryFilter.getItems().addAll(ExpenseCategory.values());
        
        // Custom cell factory for displaying categories
        categoryFilter.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ExpenseCategory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All Categories");
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        
        // Custom string converter for displaying selected value
        categoryFilter.setConverter(new StringConverter<>() {
            @Override
            public String toString(ExpenseCategory category) {
                return category == null ? "All Categories" : category.getDisplayName();
            }
            
            @Override
            public ExpenseCategory fromString(String string) {
                return null; // Not needed for our use case
            }
        });
        
        // Setup date pickers
        startDatePicker.setValue(LocalDate.now().minusMonths(3));
        endDatePicker.setValue(LocalDate.now());
    }
    
    /**
     * Sets up the sorting controls
     */
    private void setupSortingControls() {
        // Setup sort field options
        sortField.getItems().addAll(
            "Date", "Description", "Category", "Provider", "Amount", "Out of Pocket"
        );
        sortField.getSelectionModel().selectFirst();
        
        // Setup sort algorithm options
        sortAlgorithm.getItems().addAll(sortingAlgorithms.keySet());
        sortAlgorithm.getSelectionModel().selectFirst();
    }
    
    /**
     * Loads expenses data from the bag into the table
     */
    private void loadExpensesData() {
        // Convert the bag to an object array
        Object[] objectArray = expensesBag.toArray();

        // Safely cast and collect to list
        List<MedicalExpense> list = new ArrayList<>();
        for (Object obj : objectArray) {
            if (obj instanceof MedicalExpense) {
                list.add((MedicalExpense) obj);
            }
        }

        // Convert to ObservableList and bind to table
        expensesList = FXCollections.observableArrayList(list);
        filteredExpenses = new FilteredList<>(expensesList);
        applyFilters();
        expensesTable.setItems(filteredExpenses);
    }
    
    /**
     * Updates the summary labels
     */
    private void updateSummary() {
        // Calculate totals
        double total = 0;
        double totalReimbursed = 0;
        
        for (MedicalExpense expense : filteredExpenses) {
            total += expense.getAmount();
            totalReimbursed += expense.getReimbursedAmount();
        }
        
        double outOfPocket = total - totalReimbursed;
        
        // Update labels
        totalLabel.setText(String.format("$%.2f", total));
        totalReimbursedLabel.setText(String.format("$%.2f", totalReimbursed));
        totalOutOfPocketLabel.setText(String.format("$%.2f", outOfPocket));
    }
    
    /**
     * Applies the current filters to the filtered list
     */
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        ExpenseCategory category = categoryFilter.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        // Create a predicate based on the filters
        Predicate<MedicalExpense> predicate = expense -> {
            // Search text filter
            boolean matchesSearch = searchText.isEmpty() || 
                                  expense.getDescription().toLowerCase().contains(searchText) ||
                                  expense.getProvider().toLowerCase().contains(searchText);
            
            // Category filter
            boolean matchesCategory = category == null || expense.getCategory() == category;
            
            // Date filter
            boolean matchesDate = (startDate == null || !expense.getDate().isBefore(startDate)) &&
                                 (endDate == null || !expense.getDate().isAfter(endDate));
            
            return matchesSearch && matchesCategory && matchesDate;
        };
        
        // Apply the predicate
        filteredExpenses.setPredicate(predicate);
        
        // Update summary after filtering
        updateSummary();
    }
    
    /**
     * Handles the apply filters button
     */
    @FXML
    private void handleApplyFilters() {
        applyFilters();
    }
    
    /**
     * Handles the add expense button
     */
    /**
     * Handles the add expense button
     */
    /**
     * Handles the add expense button
     */
    @FXML
    private void handleAddExpense() {
        // Create a dialog for adding a new expense
        Dialog<MedicalExpense> dialog = new Dialog<>();
        dialog.setTitle("Add Expense");
        dialog.setHeaderText("Enter expense details");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        
        TextField reimbursedField = new TextField();
        reimbursedField.setPromptText("Reimbursed Amount (optional)");
        
        DatePicker datePicker = new DatePicker(LocalDate.now());
        
        ComboBox<ExpenseCategory> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(ExpenseCategory.values());
        categoryComboBox.setValue(ExpenseCategory.CONSULTATION); // Default value
        
        TextField providerField = new TextField();
        providerField.setPromptText("Provider");
        
        // Add labels and fields to grid
        grid.add(new Label("Description:"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Reimbursed:"), 0, 2);
        grid.add(reimbursedField, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryComboBox, 1, 4);
        grid.add(new Label("Provider:"), 0, 5);
        grid.add(providerField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the description field by default
        Platform.runLater(descriptionField::requestFocus);
        
        // Convert the result to an expense object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String description = descriptionField.getText();
                    double amount = Double.parseDouble(amountField.getText());
                    
                    // Parse reimbursed amount (default to 0 if empty)
                    double reimbursedAmount = 0.0;
                    if (!reimbursedField.getText().isEmpty()) {
                        reimbursedAmount = Double.parseDouble(reimbursedField.getText());
                        // Ensure reimbursed amount doesn't exceed the total amount
                        reimbursedAmount = Math.min(reimbursedAmount, amount);
                    }
                    
                    LocalDate date = datePicker.getValue();
                    ExpenseCategory category = categoryComboBox.getValue();
                    String provider = providerField.getText();
                    
                    // Validate input
                    if (description.isEmpty() || provider.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Validation Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Please fill in all required fields.");
                        alert.showAndWait();
                        return null;
                    }
                    
                    // Create a new expense
                    MedicalExpense expense = new MedicalExpense(description, amount, date, category, provider);
                    
                    // Set reimbursed amount if provided
                    if (reimbursedAmount > 0) {
                        expense.setReimbursedAmount(reimbursedAmount);
                    }
                    
                    return expense;
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter valid numbers for amount and reimbursed amount.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<MedicalExpense> result = dialog.showAndWait();
        
        result.ifPresent(expense -> {
            // Add to bag
            expensesBag.add(expense);
            
            // Add to observable list
            expensesList.add(expense);
            
            // Reapply filters
            applyFilters();
            
            // Update summary
            updateSummary();
            
            System.out.println("Added new expense: " + expense.getDescription() + 
                              ", Amount: $" + expense.getAmount() + 
                              ", Reimbursed: $" + expense.getReimbursedAmount());
        });
    }

    /**
     * Handles the edit expense button
     */
    @FXML
    private void handleEditExpense() {
        // Get the selected expense
        MedicalExpense selectedExpense = expensesTable.getSelectionModel().getSelectedItem();
        
        if (selectedExpense != null) {
            // In a real application, this would open a dialog to edit the expense
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Edit Expense");
            alert.setHeaderText(null);
            alert.setContentText("This would open a form to edit the selected expense.");
            alert.showAndWait();
            
            System.out.println("Edit Expense button clicked for: " + selectedExpense.getDescription());
        } else {
            // Show alert if no expense is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an expense to edit.");
            alert.showAndWait();
        }
    }
    
    /**
     * Handles the clear filters button
     */
    @FXML
    private void handleClearFilters() {
        // Clear filters
        searchField.clear();
        categoryFilter.setValue(null);
        startDatePicker.setValue(LocalDate.now().minusMonths(3));
        endDatePicker.setValue(LocalDate.now());
        
        // Apply cleared filters
        applyFilters();
    }
    
    /**
     * A comparable wrapper for MedicalExpense that allows sorting
     */
    static class ComparableExpense extends MedicalExpense implements Comparable<ComparableExpense> {
        private final Comparator<MedicalExpense> comparator;
        
        public ComparableExpense(MedicalExpense expense, Comparator<MedicalExpense> comparator) {
            super(expense.getDescription(), expense.getAmount(), expense.getDate(), 
                 expense.getCategory(), expense.getProvider());
            this.comparator = comparator;
            
            // Copy all remaining fields from the original expense
            this.setReimbursedAmount(expense.getReimbursedAmount());
        }
        
        @Override
        public int compareTo(ComparableExpense other) {
            return comparator.compare(this, other);
        }
    }
    
    /**
     * Handles the sort button
     */
    @FXML
    private void handleSort() {
        // Get the selected sorting algorithm
        String algorithmName = sortAlgorithm.getValue();
        SortingAlgorithm<ComparableExpense> algorithm = sortingAlgorithms.get(algorithmName);
        
        if (algorithm == null) {
            return;
        }
        
        // Get the current filtered expenses
        MedicalExpense[] expensesToSort = filteredExpenses.toArray(new MedicalExpense[0]);
        
        // Get the selected sort field
        String field = sortField.getValue();
        
        // Create a comparator based on the selected field
        Comparator<MedicalExpense> comparator = switch (field) {
            case "Date" -> Comparator.comparing(MedicalExpense::getDate);
            case "Description" -> Comparator.comparing(MedicalExpense::getDescription);
            case "Category" -> Comparator.comparing(e -> e.getCategory().getDisplayName());
            case "Provider" -> Comparator.comparing(MedicalExpense::getProvider);
            case "Amount" -> Comparator.comparing(MedicalExpense::getAmount);
            case "Out of Pocket" -> Comparator.comparing(MedicalExpense::getOutOfPocketCost);
            default -> Comparator.comparing(MedicalExpense::getDate);
        };
        
        // Convert expenses to comparable expenses
        ComparableExpense[] comparableExpenses = new ComparableExpense[expensesToSort.length];
        for (int i = 0; i < expensesToSort.length; i++) {
            comparableExpenses[i] = new ComparableExpense(expensesToSort[i], comparator);
        }
        
        // Reset performance counters
        algorithm.resetCounters();
        
        // Sort the expenses
        long startTime = System.nanoTime();
        algorithm.sort(comparableExpenses);
        long endTime = System.nanoTime();
        
        // Calculate sort time in milliseconds
        double sortTimeMs = (endTime - startTime) / 1e6;
        
        // Update performance labels
        sortTimeLabel.setText(String.format("%.2f ms", sortTimeMs));
        comparisonsLabel.setText(String.format("%d", algorithm.getComparisonCount()));
        swapsLabel.setText(String.format("%d", algorithm.getSwapCount()));
        
        // Convert back to regular expenses
        MedicalExpense[] sortedExpenses = new MedicalExpense[comparableExpenses.length];
        for (int i = 0; i < comparableExpenses.length; i++) {
            sortedExpenses[i] = comparableExpenses[i];
        }
        
        // Update the table with sorted expenses
        expensesList.clear();
        expensesList.addAll(sortedExpenses);
        
        // Reapply filters
        applyFilters();
    }
    
    /**
     * Handles the add expense button
     */
    @FXML
    private void handleDeleteExpense() {
        // Get the selected expense directly
        MedicalExpense selectedExpense = expensesTable.getSelectionModel().getSelectedItem();
        
        if (selectedExpense != null) {
            // Print details for debugging
            System.out.println("\nATTEMPTING TO DELETE:");
            System.out.println("ID: " + selectedExpense.getId());
            System.out.println("Description: " + selectedExpense.getDescription());
            
            // Confirm deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this expense?\n\n" + 
                selectedExpense.getDescription() + " - " + String.format("$%.2f", selectedExpense.getAmount()));
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Step 1: Create a brand new data model from scratch
                refreshDataModel(selectedExpense);
                
                // Step 2: Update the table and UI
                updateSummary();
            }
        } else {
            // Show alert if no expense is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an expense to delete.");
            alert.showAndWait();
        }
    }

    /**
     * Recreates the entire data model, excluding the specified expense
     * @param expenseToExclude The expense to exclude from the new data model
     */
    private void refreshDataModel(MedicalExpense expenseToExclude) {
        // Track how many expenses we have before and how many we'll have after
        int startingCount = 0;
        int endingCount = 0;
        
        // Dump all the expenses from the bag into a temporary list
        List<MedicalExpense> tempList = new ArrayList<>();
        Object[] allExpenses = expensesBag.toArray();
        
        // Count the starting total
        startingCount = allExpenses.length;
        
        System.out.println("Starting expense count: " + startingCount);
        System.out.println("Expense ID to exclude: " + expenseToExclude.getId());
        
        // Add all expenses EXCEPT the one we're excluding
        for (Object obj : allExpenses) {
            if (obj instanceof MedicalExpense expense) {
                if (!expense.getId().equals(expenseToExclude.getId())) {
                    tempList.add(expense);
                    System.out.println("Keeping expense: " + expense.getId() + " - " + expense.getDescription());
                    endingCount++;
                } else {
                    System.out.println("Excluding expense: " + expense.getId() + " - " + expense.getDescription());
                }
            }
        }
        
        System.out.println("Ending expense count: " + endingCount);
        
        // Create entirely new data structures
        BagInterface<MedicalExpense> newBag = new ResizableArrayBag<>();
        
        // Fill the new bag
        for (MedicalExpense expense : tempList) {
            newBag.add(expense);
        }
        
        // Create a new ObservableList
        ObservableList<MedicalExpense> newObservableList = FXCollections.observableArrayList(tempList);
        
        // Update references
        expensesBag = newBag;
        expensesList = newObservableList;
        
        // Create new filtered list and bind to table
        filteredExpenses = new FilteredList<>(expensesList);
        expensesTable.setItems(filteredExpenses);
        
        System.out.println("Refresh complete. Observable list size: " + expensesList.size() + ", Bag size: " + expensesBag.getCurrentSize());
    }
    /**
     * Helper method to print current table contents for debugging
     */
    private void printCurrentTableContents() {
        System.out.println("Current table contents (" + expensesList.size() + " items):");
        for (int i = 0; i < expensesList.size(); i++) {
            MedicalExpense expense = expensesList.get(i);
            System.out.println(i + ": " + expense.getDescription() + " - $" + 
                             expense.getAmount() + " - " + expense.getDate());
        }
        System.out.println();
    }
    /**
     * Rebuilds the bag from the current list
     */
    private void rebuildBagFromList() {
        // Print current expenses list size
        System.out.println("Rebuilding bag from expenses list, size: " + expensesList.size());
        
        // Create a new empty bag
        BagInterface<MedicalExpense> newBag = new ResizableArrayBag<>();
        
        // Add all expenses from the observable list to the new bag
        for (MedicalExpense expense : expensesList) {
            boolean added = newBag.add(expense);
            System.out.println("Added to new bag: " + expense.getDescription() + " - $" + 
                             expense.getAmount() + " - Success: " + added);
        }
        
        // Replace the old bag with the new one
        expensesBag = newBag;
        
        System.out.println("Bag rebuilt, new size: " + expensesBag.getCurrentSize());
    }
}