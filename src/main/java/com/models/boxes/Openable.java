package com.models.boxes;

import com.models.tools.SpecialTool;

/**
 * Interface for objects that can be opened to retrieve contents.
 * This interface defines the contract for opening behavior in the puzzle game.
 *
 * Boxes implementing this interface can be opened during the second stage
 * of each turn to acquire SpecialTools. Once opened, a box's contents are
 * removed and it becomes empty.
 *
 * @author Adem Furkan ATA - Erkan Arıkan
 * @version 1.0
 */
public interface Openable {

    /**
     * Opens the object and retrieves the SpecialTool inside.
     * After opening, the box becomes empty and its contents are removed.
     *
     * This operation can only be performed on boxes that were rolled
     * during the first stage of the current turn.
     *
     * @return The SpecialTool inside, or null if the box is empty
     */
    SpecialTool open();

    /**
     * Checks if the object is empty (contains no SpecialTool).
     *
     * An object can be empty in the following cases:
     * - It was generated empty (RegularBox 25% chance)
     * - It's a FixedBox (always empty)
     * - It has already been opened in a previous turn
     *
     * @return true if empty (no tool inside), false otherwise
     */
    boolean isEmpty();

    /**
     * Checks if the object has been opened.
     *
     * Once opened, a box's contents are removed and it remains empty
     * for the rest of the game. FixedBoxes are marked as opened from
     * the start.
     *
     * @return true if the object has been opened, false otherwise
     */
    boolean isOpened();

    /**
     * Sets the SpecialTool inside this object.
     *
     * This method is used during box generation to place tools inside boxes.
     * FixedBoxes ignore this operation as they cannot contain tools.
     *
     * @param tool The SpecialTool to place inside (null for empty)
     */
    void setContainedTool(SpecialTool tool);
}