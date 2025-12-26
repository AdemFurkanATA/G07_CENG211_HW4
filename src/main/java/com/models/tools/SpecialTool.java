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
 *
 * SECURITY FEATURES:
 * - Comprehensive null checking on all inputs
 * - Defensive copying where appropriate
 * - Bounds validation for all array/grid access
 * - Immutable constants to prevent modification
 * - Protection against integer overflow in calculations
 * - Safe random number generation with validation
 * - Detailed error messages without exposing internal state
 */
public abstract class SpecialTool {

    // ============= CONSTANTS =============
    // SECURITY: All constants are final to prevent modification
    protected static final int GRID_SIZE = 8;
    protected static final int MIN_INDEX = 0;
    protected static final int MAX_INDEX = 7;
    protected static final int MIN_USER_INPUT = 1;
    protected static final int MAX_USER_INPUT = 8;

    private static final double REGULAR_BOX_EMPTY_CHANCE = 0.25;

    // SECURITY: Define probability boundaries as constants
    private static final double UNCHANGING_TOOL_EACH_PROBABILITY = 0.20;
    private static final double REGULAR_BOX_TOOL_EACH_PROBABILITY = 0.15;

    // ============= FIELDS =============
    // SECURITY: Private final fields to prevent modification after construction
    private final String toolName;
    private final String description;

    /**
     * Constructor for SpecialTool.
     * SECURITY: Validates inputs and creates defensive copies
     *
     * @param toolName The name of the tool (must not be null or empty)
     * @param description A brief description of the tool's effect (can be null)
     * @throws IllegalArgumentException if toolName is null or empty
     */
    public SpecialTool(String toolName, String description) {
        // SECURITY: Strict validation - null and empty check
        if (toolName == null) {
            throw new IllegalArgumentException("Tool name cannot be null");
        }

        // SECURITY: Trim to prevent whitespace-only names
        String trimmedName = toolName.trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Tool name cannot be empty or whitespace-only");
        }

        // SECURITY: Defensive copy - store trimmed version
        this.toolName = trimmedName;

