package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;

/**
 * A SpecialTool that re-stamps 5 boxes in a plus (+) shape to the target letter.
 *
 * The player selects a center box, and this tool stamps:
 * - The center box itself
 * - The box above (upper neighbor)
 * - The box below (lower neighbor)
 * - The box to the left (left neighbor)
 * - The box to the right (right neighbor)
 *
 * If any of the neighboring boxes are outside the grid boundaries, they are
 * simply skipped (not an error). UnchangingBoxes in the pattern will appear
 * to be stamped but their top side letter remains unchanged (handled internally).
 *
 * Example pattern (X = stamped box):
 *        X
 *      X X X
 *        X
 */
public class PlusShapeStamp extends SpecialTool {

    /**
     * Constructor for PlusShapeStamp.
     * Initializes the tool with its name and description.
     */
    public PlusShapeStamp() {
        super("PlusShapeStamp",
                "Re-stamps 5 boxes (in a plus shape) to the target letter");
    }

    /**
     * Executes the PlusShapeStamp effect on the grid.
     *
     * Note: This method throws UnsupportedOperationException because
     * PlusShapeStamp requires center coordinates to operate.
     * Use stampPlusShape() method instead with center row and column.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter to stamp on the boxes
     * @throws UnsupportedOperationException always (use stampPlusShape instead)
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        throw new UnsupportedOperationException("PlusShapeStamp requires center coordinates. Use stampPlusShape() method instead.");
    }

    /**
     * Stamps a plus-shaped pattern of boxes to the target letter.
     * Stamps the center box and its 4 cardinal neighbors (up, down, left, right).
     *
     * Out-of-bounds neighbors are automatically skipped (valid for edge/corner boxes).
     *
     * @param grid The BoxGrid to modify
     * @param centerRow The row index of the center box (0-7)
     * @param centerCol The column index of the center box (0-7)
     * @param targetLetter The letter to stamp
     * @throws IllegalArgumentException if inputs are invalid
     */
    public void stampPlusShape(BoxGrid grid, int centerRow, int centerCol, Letter targetLetter) {
        // Validate inputs
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null");
        }
        if (targetLetter == null) {
            throw new IllegalArgumentException("Target letter cannot be null");
        }
        validateCoordinates(centerRow, centerCol);

        // Stamp center and all 4 neighbors
        // stampBox() in parent class handles boundary checks automatically
        stampBox(grid, centerRow, centerCol, targetLetter);     // Center
        stampBox(grid, centerRow - 1, centerCol, targetLetter); // Up
        stampBox(grid, centerRow + 1, centerCol, targetLetter); // Down
        stampBox(grid, centerRow, centerCol - 1, targetLetter); // Left
        stampBox(grid, centerRow, centerCol + 1, targetLetter); // Right
    }

    /**
     * Validates if a plus-shape stamp can be applied.
     * A plus shape can always be applied even at edges/corners,
     * as out-of-bounds neighbors are automatically skipped.
     *
     * @param grid The BoxGrid
     * @param centerRow The row index of the center box (0-7)
     * @param centerCol The column index of the center box (0-7)
     * @return true if the center coordinates are valid and grid is not null
     */
    public boolean canApplyStamp(BoxGrid grid, int centerRow, int centerCol) {
        return grid != null && centerRow >= MIN_INDEX && centerRow <= MAX_INDEX && centerCol >= MIN_INDEX && centerCol <= MAX_INDEX;
    }
}