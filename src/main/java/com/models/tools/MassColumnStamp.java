package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;
import com.models.boxes.Box;

/**
 * A SpecialTool that re-stamps all boxes in an entire column to the target letter.
 *
 * The player selects a column (C1 through C8), and this tool stamps all 8 boxes
 * in that column to the target letter.
 *
 * UnchangingBoxes in the column will appear to be stamped but their top side
 * letter remains unchanged. FixedBoxes are also stamped normally since their
 * inability to move doesn't prevent stamping.
 *
 * Example: If C5 is selected, all boxes from R1-C5 to R8-C5 are stamped.
 */
public class MassColumnStamp extends SpecialTool {

    /**
     * Constructor for MassColumnStamp.
     * Initializes the tool with its name and description.
     */
    public MassColumnStamp() {
        super("MassColumnStamp",
                "Re-stamps all boxes in an entire column to the target letter");
    }

    /**
     * Executes the MassColumnStamp effect on the grid.
     * Stamps all boxes in the selected column to the target letter.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter to stamp on the boxes
     * @throws Exception if the tool execution fails
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        // The actual implementation will be completed when we have BoxGrid
        // This method will:
        // 1. Ask user for column selection via menu (C1-C8 or 1-8)
        // 2. Validate the column number
        // 3. Stamp all boxes in that column

        // For now, we'll leave this as a placeholder
        // The menu system will handle user input and call stampColumn()
    }

    /**
     * Stamps all boxes in a specified column to the target letter.
     * This is a helper method that will be called from execute() after
     * getting user input.
     *
     * @param grid The BoxGrid to modify
     * @param colIndex The column index to stamp (0-7, where 0 = C1, 7 = C8)
     * @param targetLetter The letter to stamp
     */
    public void stampColumn(BoxGrid grid, int colIndex, Letter targetLetter) {
        // Validate column index
        if (colIndex < 0 || colIndex > 7) {
            return;
        }

        // Stamp all 8 boxes in the column (rows 0-7)
        for (int row = 0; row < 8; row++) {
            stampBox(grid, row, colIndex, targetLetter);
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
     * Validates if a column stamp can be applied.
     *
     * @param grid The BoxGrid
     * @param colIndex The column index (0-7)
     * @return true if the column index is valid
     */
    public boolean canApplyStamp(BoxGrid grid, int colIndex) {
        return colIndex >= 0 && colIndex < 8;
    }

    /**
     * Converts a column string (e.g., "C5" or "5") to a column index (0-7).
     *
     * @param colStr The column string input
     * @return The column index (0-7), or -1 if invalid
     */
    public static int parseColumnInput(String colStr) {
        if (colStr == null || colStr.isEmpty()) {
            return -1;
        }

        // Remove 'C' or 'c' prefix if present
        String cleaned = colStr.toUpperCase().replace("C", "").trim();

        try {
            int colNum = Integer.parseInt(cleaned);
            // Convert from 1-8 to 0-7 index
            if (colNum >= 1 && colNum <= 8) {
                return colNum - 1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }

        return -1;
    }
}