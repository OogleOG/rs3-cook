package net.botwithus.rs3cook.data;

import java.util.*;

/**
 * Comprehensive fish data for RS3 cooking
 * Contains all fish types with their cooking requirements, experience, and burn rates
 */
public class FishData {

    private String rawName;
    private String cookedName;
    private int cookingLevel;
    private double experience;
    private double bonfireExperience;
    private int stopBurningLevel;
    private int stopBurningLevelWithGauntlets;
    private int healAmount;
    private int rawItemId;
    private int cookedItemId;
    private boolean membersOnly;
    private String notes;

    // Static list of all fish data
    private static final List<FishData> ALL_FISH = new ArrayList<>();
    private static final Map<String, FishData> FISH_BY_NAME = new HashMap<>();

    static {
        initializeFishData();
    }

    public FishData(String rawName, String cookedName, int cookingLevel, double experience,
                   double bonfireExperience, int stopBurningLevel, int stopBurningLevelWithGauntlets,
                   int healAmount, int rawItemId, int cookedItemId, boolean membersOnly, String notes) {
        this.rawName = rawName;
        this.cookedName = cookedName;
        this.cookingLevel = cookingLevel;
        this.experience = experience;
        this.bonfireExperience = bonfireExperience;
        this.stopBurningLevel = stopBurningLevel;
        this.stopBurningLevelWithGauntlets = stopBurningLevelWithGauntlets;
        this.healAmount = healAmount;
        this.rawItemId = rawItemId;
        this.cookedItemId = cookedItemId;
        this.membersOnly = membersOnly;
        this.notes = notes;
    }

