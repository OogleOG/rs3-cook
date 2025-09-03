package net.botwithus.rs3cook.data;

import net.botwithus.rs3.game.Coordinate;
import java.util.*;

/**
 * Comprehensive cooking location data for RS3
 * Contains all major cooking locations with their properties and coordinates
 */
public class CookingLocation {

    private String name;
    private CookingType type;
    private Coordinate coordinate;
    private boolean hasLowerBurnRate;
    private boolean requiresQuest;
    private String questRequirement;
    private boolean membersOnly;
    private String nearbyBank;
    private double bankDistance;
    private String notes;

    // Cooking location types
    public enum CookingType {
        FIRE("Fire", false),
        BONFIRE("Bonfire", true),
        RANGE("Range", true),
        PORTABLE_RANGE("Portable Range", true),
        SULPHUR_VENT("Sulphur Vent", false),
        IRON_SPIT("Iron Spit", false);

        private final String displayName;
        private final boolean reducedBurnRate;

        CookingType(String displayName, boolean reducedBurnRate) {
            this.displayName = displayName;
            this.reducedBurnRate = reducedBurnRate;
        }

        public String getDisplayName() { return displayName; }
        public boolean hasReducedBurnRate() { return reducedBurnRate; }
    }

    // Static list of all cooking locations
    private static final List<CookingLocation> ALL_LOCATIONS = new ArrayList<>();
    private static final Map<String, CookingLocation> LOCATIONS_BY_NAME = new HashMap<>();

    static {
        initializeCookingLocations();
    }

    public CookingLocation(String name, CookingType type, Coordinate coordinate, boolean hasLowerBurnRate,
                          boolean requiresQuest, String questRequirement, boolean membersOnly,
                          String nearbyBank, double bankDistance, String notes) {
        this.name = name;
        this.type = type;
        this.coordinate = coordinate;
        this.hasLowerBurnRate = hasLowerBurnRate;
        this.requiresQuest = requiresQuest;
        this.questRequirement = questRequirement;
        this.membersOnly = membersOnly;
        this.nearbyBank = nearbyBank;
        this.bankDistance = bankDistance;
        this.notes = notes;
    }

    /**
     * Initialize all cooking locations
     */
    private static void initializeCookingLocations() {
        // Major F2P cooking locations
        addLocation("Lumbridge Range", CookingType.RANGE, new Coordinate(3210, 3214, 0), true,
                   true, "Cook's Assistant", false, "Lumbridge Bank", 15.0,
                   "Best F2P cooking location with bank upstairs");

        addLocation("Varrock East Bank Range", CookingType.RANGE, new Coordinate(3253, 3398, 0), true,
                   false, null, false, "Varrock East Bank", 8.0,
                   "Close to bank, popular F2P location");

        addLocation("Al Kharid Range", CookingType.RANGE, new Coordinate(3273, 3180, 0), true,
                   false, null, false, "Al Kharid Bank", 12.0,
                   "Good F2P alternative location");

        addLocation("Edgeville Range", CookingType.RANGE, new Coordinate(3077, 3493, 0), true,
                   false, null, false, "Edgeville Bank", 10.0,
                   "Convenient F2P location near bank");

        // Members cooking locations
        addLocation("Catherby Range", CookingType.RANGE, new Coordinate(2816, 3443, 0), true,
                   false, null, true, "Catherby Bank", 8.0,
                   "Popular members location near fishing spots");

        addLocation("Rogues' Den Range", CookingType.RANGE, new Coordinate(3043, 4973, 1), true,
                   false, null, true, "Rogues' Den Bank", 5.0,
                   "Excellent location with very close bank");

        addLocation("Cooking Guild Range", CookingType.RANGE, new Coordinate(3142, 3449, 0), true,
                   false, null, true, "Cooking Guild Bank", 3.0,
                   "Best range location, requires chef's hat or Varrock hard diary");

        addLocation("Prifddinas Range", CookingType.RANGE, new Coordinate(2225, 3308, 0), true,
                   true, "Plague's End", true, "Prifddinas Bank", 6.0,
                   "High-level location with crystal range");

        addLocation("Max Guild Range", CookingType.RANGE, new Coordinate(2329, 3677, 0), true,
                   false, null, true, "Max Guild Bank", 4.0,
                   "Requires max cape, excellent for high-level players");

        addLocation("Fort Forinthry Range", CookingType.RANGE, new Coordinate(3283, 3434, 0), true,
                   false, null, true, "Fort Forinthry Bank", 7.0,
                   "Player-owned fort location");

        // Bonfire locations
        addLocation("Grand Exchange Bonfire", CookingType.BONFIRE, new Coordinate(3164, 3477, 0), true,
                   false, null, false, "Grand Exchange Bank", 15.0,
                   "Popular bonfire location, increased XP rates");

        addLocation("Varrock West Bank Bonfire", CookingType.BONFIRE, new Coordinate(3185, 3436, 0), true,
                   false, null, false, "Varrock West Bank", 12.0,
                   "Convenient bonfire near bank");

        addLocation("Falador East Bank Bonfire", CookingType.BONFIRE, new Coordinate(3013, 3355, 0), true,
                   false, null, false, "Falador East Bank", 10.0,
                   "Good bonfire location");

        addLocation("Prifddinas Bonfire", CookingType.BONFIRE, new Coordinate(2207, 3317, 0), true,
                   true, "Plague's End", true, "Prifddinas Bank", 8.0,
                   "High-level bonfire location");

        // Portable range locations (these move, so coordinates are approximate)
        addLocation("Portable Range (W84)", CookingType.PORTABLE_RANGE, new Coordinate(3164, 3477, 0), true,
                   false, null, false, "Varies", 0.0,
                   "Community portable ranges, check W84 for locations");

        addLocation("Portable Range (W99)", CookingType.PORTABLE_RANGE, new Coordinate(3164, 3477, 0), true,
                   false, null, false, "Varies", 0.0,
                   "Community portable ranges, check W99 for locations");

        // Fire locations (basic)
        addLocation("Lumbridge Fire", CookingType.FIRE, new Coordinate(3222, 3218, 0), false,
                   false, null, false, "Lumbridge Bank", 20.0,
                   "Basic fire location near Lumbridge");

        addLocation("Varrock Fire", CookingType.FIRE, new Coordinate(3210, 3424, 0), false,
                   false, null, false, "Varrock East Bank", 25.0,
                   "Basic fire location in Varrock");

        // Special locations
        addLocation("Karamja Volcano", CookingType.SULPHUR_VENT, new Coordinate(2857, 3169, 0), false,
                   false, null, true, "TzHaar Bank", 30.0,
                   "Special cooking location using volcanic heat");
    }

