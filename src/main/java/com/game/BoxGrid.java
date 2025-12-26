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
 */
public class BoxGrid {

    private static final int GRID_SIZE = 8;
    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 7;

    // Probability constants
    private static final double REGULAR_BOX_PROBABILITY = 0.85;
    private static final double UNCHANGING_BOX_PROBABILITY = 0.95; // 0.85 + 0.10

    // The main data structure: 2D ArrayList storing all 64 boxes
    // SECURITY: Private final to prevent reassignment
    private final ArrayList<ArrayList<Box>> grid;

    /**
     * Constructor for BoxGrid.
     * Initializes an 8x8 grid with randomly generated boxes.
     */
    public BoxGrid() {
        grid = new ArrayList<>(GRID_SIZE);
        initializeGrid();
    }

    /**
     * Initializes the 8x8 grid with randomly generated boxes.
     * Each box is generated based on probability:
     * - 85% RegularBox
     * - 10% UnchangingBox
     * - 5% FixedBox
     */
    private void initializeGrid() {
        for (int row = 0; row < GRID_SIZE; row++) {
            ArrayList<Box> rowList = new ArrayList<>(GRID_SIZE);
            for (int col = 0; col < GRID_SIZE; col++) {
                Box box = generateRandomBox();
                // SECURITY: Ensure we never add null boxes
                if (box == null) {
                    box = new RegularBox(); // Fallback to RegularBox
                }
                rowList.add(box);
            }
            grid.add(rowList);
        }
    }

    /**
     * Generates a random box based on probability distribution.
     * @return A randomly generated Box (never null)
     */
    private Box generateRandomBox() {
        double random = Math.random();

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
        // SECURITY: Validate bounds before accessing
        if (!isValidCoordinate(row, col)) {
            return null;
        }

        try {
            ArrayList<Box> rowList = grid.get(row);
            if (rowList == null) {
                return null;
            }
            return rowList.get(col);
        } catch (IndexOutOfBoundsException e) {
            // Extra safety: catch any unexpected index issues
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
     */
    public Box getBoxSafe(int row, int col) {
        if (!isValidCoordinate(row, col)) {
            throw new IllegalArgumentException(
                    "Invalid grid coordinates: row=" + row + ", col=" + col +
                            " (valid range: 0-7)"
            );
        }

        Box box = getBox(row, col);
        if (box == null) {
            throw new IllegalArgumentException(
                    "No box found at location: R" + (row + 1) + "-C" + (col + 1)
            );
        }

        return box;
    }

    /**
     * Sets a box at the specified position.
     * Used when converting boxes (e.g., BoxFixer tool).
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @param box The box to place at this position (must not be null)
     * @throws IllegalArgumentException if parameters are invalid
     */
    public void setBox(int row, int col, Box box) {
        // SECURITY: Validate all inputs
        if (box == null) {
            throw new IllegalArgumentException("Box cannot be null");
        }

        if (!isValidCoordinate(row, col)) {
            throw new IllegalArgumentException(
                    "Invalid grid coordinates: row=" + row + ", col=" + col +
                            " (valid range: 0-7)"
            );
        }

        try {
            grid.get(row).set(col, box);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    "Failed to set box at row=" + row + ", col=" + col, e
            );
        }
    }

    /**
     * Validates if coordinates are within grid bounds.
     * SECURITY: Centralized validation method.
     *
     * @param row Row index
     * @param col Column index
     * @return true if coordinates are valid (0-7), false otherwise
     */
    private boolean isValidCoordinate(int row, int col) {
        return row >= MIN_INDEX && row <= MAX_INDEX &&
                col >= MIN_INDEX && col <= MAX_INDEX;
    }

    /**
     * Checks if a position is on the edge of the grid.
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return true if the position is on any edge, false otherwise
     */
    public boolean isEdge(int row, int col) {
        if (!isValidCoordinate(row, col)) {
            return false;
        }
        return row == MIN_INDEX || row == MAX_INDEX ||
                col == MIN_INDEX || col == MAX_INDEX;
    }

    /**
     * Checks if a position is in a corner of the grid.
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return true if the position is in a corner, false otherwise
     */
    public boolean isCorner(int row, int col) {
        if (!isValidCoordinate(row, col)) {
            return false;
        }
        return (row == MIN_INDEX || row == MAX_INDEX) &&
                (col == MIN_INDEX || col == MAX_INDEX);
    }

    /**
     * Gets the available directions for rolling from an edge position.
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return Unmodifiable list of available directions (empty if invalid position)
     */
    public List<Direction> getAvailableDirections(int row, int col) {
        if (!isValidCoordinate(row, col)) {
            return Collections.emptyList();
        }

        List<Direction> directions = new ArrayList<>();

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

        return Collections.unmodifiableList(directions);
    }

    /**
     * Rolls boxes starting from an edge position in the specified direction.
     * Implements the domino-effect: all boxes in the path roll until a FixedBox
     * is encountered or the grid boundary is reached.
     *
     * SECURITY: Fixed infinite loop risk and added safety checks.
     *
     * @param startRow Starting row index (0-7)
     * @param startCol Starting column index (0-7)
     * @param direction The direction to roll (must not be null)
     * @throws IllegalArgumentException if parameters are invalid
     */
    public void rollBoxesFromEdge(int startRow, int startCol, Direction direction) {
        // SECURITY: Validate all inputs
        if (!isValidCoordinate(startRow, startCol)) {
            throw new IllegalArgumentException(
                    "Invalid starting position: row=" + startRow + ", col=" + startCol
            );
        }

        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }

        // SECURITY: Track iteration count to prevent infinite loops
        int iterationCount = 0;
        final int MAX_ITERATIONS = GRID_SIZE + 1; // Max possible is 8 boxes in a line

        int currentRow = startRow;
        int currentCol = startCol;

        // Roll boxes along the path until we hit a FixedBox or boundary
        while (iterationCount < MAX_ITERATIONS) {
            iterationCount++;

            // SECURITY: Check bounds before accessing
            if (!isValidCoordinate(currentRow, currentCol)) {
                break;  // Out of bounds - stop rolling
            }

            Box currentBox = getBox(currentRow, currentCol);

            if (currentBox == null) {
                // SECURITY: Should never happen but handle gracefully
                break;
            }

            // Check if this box can be rolled
            if (!currentBox.canRoll()) {
                // FixedBox encountered - stop the domino effect
                break;
            }

            // Roll the current box
            currentBox.roll(direction);

            // Move to the next box in the direction
            currentRow += direction.getRowDelta();
            currentCol += direction.getColDelta();

            // Check if we've gone out of bounds
            if (!isValidCoordinate(currentRow, currentCol)) {
                break;
            }
        }

        // SECURITY: Log warning if max iterations reached (should never happen)
        if (iterationCount >= MAX_ITERATIONS) {
            System.err.println("WARNING: Rolling operation reached maximum iterations. " +
                    "This may indicate a logic error.");
        }
    }

