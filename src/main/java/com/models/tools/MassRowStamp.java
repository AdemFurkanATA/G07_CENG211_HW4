package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;

/**
 * A SpecialTool that re-stamps all boxes in an entire row to the target letter.
 *
 * The player selects a row (R1 through R8), and this tool stamps all 8 boxes
 * in that row to the target letter.
 *
 * UnchangingBoxes in the row will appear to be stamped but their top side
 * letter remains unchanged (handled by the Box class internally).
 * FixedBoxes are also stamped normally since their inability to move
 * doesn't prevent stamping.
 *
 * Example: If R3 is selected, all boxes from R3-C1 to R3-C8 are stamped.
 */
public class MassRowStamp extends MassStamp {

    /**
     * Constructor for MassRowStamp.
     * Initializes the tool with its name and description.
     */
    public MassRowStamp() {
        super("MassRowStamp", "Re-stamps all boxes in an entire row to the target letter");
    }

    /**
     * Executes the MassRowStamp effect on the grid.
     *
     * Note: This method throws UnsupportedOperationException because
     * MassRowStamp requires a specific row index to operate.
     * Use stampRow() method instead with the row index.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter to stamp on the boxes
     * @throws UnsupportedOperationException always (use stampRow instead)
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        throw new UnsupportedOperationException("MassRowStamp requires a row index. Use stampRow() method instead.");
    }

    /**
     * Stamps all boxes in a specified row to the target letter.
     * This is the main method to use this tool.
     *
     * @param grid The BoxGrid to modify
     * @param rowIndex The row index to stamp (0-7, where 0=R1, 7=R8)
     * @param targetLetter The letter to stamp
     * @throws IllegalArgumentException if inputs are invalid
     */
    public void stampRow(BoxGrid grid, int rowIndex, Letter targetLetter) {
        // Validate all inputs
        validateInputs(grid, targetLetter);
        validateIndex(rowIndex, "row");

        // Stamp all 8 boxes in the row
        for (int col = MIN_INDEX; col < GRID_SIZE; col++) {
            stampBox(grid, rowIndex, col, targetLetter);
        }
    }

    /**
     * Validates if a row stamp can be applied.
     *
     * @param grid The BoxGrid
     * @param rowIndex The row index (0-7)
     * @return true if the row index is valid and grid is not null
     */
    public boolean canApplyStamp(BoxGrid grid, int rowIndex) {
        return grid != null && isValidIndex(rowIndex);
    }

    /**
     * Converts a row string (e.g., "R3" or "3") to a row index (0-7).
     *
     * @param rowStr The row string input
     * @return The row index (0-7, where 0=R1, 7=R8)
     * @throws IllegalArgumentException if the input format is invalid
     */
    public static int parseRowInput(String rowStr) {
        return parseGridIndex(rowStr, 'R');
    }
}