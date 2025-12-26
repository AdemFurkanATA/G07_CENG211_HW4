package com;

import com.game.BoxPuzzle;

/**
 * Main application class for the Box Top Side Matching Puzzle App.
 *
 * This is the entry point for the puzzle game application.
 * It creates a new BoxPuzzle instance and starts the game.
 *
 * The game simulates a puzzle involving an 8x8 grid of cubic boxes
 * where the goal is to maximize a target letter on the top sides
 * of boxes through rolling and using special tools.
 *
 * @author Adem Furkan ATA - Erkan Arıkan - Utku Kavzoğlu
 * @version 1.0
 */
public class BoxPuzzleApp {

    /**
     * Main method - entry point of the application.
     * Creates and starts a new Box Puzzle game.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            BoxPuzzle game = new BoxPuzzle();

            game.start();

        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}