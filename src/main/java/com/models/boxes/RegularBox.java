package com.models.boxes;

import com.enums.Letter;
import com.models.tools.SpecialTool;

/**
 * RegularBox is the standard box type in the puzzle game.
 *
 * Characteristics:
 * - Has an 85% chance of being generated
 * - Has a 75% chance of containing a SpecialTool (25% chance of being empty)
 * - Each SpecialTool has a 15% chance when the box contains a tool
 * - Can be rolled in any direction
 * - Can be stamped, flipped, and converted to FixedBox
 * - Surfaces can be changed by SpecialTools
 *
 * This is the most common box type and serves as the default box in the game.
 */
public class RegularBox extends Box {

    /**
     * Constructor for RegularBox with randomly generated surfaces.
     * Automatically generates and assigns a SpecialTool based on probability.
     * 75% chance of containing a tool, 25% chance of being empty.
     */
    public RegularBox() {
        super();
        // Generate tool with 75% probability (25% empty)
        this.containedTool = SpecialTool.generateRandomToolForRegularBox();
    }

    /**
     * Constructor for RegularBox with specified surfaces.
     * Used when creating copies or specific boxes.
     *
     * @param surfaces Array of 6 letters for each surface
     */
    public RegularBox(Letter[] surfaces) {
        super(surfaces);
        // Generate tool with 75% probability (25% empty)
        this.containedTool = SpecialTool.generateRandomToolForRegularBox();
    }

    /**
     * Constructor for RegularBox with specified surfaces and tool.
     * Used for testing or specific box creation scenarios.
     *
     * @param surfaces Array of 6 letters for each surface
     * @param tool The SpecialTool to place inside (null for empty)
     */
    public RegularBox(Letter[] surfaces, SpecialTool tool) {
        super(surfaces);
        this.containedTool = tool;
    }

    /**
     * Checks if this RegularBox can be rolled.
     * RegularBoxes can always be rolled (unless game logic prevents it).
     *
     * @return true - RegularBoxes can always be rolled
     */
    @Override
    public boolean canRoll() {
        return true;
    }

    /**
     * Gets the type identifier for display in the grid.
     *
     * @return "R" for RegularBox
     */
    @Override
    public String getTypeIdentifier() {
        return "R";
    }

    /**
     * Stamps a new letter on the top side of the box.
     * RegularBox surfaces can be freely changed by SpecialTools.
     *
     * @param letter The letter to stamp
     */
    @Override
    public void stampTopSide(Letter letter) {
        // RegularBox allows stamping without restrictions
        surfaces[TOP] = letter;
    }

    /**
     * Returns a detailed string representation of the RegularBox.
     *
     * @return String representation with type, top letter, and status
     */
    @Override
    public String toString() {
        String status = isOpened ? "O" : "M";
        return "| " + getTypeIdentifier() + "-" + surfaces[TOP] + "-" + status + " |";
    }
}