package com.models.boxes;

import com.enums.Letter;
import com.enums.Direction;
import com.models.tools.SpecialTool;

/**
 * Abstract base class representing a cubic box in the puzzle game.
 *
 * Each box has 6 surfaces with letters stamped on them. The surfaces are:
 * - Index 0: TOP
 * - Index 1: BOTTOM
 * - Index 2: FRONT
 * - Index 3: BACK
 * - Index 4: LEFT
 * - Index 5: RIGHT
 *
 * At the start of the game, each box cannot have the same letter more than twice.
 * However, after using SpecialTools, this rule no longer applies.
 *
 * Boxes can be rolled, flipped, and stamped depending on their type.
 */
public abstract class Box {

    // Surface indices
    protected static final int TOP = 0;
    protected static final int BOTTOM = 1;
    protected static final int FRONT = 2;
    protected static final int BACK = 3;
    protected static final int LEFT = 4;
    protected static final int RIGHT = 5;

    protected Letter[] surfaces;           // 6 surfaces of the box
    protected SpecialTool containedTool;   // Tool inside the box (null if empty)
    protected boolean isOpened;            // Whether the box has been opened
    protected boolean wasRolledThisTurn;   // Tracks if box was rolled in current turn

    /**
     * Constructor for Box with randomly generated surfaces.
     * Ensures no letter appears more than twice.
     */
    public Box() {
        this.surfaces = generateRandomSurfaces();
        this.containedTool = null;
        this.isOpened = false;
        this.wasRolledThisTurn = false;
    }

    /**
     * Constructor for Box with specified surfaces.
     * Used when creating copies or specific boxes.
     *
     * @param surfaces Array of 6 letters for each surface
     */
    public Box(Letter[] surfaces) {
        if (surfaces == null || surfaces.length != 6) {
            this.surfaces = generateRandomSurfaces();
        } else {
            this.surfaces = surfaces.clone();
        }
        this.containedTool = null;
        this.isOpened = false;
        this.wasRolledThisTurn = false;
    }

    /**
     * Generates random surfaces for the box.
     * Ensures no letter appears more than twice.
     *
     * SECURITY: Added protection against infinite loop with MAX_ATTEMPTS.
     *
     * @return Array of 6 randomly generated letters
     */
    protected Letter[] generateRandomSurfaces() {
        Letter[] newSurfaces = new Letter[6];
        int[] letterCounts = new int[8];  // Count for each letter A-H

        for (int i = 0; i < 6; i++) {
            Letter randomLetter;
            int letterIndex;
            int attempts = 0;
            final int MAX_ATTEMPTS = 100;  // Safety limit

            // Keep trying until we find a letter that hasn't been used twice
            do {
                randomLetter = Letter.getRandomLetter();
                letterIndex = randomLetter.ordinal();
                attempts++;

                // SECURITY: Prevent infinite loop (though mathematically unlikely)
                if (attempts >= MAX_ATTEMPTS) {
                    // Fallback: Find first available letter
                    for (int j = 0; j < 8; j++) {
                        if (letterCounts[j] < 2) {
                            randomLetter = Letter.values()[j];
                            letterIndex = j;
                            System.err.println("WARNING: generateRandomSurfaces() hit max attempts, using fallback");
                            break;
                        }
                    }
                    break;
                }
            } while (letterCounts[letterIndex] >= 2);

            newSurfaces[i] = randomLetter;
            letterCounts[letterIndex]++;
        }

        return newSurfaces;
    }

    /**
     * Gets the letter on the top side of the box.
     *
     * @return The letter on top
     */
    public Letter getTopSide() {
        return surfaces[TOP];
    }

    /**
     * Gets the letter on the bottom side of the box.
     *
     * @return The letter on bottom
     */
    public Letter getBottomSide() {
        return surfaces[BOTTOM];
    }

    /**
     * Gets all surfaces of the box.
     *
     * @return Array of 6 letters (defensive copy)
     */
    public Letter[] getSurfaces() {
        return surfaces.clone();
    }

    /**
     * Stamps a new letter on the top side of the box.
     * This method can be overridden by subclasses (e.g., UnchangingBox).
     *
     * @param letter The letter to stamp
     */
    public void stampTopSide(Letter letter) {
        surfaces[TOP] = letter;
    }

    /**
     * Flips the box upside down (swaps top and bottom).
     */
    public void flip() {
        Letter temp = surfaces[TOP];
        surfaces[TOP] = surfaces[BOTTOM];
        surfaces[BOTTOM] = temp;
    }

