package Main;

import Graphics.OpenGL.Texture;
import Graphics.TileRenderer;
import Graphics.Transforming.Camera;
import Graphics.OpenGL.Shader;
import Graphics.Transforming.Transform;
import Levels.Characters.Character;
import Levels.Framework.Point;
import Levels.Framework.joml.*;
import Levels.Assets.Tiles.*;
import Levels.Framework.Maze;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        yPlayer = maze.getPlayerLocation().getX();
        xPlayer = maze.getPlayerLocation().getY();

        // initialize the camera by centering it on the player
        camera.setPosition( new Vector3f(
                xPlayer * 2,
                (maze.getGrid().length - yPlayer) * 2 - 10,
                16
        ) );

        // bind the shader and set the camera and world transformations
        SHADER.bind();
        SHADER.setCamera(camera);
        SHADER.setTransform(transform);

        // prepare the tile renderer for rendering
        renderer = TileRenderer.getInstance();
    }

    /**
     * renders the maze
     */
    public void render() {
        // set the camera and shader for the renderer
        renderer.setCamera(camera);
        renderer.setShader(SHADER);

        // sets used for gathering points to determine drawing locations of tiles
        Set<Point> floors = new HashSet<>();
        Set<Point> leftWalls = new HashSet<>();
        Set<Point> rightWalls = new HashSet<>();
        Set<Point> faceWalls = new HashSet<>();
        Set<Point> ceilings = new HashSet<>();

        char[][] grid = maze.getGrid();
        for ( int i = 0; i < grid.length; i++ ) {
            for ( int j = 0; j < grid[i].length; j++ ) {
                // determine what tile needs to be drawn and fill that into the sets made above
                if ( grid[i][j] == Maze.MARKER_WALL ) {
                    // we check if the tile to left is also a wall
                    if ( j > 0 && grid[i][j - 1] != Maze.MARKER_WALL ) {
                        // if there is no wall to the left, we wish to draw the left side of this wall
                        leftWalls.add( new Point(j, grid.length - i) );
                    }

                    // we check if the tile to right is also a wall
                    if ( j < grid.length - 1 && grid[i][j + 1] != Maze.MARKER_WALL ) {
                        // if there is no wall to the right, we wish to draw the right side of this wall
                        rightWalls.add( new Point(j, grid.length - i) );
                    }

                    // we check if the tile in front is also a wall
                    if ( i < grid.length - 1 && grid[i + 1][j] != Maze.MARKER_WALL ) {
                        // if there is no wall in front, we wish to draw the face of this wall
                        faceWalls.add( new Point(j, grid.length - i) );
                    }

                    // we always want to render a ceiling:
                    ceilings.add( new Point(j, grid.length - i) );
                } else if ( grid[i][j] == Maze.MARKER_SPACE || grid[i][j] == Maze.MARKER_PLAYER ) {
                    floors.add( new Point( j, grid.length - i ) );
                }
            }
        }

        for ( Point point : floors ) {
            renderer.renderTile( Background.BASIC.getTexture(), point.getX(), point.getY(), TileRenderer.FLOOR );
        }

        for ( Point point : leftWalls ) {
            renderer.renderTile( Wall.CASTLE_WALL.getTexture(), point.getX(), point.getY(), TileRenderer.LEFTS );
        }

        for ( Point point : rightWalls ) {
            renderer.renderTile( Wall.CASTLE_WALL.getTexture(), point.getX(), point.getY(), TileRenderer.RIGHT );
        }

        for ( Point point : faceWalls ) {
            renderer.renderTile( Wall.CASTLE_WALL.getTexture(), point.getX(), point.getY(), TileRenderer.FACES );
        }

        renderer.renderTile( Background.PLAYER.getTexture(), xPlayer, grid.length + 0.5f - yPlayer, TileRenderer.FACES );

        for ( Point point : ceilings ) {
            renderer.renderTile( Wall.CEILING.getTexture(), point.getX(), point.getY(), TileRenderer.CEILS );
        }
    }

    /**
     * moves the player character as specified by the parameters
     *
     * @param speed the distance covered in a single frame
     * @param vertical whether movement is supposed to be vertical or not
     */
    public void movePlayer(float speed, boolean vertical) {
        // calculate the amount of movement in the x and y direction
        xPlayer += vertical ? 0f : speed;
        yPlayer += vertical ? -speed : 0f;

        // upon moving the player, we also have to move the camera
        adjustCamera(speed * 2f, vertical);
    }

    /**
     * moves the camera, as specified by the parameters
     *
     * @param speed the distance covered by the movement in a single frame
     * @param vertical whether movement is supposed to be vertical or not
     */
    private void adjustCamera(float speed, boolean vertical) {
        // calculate the new camera position
        Vector3f cameraPos = camera.getPosition();
        camera.setPosition( new Vector3f(
                cameraPos.x + ( vertical ? 0 : speed ),
                cameraPos.y + ( vertical ? speed : 0 ),
                cameraPos.z
        ) );

        // adjust the camera for the shader, so it actually has an effect on the position of the render
        SHADER.setCamera( camera );
    }
}

