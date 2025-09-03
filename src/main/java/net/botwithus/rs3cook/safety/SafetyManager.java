package net.botwithus.rs3cook.safety;

import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3cook.config.CookingConfig;

import java.util.Random;

/**
 * Manages safety features and anti-detection measures for the cooking script
 */
public class SafetyManager {
    
    private static final Random random = new Random();
    private CookingConfig config;
    
    // Break system
    private long lastBreakTime = 0;
    private long nextBreakTime = 0;
    private boolean isOnBreak = false;
    private long breakStartTime = 0;
    private long breakDuration = 0;
    
    // Session limits
    private long sessionStartTime;
    private long maxSessionTime = 0; // 0 = unlimited
    private long targetExperience = 0; // 0 = unlimited
    private long startExperience = 0;
    
    // Anti-pattern detection
    private long lastActionTime = 0;
    private int consecutiveFailures = 0;
    private static final int MAX_CONSECUTIVE_FAILURES = 5;
    private static final long MAX_IDLE_TIME = 300000; // 5 minutes
    
    // Emergency stop conditions
    private boolean emergencyStopTriggered = false;
    private String emergencyStopReason = "";
    
    public SafetyManager(CookingConfig config) {
        this.config = config;
        this.sessionStartTime = System.currentTimeMillis();
        calculateNextBreakTime();
    }
    
    /**
     * Check if it's time for a break
     */
    public boolean isTimeForBreak() {
        if (!config.isUseBreaks()) return false;
        if (isOnBreak) return true;
        
        long currentTime = System.currentTimeMillis();
        return currentTime >= nextBreakTime;
    }
    
    /**
     * Start a break
     */
    public void startBreak() {
        if (isOnBreak) return;
        
        isOnBreak = true;
        breakStartTime = System.currentTimeMillis();
        
        // Calculate break duration
        int minBreak = config.getMinBreakTime() * 1000; // Convert to milliseconds
        int maxBreak = config.getMaxBreakTime() * 1000;
        breakDuration = random.nextInt(maxBreak - minBreak + 1) + minBreak;
        
        System.out.println("Starting break for " + (breakDuration / 1000) + " seconds");
    }
    
    /**
     * Check if break is complete
     */
    public boolean isBreakComplete() {
        if (!isOnBreak) return true;
        
        long currentTime = System.currentTimeMillis();
        boolean breakComplete = (currentTime - breakStartTime) >= breakDuration;
        
        if (breakComplete) {
            endBreak();
        }
        
        return breakComplete;
    }
    
    /**
     * End the current break
     */
    private void endBreak() {
        isOnBreak = false;
        lastBreakTime = System.currentTimeMillis();
        calculateNextBreakTime();
        System.out.println("Break completed, resuming cooking");
    }
    
    /**
     * Calculate when the next break should occur
     */
    private void calculateNextBreakTime() {
        if (!config.isUseBreaks()) return;
        
        long currentTime = System.currentTimeMillis();
        int minCookingTime = config.getMinCookingTime() * 1000; // Convert to milliseconds
        int maxCookingTime = config.getMaxCookingTime() * 1000;
        
        long cookingDuration = random.nextInt(maxCookingTime - minCookingTime + 1) + minCookingTime;
        nextBreakTime = currentTime + cookingDuration;
        
        System.out.println("Next break scheduled in " + (cookingDuration / 1000) + " seconds");
    }
    
    /**
     * Check for emergency stop conditions
     */
    public boolean shouldEmergencyStop() {
        if (emergencyStopTriggered) return true;
        
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) {
            triggerEmergencyStop("Player is null");
            return true;
        }
        
        // Check if player is in combat
        if (player.getTarget() != null) {
            triggerEmergencyStop("Player is in combat");
            return true;
        }
        
        // Check session time limits
        if (maxSessionTime > 0) {
            long sessionTime = System.currentTimeMillis() - sessionStartTime;
            if (sessionTime >= maxSessionTime) {
                triggerEmergencyStop("Maximum session time reached");
                return true;
            }
        }
        
        // Check experience limits
        if (targetExperience > 0 && startExperience > 0) {
            // This would need actual experience checking
            // For now, just a placeholder
        }
        
        // Check for excessive idle time
        long timeSinceLastAction = System.currentTimeMillis() - lastActionTime;
        if (timeSinceLastAction > MAX_IDLE_TIME) {
            triggerEmergencyStop("Script has been idle for too long");
            return true;
        }
        