    /**
     * Initialize all fish data from RS3 wiki
     */
    private static void initializeFishData() {
        // Low level fish (F2P)
        addFish("Raw minnow", "Minnow", 1, 15, 15, 19, 19, 150, 15264, 15266, false, "Created by cooking raw minnow");
        addFish("Raw crayfish", "Crayfish", 1, 30, 33, 34, 34, 200, 13435, 13433, false, "Created by cooking raw crayfish");
        addFish("Raw shrimps", "Shrimps", 1, 30, 33, 34, 34, 200, 317, 315, false, "Created by cooking raw shrimps");
        addFish("Raw sardine", "Sardine", 1, 40, 44, 38, 38, 200, 327, 325, false, "Created by cooking raw sardine");
        addFish("Raw anchovies", "Anchovies", 1, 30, 30, 34, 34, 200, 321, 319, false, "Created by cooking raw anchovies");
        addFish("Raw herring", "Herring", 5, 50, 55, 41, 41, 200, 345, 347, false, "Created by cooking raw herring");
        addFish("Raw trout", "Trout", 15, 70, 77, 50, 50, 375, 335, 333, false, "Created by cooking raw trout");
        addFish("Raw pike", "Pike", 20, 80, 80, 53, 53, 400, 349, 351, false, "Created by cooking raw pike");
        addFish("Raw salmon", "Salmon", 25, 90, 99, 58, 58, 625, 331, 329, false, "Created by cooking raw salmon");
        addFish("Raw tuna", "Tuna", 30, 100, 110, 63, 63, 750, 359, 361, false, "Created by cooking raw tuna");
        addFish("Raw lobster", "Lobster", 40, 120, 132, 74, 64, 1200, 377, 379, false, "Created by cooking raw lobster");
        addFish("Raw swordfish", "Swordfish", 45, 140, 154, 86, 80, 1400, 371, 373, false, "Created by cooking raw swordfish");

        // Members fish
        addFish("Raw karambwanji", "Karambwanji", 1, 10, 10, 28, 28, 200, 3150, 3151, true, "Members can create karambwanji by cooking raw karambwanji");
        addFish("Raw poison karambwan", "Poison karambwan", 1, 80, 80, 20, 20, -50, 3146, 3148, true, "Members can create poison karambwan by cooking raw karambwan");
        addFish("Raw mackerel", "Mackerel", 10, 60, 66, 45, 45, 250, 353, 355, true, "Members can create mackerel by cooking raw mackerel");
        addFish("Raw cod", "Cod", 18, 75, 75, 52, 52, 450, 341, 339, true, "Members can create cod by cooking raw cod");
        addFish("Raw slimy eel", "Slimy eel", 28, 95, 95, 58, 58, 700, 3379, 3381, true, "Members can create by cooking raw slimy eel");
        addFish("Raw karambwan", "Karambwan", 30, 190, 190, 99, 99, 750, 3142, 3144, true, "Members can create by cooking raw karambwan");
        addFish("Raw rainbow fish", "Rainbow fish", 35, 110, 110, 60, 60, 875, 10138, 10136, true, "Members can create by cooking raw rainbow fish");
        addFish("Raw cave eel", "Cave eel", 38, 115, 115, 74, 74, 950, 5001, 5003, true, "Members can create by cooking raw cave eel");
        addFish("Raw bass", "Bass", 43, 130, 130, 79, 79, 1300, 363, 365, true, "Members can create bass by cooking raw bass");
        addFish("Raw desert sole", "Desert sole", 52, 142.5, 142.5, 83, 83, 1450, 32622, 32624, true, "Created by cooking Raw desert sole. Can only be caught in Menaphos");
        addFish("Raw lava eel", "Lava eel", 53, 30, 30, 53, 53, 1060, 2148, 2149, true, "Members can catch and cook raw lava eel after completing Heroes' Quest");
        addFish("Raw catfish", "Catfish", 60, 145, 145, 89, 89, 1500, 32626, 32628, true, "Created by cooking Raw catfish. Can only be caught in Menaphos");
        addFish("Raw monkfish", "Monkfish", 62, 150, 165, 92, 87, 1600, 7944, 7946, true, "Members can create monkfish by cooking raw monkfish");
        addFish("Raw ghostly sole", "Ghostly sole", 66, 160, 160, 99, 99, 1650, 53004, 53006, true, "Members can create ghostly sole by cooking Raw ghostly sole");
        addFish("Raw beltfish", "Beltfish", 72, 180, 165, 90, 90, 1650, 32630, 32632, true, "Created by cooking Raw beltfish. Can only be caught in Menaphos");
        addFish("Raw green blubber jellyfish", "Green blubber jellyfish", 72, 165, 165, 87, 87, 500, 15270, 15272, true, "Green blubber jellyfish");
        addFish("Raw shark", "Shark", 80, 210, 231, 99, 94, 2000, 383, 385, true, "Members can create shark by cooking raw shark");
        addFish("Raw sea turtle", "Sea turtle", 82, 212, 212, 99, 99, 2050, 395, 397, true, "Members can create sea turtle by cooking raw sea turtle");
        addFish("Raw great white shark", "Great white shark", 84, 212, 236, 99, 94, 2100, 21511, 21513, true, "Members can create great white shark by cooking raw great white shark");
        addFish("Raw cavefish", "Cavefish", 88, 214, 235, 99, 94, 2200, 15264, 15266, true, "Members can create cavefish by cooking raw cavefish");
        addFish("Raw manta ray", "Manta ray", 91, 216, 238, 99, 99, 2275, 389, 391, true, "Members can create manta rays by cooking raw manta rays");
        addFish("Raw rocktail", "Rocktail", 93, 225, 250.8, 99, 94, 2300, 15270, 15272, true, "Members can create rocktail by cooking a raw rocktail");
        addFish("Raw tiger shark", "Tiger shark", 95, 230, 230, 99, 99, 2375, 19947, 19949, true, "Members can catch up to one Tiger shark during a Fishing Trawler attempt");
        addFish("Raw blue blubber jellyfish", "Blue blubber jellyfish", 95, 235, 235, 97, 97, 750, 15274, 15276, true, "Blue blubber jellyfish");
        addFish("Raw sailfish", "Sailfish", 99, 270, 297, 99, 99, 2400, 21517, 21519, true, "Sailfish");
    }

