package Main.Input;

import Main.GameState;

import static Main.Main.setState;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class VictoryController extends Controller {
    @Override
    void checkInputs() {
        if ( window.buttonClicked( GLFW_KEY_ESCAPE ) ) {
            setState(GameState.MAIN_MENU);
        }
    }
}
