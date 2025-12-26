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

    private static final double UNCHANGING_TOOL_EACH_PROBABILITY = 0.20;
    private static final double REGULAR_BOX_TOOL_EACH_PROBABILITY = 0.15;

    // ============= FIELDS =============
    private final String toolName;
    private final String description;

    /**
     * Constructor for SpecialTool.
     *
     * @param toolName The name of the tool (must not be null or empty)
     * @param description A brief description of the tool's effect (can be null)
     * @throws IllegalArgumentException if toolName is null or empty
     */
    public SpecialTool(String toolName, String description) {
        if (toolName == null) {
            throw new IllegalArgumentException("Tool name cannot be null");
        }

        String trimmedName = toolName.trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Tool name cannot be empty or whitespace-only");
        }

        this.toolName = trimmedName;

        this.description = (description != null) ? description.trim() : null;
    }

    // ============= GETTERS =============

    /**
     * Gets the name of this tool.
     *
     * @return The tool name (never null, never empty)
     */
    public String getToolName() {
        return new String(toolName);
    }

    /**
     * Gets the description of this tool.
     *
     * @return The tool description (may be null)
     */
    public String getDescription() {
        return (description != null) ? new String(description) : null;
    }

    // ============= TEMPLATE METHOD =============

    /**
     * Template method for using the tool.
     * This method is called when a player acquires and uses a tool.
     * It delegates to the abstract execute() method which must be
     * implemented by each specific tool type.
     *
     * @param grid The BoxGrid on which to use the tool (must not be null)
     * @param targetLetter The target letter of the current game (must not be null)
     * @throws Exception if the tool cannot be used
     * @throws IllegalArgumentException if grid or targetLetter is null
     */
    public final void useTool(BoxGrid grid, Letter targetLetter) throws Exception {
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null when using tool: " + toolName);
        }

        if (targetLetter == null) {
            throw new IllegalArgumentException("Target letter cannot be null when using tool: " + toolName);
        }

        execute(grid, targetLetter);
    }

    /**
     * Abstract method that defines the specific behavior of each tool.
     * Each subclass must implement this method to define how the tool
     * affects the box grid.
     *
     * @param grid The BoxGrid on which to execute the tool's effect (guaranteed non-null)
     * @param targetLetter The target letter of the current game (guaranteed non-null)
     * @throws Exception if the tool execution fails
     */
    protected abstract void execute(BoxGrid grid, Letter targetLetter) throws Exception;

    // ============= HELPER METHODS =============

    /**
     * Common helper method to stamp a single box.
     * Used by stamping tools (PlusShape, MassRow, MassColumn).
     *
     * This method handles:
     * - Comprehensive null checking
     * - Boundary validation with detailed logging
     * - Safe box access with null checking
     * - UnchangingBox behavior (handled by Box.stampTopSide internally)
     *
     * @param grid The BoxGrid to modify (must not be null)
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @param letter The letter to stamp (must not be null)
     */
    protected final void stampBox(BoxGrid grid, int row, int col, Letter letter) {
        if (grid == null) {
            System.err.println("SECURITY WARNING: stampBox called with null grid");
            return;
        }

        if (letter == null) {
            System.err.println("SECURITY WARNING: stampBox called with null letter");
            return;
        }

        if (row < MIN_INDEX || row > MAX_INDEX) {
            // Silent return for valid use cases (plus shape at edges)
            return;
        }

        if (col < MIN_INDEX || col > MAX_INDEX) {
            // Silent return for valid use cases (plus shape at edges)
            return;
        }

        Box box = null;
        try {
            box = grid.getBox(row, col);
        } catch (Exception e) {
            System.err.println("SECURITY WARNING: Error accessing box at [" + row + "," + col + "]: " + e.getMessage());
            return;
        }

        if (box != null) {
            try {
                box.stampTopSide(letter);
            } catch (Exception e) {
                System.err.println("SECURITY WARNING: Error stamping box at [" + row + "," + col + "]: " + e.getMessage());
            }
        }
    }

    /**
     * Parses a location string to extract row and column indices.
     * Accepts formats like "R5-C3", "5-3", "r5-c3", etc.
     *
     * @param location The location string (can be null)
     * @return An int array [row, col] in 0-7 range, or null if invalid
     */
    protected static int[] parseLocation(String location) {
        if (location == null) {
            return null;
        }

        String trimmed = location.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        if (trimmed.length() > 20) {
            System.err.println("SECURITY WARNING: Location string too long: " + trimmed.length() + " characters");
            return null;
        }

        try {
            String cleaned = trimmed.toUpperCase().replace("R", "").replace("C", "");
            cleaned = cleaned.trim();

            if (cleaned.isEmpty()) {
                return null;
            }

            String[] parts = cleaned.split("-", 3); // Limit to 3 parts max

            if (parts.length != 2) {
                return null;
            }

            String rowStr = parts[0].trim();
            String colStr = parts[1].trim();

            if (rowStr.isEmpty() || colStr.isEmpty()) {
                return null;
            }

            if (rowStr.length() > 10 || colStr.length() > 10) {
                return null;
            }

            int row = Integer.parseInt(rowStr) - 1;  // Convert to 0-7
            int col = Integer.parseInt(colStr) - 1;  // Convert to 0-7

            if (row < Integer.MIN_VALUE + 1 || row > Integer.MAX_VALUE - 1) {
                System.err.println("SECURITY WARNING: Integer overflow detected in row parsing");
                return null;
            }

            if (col < Integer.MIN_VALUE + 1 || col > Integer.MAX_VALUE - 1) {
                System.err.println("SECURITY WARNING: Integer overflow detected in col parsing");
                return null;
            }

            if (row >= MIN_INDEX && row <= MAX_INDEX && col >= MIN_INDEX && col <= MAX_INDEX) {
                return new int[]{row, col};
            }

            // Out of valid range
            return null;

        } catch (NumberFormatException e) {
            return null;
        } catch (Exception e) {
            System.err.println("SECURITY WARNING: Unexpected error in parseLocation: " + e.getClass().getName());
            return null;
        }
    }

    /**
     * Validates grid coordinates.
     *
     * @param row The row index (expected: 0-7)
     * @param col The column index (expected: 0-7)
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    protected final void validateCoordinates(int row, int col) {
        if (row < MIN_INDEX || row > MAX_INDEX) {
            throw new IllegalArgumentException(
                    "Invalid row index: " + row +
                            " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")"
            );
        }

        if (col < MIN_INDEX || col > MAX_INDEX) {
            throw new IllegalArgumentException(
                    "Invalid column index: " + col +
                            " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")"
            );
        }
    }

    /**
     * Formats a location for display (e.g., "R3-C5").
     *
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @return Formatted location string (never null)
     */
    protected final String formatLocation(int row, int col) {
        if (row < MIN_INDEX || row > MAX_INDEX || col < MIN_INDEX || col > MAX_INDEX) {
            return "INVALID_LOCATION[" + row + "," + col + "]";
        }

        return "R" + (row + 1) + "-C" + (col + 1);
    }

    // ============= STRING REPRESENTATION =============

    /**
     * Returns a string representation of this tool.
     *
     * @return The tool name (never null)
     */
    @Override
    public String toString() {
        return new String(toolName);
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
        try {
            double random = Math.random();

            if (random < 0.0 || random >= 1.0) {
                System.err.println("SECURITY WARNING: Random value out of range: " + random);
                random = 0.5; // Fallback to middle value
            }

            if (random < UNCHANGING_TOOL_EACH_PROBABILITY) {
                return new PlusShapeStamp();
            } else if (random < UNCHANGING_TOOL_EACH_PROBABILITY * 2) {
                return new MassRowStamp();
            } else if (random < UNCHANGING_TOOL_EACH_PROBABILITY * 3) {
                return new MassColumnStamp();
            } else if (random < UNCHANGING_TOOL_EACH_PROBABILITY * 4) {
                return new BoxFlipper();
            } else {
                return new BoxFixer();
            }

        } catch (Exception e) {
            System.err.println("SECURITY WARNING: Error in generateRandomTool: " + e.getMessage());
            return new PlusShapeStamp(); // Safe default
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
        try {
            double random = Math.random();

            if (random < 0.0 || random >= 1.0) {
                System.err.println("SECURITY WARNING: Random value out of range: " + random);
                random = 0.5; // Fallback to middle value (will generate a tool)
            }

            // 25% chance of being empty
            if (random < REGULAR_BOX_EMPTY_CHANCE) {
                return null;
            }

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

        } catch (Exception e) {
            System.err.println("SECURITY WARNING: Error in generateRandomToolForRegularBox: " + e.getMessage());
            return null; // Safe default for RegularBox (can be empty)
        }
    }

    // ============= EQUALS AND HASHCODE =============

    /**
     * Checks equality based on tool name.
     *
     * @param obj The object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SpecialTool)) {
            return false;
        }

        SpecialTool other = (SpecialTool) obj;

        return this.toolName != null && this.toolName.equals(other.toolName);
    }

    /**
     * Returns hash code based on tool name.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return (toolName != null) ? toolName.hashCode() : 0;
    }
}