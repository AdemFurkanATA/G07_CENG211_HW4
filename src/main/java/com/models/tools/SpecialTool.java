package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;
import com.models.boxes.Box;

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

    // ============= CONSTANTS =============
    protected static final int GRID_SIZE = 8;
    protected static final int MIN_INDEX = 0;
    protected static final int MAX_INDEX = 7;
    protected static final int MIN_USER_INPUT = 1;
    protected static final int MAX_USER_INPUT = 8;

    private static final double REGULAR_BOX_EMPTY_CHANCE = 0.25;

    // ============= FIELDS =============
    protected String toolName;
    protected String description;

    /**
     * Constructor for SpecialTool.
     *
     * @param toolName The name of the tool
     * @param description A brief description of the tool's effect
     * @throws IllegalArgumentException if toolName is null or empty
     */
    public SpecialTool(String toolName, String description) {
        if (toolName == null || toolName.isEmpty()) {
            throw new IllegalArgumentException("Tool name cannot be null or empty");
        }
        this.toolName = toolName;
        this.description = description;
    }

    // ============= GETTERS =============

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

    // ============= TEMPLATE METHOD =============

    /**
     * Template method for using the tool.
     * This method is called when a player acquires and uses a tool.
     * It delegates to the abstract execute() method which must be
     * implemented by each specific tool type.
     *
     * @param grid The BoxGrid on which to use the tool
     * @param targetLetter The target letter of the current game
     * @throws Exception if the tool cannot be used
     * @throws IllegalArgumentException if grid or targetLetter is null
     */
    public void useTool(BoxGrid grid, Letter targetLetter) throws Exception {
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null");
        }
        if (targetLetter == null) {
            throw new IllegalArgumentException("Target letter cannot be null");
        }
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

    // ============= HELPER METHODS =============

    /**
     * Common helper method to stamp a single box.
     * Used by stamping tools (PlusShape, MassRow, MassColumn).
     *
     * This method handles:
     * - Boundary checking (silently skips out-of-bounds)
     * - Null box checking
     * - UnchangingBox behavior (handled by Box.stampTopSide internally)
     *
     * @param grid The BoxGrid to modify
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @param letter The letter to stamp
     */
    protected void stampBox(BoxGrid grid, int row, int col, Letter letter) {
        // Validate inputs (silent skip for stamping operations)
        if (grid == null || letter == null) {
            return;
        }

        // Check boundaries (valid for plus shape edge cases)
        if (row < MIN_INDEX || row > MAX_INDEX || col < MIN_INDEX || col > MAX_INDEX) {
            return; // Out of bounds, skip silently
        }

        Box box = grid.getBox(row, col);
        if (box != null) {
            box.stampTopSide(letter);
        }
    }

    /**
     * Parses a location string to extract row and column indices.
     * Accepts formats like "R5-C3", "5-3", "r5-c3", etc.
     *
     * @param location The location string
     * @return An int array [row, col] in 0-7 range, or null if invalid
     */
    protected static int[] parseLocation(String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }

        // Clean and split the input
        String cleaned = location.toUpperCase().replace("R", "").replace("C", "").trim();
        String[] parts = cleaned.split("-");

        if (parts.length != 2) {
            return null;
        }

        try {
            int row = Integer.parseInt(parts[0].trim()) - 1;  // Convert to 0-7
            int col = Integer.parseInt(parts[1].trim()) - 1;  // Convert to 0-7

            if (row >= MIN_INDEX && row <= MAX_INDEX && col >= MIN_INDEX && col <= MAX_INDEX) {
                return new int[]{row, col};
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    /**
     * Validates grid coordinates.
     *
     * @param row The row index
     * @param col The column index
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    protected void validateCoordinates(int row, int col) {
        if (row < MIN_INDEX || row > MAX_INDEX) {
            throw new IllegalArgumentException("Invalid row: " + row + " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")");
        }
        if (col < MIN_INDEX || col > MAX_INDEX) {
            throw new IllegalArgumentException("Invalid column: " + col + " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")");
        }
    }

    /**
     * Formats a location for display (e.g., "R3-C5").
     *
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @return Formatted location string
     */
    protected String formatLocation(int row, int col) {
        return "R" + (row + 1) + "-C" + (col + 1);
    }

    // ============= STRING REPRESENTATION =============

    /**
     * Returns a string representation of this tool.
     *
     * @return The tool name
     */
    @Override
    public String toString() {
        return toolName;
    }

    // ============= RANDOM GENERATION (STATIC FACTORY METHODS) =============

    /**
     * Generates a random SpecialTool for UnchangingBoxes.
     * UnchangingBoxes are GUARANTEED to contain a tool.
     * Each tool has an equal 20% chance.
     *
     * @return A randomly generated SpecialTool (never null)
     */
    public static SpecialTool generateRandomTool() {
        double random = Math.random();

        // Each tool has 20% chance
        if (random < 0.20) {
            return new PlusShapeStamp();
        } else if (random < 0.40) {
            return new MassRowStamp();
        } else if (random < 0.60) {
            return new MassColumnStamp();
        } else if (random < 0.80) {
            return new BoxFlipper();
        } else {
            return new BoxFixer();
        }
    }

    /**
     * Generates a random SpecialTool for RegularBoxes.
     * RegularBoxes have a 75% chance of containing a tool.
     * - 25% chance: empty (returns null)
     * - 75% chance: tool (15% for each of the 5 tools)
     *
     * @return A randomly generated SpecialTool, or null if empty
     */
    public static SpecialTool generateRandomToolForRegularBox() {
        double random = Math.random();

        // 25% chance of being empty
        if (random < REGULAR_BOX_EMPTY_CHANCE) {
            return null;
        }

        // ✅ FIX: Use the SAME random value for tool selection
        // Remaining 75% divided among 5 tools (15% each)
        // Range mapping:
        // [0.25, 0.40) -> PlusShapeStamp (15%)
        // [0.40, 0.55) -> MassRowStamp (15%)
        // [0.55, 0.70) -> MassColumnStamp (15%)
        // [0.70, 0.85) -> BoxFlipper (15%)
        // [0.85, 1.00) -> BoxFixer (15%)

        if (random < 0.40) {
            return new PlusShapeStamp();
        } else if (random < 0.55) {
            return new MassRowStamp();
        } else if (random < 0.70) {
            return new MassColumnStamp();
        } else if (random < 0.85) {
            return new BoxFlipper();
        } else {
            return new BoxFixer();
        }
    }
}