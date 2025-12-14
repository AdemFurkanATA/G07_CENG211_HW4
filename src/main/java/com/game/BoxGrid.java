package com.game;

import com.enums.Letter;
import com.enums.Direction;
import com.models.boxes.*;
import java.util.ArrayList;
import java.util.List;

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

    // The main data structure: 2D ArrayList storing all 64 boxes
    private ArrayList<ArrayList<Box>> grid;

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
                rowList.add(generateRandomBox());
            }
            grid.add(rowList);
        }
    }

    /**
     * Generates a random box based on probability distribution.
     *
     * @return A randomly generated Box (RegularBox, UnchangingBox, or FixedBox)
     */
    private Box generateRandomBox() {
        double random = Math.random();

        if (random < 0.85) {
            // 85% chance for RegularBox
            return new RegularBox();
        } else if (random < 0.95) {
            // 10% chance for UnchangingBox (0.85 to 0.95)
            return new UnchangingBox();
        } else {
            // 5% chance for FixedBox (0.95 to 1.0)
            return new FixedBox();
        }
    }

    /**
     * Gets the box at the specified row and column.
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return The Box at the specified position, or null if out of bounds
     */
    public Box getBox(int row, int col) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return null;
        }
        return grid.get(row).get(col);
    }

    /**
     * Sets a box at the specified position.
     * Used when converting boxes (e.g., BoxFixer tool).
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @param box The box to place at this position
     */
    public void setBox(int row, int col, Box box) {
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            grid.get(row).set(col, box);
        }
    }

    /**
     * Checks if a position is on the edge of the grid.
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return true if the position is on any edge, false otherwise
     */
    public boolean isEdge(int row, int col) {
        return row == 0 || row == GRID_SIZE - 1 || col == 0 || col == GRID_SIZE - 1;
    }

    /**
     * Checks if a position is in a corner of the grid.
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return true if the position is in a corner, false otherwise
     */
    public boolean isCorner(int row, int col) {
        return (row == 0 || row == GRID_SIZE - 1) && (col == 0 || col == GRID_SIZE - 1);
    }

    /**
     * Gets the available directions for rolling from a corner position.
     *
     * @param row Row index (0-7)
     * @param col Column index (0-7)
     * @return List of available directions (2 for corners)
     */
    public List<Direction> getAvailableDirections(int row, int col) {
        List<Direction> directions = new ArrayList<>();

        if (row == 0 && col == 0) {
            // Top-left corner
            directions.add(Direction.RIGHT);
            directions.add(Direction.DOWN);
        } else if (row == 0 && col == GRID_SIZE - 1) {
            // Top-right corner
            directions.add(Direction.LEFT);
            directions.add(Direction.DOWN);
        } else if (row == GRID_SIZE - 1 && col == 0) {
            // Bottom-left corner
            directions.add(Direction.RIGHT);
            directions.add(Direction.UP);
        } else if (row == GRID_SIZE - 1 && col == GRID_SIZE - 1) {
            // Bottom-right corner
            directions.add(Direction.LEFT);
            directions.add(Direction.UP);
        } else if (row == 0) {
            // Top edge
            directions.add(Direction.DOWN);
        } else if (row == GRID_SIZE - 1) {
            // Bottom edge
            directions.add(Direction.UP);
        } else if (col == 0) {
            // Left edge
            directions.add(Direction.RIGHT);
        } else if (col == GRID_SIZE - 1) {
            // Right edge
            directions.add(Direction.LEFT);
        }

        return directions;
    }

    /**
     * Rolls boxes starting from an edge position in the specified direction.
     * Implements the domino-effect: all boxes in the path roll until a FixedBox
     * is encountered or the grid boundary is reached.
     *
     * @param startRow Starting row index (0-7)
     * @param startCol Starting column index (0-7)
     * @param direction The direction to roll
     */
    public void rollBoxesFromEdge(int startRow, int startCol, Direction direction) {
        int currentRow = startRow;
        int currentCol = startCol;

        // Roll boxes along the path until we hit a FixedBox or boundary
        while (true) {
            Box currentBox = getBox(currentRow, currentCol);

            if (currentBox == null) {
                break;  // Out of bounds
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
            if (currentRow < 0 || currentRow >= GRID_SIZE || currentCol < 0 || currentCol >= GRID_SIZE) {
                break;
            }
        }
    }

    /**
     * Resets the rolled status for all boxes at the start of a new turn.
     */
    public void resetAllRolledStatus() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
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
     * @param targetLetter The letter to count
     * @return The number of boxes with the target letter on top
     */
    public int countTargetLetters(Letter targetLetter) {
        int count = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Box box = getBox(row, col);
                if (box != null && box.getTopSide() == targetLetter) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Checks if all edge boxes are FixedBoxes.
     * This is used to detect if the game should end early (no moves possible).
     *
     * @return true if all edge boxes are FixedBoxes, false otherwise
     */
    public boolean areAllEdgeBoxesFixed() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (isEdge(row, col)) {
                    Box box = getBox(row, col);
                    if (box != null && box.canRoll()) {
                        return false;  // Found a non-fixed edge box
                    }
                }
            }
        }
        return true;  // All edge boxes are fixed
    }

    /**
     * Displays the grid in a formatted table.
     *
     * @return String representation of the grid
     */
    public String displayGrid() {
        StringBuilder sb = new StringBuilder();

        // Header row
        sb.append("    ");
        for (int col = 0; col < GRID_SIZE; col++) {
            sb.append("C").append(col + 1).append("       ");
        }
        sb.append("\n");
        sb.append("-".repeat(GRID_SIZE * 10 + 1)).append("\n");

        // Grid rows
        for (int row = 0; row < GRID_SIZE; row++) {
            sb.append("R").append(row + 1).append(" ");
            for (int col = 0; col < GRID_SIZE; col++) {
                Box box = getBox(row, col);
                sb.append(box.toString()).append(" ");
            }
            sb.append("\n");
            sb.append("-".repeat(GRID_SIZE * 10 + 1)).append("\n");
        }

        return sb.toString();
    }

    /**
     * Parses a location string (e.g., "R1-C2" or "1-2") to row and column indices.
     *
     * @param location The location string
     * @return int array [row, col] in 0-7 range, or null if invalid
     */
    public static int[] parseLocation(String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }

        String cleaned = location.toUpperCase().replace("R", "").replace("C", "").trim();
        String[] parts = cleaned.split("-");

        if (parts.length != 2) {
            return null;
        }

        try {
            int row = Integer.parseInt(parts[0].trim()) - 1;
            int col = Integer.parseInt(parts[1].trim()) - 1;

            if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                return new int[]{row, col};
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }
}