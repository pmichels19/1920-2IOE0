package Main.Input;

import Graphics.IO.Window;
import Graphics.Rendering.World;
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
    private final DeathScreenController deathScreenController;

    // when a menu is started up, we want to give the player 10 frames of cooldown time, so the player
    // does not skip the menu on accident
    private static int startUpCooldown = 10;

    /**
     * once a state change has taken place,
     */
    public static void changedState() {
        startUpCooldown = 10;
    }

    public MainController(Maze maze, World world, Window window) {
        Controller.setWindow( window );
        Controller.setWorld( world );
        Controller.setMaze( maze );

        mainMenuController = new MainMenuController();
        gameController = new InGameController();
        pauseMenuController = new PauseMenuController();
        slotPickController = new SlotPickController();
        deathScreenController = new DeathScreenController();
    }

    /**
     * method that will check for activation from all available inputs
     */
    public void checkInputs() {
        if ( startUpCooldown == 0 ) {
            // depending on the current game state, we wish to check different inputs
            GameState state = Main.Main.getState();
            Controller controller = null;

            if (state == GameState.IN_GAME) {
                controller = gameController;
            } else if (state == GameState.PAUSED) {
                controller = pauseMenuController;
            } else if (state == GameState.MAIN_MENU) {
                controller = mainMenuController;
            } else if (state == GameState.STARTING_GAME || state == GameState.LOADING_SAVE || state == GameState.SAVING_GAME) {
                controller = slotPickController;
            } else if (state == GameState.DEAD) {
                controller = deathScreenController;
            }

            assert controller != null;
            controller.checkInputs();
        } else {
            startUpCooldown--;
        }
    }

//    /**
//     * returns the speed with which the player is currently moving
//     *
//     * @return {@code speed}
//     */
//    public float getSpeed() {
//        return gameController.getSpeed();
//    }
//
//    /**
//     * returns whether the current movement is vertical or not
//     *
//     * @return {@code vertical}
//     */
//    public boolean isVertical() {
//        return gameController.isVertical();
//    }
}
