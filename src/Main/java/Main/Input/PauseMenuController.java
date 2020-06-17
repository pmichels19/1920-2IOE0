package Main.Input;

import Graphics.Rendering.PauseScreen;
import Main.GameState;
import Main.Main;

import java.io.File;

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
        File screenshot = new File("Saves/lastSave.png");

        // first we check if the player wants to go back in game again
        if ( pauseCooldown == 0 ) {
            if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
                pauseCooldown = 10;
                // when going back in game, get rid of the lastSave.png made when entering the pause menu
                if ( !screenshot.delete() ) {
                    throw new IllegalStateException("Could not delete the lastSave.png in the saves folder");
                }
                setState( GameState.IN_GAME );
                return;
            }
        } else {
            pauseCooldown--;
        }

        // if the player selected an option, we need to execute the selected option
        if ( window.buttonClicked(GLFW_KEY_ENTER) ) {
            switch ( Main.getPauseScreen().getSelected() ) {
                // 0 is mapped to going back in game
                case 0:
                    // when going back in game, get rid of the lastSave.png made when entering the pause menu
                    if ( !screenshot.delete() ) {
                        throw new IllegalStateException("Could not delete the lastSave.png in the saves folder");
                    }
                    setState( GameState.IN_GAME );
                    return;
                // 1 is mapped to saving the game
                case 1:
                    setState( GameState.SAVING_GAME );
                    break;
                // 2 is mapped to returning to the main menu
                case 2:
                    // when going back to the main menu, get rid of the lastSave.png made when entering the pause menu
                    if ( !screenshot.delete() ) {
                        throw new IllegalStateException("Could not delete the lastSave.png in the saves folder");
                    }
                    setState( GameState.MAIN_MENU );
                    return;
                // 3 is mapped to exiting to desktop
                case 3:
                    // set the screenshot to be removed once the program stops running
                    screenshot.deleteOnExit();
                    // then give the command to close the window
                    glfwSetWindowShouldClose( window.getWindow(), true );
                    return;
            }
        }

        // we can go up and down in the pause menu on cooldown
        if (pauseMenuCooldown == 0) {
            if ( window.buttonClicked(GLFW_KEY_UP) ) {
                Main.getPauseScreen().changeSelected(true);
                pauseMenuCooldown = 5;
            } else if ( window.buttonClicked(GLFW_KEY_DOWN) ) {
                Main.getPauseScreen().changeSelected(false);
                pauseMenuCooldown = 5;
            }
        } else {
            pauseMenuCooldown --;
        }
    }
}
