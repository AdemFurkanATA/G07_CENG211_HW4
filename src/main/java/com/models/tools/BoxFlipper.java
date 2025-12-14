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
 * UnchangingBoxes CAN be flipped, but since their letters cannot be changed
 * by SpecialTools, they will appear unchanged after flipping.
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
     * Flips the selected box upside down.
     *
     * @param grid The BoxGrid on which to execute the tool's effect
     * @param targetLetter The target letter (not used by BoxFlipper, but required by interface)
     * @throws Exception if the tool execution fails
     */
    @Override
    protected void execute(BoxGrid grid, Letter targetLetter) throws Exception {
        // The actual implementation will be completed when we have BoxGrid
        // This method will:
        // 1. Ask user for box location via menu (e.g., "R2-C4")
        // 2. Validate the location
        // 3. Check if it's a FixedBox (throw exception if so)
        // 4. Flip the box (swap top and bottom)

        // For now, we'll leave this as a placeholder
        // The menu system will handle user input and call flipBox()
    }

    /**
     * Flips a box at the specified location upside down.
     * This is a helper method that will be called from execute() after
     * getting user input.
     *
     * @param grid The BoxGrid to modify
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @throws UnmovableFixedBoxException if the box is a FixedBox
     */
    public void flipBox(BoxGrid grid, int row, int col) throws UnmovableFixedBoxException {
        // Validate coordinates
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return;
        }

        Box box = grid.getBox(row, col);

        if (box == null) {
            return;
        }

        // Check if it's a FixedBox
        if (box instanceof FixedBox) {
            String location = "R" + (row + 1) + "-C" + (col + 1);
            throw new UnmovableFixedBoxException(
                    "Cannot flip a FixedBox! Continuing to the next turn...",
                    location,
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
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return false;
        }

        Box box = grid.getBox(row, col);

        // Can't flip null boxes or FixedBoxes
        return box != null && !(box instanceof FixedBox);
    }

    /**
     * Parses a box location string to extract row and column indices.
     * Accepts formats like "R2-C4", "2-4", "r2-c4", etc.
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