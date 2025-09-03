package net.botwithus.rs3cook.cooking;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3cook.data.FishData;
import net.botwithus.rs3cook.data.CookingLocation;

import java.util.Random;

/**
 * Handles the actual cooking actions and interface interactions
 */
public class CookingActionHandler {
    
    private static final Random random = new Random();
    private FishData currentFish;
    private CookingLocationManager locationManager;
    private int fishCookedThisSession = 0;
    private int fishBurnedThisSession = 0;
    private long lastCookingTime = 0;
    
    // Interface IDs (these would need to be determined from actual game)
    private static final int COOKING_INTERFACE_ID = 1251; // Example ID
    private static final int MAKE_X_INTERFACE_ID = 1370; // Example ID
    
    public CookingActionHandler(CookingLocationManager locationManager) {
        this.locationManager = locationManager;
    }
    
    /**
     * Perform a complete cooking cycle
     */
    public CookingResult performCookingCycle(FishData fish) {
        this.currentFish = fish;
        
        // Check if we're at the right location
        if (!locationManager.isAtLocation()) {
            return CookingResult.NEED_TO_WALK;
        }
        
        // Check if we have raw fish
        if (!hasRawFish()) {
            return CookingResult.NEED_TO_BANK;
        }
        
        // Check if inventory is full of cooked items
        if (isInventoryFullOfCookedItems()) {
            return CookingResult.NEED_TO_BANK;
        }
        
        // Start cooking
        if (!locationManager.startCooking(fish)) {
            return CookingResult.FAILED;
        }
        
        // Handle the cooking interface
        if (!handleCookingInterface()) {
            return CookingResult.FAILED;
        }
        
        // Wait for cooking to complete
        waitForCookingCompletion();
        
        // Update statistics
        updateCookingStatistics();
        
        return CookingResult.SUCCESS;
    }
    
    /**
     * Handle the cooking interface (Make-X dialog)
     */
    private boolean handleCookingInterface() {
        // Wait for interface to appear
        if (!Execution.delayUntil(5000, () -> isCookingInterfaceOpen())) {
            System.out.println("Cooking interface did not appear");
            return false;
        }
        
        // Handle Make-X interface
        if (Interfaces.isOpen(MAKE_X_INTERFACE_ID)) {
            return handleMakeXInterface();
        }
        
        // Handle cooking progress interface
        if (Interfaces.isOpen(COOKING_INTERFACE_ID)) {
            // Interface is already open and cooking should start automatically
            return true;
        }
        
        System.out.println("Unknown cooking interface state");
        return false;
    }
    
    /**
     * Handle the Make-X interface for selecting quantity
     */
    private boolean handleMakeXInterface() {
        System.out.println("Handling Make-X interface...");
        
        // Try to click "Make All" or enter maximum quantity
        // This would need actual interface component interaction
        
        // For now, simulate clicking and delay
        Execution.delay(random.nextInt(1000) + 500);
        
        // Simulate pressing space or enter to confirm
        // This would need actual key input
        
        return true;
    }
    
    /**
     * Check if cooking interface is open
     */
    private boolean isCookingInterfaceOpen() {
        return Interfaces.isOpen(COOKING_INTERFACE_ID) || Interfaces.isOpen(MAKE_X_INTERFACE_ID);
    }
    
    /**
     * Wait for cooking to complete
     */
    private void waitForCookingCompletion() {
        System.out.println("Waiting for cooking to complete...");
        lastCookingTime = System.currentTimeMillis();
        
        // Wait until cooking is done
        Execution.delayUntil(60000, () -> {
            LocalPlayer player = Client.getLocalPlayer();
            if (player == null) return true;
            
            // Check if still cooking
            boolean stillCooking = locationManager.isCooking() || isCookingInterfaceOpen();
            
            // Check if we ran out of raw fish
            boolean hasRawFish = hasRawFish();
            
            // Check if inventory is full
            boolean inventoryFull = Backpack.isFull();
            
            return !stillCooking || !hasRawFish || inventoryFull;
        });
        
        // Small additional delay for any remaining animations
        Execution.delay(random.nextInt(1000) + 500);
    }
    
