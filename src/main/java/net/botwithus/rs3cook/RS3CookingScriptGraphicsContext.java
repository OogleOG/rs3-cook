package net.botwithus.rs3cook;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.rs3cook.data.CookingTask;

import java.util.List;

public class RS3CookingScriptGraphicsContext extends ScriptGraphicsContext {

    public RS3CookingScript script;

    // UI state variables
    private int selectedFishIndex = 0;
    private int taskQuantity = 100;

    private static final String[] FISH_TYPES = {
        "Raw shrimps", "Raw anchovies", "Raw sardine", "Raw herring",
        "Raw mackerel", "Raw trout", "Raw cod", "Raw pike", "Raw salmon",
        "Raw tuna", "Raw lobster", "Raw bass", "Raw swordfish",
        "Raw monkfish", "Raw karambwan", "Raw shark", "Raw cavefish",
        "Raw rocktail", "Raw sailfish", "Raw blue blubber jellyfish"
    };

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

                // TASK QUEUE TAB
                if (ImGui.BeginTabItem("Task Queue", ImGuiWindowFlag.None.getValue())) {
                    drawTaskQueueTab();
                    ImGui.EndTabItem();
                }

                // SETTINGS TAB
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Oogey Cooking Settings");
                    ImGui.Text("Status: " + script.getUiStatus());
                    ImGui.Separator();

                    if (ImGui.Button("Portable range")) {
                        script.setPreferredLocation("Portable range");
                    }
                    if (ImGui.Button("Bonfire (Priff)")) {
                        script.setPreferredLocation("Bonfire");
                    }

                    ImGui.Text("Current: " + script.getPreferredLocation());
                    ImGui.Separator();

                    if (!script.isEnabled()) {
                        if (ImGui.Button("START COOKING")) script.setEnabled(true);
                    } else {
                        if (ImGui.Button("STOP SCRIPT"))  script.setEnabled(false);
                    }

                    ImGui.EndTabItem();
                }





                // STATISTICS TAB
                if (ImGui.BeginTabItem("Statistics", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Task Progress");
                    ImGui.Separator();

                    List<CookingTask> tasks = script.getCookingTasks();
                    if (tasks.isEmpty()) {
                        ImGui.Text("No tasks added yet");
                    } else {
                        int totalTasks = tasks.size();
                        long completedTasks = tasks.stream().mapToLong(task -> task.isCompleted() ? 1 : 0).sum();

                        ImGui.Text("Tasks: " + completedTasks + "/" + totalTasks);
                        ImGui.Separator();

                        for (CookingTask task : tasks) {
                            String status = task.isCompleted() ? "✓" : "○";
                            ImGui.Text(status + " " + task.toString());

                            // Progress bar for each task
                            float progress = task.getProgressPercent();
                            ImGui.ProgressBar(task.getFishName() + " Progress", progress, 200.0f, 20.0f);
                        }
                    }

                    ImGui.Separator();
                    ImGui.Text("Current Status: " + script.getUiStatus());
                    ImGui.Text("Mode: " + script.getPreferredLocation());

                    ImGui.EndTabItem();
                }

                ImGui.EndTabBar();
            }
        }
        ImGui.End(); // close window
    }

    private void drawTaskQueueTab() {
        ImGui.Text("Cooking Task Queue");
        ImGui.Separator();

        // Current task display
        CookingTask current = script.getCurrentTask();
        if (current != null) {
            ImGui.Text("Current Task: " + current.toString());
            float progress = current.getProgressPercent();
            ImGui.ProgressBar("Progress: " + (int)(progress * 100) + "%", progress, 200.0f, 20.0f);
        } else {
            ImGui.Text("No active task");
        }

        ImGui.Separator();

        // Task list
        ImGui.Text("Queued Tasks:");
        List<CookingTask> tasks = script.getCookingTasks();

        if (tasks.isEmpty()) {
            ImGui.Text("No tasks in queue");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                CookingTask task = tasks.get(i);
                String status = task.isCompleted() ? "[DONE]" : "[PENDING]";
                ImGui.Text(status + " " + task.toString());

                ImGui.SameLine();
                if (ImGui.Button("Remove##" + i)) {
                    script.removeTask(i);
                }
            }
        }

        ImGui.Separator();

        // Add new task
        ImGui.Text("Add New Task:");

        // Fish selection dropdown
        ImGui.Text("Fish Type:");
        selectedFishIndex = ImGui.Combo("##FishType", selectedFishIndex, FISH_TYPES);

        ImGui.Text("Quantity:");
        taskQuantity = ImGui.InputInt("##Quantity", taskQuantity);
        if (taskQuantity < 1) taskQuantity = 1;

        if (ImGui.Button("Add Task")) {
            script.addTask(FISH_TYPES[selectedFishIndex], taskQuantity);
        }

        ImGui.SameLine();
        if (ImGui.Button("Clear All Tasks")) {
            script.clearAllTasks();
        }
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}
