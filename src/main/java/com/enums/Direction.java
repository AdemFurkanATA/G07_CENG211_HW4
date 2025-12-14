package com.enums;

/**
 * Enum representing the four cardinal directions for rolling boxes.
 * Used to determine which direction boxes should roll when selected from edges.
 *
 * - UP: Rolling upwards (towards R1)
 * - DOWN: Rolling downwards (towards R8)
 * - LEFT: Rolling to the left (towards C1)
 * - RIGHT: Rolling to the right (towards C8)
 */
public enum Direction {
    UP("upwards", -1, 0),
    DOWN("downwards", 1, 0),
    LEFT("left", 0, -1),
    RIGHT("right", 0, 1);

    private final String displayName;
    private final int rowDelta;    // Change in row position
    private final int colDelta;    // Change in column position

    /**
     * Constructor for Direction enum.
     *
     * @param displayName Human-readable name for display in menus
     * @param rowDelta The change in row index when moving in this direction
     * @param colDelta The change in column index when moving in this direction
     */
    Direction(String displayName, int rowDelta, int colDelta) {
        this.displayName = displayName;
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    /**
     * Gets the display name for this direction.
     *
     * @return A human-readable string representation
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the row delta for moving in this direction.
     *
     * @return -1 for UP, 1 for DOWN, 0 for LEFT/RIGHT
     */
    public int getRowDelta() {
        return rowDelta;
    }

    /**
     * Gets the column delta for moving in this direction.
     *
     * @return -1 for LEFT, 1 for RIGHT, 0 for UP/DOWN
     */
    public int getColDelta() {
        return colDelta;
    }

    /**
     * Checks if this direction is horizontal (LEFT or RIGHT).
     *
     * @return true if LEFT or RIGHT, false otherwise
     */
    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }

    /**
     * Checks if this direction is vertical (UP or DOWN).
     *
     * @return true if UP or DOWN, false otherwise
     */
    public boolean isVertical() {
        return this == UP || this == DOWN;
    }

    /**
     * Gets the opposite direction.
     *
     * @return The opposite direction (UP<->DOWN, LEFT<->RIGHT)
     */
    public Direction getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return this;
        }
    }

    /**
     * Converts a menu choice number to a Direction.
     * Used for corner boxes where player chooses direction.
     *
     * @param choice The menu choice (1 or 2)
     * @param isHorizontal true if choosing between horizontal directions
     * @return The corresponding Direction, or null if invalid
     */
    public static Direction fromChoice(int choice, boolean isHorizontal) {
        if (isHorizontal) {
            return choice == 1 ? LEFT : (choice == 2 ? RIGHT : null);
        } else {
            return choice == 1 ? UP : (choice == 2 ? DOWN : null);
        }
    }
}