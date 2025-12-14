package com.exceptions;

/**
 * Exception thrown when a player attempts to use a BoxFixer tool on a box
 * that is already a FixedBox.
 *
 * The BoxFixer tool is designed to replace a RegularBox or UnchangingBox with
 * an identical FixedBox copy. However, if the selected box is already a FixedBox,
 * this exception is thrown.
 *
 * When this exception is thrown, the current turn is wasted and the game
 * proceeds to the next turn. The BoxFixer tool is also consumed.
 */
public class BoxAlreadyFixedException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String boxLocation;

    /**
     * Constructs a new BoxAlreadyFixedException with a default message.
     */
    public BoxAlreadyFixedException() {
        super("The selected box is already a FixedBox and cannot be fixed again. Continuing to the next turn...");
        this.boxLocation = null;
    }

    /**
     * Constructs a new BoxAlreadyFixedException with a custom message.
     * @param message The detail message explaining why the exception occurred
     */
    public BoxAlreadyFixedException(String message) {
        super(message != null ? message : "The selected box is already a FixedBox and cannot be fixed again. Continuing to the next turn...");
        this.boxLocation = null;
    }

    /**
     * Constructs a new BoxAlreadyFixedException with a custom message and the box location.
     * @param message The detail message
     * @param boxLocation The location of the already-fixed box (e.g., "R3-C8")
     */
    public BoxAlreadyFixedException(String message, String boxLocation) {
        super(message != null ? message : "The selected box is already a FixedBox and cannot be fixed again. Continuing to the next turn...");

        this.boxLocation = boxLocation != null ? boxLocation : null;
    }

    /**
     * Gets the location of the box that was already fixed.
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