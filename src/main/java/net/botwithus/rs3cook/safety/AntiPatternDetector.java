package net.botwithus.rs3cook.safety;

import java.util.*;

/**
 * Detects and prevents repetitive patterns that could indicate botting
 */
public class AntiPatternDetector {
    
    private static final int MAX_PATTERN_HISTORY = 50;
    private static final int PATTERN_THRESHOLD = 5; // Number of identical patterns to trigger warning
    
    // Action tracking
    private List<ActionRecord> actionHistory = new ArrayList<>();
    private Map<String, Integer> actionCounts = new HashMap<>();
    private Map<String, Long> lastActionTimes = new HashMap<>();
    
    // Timing analysis
    private List<Long> timingIntervals = new ArrayList<>();
    private static final int MAX_TIMING_HISTORY = 20;
    private static final double TIMING_VARIANCE_THRESHOLD = 0.1; // 10% variance threshold
    
    // Location tracking
    private List<LocationRecord> locationHistory = new ArrayList<>();
    private static final int MAX_LOCATION_HISTORY = 30;
    
    /**
     * Record an action for pattern analysis
     */
    public void recordAction(String actionType, String details, long timestamp) {
        ActionRecord record = new ActionRecord(actionType, details, timestamp);
        
        // Add to history
        actionHistory.add(record);
        if (actionHistory.size() > MAX_PATTERN_HISTORY) {
            actionHistory.remove(0);
        }
        
        // Update action counts
        actionCounts.put(actionType, actionCounts.getOrDefault(actionType, 0) + 1);
        
        // Record timing intervals
        Long lastTime = lastActionTimes.get(actionType);
        if (lastTime != null) {
            long interval = timestamp - lastTime;
            timingIntervals.add(interval);
            if (timingIntervals.size() > MAX_TIMING_HISTORY) {
                timingIntervals.remove(0);
            }
        }
        lastActionTimes.put(actionType, timestamp);
    }
    
    /**
     * Record a location for movement pattern analysis
     */
    public void recordLocation(int x, int y, long timestamp) {
        LocationRecord record = new LocationRecord(x, y, timestamp);
        
        locationHistory.add(record);
        if (locationHistory.size() > MAX_LOCATION_HISTORY) {
            locationHistory.remove(0);
        }
    }
    
    /**
     * Analyze for suspicious patterns
     */
    public PatternAnalysisResult analyzePatterns() {
        PatternAnalysisResult result = new PatternAnalysisResult();
        
        // Check for repetitive action sequences
        result.hasRepetitiveActions = detectRepetitiveActions();
        
        // Check for too-regular timing
        result.hasSuspiciousTiming = detectSuspiciousTiming();
        
        // Check for repetitive movement patterns
        result.hasRepetitiveMovement = detectRepetitiveMovement();
        
        // Check for excessive action frequency
        result.hasExcessiveFrequency = detectExcessiveFrequency();
        
        // Calculate overall risk score
        result.riskScore = calculateRiskScore(result);
        
        return result;
    }
    
