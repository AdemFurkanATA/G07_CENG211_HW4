package com.models.boxes;

import com.enums.Letter;

/**
 * Interface for objects that can have their surfaces stamped with letters.
 * This interface defines the contract for stamping behavior in the puzzle game.
 *
 * Objects implementing this interface can have letters stamped on their top surface
 * using various SpecialTools. The actual behavior may vary by implementation
 * (e.g., UnchangingBox ignores stamp operations).
 *
 * @author Adem Furkan ATA - Erkan Arıkan - Utku Kavzoğlu - İsmail Özkaya
 * @version 1.0
 */
public interface Stampable {

    /**
     * Stamps a new letter on the top side of the object.
     * This method is called by SpecialTools to change the top surface letter.
     *
     * Implementation notes:
     * - RegularBox: Accepts the stamp and changes its top side
     * - UnchangingBox: Appears to accept but ignores the stamp
     * - FixedBox: Accepts the stamp and changes its top side
     *
     * @param letter The letter to stamp on the top side
     * @throws IllegalArgumentException if letter is null
     */
    void stampTopSide(Letter letter);

    /**
     * Gets the letter currently displayed on the top side.
     * This is the visible letter when looking at the grid.
     *
     * @return The letter on the top side (never null)
     */
    Letter getTopSide();
}