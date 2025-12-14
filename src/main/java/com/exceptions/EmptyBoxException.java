package com.exceptions;

/**
 * Exception thrown when a player attempts to open a box that is empty.
 *
 * This exception is thrown in the following scenarios:
 * - A RegularBox is opened but contains no SpecialTool (25% chance)
 * - A FixedBox is opened (always empty)
 * - A box that has already been opened in a previous turn is opened again
 *
 * When this exception is thrown, the current turn is wasted and the game
 * proceeds to the next turn.
 */
public class EmptyBoxException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String boxLocation;

    /**
     * Constructs a new EmptyBoxException with a default message.
     */
    public EmptyBoxException() {
        super("BOX IS EMPTY! Continuing to the next turn...");
        this.boxLocation = null;
    }

    /**
     * Constructs a new EmptyBoxException with a custom message.
     * @param message The detail message explaining why the exception occurred
     */
    public EmptyBoxException(String message) {
        super(message != null ? message : "BOX IS EMPTY! Continuing to the next turn...");
        this.boxLocation = null;
    }

    /**
     * Constructs a new EmptyBoxException with a custom message and the box location.
     * @param message The detail message
     * @param boxLocation The location of the empty box (e.g., "R1-C2")
     */
    public EmptyBoxException(String message, String boxLocation) {
        super(message != null ? message : "BOX IS EMPTY! Continuing to the next turn...");

        this.boxLocation = boxLocation != null ? boxLocation : null;
    }

    /**
     * Gets the location of the box that was empty.
     * @return The box location string, or null if not set
     */
    public String getBoxLocation() {
        return boxLocation;
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
            sb.append(" [Location: ").append(boxLocation).append("]");
        }

        return sb.toString();
    }
}