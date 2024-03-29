package Main.Input;

import Graphics.Rendering.MainMenu;
import Main.GameState;
import Main.Main;

import static Main.Main.setState;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static Main.SaveManager.loadLatest;

/**
 * class that will take care of the inputs in the main menu
 */
public class MainMenuController extends Controller {
    private int mainMenuCooldown = 0;

    @Override
    void checkInputs() {
        // we can go up and down in the pause menu on cooldown
        if (mainMenuCooldown == 0) {
            // if the player selected an option, we need to execute the selected option
            if ( window.buttonClicked(GLFW_KEY_ENTER) ) {
                switch ( Main.getMainMenu().getSelected() ) {
                    // 0 is mapped to continuing from the latest game
                    case 0:
                        if ( loadLatest() ) {
                            Main.getMainMenu().resetSelected();
                            setState( GameState.IN_GAME );
                        }
                        break;
                    // 1 is mapped to starting a new game
                    case 1:
                        Main.getMainMenu().resetSelected();
                        setState( GameState.STARTING_GAME );
                        break;
                    // 2 is mapped to loading a saved game
                    case 2:
                        Main.getMainMenu().resetSelected();
                        setState( GameState.LOADING_SAVE );
                        break;
                    // 3 is mapped to exiting the game
                    case 3:
                        glfwSetWindowShouldClose( window.getWindow(), true );
                        break;
                }

                mainMenuCooldown = 5;
            }

            if ( window.buttonClicked(GLFW_KEY_UP) ) {
                Main.getMainMenu().changeSelected(true);
                mainMenuCooldown = 5;
            } else if ( window.buttonClicked(GLFW_KEY_DOWN) ) {
                Main.getMainMenu().changeSelected(false);
                mainMenuCooldown = 5;
            }
        } else {
            mainMenuCooldown --;
        }
    }
}
