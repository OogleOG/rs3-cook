package net.botwithus.rs3cook.statistics;

import net.botwithus.rs3cook.data.FishData;
import net.botwithus.rs3cook.data.CookingUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Comprehensive statistics tracking for the cooking script
 */
public class StatisticsManager {
    
    // Session tracking
    private long sessionStartTime;
    private long sessionEndTime = 0;
    private boolean sessionActive = true;
    
    // Experience tracking
    private long startExperience = 0;
    private long currentExperience = 0;
    private long experienceGained = 0;
    private int startLevel = 1;
    private int currentLevel = 1;
    private int levelsGained = 0;
    
    // Fish statistics
    private Map<String, FishStatistics> fishStats = new ConcurrentHashMap<>();
    private int totalFishCooked = 0;
    private int totalFishBurned = 0;
    private int totalFishAttempted = 0;
    
    // Action tracking
    private int bankingTrips = 0;
    private int walkingActions = 0;
    private int cookingActions = 0;
    private long totalBankingTime = 0;
    private long totalWalkingTime = 0;
    private long totalCookingTime = 0;
    
    // Efficiency metrics
    private List<Long> xpPerHourHistory = new ArrayList<>();
    private List<Double> successRateHistory = new ArrayList<>();
    private long lastXpCalculationTime = 0;
    
    // Profit tracking
    private long totalProfit = 0;
    private Map<String, Integer> itemPrices = new HashMap<>();
    
    public StatisticsManager() {
        this.sessionStartTime = System.currentTimeMillis();
        this.lastXpCalculationTime = sessionStartTime;
    }
    
    /**
     * Record a fish cooking attempt
     */
    public void recordFishAttempt(FishData fish, boolean successful, boolean burned) {
        String fishName = fish.getRawName();
        
        // Get or create fish statistics
        FishStatistics stats = fishStats.computeIfAbsent(fishName, k -> new FishStatistics(fish));
        
        // Update fish-specific stats
        stats.attempts++;
        if (successful) {
            stats.cooked++;
            totalFishCooked++;
            experienceGained += fish.getExperience();
        }
        if (burned) {
            stats.burned++;
            totalFishBurned++;
        }
        
        // Update totals
        totalFishAttempted++;
        
        // Update success rate history
        updateSuccessRateHistory();
        
        System.out.println("Fish attempt recorded: " + fishName + " (Success: " + successful + ", Burned: " + burned + ")");
    }
    
    /**
     * Record a banking trip
     */
    public void recordBankingTrip(long duration) {
        bankingTrips++;
        totalBankingTime += duration;
        System.out.println("Banking trip recorded: " + (duration / 1000) + " seconds");
    }
    
    /**
     * Record a walking action
     */
    public void recordWalkingAction(long duration) {
        walkingActions++;
        totalWalkingTime += duration;
    }
    
    /**
     * Record a cooking session
     */
    public void recordCookingSession(long duration) {
        cookingActions++;
        totalCookingTime += duration;
    }
    
    /**
     * Update experience values
     */
    public void updateExperience(long currentExp, int currentLvl) {
        if (startExperience == 0) {
            startExperience = currentExp;
            startLevel = currentLvl;
        }
        
        this.currentExperience = currentExp;
        this.experienceGained = currentExp - startExperience;
        
        if (currentLvl > this.currentLevel) {
            levelsGained = currentLvl - startLevel;
            System.out.println("Level up detected! New level: " + currentLvl);
        }
        this.currentLevel = currentLvl;
        
        // Update XP/hour history
        updateXpPerHourHistory();
    }
    
    /**
     * Update XP per hour history
     */
    private void updateXpPerHourHistory() {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastXpCalculationTime;
        
        // Update every 5 minutes
        if (timeDiff >= 300000) {
            long xpPerHour = calculateCurrentXpPerHour();
            xpPerHourHistory.add(xpPerHour);
            
            // Keep only last 12 entries (1 hour of data)
            if (xpPerHourHistory.size() > 12) {
                xpPerHourHistory.remove(0);
            }
            
            lastXpCalculationTime = currentTime;
        }
    }
    
    /**
     * Update success rate history
     */
    private void updateSuccessRateHistory() {
        if (totalFishAttempted > 0) {
            double successRate = (double) totalFishCooked / totalFishAttempted;
            successRateHistory.add(successRate);
            
            // Keep only last 20 entries
            if (successRateHistory.size() > 20) {
                successRateHistory.remove(0);
            }
        }
    }
    
    /**
     * Calculate current XP per hour
     */
    public long calculateCurrentXpPerHour() {
        long sessionTime = getSessionDuration();
        if (sessionTime <= 0) return 0;
        
        return (experienceGained * 3600000) / sessionTime;
    }
    
    /**
     * Calculate fish per hour
     */
    public long calculateFishPerHour() {
        long sessionTime = getSessionDuration();
        if (sessionTime <= 0) return 0;
        
        return (totalFishCooked * 3600000) / sessionTime;
    }
    
