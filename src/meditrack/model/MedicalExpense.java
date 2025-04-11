package meditrack.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a medical expense in the MediTrack system
 */
public class MedicalExpense {
    private String id;
    private String description;
    private double amount;
    private LocalDate date;
    private ExpenseCategory category;
    private String provider;
    private boolean reimbursed;
    private double reimbursedAmount;
    
    /**
     * Enum representing different categories of medical expenses
     */
    public enum ExpenseCategory {
        CONSULTATION("Consultation"),
        MEDICATION("Medication"),
        LABORATORY("Laboratory Tests"),
        IMAGING("Imaging/Diagnostics"),
        PROCEDURE("Medical Procedure"),
        THERAPY("Therapy"),
        HOSPITALIZATION("Hospitalization"),
        EQUIPMENT("Medical Equipment"),
        INSURANCE("Insurance Premium"),
        OTHER("Other Expenses");
        
        private final String displayName;
        
        ExpenseCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Creates a new MedicalExpense with the specified details
     */
    public MedicalExpense(String description, double amount, LocalDate date,
                         ExpenseCategory category, String provider) {
        this.id = generateId();
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.provider = provider;
        this.reimbursed = false;
        this.reimbursedAmount = 0.0;
    }
    
    /**
     * Generates a unique ID for the expense based on timestamp
     */
    private String generateId() {
        return "EXP-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }

    // Getters and setters
    public String getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public ExpenseCategory getCategory() {
        return category;
    }
    
    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public boolean isReimbursed() {
        return reimbursed;
    }
    
    public void setReimbursed(boolean reimbursed) {
        this.reimbursed = reimbursed;
    }
    
    public double getReimbursedAmount() {
        return reimbursedAmount;
    }
    
    public void setReimbursedAmount(double reimbursedAmount) {
        this.reimbursedAmount = reimbursedAmount;
        if (reimbursedAmount > 0) {
            this.reimbursed = true;
        } else {
            this.reimbursed = false;
        }
    }
    
    /**
     * Returns the out-of-pocket cost (amount - reimbursed amount)
     */
    public double getOutOfPocketCost() {
        return amount - reimbursedAmount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalExpense expense = (MedicalExpense) o;
        return Objects.equals(id, expense.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s - $%.2f - %s - %s", 
                            description, amount, date, category.getDisplayName());
    }
}