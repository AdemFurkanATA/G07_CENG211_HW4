package com.models.boxes;

import com.enums.Letter;
import com.enums.Direction;
import com.models.tools.SpecialTool;

/**
 * FixedBox is a special box type that cannot be moved or rolled.
 *
 * Characteristics:
 * - Has a 5% chance of being generated
 * - NEVER contains a SpecialTool (always empty)
 * - Marked as "opened" from the start (status is always "O")
 * - CANNOT be rolled in any direction
 * - Its top side stays the same at all times
 * - Stops the domino-effect from being transmitted past it
 * - If an edge FixedBox is selected for rolling, throws UnmovableFixedBoxException
 * - Cannot be flipped using BoxFlipper (throws UnmovableFixedBoxException)
 * - Attempting to fix it with BoxFixer throws BoxAlreadyFixedException
 *
 * FixedBoxes act as immovable obstacles in the grid, blocking the rolling
 * chain effect when boxes are rolled from edges.
 */
public class FixedBox extends Box {

    /**
     * Constructor for FixedBox with randomly generated surfaces.
     * FixedBox is always empty and marked as opened.
     */
    public FixedBox() {
        super();
        this.containedTool = null;  // FixedBox never contains a tool
        this.isOpened = true;       // FixedBox is always marked as opened
    }

    /**
     * Constructor for FixedBox with specified surfaces.
     * Used when converting another box to a FixedBox using BoxFixer.
     *
     * @param surfaces Array of 6 letters for each surface
     */
    public FixedBox(Letter[] surfaces) {
        super(surfaces);
        this.containedTool = null;  // FixedBox never contains a tool
        this.isOpened = true;       // FixedBox is always marked as opened
    }

    /**
     * Checks if this FixedBox can be rolled.
     * FixedBoxes CANNOT be rolled under any circumstances.
     *
     * @return false - FixedBoxes can never be rolled
     */
    @Override
    public boolean canRoll() {
        return false;
    }

    /**
     * Gets the type identifier for display in the grid.
     *
     * @return "X" for FixedBox
     */
    @Override
    public String getTypeIdentifier() {
        return "X";
    }

    /**
     * Attempts to roll the FixedBox.
     *
     * FixedBoxes cannot be rolled, so this method overrides the parent
     * implementation and does nothing. The box remains in its original
     * orientation regardless of the direction parameter.
     *
     * SECURITY: Intentionally does nothing - this is the correct behavior.
     * The wasRolledThisTurn flag is NOT set to true.
     *
     * @param direction The direction to roll (ignored)
     */
    @Override
    public void roll(Direction direction) {
        // SECURITY: FixedBox explicitly cannot be rolled
        // Intentionally do nothing - no state change
        // wasRolledThisTurn remains false
    }

    /**
     * Attempts to flip the FixedBox.
     *
     * FixedBoxes cannot be flipped, so this method overrides the parent
     * implementation and does nothing. The top and bottom sides remain unchanged.
     *
     * SECURITY: Intentionally does nothing - this is the correct behavior.
     */
    @Override
    public void flip() {
        // SECURITY: FixedBox explicitly cannot be flipped
        // Intentionally do nothing - no state change
    }

    /**
     * Stamps a new letter on the top side of the box.
     *
     * FixedBoxes CAN be stamped (their immobility doesn't prevent stamping).
     * This allows SpecialTools like PlusShapeStamp, MassRowStamp, etc. to
     * stamp FixedBoxes even though they cannot be moved.
     *
     * @param letter The letter to stamp
     */
    @Override
    public void stampTopSide(Letter letter) {
        // FixedBox can be stamped (only rolling/flipping is prevented)
        surfaces[TOP] = letter;
    }

    /**
     * Opens the FixedBox.
     *
     * FixedBoxes are always empty, so this always returns null.
     * However, the method can still be called (though it serves no purpose).
     *
     * @return null - FixedBox never contains a tool
     */
    @Override
    public SpecialTool open() {
        // FixedBox is always empty
        isOpened = true;  // Already true, but maintain consistency
        return null;
    }

    /**
     * Checks if the box is empty.
     *
     * @return true - FixedBox is always empty
     */
    @Override
    public boolean isEmpty() {
        return true;  // FixedBox is always empty
    }

    /**
     * Sets the tool inside this box.
     *
     * FixedBox cannot contain tools, so this method does nothing.
     * This prevents tools from being placed in FixedBoxes.
     *
     * SECURITY: Intentionally does nothing to enforce invariant.
     *
     * @param tool The SpecialTool to place inside (ignored)
     */
    @Override
    public void setContainedTool(SpecialTool tool) {
        // SECURITY: FixedBox explicitly cannot contain tools
        // Intentionally do nothing - containedTool remains null
        // This enforces the invariant that FixedBox is always empty
    }

    /**
     * Returns a detailed string representation of the FixedBox.
     * FixedBox always shows status as "O" (opened/empty).
     *
     * @return String representation with type, top letter, and status
     */
    @Override
    public String toString() {
        // FixedBox always shows "O" status (empty)
        return "| " + getTypeIdentifier() + "-" + surfaces[TOP] + "-O |";
    }
}