package Main;

import Graphics.*;
import Graphics.Generators.*;
import Levels.Framework.joml.*;
import Levels.Tiles.*;
import Levels.Framework.Maze;

import java.util.*;

/**
 * Class for rendering a maze
 */
public class Renderer {
    private final Set<Point> backgrounds;
    private final Map<Point, Decoration> decorations;
    private final Set<Point> walls;

    private final Maze maze;
    private final Shader SHADER = new Shader("testShader");

    private final Model wallModel;
    private final Model ceilModel;
    private final Model backgroundModel;
    private final Camera camera;
    private Model playerModel;

    final float[] textures = new float[] {
            0, 1,
            1, 1,
            1, 0,
            0, 0
    };

    public static final float BLOCK_WIDTH = 1.0f / 6.0f;

    /**
     * initialises the global variables for the renderer
     *
     * @param maze the maze that has to be rendered
     * @param width the width of the window
     * @param height the height of the window
     */
    Renderer(Maze maze, int width, int height) {
        // prepare the camera
        camera = new Camera();
        camera.setPerspective(
                (float) Math.toRadians(40.0),
                (float) width / (float) height,
                0.01f,
                1000.0f
        );
        camera.setPosition(new Vector3f(0, 0, 4));

        // prepare the transformations
        Transform transform = new Transform();
        transform.getRotation().rotateAxis((float) Math.toRadians(270.0), 0, 0, 1);
        transform.getRotation().rotateAxis((float) Math.toRadians(-30.0), 0, 1, 0);

        backgrounds = new HashSet<>();
        decorations = new HashMap<>();
        walls = new HashSet<Point>();

        this.maze = maze;

        SHADER.bind();
        SHADER.setCamera(camera);
        SHADER.setTransform(transform);

        gatherGridInfo();

        wallModel = new WallGenerator(maze, walls).generate();
        ceilModel = new CeilingGenerator(maze, walls).generate();
        backgroundModel = new BackgroundGenerator(maze, backgrounds).generate();
    }

    /**
     * gather information based on the grid in {@code maze} so we can prepare for rendering
     */
    private void gatherGridInfo() {
        // gather the data on the grid
        char[][] grid = maze.getGrid();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // fill the mappings according to the grid in the maze class
                if (grid[i][j] == Maze.MARKER_WALL) {
                    walls.add(new Point(i, j));
                    continue;
                }

                // we always want a background if there is not a wall on a tile
                backgrounds.add( new Point(i, j) );
            }
        }
    }

    /**
     * generates {@code playerModel} when called
     */
    private void generatePlayerModel() {
        final float[] player = new float[]{
                // TOP RIGHT
                delta_x * BLOCK_WIDTH, (delta_y + 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM RIGHT
                delta_x * BLOCK_WIDTH, (delta_y - 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM LEFT
                delta_x * BLOCK_WIDTH, (delta_y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // TOP LEFT
                delta_x * BLOCK_WIDTH, (delta_y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
        };

        playerModel = new Model(player, textures);
    }

    private int counter = 0;
    private float delta_x = 0;
    private float delta_y = 0;
    private float block_speed;
    private float base_speed;
    private boolean vertical;

    /**
     * renders the maze
     */
    public void render() {
        calculateCameraPosition();

        generatePlayerModel();

        renderBackgrounds();
        renderWalls();
        renderPlayer();
        renderCeilings();
    }

    /**
     * repositions the camera if needed
     */
    private void calculateCameraPosition() {
        if (counter > 0) {
            Vector3f curPos = camera.getPosition();
            Vector3f newPosition;
            float dist = block_speed * 60f * base_speed;
            if (vertical) {
                // when moving vertically, we need to consider the fact that the world is rotated by 30 degrees
                newPosition = new Vector3f(
                        curPos.x,
                        curPos.y + ( block_speed * (float) Math.cos(Math.toRadians(-30.0)) ),
                        curPos.z - ( block_speed * (float) Math.tan(Math.toRadians( 30.0)) )
                );
                delta_x += block_speed > 0 ? -dist : dist;
            } else {
                newPosition = new Vector3f(
                        curPos.x + block_speed,
                        curPos.y,
                        curPos.z
                );
                delta_y += block_speed > 0 ? dist : -dist;
            }

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
        base_speed = speed;
        block_speed = speed * BLOCK_WIDTH * 2.0f;
        this.vertical = vertical;
        counter = frames;
    }

    /**
     * draws the background (floor) tiles
     */
    private void renderBackgrounds() {
        // get the texture ready for rendering
        SHADER.setUniform("sampler", Background.BASIC.getSampler());
        Background.BASIC.bindTexture();

        backgroundModel.render();

    }

    private void renderPlayer() {
        SHADER.setUniform("sampler", Background.PLAYER.getSampler());
        Background.PLAYER.bindTexture();

        playerModel.render();
    }

    /**
     * draw the specified decoration at the specified point
     *
     * @param point the point at which the decoration is located
     * @param decoration the decoration to draw
     */
    private void renderDecorations(Point point, Decoration decoration) {

    }

    /**
     * draws the wall tiles
     */
    private void renderWalls() {
        SHADER.setUniform("sampler", Wall.CASTLE_WALL.getSampler());
        Wall.CASTLE_WALL.bindTexture();
        wallModel.render();
    }

    /**
     * draws the ceiling tiles
     */
    private void renderCeilings() {
        SHADER.setUniform("sampler", Wall.CEILING.getSampler());
        Wall.CEILING.bindTexture();
        ceilModel.render();
    }
}

