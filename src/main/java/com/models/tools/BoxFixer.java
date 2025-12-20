package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;
import com.models.boxes.Box;
import com.models.boxes.FixedBox;
import com.exceptions.BoxAlreadyFixedException;

/**
 * A SpecialTool that replaces a box with an identical FixedBox copy.
 *
 * The player selects a specific box, and this tool converts it to a FixedBox.
 * The FixedBox will have the same top side letter as the original box, but:
 * - It cannot be rolled in any direction
 * - It stops the domino-effect from passing through it
 * - Any SpecialTool inside the original box is removed from the game
 *
 * IMPORTANT: This tool CANNOT be used on boxes that are already FixedBoxes.
 * Attempting to fix an already-fixed box will throw a BoxAlreadyFixedException
 * and waste the turn.
 *
 * Both RegularBoxes and UnchangingBoxes can be converted to FixedBoxes.
 *
 * Example: If a RegularBox with 'D' on top is fixed:
 *          - It becomes a FixedBox with 'D' on top
 *          - Any tool inside is lost
 *          - It can no longer be moved
 */
public class BoxFixer extends SpecialTool {

    /**
     * Constructor for BoxFixer.
     * Initializes the tool with its name and description.
     */
    public BoxFixer() {
        super("BoxFixer", "Replaces a box with an identical FixedBox copy");
    }

    /**
     * Executes the BoxFixer effect on the grid.
     *
     * Note: This method throws UnsupportedOperationException because
     * BoxFixer requires specific coordinates to operate.
     * Use fixBox() method instead with row and column indices.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter (not used by BoxFixer)
     * @throws UnsupportedOperationException always (use fixBox instead)
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        throw new UnsupportedOperationException("BoxFixer requires coordinates. Use fixBox() method instead.");
    }

    /**
     * Fixes a box at the specified location by converting it to a FixedBox.
     * The new FixedBox will have the same surfaces as the original box,
     * but any tool inside will be removed.
     *
     * @param grid The BoxGrid to modify
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @throws IllegalArgumentException if coordinates are invalid or grid is null
     * @throws BoxAlreadyFixedException if the box is already a FixedBox
     */
    public void fixBox(BoxGrid grid, int row, int col) throws BoxAlreadyFixedException {
        // Validate inputs
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null");
        }
        validateCoordinates(row, col);

        Box box = grid.getBox(row, col);

        if (box == null) {
            throw new IllegalArgumentException("No box found at location: " + formatLocation(row, col));
        }

        // Check if it's already a FixedBox
        if (box instanceof FixedBox) {
            throw new BoxAlreadyFixedException("The selected box is already a FixedBox and cannot be fixed again. Continuing to the next turn...", formatLocation(row, col));
        }

        // Convert the box to a FixedBox
        // Create a new FixedBox with the same surfaces as the original
        // Note: FixedBoxes are always empty (no tools inside)
        FixedBox fixedBox = new FixedBox(box.getSurfaces());

        // Replace the box in the grid
        grid.setBox(row, col, fixedBox);
    }

    /**
     * Validates if a box can be fixed.
     * A box can be fixed if it exists and is not already a FixedBox.
     *
     * @param grid The BoxGrid
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @return true if the box can be fixed, false otherwise
     */
    public boolean canFixBox(BoxGrid grid, int row, int col) {
        if (grid == null) {
            return false;
        }

        if (row < MIN_INDEX || row > MAX_INDEX || col < MIN_INDEX || col > MAX_INDEX) {
            return false;
        }

        Box box = grid.getBox(row, col);
        // Can't fix null boxes or already-fixed boxes
        return box != null && !(box instanceof FixedBox);
    }
}