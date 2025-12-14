package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;

/**
 * Abstract base class for all SpecialTools in the Box Puzzle game.
 *
 * SpecialTools are acquired by opening boxes during the second stage of each turn.
 * Each tool has different effects on the box grid and must be used immediately
 * after being acquired.
 *
 * There are 5 types of SpecialTools:
 * - PlusShapeStamp: Re-stamps 5 boxes in a plus shape
 * - MassRowStamp: Re-stamps an entire row
 * - MassColumnStamp: Re-stamps an entire column
 * - BoxFlipper: Flips a box upside down
 * - BoxFixer: Converts a box to a FixedBox
 *
 * This class uses the Template Method pattern where useTool() calls
 * the abstract execute() method that must be implemented by subclasses.
 */
public abstract class SpecialTool {

    protected String toolName;          // Name of the tool (e.g., "PlusShapeStamp")
    protected String description;       // Description of what the tool does

    /**
     * Constructor for SpecialTool.
     *
     * @param toolName The name of the tool
     * @param description A brief description of the tool's effect
     */
    public SpecialTool(String toolName, String description) {
        this.toolName = toolName;
        this.description = description;
    }

    /**
     * Gets the name of this tool.
     *
     * @return The tool name
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * Gets the description of this tool.
     *
     * @return The tool description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Template method for using the tool.
     * This method is called when a player acquires and uses a tool.
     * It delegates to the abstract execute() method which must be
     * implemented by each specific tool type.
     *
     * @param grid The BoxGrid on which to use the tool
     * @param targetLetter The target letter of the current game
     * @throws Exception if the tool cannot be used (e.g., invalid target)
     */
    public void useTool(BoxGrid grid, Letter targetLetter) throws Exception {
        // Call the specific implementation in subclasses
        execute(grid, targetLetter);
    }

    /**
     * Abstract method that defines the specific behavior of each tool.
     * Each subclass must implement this method to define how the tool
     * affects the box grid.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter of the current game
     * @throws Exception if the tool execution fails
     */
    protected abstract void execute(BoxGrid grid, Letter targetLetter) throws Exception;

    /**
     * Returns a string representation of this tool.
     *
     * @return The tool name
     */
    @Override
    public String toString() {
        return toolName;
    }

    /**
     * Static method to generate a random SpecialTool.
     * Used when generating box contents at the start of the game.
     *
     * Each tool has an equal 20% chance for UnchangingBoxes,
     * and 15% chance for RegularBoxes (with 25% chance of being empty).
     *
     * @return A randomly generated SpecialTool, or null for empty boxes
     */
    public static SpecialTool generateRandomTool() {
        double random = Math.random();

        // Each tool has 20% chance (0.2 each)
        if (random < 0.2) {
            return new PlusShapeStamp();
        } else if (random < 0.4) {
            return new MassRowStamp();
        } else if (random < 0.6) {
            return new MassColumnStamp();
        } else if (random < 0.8) {
            return new BoxFlipper();
        } else {
            return new BoxFixer();
        }
    }

    /**
     * Static method to generate a random SpecialTool for RegularBoxes.
     * RegularBoxes have a 75% chance of containing a tool (15% each tool).
     *
     * @return A randomly generated SpecialTool, or null if empty (25% chance)
     */
    public static SpecialTool generateRandomToolForRegularBox() {
        double random = Math.random();

        // 25% chance of being empty
        if (random < 0.25) {
            return null;
        }

        // Remaining 75% divided among 5 tools (15% each)
        double toolRandom = Math.random();
        if (toolRandom < 0.2) {
            return new PlusShapeStamp();
        } else if (toolRandom < 0.4) {
            return new MassRowStamp();
        } else if (toolRandom < 0.6) {
            return new MassColumnStamp();
        } else if (toolRandom < 0.8) {
            return new BoxFlipper();
        } else {
            return new BoxFixer();
        }
    }
}