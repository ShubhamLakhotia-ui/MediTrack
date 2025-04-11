package meditrack.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a medication reminder in the MediTrack system
 * Implements Comparable to allow for priority-based sorting
 */
public class MedicationReminder implements Comparable<MedicationReminder> {
    private String id;
    private Medication medication;
    private LocalDateTime dueDateTime;
    private boolean taken;
    private int priority; // 1-5 scale where 5 is highest priority
    
    /**
     * Creates a new medication reminder
     * @param medication The medication for this reminder
     * @param dueDateTime When the medication should be taken
     * @param priority The priority of this reminder (1-5)
     */
    public MedicationReminder(Medication medication, LocalDateTime dueDateTime, int priority) {
        this.id = generateId();
        this.medication = medication;
        this.dueDateTime = dueDateTime;
        this.taken = false;
        this.priority = validatePriority(priority);
    }
    
    /**
     * Validates that the priority is between 1 and 5
     * @param priority The priority to validate
     * @return The validated priority (clamped to 1-5)
     */
    private int validatePriority(int priority) {
        return Math.max(1, Math.min(5, priority));
    }
    
    /**
     * Generates a unique ID for the reminder based on timestamp
     */
    private String generateId() {
        return "REM-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public Medication getMedication() {
        return medication;
    }
    
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }
    
    public void setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
    }
    
    public boolean isTaken() {
        return taken;
    }
    
    public void setTaken(boolean taken) {
        this.taken = taken;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = validatePriority(priority);
    }
    
    /**
     * Marks the reminder as taken
     */
    public void markAsTaken() {
        this.taken = true;
    }
    
    /**
     * Checks if the reminder is overdue
     * @return true if the reminder is overdue, false otherwise
     */
    public boolean isOverdue() {
        return !taken && LocalDateTime.now().isAfter(dueDateTime);
    }
    
    /**
     * Calculates how many minutes until the reminder is due
     * @return minutes until due, negative if overdue
     */
    public long minutesUntilDue() {
        return java.time.Duration.between(
            LocalDateTime.now(), dueDateTime).toMinutes();
    }
    
    /**
     * Calculates the effective priority based on time until due and base priority
     * This increases priority as the due time approaches or passes
     * @return The effective priority value used for sorting
     */
//    public int getEffectivePriority() {
//        if (taken) {
//            return 0; // Lowest priority if already taken
//        }
//        
//        long minutes = minutesUntilDue();
//        
//        if (minutes < 0) {
//            // Overdue - highest priority
//            return priority * 100;
//        } else if (minutes < 30) {
//            // Due within 30 minutes
//            return priority * 50;
//        } else if (minutes < 60) {
//            // Due within an hour
//            return priority * 20;
//        } else if (minutes < 4 * 60) {
//            // Due within 4 hours
//            return priority * 10;
//        } else {
//            // Due later
//            return priority;
//        }
//    }
    public int getEffectivePriority() {
    	   System.out.println("Called getEffectivePriority for " + medication.getName() + 
                   ", Base Priority: " + priority);
        if (taken) {
            return Integer.MAX_VALUE; // Lowest priority if already taken
        }
        
        // Invert the priority so that priority 1 gets highest effective value
        int invertedPriority = 6 - priority; // 1→5, 2→4, 3→3, 4→2, 5→1
        System.out.println("Inverted Priority: " + invertedPriority);
        long minutes = minutesUntilDue();
        
        if (minutes < 0) {
            // Overdue - highest priority
            return invertedPriority * 100;
        } else if (minutes < 30) {
            // Due within 30 minutes
            return invertedPriority * 50;
        } else if (minutes < 60) {
            // Due within an hour
            return invertedPriority * 20;
        } else if (minutes < 4 * 60) {
            // Due within 4 hours
            return invertedPriority * 10;
        } else {
            // Due later
            return invertedPriority;
        }
    }
    
    @Override
    public int compareTo(MedicationReminder other) {
        // Compare based on effective priority (reversed for max heap)
        return Integer.compare(other.getEffectivePriority(), this.getEffectivePriority());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicationReminder that = (MedicationReminder) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s - Due: %s - Priority: %d", 
                           medication.getName(), 
                           dueDateTime.toString(), 
                           priority);
    }
}