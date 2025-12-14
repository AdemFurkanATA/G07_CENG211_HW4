package com.models.boxes;

import com.enums.Letter;
import com.models.tools.SpecialTool;

/**
 * UnchangingBox is a special box type that cannot have its surfaces changed.
 *
 * Characteristics:
 * - Has a 10% chance of being generated
 * - ALWAYS contains a SpecialTool (guaranteed, never empty)
 * - Each SpecialTool has a 20% chance (5 tools × 20% = 100%)
 * - Can be rolled in any direction
 * - Can be flipped upside down
 * - CANNOT have its surface letters changed by any SpecialTool
 * - When stamped, appears to accept the stamp but the top side remains unchanged
 * - Can be converted to a FixedBox (but its unchanging nature is lost)
 *
 * The key feature is that no SpecialTool can change the letters on its surfaces,
 * even though the stamping operation appears to work.
 */
public class UnchangingBox extends Box {

    /**
     * Constructor for UnchangingBox with randomly generated surfaces.
     * Automatically generates and assigns a SpecialTool (guaranteed).
     * Each tool has a 20% chance.
     */
    public UnchangingBox() {
        super();
        // UnchangingBox ALWAYS contains a tool (each tool has 20% chance)
        this.containedTool = SpecialTool.generateRandomTool();
    }

    /**
     * Constructor for UnchangingBox with specified surfaces.
     * Used when creating copies or specific boxes.
     *
     * @param surfaces Array of 6 letters for each surface
     */
    public UnchangingBox(Letter[] surfaces) {
        super(surfaces);
        // UnchangingBox ALWAYS contains a tool
        this.containedTool = SpecialTool.generateRandomTool();
    }

    /**
     * Constructor for UnchangingBox with specified surfaces and tool.
     * Used for testing or specific box creation scenarios.
     *
     * @param surfaces Array of 6 letters for each surface
     * @param tool The SpecialTool to place inside
     */
    public UnchangingBox(Letter[] surfaces, SpecialTool tool) {
        super(surfaces);
        this.containedTool = tool;
    }

    /**
     * Checks if this UnchangingBox can be rolled.
     * UnchangingBoxes can be rolled (only their letters cannot be changed).
     *
     * @return true - UnchangingBoxes can be rolled
     */
    @Override
    public boolean canRoll() {
        return true;
    }

    /**
     * Gets the type identifier for display in the grid.
     *
     * @return "U" for UnchangingBox
     */
    @Override
    public String getTypeIdentifier() {
        return "U";
    }

    /**
     * Attempts to stamp a new letter on the top side of the box.
     *
     * CRITICAL: UnchangingBox surfaces CANNOT be changed by any SpecialTool.
     * This method appears to accept the stamp operation (no error is thrown),
     * but the top side letter remains unchanged.
     *
     * This is the key feature that distinguishes UnchangingBox from other boxes.
     *
     * @param letter The letter attempting to be stamped (will be ignored)
     */
    @Override
    public void stampTopSide(Letter letter) {
        // Do nothing - UnchangingBox cannot have its surfaces changed
        // The operation appears successful but has no effect
        // This prevents SpecialTools from changing the letters
    }

    /**
     * Returns a detailed string representation of the UnchangingBox.
     *
     * @return String representation with type, top letter, and status
     */
    @Override
    public String toString() {
        String status = isOpened ? "O" : "M";
        return "| " + getTypeIdentifier() + "-" + surfaces[TOP] + "-" + status + " |";
    }
}