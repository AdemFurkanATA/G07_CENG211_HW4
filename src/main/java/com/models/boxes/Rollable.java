package com.models.boxes;

import com.enums.Direction;

/**
 * Interface for objects that can be rolled in different directions.
 * This interface defines the contract for rolling behavior in the puzzle game.
 *
 * Boxes that implement this interface can be rolled in cardinal directions,
 * which changes their surface orientation (like rolling a die).
 *
 * @author Adem Furkan ATA - Erkan Arıkan - Utku Kavzoğlu - İsmail Özkaya
 * @version 1.0
 */
public interface Rollable {

    /**
     * Rolls the object in the specified direction.
     * The surfaces rotate according to the direction of the roll.
     *
     * @param direction The direction to roll (UP, DOWN, LEFT, RIGHT)
     * @throws IllegalArgumentException if direction is null
     */
    void roll(Direction direction);

    /**
     * Checks if this object can be rolled.
     * Some boxes (like FixedBox) cannot be rolled.
     *
     * @return true if the object can be rolled, false otherwise
     */
    boolean canRoll();

    /**
     * Checks if this object was rolled during the current turn.
     * Used to track which boxes were affected during the first stage.
     *
     * @return true if rolled this turn, false otherwise
     */
    boolean wasRolledThisTurn();

    /**
     * Resets the rolled status for a new turn.
     * Called at the beginning of each turn to clear the previous turn's state.
     */
    void resetRolledStatus();
}