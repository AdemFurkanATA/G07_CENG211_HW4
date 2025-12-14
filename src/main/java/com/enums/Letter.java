package com.enums;

/**
 * Enum representing the possible letters that can be stamped on box surfaces.
 * There are 8 possible letters in total: A, B, C, D, E, F, G, H.
 * Each letter can appear on a box's surface, and the game's goal is to maximize
 * a specific target letter on the top sides of boxes.
 */
public enum Letter {
    A, B, C, D, E, F, G, H;

    /**
     * Returns a random Letter from the available options.
     * This is used for generating random box surfaces and selecting target letters.
     *
     * @return A randomly selected Letter
     */
    public static Letter getRandomLetter() {
        Letter[] letters = Letter.values();
        int randomIndex = (int) (Math.random() * letters.length);
        return letters[randomIndex];
    }

    /**
     * Converts a string representation to a Letter enum.
     * Case-insensitive conversion.
     *
     * @param str The string to convert (e.g., "A", "a", "B", "b")
     * @return The corresponding Letter enum, or null if invalid
     */
    public static Letter fromString(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        try {
            return Letter.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Checks if a given string is a valid letter.
     *
     * @param str The string to validate
     * @return true if the string represents a valid Letter, false otherwise
     */
    public static boolean isValidLetter(String str) {
        return fromString(str) != null;
    }
}