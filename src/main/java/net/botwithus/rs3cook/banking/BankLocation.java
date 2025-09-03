package net.botwithus.rs3cook.banking;

import net.botwithus.rs3.game.Coordinate;
import java.util.*;

/**
 * Represents a banking location in RS3
 */
public class BankLocation {
    
    private String name;
    private BankType type;
    private Coordinate coordinate;
    private boolean membersOnly;
    private boolean requiresQuest;
    private String questRequirement;
    private String notes;
    
    public enum BankType {
        BANK_BOOTH("Bank booth"),
        BANK_CHEST("Bank chest"),
        BANKER_NPC("Banker");
        
        private final String displayName;
        
        BankType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    // Static list of all bank locations
    private static final List<BankLocation> ALL_BANKS = new ArrayList<>();
    private static final Map<String, BankLocation> BANKS_BY_NAME = new HashMap<>();
    
    static {
        initializeBankLocations();
    }
    
    public BankLocation(String name, BankType type, Coordinate coordinate, boolean membersOnly,
                       boolean requiresQuest, String questRequirement, String notes) {
        this.name = name;
        this.type = type;
        this.coordinate = coordinate;
        this.membersOnly = membersOnly;
        this.requiresQuest = requiresQuest;
        this.questRequirement = questRequirement;
        this.notes = notes;
    }
    
    /**
     * Initialize all bank locations
     */
    private static void initializeBankLocations() {
        // Major F2P banks
        addBank("Lumbridge Bank", BankType.BANK_BOOTH, new Coordinate(3208, 3220, 2), false, false, null,
               "Upstairs from Lumbridge range, very convenient");
        
        addBank("Varrock East Bank", BankType.BANK_BOOTH, new Coordinate(3253, 3420, 0), false, false, null,
               "Popular F2P bank near Grand Exchange");
        
        addBank("Varrock West Bank", BankType.BANK_BOOTH, new Coordinate(3185, 3441, 0), false, false, null,
               "West Varrock bank, close to mining guild");
        
        addBank("Al Kharid Bank", BankType.BANK_BOOTH, new Coordinate(3269, 3167, 0), false, false, null,
               "Al Kharid bank, near range and furnace");
        
        addBank("Falador East Bank", BankType.BANK_BOOTH, new Coordinate(3013, 3355, 0), false, false, null,
               "Falador east bank, central location");
        
        addBank("Falador West Bank", BankType.BANK_BOOTH, new Coordinate(2946, 3368, 0), false, false, null,
               "Falador west bank, near party room");
        
        addBank("Edgeville Bank", BankType.BANK_BOOTH, new Coordinate(3094, 3492, 0), false, false, null,
               "Edgeville bank, close to wilderness");
        
        addBank("Draynor Bank", BankType.BANK_BOOTH, new Coordinate(3092, 3243, 0), false, false, null,
               "Draynor village bank, near willows");
        
        addBank("Grand Exchange Bank", BankType.BANK_CHEST, new Coordinate(3164, 3487, 0), false, false, null,
               "Grand Exchange bank chest, very popular");
        
        // Members banks
        addBank("Catherby Bank", BankType.BANK_BOOTH, new Coordinate(2808, 3441, 0), true, false, null,
               "Catherby bank, excellent for fishing and cooking");
        
        addBank("Seers' Village Bank", BankType.BANK_BOOTH, new Coordinate(2726, 3493, 0), true, false, null,
               "Seers' Village bank, near flax field");
        
        addBank("Camelot Bank", BankType.BANK_CHEST, new Coordinate(2725, 3493, 0), true, false, null,
               "Camelot bank chest, same as Seers'");
        
        addBank("Ardougne North Bank", BankType.BANK_CHEST, new Coordinate(2615, 3332, 0), true, false, null,
               "Ardougne north bank, near market");
        
        addBank("Ardougne South Bank", BankType.BANK_BOOTH, new Coordinate(2655, 3283, 0), true, false, null,
               "Ardougne south bank, near monastery");
        
        addBank("Yanille Bank", BankType.BANK_BOOTH, new Coordinate(2612, 3094, 0), true, false, null,
               "Yanille bank, near magic guild");
        
        addBank("Rogues' Den Bank", BankType.BANK_CHEST, new Coordinate(3043, 4973, 1), true, false, null,
               "Rogues' Den bank, excellent for cooking");
        
        addBank("Cooking Guild Bank", BankType.BANK_CHEST, new Coordinate(3142, 3449, 0), true, false, null,
               "Cooking Guild bank, requires chef's hat");
        
        addBank("Fishing Guild Bank", BankType.BANK_CHEST, new Coordinate(2586, 3419, 0), true, false, null,
               "Fishing Guild bank, great for fishing");
        
        addBank("Prifddinas Bank", BankType.BANK_CHEST, new Coordinate(2283, 3362, 0), true, true, "Plague's End",
               "Prifddinas crystal bank, high level area");
        
        addBank("Max Guild Bank", BankType.BANK_CHEST, new Coordinate(2329, 3677, 0), true, false, null,
               "Max Guild bank, requires max cape");
        
        addBank("Fort Forinthry Bank", BankType.BANK_CHEST, new Coordinate(3283, 3434, 0), true, false, null,
               "Player-owned fort bank");
        
        addBank("Burthorpe Bank", BankType.BANK_BOOTH, new Coordinate(2887, 3537, 0), true, false, null,
               "Burthorpe bank, near combat academy");
        
        addBank("Taverley Bank", BankType.BANK_BOOTH, new Coordinate(2878, 3417, 0), true, false, null,
               "Taverley bank, near herblore shop");
        
        addBank("Canifis Bank", BankType.BANK_BOOTH, new Coordinate(3512, 3480, 0), true, true, "Priest in Peril",
               "Canifis bank, in Morytania");
        
        addBank("Burgh de Rott Bank", BankType.BANK_CHEST, new Coordinate(3496, 3211, 0), true, true, "In Aid of the Myreque",
               "Burgh de Rott bank, requires quest");
        
        addBank("Castle Wars Bank", BankType.BANK_CHEST, new Coordinate(2443, 3083, 0), true, false, null,
               "Castle Wars bank chest");
        
        addBank("Shantay Pass Bank", BankType.BANK_CHEST, new Coordinate(3308, 3120, 0), true, false, null,
               "Shantay Pass bank, desert entrance");
        
        addBank("Nardah Bank", BankType.BANK_BOOTH, new Coordinate(3428, 2892, 0), true, false, null,
               "Nardah bank, in desert");
        
        addBank("Menaphos Bank", BankType.BANK_CHEST, new Coordinate(3217, 2725, 0), true, true, "The Jack of Spades",
               "Menaphos bank, requires quest access");
    }
    
