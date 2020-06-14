package Main.Input;

import Main.GameState;
import Main.Main;

import static org.lwjgl.glfw.GLFW.*;
import static Main.SaveManager.loadLatest;
import static Main.Main.setState;

public class DeathScreenController extends Controller {

    private int switchCooldown = 0;

    @Override
    void checkInputs() {
        // if an option was selected, execute that option:
        if ( window.buttonClicked(GLFW_KEY_ENTER) ) {
            switch ( Main.getDeathScreen().getSelected() ) {
                case 0:
                    // if the player wants to continue from the latest save, do so
                    loadLatest();
                    // and go back in game
                    setState( GameState.IN_GAME );
                    return;
                case 1:
                    // if the player wants to load a save, go into the slotpicker for loading saves
                    setState( GameState.LOADING_SAVE );
                    return;
                case 2:
                    setState( GameState.MAIN_MENU );
                    return;
                case 3:
                    glfwSetWindowShouldClose( window.getWindow(), true );
                    return;
            }
        }

        // if the player wanted to swith up/down in the menu we need to check as well
        if ( switchCooldown == 0 ) {
            if ( window.buttonClicked(GLFW_KEY_UP) ) {
                Main.getDeathScreen().changeSelected( true );
                switchCooldown = 10;
            } else if ( window.buttonClicked(GLFW_KEY_DOWN) ) {
                Main.getDeathScreen().changeSelected( false );
                switchCooldown = 10;
            }
        } else {
            switchCooldown--;
        }
    }
}
