package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;
import com.models.boxes.Box;

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
 * to be stamped but their top side letter remains unchanged.
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
     * Stamps the center box and its 4 cardinal neighbors to the target letter.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter to stamp on the boxes
     * @throws Exception if the tool execution fails
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        // The actual implementation will be completed when we have BoxGrid
        // This method will:
        // 1. Ask user for center box location via menu
        // 2. Validate the location
        // 3. Get the center box and its neighbors (up, down, left, right)
        // 4. Stamp each box's top side to targetLetter
        // 5. Handle boundary cases (edge/corner boxes have fewer neighbors)

        // For now, we'll leave this as a placeholder
        // The menu system will handle user input and call stampPlusShape()
    }

    /**
     * Stamps a plus-shaped pattern of boxes to the target letter.
     * This is a helper method that will be called from execute() after
     * getting user input.
     *
     * @param grid The BoxGrid to modify
     * @param centerRow The row index of the center box (0-7)
     * @param centerCol The column index of the center box (0-7)
     * @param targetLetter The letter to stamp
     */
    public void stampPlusShape(BoxGrid grid, int centerRow, int centerCol, Letter targetLetter) {
        // Stamp center box
        stampBox(grid, centerRow, centerCol, targetLetter);

        // Stamp upper neighbor (if exists)
        if (centerRow > 0) {
            stampBox(grid, centerRow - 1, centerCol, targetLetter);
        }

        // Stamp lower neighbor (if exists)
        if (centerRow < 7) {
            stampBox(grid, centerRow + 1, centerCol, targetLetter);
        }

        // Stamp left neighbor (if exists)
        if (centerCol > 0) {
            stampBox(grid, centerRow, centerCol - 1, targetLetter);
        }

        // Stamp right neighbor (if exists)
        if (centerCol < 7) {
            stampBox(grid, centerRow, centerCol + 1, targetLetter);
        }
    }

    /**
     * Helper method to stamp a single box at the given position.
     * Handles UnchangingBox special case (appears stamped but doesn't change).
     *
     * @param grid The BoxGrid to modify
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @param letter The letter to stamp
     */
    private void stampBox(BoxGrid grid, int row, int col, Letter letter) {
        Box box = grid.getBox(row, col);
        if (box != null) {
            // Stamp the top side of the box
            // UnchangingBox will handle this internally and not actually change
            box.stampTopSide(letter);
        }
    }

    /**
     * Validates if a plus-shape stamp can be applied.
     * A plus shape can always be applied, even at edges/corners,
     * as out-of-bounds neighbors are simply skipped.
     *
     * @param grid The BoxGrid
     * @param centerRow The row index of the center box
     * @param centerCol The column index of the center box
     * @return true if the stamp can be applied (always true for valid coordinates)
     */
    public boolean canApplyStamp(BoxGrid grid, int centerRow, int centerCol) {
        // Check if center position is within bounds
        return centerRow >= 0 && centerRow < 8 && centerCol >= 0 && centerCol < 8;
    }
}