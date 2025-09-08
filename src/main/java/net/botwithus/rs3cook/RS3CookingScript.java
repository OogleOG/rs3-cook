package net.botwithus.rs3cook;

import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.inventories.Bank;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.input.KeyboardInput;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.awt.event.KeyEvent;

public class RS3CookingScript extends LoopingScript {

    // === CONFIG ===
    private String selectedFish = "Raw bass";
    private String preferredLocation = "";

    private enum BotState {COOKING, BANKING}


    public RS3CookingScript(String name, ScriptConfig cfg, ScriptDefinition def) {
        super(name, cfg, def);
        this.sgc = new RS3CookingScriptGraphicsContext(getConsole(), this);
        println("RS3 Cooking: started.");
    }

    @Override
    public void onLoop() {
        println("=== LOOP START ===");

        if (Client.getGameState() != Client.GameState.LOGGED_IN) {
            println("DEBUG: Not logged in, game state: " + Client.getGameState());
            return;
        }

        if (Client.getLocalPlayer() == null) {
            println("DEBUG: Local player is null");
            return;
        }

        // Check if script is enabled
        if (!enabled) {
            println("DEBUG: Script is disabled, waiting...");
            Execution.delay(1000); // Wait 1 second when disabled
            return;
        }

        println("DEBUG: Script is enabled, computing state...");
        BotState state = computeState();
        println("DEBUG: Current state: " + state + " | Fish: " + selectedFish + " | Location: " + preferredLocation);

        switch (state) {
            case BANKING -> {
                println("DEBUG: Entering banking cycle");
                doBankingCycle();
            }
            case COOKING -> {
                println("DEBUG: Entering cooking cycle");
                doCookingCycle();
            }
        }
        println("=== LOOP END ===");
    }

    private BotState computeState() {
        println("DEBUG: Computing state...");
        boolean hasFish = net.botwithus.rs3.game.inventories.Backpack.contains(selectedFish);
        println("DEBUG: Has " + selectedFish + " in backpack: " + hasFish);

        if (!hasFish) {
            println("DEBUG: No fish found, returning BANKING state");
            return BotState.BANKING;
        }

        println("DEBUG: Fish found, returning COOKING state");
        return BotState.COOKING;
    }

    // -------------------------
    // BANKING
    // -------------------------
    private void doBankingCycle() {
        println("DEBUG: === BANKING CYCLE START ===");
        println("DEBUG: Looking for banker to load preset");

        // Find banker and use "Load Last Preset from" option
        println("DEBUG: Searching for banker with 'Load Last Preset from' option...");

        SceneObject BANK_CHEST = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
        if (BANK_CHEST != null) {
            println("Located a bank chest");
            if (BANK_CHEST.interact("Load Last Preset from")) {
                boolean success = Execution.delayUntil(
                        () -> {
                            boolean hasFish = net.botwithus.rs3.game.inventories.Backpack.contains(selectedFish);
                            if (hasFish) println("DEBUG: Fish detected in backpack!");
                            return hasFish;
                        },
                        () -> {
                            boolean busy = localIsBusy();
                            if (busy) println("DEBUG: Player is busy (moving/animating)");
                            return busy;
                        },
                        5000
                );

                if (success) {
                    println("DEBUG: Preset loaded successfully!");
                } else {
                    println("DEBUG: Preset loading timed out or failed");
                }
            } else {
                println("DEBUG: Failed to interact with banker");
            }
        }
        Npc banker = NpcQuery.newQuery().option("Load Last Preset from").results().nearest();
        if (banker == null) {
            println("DEBUG: No banker with preset option found, searching for any banker...");
            banker = NpcQuery.newQuery().name("Banker").results().nearest();
        }

        if (banker != null && banker.getCoordinate() != null && Client.getLocalPlayer() != null) {
            println("DEBUG: Found banker: " + banker.getName() + " at distance: " + banker.getCoordinate().distanceTo(Client.getLocalPlayer().getCoordinate()));
            println("DEBUG: Attempting to interact with 'Load Last Preset from'...");

            if (banker.interact("Load Last Preset from")) {
                println("DEBUG: Interaction successful, waiting for preset to load...");

                // Wait for the preset to load (inventory to change)
                boolean success = Execution.delayUntil(
                        () -> {
                            boolean hasFish = net.botwithus.rs3.game.inventories.Backpack.contains(selectedFish);
                            if (hasFish) println("DEBUG: Fish detected in backpack!");
                            return hasFish;
                        },
                        () -> {
                            boolean busy = localIsBusy();
                            if (busy) println("DEBUG: Player is busy (moving/animating)");
                            return busy;
                        },
                        5000
                );

                if (success) {
                    println("DEBUG: Preset loaded successfully!");
                } else {
                    println("DEBUG: Preset loading timed out or failed");
                }
            } else {
                println("DEBUG: Failed to interact with banker");
            }
        } else {
            println("DEBUG: No banker found nearby");
        }

        println("DEBUG: === BANKING CYCLE END ===");
    }



