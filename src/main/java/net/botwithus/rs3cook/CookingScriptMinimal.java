package net.botwithus.rs3cook;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.rs3cook.ui.CookingScriptMinimalGraphicsContext;

import java.util.Random;

/**
 * Minimal RS3 Cooking Script to test compilation
 */
public class CookingScriptMinimal extends LoopingScript {

    private BotState botState = BotState.IDLE;
    private Random random = new Random();

    public enum BotState {
        IDLE,
        COOKING,
        BANKING
    }

    public CookingScriptMinimal(String name, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(name, scriptConfig, scriptDefinition);
        this.sgc = new CookingScriptMinimalGraphicsContext(getConsole(), this);
        println("RS3 Cooking Script loaded successfully!");
    }

    @Override
    public void onLoop() {
        this.loopDelay = random.nextInt(600) + 600;
        
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            Execution.delay(random.nextLong(2000, 5000));
            return;
        }

        switch (botState) {
            case IDLE -> {
                println("RS3 Cooking Script is idle. Use the interface to start!");
                Execution.delay(random.nextLong(2000, 5000));
            }
            case COOKING -> {
                println("Cooking...");
                Execution.delay(random.nextLong(3000, 6000));
                
                if (Backpack.isFull()) {
                    botState = BotState.BANKING;
                }
            }
            case BANKING -> {
                println("Banking...");
                Execution.delay(random.nextLong(2000, 4000));
                botState = BotState.COOKING;
            }
        }
    }

    // Getters for UI
    public BotState getBotState() { return botState; }
    public void setBotState(BotState botState) { this.botState = botState; }
}
