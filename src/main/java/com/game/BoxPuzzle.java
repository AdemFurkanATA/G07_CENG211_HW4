package com.game;

import com.enums.Letter;
import com.enums.Direction;
import com.models.boxes.*;
import com.models.tools.*;
import com.exceptions.*;
import java.util.Scanner;
import java.util.List;

/**
 * Main game class that manages the Box Puzzle game.
 * Contains the game loop, turn management, and delegates menu operations
 * to an inner class.
 */
public class BoxPuzzle {

    private static final int MAX_TURNS = 5;

    private BoxGrid grid;
    private Letter targetLetter;
    private int currentTurn;
    private GameMenu menu;
    private boolean gameOver;

    /**
     * Constructor for BoxPuzzle.
     * Initializes the game with a new grid and random target letter.
     */
    public BoxPuzzle() {
        this.grid = new BoxGrid();
        this.targetLetter = Letter.getRandomLetter();
        this.currentTurn = 0;
        this.menu = new GameMenu();
        this.gameOver = false;
    }

    /**
     * Starts and runs the game.
     */
    public void start() {
        menu.displayWelcome();
        menu.displayGrid();

        // Main game loop
        while (currentTurn < MAX_TURNS && !gameOver) {
            currentTurn++;

            // Reset rolled status at the start of each turn
            grid.resetAllRolledStatus();

            System.out.println("\n=====> TURN " + currentTurn + ":");

            // Check if any moves are possible
            if (grid.areAllEdgeBoxesFixed()) {
                System.out.println("No moves can be made (all edge boxes are fixed).");
                gameOver = true;
                displayGameOver(false);
                return;
            }

            // Optional: View box surfaces
            menu.offerBoxViewing();

            try {
                // Stage 1: Roll boxes from edge
                menu.firstStage();

                // Stage 2: Open box and use tool
                menu.secondStage();

            } catch (EmptyBoxException e) {
                System.out.println(e.getMessage());
                // Turn is wasted, continue to next turn
            } catch (UnmovableFixedBoxException e) {
                System.out.println(e.getMessage());
                // Turn is wasted, continue to next turn
            } catch (BoxAlreadyFixedException e) {
                System.out.println(e.getMessage());
                // Turn is wasted, continue to next turn
            }
        }

        // Game finished successfully (completed all turns)
        if (!gameOver) {
            displayGameOver(true);
        }
    }

    /**
     * Displays the game over message and final statistics.
     *
     * @param success true if game completed successfully, false if failed
     */
    private void displayGameOver(boolean success) {
        System.out.println("\n******** GAME OVER ********");
        System.out.println("The final state of the box grid:");
        menu.displayGrid();

        int count = grid.countTargetLetters(targetLetter);
        System.out.println("THE TOTAL NUMBER OF TARGET LETTER \"" + targetLetter + "\" IN THE BOX GRID --> " + count);

        if (success) {
            System.out.println("The game has been SUCCESSFULLY completed!");
        } else {
            System.out.println("The game has FAILED - no more moves can be made.");
        }
    }

    /**
     * Inner class that handles all menu operations and user interactions.
     * This demonstrates the use of Inner Classes as required by the assignment.
     */
    private class GameMenu {

        private Scanner scanner;

        /**
         * Constructor for GameMenu.
         */
        public GameMenu() {
            this.scanner = new Scanner(System.in);
        }

        /**
         * Displays the welcome message.
         */
        public void displayWelcome() {
            System.out.println("Welcome to Box Top Side Matching Puzzle App. An 8x8 box grid is being generated.");
            System.out.println("Your goal is to maximize the letter \"" + targetLetter + "\" on the top sides of the boxes.");
            System.out.println("\nThe initial state of the box grid:");
        }

        /**
         * Displays the current grid state.
         */
        public void displayGrid() {
            System.out.println(grid.displayGrid());
        }

        /**
         * Offers the player the option to view all surfaces of a box.
         */
        public void offerBoxViewing() {
            System.out.print("---> Do you want to view all surfaces of a box? [1] Yes or [2] No? ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                viewBoxSurfaces();
            } else {
                System.out.println("Continuing to the first stage...");
            }
        }

        /**
         * Allows the player to view all surfaces of a selected box.
         */
        private void viewBoxSurfaces() {
            while (true) {
                System.out.print("Please enter the location of the box you want to view: ");
                String location = scanner.nextLine().trim();

                int[] coords = BoxGrid.parseLocation(location);
                if (coords == null) {
                    System.out.println("INCORRECT INPUT: Invalid location format. Please try again.");
                    continue;
                }

                Box box = grid.getBox(coords[0], coords[1]);
                if (box == null) {
                    System.out.println("INCORRECT INPUT: No box at this location. Please try again.");
                    continue;
                }

                System.out.println(box.displayCube());
                break;
            }
        }