        // SECURITY: Description can be null, but if provided, trim it
        this.description = (description != null) ? description.trim() : null;
    }

    // ============= GETTERS =============

    /**
     * Gets the name of this tool.
     * SECURITY: Returns a defensive copy to prevent modification
     *
     * @return The tool name (never null, never empty)
     */
    public String getToolName() {
        // SECURITY: String is immutable in Java, but explicit defensive copy
        return new String(toolName);
    }

    /**
     * Gets the description of this tool.
     * SECURITY: Returns a defensive copy to prevent modification
     *
     * @return The tool description (may be null)
     */
    public String getDescription() {
        // SECURITY: Return defensive copy if not null
        return (description != null) ? new String(description) : null;
    }

    // ============= TEMPLATE METHOD =============

    /**
     * Template method for using the tool.
     * This method is called when a player acquires and uses a tool.
     * It delegates to the abstract execute() method which must be
     * implemented by each specific tool type.
     *
     * SECURITY: Comprehensive input validation before delegation
     *
     * @param grid The BoxGrid on which to use the tool (must not be null)
     * @param targetLetter The target letter of the current game (must not be null)
     * @throws Exception if the tool cannot be used
     * @throws IllegalArgumentException if grid or targetLetter is null
     */
    public final void useTool(BoxGrid grid, Letter targetLetter) throws Exception {
        // SECURITY: Validate grid is not null
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null when using tool: " + toolName);
        }

        // SECURITY: Validate targetLetter is not null
        if (targetLetter == null) {
            throw new IllegalArgumentException("Target letter cannot be null when using tool: " + toolName);
        }

        // SECURITY: Delegate to subclass implementation
        execute(grid, targetLetter);
    }

    /**
     * Abstract method that defines the specific behavior of each tool.
     * Each subclass must implement this method to define how the tool
     * affects the box grid.
     *
     * SECURITY: Subclasses MUST validate all inputs and handle errors properly
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
     * SECURITY: Silent failure for out-of-bounds to prevent information leakage
     *
     * @param grid The BoxGrid to modify (must not be null)
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @param letter The letter to stamp (must not be null)
     */
    protected final void stampBox(BoxGrid grid, int row, int col, Letter letter) {
        // SECURITY: Validate all inputs - fail silently for stamping operations
        if (grid == null) {
            System.err.println("SECURITY WARNING: stampBox called with null grid");
            return;
        }

        if (letter == null) {
            System.err.println("SECURITY WARNING: stampBox called with null letter");
            return;
        }

        // SECURITY: Validate boundaries before accessing grid
        if (row < MIN_INDEX || row > MAX_INDEX) {
            // Silent return for valid use cases (plus shape at edges)
            return;
        }

        if (col < MIN_INDEX || col > MAX_INDEX) {
            // Silent return for valid use cases (plus shape at edges)
            return;
        }

        // SECURITY: Safe box retrieval with null check
        Box box = null;
        try {
            box = grid.getBox(row, col);
        } catch (Exception e) {
            // SECURITY: Catch any unexpected errors from grid access
            System.err.println("SECURITY WARNING: Error accessing box at [" + row + "," + col + "]: " + e.getMessage());
            return;
        }

        // SECURITY: Validate box is not null before stamping
        if (box != null) {
            try {
                box.stampTopSide(letter);
            } catch (Exception e) {
                // SECURITY: Catch any errors from stamping operation
                System.err.println("SECURITY WARNING: Error stamping box at [" + row + "," + col + "]: " + e.getMessage());
            }
        }
    }

    /**
     * Parses a location string to extract row and column indices.
     * Accepts formats like "R5-C3", "5-3", "r5-c3", etc.
     *
     * SECURITY: Comprehensive input validation and sanitization
     *
     * @param location The location string (can be null)
     * @return An int array [row, col] in 0-7 range, or null if invalid
     */
    protected static int[] parseLocation(String location) {
        // SECURITY: Null and empty validation
        if (location == null) {
            return null;
        }

        // SECURITY: Trim whitespace to prevent whitespace attacks
        String trimmed = location.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        // SECURITY: Limit input length to prevent DoS attacks
        if (trimmed.length() > 20) {
            System.err.println("SECURITY WARNING: Location string too long: " + trimmed.length() + " characters");
            return null;
        }

        try {
            // SECURITY: Clean and sanitize input
            String cleaned = trimmed.toUpperCase().replace("R", "").replace("C", "");
            cleaned = cleaned.trim();

            // SECURITY: Validate cleaned string is not empty
            if (cleaned.isEmpty()) {
                return null;
            }

            // SECURITY: Split with limit to prevent excessive array creation
            String[] parts = cleaned.split("-", 3); // Limit to 3 parts max

            if (parts.length != 2) {
                return null;
            }

            // SECURITY: Trim each part and validate not empty
            String rowStr = parts[0].trim();
            String colStr = parts[1].trim();

            if (rowStr.isEmpty() || colStr.isEmpty()) {
                return null;
            }

            // SECURITY: Validate string length before parsing to prevent overflow
            if (rowStr.length() > 10 || colStr.length() > 10) {
                return null;
            }

            // SECURITY: Parse with proper exception handling
            int row = Integer.parseInt(rowStr) - 1;  // Convert to 0-7
            int col = Integer.parseInt(colStr) - 1;  // Convert to 0-7

            // SECURITY: Check for integer overflow/underflow
            if (row < Integer.MIN_VALUE + 1 || row > Integer.MAX_VALUE - 1) {
                System.err.println("SECURITY WARNING: Integer overflow detected in row parsing");
                return null;
            }

            if (col < Integer.MIN_VALUE + 1 || col > Integer.MAX_VALUE - 1) {
                System.err.println("SECURITY WARNING: Integer overflow detected in col parsing");
                return null;
            }

            // SECURITY: Validate range
            if (row >= MIN_INDEX && row <= MAX_INDEX && col >= MIN_INDEX && col <= MAX_INDEX) {
                return new int[]{row, col};
            }

            // Out of valid range
            return null;

        } catch (NumberFormatException e) {
            // SECURITY: Invalid number format - return null without exposing details
            return null;
        } catch (Exception e) {
            // SECURITY: Catch any unexpected errors
            System.err.println("SECURITY WARNING: Unexpected error in parseLocation: " + e.getClass().getName());
            return null;
        }
    }

    /**
     * Validates grid coordinates.
     * SECURITY: Comprehensive bounds checking with detailed error messages
     *
     * @param row The row index (expected: 0-7)
     * @param col The column index (expected: 0-7)
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    protected final void validateCoordinates(int row, int col) {
        // SECURITY: Validate row bounds
        if (row < MIN_INDEX || row > MAX_INDEX) {
            throw new IllegalArgumentException(
                    "Invalid row index: " + row +
                            " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")"
            );
        }

        // SECURITY: Validate column bounds
        if (col < MIN_INDEX || col > MAX_INDEX) {
            throw new IllegalArgumentException(
                    "Invalid column index: " + col +
                            " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")"
            );
        }
    }

    /**
     * Formats a location for display (e.g., "R3-C5").
     * SECURITY: Safe formatting with bounds validation
     *
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @return Formatted location string (never null)
     */
    protected final String formatLocation(int row, int col) {
        // SECURITY: Validate inputs before formatting
        if (row < MIN_INDEX || row > MAX_INDEX || col < MIN_INDEX || col > MAX_INDEX) {
            return "INVALID_LOCATION[" + row + "," + col + "]";
        }

        // SECURITY: Safe formatting with validated inputs
        return "R" + (row + 1) + "-C" + (col + 1);
    }

    // ============= STRING REPRESENTATION =============

    /**
     * Returns a string representation of this tool.
     * SECURITY: Returns defensive copy
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
     * SECURITY: Safe random generation with validation and fallback
     *
     * @return A randomly generated SpecialTool (never null)
     */
    public static SpecialTool generateRandomTool() {
        try {
            // SECURITY: Use Math.random() which is thread-safe
            double random = Math.random();

            // SECURITY: Validate random value is in expected range
            if (random < 0.0 || random >= 1.0) {
                System.err.println("SECURITY WARNING: Random value out of range: " + random);
                random = 0.5; // Fallback to middle value
            }

            // SECURITY: Each tool has 20% chance (0.20)
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
            // SECURITY: Fallback to default tool if anything goes wrong
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
     * SECURITY: Safe random generation with validation and fallback
     *
     * @return A randomly generated SpecialTool, or null if empty
     */
    public static SpecialTool generateRandomToolForRegularBox() {
        try {
            // SECURITY: Use Math.random() which is thread-safe
            double random = Math.random();

            // SECURITY: Validate random value is in expected range
            if (random < 0.0 || random >= 1.0) {
                System.err.println("SECURITY WARNING: Random value out of range: " + random);
                random = 0.5; // Fallback to middle value (will generate a tool)
            }

            // 25% chance of being empty
            if (random < REGULAR_BOX_EMPTY_CHANCE) {
                return null;
            }

            // SECURITY: Use the SAME random value for tool selection
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
            // SECURITY: Fallback behavior - return null for error cases
            System.err.println("SECURITY WARNING: Error in generateRandomToolForRegularBox: " + e.getMessage());
            return null; // Safe default for RegularBox (can be empty)
        }
    }

    // ============= EQUALS AND HASHCODE =============

    /**
     * Checks equality based on tool name.
     * SECURITY: Null-safe comparison
     *
     * @param obj The object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        // SECURITY: Null check
        if (obj == null) {
            return false;
        }

        // SECURITY: Same reference check
        if (this == obj) {
            return true;
        }

        // SECURITY: Type check
        if (!(obj instanceof SpecialTool)) {
            return false;
        }

        SpecialTool other = (SpecialTool) obj;

        // SECURITY: Null-safe string comparison
        return this.toolName != null && this.toolName.equals(other.toolName);
    }

    /**
     * Returns hash code based on tool name.
     * SECURITY: Consistent with equals()
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        // SECURITY: Null-safe hash code
        return (toolName != null) ? toolName.hashCode() : 0;
    }
}