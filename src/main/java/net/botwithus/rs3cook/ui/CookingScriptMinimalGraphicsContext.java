package net.botwithus.rs3cook.ui;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.rs3cook.CookingScriptMinimal;

/**
 * Simple graphics context for the minimal cooking script
 */
public class CookingScriptMinimalGraphicsContext extends ScriptGraphicsContext {

    private CookingScriptMinimal script;

    public CookingScriptMinimalGraphicsContext(ScriptConsole scriptConsole, CookingScriptMinimal script) {
        super(scriptConsole);
        this.script = script;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("RS3 Cooking Script", ImGuiWindowFlag.None.getValue())) {
            ImGui.Text("RS3 Cooking Script - Minimal Version");
            ImGui.Text("Current State: " + script.getBotState().toString());
            
            if (ImGui.Button("Start Cooking")) {
                script.setBotState(CookingScriptMinimal.BotState.COOKING);
            }
            
            if (ImGui.Button("Stop Script")) {
                script.setBotState(CookingScriptMinimal.BotState.IDLE);
            }
            
            if (ImGui.Button("Test Banking")) {
                script.setBotState(CookingScriptMinimal.BotState.BANKING);
            }
            
            ImGui.Text("This is a minimal version for testing.");
            ImGui.Text("Full features will be added once compilation is confirmed.");
            
            ImGui.End();
        }
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
        // No additional overlay panels - settings panel is enough
    }
}