        /**
         * Executes the first stage of a turn: rolling boxes from an edge.
         *
         * @throws UnmovableFixedBoxException if a FixedBox is selected from edge
         */
        public void firstStage() throws UnmovableFixedBoxException {
            System.out.println("\n---> TURN " + currentTurn + " – FIRST STAGE:");

            int[] coords = null;
            Box selectedBox = null;

            // Get valid edge box
            while (true) {
                System.out.print("Please enter the location of the edge box you want to roll: ");
                String location = scanner.nextLine().trim();

                coords = BoxGrid.parseLocation(location);
                if (coords == null) {
                    System.out.println("INCORRECT INPUT: Invalid location format. Please reenter the location: ");
                    continue;
                }

                if (!grid.isEdge(coords[0], coords[1])) {
                    System.out.print("INCORRECT INPUT: The chosen box is not on any of the edges. Please reenter the location: ");
                    continue;
                }

                selectedBox = grid.getBox(coords[0], coords[1]);
                break;
            }

            // Check if it's a FixedBox
            if (!selectedBox.canRoll()) {
                throw new UnmovableFixedBoxException(
                        "\nHOWEVER, IT IS FIXED BOX AND CANNOT BE MOVED. Continuing to the next turn...",
                        "R" + (coords[0] + 1) + "-C" + (coords[1] + 1),
                        "roll"
                );
            }

            // Determine direction(s)
            List<Direction> availableDirections = grid.getAvailableDirections(coords[0], coords[1]);
            Direction chosenDirection = null;

            if (availableDirections.size() == 2) {
                // Corner box - ask player to choose
                System.out.print("The chosen box can be rolled to either [1] " +
                        availableDirections.get(0).getDisplayName() + " or [2] " +
                        availableDirections.get(1).getDisplayName() + ": ");

                while (true) {
                    String choice = scanner.nextLine().trim();
                    if (choice.equals("1")) {
                        chosenDirection = availableDirections.get(0);
                        break;
                    } else if (choice.equals("2")) {
                        chosenDirection = availableDirections.get(1);
                        break;
                    } else {
                        System.out.print("INCORRECT INPUT: Please enter 1 or 2: ");
                    }
                }
            } else if (availableDirections.size() == 1) {
                // Edge box - only one direction
                chosenDirection = availableDirections.get(0);
            }

            // Roll the boxes
            grid.rollBoxesFromEdge(coords[0], coords[1], chosenDirection);

            // Check if stopped by FixedBox
            boolean stoppedByFixed = checkIfStoppedByFixedBox(coords[0], coords[1], chosenDirection);

            if (stoppedByFixed) {
                System.out.println("The chosen box and any box on its path have been rolled " +
                        chosenDirection.getDisplayName() + " until a FixedBox has been reached. The new state of the box grid:");
            } else {
                System.out.println("The chosen box and any box on its path have been rolled to the " +
                        chosenDirection.getDisplayName() + ". The new state of the box grid:");
            }

            displayGrid();
        }

        /**
         * Checks if the rolling was stopped by a FixedBox.
         */
        private boolean checkIfStoppedByFixedBox(int startRow, int startCol, Direction direction) {
            int row = startRow;
            int col = startCol;

            while (true) {
                row += direction.getRowDelta();
                col += direction.getColDelta();

                if (row < 0 || row >= 8 || col < 0 || col >= 8) {
                    return false;  // Reached boundary
                }

                Box box = grid.getBox(row, col);
                if (box != null && !box.canRoll()) {
                    return true;  // Stopped by FixedBox
                }
            }
        }

        /**
         * Executes the second stage of a turn: opening a box and using a tool.
         * Uses generics for tool handling as required by the assignment.
         *
         * @throws EmptyBoxException if the box is empty
         * @throws UnmovableFixedBoxException if trying to flip a FixedBox
         * @throws BoxAlreadyFixedException if trying to fix an already-fixed box
         */
        public <T extends SpecialTool> void secondStage()
                throws EmptyBoxException, UnmovableFixedBoxException, BoxAlreadyFixedException {
            System.out.println("\n---> TURN " + currentTurn + " – SECOND STAGE:");

            int[] coords = null;
            Box selectedBox = null;

            // Get valid rolled box
            while (true) {
                System.out.print("Please enter the location of the box you want to open: ");
                String location = scanner.nextLine().trim();

                coords = BoxGrid.parseLocation(location);
                if (coords == null) {
                    System.out.println("INCORRECT INPUT: Invalid location format. Please reenter the location: ");
                    continue;
                }

                selectedBox = grid.getBox(coords[0], coords[1]);

                if (!selectedBox.wasRolledThisTurn()) {
                    System.out.print("INCORRECT INPUT: The chosen box was not rolled during the first stage. Please reenter the location: ");
                    continue;
                }

                break;
            }

            // Open the box
            T acquiredTool = (T) selectedBox.open();

            if (acquiredTool == null) {
                throw new EmptyBoxException("BOX IS EMPTY! Continuing to the next turn...");
            }

            // Tool acquired
            String location = "R" + (coords[0] + 1) + "-C" + (coords[1] + 1);
            System.out.println("The box on location " + location + " is opened. It contains a SpecialTool --> " + acquiredTool.getToolName());

            // Use the tool immediately
            useTool(acquiredTool);

            displayGrid();
        }

