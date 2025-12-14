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
        super("BoxFixer",
                "Replaces a box with an identical FixedBox copy");
    }

    /**
     * Executes the BoxFixer effect on the grid.
     * Converts the selected box to a FixedBox.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter (not used by BoxFixer, but required by interface)
     * @throws Exception if the tool execution fails
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        // The actual implementation will be completed when we have BoxGrid
        // This method will:
        // 1. Ask user for box location via menu (e.g., "R5-C3")
        // 2. Validate the location
        // 3. Check if it's already a FixedBox (throw exception if so)
        // 4. Replace the box with a FixedBox copy
        // 5. Remove any tool inside the original box

        // For now, we'll leave this as a placeholder
        // The menu system will handle user input and call fixBox()
    }

    /**
     * Fixes a box at the specified location by converting it to a FixedBox.
     * This is a helper method that will be called from execute() after
     * getting user input.
     *
     * @param grid The BoxGrid to modify
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @throws BoxAlreadyFixedException if the box is already a FixedBox
     */
    public void fixBox(BoxGrid grid, int row, int col) throws BoxAlreadyFixedException {
        // Validate coordinates
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return;
        }

        Box box = grid.getBox(row, col);

        if (box == null) {
            return;
        }

        // Check if it's already a FixedBox
        if (box instanceof FixedBox) {
            String location = "R" + (row + 1) + "-C" + (col + 1);
            throw new BoxAlreadyFixedException(
                    "The selected box is already a FixedBox and cannot be fixed again. Continuing to the next turn...",
                    location
            );
        }

        // Convert the box to a FixedBox
        // Get the current top side letter
        Letter topLetter = box.getTopSide();

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
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return false;
        }

        Box box = grid.getBox(row, col);

        // Can't fix null boxes or already-fixed boxes
        return box != null && !(box instanceof FixedBox);
    }

    /**
     * Parses a box location string to extract row and column indices.
     * Accepts formats like "R5-C3", "5-3", "r5-c3", etc.
     *
     * @param location The location string
     * @return An int array [row, col] in 0-7 range, or null if invalid
     */
    public static int[] parseLocation(String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }

        // Clean and split the input
        String cleaned = location.toUpperCase().replace("R", "").replace("C", "").trim();
        String[] parts = cleaned.split("-");

        if (parts.length != 2) {
            return null;
        }

        try {
            int row = Integer.parseInt(parts[0].trim()) - 1;  // Convert to 0-7 index
            int col = Integer.parseInt(parts[1].trim()) - 1;  // Convert to 0-7 index

            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                return new int[]{row, col};
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }
}