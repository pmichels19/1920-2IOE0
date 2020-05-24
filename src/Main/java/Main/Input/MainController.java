package Main.Input;

import Graphics.IO.Window;
import Levels.Framework.Maze;
import Main.GameState;

/**
 * class for handling inputs to the game
 */
public class MainController {
    private final MainMenuController mainMenuController;
    private final InGameController gameController;
    private final PauseMenuController pauseMenuController;
    private final SlotPickController slotPickController;

    public MainController(Maze maze, Window window) {
        Controller.setWindow( window );
        Controller.setMaze( maze );

        mainMenuController = new MainMenuController();
        gameController = new InGameController();
        pauseMenuController = new PauseMenuController();
        slotPickController = new SlotPickController();
    }

    /**
     * method that will check for activation from all available inputs
     */
    public void checkInputs() {
        // depending on the current game state, we wish to check different inputs
        GameState state = Main.Main.getState();
        if ( state == GameState.IN_GAME ) {
            gameController.checkInputs();
        } else if ( state == GameState.PAUSED ) {
            pauseMenuController.checkInputs();
        } else if ( state == GameState.MAIN_MENU ) {
            mainMenuController.checkInputs();
        } else if ( state == GameState.STARTING_GAME || state == GameState.LOADING_SAVE || state == GameState.SAVING_GAME ) {
            slotPickController.checkInputs();
        }
    }

    /**
     * reduces the cooldown left on moving by one
     */
    public void decrementMovementCounter() {
        gameController.decrementMovementCounter();
    }

    /**
     * returns the cooldown left on moving
     *
     * @return {@code movementCounter}
     */
    public int getMovementCounter() {
        return gameController.getMovementCounter();
    }

    /**
     * returns the speed with which the player is currently moving
     *
     * @return {@code speed}
     */
    public float getSpeed() {
        return gameController.getSpeed();
    }

    /**
     * returns whether the current movement is vertical or not
     *
     * @return {@code vertical}
     */
    public boolean isVertical() {
        return gameController.isVertical();
    }
}
