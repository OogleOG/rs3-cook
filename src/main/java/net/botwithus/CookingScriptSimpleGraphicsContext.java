package net.botwithus;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

/**
 * Simple graphics context for the cooking script
 */
public class CookingScriptSimpleGraphicsContext extends ScriptGraphicsContext {

    private CookingScriptSimple script;

    public CookingScriptSimpleGraphicsContext(ScriptConsole scriptConsole, CookingScriptSimple script) {
        super(scriptConsole);
        this.script = script;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("RS3 Cooking Script", ImGuiWindowFlag.None.getValue())) {
            ImGui.Text("RS3 Comprehensive Cooking Script");
            ImGui.Text("Current State: " + script.getBotState().toString());
            
            if (ImGui.Button("Start Cooking")) {
                script.setBotState(CookingScriptSimple.BotState.COOKING);
            }
            
            if (ImGui.Button("Stop Script")) {
                script.setBotState(CookingScriptSimple.BotState.IDLE);
            }
            
            ImGui.End();
        }
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}
