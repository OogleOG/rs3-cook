package net.botwithus.rs3cook;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.rs3cook.data.FishData;
import net.botwithus.rs3cook.data.CookingLocation;
import net.botwithus.rs3cook.config.CookingConfig;
import net.botwithus.rs3cook.ui.CookingScriptGraphicsContext;
import net.botwithus.rs3cook.cooking.CookingLocationManager;
import net.botwithus.rs3cook.cooking.CookingActionHandler;
import net.botwithus.rs3cook.banking.BankingManager;
import net.botwithus.rs3cook.safety.SafetyManager;
import net.botwithus.rs3cook.safety.AntiPatternDetector;
import net.botwithus.rs3cook.statistics.StatisticsManager;
import net.botwithus.rs3cook.statistics.SessionLogger;

import java.util.Random;

/**
 * Main cooking script class for RS3 BotWithUs
 * Supports all fish types and cooking methods with intelligent banking
 */
public class CookingScript extends LoopingScript {

    // Script state management
    private BotState botState = BotState.IDLE;
    private Random random = new Random();
    
    // Configuration and data
    private CookingConfig config;
    private FishData selectedFish;
    private CookingLocation currentLocation;

    // Cooking managers
    private CookingLocationManager locationManager;
    private CookingActionHandler actionHandler;
    private BankingManager bankingManager;

    // Safety and anti-detection
    private SafetyManager safetyManager;
    private AntiPatternDetector patternDetector;

    // Statistics and logging
    private StatisticsManager statisticsManager;
    private SessionLogger sessionLogger;
    
    // Statistics tracking
    private long sessionStartTime;
    private int fishCooked = 0;
    private int fishBurned = 0;
    private long experienceGained = 0;
    private long startExperience = 0;
    
    // Safety and timing
    private long lastActionTime = 0;
    private int consecutiveFailures = 0;
    private static final int MAX_FAILURES = 5;
    private static final long ACTION_TIMEOUT = 10000; // 10 seconds

    /**
     * Bot states for different phases of cooking
     */
    public enum BotState {
        IDLE,                    // Waiting for user input or initialization
        INITIALIZING,           // Setting up script configuration
        WALKING_TO_BANK,        // Moving to nearest bank
        BANKING,                // Withdrawing raw fish / depositing cooked fish
        WALKING_TO_COOKING,     // Moving to cooking location
        COOKING,                // Actively cooking fish
        HANDLING_INTERFACE,     // Dealing with cooking interfaces
        ERROR_RECOVERY,         // Recovering from errors
        BREAK_TIME,            // Taking a break for anti-detection
        STOPPING               // Gracefully stopping the script
    }

    /**
     * Constructor for the cooking script
     */
    public CookingScript(String name, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(name, scriptConfig, scriptDefinition);
        this.sgc = new CookingScriptGraphicsContext(getConsole(), this);
        this.config = new CookingConfig();
        this.sessionStartTime = System.currentTimeMillis();
        
        // Initialize with default values
        this.selectedFish = FishData.getDefaultFish();
        this.currentLocation = CookingLocation.getDefaultLocation();

        // Initialize managers
        this.locationManager = new CookingLocationManager();
        this.actionHandler = new CookingActionHandler(locationManager);
        this.bankingManager = new BankingManager(config);
        this.safetyManager = new SafetyManager(config);
        this.patternDetector = new AntiPatternDetector();
        this.statisticsManager = new StatisticsManager();
        this.sessionLogger = new SessionLogger();

        println("RS3 Cooking Script initialized successfully!");
    }

    @Override
    public void onLoop() {
        // Use safety manager for human-like delays
        this.loopDelay = safetyManager.generateRandomDelay();

        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            Execution.delay(random.nextLong(2000, 5000));
            return;
        }

        // Check for emergency stops using safety manager
        if (safetyManager.shouldEmergencyStop()) {
            println("Emergency stop triggered: " + safetyManager.getEmergencyStopReason());
            botState = BotState.STOPPING;
            return;
        }

        // Check if it's time for a break
        if (safetyManager.isTimeForBreak()) {
            if (!safetyManager.isOnBreak()) {
                safetyManager.startBreak();
            }
            botState = BotState.BREAK_TIME;
            return;
        }

        // Main state machine
        switch (botState) {
            case IDLE -> handleIdleState(player);
            case INITIALIZING -> handleInitializingState(player);
            case WALKING_TO_BANK -> handleWalkingToBankState(player);
            case BANKING -> handleBankingState(player);
            case WALKING_TO_COOKING -> handleWalkingToCookingState(player);
            case COOKING -> handleCookingState(player);
            case HANDLING_INTERFACE -> handleInterfaceState(player);
            case ERROR_RECOVERY -> handleErrorRecoveryState(player);
            case BREAK_TIME -> handleBreakTimeState(player);
            case STOPPING -> handleStoppingState(player);
        }
        
