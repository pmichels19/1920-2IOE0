package Main.Input;

import Graphics.IO.Window;
import Graphics.Rendering.World;
import Levels.Characters.Player;
import Levels.Framework.Maze;

abstract class Controller {
    static Window window;
    static World world;
    static Maze maze;

    static Player player = Player.getInstance();

    // pause cooldown makes the pause menu appear and disappear in a controllable manner, this is shared by multiple
    // controllers: both the in-game and the pausemenu controller
    static int pauseCooldown = 0;

    /**
     * sets the window to take the inputs from
     *
     * @param window the window to connect the controller to
     */
    public static void setWindow(Window window) {
        Controller.window = window;
    }

    /**
     * sets the maze to apply changes to
     *
     * @param maze the maze the controller is used on
     */
    public static void setMaze(Maze maze) {
        Controller.maze = maze;
    }

    /**
     * sets the world to apply changes to
     *
     * @param world the world the controller is used on
     */
    public static void setWorld(World world) {
        Controller.world = world;
    }

    /**
     * checks the inputs as specified by the subclass
     */
    abstract void checkInputs();
}