    /**
     * Helper method to add banks to collections
     */
    private static void addBank(String name, BankType type, Coordinate coordinate, boolean membersOnly,
                               boolean requiresQuest, String questRequirement, String notes) {
        BankLocation bank = new BankLocation(name, type, coordinate, membersOnly, requiresQuest, questRequirement, notes);
        ALL_BANKS.add(bank);
        BANKS_BY_NAME.put(name, bank);
    }
    
    /**
     * Get all bank locations
     */
    public static List<BankLocation> getAllBanks() {
        return new ArrayList<>(ALL_BANKS);
    }
    
    /**
     * Get bank by name
     */
    public static BankLocation getBankByName(String name) {
        return BANKS_BY_NAME.get(name);
    }
    
    /**
     * Get F2P banks only
     */
    public static List<BankLocation> getF2PBanks() {
        return ALL_BANKS.stream()
                .filter(bank -> !bank.isMembersOnly())
                .sorted(Comparator.comparing(BankLocation::getName))
                .toList();
    }
    
    /**
     * Get members banks only
     */
    public static List<BankLocation> getMembersBanks() {
        return ALL_BANKS.stream()
                .filter(BankLocation::isMembersOnly)
                .sorted(Comparator.comparing(BankLocation::getName))
                .toList();
    }
    
    /**
     * Get banks by type
     */
    public static List<BankLocation> getBanksByType(BankType type) {
        return ALL_BANKS.stream()
                .filter(bank -> bank.getType() == type)
                .sorted(Comparator.comparing(BankLocation::getName))
                .toList();
    }
    
    /**
     * Get default bank
     */
    public static BankLocation getDefaultBank() {
        return getBankByName("Lumbridge Bank");
    }
    
    /**
     * Find nearest bank to a coordinate
     */
    public static BankLocation findNearestBank(Coordinate location, boolean membersOnly) {
        List<BankLocation> availableBanks = membersOnly ? getAllBanks() : getF2PBanks();
        
        return availableBanks.stream()
                .filter(bank -> !bank.requiresQuest()) // Filter out quest-locked banks for now
                .min((b1, b2) -> Double.compare(
                    calculateDistance(location, b1.getCoordinate()),
                    calculateDistance(location, b2.getCoordinate())
                ))
                .orElse(getDefaultBank());
    }
    
    /**
     * Calculate distance between coordinates
     */
    private static double calculateDistance(Coordinate from, Coordinate to) {
        if (from == null || to == null) return Double.MAX_VALUE;
        
        double dx = from.getX() - to.getX();
        double dy = from.getY() - to.getY();
        
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    // Getters
    public String getName() { return name; }
    public BankType getType() { return type; }
    public Coordinate getCoordinate() { return coordinate; }
    public boolean isMembersOnly() { return membersOnly; }
    public boolean requiresQuest() { return requiresQuest; }
    public String getQuestRequirement() { return questRequirement; }
    public String getNotes() { return notes; }
    
    @Override
    public String toString() {
        return name + " (" + type.getDisplayName() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BankLocation that = (BankLocation) obj;
        return Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