    /**
     * Update cooking statistics after a cooking session
     */
    private void updateCookingStatistics() {
        if (currentFish == null) return;
        
        // Count cooked and burned fish
        int cookedCount = Backpack.getCount(currentFish.getCookedItemId());
        int burnedCount = Backpack.getCount("Burnt " + currentFish.getCookedName().toLowerCase());
        
        // This is a simplified approach - in reality we'd need to track changes
        // For now, just increment based on what we find
        fishCookedThisSession += cookedCount;
        fishBurnedThisSession += burnedCount;
        
        System.out.println("Cooking session complete. Cooked: " + cookedCount + ", Burned: " + burnedCount);
    }
    
    /**
     * Check if player has raw fish in inventory
     */
    private boolean hasRawFish() {
        if (currentFish == null) return false;
        return Backpack.contains(currentFish.getRawItemId());
    }
    
    /**
     * Check if inventory is full of cooked items (need to bank)
     */
    private boolean isInventoryFullOfCookedItems() {
        if (!Backpack.isFull()) return false;
        
        // Check if inventory contains mostly cooked fish
        int cookedCount = currentFish != null ? Backpack.getCount(currentFish.getCookedItemId()) : 0;
        int totalItems = Backpack.getCount();
        
        // If more than 80% of inventory is cooked fish, consider it full
        return cookedCount > (totalItems * 0.8);
    }
    
    /**
     * Get cooking progress information
     */
    public CookingProgress getCookingProgress() {
        LocalPlayer player = Client.getLocalPlayer();
        boolean isCooking = player != null && locationManager.isCooking();
        
        int rawFishCount = currentFish != null ? Backpack.getCount(currentFish.getRawItemId()) : 0;
        int cookedFishCount = currentFish != null ? Backpack.getCount(currentFish.getCookedItemId()) : 0;
        
        long estimatedTimeRemaining = 0;
        if (isCooking && rawFishCount > 0) {
            estimatedTimeRemaining = locationManager.getEstimatedCookingTime(rawFishCount);
        }
        
        return new CookingProgress(isCooking, rawFishCount, cookedFishCount, estimatedTimeRemaining);
    }
    
    /**
     * Reset session statistics
     */
    public void resetStatistics() {
        fishCookedThisSession = 0;
        fishBurnedThisSession = 0;
        lastCookingTime = 0;
    }
    
    /**
     * Check if it's safe to continue cooking
     */
    public boolean isSafeToContinue() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) return false;
        
        // Check if player is in combat
        if (player.getTarget() != null) {
            System.out.println("Player is in combat, stopping cooking");
            return false;
        }
        
        // Check if we've been cooking for too long without progress
        long timeSinceLastCooking = System.currentTimeMillis() - lastCookingTime;
        if (timeSinceLastCooking > 300000) { // 5 minutes
            System.out.println("No cooking progress for 5 minutes, stopping");
            return false;
        }
        
        return true;
    }
    
    // Getters
    public int getFishCookedThisSession() { return fishCookedThisSession; }
    public int getFishBurnedThisSession() { return fishBurnedThisSession; }
    public FishData getCurrentFish() { return currentFish; }
    
    /**
     * Enum for cooking results
     */
    public enum CookingResult {
        SUCCESS,
        NEED_TO_WALK,
        NEED_TO_BANK,
        FAILED
    }
    
    /**
     * Class to hold cooking progress information
     */
    public static class CookingProgress {
        private final boolean isCooking;
        private final int rawFishCount;
        private final int cookedFishCount;
        private final long estimatedTimeRemaining;
        
        public CookingProgress(boolean isCooking, int rawFishCount, int cookedFishCount, long estimatedTimeRemaining) {
            this.isCooking = isCooking;
            this.rawFishCount = rawFishCount;
            this.cookedFishCount = cookedFishCount;
            this.estimatedTimeRemaining = estimatedTimeRemaining;
        }
        
        // Getters
        public boolean isCooking() { return isCooking; }
        public int getRawFishCount() { return rawFishCount; }
        public int getCookedFishCount() { return cookedFishCount; }
        public long getEstimatedTimeRemaining() { return estimatedTimeRemaining; }
    }
}