    /**
     * Rolls the box in a specified direction.
     * The surfaces rotate according to the direction of the roll.
     *
     * SECURITY: Added null check for direction parameter.
     *
     * @param direction The direction to roll (UP, DOWN, LEFT, RIGHT)
     * @throws IllegalArgumentException if direction is null
     */
    public void roll(Direction direction) {
        // SECURITY: Validate direction is not null
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }

        Letter temp;

        switch (direction) {
            case RIGHT:
                // LEFT → TOP → RIGHT → BOTTOM → LEFT
                temp = surfaces[LEFT];
                surfaces[LEFT] = surfaces[BOTTOM];
                surfaces[BOTTOM] = surfaces[RIGHT];
                surfaces[RIGHT] = surfaces[TOP];
                surfaces[TOP] = temp;
                break;

            case LEFT:
                // RIGHT → TOP → LEFT → BOTTOM → RIGHT
                temp = surfaces[RIGHT];
                surfaces[RIGHT] = surfaces[BOTTOM];
                surfaces[BOTTOM] = surfaces[LEFT];
                surfaces[LEFT] = surfaces[TOP];
                surfaces[TOP] = temp;
                break;

            case DOWN:
                // FRONT → TOP → BACK → BOTTOM → FRONT
                temp = surfaces[FRONT];
                surfaces[FRONT] = surfaces[BOTTOM];
                surfaces[BOTTOM] = surfaces[BACK];
                surfaces[BACK] = surfaces[TOP];
                surfaces[TOP] = temp;
                break;

            case UP:
                // BACK → TOP → FRONT → BOTTOM → BACK
                temp = surfaces[BACK];
                surfaces[BACK] = surfaces[BOTTOM];
                surfaces[BOTTOM] = surfaces[FRONT];
                surfaces[FRONT] = surfaces[TOP];
                surfaces[TOP] = temp;
                break;
        }

        wasRolledThisTurn = true;
    }

    /**
     * Checks if the box can be rolled.
     * Abstract method to be implemented by subclasses.
     *
     * @return true if the box can be rolled, false otherwise
     */
    public abstract boolean canRoll();

    /**
     * Opens the box and retrieves the tool inside.
     *
     * @return The SpecialTool inside, or null if empty
     */
    public SpecialTool open() {
        isOpened = true;
        SpecialTool tool = containedTool;
        containedTool = null;  // Remove tool after opening
        return tool;
    }

    /**
     * Checks if the box is empty (no tool inside).
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return containedTool == null;
    }

    /**
     * Checks if the box has been opened.
     *
     * @return true if opened, false otherwise
     */
    public boolean isOpened() {
        return isOpened;
    }

    /**
     * Sets the tool inside this box.
     *
     * @param tool The SpecialTool to place inside
     */
    public void setContainedTool(SpecialTool tool) {
        this.containedTool = tool;
    }

    /**
     * Checks if the box was rolled during the current turn.
     *
     * @return true if rolled this turn, false otherwise
     */
    public boolean wasRolledThisTurn() {
        return wasRolledThisTurn;
    }

    /**
     * Resets the rolled status for a new turn.
     */
    public void resetRolledStatus() {
        wasRolledThisTurn = false;
    }

    /**
     * Gets the box type identifier for display.
     * Abstract method to be implemented by subclasses.
     *
     * @return "R" for RegularBox, "U" for UnchangingBox, "X" for FixedBox
     */
    public abstract String getTypeIdentifier();

    /**
     * Displays all surfaces of the box in a cube diagram format.
     * Shows the box from all angles with the top side in the middle.
     *
     * @return String representation of the cube
     */
    public String displayCube() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----\n");
        sb.append("| ").append(surfaces[BACK]).append(" |\n");
        sb.append("-------------\n");
        sb.append("| ").append(surfaces[LEFT]).append(" | ").append(surfaces[TOP]).append(" | ").append(surfaces[RIGHT]).append(" |\n");
        sb.append("-------------\n");
        sb.append("| ").append(surfaces[FRONT]).append(" |\n");
        sb.append("-----\n");
        sb.append("| ").append(surfaces[BOTTOM]).append(" |\n");
        sb.append("-----");
        return sb.toString();
    }

    /**
     * Returns a string representation for the grid display.
     * Format: | TYPE-LETTER-STATUS |
     *
     * @return String representation
     */
    @Override
    public String toString() {
        String status = isOpened ? "O" : "M";
        return "| " + getTypeIdentifier() + "-" + surfaces[TOP] + "-" + status + " |";
    }
}