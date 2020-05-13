package AI;

import Graphics.Renderer;
import Main.Point;
import Main.Timer;
import Graphics.Window;
import Levels.Framework.Maze;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;

public class AStarClickTest {

    // standard varialbes for width and height of the game
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;

    // cap at 60 fps for now
    private static final double FRAME_CAP = 1.0 / 60.0;
    private static final double MOVEMENT_CAP = 1.0 / 5.0;

    public static void main(String[] args) throws IOException {
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        Window window = new Window(SCREEN_WIDTH, SCREEN_HEIGHT);
        window.setFullscreen(false);
        window.createWindow("Mazes of Magic");

        GL.createCapabilities();

        Maze maze = new Maze("level_1");
        Renderer renderer = new Renderer(maze, SCREEN_WIDTH, SCREEN_HEIGHT);

        // enable use of textures
        glEnable(GL_TEXTURE_2D);

        // Stuff to keep track of the fps
        double frame_time = 0;
        int frames = 0;
        int moving_frames = (int) (MOVEMENT_CAP * 60.0f);

        // starting time
        double time = Timer.getTime();
        // time that has not yet been processed
        double unprocessed = 0;

        double inputAllowed = 0;

        while (!window.shouldClose()) {
            boolean can_render = false;

            // update the time variables
            double time_2 = Timer.getTime();
            double passed = time_2 - time;

            unprocessed += passed;
            frame_time += passed;
            time = time_2;

            while (unprocessed >= FRAME_CAP) {
                unprocessed -= FRAME_CAP;
                can_render = true;

                // for now, allow exiting of the window by pressing escape
                if (window.buttonClicked(GLFW_KEY_ESCAPE)) {
                    window.close();
                }

                if ((window.buttonClicked(GLFW_KEY_W) || window.buttonClicked(GLFW_KEY_UP)) && time > inputAllowed) {
                    if (maze.moveUp()) {
                        inputAllowed = time + MOVEMENT_CAP;
                        renderer.setChange(moving_frames, 1.0f / (float) moving_frames, true);
                    }
                }

                if ((window.buttonClicked(GLFW_KEY_A) || window.buttonClicked(GLFW_KEY_LEFT)) && time > inputAllowed) {
                    if (maze.moveLeft()) {
                        inputAllowed = time + MOVEMENT_CAP;
                        renderer.setChange(moving_frames, -1.0f / (float) moving_frames, false);
                    }
                }

                if ((window.buttonClicked(GLFW_KEY_S) || window.buttonClicked(GLFW_KEY_DOWN)) && time > inputAllowed) {
                    if (maze.moveDown()) {
                        inputAllowed = time + MOVEMENT_CAP;
                        renderer.setChange(moving_frames, -1.0f / (float) moving_frames, true);
                    }
                }

                if ((window.buttonClicked(GLFW_KEY_D) || window.buttonClicked(GLFW_KEY_RIGHT)) && time > inputAllowed) {
                    if (maze.moveRight()) {
                        inputAllowed = time + MOVEMENT_CAP;
                        renderer.setChange(moving_frames, 1.0f / (float) moving_frames, false);
                    }
                }

                if ((window.buttonClicked(GLFW_KEY_K)) && time > inputAllowed) {
                    Point location = maze.getPlayerLocation();
                    Point destination = new Point(1, 1);
                    AStarSolver ass = new AStarSolver();
                    ArrayList<Point> path = ass.CalculateShortestPath(location, destination, maze.getGrid());
                    if (path.size()>0) {
                            Point next = path.remove(path.size() - 1);

                        if (next.getY() > location.getY()) {
                            if (maze.moveRight()) {
                                inputAllowed = time + MOVEMENT_CAP;
                                renderer.setChange(moving_frames, 1.0f / (float) moving_frames, false);
                            }
                        }
                        if (next.getY() < location.getY()) {
                            if (maze.moveLeft()) {
                                inputAllowed = time + MOVEMENT_CAP;
                                renderer.setChange(moving_frames, -1.0f / (float) moving_frames, false);
                            }
                        }
                        if (next.getX() < location.getX()) {
                            if (maze.moveUp()) {
                                inputAllowed = time + MOVEMENT_CAP;
                                renderer.setChange(moving_frames, 1.0f / (float) moving_frames, true);
                            }
                        }
                        if (next.getX() > location.getX()) {
                            if (maze.moveDown()) {
                                inputAllowed = time + MOVEMENT_CAP;
                                renderer.setChange(moving_frames, -1.0f / (float) moving_frames, true);
                            }
                        }
                    }
                }

                glfwPollEvents();

                if (frame_time >= 1.0) {
                    frame_time = 0;
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }
            }

            // make sure we only draw 60 frames every second
            if (can_render) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                renderer.render();

                window.swapBuffers();

                frames++;
            }
        }

        glfwTerminate();
    }
}
