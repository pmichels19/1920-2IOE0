package Main.Input;

import Graphics.Rendering.SlotPickMenu;
import Main.GameState;
import Main.Main;

import static Main.SaveManager.*;
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
        SlotPickMenu menu = null;
        GameState state = getState();
        if (state == GameState.STARTING_GAME) {
            menu = Main.getNewGameMenu();
        } else if (state == GameState.LOADING_SAVE) {
            menu = Main.getLoadMenu();
        } else if (state == GameState.SAVING_GAME) {
            menu = Main.getSaveMenu();
        }
        assert menu != null;

        // if escape is pressed, we need to go back to the appropriate state, which depends on the current game state
        if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
            // the slot picker is only shown in 3 menus: new game, load game, save game
            if (state == GameState.STARTING_GAME || state == GameState.LOADING_SAVE) {
                // if you are starting a new game from and press escape, you will go back to the main menu
                menu.resetSelected();
                setState(GameState.MAIN_MENU);
            } else {
                // if you are saving the game, you go back to the pause screen
                menu.resetSelected();
                setState(GameState.PAUSED);
            }
            return;
        }

        // we check if the player selected a slot using the enter key
        if (window.buttonClicked(GLFW_KEY_ENTER)) {
            // once again we have to deal with the three menus: new game, load game, save game
            if (state == GameState.STARTING_GAME) {
                // if you are starting a new game, all save data will be wiped and a new save will be started
                purgeSlot( menu.getSelected() );
                menu.resetSelected();
                setState(GameState.IN_GAME);
            } else if (state == GameState.LOADING_SAVE) {
                if ( loadFromSlot( menu.getSelected() ) ) {
                    menu.resetSelected();
                    setState(GameState.IN_GAME);
                }
            } else {
                // if you are saving the game, we save and return the player to the pause screen
                saveToSlot( menu.getSelected() );
                menu.resetSelected();
                setState(GameState.PAUSED);
            }
            return;
        }

        // if enter or escape was not pressed we check if the player wants to move up/down in the slot picker
        if (slotSwitchCooldown == 0) {
            if (window.buttonClicked(GLFW_KEY_UP)) {
                menu.changeSelected(true);
                slotSwitchCooldown = 5;
            } else if (window.buttonClicked(GLFW_KEY_DOWN)) {
                menu.changeSelected(false);
                slotSwitchCooldown = 5;
            }
        } else {
            slotSwitchCooldown--;
        }
    }
}
