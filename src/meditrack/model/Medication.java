package meditrack.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a medication in the MediTrack system
 */
public class Medication {
    private String id;
    private String name;
    private String dosage;
    private String instructions;
    private String prescriber;
    private LocalDate prescriptionDate;
    private LocalDate refillDate;
    private int refillsRemaining;
    private double cost;
    private boolean isActive;
    
    /**
     * Creates a new Medication with the specified details
     */
    public Medication(String name, String dosage, String instructions, 
                     String prescriber, LocalDate prescriptionDate, 
                     int refillsRemaining, double cost) {
        this.id = generateId();
        this.name = name;
        this.dosage = dosage;
        this.instructions = instructions;
        this.prescriber = prescriber;
        this.prescriptionDate = prescriptionDate;
        this.refillDate = prescriptionDate;
        this.refillsRemaining = refillsRemaining;
        this.cost = cost;
        this.isActive = true;
    }
    
    /**
     * Generates a unique ID for the medication based on timestamp
     */
    private String generateId() {
        return "MED-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
    
    /**
     * Refills the medication if refills are available
     * @return true if the refill was successful, false otherwise
     */
    public boolean refill() {
        if (refillsRemaining > 0) {
            refillsRemaining--;
            refillDate = LocalDate.now();
            return true;
        }
        return false;
    }

    // Getters and setters
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDosage() {
        return dosage;
    }
    
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getPrescriber() {
        return prescriber;
    }
    
    public void setPrescriber(String prescriber) {
        this.prescriber = prescriber;
    }
    
    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }
    
    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
    
    public LocalDate getRefillDate() {
        return refillDate;
    }
    
    public void setRefillDate(LocalDate refillDate) {
        this.refillDate = refillDate;
    }
    
    public int getRefillsRemaining() {
        return refillsRemaining;
    }
    
    public void setRefillsRemaining(int refillsRemaining) {
        this.refillsRemaining = refillsRemaining;
    }
    
    public double getCost() {
        return cost;
    }
    
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    /**
     * Calculates the estimated days until the next refill is needed based on instructions
     * This is a simple implementation - a real system would parse the instructions more carefully
     */
    public int getDaysUntilRefill() {
        // Simple implementation - would need more sophisticated parsing in a real system
        if (instructions.toLowerCase().contains("daily")) {
            return 30;
        } else if (instructions.toLowerCase().contains("twice daily") || 
                  instructions.toLowerCase().contains("2 times")) {
            return 15;
        } else if (instructions.toLowerCase().contains("weekly")) {
            return 90;
        } else {
            return 30; // Default to 30 days
        }
    }
    
    /**
     * Calculates the priority level for refill reminders
     * Higher priority (1-5) means more urgent
     */
    public int getRefillPriority() {
        if (refillsRemaining == 0) {
            return 5; // Highest priority - no refills left
        }
        
        LocalDate today = LocalDate.now();
        LocalDate estimatedRefillDate = refillDate.plusDays(getDaysUntilRefill());
        
        if (today.isAfter(estimatedRefillDate)) {
            return 4; // Overdue for refill
        }
        
        long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, estimatedRefillDate);
        
        if (daysUntil < 3) return 3;
        if (daysUntil < 7) return 2;
        return 1; // Lowest priority
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medication that = (Medication) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s - %s", name, dosage, instructions);
    }
}