    // -------------------------
    // COOKING
    // -------------------------
    private void doCookingCycle() {
        println("DEBUG: === COOKING CYCLE START ===");

        if (!net.botwithus.rs3.game.inventories.Backpack.contains(selectedFish)) {
            println("DEBUG: No fish in backpack, exiting cooking cycle");
            return;
        }

        println("DEBUG: Fish found in backpack, proceeding with cooking");

        if (cookingWidgetOpen()) {
            println("DEBUG: Cooking widget is already open");
            pressCookAll();
            waitWhileCooking();
            return;
        }

        println("DEBUG: Cooking widget not open, looking for cooking object");
        SceneObject cooker = findCookingObject();
        if (cooker == null) {
            println("DEBUG: No cooking object found, exiting");
            return;
        }

        println("DEBUG: Found cooking object: " + cooker.getName());
        String action = cookerAction(cooker);
        println("DEBUG: Using action: " + action);

        if (cooker.interact(action)) {
            println("DEBUG: Interaction successful, waiting for cooking widget or animation...");

            boolean ready = Execution.delayUntil(
                    () -> {
                        boolean widgetOpen = cookingWidgetOpen();
                        boolean animating = isCookingAnimation();
                        if (widgetOpen) println("DEBUG: Cooking widget opened!");
                        if (animating) println("DEBUG: Cooking animation started!");
                        return widgetOpen || animating;
                    },
                    () -> {
                        boolean busy = localIsBusy();
                        if (busy) println("DEBUG: Player is busy during cooking interaction");
                        return busy;
                    },
                    5000
            );

            if (ready && cookingWidgetOpen()) {
                println("DEBUG: Widget ready, pressing cook all");
                pressCookAll();
            } else if (!ready) {
                println("DEBUG: Cooking interaction timed out");
            }

            waitWhileCooking();
        } else {
            println("DEBUG: Failed to interact with cooking object");
        }

        println("DEBUG: === COOKING CYCLE END ===");
    }

    private SceneObject findCookingObject() {
        println("DEBUG: === FINDING COOKING OBJECT ===");
        println("DEBUG: Preferred location: " + preferredLocation);

        if ("Portable range".equalsIgnoreCase(preferredLocation) || "portable".equalsIgnoreCase(preferredLocation)) {
            println("DEBUG: Looking for portable range...");
            SceneObject pr = SceneObjectQuery.newQuery()
                    .option("Cook").name("Portable range").results().nearest();
            if (pr != null) {
                println("DEBUG: Found portable range: " + pr.getName() + " at distance: " + pr.getCoordinate().distanceTo(Client.getLocalPlayer().getCoordinate()));
                return pr;
            } else {
                println("DEBUG: No portable range found");
            }
        }

        println("DEBUG: Looking for bonfire...");
        SceneObject bonfire = SceneObjectQuery.newQuery().name("Bonfire").option("Cook on").results().nearest();
        if (bonfire != null) {
            println("DEBUG: Found bonfire: " + bonfire.getName() + " at distance: " + bonfire.getCoordinate().distanceTo(Client.getLocalPlayer().getCoordinate()));
        } else {
            println("DEBUG: No bonfire found");
        }
        return bonfire;
    }