    /**
     * Detect repetitive action sequences
     */
    private boolean detectRepetitiveActions() {
        if (actionHistory.size() < 10) return false;
        
        // Look for identical sequences of 3+ actions
        for (int seqLength = 3; seqLength <= 5; seqLength++) {
            Map<String, Integer> sequenceCounts = new HashMap<>();
            
            for (int i = 0; i <= actionHistory.size() - seqLength; i++) {
                StringBuilder sequence = new StringBuilder();
                for (int j = 0; j < seqLength; j++) {
                    sequence.append(actionHistory.get(i + j).actionType).append("-");
                }
                
                String seqStr = sequence.toString();
                sequenceCounts.put(seqStr, sequenceCounts.getOrDefault(seqStr, 0) + 1);
                
                if (sequenceCounts.get(seqStr) >= PATTERN_THRESHOLD) {
                    System.out.println("Detected repetitive action sequence: " + seqStr);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Detect suspicious timing patterns
     */
    private boolean detectSuspiciousTiming() {
        if (timingIntervals.size() < 10) return false;
        
        // Calculate variance in timing intervals
        double mean = timingIntervals.stream().mapToLong(Long::longValue).average().orElse(0);
        double variance = timingIntervals.stream()
                .mapToDouble(interval -> Math.pow(interval - mean, 2))
                .average().orElse(0);
        double stdDev = Math.sqrt(variance);
        double coefficientOfVariation = stdDev / mean;
        
        // If timing is too regular (low variance), it's suspicious
        if (coefficientOfVariation < TIMING_VARIANCE_THRESHOLD) {
            System.out.println("Detected suspicious timing pattern. CV: " + coefficientOfVariation);
            return true;
        }
        
        return false;
    }
    
    /**
     * Detect repetitive movement patterns
     */
    private boolean detectRepetitiveMovement() {
        if (locationHistory.size() < 15) return false;
        
        // Look for repeated movement sequences
        Map<String, Integer> movementPatterns = new HashMap<>();
        
        for (int i = 0; i <= locationHistory.size() - 3; i++) {
            StringBuilder pattern = new StringBuilder();
            for (int j = 0; j < 3; j++) {
                LocationRecord loc = locationHistory.get(i + j);
                pattern.append(loc.x).append(",").append(loc.y).append("-");
            }
            
            String patternStr = pattern.toString();
            movementPatterns.put(patternStr, movementPatterns.getOrDefault(patternStr, 0) + 1);
            
            if (movementPatterns.get(patternStr) >= 3) {
                System.out.println("Detected repetitive movement pattern: " + patternStr);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Detect excessive action frequency
     */
    private boolean detectExcessiveFrequency() {
        long currentTime = System.currentTimeMillis();
        long oneMinuteAgo = currentTime - 60000;
        
        // Count actions in the last minute
        long recentActions = actionHistory.stream()
                .filter(action -> action.timestamp > oneMinuteAgo)
                .count();
        
        // If more than 60 actions per minute, it's suspicious
        if (recentActions > 60) {
            System.out.println("Detected excessive action frequency: " + recentActions + " actions/minute");
            return true;
        }
        
        return false;
    }
    
    /**
     * Calculate overall risk score
     */
    private double calculateRiskScore(PatternAnalysisResult result) {
        double score = 0.0;
        
        if (result.hasRepetitiveActions) score += 0.3;
        if (result.hasSuspiciousTiming) score += 0.25;
        if (result.hasRepetitiveMovement) score += 0.2;
        if (result.hasExcessiveFrequency) score += 0.25;
        
        return Math.min(1.0, score);
    }
    
    /**
     * Get recommendations to reduce pattern detection
     */
    public List<String> getAntiPatternRecommendations() {
        List<String> recommendations = new ArrayList<>();
        PatternAnalysisResult result = analyzePatterns();
        
        if (result.hasRepetitiveActions) {
            recommendations.add("Vary action sequences - add random pauses or different approaches");
        }
        
        if (result.hasSuspiciousTiming) {
            recommendations.add("Increase timing randomization - vary delays between actions");
        }
        
        if (result.hasRepetitiveMovement) {
            recommendations.add("Vary movement paths - take different routes occasionally");
        }
        
        if (result.hasExcessiveFrequency) {
            recommendations.add("Reduce action frequency - add longer pauses between actions");
        }
        
        if (result.riskScore > 0.7) {
            recommendations.add("HIGH RISK: Consider taking a longer break or stopping the script");
        } else if (result.riskScore > 0.4) {
            recommendations.add("MEDIUM RISK: Increase randomization and take more breaks");
        }
        
        return recommendations;
    }
    
    /**
     * Clear history (useful for starting fresh)
     */
    public void clearHistory() {
        actionHistory.clear();
        actionCounts.clear();
        lastActionTimes.clear();
        timingIntervals.clear();
        locationHistory.clear();
    }
    
    /**
     * Get statistics about recorded actions
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActions", actionHistory.size());
        stats.put("actionCounts", new HashMap<>(actionCounts));
        stats.put("averageTimingInterval", 
                 timingIntervals.stream().mapToLong(Long::longValue).average().orElse(0));
        stats.put("locationCount", locationHistory.size());
        
        PatternAnalysisResult result = analyzePatterns();
        stats.put("riskScore", result.riskScore);
        stats.put("hasPatterns", result.hasAnyPattern());
        
        return stats;
    }
    
    /**
     * Action record class
     */
    private static class ActionRecord {
        final String actionType;
        final String details;
        final long timestamp;
        
        ActionRecord(String actionType, String details, long timestamp) {
            this.actionType = actionType;
            this.details = details;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * Location record class
     */
    private static class LocationRecord {
        final int x, y;
        final long timestamp;
        
        LocationRecord(int x, int y, long timestamp) {
            this.x = x;
            this.y = y;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * Pattern analysis result class
     */
    public static class PatternAnalysisResult {
        boolean hasRepetitiveActions = false;
        boolean hasSuspiciousTiming = false;
        boolean hasRepetitiveMovement = false;
        boolean hasExcessiveFrequency = false;
        double riskScore = 0.0;
        
        public boolean hasAnyPattern() {
            return hasRepetitiveActions || hasSuspiciousTiming || 
                   hasRepetitiveMovement || hasExcessiveFrequency;
        }
        
        // Getters
        public boolean hasRepetitiveActions() { return hasRepetitiveActions; }
        public boolean hasSuspiciousTiming() { return hasSuspiciousTiming; }
        public boolean hasRepetitiveMovement() { return hasRepetitiveMovement; }
        public boolean hasExcessiveFrequency() { return hasExcessiveFrequency; }
        public double getRiskScore() { return riskScore; }
    }
}
