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

    private static final long serialVersionUID = 2L;

    private final String boxLocation;
    private final String attemptedAction;

    /**
     * Constructs a new UnmovableFixedBoxException with a default message.
     */
    public UnmovableFixedBoxException() {
        super("HOWEVER, IT IS FIXED BOX AND CANNOT BE MOVED. Continuing to the next turn...");
        this.boxLocation = null;
        this.attemptedAction = null;
    }

    /**
     * Constructs a new UnmovableFixedBoxException with a custom message.
     * @param message The detail message explaining why the exception occurred
     */
    public UnmovableFixedBoxException(String message) {
        super(message != null ? message : "HOWEVER, IT IS FIXED BOX AND CANNOT BE MOVED. Continuing to the next turn...");
        this.boxLocation = null;
        this.attemptedAction = null;
    }

    /**
     * Constructs a new UnmovableFixedBoxException with a custom message and the box location.
     * @param message The detail message
     * @param boxLocation The location of the FixedBox (e.g., "R3-C8")
     */
    public UnmovableFixedBoxException(String message, String boxLocation) {
        super(message != null ? message : "HOWEVER, IT IS FIXED BOX AND CANNOT BE MOVED. Continuing to the next turn...");
        this.boxLocation = boxLocation;
        this.attemptedAction = null;
    }

    /**
     * Constructs a new UnmovableFixedBoxException with full details.
     * @param message The detail message
     * @param boxLocation The location of the FixedBox
     * @param attemptedAction The action that was attempted (e.g., "roll", "flip")
     */
    public UnmovableFixedBoxException(String message, String boxLocation, String attemptedAction) {
        super(message != null ? message : "HOWEVER, IT IS FIXED BOX AND CANNOT BE MOVED. Continuing to the next turn...");
        this.boxLocation = boxLocation;
        this.attemptedAction = attemptedAction;
    }

    /**
     * Gets the location of the FixedBox that couldn't be moved.
     * @return The box location string, or null if not set
     */
    public String getBoxLocation() {
        return boxLocation;
    }

    /**
     * Gets the action that was attempted on the FixedBox.
     * @return The attempted action (e.g., "roll", "flip"), or null if not set
     */
    public String getAttemptedAction() {
        return attemptedAction;
    }

    /**
     * Returns a detailed string representation of this exception.
     * @return String containing exception details
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());

        String message = getMessage();
        if (message != null) {
            sb.append(": ").append(message);
        }

        if (boxLocation != null) {
            sb.append(" [Location: ").append(boxLocation);

            if (attemptedAction != null) {
                sb.append(", Attempted Action: ").append(attemptedAction);
            }

            sb.append("]");
        }

        return sb.toString();
    }
}