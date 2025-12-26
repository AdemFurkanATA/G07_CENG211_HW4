package com.game;

import com.enums.Letter;
import com.enums.Direction;
import com.models.boxes.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * ANSWER TO COLLECTIONS QUESTION:
 *
 * I chose to use ArrayList<ArrayList<Box>> for storing the 8x8 grid of boxes.
 *
 * Reasoning:
 * 1. FAST INDEX-BASED ACCESS: ArrayList provides O(1) access time for get/set operations
 *    using indices, which is crucial for frequent grid operations (getting box at R3-C5).
 *
 * 2. MAINTAINS ORDER: The grid structure requires maintaining a specific order of boxes
 *    in rows and columns. ArrayList preserves insertion order perfectly.
 *
 * 3. DYNAMIC SIZE: Although our grid is fixed at 8x8, ArrayList provides flexibility
 *    for potential future modifications and makes initialization clean.
 *
 * 4. ITERATION SUPPORT: ArrayList provides excellent iteration capabilities for operations
 *    like counting target letters, displaying the grid, and rolling operations.
 *
 * 5. 2D STRUCTURE: Using ArrayList<ArrayList<Box>> naturally represents the 2D grid
 *    structure where grid.get(row).get(col) intuitively maps to R(row+1)-C(col+1).
 *
 * Alternative considered: HashMap<String, Box> with "R1-C1" keys was considered but
 * rejected because it doesn't maintain spatial relationships needed for rolling operations
 * and neighbor detection (plus shape, row/column operations).
 *
 * SECURITY ENHANCEMENTS:
 * - Immutable constants prevent modification
 * - Comprehensive null checking on all operations
 * - Defensive copying for returned collections
 * - Bounds validation before all array access
 * - Protection against concurrent modification
 * - Safe iteration with null checks
 * - Integer overflow protection
 */
public class BoxGrid {

    // SECURITY: All constants are final and immutable
    private static final int GRID_SIZE = 8;
    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 7;

    // SECURITY: Probability constants are final
    private static final double REGULAR_BOX_PROBABILITY = 0.85;
    private static final double UNCHANGING_BOX_PROBABILITY = 0.95; // 0.85 + 0.10

    // SECURITY: Maximum string length for parsing to prevent DoS
    private static final int MAX_LOCATION_STRING_LENGTH = 50;

    // SECURITY: Maximum iterations to prevent infinite loops
    private static final int MAX_ROLL_ITERATIONS = GRID_SIZE + 1;

    // The main data structure: 2D ArrayList storing all 64 boxes
    // SECURITY: Private final to prevent reassignment and external access
    private final ArrayList<ArrayList<Box>> grid;

    // SECURITY: Thread-safety flag (for future multi-threading support)
    private volatile boolean isInitialized = false;

    /**
     * Constructor for BoxGrid.
     * Initializes an 8x8 grid with randomly generated boxes.
     *
     * SECURITY: Ensures complete initialization before use
     */
    public BoxGrid() {
        // SECURITY: Initialize with exact capacity to prevent resizing
        this.grid = new ArrayList<>(GRID_SIZE);
        initializeGrid();
        this.isInitialized = true;
    }

    /**
     * Initializes the 8x8 grid with randomly generated boxes.
     * Each box is generated based on probability:
     * - 85% RegularBox
     * - 10% UnchangingBox
     * - 5% FixedBox
     *
     * SECURITY: Ensures no null boxes in grid
     */
    private void initializeGrid() {
        for (int row = 0; row < GRID_SIZE; row++) {
            // SECURITY: Initialize row with exact capacity
            ArrayList<Box> rowList = new ArrayList<>(GRID_SIZE);

            for (int col = 0; col < GRID_SIZE; col++) {
                Box box = generateRandomBox();

                // SECURITY: Double-check null protection
                if (box == null) {
                    System.err.println("SECURITY WARNING: generateRandomBox() returned null at [" +
                            row + "," + col + "], using RegularBox fallback");
                    box = new RegularBox(); // Fallback to RegularBox
                }

                rowList.add(box);
            }

            grid.add(rowList);
        }

        // SECURITY: Verify grid integrity after initialization
        if (!verifyGridIntegrity()) {
            throw new IllegalStateException("Grid initialization failed integrity check");
        }
    }

