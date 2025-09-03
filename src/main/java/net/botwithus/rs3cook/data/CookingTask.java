package net.botwithus.rs3cook.data;

/**
 * Represents a cooking task with a specific fish type and target quantity
 */
public class CookingTask {
    private String fishName;
    private int targetQuantity;
    private int cookedCount;
    private boolean completed;
    
    public CookingTask(String fishName, int targetQuantity) {
        this.fishName = fishName;
        this.targetQuantity = targetQuantity;
        this.cookedCount = 0;
        this.completed = false;
    }
    
    // Getters
    public String getFishName() { return fishName; }
    public int getTargetQuantity() { return targetQuantity; }
    public int getCookedCount() { return cookedCount; }
    public int getRemainingCount() { return targetQuantity - cookedCount; }
    public boolean isCompleted() { return completed || cookedCount >= targetQuantity; }
    
    // Progress tracking
    public void incrementCooked(int amount) {
        cookedCount += amount;
        if (cookedCount >= targetQuantity) {
            completed = true;
        }
    }
    
    public float getProgressPercent() {
        if (targetQuantity <= 0) return 0.0f;
        return Math.min(1.0f, (float)cookedCount / targetQuantity);
    }
    
    // Setters
    public void setTargetQuantity(int quantity) { 
        this.targetQuantity = quantity;
        // Recheck completion status
        if (cookedCount >= targetQuantity) {
            completed = true;
        }
    }
    
    public void reset() { 
        cookedCount = 0; 
        completed = false; 
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    @Override
    public String toString() {
        return fishName + " (" + cookedCount + "/" + targetQuantity + ")";
    }
    
    public String getStatusString() {
        if (isCompleted()) {
            return "✓ COMPLETED";
        } else {
            return "○ " + getRemainingCount() + " remaining";
        }
    }
}
