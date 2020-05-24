package Main.Input;

import Graphics.Rendering.SlotPickMenu;
import Main.GameState;

import static org.lwjgl.glfw.GLFW.*;
import static Main.Main.getState;
import static Main.Main.setState;

/**
 * controller that provides input checks for all menus that have to do with the slot picker
 */
public class SlotPickController extends Controller {
    // a cooldown on the switching between slots, for more control
    private int slotSwitchCooldown = 0;

    @Override
    void checkInputs() {
        // if escape is pressed, we need to go back to the appropriate state, which depends on the current game state
        if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
            // the slot picker is only shown in 3 menus: new game, load game, save game
            if (getState() == GameState.STARTING_GAME) {
                // if you are starting a new game from and press escape, you will go back to the main menu
                setState(GameState.MAIN_MENU);
            } else if (getState() == GameState.LOADING_SAVE) {
                // if you are loading a save, you go back to the main meu as well
                // TODO: set up a way to load data into the player object and maze object from the correct save slot
                setState(GameState.MAIN_MENU);
            } else if (getState() == GameState.SAVING_GAME) {
                // if you are saving the game, you go back to the pause screen
                // TODO: set up a way to save the player data to the correct save slot
                setState(GameState.PAUSED);
            }

            // after exiting a slot picker, we reset the selected slot and return
            SlotPickMenu.resetSelected();
            return;
        }

        // we check if the player selected a slot using the enter key
        if (window.buttonClicked(GLFW_KEY_ENTER)) {
            // once again we have to deal with the three menus: new game, load game, save game
            if (getState() == GameState.STARTING_GAME) {
                // if you are starting a new game from and press escape, you will go back to the main menu
                // TODO: overwrite any save data in the selected save slot and start a new game
                setState(GameState.IN_GAME);
            } else if (getState() == GameState.LOADING_SAVE) {
                // if you are loading a save, you go back to the main meu as well
                // TODO: load the save data from a slot into the player object and then go in game
                setState(GameState.IN_GAME);
            } else if (getState() == GameState.SAVING_GAME) {
                // if you are saving the game, you go back to the pause screen
                // TODO: overwrite any save data located in the selected save slot
                setState(GameState.PAUSED);
            }
            // if a save slot is selected, reset the startUpCooldown and the selected slot
            SlotPickMenu.resetSelected();
            return;
        }

        // if enter or escape was not pressed we check if the player wants to move up/down in the slot picker
        if (slotSwitchCooldown == 0) {
            if (window.buttonClicked(GLFW_KEY_UP)) {
                SlotPickMenu.changeSelected(true);
                slotSwitchCooldown = 5;
            } else if (window.buttonClicked(GLFW_KEY_DOWN)) {
                SlotPickMenu.changeSelected(false);
                slotSwitchCooldown = 5;
            }
        } else {
            slotSwitchCooldown--;
        }
    }
}
