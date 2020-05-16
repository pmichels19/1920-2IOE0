package Main;

import Graphics.TileRenderer;
import Graphics.Transforming.Camera;
import Graphics.OpenGL.Shader;
import Graphics.Transforming.Transform;
import Levels.Characters.Character;
import Levels.Framework.joml.*;
import Levels.Assets.Tiles.*;
import Levels.Framework.Maze;

import static java.lang.Math.*;

/**
 * Class for rendering the world and (for now) the player
 */
public class World {
    // the constants needed for rendering:
    // the maze for data on what to draw where
    private final Maze maze;
    // the shader to use when rendering
    private final Shader SHADER = new Shader("testShader");
    // the camera containing the camera position and projection on the world
    private final Camera camera;
    // the tile renderer to actaully draw the world and the player
    private final TileRenderer renderer;

    // variables to keep track of the player location in the world
    private float xPlayer;
    private float yPlayer;

    /**
     * initialises the global variables for the renderer
     *
     * @param maze the maze that has to be rendered
     * @param width the width of the window
     * @param height the height of the window
     */
    public World(Character player, Maze maze, int width, int height) {
        // prepare the camera by setting up its perspective
        camera = new Camera();
        camera.setPerspective((float) toRadians(40.0), (float) width / (float) height, 0.01f, 1000.0f);

        // prepare the transformations of the world
        Transform transform = new Transform();
        transform.getRotation().rotateAxis( -(float) toRadians(35.0), 1, 0, 0 );

        // set the maze object
        this.maze = maze;

        // initialize the player location variables
        xPlayer = maze.getPlayerLocation().getX();
        yPlayer = maze.getPlayerLocation().getY();

        // center the camera on the player character
        camera.setPosition( new Vector3f(
                yPlayer * 2,
                xPlayer * 2 - 8,
                16
        ) );

        // bind the shader and set the camera and world transformations
        SHADER.bind();
        SHADER.setCamera(camera);
        SHADER.setTransform(transform);

        // prepare the tile renderer for rendering
        renderer = TileRenderer.getInstance();
        renderer.setCamera(camera);
        renderer.setShader(SHADER);
    }

    // variables for moving the camera and player around
    private int counter = 0;
    private float speed;
    private float block_speed;
    private boolean vertical;

    /**
     * renders the maze
     */
    public void render() {
        calculateCameraPosition();

        SHADER.bind();
        char[][] grid = maze.getGrid();

        for ( int i = 0; i < grid.length; i++ ) {
            for ( int j = 0; j < grid[i].length; j++ ) {
                // determine what tile needs to be drawn
                if ( grid[i][j] == Maze.MARKER_WALL ) {
                    renderer.renderTile( Wall.CASTLE_WALL.getTexture(), j, grid.length - i, 3 );
                } else {
                    renderer.renderTile( Background.BASIC.getTexture(), j, grid.length - i, 2 );
                }

            }
        }

        // finally render the player
        renderer.renderTile( Background.PLAYER.getTexture(), yPlayer, grid.length - xPlayer, 2 );
    }

    /**
     * repositions the camera if needed
     */
    private void calculateCameraPosition() {
        if (counter > 0) {
            // get the current camera position
            Vector3f curPos = camera.getPosition();

            // calculate the new camera position
            Vector3f newPosition = new Vector3f(
                    // if the movement is vertical, we keep the current x position, otherwise we add the block_speed
                    vertical ? curPos.x : (curPos.x + block_speed),
                    // if the movement is vertical, we add the block_speed to the current y position
                    vertical ? (curPos.y + block_speed) : curPos.y,
                    curPos.z
            );

            // calculate the amount of movement in the x and y direction
            xPlayer += vertical ? -speed : 0f;
            yPlayer += vertical ? 0f : speed;

            // reposition the camera and decrement the counter
            camera.setPosition( newPosition );
            SHADER.setCamera(camera);
            counter--;
        }
    }

    /**
     * sets the counter, speed and vertical variable such that the camera can move smoothly with a speed of
     * {@code speed} over {@code frames} frames in the specified direction
     *
     * @param frames the amount of frames the camera has to move for
     * @param speed the speed of the camera
     * @param vertical whether movement is on the x or the y axis
     */
    public void setChange(int frames, float speed, boolean vertical) {
        this.speed = speed;
        block_speed = speed * 2.0f;
        this.vertical = vertical;
        counter = frames;
    }
}

