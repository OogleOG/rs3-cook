package net.botwithus.rs3cook.ui;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.rs3cook.CookingScript;
import net.botwithus.rs3cook.data.FishData;
import net.botwithus.rs3cook.data.CookingLocation;
import net.botwithus.rs3cook.data.CookingUtils;
import net.botwithus.rs3cook.banking.BankLocation;
import net.botwithus.rs3cook.cooking.CookingActionHandler;

import java.util.List;

/**
 * Comprehensive ImGui interface for the cooking script
 * Provides fish selection, location preferences, banking options, and statistics
 */
public class CookingScriptGraphicsContext extends ScriptGraphicsContext {

    private CookingScript script;

    // UI state variables
    private String selectedFishName = "Raw shrimps";
    private String selectedLocationName = "Lumbridge Range";
    private String selectedBankName = "Lumbridge Bank";
    private boolean showAdvancedSettings = false;
    private boolean showDebugInfo = false;
    private String fishFilter = "";
    private boolean membersOnly = true;

    public CookingScriptGraphicsContext(ScriptConsole scriptConsole, CookingScript script) {
        super(scriptConsole);
        this.script = script;

        // Initialize with current script values
        if (script.getSelectedFish() != null) {
            selectedFishName = script.getSelectedFish().getRawName();
        }
        if (script.getCurrentLocation() != null) {
            selectedLocationName = script.getCurrentLocation().getName();
        }
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("RS3 Cooking Script", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("CookingTabs", ImGuiWindowFlag.None.getValue())) {

                // Main settings tab
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    drawMainSettingsTab();
                    ImGui.EndTabItem();
                }

                // Fish selection tab
                if (ImGui.BeginTabItem("Fish Selection", ImGuiWindowFlag.None.getValue())) {
                    drawFishSelectionTab();
                    ImGui.EndTabItem();
                }

                // Location settings tab
                if (ImGui.BeginTabItem("Locations", ImGuiWindowFlag.None.getValue())) {
                    drawLocationSettingsTab();
                    ImGui.EndTabItem();
                }

                // Banking settings tab
                if (ImGui.BeginTabItem("Banking", ImGuiWindowFlag.None.getValue())) {
                    drawBankingSettingsTab();
                    ImGui.EndTabItem();
                }
                
                // Statistics tab
                if (ImGui.BeginTabItem("Statistics", ImGuiWindowFlag.None.getValue())) {
                    drawStatisticsTab();
                    ImGui.EndTabItem();
                }

                // Debug tab
                if (ImGui.BeginTabItem("Debug", ImGuiWindowFlag.None.getValue())) {
                    drawDebugTab();
                    ImGui.EndTabItem();
                }
                
                ImGui.EndTabBar();
            }
            ImGui.End();
        }
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();

        // Enhanced overlay showing current state and progress
        if (ImGui.Begin("Cooking Status", ImGuiWindowFlag.NoTitleBar.getValue() |
                       ImGuiWindowFlag.NoResize.getValue() |
                       ImGuiWindowFlag.AlwaysAutoResize.getValue())) {

            // Current state
            ImGui.Text("State: " + script.getBotState().toString());
            ImGui.Text("Fish: " + script.getSelectedFish().getName());
            ImGui.Text("Location: " + script.getCurrentLocation().getName());

            // Progress information
            if (script.getActionHandler() != null) {
                CookingActionHandler.CookingProgress progress = script.getActionHandler().getCookingProgress();
                if (progress.isCooking()) {
                    ImGui.Text("Cooking in progress...");
                    ImGui.Text("Raw fish: " + progress.getRawFishCount());
                    ImGui.Text("Cooked fish: " + progress.getCookedFishCount());

                    if (progress.getEstimatedTimeRemaining() > 0) {
                        String timeStr = CookingUtils.formatTime(progress.getEstimatedTimeRemaining());
                        ImGui.Text("Time remaining: " + timeStr);
                    }
                }
            }

            // Session stats
            long sessionTime = script.getSessionTime();
            if (sessionTime > 0) {
                String timeStr = CookingUtils.formatTime(sessionTime);
                ImGui.Text("Session: " + timeStr);

                if (script.getExperienceGained() > 0) {
                    long xpPerHour = (script.getExperienceGained() * 3600000) / sessionTime;
                    ImGui.Text("XP/hr: " + CookingUtils.formatExperience(xpPerHour));
                }
            }

            ImGui.End();
        }
    }

    /**
     * Draw the main settings tab
     */
    private void drawMainSettingsTab() {
        ImGui.Text("RS3 Comprehensive Cooking Script");
        ImGui.Separator();

        // Current status
        ImGui.Text("Current State: " + script.getBotState().toString());
        ImGui.Text("Selected Fish: " + script.getSelectedFish().getName());
        ImGui.Text("Cooking Location: " + script.getCurrentLocation().getName());

        ImGui.Separator();

        // Quick start/stop controls
        if (ImGui.Button("Start Cooking")) {
            script.setBotState(CookingScript.BotState.INITIALIZING);
        }
        if (ImGui.Button("Stop Script")) {
            script.setBotState(CookingScript.BotState.STOPPING);
        }

        ImGui.Separator();

        // Configuration options
        ImGui.Text("Configuration:");

        boolean useBreaks = script.getConfig().isUseBreaks();
        boolean newUseBreaks = ImGui.Checkbox("Use breaks", useBreaks);
        if (newUseBreaks != useBreaks) {
            script.getConfig().setUseBreaks(newUseBreaks);
        }

        boolean useBankPresets = script.getConfig().isUseBankPresets();
        boolean newUseBankPresets = ImGui.Checkbox("Use bank presets", useBankPresets);
        if (newUseBankPresets != useBankPresets) {
            script.getConfig().setUseBankPresets(newUseBankPresets);
        }

        if (useBankPresets) {
            ImGui.Text("Preset number: " + script.getConfig().getBankPresetNumber());
            // Note: Slider implementation would need the correct BotWithUs API
        }

        boolean stopOnLevelUp = script.getConfig().isStopOnLevelUp();
        boolean newStopOnLevelUp = ImGui.Checkbox("Stop on level up", stopOnLevelUp);
        if (newStopOnLevelUp != stopOnLevelUp) {
            script.getConfig().setStopOnLevelUp(newStopOnLevelUp);
        }
    }

    /**
     * Draw the fish selection tab
     */
    private void drawFishSelectionTab() {
        ImGui.Text("Fish Selection");
        ImGui.Separator();

        // Fish filter - simplified for now
        ImGui.Text("Filter: " + fishFilter);
        // Note: Text input would need the correct BotWithUs API

        // Members only toggle
        boolean newMembersOnly = ImGui.Checkbox("Members only", membersOnly);
        if (newMembersOnly != membersOnly) {
            membersOnly = newMembersOnly;
        }

        ImGui.Separator();

        // Fish list
        List<FishData> availableFish = membersOnly ? FishData.getAllFish() : FishData.getF2PFish();

        // Apply filter
        if (!fishFilter.isEmpty()) {
            availableFish = availableFish.stream()
                    .filter(fish -> fish.getRawName().toLowerCase().contains(fishFilter.toLowerCase()))
                    .toList();
        }

        ImGui.Text("Available Fish:");
        ImGui.Text("Selected: " + selectedFishName);

        // Simplified fish selection - show first few fish as buttons
        int count = 0;
        for (FishData fish : availableFish) {
            if (count >= 10) break; // Limit to first 10 for now

            String displayText = fish.getRawName() + " (Lvl " + fish.getCookingLevel() + ")";
            if (ImGui.Button(displayText)) {
                selectedFishName = fish.getRawName();
                script.setSelectedFish(fish);
            }
            count++;
        }

        // Fish information
        FishData selectedFish = FishData.getFishByName(selectedFishName);
        if (selectedFish != null) {
            ImGui.Separator();
            ImGui.Text("Fish Information:");
            ImGui.Text("Name: " + selectedFish.getRawName());
            ImGui.Text("Cooking Level: " + selectedFish.getCookingLevel());
            ImGui.Text("Experience: " + selectedFish.getExperience());
            ImGui.Text("Bonfire XP: " + selectedFish.getBonfireExperience());
            ImGui.Text("Stop Burning: Level " + selectedFish.getStopBurningLevel());
            ImGui.Text("With Gauntlets: Level " + selectedFish.getStopBurningLevelWithGauntlets());
            ImGui.Text("Heals: " + selectedFish.getHealAmount() + " LP");
            if (selectedFish.isMembersOnly()) {
                ImGui.Text("Members Only: Yes");
            }

            // Experience calculations
            ImGui.Separator();
            ImGui.Text("Calculations:");
            // This would need current cooking level
            int currentLevel = 50; // Placeholder
            long xpPerHour = CookingUtils.calculateExperiencePerHour(
                selectedFish, script.getCurrentLocation(), currentLevel, false);
            ImGui.Text("Estimated XP/hour: " + CookingUtils.formatExperience(xpPerHour));
        }
    }

    /**
     * Draw the location settings tab
     */
    private void drawLocationSettingsTab() {
        ImGui.Text("Cooking Locations");
        ImGui.Separator();

        // Location type filter
        ImGui.Text("Filter by type:");
        if (ImGui.Button("All")) {
            // Show all locations
        }
        if (ImGui.Button("Ranges")) {
            // Filter to ranges only
        }
        if (ImGui.Button("Bonfires")) {
            // Filter to bonfires only
        }
        if (ImGui.Button("F2P Only")) {
            // Filter to F2P locations
        }

        ImGui.Separator();

        // Location list
        List<CookingLocation> availableLocations = membersOnly ?
            CookingLocation.getAllLocations() : CookingLocation.getF2PLocations();

        ImGui.Text("Available Locations:");
        ImGui.Text("Selected: " + selectedLocationName);

        // Simplified location selection - show first few locations as buttons
        int count = 0;
        for (CookingLocation location : availableLocations) {
            if (count >= 8) break; // Limit to first 8 for now

            String displayText = location.getName() + " (" + location.getType().getDisplayName() + ")";
            if (ImGui.Button(displayText)) {
                selectedLocationName = location.getName();
                script.setCurrentLocation(location);
            }
            count++;
        }

        // Location information
        CookingLocation selectedLocation = CookingLocation.getLocationByName(selectedLocationName);
        if (selectedLocation != null) {
            ImGui.Separator();
            ImGui.Text("Location Information:");
            ImGui.Text("Name: " + selectedLocation.getName());
            ImGui.Text("Type: " + selectedLocation.getType().getDisplayName());
            ImGui.Text("Coordinates: " + selectedLocation.getCoordinate().toString());
            ImGui.Text("Nearby Bank: " + selectedLocation.getNearbyBank());
            ImGui.Text("Bank Distance: " + selectedLocation.getBankDistance() + " tiles");

            if (selectedLocation.hasLowerBurnRate()) {
                ImGui.Text("Lower Burn Rate: Yes");
            }
            if (selectedLocation.isMembersOnly()) {
                ImGui.Text("Members Only: Yes");
            }
            if (selectedLocation.requiresQuest()) {
                ImGui.Text("Quest Required: " + selectedLocation.getQuestRequirement());
            }

            if (!selectedLocation.getNotes().isEmpty()) {
                ImGui.Separator();
                ImGui.Text("Notes:");
                ImGui.Text(selectedLocation.getNotes());
            }
        }
    }

    /**
     * Draw the banking settings tab
     */
    private void drawBankingSettingsTab() {
        ImGui.Text("Banking Configuration");
        ImGui.Separator();

        // Banking options
        boolean useNearestBank = script.getConfig().isUseNearestBank();
        boolean newUseNearestBank = ImGui.Checkbox("Use nearest bank", useNearestBank);
        if (newUseNearestBank != useNearestBank) {
            script.getConfig().setUseNearestBank(newUseNearestBank);
        }

        boolean useBankPresets = script.getConfig().isUseBankPresets();
        boolean newUseBankPresets = ImGui.Checkbox("Use bank presets", useBankPresets);
        if (newUseBankPresets != useBankPresets) {
            script.getConfig().setUseBankPresets(newUseBankPresets);
        }

        if (useBankPresets) {
            ImGui.Text("Preset number: " + script.getConfig().getBankPresetNumber());
            ImGui.Text("Make sure your preset contains raw fish!");
        }

        ImGui.Separator();

        // Bank selection (if not using nearest)
        if (!useNearestBank) {
            ImGui.Text("Select Bank:");
            ImGui.Text("Selected: " + selectedBankName);

            List<BankLocation> availableBanks = membersOnly ?
                BankLocation.getAllBanks() : BankLocation.getF2PBanks();

            // Show first few banks as buttons
            int count = 0;
            for (BankLocation bank : availableBanks) {
                if (count >= 6) break; // Limit to first 6 for now

                String displayText = bank.getName();
                if (ImGui.Button(displayText)) {
                    selectedBankName = bank.getName();
                    // Set in banking manager
                    if (script.getBankingManager() != null) {
                        script.getBankingManager().setCurrentBank(bank);
                    }
                }
                count++;
            }
        }

        // Current banking status
        ImGui.Separator();
        ImGui.Text("Banking Status:");
        if (script.getBankingManager() != null) {
            BankLocation currentBank = script.getBankingManager().getCurrentBank();
            if (currentBank != null) {
                ImGui.Text("Current Bank: " + currentBank.getName());
                ImGui.Text("Bank Type: " + currentBank.getType().getDisplayName());

                if (script.getBankingManager().isAtBank()) {
                    ImGui.Text("Status: At bank");
                } else {
                    ImGui.Text("Status: Not at bank");
                }

                long bankingTime = script.getBankingManager().getEstimatedBankingTime();
                ImGui.Text("Est. banking time: " + CookingUtils.formatTime(bankingTime));
            }
        }
    }

    /**
     * Draw the statistics tab
     */
    private void drawStatisticsTab() {
        ImGui.Text("Session Statistics");
        ImGui.Separator();

        // Time statistics
        long sessionTime = script.getSessionTime();
        long hours = sessionTime / 3600000;
        long minutes = (sessionTime % 3600000) / 60000;
        long seconds = (sessionTime % 60000) / 1000;

        ImGui.Text("Session Time: " + hours + "h " + minutes + "m " + seconds + "s");

        // Cooking statistics
        ImGui.Text("Fish Cooked: " + script.getFishCooked());
        ImGui.Text("Fish Burned: " + script.getFishBurned());

        int totalFish = script.getFishCooked() + script.getFishBurned();
        if (totalFish > 0) {
            double successRate = (double) script.getFishCooked() / totalFish * 100;
            ImGui.Text("Success Rate: " + String.format("%.1f%%", successRate));
        }

        // Experience statistics
        ImGui.Text("Experience Gained: " + CookingUtils.formatExperience(script.getExperienceGained()));

        if (sessionTime > 0) {
            long xpPerHour = (script.getExperienceGained() * 3600000) / sessionTime;
            ImGui.Text("XP/Hour: " + CookingUtils.formatExperience(xpPerHour));

            if (totalFish > 0) {
                long fishPerHour = (totalFish * 3600000) / sessionTime;
                ImGui.Text("Fish/Hour: " + fishPerHour);
            }
        }

        ImGui.Separator();

        // Progress information (simplified without progress bars)
        ImGui.Text("Progress:");

        // Session progress (if target is set)
        int targetXP = script.getConfig().getTargetExperience();
        if (targetXP > 0) {
            float progress = (float) script.getExperienceGained() / targetXP * 100;
            ImGui.Text("Target Progress: " + String.format("%.1f%%", Math.min(progress, 100.0f)));
            ImGui.Text("Target: " + CookingUtils.formatExperience(targetXP));
        }

        // Session time limit
        int maxSessionTime = script.getConfig().getMaxSessionTime();
        if (maxSessionTime > 0) {
            float timeProgress = (float) sessionTime / (maxSessionTime * 60000) * 100;
            ImGui.Text("Time Progress: " + String.format("%.1f%%", Math.min(timeProgress, 100.0f)));
            ImGui.Text("Time limit: " + maxSessionTime + " minutes");
        }

        ImGui.Separator();

        // Reset button
        if (ImGui.Button("Reset Statistics")) {
            if (script.getActionHandler() != null) {
                script.getActionHandler().resetStatistics();
            }
        }
    }

    /**
     * Draw the debug tab
     */
    private void drawDebugTab() {
        ImGui.Text("Debug Information");
        ImGui.Separator();

        // Script controls
        ImGui.Text("Script Controls:");
        if (ImGui.Button("Start Script")) {
            script.setBotState(CookingScript.BotState.INITIALIZING);
        }
        if (ImGui.Button("Stop Script")) {
            script.setBotState(CookingScript.BotState.STOPPING);
        }
        if (ImGui.Button("Reset to Idle")) {
            script.setBotState(CookingScript.BotState.IDLE);
        }

        ImGui.Separator();

        // State information
        ImGui.Text("Current State: " + script.getBotState().toString());

        // Location information
        if (script.getLocationManager() != null) {
            ImGui.Text("At cooking location: " + script.getLocationManager().isAtLocation());
            if (script.getLocationManager().getCurrentCookingObject() != null) {
                ImGui.Text("Cooking object: " + script.getLocationManager().getCurrentCookingObject().getName());
            }
        }

        // Banking information
        if (script.getBankingManager() != null) {
            ImGui.Text("At bank: " + script.getBankingManager().isAtBank());
            ImGui.Text("Has raw fish: " + script.getBankingManager().hasEnoughRawFish());
            ImGui.Text("Needs to bank: " + script.getBankingManager().needsToBank());
        }

        // Debug options
        ImGui.Separator();
        ImGui.Text("Debug Options:");

        boolean newShowDebugInfo = ImGui.Checkbox("Show debug info", showDebugInfo);
        if (newShowDebugInfo != showDebugInfo) {
            showDebugInfo = newShowDebugInfo;
            script.getConfig().setShowDebugInfo(showDebugInfo);
        }

        boolean newShowAdvancedSettings = ImGui.Checkbox("Show advanced settings", showAdvancedSettings);
        if (newShowAdvancedSettings != showAdvancedSettings) {
            showAdvancedSettings = newShowAdvancedSettings;
        }

        if (showAdvancedSettings) {
            ImGui.Separator();
            ImGui.Text("Advanced Settings:");

            // Break system settings (simplified display)
            if (script.getConfig().isUseBreaks()) {
                ImGui.Text("Min break time: " + script.getConfig().getMinBreakTime() + "s");
                ImGui.Text("Max break time: " + script.getConfig().getMaxBreakTime() + "s");
                ImGui.Text("Min cooking time: " + script.getConfig().getMinCookingTime() + "s");
                ImGui.Text("Max cooking time: " + script.getConfig().getMaxCookingTime() + "s");
                ImGui.Text("Note: Use config file to modify these values");
            }
        }
    }
}
