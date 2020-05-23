package Main.Input;

import Graphics.Rendering.PauseScreen;
import Main.GameState;

import java.io.FileNotFoundException;

import static Main.Main.setState;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;

/**
 * class that will take care of inputs in the pause menu
 */
public class PauseMenuController extends Controller {
    // pauseMenu cooldown provides control when navigating the pause menu options
    private int pauseMenuCooldown = 0;

    @Override
    void checkInputs() {
        // first we check if the player wants to go back in game again
        if ( pauseCooldown == 0 ) {
            if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
                pauseCooldown = 10;
                setState( GameState.IN_GAME );
                return;
            }
        } else {
            pauseCooldown--;
        }

        // we can go up and down in the pause menu on cooldown
        if (pauseMenuCooldown == 0) {
            // if the player selected an option, we need to execute the selected option
            if ( window.buttonClicked(GLFW_KEY_ENTER) ) {
                switch ( PauseScreen.getSelectedOption() ) {
                    // 0 is mapped to saving the game
                    case 0:
                        try {
                            maze.saveCurrentMaze();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    // 1 is mapped to saving the game, then exiting
                    case 1:
                        setState( GameState.MAIN_MENU );
                        break;
                    // 2 is mapped to quitting the game, without saving
                    case 2:
                        glfwSetWindowShouldClose( window.getWindow(), true );
                        break;
                }

                pauseMenuCooldown = 5;
            }

            if ( window.buttonClicked(GLFW_KEY_UP) ) {
                PauseScreen.changeSelected(true);
                pauseMenuCooldown = 5;
            } else if ( window.buttonClicked(GLFW_KEY_DOWN) ) {
                PauseScreen.changeSelected(false);
                pauseMenuCooldown = 5;
            }
        } else {
            pauseMenuCooldown --;
        }
    }
}