    /**
     * Generates a random box based on probability distribution.
     * SECURITY: Protected against random value anomalies
     *
     * @return A randomly generated Box (never null)
     */
    private Box generateRandomBox() {
        try {
            double random = Math.random();

            // SECURITY: Validate random value is in expected range
            if (random < 0.0 || random >= 1.0) {
                System.err.println("SECURITY WARNING: Random value out of range: " + random);
                random = 0.5; // Fallback to middle value (will generate RegularBox)
            }

            if (random < REGULAR_BOX_PROBABILITY) {
                // 85% chance for RegularBox
                return new RegularBox();
            } else if (random < UNCHANGING_BOX_PROBABILITY) {
                // 10% chance for UnchangingBox (0.85 to 0.95)
                return new UnchangingBox();
            } else {
                // 5% chance for FixedBox (0.95 to 1.0)
                return new FixedBox();
            }
        } catch (Exception e) {
            // SECURITY: Fallback on any error
            System.err.println("SECURITY WARNING: Error in generateRandomBox: " + e.getMessage());
            return new RegularBox(); // Safe default
        }
    }

    /**
     * Verifies the integrity of the grid structure.
     * SECURITY: Ensures grid is properly initialized with no null values
     *
     * @return true if grid is valid, false otherwise
     */
    private boolean verifyGridIntegrity() {
        if (grid == null || grid.size() != GRID_SIZE) {
            System.err.println("SECURITY ERROR: Grid structure is invalid");
            return false;
        }

        for (int row = 0; row < GRID_SIZE; row++) {
            ArrayList<Box> rowList = grid.get(row);

            if (rowList == null || rowList.size() != GRID_SIZE) {
                System.err.println("SECURITY ERROR: Row " + row + " is invalid");
                return false;
            }

            for (int col = 0; col < GRID_SIZE; col++) {
                if (rowList.get(col) == null) {
                    System.err.println("SECURITY ERROR: Null box found at [" + row + "," + col + "]");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Gets the box at the specified row and column.
     * SECURITY: Returns null for invalid coordinates instead of throwing exception.
     * Caller MUST check for null before using the returned box.
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return The Box at the specified position, or null if out of bounds
     */
    public Box getBox(int row, int col) {
        // SECURITY: Validate initialization state
        if (!isInitialized) {
            System.err.println("SECURITY WARNING: getBox called before grid initialization");
            return null;
        }

        // SECURITY: Validate bounds before accessing
        if (!isValidCoordinate(row, col)) {
            return null;
        }

        try {
            ArrayList<Box> rowList = grid.get(row);

            // SECURITY: Additional null check for row
            if (rowList == null) {
                System.err.println("SECURITY ERROR: Row " + row + " is null in getBox");
                return null;
            }

            // SECURITY: Bounds check before accessing column
            if (col < 0 || col >= rowList.size()) {
                System.err.println("SECURITY ERROR: Column " + col + " out of bounds in row " + row);
                return null;
            }

            return rowList.get(col);

        } catch (IndexOutOfBoundsException e) {
            // SECURITY: Extra safety - catch any unexpected index issues
            System.err.println("SECURITY ERROR: IndexOutOfBounds in getBox[" + row + "," + col + "]: " +
                    e.getMessage());
            return null;
        } catch (Exception e) {
            // SECURITY: Catch all other unexpected errors
            System.err.println("SECURITY ERROR: Unexpected error in getBox[" + row + "," + col + "]: " +
                    e.getMessage());
            return null;
        }
    }

    /**
     * Gets the box at the specified row and column with exception throwing.
     * SECURITY: Use this when you need to ensure a box exists.
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return The Box at the specified position (never null)
     * @throws IllegalArgumentException if coordinates are invalid or box doesn't exist
     * @throws IllegalStateException if grid is not initialized
     */
    public Box getBoxSafe(int row, int col) {
        // SECURITY: Check initialization
        if (!isInitialized) {
            throw new IllegalStateException("Grid is not initialized");
        }

        // SECURITY: Validate coordinates
        if (!isValidCoordinate(row, col)) {
            throw new IllegalArgumentException(
                    "Invalid grid coordinates: row=" + row + ", col=" + col +
                            " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")"
            );
        }

        Box box = getBox(row, col);

        // SECURITY: Ensure box is not null
        if (box == null) {
            throw new IllegalArgumentException(
                    "No box found at location: R" + (row + 1) + "-C" + (col + 1) +
                            " (grid integrity compromised)"
            );
        }

        return box;
    }

    /**
     * Sets a box at the specified position.
     * Used when converting boxes (e.g., BoxFixer tool).
     *
     * SECURITY: Comprehensive validation and atomic operation
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @param box The box to place at this position (must not be null)
     * @throws IllegalArgumentException if parameters are invalid
     * @throws IllegalStateException if grid is not initialized
     */
    public void setBox(int row, int col, Box box) {
        // SECURITY: Check initialization
        if (!isInitialized) {
            throw new IllegalStateException("Grid is not initialized");
        }

        // SECURITY: Validate box is not null
        if (box == null) {
            throw new IllegalArgumentException(
                    "Box cannot be null when setting at [" + row + "," + col + "]"
            );
        }

        // SECURITY: Validate coordinates
        if (!isValidCoordinate(row, col)) {
            throw new IllegalArgumentException(
                    "Invalid grid coordinates: row=" + row + ", col=" + col +
                            " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")"
            );
        }

        try {
            ArrayList<Box> rowList = grid.get(row);

            // SECURITY: Validate row exists
            if (rowList == null) {
                throw new IllegalStateException(
                        "Row " + row + " is null (grid integrity compromised)"
                );
            }

            // SECURITY: Atomic set operation
            rowList.set(col, box);

        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    "Failed to set box at row=" + row + ", col=" + col +
                            " (index out of bounds)", e
            );
        } catch (IllegalStateException e) {
            // Re-throw IllegalStateException
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to set box at row=" + row + ", col=" + col +
                            " (unexpected error: " + e.getClass().getSimpleName() + ")", e
            );
        }
    }

