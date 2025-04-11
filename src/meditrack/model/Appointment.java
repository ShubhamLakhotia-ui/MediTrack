package meditrack.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a healthcare appointment in the MediTrack system
 */
public class Appointment implements Comparable<Appointment> {
    private String id;
    private LocalDateTime dateTime;
    private String provider;
    private String reason;
    private String providerSpecialty;
    private String location;
    private String notes;
    private boolean reminderSet;
    private int reminderMinutesBefore;
    
    /**
     * Creates a new appointment with the specified details
     * @param dateTime The date and time of the appointment
     * @param provider The healthcare provider's name
     * @param reason The reason for the appointment
     * @param providerSpecialty The specialty of the provider
     */
    public Appointment(LocalDateTime dateTime, String provider, String reason, String providerSpecialty) {
        this.id = generateId();
        this.dateTime = dateTime;
        this.provider = provider;
        this.reason = reason;
        this.providerSpecialty = providerSpecialty;
        this.location = "";
        this.notes = "";
        this.reminderSet = false;
        this.reminderMinutesBefore = 60; // Default: 1 hour before
    }
    
    /**
     * Creates a new appointment with the specified details
     * @param dateTime The date and time of the appointment
     * @param provider The healthcare provider's name
     * @param reason The reason for the appointment
     * @param providerSpecialty The specialty of the provider
     * @param location The location of the appointment
     */
    public Appointment(LocalDateTime dateTime, String provider, String reason, 
                      String providerSpecialty, String location) {
        this(dateTime, provider, reason, providerSpecialty);
        this.location = location;
    }
    
    /**
     * Generates a unique ID for the appointment based on timestamp
     */
    private String generateId() {
        return "APT-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
    
    /**
     * Sets a reminder for this appointment
     * @param minutesBefore The number of minutes before the appointment to send the reminder
     */
    public void setReminder(int minutesBefore) {
        this.reminderSet = true;
        this.reminderMinutesBefore = minutesBefore;
    }
    
    /**
     * Disables the reminder for this appointment
     */
    public void disableReminder() {
        this.reminderSet = false;
    }
    
    /**
     * Calculates how many minutes until the appointment
     * @return minutes until appointment, negative if appointment has passed
     */
    public long minutesUntil() {
        return java.time.Duration.between(
            LocalDateTime.now(), dateTime).toMinutes();
    }
    
    /**
     * Checks if the appointment is upcoming (within 24 hours)
     * @return true if the appointment is within the next 24 hours
     */
    public boolean isUpcoming() {
        long minutes = minutesUntil();
        return minutes > 0 && minutes <= 24 * 60; // 24 hours in minutes
    }
    
    /**
     * Checks if the appointment has passed
     * @return true if the appointment has already passed
     */
    public boolean hasPassed() {
        return minutesUntil() < 0;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getProviderSpecialty() {
        return providerSpecialty;
    }
    
    public void setProviderSpecialty(String providerSpecialty) {
        this.providerSpecialty = providerSpecialty;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public boolean isReminderSet() {
        return reminderSet;
    }
    
    public int getReminderMinutesBefore() {
        return reminderMinutesBefore;
    }
    
    /**
     * Gets the date/time when the reminder should trigger
     * @return The reminder date/time
     */
    public LocalDateTime getReminderDateTime() {
        return dateTime.minusMinutes(reminderMinutesBefore);
    }
    
    @Override
    public int compareTo(Appointment other) {
        // Compare based on date/time
        return this.dateTime.compareTo(other.dateTime);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s - %s", 
                           dateTime.toString(), provider, reason);
    }
}