        /**
         * Uses the acquired tool with polymorphism and generics.
         * This method demonstrates the required use of generics and polymorphism.
         *
         * @param tool The tool to use (generic type T extends SpecialTool)
         */
        private <T extends SpecialTool> void useTool(T tool)
                throws UnmovableFixedBoxException, BoxAlreadyFixedException {

            if (tool instanceof PlusShapeStamp) {
                usePlusShapeStamp((PlusShapeStamp) tool);
            } else if (tool instanceof MassRowStamp) {
                useMassRowStamp((MassRowStamp) tool);
            } else if (tool instanceof MassColumnStamp) {
                useMassColumnStamp((MassColumnStamp) tool);
            } else if (tool instanceof BoxFlipper) {
                useBoxFlipper((BoxFlipper) tool);
            } else if (tool instanceof BoxFixer) {
                useBoxFixer((BoxFixer) tool);
            }
        }

        /**
         * Handles PlusShapeStamp tool usage.
         */
        private void usePlusShapeStamp(PlusShapeStamp tool) {
            while (true) {
                System.out.print("Please enter the location of the box to use this SpecialTool: ");
                String location = scanner.nextLine().trim();

                int[] coords = BoxGrid.parseLocation(location);
                if (coords == null) {
                    System.out.println("INCORRECT INPUT: Invalid location. Please try again.");
                    continue;
                }

                tool.stampPlusShape(grid, coords[0], coords[1], targetLetter);
                System.out.println("Top sides of the chosen box (R" + (coords[0]+1) + "-C" + (coords[1]+1) +
                        ") and its surrounding boxes have been stamped to letter \"" + targetLetter + "\". The new state of the box grid:");
                break;
            }
        }

        /**
         * Handles MassRowStamp tool usage.
         */
        private void useMassRowStamp(MassRowStamp tool) {
            while (true) {
                System.out.print("Please enter the row to stamp (e.g., R3 or 3): ");
                String input = scanner.nextLine().trim();

                int rowIndex = MassRowStamp.parseRowInput(input);
                if (rowIndex == -1) {
                    System.out.println("INCORRECT INPUT: Invalid row. Please try again.");
                    continue;
                }

                tool.stampRow(grid, rowIndex, targetLetter);
                System.out.println("All boxes in row R" + (rowIndex+1) +
                        " have been stamped to letter \"" + targetLetter + "\". The new state of the box grid:");
                break;
            }
        }

        /**
         * Handles MassColumnStamp tool usage.
         */
        private void useMassColumnStamp(MassColumnStamp tool) {
            while (true) {
                System.out.print("Please enter the column to stamp (e.g., C5 or 5): ");
                String input = scanner.nextLine().trim();

                int colIndex = MassColumnStamp.parseColumnInput(input);
                if (colIndex == -1) {
                    System.out.println("INCORRECT INPUT: Invalid column. Please try again.");
                    continue;
                }

                tool.stampColumn(grid, colIndex, targetLetter);
                System.out.println("All boxes in column C" + (colIndex+1) +
                        " have been stamped to letter \"" + targetLetter + "\". The new state of the box grid:");
                break;
            }
        }

        /**
         * Handles BoxFlipper tool usage.
         */
        private void useBoxFlipper(BoxFlipper tool) throws UnmovableFixedBoxException {
            while (true) {
                System.out.print("Please enter the location of the box to use this SpecialTool: ");
                String location = scanner.nextLine().trim();

                int[] coords = BoxGrid.parseLocation(location);
                if (coords == null) {
                    System.out.println("INCORRECT INPUT: Invalid location. Please try again.");
                    continue;
                }

                tool.flipBox(grid, coords[0], coords[1]);
                System.out.println("The chosen box on location R" + (coords[0]+1) + "-C" + (coords[1]+1) +
                        " has been flipped upside down. The new state of the box grid:");
                break;
            }
        }

        /**
         * Handles BoxFixer tool usage.
         */
        private void useBoxFixer(BoxFixer tool) throws BoxAlreadyFixedException {
            while (true) {
                System.out.print("Please enter the location of the box to use this SpecialTool: ");
                String location = scanner.nextLine().trim();

                int[] coords = BoxGrid.parseLocation(location);
                if (coords == null) {
                    System.out.println("INCORRECT INPUT: Invalid location. Please try again.");
                    continue;
                }

                tool.fixBox(grid, coords[0], coords[1]);
                System.out.println("The chosen box on location R" + (coords[0]+1) + "-C" + (coords[1]+1) +
                        " has been converted to a FixedBox. The new state of the box grid:");
                break;
            }
        }
    }
}