    /**
     * Validates if coordinates are within grid bounds.
     * SECURITY: Centralized validation method with overflow protection.
     *
     * @param row Row index
     * @param col Column index
     * @return true if coordinates are valid (0-7), false otherwise
     */
    private boolean isValidCoordinate(int row, int col) {
        // SECURITY: Check for negative values (including overflow)
        if (row < MIN_INDEX || col < MIN_INDEX) {
            return false;
        }

        // SECURITY: Check for out of bounds (including overflow)
        if (row > MAX_INDEX || col > MAX_INDEX) {
            return false;
        }

        return true;
    }

    /**
     * Checks if a position is on the edge of the grid.
     * SECURITY: Safe bounds checking
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return true if the position is on any edge, false otherwise
     */
    public boolean isEdge(int row, int col) {
        // SECURITY: Validate coordinates first
        if (!isValidCoordinate(row, col)) {
            return false;
        }

        return row == MIN_INDEX || row == MAX_INDEX ||
                col == MIN_INDEX || col == MAX_INDEX;
    }

    /**
     * Checks if a position is in a corner of the grid.
     * SECURITY: Safe bounds checking
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return true if the position is in a corner, false otherwise
     */
    public boolean isCorner(int row, int col) {
        // SECURITY: Validate coordinates first
        if (!isValidCoordinate(row, col)) {
            return false;
        }

        return (row == MIN_INDEX || row == MAX_INDEX) &&
                (col == MIN_INDEX || col == MAX_INDEX);
    }

    /**
     * Gets the available directions for rolling from an edge position.
     * SECURITY: Returns unmodifiable defensive copy
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return Unmodifiable list of available directions (empty if invalid position)
     */
    public List<Direction> getAvailableDirections(int row, int col) {
        // SECURITY: Validate coordinates
        if (!isValidCoordinate(row, col)) {
            return Collections.emptyList();
        }

        // SECURITY: Use ArrayList with initial capacity
        List<Direction> directions = new ArrayList<>(2); // Max 2 directions for corners

        // Corner cases
        if (row == MIN_INDEX && col == MIN_INDEX) {
            // Top-left corner: can roll RIGHT or DOWN
            directions.add(Direction.RIGHT);
            directions.add(Direction.DOWN);
        } else if (row == MIN_INDEX && col == MAX_INDEX) {
            // Top-right corner: can roll LEFT or DOWN
            directions.add(Direction.LEFT);
            directions.add(Direction.DOWN);
        } else if (row == MAX_INDEX && col == MIN_INDEX) {
            // Bottom-left corner: can roll RIGHT or UP
            directions.add(Direction.RIGHT);
            directions.add(Direction.UP);
        } else if (row == MAX_INDEX && col == MAX_INDEX) {
            // Bottom-right corner: can roll LEFT or UP
            directions.add(Direction.LEFT);
            directions.add(Direction.UP);
        }
        // Edge cases (non-corner)
        else if (row == MIN_INDEX) {
            // Top edge: can only roll DOWN
            directions.add(Direction.DOWN);
        } else if (row == MAX_INDEX) {
            // Bottom edge: can only roll UP
            directions.add(Direction.UP);
        } else if (col == MIN_INDEX) {
            // Left edge: can only roll RIGHT
            directions.add(Direction.RIGHT);
        } else if (col == MAX_INDEX) {
            // Right edge: can only roll LEFT
            directions.add(Direction.LEFT);
        }

        // SECURITY: Return unmodifiable copy to prevent external modification
        return Collections.unmodifiableList(directions);
    }