    /**
     * Resets the rolled status for all boxes at the start of a new turn.
     * SECURITY: Handles null boxes gracefully.
     */
    public void resetAllRolledStatus() {
        for (int row = MIN_INDEX; row <= MAX_INDEX; row++) {
            for (int col = MIN_INDEX; col <= MAX_INDEX; col++) {
                Box box = getBox(row, col);
                if (box != null) {
                    box.resetRolledStatus();
                }
            }
        }
    }

    /**
     * Counts the number of boxes with the target letter on their top side.
     *
     * @param targetLetter The letter to count (must not be null)
     * @return The number of boxes with the target letter on top (0-64)
     * @throws IllegalArgumentException if targetLetter is null
     */
    public int countTargetLetters(Letter targetLetter) {
        if (targetLetter == null) {
            throw new IllegalArgumentException("Target letter cannot be null");
        }

        int count = 0;

        for (int row = MIN_INDEX; row <= MAX_INDEX; row++) {
            for (int col = MIN_INDEX; col <= MAX_INDEX; col++) {
                Box box = getBox(row, col);

                // SECURITY: Check for null and null top side
                if (box != null && box.getTopSide() != null &&
                        box.getTopSide() == targetLetter) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Checks if all edge boxes are FixedBoxes.
     * Used to determine if the game can continue.
     *
     * @return true if all edge boxes are FixedBoxes, false otherwise
     */
    public boolean areAllEdgeBoxesFixed() {
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
     * SECURITY: Handles null boxes gracefully.
     *
     * @return String representation of the grid (never null)
     */
    public String displayGrid() {
        StringBuilder sb = new StringBuilder();

        // Header row
        sb.append("    ");
        for (int col = MIN_INDEX; col <= MAX_INDEX; col++) {
            sb.append("C").append(col + 1).append("       ");
        }
        sb.append("\n");
        sb.append("-".repeat(GRID_SIZE * 10 + 1)).append("\n");

        // Grid rows
        for (int row = MIN_INDEX; row <= MAX_INDEX; row++) {
            sb.append("R").append(row + 1).append(" ");

            for (int col = MIN_INDEX; col <= MAX_INDEX; col++) {
                Box box = getBox(row, col);

                if (box != null) {
                    sb.append(box.toString()).append(" ");
                } else {
                    // SECURITY: Handle null boxes (should never happen)
                    sb.append("| NULL  | ");
                }
            }

            sb.append("\n");
            sb.append("-".repeat(GRID_SIZE * 10 + 1)).append("\n");
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
        if (location == null || location.trim().isEmpty()) {
            return null;
        }

        try {
            // SECURITY: Clean input - remove whitespace and convert to uppercase
            String cleaned = location.trim().toUpperCase();

            // Remove R and C prefixes (case-insensitive)
            cleaned = cleaned.replace("R", "").replace("C", "");

            // SECURITY: Additional cleaning - remove any remaining whitespace
            cleaned = cleaned.trim();

            // Split by dash
            String[] parts = cleaned.split("-");

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

            // Parse to integers
            int row = Integer.parseInt(rowStr) - 1;  // Convert 1-8 to 0-7
            int col = Integer.parseInt(colStr) - 1;  // Convert 1-8 to 0-7

            // SECURITY: Validate range
            if (row >= MIN_INDEX && row <= MAX_INDEX &&
                    col >= MIN_INDEX && col <= MAX_INDEX) {
                return new int[]{row, col};
            }

            // Out of range
            return null;

        } catch (NumberFormatException e) {
            // SECURITY: Invalid number format
            return null;
        } catch (Exception e) {
            // SECURITY: Catch any other unexpected errors
            return null;
        }
    }

    /**
     * Validates a location string without parsing.
     * Useful for pre-validation before parsing.
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
     *
     * @return The grid size (8)
     */
    public static int getGridSize() {
        return GRID_SIZE;
    }

    /**
     * Gets the minimum valid index.
     *
     * @return The minimum index (0)
     */
    public static int getMinIndex() {
        return MIN_INDEX;
    }

    /**
     * Gets the maximum valid index.
     *
     * @return The maximum index (7)
     */
    public static int getMaxIndex() {
        return MAX_INDEX;
    }
}