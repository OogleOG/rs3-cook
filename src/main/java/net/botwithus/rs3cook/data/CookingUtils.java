package net.botwithus.rs3cook.data;

import net.botwithus.rs3.game.Coordinate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for cooking-related calculations and helper methods
 */
public class CookingUtils {
    
    /**
     * Calculate experience per hour based on fish type and cooking method
     */
    public static long calculateExperiencePerHour(FishData fish, CookingLocation location, 
                                                 int cookingLevel, boolean hasGauntlets) {
        double baseExperience = location.getType() == CookingLocation.CookingType.BONFIRE ? 
                               fish.getBonfireExperience() : fish.getExperience();
        
        // Calculate burn rate
        double burnRate = fish.getBurnRateAtLevel(cookingLevel, hasGauntlets);
        double successRate = 1.0 - burnRate;
        
        // Estimate cooking speed (fish per hour)
        int fishPerHour = estimateFishPerHour(fish, location);
        
        // Calculate effective experience per hour
        return Math.round(baseExperience * successRate * fishPerHour);
    }
    
    /**
     * Estimate how many fish can be cooked per hour
     */
    private static int estimateFishPerHour(FishData fish, CookingLocation location) {
        // Base cooking time varies by fish type and location
        double baseTimePerFish = 3.6; // seconds per fish (base)
        
        // Adjust for location type
        switch (location.getType()) {
            case PORTABLE_RANGE:
                baseTimePerFish *= 0.9; // 10% faster
                break;
            case BONFIRE:
                baseTimePerFish *= 1.1; // 10% slower but more XP
                break;
            case FIRE:
                baseTimePerFish *= 1.2; // 20% slower
                break;
            default:
                break;
        }
        
        // Adjust for fish complexity (higher level = slightly slower)
        if (fish.getCookingLevel() > 80) {
            baseTimePerFish *= 1.1;
        } else if (fish.getCookingLevel() > 50) {
            baseTimePerFish *= 1.05;
        }
        
        // Add banking time (estimated)
        double bankingTimePerInventory = location.getBankDistance() * 2; // seconds
        int fishPerInventory = 28; // assuming full inventory
        double bankingTimePerFish = bankingTimePerInventory / fishPerInventory;
        
        double totalTimePerFish = baseTimePerFish + bankingTimePerFish;
        return (int) (3600 / totalTimePerFish); // fish per hour
    }
    
    /**
     * Calculate profit per hour
     */
    public static long calculateProfitPerHour(FishData fish, CookingLocation location, 
                                            int cookingLevel, boolean hasGauntlets,
                                            int rawFishPrice, int cookedFishPrice) {
        double burnRate = fish.getBurnRateAtLevel(cookingLevel, hasGauntlets);
        double successRate = 1.0 - burnRate;
        
        int fishPerHour = estimateFishPerHour(fish, location);
        
        // Calculate profit per fish
        double profitPerFish = (cookedFishPrice * successRate) - rawFishPrice;
        
        return Math.round(profitPerFish * fishPerHour);
    }
    
    /**
     * Find the best fish for a given cooking level
     */
    public static FishData getBestFishForLevel(int cookingLevel, boolean membersOnly) {
        List<FishData> availableFish = membersOnly ? 
            FishData.getAllFish() : FishData.getF2PFish();
        
        return availableFish.stream()
                .filter(fish -> fish.getCookingLevel() <= cookingLevel)
                .max((f1, f2) -> Double.compare(f1.getExperience(), f2.getExperience()))
                .orElse(FishData.getDefaultFish());
    }
    
    /**
     * Find the best cooking location for a player
     */
    public static CookingLocation getBestLocationForPlayer(boolean isMembers, boolean hasQuestRequirements) {
        List<CookingLocation> availableLocations = isMembers ? 
            CookingLocation.getAllLocations() : CookingLocation.getF2PLocations();
        
        return availableLocations.stream()
                .filter(location -> !location.requiresQuest() || hasQuestRequirements)
                .filter(CookingLocation::hasLowerBurnRate) // Prefer lower burn rate
                .min((l1, l2) -> Double.compare(l1.getBankDistance(), l2.getBankDistance()))
                .orElse(CookingLocation.getDefaultLocation());
    }
    