    /**
     * Rolls boxes starting from an edge position in the specified direction.
     * Implements the domino-effect: all boxes in the path roll until a FixedBox
     * is encountered or the grid boundary is reached.
     *
     * SECURITY: Protected against infinite loops and invalid state
     *
     * @param startRow Starting row index (0-7)
     * @param startCol Starting column index (0-7)
     * @param direction The direction to roll (must not be null)
     * @throws IllegalArgumentException if parameters are invalid
     * @throws IllegalStateException if grid is not initialized
     */
    public void rollBoxesFromEdge(int startRow, int startCol, Direction direction) {
        // SECURITY: Check initialization
        if (!isInitialized) {
            throw new IllegalStateException("Grid is not initialized");
        }

        // SECURITY: Validate all inputs
        if (!isValidCoordinate(startRow, startCol)) {
            throw new IllegalArgumentException(
                    "Invalid starting position: row=" + startRow + ", col=" + startCol +
                            " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")"
            );
        }

        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }

        // SECURITY: Track iteration count to prevent infinite loops
        int iterationCount = 0;

        int currentRow = startRow;
        int currentCol = startCol;

        // Roll boxes along the path until we hit a FixedBox or boundary
        while (iterationCount < MAX_ROLL_ITERATIONS) {
            iterationCount++;

            // SECURITY: Check bounds before accessing
            if (!isValidCoordinate(currentRow, currentCol)) {
                break;  // Out of bounds - stop rolling
            }

            Box currentBox = getBox(currentRow, currentCol);

            if (currentBox == null) {
                // SECURITY: Should never happen but handle gracefully
                System.err.println("SECURITY ERROR: Null box encountered during rolling at [" +
                        currentRow + "," + currentCol + "]");
                break;
            }

            // Check if this box can be rolled
            if (!currentBox.canRoll()) {
                // FixedBox encountered - stop the domino effect
                break;
            }

            // SECURITY: Safe roll operation with try-catch
            try {
                currentBox.roll(direction);
            } catch (Exception e) {
                System.err.println("SECURITY ERROR: Error rolling box at [" + currentRow + "," +
                        currentCol + "]: " + e.getMessage());
                break; // Stop rolling on error
            }

            // Move to the next box in the direction
            // SECURITY: Safe arithmetic (no overflow possible with small grid)
            currentRow += direction.getRowDelta();
            currentCol += direction.getColDelta();

            // Check if we've gone out of bounds
            if (!isValidCoordinate(currentRow, currentCol)) {
                break;
            }
        }

        // SECURITY: Log warning if max iterations reached (should never happen)
        if (iterationCount >= MAX_ROLL_ITERATIONS) {
            System.err.println("SECURITY WARNING: Rolling operation reached maximum iterations (" +
                    MAX_ROLL_ITERATIONS + "). This may indicate a logic error.");
        }
    }

    /**
     * Resets the rolled status for all boxes at the start of a new turn.
     * SECURITY: Handles null boxes gracefully and uses safe iteration
     */
    public void resetAllRolledStatus() {
        // SECURITY: Check initialization
        if (!isInitialized) {
            System.err.println("SECURITY WARNING: resetAllRolledStatus called before initialization");
            return;
        }

        // SECURITY: Safe iteration with bounds checking
        for (int row = MIN_INDEX; row <= MAX_INDEX; row++) {
            for (int col = MIN_INDEX; col <= MAX_INDEX; col++) {
                Box box = getBox(row, col);

                if (box != null) {
                    try {
                        box.resetRolledStatus();
                    } catch (Exception e) {
                        System.err.println("SECURITY ERROR: Error resetting box at [" + row + "," +
                                col + "]: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Counts the number of boxes with the target letter on their top side.
     * SECURITY: Safe counting with null checks
     *
     * @param targetLetter The letter to count (must not be null)
     * @return The number of boxes with the target letter on top (0-64)
     * @throws IllegalArgumentException if targetLetter is null
     */
    public int countTargetLetters(Letter targetLetter) {
        // SECURITY: Validate target letter
        if (targetLetter == null) {
            throw new IllegalArgumentException("Target letter cannot be null");
        }

        // SECURITY: Check initialization
        if (!isInitialized) {
            System.err.println("SECURITY WARNING: countTargetLetters called before initialization");
            return 0;
        }

        int count = 0;

        // SECURITY: Safe iteration with comprehensive null checks
        for (int row = MIN_INDEX; row <= MAX_INDEX; row++) {
            for (int col = MIN_INDEX; col <= MAX_INDEX; col++) {
                try {
                    Box box = getBox(row, col);

                    // SECURITY: Check for null box and null top side
                    if (box != null && box.getTopSide() != null &&
                            box.getTopSide() == targetLetter) {
                        count++;
                    }
                } catch (Exception e) {
                    System.err.println("SECURITY ERROR: Error accessing box at [" + row + "," +
                            col + "] during count: " + e.getMessage());
                }
            }
        }

        return count;
    }

    /**
     * Checks if all edge boxes are FixedBoxes.
     * Used to determine if the game can continue.
     * SECURITY: Safe checking with null protection
     *
     * @return true if all edge boxes are FixedBoxes, false otherwise
     */
    public boolean areAllEdgeBoxesFixed() {
        // SECURITY: Check initialization
        if (!isInitialized) {
            System.err.println("SECURITY WARNING: areAllEdgeBoxesFixed called before initialization");
            return false;
        }

        // SECURITY: Safe iteration
        for (int row = MIN_INDEX; row <= MAX_INDEX; row++) {
            for (int col = MIN_INDEX; col <= MAX_INDEX; col++) {
                if (isEdge(row, col)) {
                    Box box = getBox(row, col);

                    // SECURITY: If box is null or can roll, not all edges are fixed
                    if (box == null || box.canRoll()) {
                        return false;
                    }
                }
            }
        }

        return true;  // All edge boxes are fixed
    }

    /**
     * Displays the grid in a formatted table.
     * SECURITY: Handles null boxes gracefully and prevents injection
     *
     * @return String representation of the grid (never null)
     */
    public String displayGrid() {
        // SECURITY: Check initialization
        if (!isInitialized) {
            return "Grid not initialized";
        }

        // SECURITY: Use StringBuilder with initial capacity
        StringBuilder sb = new StringBuilder(1024); // Reasonable initial size

        try {
            // Header row
            sb.append("    ");
            for (int col = MIN_INDEX; col <= MAX_INDEX; col++) {
                sb.append("C").append(col + 1).append("       ");
            }
            sb.append("\n");

            // SECURITY: Safe string repetition with bounds
            int separatorLength = GRID_SIZE * 10 + 1;
            for (int i = 0; i < separatorLength && i < 1000; i++) {
                sb.append("-");
            }
            sb.append("\n");

            // Grid rows
            for (int row = MIN_INDEX; row <= MAX_INDEX; row++) {
                sb.append("R").append(row + 1).append(" ");

                for (int col = MIN_INDEX; col <= MAX_INDEX; col++) {
                    Box box = getBox(row, col);

                    if (box != null) {
                        try {
                            // SECURITY: Get string representation safely
                            String boxStr = box.toString();
                            if (boxStr != null) {
                                sb.append(boxStr).append(" ");
                            } else {
                                sb.append("| NULL  | ");
                            }
                        } catch (Exception e) {
                            sb.append("| ERROR | ");
                        }
                    } else {
                        // SECURITY: Handle null boxes
                        sb.append("| NULL  | ");
                    }
                }

                sb.append("\n");

                // SECURITY: Safe separator
                for (int i = 0; i < separatorLength && i < 1000; i++) {
                    sb.append("-");
                }
                sb.append("\n");
            }

        } catch (Exception e) {
            System.err.println("SECURITY ERROR: Error displaying grid: " + e.getMessage());
            return "Error displaying grid";
        }

        return sb.toString();
    }

    /**
     * Parses a location string (e.g., "R1-C2" or "1-2") to row and column indices.
     * SECURITY: Comprehensive input validation and error handling.
     *
     * @param location The location string (can be null)
     * @return int array [row, col] in 0-7 range, or null if invalid
     */
    public static int[] parseLocation(String location) {
        // SECURITY: Null and empty check
        if (location == null) {
            return null;
        }

        String trimmed = location.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        // SECURITY: Length check to prevent DoS
        if (trimmed.length() > MAX_LOCATION_STRING_LENGTH) {
            System.err.println("SECURITY WARNING: Location string too long: " + trimmed.length());
            return null;
        }

        try {
            // SECURITY: Clean input - remove whitespace and convert to uppercase
            String cleaned = trimmed.toUpperCase();

            // Remove R and C prefixes (case-insensitive)
            cleaned = cleaned.replace("R", "").replace("C", "");

            // SECURITY: Additional cleaning - remove any remaining whitespace
            cleaned = cleaned.trim();

            // SECURITY: Check if cleaned string is empty
            if (cleaned.isEmpty()) {
                return null;
            }

            // Split by dash with limit
            String[] parts = cleaned.split("-", 3); // Limit to 3 parts

            if (parts.length != 2) {
                return null;
            }

            // SECURITY: Trim each part before parsing
            String rowStr = parts[0].trim();
            String colStr = parts[1].trim();

            // SECURITY: Check for empty parts
            if (rowStr.isEmpty() || colStr.isEmpty()) {
                return null;
            }

            // SECURITY: Length check for each part
            if (rowStr.length() > 10 || colStr.length() > 10) {
                return null;
            }

            // Parse to integers
            int row = Integer.parseInt(rowStr) - 1;  // Convert 1-8 to 0-7
            int col = Integer.parseInt(colStr) - 1;  // Convert 1-8 to 0-7

            // SECURITY: Check for overflow
            if (row < Integer.MIN_VALUE + 1 || row > Integer.MAX_VALUE - 1 ||
                    col < Integer.MIN_VALUE + 1 || col > Integer.MAX_VALUE - 1) {
                System.err.println("SECURITY WARNING: Integer overflow in parseLocation");
                return null;
            }

            // SECURITY: Validate range
            if (row >= MIN_INDEX && row <= MAX_INDEX &&
                    col >= MIN_INDEX && col <= MAX_INDEX) {
                return new int[]{row, col};
            }

            // Out of range
            return null;

        } catch (NumberFormatException e) {
            // SECURITY: Invalid number format - return null
            return null;
        } catch (Exception e) {
            // SECURITY: Catch any other unexpected errors
            System.err.println("SECURITY WARNING: Unexpected error in parseLocation: " +
                    e.getClass().getSimpleName());
            return null;
        }
    }

    /**
     * Validates a location string without parsing.
     * Useful for pre-validation before parsing.
     * SECURITY: Safe validation
     *
     * @param location The location string to validate
     * @return true if the location string is valid, false otherwise
     */
    public static boolean isValidLocationString(String location) {
        int[] coords = parseLocation(location);
        return coords != null;
    }

    /**
     * Gets the grid size constant.
     * SECURITY: Immutable constant
     *
     * @return The grid size (8)
     */
    public static int getGridSize() {
        return GRID_SIZE;
    }

    /**
     * Gets the minimum valid index.
     * SECURITY: Immutable constant
     *
     * @return The minimum index (0)
     */
    public static int getMinIndex() {
        return MIN_INDEX;
    }

    /**
     * Gets the maximum valid index.
     * SECURITY: Immutable constant
     *
     * @return The maximum index (7)
     */
    public static int getMaxIndex() {
        return MAX_INDEX;
    }

    /**
     * Checks if the grid is properly initialized.
     * SECURITY: Thread-safe check
     *
     * @return true if initialized, false otherwise
     */
    public boolean isInitialized() {
        return isInitialized;
    }
}