    /**
     * Calculate average XP per hour
     */
    public long calculateAverageXpPerHour() {
        if (xpPerHourHistory.isEmpty()) {
            return calculateCurrentXpPerHour();
        }
        
        return (long) xpPerHourHistory.stream().mapToLong(Long::longValue).average().orElse(0);
    }
    
    /**
     * Calculate success rate
     */
    public double calculateSuccessRate() {
        if (totalFishAttempted == 0) return 0.0;
        return (double) totalFishCooked / totalFishAttempted;
    }
    
    /**
     * Calculate burn rate
     */
    public double calculateBurnRate() {
        if (totalFishAttempted == 0) return 0.0;
        return (double) totalFishBurned / totalFishAttempted;
    }
    
    /**
     * Get session duration in milliseconds
     */
    public long getSessionDuration() {
        long endTime = sessionActive ? System.currentTimeMillis() : sessionEndTime;
        return endTime - sessionStartTime;
    }
    
    /**
     * Get formatted session time
     */
    public String getFormattedSessionTime() {
        return CookingUtils.formatTime(getSessionDuration());
    }
    
    /**
     * End the current session
     */
    public void endSession() {
        sessionActive = false;
        sessionEndTime = System.currentTimeMillis();
        System.out.println("Session ended. Duration: " + getFormattedSessionTime());
    }
    
    /**
     * Get comprehensive statistics report
     */
    public StatisticsReport generateReport() {
        return new StatisticsReport(
            getSessionDuration(),
            experienceGained,
            calculateCurrentXpPerHour(),
            calculateAverageXpPerHour(),
            totalFishCooked,
            totalFishBurned,
            totalFishAttempted,
            calculateSuccessRate(),
            calculateBurnRate(),
            bankingTrips,
            levelsGained,
            new HashMap<>(fishStats),
            totalProfit
        );
    }
    
    /**
     * Reset all statistics
     */
    public void resetStatistics() {
        sessionStartTime = System.currentTimeMillis();
        sessionEndTime = 0;
        sessionActive = true;
        
        startExperience = currentExperience;
        experienceGained = 0;
        startLevel = currentLevel;
        levelsGained = 0;
        
        fishStats.clear();
        totalFishCooked = 0;
        totalFishBurned = 0;
        totalFishAttempted = 0;
        
        bankingTrips = 0;
        walkingActions = 0;
        cookingActions = 0;
        totalBankingTime = 0;
        totalWalkingTime = 0;
        totalCookingTime = 0;
        
        xpPerHourHistory.clear();
        successRateHistory.clear();
        lastXpCalculationTime = sessionStartTime;
        
        totalProfit = 0;
        
        System.out.println("Statistics reset");
    }
    
    // Getters
    public long getSessionStartTime() { return sessionStartTime; }
    public long getExperienceGained() { return experienceGained; }
    public int getTotalFishCooked() { return totalFishCooked; }
    public int getTotalFishBurned() { return totalFishBurned; }
    public int getTotalFishAttempted() { return totalFishAttempted; }
    public int getBankingTrips() { return bankingTrips; }
    public int getLevelsGained() { return levelsGained; }
    public int getCurrentLevel() { return currentLevel; }
    public Map<String, FishStatistics> getFishStats() { return new HashMap<>(fishStats); }
    public boolean isSessionActive() { return sessionActive; }
    
    /**
     * Individual fish statistics
     */
    public static class FishStatistics {
        public final FishData fish;
        public int attempts = 0;
        public int cooked = 0;
        public int burned = 0;
        public long totalExperience = 0;
        
        public FishStatistics(FishData fish) {
            this.fish = fish;
        }
        
        public double getSuccessRate() {
            return attempts > 0 ? (double) cooked / attempts : 0.0;
        }
        
        public double getBurnRate() {
            return attempts > 0 ? (double) burned / attempts : 0.0;
        }
    }
    
    /**
     * Comprehensive statistics report
     */
    public static class StatisticsReport {
        public final long sessionDuration;
        public final long experienceGained;
        public final long currentXpPerHour;
        public final long averageXpPerHour;
        public final int fishCooked;
        public final int fishBurned;
        public final int fishAttempted;
        public final double successRate;
        public final double burnRate;
        public final int bankingTrips;
        public final int levelsGained;
        public final Map<String, FishStatistics> fishStats;
        public final long totalProfit;
        
        public StatisticsReport(long sessionDuration, long experienceGained, long currentXpPerHour,
                              long averageXpPerHour, int fishCooked, int fishBurned, int fishAttempted,
                              double successRate, double burnRate, int bankingTrips, int levelsGained,
                              Map<String, FishStatistics> fishStats, long totalProfit) {
            this.sessionDuration = sessionDuration;
            this.experienceGained = experienceGained;
            this.currentXpPerHour = currentXpPerHour;
            this.averageXpPerHour = averageXpPerHour;
            this.fishCooked = fishCooked;
            this.fishBurned = fishBurned;
            this.fishAttempted = fishAttempted;
            this.successRate = successRate;
            this.burnRate = burnRate;
            this.bankingTrips = bankingTrips;
            this.levelsGained = levelsGained;
            this.fishStats = fishStats;
            this.totalProfit = totalProfit;
        }
    }
}
