package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;

/**
 * A SpecialTool that re-stamps all boxes in an entire column to the target letter.
 *
 * The player selects a column (C1 through C8), and this tool stamps all 8 boxes
 * in that column to the target letter.
 *
 * UnchangingBoxes in the column will appear to be stamped but their top side
 * letter remains unchanged (handled by the Box class internally).
 * FixedBoxes are also stamped normally since their inability to move
 * doesn't prevent stamping.
 *
 * Example: If C5 is selected, all boxes from R1-C5 to R8-C5 are stamped.
 */
public class MassColumnStamp extends MassStamp {

    /**
     * Constructor for MassColumnStamp.
     * Initializes the tool with its name and description.
     */
    public MassColumnStamp() {
        super("MassColumnStamp", "Re-stamps all boxes in an entire column to the target letter");
    }

    /**
     * Executes the MassColumnStamp effect on the grid.
     *
     * Note: This method throws UnsupportedOperationException because
     * MassColumnStamp requires a specific column index to operate.
     * Use stampColumn() method instead with the column index.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter to stamp on the boxes
     * @throws UnsupportedOperationException always (use stampColumn instead)
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        throw new UnsupportedOperationException("MassColumnStamp requires a column index. Use stampColumn() method instead.");
    }

    /**
     * Stamps all boxes in a specified column to the target letter.
     * This is the main method to use this tool.
     *
     * @param grid The BoxGrid to modify
     * @param colIndex The column index to stamp (0-7, where 0=C1, 7=C8)
     * @param targetLetter The letter to stamp
     * @throws IllegalArgumentException if inputs are invalid
     */
    public void stampColumn(BoxGrid grid, int colIndex, Letter targetLetter) {
        // Validate all inputs
        validateInputs(grid, targetLetter);
        validateIndex(colIndex, "column");

        // Stamp all 8 boxes in the column
        for (int row = MIN_INDEX; row < GRID_SIZE; row++) {
            stampBox(grid, row, colIndex, targetLetter);
        }
    }

    /**
     * Validates if a column stamp can be applied.
     *
     * @param grid The BoxGrid
     * @param colIndex The column index (0-7)
     * @return true if the column index is valid and grid is not null
     */
    public boolean canApplyStamp(BoxGrid grid, int colIndex) {
        return grid != null && isValidIndex(colIndex);
    }

    /**
     * Converts a column string (e.g., "C5" or "5") to a column index (0-7).
     *
     * @param colStr The column string input
     * @return The column index (0-7, where 0=C1, 7=C8)
     * @throws IllegalArgumentException if the input format is invalid
     */
    public static int parseColumnInput(String colStr) {
        return parseGridIndex(colStr, 'C');
    }
}