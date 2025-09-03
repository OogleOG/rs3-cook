package net.botwithus.rs3cook.cooking;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3cook.data.CookingLocation;
import net.botwithus.rs3cook.data.FishData;

import java.util.Random;

/**
 * Manages cooking location interactions and pathfinding
 */
public class CookingLocationManager {
    
    private static final Random random = new Random();
    private CookingLocation currentLocation;
    private SceneObject currentCookingObject;
    
    public CookingLocationManager() {
        this.currentLocation = CookingLocation.getDefaultLocation();
    }
    
    /**
     * Set the current cooking location
     */
    public void setLocation(CookingLocation location) {
        this.currentLocation = location;
        this.currentCookingObject = null; // Reset cached object
    }
    
    /**
     * Walk to the current cooking location
     */
    public boolean walkToLocation() {
        if (currentLocation == null) {
            System.out.println("No cooking location set!");
            return false;
        }
        
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) return false;
        
        Coordinate playerCoord = player.getCoordinate();
        Coordinate targetCoord = currentLocation.getCoordinate();
        
        // Check if already at location
        if (isAtLocation()) {
            return true;
        }
        
        System.out.println("Walking to " + currentLocation.getName() + "...");
        Movement.walkTo(targetCoord.getX(), targetCoord.getY(), false);
        return false;
    }
    
    /**
     * Check if player is at the cooking location
     */
    public boolean isAtLocation() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || currentLocation == null) return false;
        
        Coordinate playerCoord = player.getCoordinate();
        Coordinate targetCoord = currentLocation.getCoordinate();
        
        // Consider "at location" if within 5 tiles
        double distance = Math.sqrt(
            Math.pow(playerCoord.getX() - targetCoord.getX(), 2) +
            Math.pow(playerCoord.getY() - targetCoord.getY(), 2)
        );
        
        return distance <= 5.0;
    }
    
    /**
     * Find and interact with cooking object at current location
     */
    public boolean startCooking(FishData fish) {
        if (!isAtLocation()) {
            System.out.println("Not at cooking location!");
            return false;
        }
        
        // Find the cooking object
        SceneObject cookingObject = findCookingObject();
        if (cookingObject == null) {
            System.out.println("Could not find cooking object at " + currentLocation.getName());
            return false;
        }
        
        // Check if we have raw fish
        if (!hasRawFish(fish)) {
            System.out.println("No raw " + fish.getRawName() + " in inventory!");
            return false;
        }
        
        // Use raw fish on cooking object
        return useFishOnObject(fish, cookingObject);
    }
    
    /**
     * Find the appropriate cooking object based on location type
     */
    private SceneObject findCookingObject() {
        if (currentCookingObject != null) {
            return currentCookingObject;
        }
        
        SceneObjectQuery query = SceneObjectQuery.newQuery();
        
        switch (currentLocation.getType()) {
            case RANGE:
                currentCookingObject = query.name("Range", "Cooking range", "Clay oven")
                    .option("Cook")
                    .results()
                    .nearest();
                break;
                
            case FIRE:
                currentCookingObject = query.name("Fire", "Campfire")
                    .option("Cook")
                    .results()
                    .nearest();
                break;
                
            case BONFIRE:
                currentCookingObject = query.name("Bonfire")
                    .option("Cook")
                    .results()
                    .nearest();
                break;
                
            case PORTABLE_RANGE:
                currentCookingObject = query.name("Portable range")
                    .option("Cook")
                    .results()
                    .nearest();
                break;
                
            case SULPHUR_VENT:
                currentCookingObject = query.name("Sulphur vent")
                    .option("Cook")
                    .results()
                    .nearest();
                break;
                
            case IRON_SPIT:
                currentCookingObject = query.name("Iron spit")
                    .option("Cook")
                    .results()
                    .nearest();
                break;
                
            default:
                System.out.println("Unknown cooking location type: " + currentLocation.getType());
                return null;
        }
        
        return currentCookingObject;
    }
    
    /**
     * Check if player has raw fish in inventory
     */
    private boolean hasRawFish(FishData fish) {
        return Backpack.contains(fish.getRawItemId());
    }
    
    /**
     * Use raw fish on cooking object
     */
    private boolean useFishOnObject(FishData fish, SceneObject cookingObject) {
        System.out.println("Using " + fish.getRawName() + " on " + cookingObject.getName());
        
        // First select the raw fish
        if (!Backpack.interact(fish.getRawItemId(), "Use")) {
            System.out.println("Failed to select raw fish");
            return false;
        }
        
        // Small delay for selection
        Execution.delay(random.nextInt(300) + 200);
        
        // Then click on the cooking object
        if (!cookingObject.interact("Use")) {
            System.out.println("Failed to use fish on cooking object");
            return false;
        }
        
        // Wait for cooking interface to appear
        Execution.delay(random.nextInt(1000) + 500);
        return true;
    }
    
    /**
     * Handle cooking interface (Make-X dialog)
     */
    public boolean handleCookingInterface() {
        // This will be implemented when we have access to interface handling
        // For now, just wait and assume it works
        System.out.println("Handling cooking interface...");
        
        // Wait for interface to appear and be handled
        Execution.delay(random.nextInt(2000) + 1000);
        
        return true;
    }
    
    /**
     * Check if currently cooking
     */
    public boolean isCooking() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) return false;
        
        // Check if player is performing cooking animation
        // This would need to check the actual animation ID
        return player.getAnimationId() != -1;
    }
    
    /**
     * Wait for cooking to complete
     */
    public void waitForCookingCompletion() {
        System.out.println("Waiting for cooking to complete...");
        
        // Wait until player stops cooking or inventory changes significantly
        Execution.delayUntil(30000, () -> {
            LocalPlayer player = Client.getLocalPlayer();
            return player == null || !isCooking() || Backpack.isFull();
        });
        
        // Additional small delay for any remaining animations
        Execution.delay(random.nextInt(1000) + 500);
    }
    
    /**
     * Find nearest cooking location to player
     */
    public static CookingLocation findNearestLocation(boolean membersOnly) {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) return CookingLocation.getDefaultLocation();
        
        Coordinate playerCoord = player.getCoordinate();
        return net.botwithus.rs3cook.data.CookingUtils.findNearestLocation(playerCoord, membersOnly);
    }
    
    /**
     * Check if location is accessible (quest requirements, etc.)
     */
    public boolean isLocationAccessible(CookingLocation location) {
        // Basic checks - would need to be expanded with actual quest checking
        if (location.requiresQuest()) {
            System.out.println("Location " + location.getName() + " requires quest: " + location.getQuestRequirement());
            // For now, assume quest requirements are not met
            return false;
        }
        
        return true;
    }
    
    /**
     * Get estimated cooking time for current location
     */
    public long getEstimatedCookingTime(int fishCount) {
        // Base time per fish varies by location type
        double baseTimePerFish = 3.6; // seconds
        
        switch (currentLocation.getType()) {
            case PORTABLE_RANGE:
                baseTimePerFish *= 0.9; // 10% faster
                break;
            case BONFIRE:
                baseTimePerFish *= 1.1; // 10% slower but more XP
                break;
            case FIRE:
                baseTimePerFish *= 1.2; // 20% slower
                break;
            default:
                break;
        }
        
        return Math.round(baseTimePerFish * fishCount * 1000); // Convert to milliseconds
    }
    
    // Getters
    public CookingLocation getCurrentLocation() { return currentLocation; }
    public SceneObject getCurrentCookingObject() { return currentCookingObject; }
}
