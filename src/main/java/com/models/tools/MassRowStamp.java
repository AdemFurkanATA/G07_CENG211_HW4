package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;
import com.models.boxes.Box;

/**
 * A SpecialTool that re-stamps all boxes in an entire row to the target letter.
 *
 * The player selects a row (R1 through R8), and this tool stamps all 8 boxes
 * in that row to the target letter.
 *
 * UnchangingBoxes in the row will appear to be stamped but their top side
 * letter remains unchanged. FixedBoxes are also stamped normally since their
 * inability to move doesn't prevent stamping.
 *
 * Example: If R3 is selected, all boxes from R3-C1 to R3-C8 are stamped.
 */
public class MassRowStamp extends SpecialTool {

    /**
     * Constructor for MassRowStamp.
     * Initializes the tool with its name and description.
     */
    public MassRowStamp() {
        super("MassRowStamp",
                "Re-stamps all boxes in an entire row to the target letter");
    }

    /**
     * Executes the MassRowStamp effect on the grid.
     * Stamps all boxes in the selected row to the target letter.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter to stamp on the boxes
     * @throws Exception if the tool execution fails
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        // The actual implementation will be completed when we have BoxGrid
        // This method will:
        // 1. Ask user for row selection via menu (R1-R8 or 1-8)
        // 2. Validate the row number
        // 3. Stamp all boxes in that row

        // For now, we'll leave this as a placeholder
        // The menu system will handle user input and call stampRow()
    }

    /**
     * Stamps all boxes in a specified row to the target letter.
     * This is a helper method that will be called from execute() after
     * getting user input.
     *
     * @param grid The BoxGrid to modify
     * @param rowIndex The row index to stamp (0-7, where 0 = R1, 7 = R8)
     * @param targetLetter The letter to stamp
     */
    public void stampRow(BoxGrid grid, int rowIndex, Letter targetLetter) {
        // Validate row index
        if (rowIndex < 0 || rowIndex > 7) {
            return;
        }

        // Stamp all 8 boxes in the row (columns 0-7)
        for (int col = 0; col < 8; col++) {
            stampBox(grid, rowIndex, col, targetLetter);
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
     * Validates if a row stamp can be applied.
     *
     * @param grid The BoxGrid
     * @param rowIndex The row index (0-7)
     * @return true if the row index is valid
     */
    public boolean canApplyStamp(BoxGrid grid, int rowIndex) {
        return rowIndex >= 0 && rowIndex < 8;
    }

    /**
     * Converts a row string (e.g., "R3" or "3") to a row index (0-7).
     *
     * @param rowStr The row string input
     * @return The row index (0-7), or -1 if invalid
     */
    public static int parseRowInput(String rowStr) {
        if (rowStr == null || rowStr.isEmpty()) {
            return -1;
        }

        // Remove 'R' or 'r' prefix if present
        String cleaned = rowStr.toUpperCase().replace("R", "").trim();

        try {
            int rowNum = Integer.parseInt(cleaned);
            // Convert from 1-8 to 0-7 index
            if (rowNum >= 1 && rowNum <= 8) {
                return rowNum - 1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }

        return -1;
    }
}