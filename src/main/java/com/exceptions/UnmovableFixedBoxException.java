package com.exceptions;

/**
 * Exception thrown when a player attempts to move or flip a FixedBox.
 *
 * This exception is thrown in the following scenarios:
 * - An edge FixedBox is selected during the first stage of a turn for rolling
 * - A BoxFlipper tool is used on a FixedBox
 *
 * FixedBoxes cannot be rolled in any direction and their top side stays the same
 * at all times. They also stop the domino-effect from being transmitted past them.
 *
 * When this exception is thrown, the current turn is wasted and the game
 * proceeds to the next turn.
 */
public class UnmovableFixedBoxException extends Exception {

    private String boxLocation;  // The location of the FixedBox (e.g., "R3-C8")
    private String attemptedAction;  // The action that was attempted (e.g., "roll", "flip")

    /**
     * Constructs a new UnmovableFixedBoxException with a default message.
     */
    public UnmovableFixedBoxException() {
        super("HOWEVER, IT IS FIXED BOX AND CANNOT BE MOVED. Continuing to the next turn...");
    }

    /**
     * Constructs a new UnmovableFixedBoxException with a custom message.
     *
     * @param message The detail message explaining why the exception occurred
     */
    public UnmovableFixedBoxException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnmovableFixedBoxException with a custom message and the box location.
     *
     * @param message The detail message
     * @param boxLocation The location of the FixedBox (e.g., "R3-C8")
     */
    public UnmovableFixedBoxException(String message, String boxLocation) {
        super(message);
        this.boxLocation = boxLocation;
    }

    /**
     * Constructs a new UnmovableFixedBoxException with full details.
     *
     * @param message The detail message
     * @param boxLocation The location of the FixedBox
     * @param attemptedAction The action that was attempted (e.g., "roll", "flip")
     */
    public UnmovableFixedBoxException(String message, String boxLocation, String attemptedAction) {
        super(message);
        this.boxLocation = boxLocation;
        this.attemptedAction = attemptedAction;
    }

    /**
     * Gets the location of the FixedBox that couldn't be moved.
     *
     * @return The box location string, or null if not set
     */
    public String getBoxLocation() {
        return boxLocation;
    }

    /**
     * Sets the location of the FixedBox.
     *
     * @param boxLocation The box location (e.g., "R3-C8")
     */
    public void setBoxLocation(String boxLocation) {
        this.boxLocation = boxLocation;
    }

    /**
     * Gets the action that was attempted on the FixedBox.
     *
     * @return The attempted action (e.g., "roll", "flip"), or null if not set
     */
    public String getAttemptedAction() {
        return attemptedAction;
    }

    /**
     * Sets the action that was attempted.
     *
     * @param attemptedAction The attempted action
     */
    public void setAttemptedAction(String attemptedAction) {
        this.attemptedAction = attemptedAction;
    }
}