    /**
     * Calculate distance between two coordinates
     */
    public static double calculateDistance(Coordinate from, Coordinate to) {
        if (from == null || to == null) return Double.MAX_VALUE;
        
        double dx = from.getX() - to.getX();
        double dy = from.getY() - to.getY();
        double dz = from.getZ() - to.getZ();
        
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Find nearest cooking location to a coordinate
     */
    public static CookingLocation findNearestLocation(Coordinate playerLocation, boolean membersOnly) {
        List<CookingLocation> availableLocations = membersOnly ? 
            CookingLocation.getAllLocations() : CookingLocation.getF2PLocations();
        
        return availableLocations.stream()
                .min((l1, l2) -> Double.compare(
                    calculateDistance(playerLocation, l1.getCoordinate()),
                    calculateDistance(playerLocation, l2.getCoordinate())
                ))
                .orElse(CookingLocation.getDefaultLocation());
    }
    
    /**
     * Get recommended fish progression for training
     */
    public static List<FishData> getTrainingProgression(boolean membersOnly) {
        List<FishData> allFish = membersOnly ? FishData.getAllFish() : FishData.getF2PFish();
        
        // Filter to good training fish (reasonable XP and not too expensive)
        return allFish.stream()
                .filter(fish -> fish.getExperience() >= 30) // Minimum XP threshold
                .filter(fish -> !fish.getRawName().contains("poison")) // Exclude poison
                .filter(fish -> !fish.getRawName().contains("karambwanji")) // Exclude low XP fish
                .sorted((f1, f2) -> Integer.compare(f1.getCookingLevel(), f2.getCookingLevel()))
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate levels needed to stop burning
     */
    public static int getLevelsUntilNoBurn(FishData fish, int currentLevel, boolean hasGauntlets) {
        int stopBurningLevel = hasGauntlets ? 
            fish.getStopBurningLevelWithGauntlets() : fish.getStopBurningLevel();
        
        return Math.max(0, stopBurningLevel - currentLevel);
    }
    
    /**
     * Format experience value for display
     */
    public static String formatExperience(long experience) {
        if (experience >= 1_000_000) {
            return String.format("%.1fM", experience / 1_000_000.0);
        } else if (experience >= 1_000) {
            return String.format("%.1fK", experience / 1_000.0);
        } else {
            return String.valueOf(experience);
        }
    }
    
    /**
     * Format time duration for display
     */
    public static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    /**
     * Check if a fish is profitable to cook
     */
    public static boolean isProfitable(FishData fish, int rawPrice, int cookedPrice, 
                                     int cookingLevel, boolean hasGauntlets) {
        double burnRate = fish.getBurnRateAtLevel(cookingLevel, hasGauntlets);
        double successRate = 1.0 - burnRate;
        
        double expectedValue = cookedPrice * successRate;
        return expectedValue > rawPrice;
    }
    
    /**
     * Get cooking level from experience
     */
    public static int getLevelFromExperience(long experience) {
        // RS3 experience table (simplified)
        if (experience >= 13034431) return 99;
        if (experience >= 10692629) return 98;
        if (experience >= 8771558) return 97;
        if (experience >= 7195629) return 96;
        if (experience >= 5902831) return 95;
        if (experience >= 4842295) return 94;
        if (experience >= 3972294) return 93;
        if (experience >= 3258594) return 92;
        if (experience >= 2673114) return 91;
        if (experience >= 2192818) return 90;
        
        // Continue with more levels as needed...
        // For now, return a simple approximation
        return Math.min(99, (int) Math.sqrt(experience / 100));
    }
    
    /**
     * Get experience needed for next level
     */
    public static long getExperienceForLevel(int level) {
        if (level <= 1) return 0;
        if (level >= 99) return 13034431;
        
        // Simplified RS3 experience formula
        double experience = 0;
        for (int i = 1; i < level; i++) {
            experience += Math.floor(i + 300 * Math.pow(2, i / 7.0)) / 4;
        }
        return Math.round(experience);
    }
}
