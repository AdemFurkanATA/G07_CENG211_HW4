package com.models.tools;

import com.game.BoxGrid;
import com.enums.Letter;

/**
 * Abstract base class for mass stamping tools (row and column).
 * Provides common functionality for stamping multiple boxes at once.
 *
 * This class serves as a middle layer between SpecialTool and the
 * concrete mass stamping implementations (MassRowStamp, MassColumnStamp).
 */
public abstract class MassStamp extends SpecialTool {

    /**
     * Constructor for MassStamp tools.
     *
     * @param name The name of the tool
     * @param description The description of the tool's effect
     */
    protected MassStamp(String name, String description) {
        super(name, description);
    }

    /**
     * Validates if a grid index (row or column) is valid.
     *
     * @param index The index to validate (0-7)
     * @return true if valid, false otherwise
     */
    protected boolean isValidIndex(int index) {
        return index >= MIN_INDEX && index <= MAX_INDEX;
    }

    /**
     * Validates a grid index and throws an exception if invalid.
     *
     * @param index The index to validate
     * @param indexType The type of index ("row" or "column") for error message
     * @throws IllegalArgumentException if index is out of bounds
     */
    protected void validateIndex(int index, String indexType) {
        if (!isValidIndex(index)) {
            throw new IllegalArgumentException("Invalid " + indexType + " index: " + index + " (valid range: " + MIN_INDEX + "-" + MAX_INDEX + ")");
        }
    }

    /**
     * Validates grid and target letter inputs.
     *
     * @param grid The BoxGrid
     * @param targetLetter The target letter
     * @throws IllegalArgumentException if any input is null
     */
    protected void validateInputs(BoxGrid grid, Letter targetLetter) {
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null");
        }
        if (targetLetter == null) {
            throw new IllegalArgumentException("Target letter cannot be null");
        }
    }

    /**
     * Parses a grid index string (e.g., "R3", "C5", "3", "5").
     * Removes the optional prefix and converts to 0-based index.
     *
     * @param input The input string
     * @param prefix The expected prefix ('R' or 'C'), case-insensitive
     * @return The index (0-7)
     * @throws IllegalArgumentException if input is invalid
     */
    protected static int parseGridIndex(String input, char prefix) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        // Remove prefix (case-insensitive) and trim
        String cleaned = input.toUpperCase().replace(String.valueOf(prefix), "").trim();

        try {
            int num = Integer.parseInt(cleaned);

            // Validate range (user input is 1-8, convert to 0-7)
            if (num >= MIN_USER_INPUT && num <= MAX_USER_INPUT) {
                return num - 1;
            }

            throw new IllegalArgumentException(
                    "Number must be between " + MIN_USER_INPUT +
                            " and " + MAX_USER_INPUT + ", got: " + num
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid format: '" + input + "'. Expected format: " +
                            prefix + "1-" + prefix + "8 or 1-8"
            );
        }
    }
}