    /**
     * Helper method to add fish to the collections
     */
    private static void addFish(String rawName, String cookedName, int cookingLevel, double experience,
                               double bonfireExperience, int stopBurningLevel, int stopBurningLevelWithGauntlets,
                               int healAmount, int rawItemId, int cookedItemId, boolean membersOnly, String notes) {
        FishData fish = new FishData(rawName, cookedName, cookingLevel, experience, bonfireExperience,
                                   stopBurningLevel, stopBurningLevelWithGauntlets, healAmount,
                                   rawItemId, cookedItemId, membersOnly, notes);
        ALL_FISH.add(fish);
        FISH_BY_NAME.put(rawName, fish);
        FISH_BY_NAME.put(cookedName, fish);
    }

    /**
     * Get all available fish
     */
    public static List<FishData> getAllFish() {
        return new ArrayList<>(ALL_FISH);
    }

    /**
     * Get fish by name (raw or cooked)
     */
    public static FishData getFishByName(String name) {
        return FISH_BY_NAME.get(name);
    }

    /**
     * Get fish suitable for a given cooking level
     */
    public static List<FishData> getFishForLevel(int cookingLevel) {
        return ALL_FISH.stream()
                .filter(fish -> fish.getCookingLevel() <= cookingLevel)
                .sorted(Comparator.comparingInt(FishData::getCookingLevel).reversed())
                .toList();
    }

    /**
     * Get F2P fish only
     */
    public static List<FishData> getF2PFish() {
        return ALL_FISH.stream()
                .filter(fish -> !fish.isMembersOnly())
                .sorted(Comparator.comparingInt(FishData::getCookingLevel))
                .toList();
    }

    /**
     * Get members fish only
     */
    public static List<FishData> getMembersFish() {
        return ALL_FISH.stream()
                .filter(FishData::isMembersOnly)
                .sorted(Comparator.comparingInt(FishData::getCookingLevel))
                .toList();
    }

    /**
     * Get default fish for new users
     */
    public static FishData getDefaultFish() {
        return getFishByName("Raw shrimps");
    }

    /**
     * Check if fish will burn at given level
     */
    public boolean willBurnAtLevel(int cookingLevel, boolean hasGauntlets) {
        int burnLevel = hasGauntlets ? stopBurningLevelWithGauntlets : stopBurningLevel;
        return cookingLevel < burnLevel;
    }

    /**
     * Calculate burn rate at given level
     */
    public double getBurnRateAtLevel(int cookingLevel, boolean hasGauntlets) {
        if (!willBurnAtLevel(cookingLevel, hasGauntlets)) {
            return 0.0;
        }

        int burnLevel = hasGauntlets ? stopBurningLevelWithGauntlets : stopBurningLevel;
        int levelDiff = burnLevel - cookingLevel;

        // Rough approximation of burn rate based on level difference
        if (levelDiff >= 20) return 0.8; // 80% burn rate
        if (levelDiff >= 15) return 0.6; // 60% burn rate
        if (levelDiff >= 10) return 0.4; // 40% burn rate
        if (levelDiff >= 5) return 0.2;  // 20% burn rate
        return 0.1; // 10% burn rate
    }

    // Getters
    public String getRawName() { return rawName; }
    public String getCookedName() { return cookedName; }
    public String getName() { return rawName; } // For compatibility
    public int getCookingLevel() { return cookingLevel; }
    public double getExperience() { return experience; }
    public double getBonfireExperience() { return bonfireExperience; }
    public int getStopBurningLevel() { return stopBurningLevel; }
    public int getStopBurningLevelWithGauntlets() { return stopBurningLevelWithGauntlets; }
    public int getHealAmount() { return healAmount; }
    public int getRawItemId() { return rawItemId; }
    public int getCookedItemId() { return cookedItemId; }
    public boolean isMembersOnly() { return membersOnly; }
    public String getNotes() { return notes; }

    @Override
    public String toString() {
        return rawName + " (Level " + cookingLevel + ", " + experience + " XP)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FishData fishData = (FishData) obj;
        return Objects.equals(rawName, fishData.rawName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawName);
    }
}