    /**
     * Helper method to add locations to collections
     */
    private static void addLocation(String name, CookingType type, Coordinate coordinate, boolean hasLowerBurnRate,
                                  boolean requiresQuest, String questRequirement, boolean membersOnly,
                                  String nearbyBank, double bankDistance, String notes) {
        CookingLocation location = new CookingLocation(name, type, coordinate, hasLowerBurnRate,
                                                     requiresQuest, questRequirement, membersOnly,
                                                     nearbyBank, bankDistance, notes);
        ALL_LOCATIONS.add(location);
        LOCATIONS_BY_NAME.put(name, location);
    }

    /**
     * Get all cooking locations
     */
    public static List<CookingLocation> getAllLocations() {
        return new ArrayList<>(ALL_LOCATIONS);
    }

    /**
     * Get location by name
     */
    public static CookingLocation getLocationByName(String name) {
        return LOCATIONS_BY_NAME.get(name);
    }

    /**
     * Get locations by type
     */
    public static List<CookingLocation> getLocationsByType(CookingType type) {
        return ALL_LOCATIONS.stream()
                .filter(location -> location.getType() == type)
                .sorted(Comparator.comparing(CookingLocation::getName))
                .toList();
    }

    /**
     * Get F2P locations only
     */
    public static List<CookingLocation> getF2PLocations() {
        return ALL_LOCATIONS.stream()
                .filter(location -> !location.isMembersOnly())
                .sorted(Comparator.comparing(CookingLocation::getName))
                .toList();
    }

    /**
     * Get members locations only
     */
    public static List<CookingLocation> getMembersLocations() {
        return ALL_LOCATIONS.stream()
                .filter(CookingLocation::isMembersOnly)
                .sorted(Comparator.comparing(CookingLocation::getName))
                .toList();
    }

    /**
     * Get locations sorted by bank distance
     */
    public static List<CookingLocation> getLocationsByBankDistance() {
        return ALL_LOCATIONS.stream()
                .sorted(Comparator.comparingDouble(CookingLocation::getBankDistance))
                .toList();
    }

    /**
     * Get default cooking location
     */
    public static CookingLocation getDefaultLocation() {
        return getLocationByName("Lumbridge Range");
    }

    /**
     * Get best location for a player's membership status
     */
    public static CookingLocation getBestLocation(boolean isMembers) {
        if (isMembers) {
            return getLocationByName("Rogues' Den Range");
        } else {
            return getLocationByName("Lumbridge Range");
        }
    }

    // Getters
    public String getName() { return name; }
    public CookingType getType() { return type; }
    public Coordinate getCoordinate() { return coordinate; }
    public boolean hasLowerBurnRate() { return hasLowerBurnRate; }
    public boolean requiresQuest() { return requiresQuest; }
    public String getQuestRequirement() { return questRequirement; }
    public boolean isMembersOnly() { return membersOnly; }
    public String getNearbyBank() { return nearbyBank; }
    public double getBankDistance() { return bankDistance; }
    public String getNotes() { return notes; }

    @Override
    public String toString() {
        return name + " (" + type.getDisplayName() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CookingLocation that = (CookingLocation) obj;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