        // Check for too many consecutive failures
        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            triggerEmergencyStop("Too many consecutive failures");
            return true;
        }
        
        return false;
    }
    
    /**
     * Trigger an emergency stop
     */
    private void triggerEmergencyStop(String reason) {
        emergencyStopTriggered = true;
        emergencyStopReason = reason;
        System.out.println("EMERGENCY STOP: " + reason);
    }
    
    /**
     * Record a successful action
     */
    public void recordSuccessfulAction() {
        lastActionTime = System.currentTimeMillis();
        consecutiveFailures = 0;
    }
    
    /**
     * Record a failed action
     */
    public void recordFailedAction() {
        lastActionTime = System.currentTimeMillis();
        consecutiveFailures++;
        System.out.println("Action failed. Consecutive failures: " + consecutiveFailures);
    }
    
    /**
     * Generate a random delay for human-like behavior
     */
    public long generateRandomDelay() {
        // Base delay between 600-1200ms
        int baseDelay = random.nextInt(600) + 600;
        
        // Add some variation based on recent activity
        if (consecutiveFailures > 0) {
            // Longer delays after failures
            baseDelay += random.nextInt(1000) + 500;
        }
        
        // Occasionally add longer pauses for realism
        if (random.nextInt(20) == 0) { // 5% chance
            baseDelay += random.nextInt(2000) + 1000;
        }
        
        return baseDelay;
    }
    
    /**
     * Generate random mouse movement delay
     */
    public long generateMouseDelay() {
        return random.nextInt(300) + 100; // 100-400ms
    }
    
    /**
     * Generate random typing delay
     */
    public long generateTypingDelay() {
        return random.nextInt(200) + 50; // 50-250ms
    }
    
    /**
     * Check if action should be randomized
     */
    public boolean shouldRandomizeAction() {
        return random.nextInt(10) == 0; // 10% chance to add extra randomization
    }
    
    /**
     * Get random offset for coordinates (anti-pattern)
     */
    public int getRandomOffset() {
        return random.nextInt(5) - 2; // -2 to +2 pixel offset
    }
    
    /**
     * Check if we should take a micro-break (short pause)
     */
    public boolean shouldTakeMicroBreak() {
        return random.nextInt(50) == 0; // 2% chance
    }
    
    /**
     * Generate micro-break duration
     */
    public long generateMicroBreakDuration() {
        return random.nextInt(3000) + 1000; // 1-4 seconds
    }
    
    /**
     * Reset safety manager for new session
     */
    public void resetSession() {
        sessionStartTime = System.currentTimeMillis();
        lastBreakTime = 0;
        nextBreakTime = 0;
        isOnBreak = false;
        consecutiveFailures = 0;
        emergencyStopTriggered = false;
        emergencyStopReason = "";
        calculateNextBreakTime();
    }
    
    /**
     * Set session limits
     */
    public void setSessionLimits(long maxSessionTimeMinutes, long targetExp, long startExp) {
        this.maxSessionTime = maxSessionTimeMinutes * 60000; // Convert to milliseconds
        this.targetExperience = targetExp;
        this.startExperience = startExp;
    }
    
    /**
     * Get remaining session time
     */
    public long getRemainingSessionTime() {
        if (maxSessionTime <= 0) return -1; // Unlimited
        
        long elapsed = System.currentTimeMillis() - sessionStartTime;
        return Math.max(0, maxSessionTime - elapsed);
    }
    
    /**
     * Get time until next break
     */
    public long getTimeUntilNextBreak() {
        if (!config.isUseBreaks() || isOnBreak) return -1;
        
        long currentTime = System.currentTimeMillis();
        return Math.max(0, nextBreakTime - currentTime);
    }
    
    /**
     * Get remaining break time
     */
    public long getRemainingBreakTime() {
        if (!isOnBreak) return 0;
        
        long elapsed = System.currentTimeMillis() - breakStartTime;
        return Math.max(0, breakDuration - elapsed);
    }
    
    // Getters
    public boolean isOnBreak() { return isOnBreak; }
    public boolean isEmergencyStopTriggered() { return emergencyStopTriggered; }
    public String getEmergencyStopReason() { return emergencyStopReason; }
    public int getConsecutiveFailures() { return consecutiveFailures; }
    public long getSessionStartTime() { return sessionStartTime; }
}