    private String cookerAction(SceneObject obj) {
        String name = obj.getName() == null ? "" : obj.getName().toLowerCase();
        println("DEBUG: Determining action for object: " + name);

        if (name.contains("portable")) {
            println("DEBUG: Using 'Cook' action for portable range");
            return "Cook";
        }
        if (name.contains("bonfire")) {
            println("DEBUG: Using 'Cook on' action for bonfire");
            return "Cook on";
        }

        println("DEBUG: Using default 'Cook' action");
        return "Cook";
    }

    private boolean cookingWidgetOpen() {
        boolean widget1370 = Interfaces.isOpen(1370);
        boolean widget1251 = Interfaces.isOpen(1251);
        boolean isOpen = widget1370 || widget1251;

        println("DEBUG: Checking cooking widgets - 1370: " + widget1370 + ", 1251: " + widget1251 + ", result: " + isOpen);
        return isOpen;
    }

    private void pressCookAll() {
        println("DEBUG: === PRESSING COOK ALL ===");
        println("DEBUG: Pressing SPACE for Cook All...");
        KeyboardInput.pressKey(KeyEvent.VK_SPACE);
        println("DEBUG: SPACE key pressed, waiting 200ms");
        Execution.delay(200);
        println("DEBUG: Cook All action completed");
    }

    private void waitWhileCooking() {
        println("DEBUG: === WAITING WHILE COOKING ===");

        while (true) {
            println("DEBUG: Starting 63-second cooking wait cycle...");

            // Wait 30 seconds
            Execution.delay(63000 / 600);

            // Check if we're still animating
            boolean stillAnimating = isCookingAnimation();
            println("DEBUG: After 63 seconds - still animating: " + stillAnimating);

            if (stillAnimating) {
                println("DEBUG: Still cooking, resetting 30-second timer...");
                // Continue the loop (reset timer)
            } else {
                println("DEBUG: No longer animating, exiting cooking wait");
                break; // Exit the loop, will go back to banking state
            }
        }

        println("DEBUG: === COOKING WAIT COMPLETED ===");
    }

    private boolean isCookingAnimation() {
        LocalPlayer p = Client.getLocalPlayer();
        if (p == null) {
            println("DEBUG: Player is null in isCookingAnimation");
            return false;
        }

        boolean isMoving = p.isMoving();
        int animationId = p.getAnimationId();
        boolean hasAnimation = animationId != -1;
        boolean isCooking = !isMoving && hasAnimation;

        if (isCooking) {
            println("DEBUG: Player is cooking (animation: " + animationId + ")");
        }

        return isCooking;
    }

    private boolean localIsBusy() {
        LocalPlayer p = Client.getLocalPlayer();
        if (p == null) {
            println("DEBUG: Player is null in localIsBusy");
            return false;
        }

        boolean isMoving = p.isMoving();
        boolean hasAnimation = p.getAnimationId() != -1;
        boolean isBusy = isMoving || hasAnimation;

        if (isBusy) {
            println("DEBUG: Player is busy - moving: " + isMoving + ", animation: " + p.getAnimationId());
        }

        return isBusy;
    }

    // UI bindings
    public void setSelectedFish(String fish) {
        this.selectedFish = fish;
    }

    public void setPreferredLocation(String loc) {
        this.preferredLocation = loc;
    }

    private boolean enabled = true; // Start enabled by default

    // --- Expose config to UI ---
    public String getSelectedFish() { return selectedFish; }

    public String getPreferredLocation() { return preferredLocation; }

    // --- Start/Stop control ---
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getUiStatus() {
        if (!enabled) return "Stopped";
        return "Running";
    }

    @Override
    public void pause() {
        super.pause();
    }
}
