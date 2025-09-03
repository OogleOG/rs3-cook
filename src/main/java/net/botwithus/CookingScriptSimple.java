package net.botwithus;

import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.util.Random;

/**
 * Simplified RS3 Cooking Script for BotWithUs
 * Following the exact pattern of the skeleton script
 */
public class CookingScriptSimple extends LoopingScript {

    private BotState botState = BotState.IDLE;
    private Random random = new Random();

    enum BotState {
        IDLE,
        COOKING,
        BANKING,
        WALKING
    }

    public CookingScriptSimple(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new CookingScriptSimpleGraphicsContext(getConsole(), this);
        println("RS3 Cooking Script loaded successfully!");
    }

    @Override
    public void onLoop() {
        // Default loop delay
        this.loopDelay = random.nextInt(600) + 600;
        
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            Execution.delay(random.nextLong(3000, 7000));
            return;
        }

        switch (botState) {
            case IDLE -> {
                println("RS3 Cooking Script is idle. Use the interface to start cooking!");
                Execution.delay(random.nextLong(2000, 5000));
            }
            case COOKING -> {
                println("Cooking fish...");
                // Simplified cooking logic
                Execution.delay(random.nextLong(3000, 6000));
                
                // Simulate needing to bank after some time
                if (random.nextInt(10) == 0) {
                    botState = BotState.BANKING;
                }
            }
            case BANKING -> {
                println("Banking...");
                // Simplified banking logic
                Execution.delay(random.nextLong(2000, 4000));
                botState = BotState.COOKING;
            }
            case WALKING -> {
                println("Walking...");
                // Simplified walking logic
                Execution.delay(random.nextLong(2000, 4000));
                botState = BotState.COOKING;
            }
        }
    }

    // Getters and setters for UI
    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }
}
