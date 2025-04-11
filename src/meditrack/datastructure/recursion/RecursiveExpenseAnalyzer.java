package meditrack.datastructure.recursion;

import meditrack.model.MedicalExpense;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class that demonstrates recursive algorithms for medical expense analysis
 */
public class RecursiveExpenseAnalyzer {

    /**
     * Recursively calculates the total expenses in a list
     * @param expenses The list of expenses to analyze
     * @param index The current index in the list
     * @return The total amount of all expenses
     */
    public static double calculateTotalExpensesRecursive(List<MedicalExpense> expenses, int index) {
        // Base case: if we've reached the end of the list, return 0
        if (index >= expenses.size()) {
            return 0;
        }
        
        // Recursive case: add the current expense to the total of the rest
        return expenses.get(index).getAmount() + 
               calculateTotalExpensesRecursive(expenses, index + 1);
    }
    
    /**
     * Wrapper method for the recursive total calculation
     * @param expenses The list of expenses to analyze
     * @return The total amount of all expenses
     */
    public static double calculateTotalExpenses(List<MedicalExpense> expenses) {
        return calculateTotalExpensesRecursive(expenses, 0);
    }
    
    /**
     * Recursively calculates the total expenses by category
     * @param expenses The list of expenses to analyze
     * @param index The current index in the list
     * @param categoryTotals Map to store category totals
     * @return The map of category totals
     */
    public static Map<MedicalExpense.ExpenseCategory, Double> 
            calculateExpensesByCategoryRecursive(
                List<MedicalExpense> expenses, 
                int index, 
                Map<MedicalExpense.ExpenseCategory, Double> categoryTotals) {
                
        // Base case: if we've reached the end of the list, return the map
        if (index >= expenses.size()) {
            return categoryTotals;
        }
        
        // Get the current expense
        MedicalExpense expense = expenses.get(index);
        MedicalExpense.ExpenseCategory category = expense.getCategory();
        
        // Update the category total
        double currentTotal = categoryTotals.getOrDefault(category, 0.0);
        categoryTotals.put(category, currentTotal + expense.getAmount());
        
        // Recursive call to process the rest of the list
        return calculateExpensesByCategoryRecursive(expenses, index + 1, categoryTotals);
    }
    
    /**
     * Wrapper method for the recursive category calculation
     * @param expenses The list of expenses to analyze
     * @return The map of category totals
     */
    public static Map<MedicalExpense.ExpenseCategory, Double> 
            calculateExpensesByCategory(List<MedicalExpense> expenses) {
        return calculateExpensesByCategoryRecursive(
            expenses, 0, new HashMap<>());
    }
    
    /**
     * Recursively finds expenses within a date range
     * @param expenses The list of expenses to search
     * @param startDate The start of the date range
     * @param endDate The end of the date range
     * @param index The current index in the list
     * @param result The list to store matching expenses
     * @return A list of expenses within the date range
     */
    public static List<MedicalExpense> findExpensesInDateRangeRecursive(
            List<MedicalExpense> expenses,
            LocalDate startDate,
            LocalDate endDate,
            int index,
            List<MedicalExpense> result) {
            
        // Base case: if we've reached the end of the list, return the result
        if (index >= expenses.size()) {
            return result;
        }
        
        // Get the current expense
        MedicalExpense expense = expenses.get(index);
        LocalDate expenseDate = expense.getDate();
        
        // If the expense is within the date range, add it to the result
        if ((expenseDate.isEqual(startDate) || expenseDate.isAfter(startDate)) &&
            (expenseDate.isEqual(endDate) || expenseDate.isBefore(endDate))) {
            result.add(expense);
        }
        
        // Recursive call to process the rest of the list
        return findExpensesInDateRangeRecursive(expenses, startDate, endDate, index + 1, result);
    }
    
    /**
     * Wrapper method for finding expenses in a date range
     * @param expenses The list of expenses to search
     * @param startDate The start of the date range
     * @param endDate The end of the date range
     * @return A list of expenses within the date range
     */
    public static List<MedicalExpense> findExpensesInDateRange(
            List<MedicalExpense> expenses,
            LocalDate startDate,
            LocalDate endDate) {
        return findExpensesInDateRangeRecursive(
            expenses, startDate, endDate, 0, new ArrayList<>());
    }
    
    /**
     * Recursively projects future expenses based on historical data
     * This is a simple projection that assumes the same spending pattern will continue
     * @param expenses Historical expenses
     * @param monthsToProject Number of months to project into the future
     * @param monthIndex Current month index in the projection
     * @param averageMonthlyExpense The average monthly expense
     * @param projections List to store the projections
     * @return List of projected expenses
     */
    public static List<Double> projectFutureExpensesRecursive(
            List<MedicalExpense> expenses,
            int monthsToProject,
            int monthIndex,
            double averageMonthlyExpense,
            List<Double> projections) {
            
        // Base case: if we've projected all requested months, return the projections
        if (monthIndex >= monthsToProject) {
            return projections;
        }
        
        // Calculate the projected expense for this month
        // This could be more sophisticated in a real application
        // For simplicity, we're just using the average plus some random variation
        double randomFactor = 0.9 + Math.random() * 0.2; // Random factor between 0.9 and 1.1
        double projectedExpense = averageMonthlyExpense * randomFactor;
        
        // Add the projection to the list
        projections.add(projectedExpense);
        
        // Recursive call to project the next month
        return projectFutureExpensesRecursive(
            expenses, monthsToProject, monthIndex + 1, averageMonthlyExpense, projections);
    }
    
    /**
     * Wrapper method for projecting future expenses
     * @param expenses Historical expenses
     * @param monthsToProject Number of months to project
     * @return List of projected monthly expenses
     */
    public static List<Double> projectFutureExpenses(
            List<MedicalExpense> expenses, int monthsToProject) {
        // Calculate the average monthly expense
        double totalExpense = calculateTotalExpenses(expenses);
        // Assuming the expenses cover a full year
        double averageMonthlyExpense = totalExpense / 12;
        
        return projectFutureExpensesRecursive(
            expenses, monthsToProject, 0, averageMonthlyExpense, new ArrayList<>());
    }
}