        updateLastActionTime();
    }

    /**
     * Handle idle state - waiting for configuration
     */
    private void handleIdleState(LocalPlayer player) {
        if (config.isConfigured()) {
            println("Configuration complete, initializing script...");
            botState = BotState.INITIALIZING;
        } else {
            // Wait for user to configure via UI
            Execution.delay(random.nextLong(1000, 2000));
        }
    }

    /**
     * Handle initialization state - prepare for cooking
     */
    private void handleInitializingState(LocalPlayer player) {
        try {
            // Record starting experience
            startExperience = getCurrentCookingExperience();

            // Set target fish in banking manager
            bankingManager.setTargetFish(selectedFish);

            // Determine what we need to do first
            if (needsToBankFirst()) {
                println("Need to bank first, walking to bank...");
                botState = BotState.WALKING_TO_BANK;
            } else if (hasRawFishInInventory()) {
                println("Have raw fish, walking to cooking location...");
                botState = BotState.WALKING_TO_COOKING;
            } else {
                println("No raw fish, need to bank...");
                botState = BotState.WALKING_TO_BANK;
            }
            
            Execution.delay(random.nextLong(500, 1500));
        } catch (Exception e) {
            println("Error during initialization: " + e.getMessage());
            botState = BotState.ERROR_RECOVERY;
        }
    }

    /**
     * Handle walking to bank state
     */
    private void handleWalkingToBankState(LocalPlayer player) {
        try {
            // Record action and location for pattern detection
            patternDetector.recordAction("WALK_TO_BANK", "nearest", System.currentTimeMillis());
            if (player.getCoordinate() != null) {
                patternDetector.recordLocation(player.getCoordinate().getX(),
                                             player.getCoordinate().getY(),
                                             System.currentTimeMillis());
            }

            println("Walking to nearest bank...");

            if (bankingManager.walkToNearestBank()) {
                println("Arrived at bank");
                safetyManager.recordSuccessfulAction();
                botState = BotState.BANKING;
            } else {
                println("Failed to walk to bank");
                safetyManager.recordFailedAction();
                botState = BotState.ERROR_RECOVERY;
            }

            Execution.delay(safetyManager.generateRandomDelay());
        } catch (Exception e) {
            println("Error walking to bank: " + e.getMessage());
            safetyManager.recordFailedAction();
            botState = BotState.ERROR_RECOVERY;
        }
    }

    /**
     * Handle banking state
     */
    private void handleBankingState(LocalPlayer player) {
        try {
            // Record banking action
            patternDetector.recordAction("BANKING", selectedFish.getRawName(), System.currentTimeMillis());

            println("Performing banking operation...");

            if (bankingManager.performBankingOperation()) {
                println("Banking completed successfully");
                safetyManager.recordSuccessfulAction();

                // Check if we got raw fish
                if (hasRawFishInInventory()) {
                    botState = BotState.WALKING_TO_COOKING;
                } else {
                    println("No raw fish obtained from bank, stopping");
                    botState = BotState.STOPPING;
                }
            } else {
                println("Banking failed");
                safetyManager.recordFailedAction();
                botState = BotState.ERROR_RECOVERY;
            }

            Execution.delay(safetyManager.generateRandomDelay());
        } catch (Exception e) {
            println("Error during banking: " + e.getMessage());
            safetyManager.recordFailedAction();
            botState = BotState.ERROR_RECOVERY;
        }
    }

    /**
     * Handle walking to cooking location state
     */
    private void handleWalkingToCookingState(LocalPlayer player) {
        try {
            // Set the location in the manager
            locationManager.setLocation(currentLocation);

            println("Walking to " + currentLocation.getName() + "...");

            if (locationManager.walkToLocation()) {
                println("Arrived at cooking location");
                botState = BotState.COOKING;
            } else {
                println("Failed to walk to cooking location");
                consecutiveFailures++;
                botState = BotState.ERROR_RECOVERY;
            }

            Execution.delay(random.nextLong(1000, 2000));
        } catch (Exception e) {
            println("Error walking to cooking location: " + e.getMessage());
            botState = BotState.ERROR_RECOVERY;
        }
    }

    /**
     * Handle cooking state
     */
    private void handleCookingState(LocalPlayer player) {
        try {
            // Check if it's safe to continue
            if (!actionHandler.isSafeToContinue()) {
                println("Not safe to continue cooking, stopping");
                botState = BotState.STOPPING;
                return;
            }

            // Record action for pattern detection
            patternDetector.recordAction("COOKING", selectedFish.getRawName(), System.currentTimeMillis());

            // Perform cooking cycle
            CookingActionHandler.CookingResult result = actionHandler.performCookingCycle(selectedFish);

            switch (result) {
                case SUCCESS:
                    println("Cooking cycle completed successfully");
                    // Record successful action
                    safetyManager.recordSuccessfulAction();
                    consecutiveFailures = 0;

                    // Check if we need to bank
                    if (!hasRawFishInInventory() || needsToBankFirst()) {
                        botState = BotState.WALKING_TO_BANK;
                    } else {
                        // Continue cooking with small delay
                        Execution.delay(random.nextLong(1000, 3000));
                    }
                    break;

                case NEED_TO_WALK:
                    println("Need to walk to cooking location");
                    botState = BotState.WALKING_TO_COOKING;
                    break;

                case NEED_TO_BANK:
                    println("Need to bank");
                    botState = BotState.WALKING_TO_BANK;
                    break;

                case FAILED:
                    println("Cooking failed");
                    safetyManager.recordFailedAction();
                    consecutiveFailures++;
                    botState = BotState.ERROR_RECOVERY;
                    break;
            }

            // Update statistics
            updateSessionStatistics();

        } catch (Exception e) {
            println("Error during cooking: " + e.getMessage());
            botState = BotState.ERROR_RECOVERY;
        }
    }

    /**
     * Handle interface state
     */
    private void handleInterfaceState(LocalPlayer player) {
        println("Handling cooking interface...");
        Execution.delay(random.nextLong(1000, 2000));
        botState = BotState.COOKING;
    }

    /**
     * Handle error recovery state
     */
    private void handleErrorRecoveryState(LocalPlayer player) {
        consecutiveFailures++;
        println("Error recovery attempt " + consecutiveFailures + "/" + MAX_FAILURES);
        
        if (consecutiveFailures >= MAX_FAILURES) {
            println("Too many consecutive failures, stopping script for safety");
            botState = BotState.STOPPING;
        } else {
            // Try to recover by going back to initialization
            Execution.delay(random.nextLong(5000, 10000));
            botState = BotState.INITIALIZING;
        }
    }

    /**
     * Handle break time state
     */
    private void handleBreakTimeState(LocalPlayer player) {
        if (safetyManager.isBreakComplete()) {
            println("Break completed, resuming cooking");
            botState = BotState.INITIALIZING;
        } else {
            long remainingTime = safetyManager.getRemainingBreakTime();
            println("On break... " + (remainingTime / 1000) + " seconds remaining");
            Execution.delay(Math.min(5000, remainingTime)); // Check every 5 seconds or remaining time
        }
    }

    /**
     * Handle stopping state
     */
    private void handleStoppingState(LocalPlayer player) {
        println("Script stopping gracefully...");
        printSessionStatistics();
        return;
    }

    // Helper methods - emergency stop now handled by SafetyManager

    private boolean needsToBankFirst() {
        return !hasRawFishInInventory() || bankingManager.needsToBank();
    }

    private boolean hasRawFishInInventory() {
        return selectedFish != null && Backpack.contains(selectedFish.getRawItemId());
    }

    /**
     * Update session statistics from action handler
     */
    private void updateSessionStatistics() {
        if (actionHandler != null) {
            fishCooked = actionHandler.getFishCookedThisSession();
            fishBurned = actionHandler.getFishBurnedThisSession();

            // Calculate experience gained (simplified)
            experienceGained = (long) (fishCooked * selectedFish.getExperience());

            // Update statistics manager
            if (statisticsManager != null) {
                statisticsManager.updateExperience(experienceGained, getCurrentCookingLevel());
            }
        }
    }

    private long getCurrentCookingExperience() {
        // Implementation pending - get current cooking XP
        return 0;
    }

    private void updateLastActionTime() {
        lastActionTime = System.currentTimeMillis();
    }

    private void printSessionStatistics() {
        long sessionTime = System.currentTimeMillis() - sessionStartTime;
        long hours = sessionTime / 3600000;
        long minutes = (sessionTime % 3600000) / 60000;
        
        println("=== Session Statistics ===");
        println("Session time: " + hours + "h " + minutes + "m");
        println("Fish cooked: " + fishCooked);
        println("Fish burned: " + fishBurned);
        println("Experience gained: " + experienceGained);
        if (sessionTime > 0) {
            long xpPerHour = (experienceGained * 3600000) / sessionTime;
            println("XP/hour: " + xpPerHour);
        }
    }

    // Getters and setters for UI
    public BotState getBotState() { return botState; }
    public void setBotState(BotState botState) { this.botState = botState; }
    
    public CookingConfig getConfig() { return config; }
    public FishData getSelectedFish() { return selectedFish; }
    public void setSelectedFish(FishData fish) { this.selectedFish = fish; }
    
    public CookingLocation getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(CookingLocation location) { this.currentLocation = location; }
    
    public int getFishCooked() { return fishCooked; }
    public int getFishBurned() { return fishBurned; }
    public long getExperienceGained() { return experienceGained; }
    public long getSessionTime() { return System.currentTimeMillis() - sessionStartTime; }

    public CookingLocationManager getLocationManager() { return locationManager; }
    public CookingActionHandler getActionHandler() { return actionHandler; }
    public BankingManager getBankingManager() { return bankingManager; }
    public SafetyManager getSafetyManager() { return safetyManager; }
    public AntiPatternDetector getPatternDetector() { return patternDetector; }
    public StatisticsManager getStatisticsManager() { return statisticsManager; }
    public SessionLogger getSessionLogger() { return sessionLogger; }
}
