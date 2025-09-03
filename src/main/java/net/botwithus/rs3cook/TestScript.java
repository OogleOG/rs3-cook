package net.botwithus.rs3cook;

import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

/**
 * Simple test script to verify BotWithUs integration
 */
public class TestScript extends LoopingScript {

    public TestScript(String name, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(name, scriptConfig, scriptDefinition);
        println("Test script initialized!");
    }

    @Override
    public void onLoop() {
        println("Test script is running...");
        this.loopDelay = 5000; // 5 second delay
    }
}
