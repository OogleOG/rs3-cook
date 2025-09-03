package net.botwithus.rs3cook;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

public class RS3CookingScriptGraphicsContext extends ScriptGraphicsContext {

    public RS3CookingScript script;

    public RS3CookingScriptGraphicsContext(ScriptConsole scriptConsole, RS3CookingScript script) {
        super(scriptConsole);
        this.script = script;
    }

    @Override
    public void drawSettings() {
        // Use proper BotWithUs ImGui syntax
        if (ImGui.Begin("RS3 Cooking Script", ImGuiWindowFlag.None.getValue())) {
            ImGui.Text("RS3 Cooking Script - UI Test");
            ImGui.Text("Status: " + script.getUiStatus());
            ImGui.Separator();

            if (ImGui.BeginTabBar("CookingTabs", ImGuiWindowFlag.None.getValue())) {

                // SETTINGS TAB
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("RS3 Cooking Script");
                    ImGui.Text("Status: " + script.getUiStatus());
                    ImGui.Separator();

                    ImGui.Text("Selected Fish: " + script.getSelectedFish());
                    ImGui.Text("Cooking Method: " + script.getPreferredLocation());

                    ImGui.Separator();

                    if (!script.isEnabled()) {
                        if (ImGui.Button("START COOKING")) script.setEnabled(true);
                    } else {
                        if (ImGui.Button("STOP SCRIPT"))  script.setEnabled(false);
                    }

                    ImGui.EndTabItem();
                }

                // FISH SELECTION TAB
                if (ImGui.BeginTabItem("Fish Selection", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Select which fish to cook:");
                    ImGui.Separator();

                    ImGui.Text("Low Level");
                    if (ImGui.Button("Raw shrimps"))    script.setSelectedFish("Raw shrimps");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw anchovies"))  script.setSelectedFish("Raw anchovies");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw sardine"))    script.setSelectedFish("Raw sardine");

                    if (ImGui.Button("Raw herring"))    script.setSelectedFish("Raw herring");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw mackerel"))   script.setSelectedFish("Raw mackerel");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw trout"))      script.setSelectedFish("Raw trout");

                    if (ImGui.Button("Raw cod"))        script.setSelectedFish("Raw cod");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw pike"))       script.setSelectedFish("Raw pike");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw salmon"))     script.setSelectedFish("Raw salmon");

                    ImGui.Separator();

                    ImGui.Text("Medium Level");
                    if (ImGui.Button("Raw tuna"))       script.setSelectedFish("Raw tuna");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw lobster"))    script.setSelectedFish("Raw lobster");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw bass"))       script.setSelectedFish("Raw bass");

                    if (ImGui.Button("Raw swordfish"))  script.setSelectedFish("Raw swordfish");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw monkfish"))   script.setSelectedFish("Raw monkfish");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw karambwan"))  script.setSelectedFish("Raw karambwan");

                    ImGui.Separator();

                    ImGui.Text("High Level");
                    if (ImGui.Button("Raw shark"))      script.setSelectedFish("Raw shark");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw cavefish"))   script.setSelectedFish("Raw cavefish");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw rocktail"))   script.setSelectedFish("Raw rocktail");

                    if (ImGui.Button("Raw sailfish"))   script.setSelectedFish("Raw sailfish");
                    ImGui.SameLine();
                    if (ImGui.Button("Raw blue blubber jellyfish"))
                        script.setSelectedFish("Raw blue blubber jellyfish");

                    ImGui.EndTabItem();
                }

                // COOKING METHOD TAB
                if (ImGui.BeginTabItem("Cooking Method", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Select your cooking method:");
                    ImGui.Separator();

                    if (ImGui.Button("Portable range")) {
                        script.setPreferredLocation("Portable range");
                        ImGui.Text("Selected: Portable range");
                    }
                    if (ImGui.Button("Bonfire (Priff)")) {
                        script.setPreferredLocation("Bonfire");
                        ImGui.Text("Selected: Bonfire");
                    }

                    ImGui.Separator();
                    ImGui.Text("Current selection: " + script.getPreferredLocation());

                    ImGui.EndTabItem();
                }

                // STATS TAB (lightweight)
                if (ImGui.BeginTabItem("Statistics", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Session Stats");
                    ImGui.Separator();
                    ImGui.Text("Selected Fish: " + script.getSelectedFish());
                    ImGui.Text("Mode: " + script.getPreferredLocation());
                    ImGui.Text("Running: " + (script.isEnabled() ? "Yes" : "No"));
                    ImGui.Text("Status: " + script.getUiStatus());

                    ImGui.Separator();
                    ImGui.Text("Debug Info:");
                    ImGui.Text("Fish in backpack: " + (net.botwithus.rs3.game.inventories.Backpack.contains(script.getSelectedFish()) ? "Yes" : "No"));
                    ImGui.Text("Bank open: " + (net.botwithus.rs3.game.inventories.Bank.isOpen() ? "Yes" : "No"));

                    ImGui.EndTabItem();
                }

                ImGui.EndTabBar();
            }
        }
        ImGui.End(); // close window
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}
