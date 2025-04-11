package meditrack.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a healthcare provider in the MediTrack system
 */
public class HealthcareProvider {
    private String id;
    private String name;
    private String specialty;
    private String address;
    private String phone;
    private String email;
    private List<Visit> visitHistory;
    private double averageRating;
    private int ratingCount;
    
    /**
     * Represents a visit to a healthcare provider
     */
    public static class Visit {
        private LocalDate date;
        private String reason;
        private double cost;
        private int rating; // 1-5 stars
        private String notes;
        
        public Visit(LocalDate date, String reason, double cost) {
            this.date = date;
            this.reason = reason;
            this.cost = cost;
            this.rating = 0;
            this.notes = "";
        }
        
        // Getters and setters
        public LocalDate getDate() {
            return date;
        }
        
        public void setDate(LocalDate date) {
            this.date = date;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
        
        public double getCost() {
            return cost;
        }
        
        public void setCost(double cost) {
            this.cost = cost;
        }
        
        public int getRating() {
            return rating;
        }
        
        public void setRating(int rating) {
            this.rating = Math.max(0, Math.min(5, rating));
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
        
        @Override
        public String toString() {
            return String.format("%s - %s - $%.2f", date, reason, cost);
        }
    }
    
    /**
     * Creates a new healthcare provider
     * @param name The name of the provider
     * @param specialty The provider's specialty
     * @param address The provider's address
     * @param phone The provider's phone number
     * @param email The provider's email address
     */
    public HealthcareProvider(String name, String specialty, String address, String phone, String email) {
        this.id = generateId();
        this.name = name;
        this.specialty = specialty;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.visitHistory = new ArrayList<>();
        this.averageRating = 0.0;
        this.ratingCount = 0;
    }
    
    /**
     * Generates a unique ID for the provider based on timestamp
     */
    private String generateId() {
        return "PROV-" + System.currentTimeMillis();
    }
    
    /**
     * Adds a visit to this provider's history
     * @param visit The visit to add
     */
    public void addVisit(Visit visit) {
        visitHistory.add(visit);
    }
    
    /**
     * Adds a rating for this provider
     * @param rating The rating (1-5)
     */
    public void addRating(int rating) {
        // Validate rating
        rating = Math.max(1, Math.min(5, rating));
        
        // Update average rating
        double totalRating = averageRating * ratingCount + rating;
        ratingCount++;
        averageRating = totalRating / ratingCount;
    }
    
    /**
     * Calculates the total cost of all visits to this provider
     * @return The total cost
     */
    public double getTotalCost() {
        double total = 0;
        
        for (Visit visit : visitHistory) {
            total += visit.getCost();
        }
        
        return total;
    }
    
    /**
     * Gets the most recent visit to this provider
     * @return The most recent visit, or null if there are no visits
     */
    public Visit getMostRecentVisit() {
        if (visitHistory.isEmpty()) {
            return null;
        }
        
        Visit mostRecent = visitHistory.get(0);
        
        for (Visit visit : visitHistory) {
            if (visit.getDate().isAfter(mostRecent.getDate())) {
                mostRecent = visit;
            }
        }
        
        return mostRecent;
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
    
    public String getSpecialty() {
        return specialty;
    }
    
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<Visit> getVisitHistory() {
        return new ArrayList<>(visitHistory); // Return a copy to prevent modification
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    public int getRatingCount() {
        return ratingCount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthcareProvider provider = (HealthcareProvider) o;
        return Objects.equals(id, provider.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s - Rating: %.1f (%d reviews)", 
                           name, specialty, averageRating, ratingCount);
    }
}