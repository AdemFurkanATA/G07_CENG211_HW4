package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;
import com.models.boxes.Box;
import com.models.boxes.FixedBox;
import com.exceptions.UnmovableFixedBoxException;

/**
 * A SpecialTool that flips a box upside down.
 *
 * The player selects a specific box, and this tool swaps the top and bottom
 * sides of that box. The letter that was on the top becomes the bottom,
 * and the letter that was on the bottom becomes the new top.
 *
 * IMPORTANT: This tool CANNOT be used on FixedBoxes. Attempting to flip
 * a FixedBox will throw an UnmovableFixedBoxException and waste the turn.
 *
 * UnchangingBoxes CAN be flipped. While their internal state changes,
 * their top side letter will appear unchanged due to UnchangingBox's
 * special behavior.
 *
 * Example: If a box has 'A' on top and 'B' on bottom, after flipping:
 *          - Top will have 'B'
 *          - Bottom will have 'A'
 */
public class BoxFlipper extends SpecialTool {

    /**
     * Constructor for BoxFlipper.
     * Initializes the tool with its name and description.
     */
    public BoxFlipper() {
        super("BoxFlipper",
                "Flips a box upside down (swaps top and bottom sides)");
    }

    /**
     * Executes the BoxFlipper effect on the grid.
     *
     * Note: This method throws UnsupportedOperationException because
     * BoxFlipper requires specific coordinates to operate.
     * Use flipBox() method instead with row and column indices.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter (not used by BoxFlipper)
     * @throws UnsupportedOperationException always (use flipBox instead)
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        throw new UnsupportedOperationException(
                "BoxFlipper requires coordinates. Use flipBox() method instead."
        );
    }

/**
 * Flips a box at the specified location upside down.
 * Swaps the top and bottom sides of the box.
 *
 * @param grid The BoxGrid to modify
 * @param row The row index (0-7)
 * @param col The column index (0-7)
 * @throws IllegalArgumentException if coordinates are invalid or grid is null
 * @throws UnmovableFixedBoxException if the box is a FixedBox
 */
public void flipBox(BoxGrid grid, int row, int col) throws UnmovableFixedBoxException {
    // Validate inputs
    if (grid == null) {
        throw new IllegalArgumentException("Grid cannot be null");
    }
    validateCoordinates(row, col);

    Box box = grid.getBox(row, col);

    if (box == null) {
        throw new IllegalArgumentException(
                "No box found at location: " + formatLocation(row, col)
        );
    }

    // Check if it's a FixedBox
    if (box instanceof FixedBox) {
        throw new UnmovableFixedBoxException(
                "Cannot flip a FixedBox! Continuing to the next turn...",
                formatLocation(row, col),
                "flip"
        );
    }

    // Flip the box (swap top and bottom sides)
    box.flip();
}

    /**
     * Validates if a box can be flipped.
     * A box can be flipped if it exists and is not a FixedBox.
     *
     * @param grid The BoxGrid
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @return true if the box can be flipped, false otherwise
     */
    public boolean canFlipBox(BoxGrid grid, int row, int col) {
        if (grid == null) {
            return false;
        }

        if (row < MIN_INDEX || row > MAX_INDEX ||
                col < MIN_INDEX || col > MAX_INDEX) {
            return false;
        }

        Box box = grid.getBox(row, col);

        // Can't flip null boxes or FixedBoxes
        return box != null && !(box instanceof FixedBox);
    }
}