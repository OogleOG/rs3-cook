package net.botwithus.rs3cook.banking;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3cook.data.FishData;
import net.botwithus.rs3cook.config.CookingConfig;

import java.util.Random;

/**
 * Manages all banking operations for the cooking script
 */
public class BankingManager {
    
    private static final Random random = new Random();
    private BankLocation currentBank;
    private CookingConfig config;
    private FishData targetFish;
    
    public BankingManager(CookingConfig config) {
        this.config = config;
        this.currentBank = BankLocation.getDefaultBank();
    }
    
    /**
     * Set the target fish for banking operations
     */
    public void setTargetFish(FishData fish) {
        this.targetFish = fish;
    }
    
    /**
     * Find and walk to the nearest bank
     */
    public boolean walkToNearestBank() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) return false;
        
        // Find nearest bank
        Coordinate playerCoord = player.getCoordinate();
        boolean isMembers = true; // Would need to check actual membership status
        
        BankLocation nearestBank = BankLocation.findNearestBank(playerCoord, isMembers);
        if (nearestBank == null) {
            System.out.println("No suitable bank found!");
            return false;
        }
        
        return walkToBank(nearestBank);
    }
    
    /**
     * Walk to a specific bank
     */
    public boolean walkToBank(BankLocation bank) {
        if (bank == null) {
            System.out.println("No bank specified!");
            return false;
        }
        
        this.currentBank = bank;
        
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) return false;
        
        // Check if already at bank
        if (isAtBank()) {
            return true;
        }
        
        System.out.println("Walking to " + bank.getName() + "...");
        
        Coordinate bankCoord = bank.getCoordinate();
        Movement.walkTo(bankCoord.getX(), bankCoord.getY(), false);

        return false;
    }
    
    /**
     * Check if player is at a bank
     */
    public boolean isAtBank() {
        if (currentBank == null) return false;
        
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) return false;
        
        Coordinate playerCoord = player.getCoordinate();
        Coordinate bankCoord = currentBank.getCoordinate();
        
        // Consider "at bank" if within 8 tiles
        double distance = Math.sqrt(
            Math.pow(playerCoord.getX() - bankCoord.getX(), 2) +
            Math.pow(playerCoord.getY() - bankCoord.getY(), 2)
        );
        
        return distance <= 8.0;
    }
    
    /**
     * Open the bank interface
     */
    public boolean openBank() {
        if (!isAtBank()) {
            System.out.println("Not at bank location!");
            return false;
        }
        
        // Check if bank is already open
        if (isBankInterfaceOpen()) {
            return true;
        }
        
        // Find and interact with bank object/NPC
        boolean bankOpened = switch (currentBank.getType()) {
            case BANK_BOOTH, BANK_CHEST -> openBankObject();
            case BANKER_NPC -> openBankNpc();
        };

        if (bankOpened) {
            // Wait for bank interface to open
            Execution.delayUntil(5000, this::isBankInterfaceOpen);
            return isBankInterfaceOpen();
        }
        
        return false;
    }
    
    /**
     * Open bank via object (booth/chest)
     */
    private boolean openBankObject() {
        SceneObject bankObject = SceneObjectQuery.newQuery()
                .name("Bank booth", "Bank chest", "Bank", "Chest")
                .option("Bank", "Use")
                .results()
                .nearest();
        
        if (bankObject != null) {
            System.out.println("Opening bank: " + bankObject.getName());
            return bankObject.interact("Bank");
        }
        
        System.out.println("Could not find bank object");
        return false;
    }
    
    /**
     * Open bank via NPC
     */
    private boolean openBankNpc() {
        Npc banker = NpcQuery.newQuery()
                .name("Banker", "Bank clerk")
                .option("Bank")
                .results()
                .nearest();
        
        if (banker != null) {
            System.out.println("Opening bank with: " + banker.getName());
            return banker.interact("Bank");
        }
        
        System.out.println("Could not find banker NPC");
        return false;
    }
    
    /**
     * Open deposit box
     */
    private boolean openDepositBox() {
        SceneObject depositBox = SceneObjectQuery.newQuery()
                .name("Bank deposit box", "Deposit box")
                .option("Deposit")
                .results()
                .nearest();
        
        if (depositBox != null) {
            System.out.println("Opening deposit box");
            return depositBox.interact("Deposit");
        }
        
        System.out.println("Could not find deposit box");
        return false;
    }
    
    /**
     * Open portable bank
     */
    private boolean openPortableBank() {
        SceneObject portableBank = SceneObjectQuery.newQuery()
                .name("Portable bank")
                .option("Bank")
                .results()
                .nearest();
        
        if (portableBank != null) {
            System.out.println("Opening portable bank");
            return portableBank.interact("Bank");
        }
        
        System.out.println("Could not find portable bank");
        return false;
    }
    
    /**
     * Perform complete banking operation for cooking
     */
    public boolean performBankingOperation() {
        if (targetFish == null) {
            System.out.println("No target fish set for banking!");
            return false;
        }
        
        // Open bank
        if (!openBank()) {
            System.out.println("Failed to open bank");
            return false;
        }
        
        // Deposit cooked fish and other items
        if (!depositCookedItems()) {
            System.out.println("Failed to deposit items");
            return false;
        }
        
        // Withdraw raw fish
        if (!withdrawRawFish()) {
            System.out.println("Failed to withdraw raw fish");
            return false;
        }
        
        // Close bank
        closeBank();
        
        return true;
    }
    
    /**
     * Deposit cooked fish and other items
     */
    private boolean depositCookedItems() {
        System.out.println("Depositing cooked items...");
        
        // Deposit cooked fish
        if (Backpack.contains(targetFish.getCookedItemId())) {
            depositItem(targetFish.getCookedItemId());
            Execution.delay(random.nextInt(500) + 300);
        }

        // Deposit burned fish
        String burnedFishName = "Burnt " + targetFish.getCookedName().toLowerCase();
        if (Backpack.contains(burnedFishName)) {
            depositItem(burnedFishName);
            Execution.delay(random.nextInt(500) + 300);
        }
        
        // Deposit any other items except raw fish and tools
        // This is a simplified approach - in reality we'd be more selective
        for (int i = 0; i < 28; i++) {
            // Skip raw fish and essential items
            // This would need more sophisticated item checking
        }
        
        return true;
    }
    
    /**
     * Withdraw raw fish
     */
    private boolean withdrawRawFish() {
        System.out.println("Withdrawing raw " + targetFish.getRawName() + "...");
        
        if (config.isUseBankPresets()) {
            return withdrawUsingPreset();
        } else {
            return withdrawManually();
        }
    }
    
    /**
     * Withdraw using bank preset
     */
    private boolean withdrawUsingPreset() {
        int presetNumber = config.getBankPresetNumber();
        System.out.println("Loading bank preset " + presetNumber);
        
        // Load preset (this would need actual preset loading implementation)
        // Bank.loadPreset(presetNumber);
        
        Execution.delay(random.nextInt(1000) + 500);
        return true;
    }
    
    /**
     * Withdraw manually
     */
    private boolean withdrawManually() {
        // Withdraw all raw fish
        if (bankContains(targetFish.getRawName())) {
            withdrawItem(targetFish.getRawItemId());
            Execution.delay(random.nextInt(1000) + 500);
            return true;
        } else {
            System.out.println("No " + targetFish.getRawName() + " found in bank!");
            return false;
        }
    }
    
    /**
     * Close bank interface
     */
    public void closeBank() {
        if (isBankInterfaceOpen()) {
            closeBankInterface();
            Execution.delay(random.nextInt(500) + 300);
        }
    }
    
    /**
     * Check if we have enough raw fish to continue
     */
    public boolean hasEnoughRawFish() {
        if (targetFish == null) return false;
        
        int rawFishCount = Backpack.getCount(targetFish.getRawItemId());
        return rawFishCount > 0;
    }
    
    /**
     * Check if we need to bank (inventory full of cooked items)
     */
    public boolean needsToBank() {
        if (targetFish == null) return false;
        
        // Check if inventory is full
        if (!Backpack.isFull()) return false;
        
        // Check if we have cooked fish to deposit
        int cookedCount = Backpack.getCount(targetFish.getCookedItemId());
        return cookedCount > 0;
    }
    
    /**
     * Get estimated banking time
     */
    public long getEstimatedBankingTime() {
        // Base banking time
        long baseTime = 10000; // 10 seconds
        
        // Add travel time based on distance
        if (currentBank != null) {
            LocalPlayer player = Client.getLocalPlayer();
            if (player != null) {
                double distance = Math.sqrt(
                    Math.pow(player.getCoordinate().getX() - currentBank.getCoordinate().getX(), 2) +
                    Math.pow(player.getCoordinate().getY() - currentBank.getCoordinate().getY(), 2)
                );
                baseTime += (long) (distance * 100); // Rough estimate
            }
        }
        
        return baseTime;
    }
    
    // Placeholder methods for banking API (to be implemented when API is available)
    private boolean isBankInterfaceOpen() {
        // TODO: Implement when banking API is available
        // This should check if the bank interface is currently open
        return false;
    }

    private void depositItem(int itemId) {
        // TODO: Implement when banking API is available
        // This should deposit all items of the given ID
        System.out.println("Depositing item ID: " + itemId);
    }

    private void depositItem(String itemName) {
        // TODO: Implement when banking API is available
        // This should deposit all items with the given name
        System.out.println("Depositing item: " + itemName);
    }

    private boolean bankContains(String itemName) {
        // TODO: Implement when banking API is available
        // This should check if the bank contains the item
        System.out.println("Checking if bank contains: " + itemName);
        return true; // Assume true for now
    }

    private void withdrawItem(int itemId) {
        // TODO: Implement when banking API is available
        // This should withdraw all items of the given ID
        System.out.println("Withdrawing item ID: " + itemId);
    }

    private void closeBankInterface() {
        // TODO: Implement when banking API is available
        // This should close the bank interface
        System.out.println("Closing bank interface");
    }

    // Getters and setters
    public BankLocation getCurrentBank() { return currentBank; }
    public void setCurrentBank(BankLocation bank) { this.currentBank = bank; }
    public FishData getTargetFish() { return targetFish; }
}
