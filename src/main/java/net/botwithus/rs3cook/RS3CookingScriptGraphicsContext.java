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
        if (ImGui.Begin("Oogey Cooking", ImGuiWindowFlag.None.getValue())) {
            ImGui.Text("Oogey Cooking");
            ImGui.Text("Status: " + script.getUiStatus());
            ImGui.Separator();

            if (ImGui.BeginTabBar("CookingTabs", ImGuiWindowFlag.None.getValue())) {

                // SETTINGS TAB
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Oogey Cooking");
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
                // FISH SELECTION TAB
                if (ImGui.BeginTabItem("Fish Selection", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Select which fish to cook:");
                    ImGui.Separator();

                    ImGui.Text("Search:");
                    ImGui.InputText("##fishSearch", fishSearch);       // keep your existing binding
                    ImGui.SameLine();
                    ImGui.Checkbox("Show F2P", showF2P);
                    ImGui.SameLine();
                    ImGui.Checkbox("Show Members", showMembers);

                    ImGui.Separator();

                    java.util.List<net.botwithus.rs3cook.data.FishData> all =
                            new java.util.ArrayList<>(net.botwithus.rs3cook.data.FishData.getAllFish());
                    all.sort(java.util.Comparator
                            .comparingInt(net.botwithus.rs3cook.data.FishData::getCookingLevel)
                            .thenComparing(net.botwithus.rs3cook.data.FishData::getRawName));

                    if (ImGui.BeginChild("fishList", 0, 360, true, ImGuiWindowFlag.None.getValue())) {

                        // NOTE: Columns(count, id, border)
                        ImGui.Columns(3, "fishGrid", false);

                        for (var f : all) {
                            boolean isMembers = f.isMembersOnly();
                            if (!showMembers && isMembers) continue;
                            if (!showF2P && !isMembers) continue;

                            String raw = f.getRawName();
                            String cooked = f.getCookedName();

                            if (!fishSearch.isEmpty()) {
                                String s = fishSearch.toLowerCase();
                                if (!raw.toLowerCase().contains(s) && !cooked.toLowerCase().contains(s)) continue;
                            }

                            String label = raw + "##" + cooked; // unique id
                            if (ImGui.Button(label)) {
                                script.setSelectedFish(raw); // always RAW internally
                            }
                            ImGui.Text("Lvl " + f.getCookingLevel() + (isMembers ? " (P2P)" : " (F2P)"));

                            ImGui.NextColumn();
                        }

                        // Reset back to 1 column with the SAME id (must be non-null)
                        ImGui.Columns(1, "fishGrid", false);

                        ImGui.EndChild();
                    }

                    ImGui.Separator();
                    ImGui.Text("Selected: " + script.getSelectedFish());

                    ImGui.EndTabItem();
                }

                // COOKING METHOD TAB
                if (ImGui.BeginTabItem("Cooking Method", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Select your cooking method:");
                    ImGui.Separator();

                    if (ImGui.Button("Range")) {
                        script.setPreferredLocation("Range");
                        ImGui.Text("Selected: Range (Fort Kitchen)");
                    }
                    if (ImGui.Button("Portable range")) {
                        script.setPreferredLocation("Portable range");
                        ImGui.Text("Selected: Portable range");
                    }
                    if (ImGui.Button("Bonfire (Priff)")) {
                        script.setPreferredLocation("Bonfire");
                        ImGui.Text("Selected: Bonfire (Priff)");
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

    // --- UI state ---
    private String fishSearch = "";
    private boolean showMembers = true;
    private boolean showF2P = true;

    // Tiny helpers
    private static String norm(String s){ return s == null ? "" : s.trim().toLowerCase(); }
    private static boolean containsIgnoreCase(String hay, String needle){
        return norm(hay).contains(norm(needle));
    }

}
