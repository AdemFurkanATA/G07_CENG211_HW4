package com.models.boxes;

import com.enums.Letter;

/**
 * Interface for objects that can be flipped upside down.
 * This interface defines the contract for flipping behavior in the puzzle game.
 *
 * Objects implementing this interface can be flipped using the BoxFlipper tool,
 * which swaps the top and bottom sides of the object.
 *
 * Note: FixedBoxes cannot be flipped (their flip() method does nothing).
 *
 * @author Adem Furkan ATA - Erkan Arıkan - Utku Kavzoğlu - İsmail Özkaya
 * @version 1.0
 */
public interface Flippable {

    /**
     * Flips the object upside down.
     *
     * This operation swaps the top and bottom surfaces:
     * - The current top side becomes the new bottom side
     * - The current bottom side becomes the new top side
     *
     * Implementation notes:
     * - RegularBox and UnchangingBox: Perform the flip operation
     * - FixedBox: Does nothing (cannot be flipped)
     *
     * When a BoxFlipper tool is used on a FixedBox, an
     * UnmovableFixedBoxException is thrown before this method is called.
     */
    void flip();

    /**
     * Gets the letter on the bottom side of the object.
     *
     * This is useful for:
     * - Previewing what letter will appear on top after flipping
     * - Displaying all surfaces of a box in the cube diagram
     *
     * @return The letter on the bottom side (never null)
     */
    Letter getBottomSide();

    /**
     * Gets all surfaces of the object.
     *
     * Returns a defensive copy of the 6 surfaces:
     * - Index 0: TOP
     * - Index 1: BOTTOM
     * - Index 2: FRONT
     * - Index 3: BACK
     * - Index 4: LEFT
     * - Index 5: RIGHT
     *
     * @return Array of 6 letters representing all surfaces
     */
    Letter[] getSurfaces();
}