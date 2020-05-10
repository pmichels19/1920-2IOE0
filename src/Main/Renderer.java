package Main;

import Graphics.Camera;
import Graphics.Model;
import Graphics.Shader;
import Graphics.Transform;
import Levels.Framework.Maze;
import Levels.Framework.joml.*;
import Levels.Tiles.Background;
import Levels.Tiles.Decoration;
import Levels.Tiles.Wall;

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

    private Model wallModel;
    private Model ceilModel;
    private Model backgroundModel;
    private Model playerModel;
    private final Camera camera;
    private Point playerLocation;

    private char[][] grid;

    private boolean playerMoved;

    private final float BLOCK_WIDTH = 1.0f / 6.0f;

    private final float[] textures = new float[] {
            0, 1,
            1, 1,
            1, 0,
            0, 0
    };

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

        playerLocation = maze.getPlayerLocation();

        SHADER.bind();
        SHADER.setCamera(camera);
        SHADER.setTransform(transform);

        playerMoved = true;

        gatherGridInfo();
        generateBackgroundModel();
        generateWallModel();
    }

    /**
     * gather information based on the grid in {@code maze} so we can prepare for rendering
     */
    private void gatherGridInfo() {
        // gather the data on the grid
        grid = maze.getGrid();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // fill the mappings according to the grid in the maze class
                if (grid[i][j] == Maze.MARKER_WALL) {
                    walls.add(new Point(i, j));
                    continue;
                } else if (grid[i][j] == Maze.MARKER_PLAYER) {
                    // check if the player moved in the maze globally
                    playerLocation = maze.getPlayerLocation();
                }

                // we always want a background if there is not a wall on a tile
                backgrounds.add( new Point(i, j) );
            }
        }
    }

    /**
     * generates the {@code wallModel} when called
     */
    private void generateWallModel() {
        List<float[]> vertices_list = new ArrayList<>();
        List<float[]> ceilings_list = new ArrayList<>();

        for (Point point : walls) {
            final int x_point = point.getX();
            final int y_point = point.getY();

            // use the point to get the vertices so we can draw the wall tile
            float x = (x_point - playerLocation.getX()) * 2.0f;
            float y = (y_point - playerLocation.getY()) * 2.0f;

            if (y_point > 0 && grid[x_point][y_point - 1] != Maze.MARKER_WALL) {
                // walls on the left
                final float[] vertices_left = new float[]{
                        // TOP LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // BOTTOM RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                        // BOTTOM LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f
                };
                vertices_list.add(vertices_left);
            }

            if (y_point < grid.length - 1 && grid[x_point][y_point + 1] != Maze.MARKER_WALL) {
                // walls on the right
                final float[] vertices_right = new float[]{
                        // TOP LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // BOTTOM RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                        // BOTTOM LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f
                };
                vertices_list.add(vertices_right);
            }

            if (x_point < grid.length - 1 && grid[x_point + 1][y_point] != Maze.MARKER_WALL) {
                // walls on the face
                final float[] vertices_face = new float[]{
                        // BOTTOM LEFT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP LEFT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                        // BOTTOM RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f
                };
                vertices_list.add(vertices_face);
            }

            final float[] ceiling = new float[]{
                    // TOP LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // TOP RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // BOTTOM RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // BOTTOM LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH
            };

            ceilings_list.add(ceiling);
        }

        float[] vertices_total = new float[vertices_list.size() * 12];
        float[] wall_textures = new float[vertices_list.size() * 8];
        for (int i = 0; i < vertices_list.size(); i++) {
            System.arraycopy(
                    vertices_list.get(i), 0, vertices_total, i * 12, vertices_list.get(i).length
            );
            System.arraycopy(
                    textures, 0, wall_textures, i * 8, textures.length
            );
        }

        wallModel = new Model(vertices_total, wall_textures);

        float[] ceilings_total = new float[ceilings_list.size() * 12];
        float[] ceil_textures = new float[ceilings_list.size() * 8];
        for (int i = 0; i < ceilings_list.size(); i++) {
            System.arraycopy(
                    ceilings_list.get(i), 0, ceilings_total, i * 12, ceilings_list.get(i).length
            );
            System.arraycopy(
                    textures, 0, ceil_textures, i * 8, textures.length
            );
        }

        ceilModel = new Model(ceilings_total, ceil_textures);
    }

    /**
     * generates {@code backgroundModel} when called
     */
    private void generateBackgroundModel() {
        float[] vertices_total = new float[backgrounds.size() * 12];
        float[] textures_total = new float[backgrounds.size() * 8];
        int i = 0;

        for (Point point : backgrounds) {
            // use the point to get the vertices so we can draw the wall tile
            float x = (point.getX() - playerLocation.getX()) * 2.0f;
            float y = (point.getY() - playerLocation.getY()) * 2.0f;
            final float[] vertices = new float[]{
                    (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                    (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                    (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                    (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
            };

            System.arraycopy(vertices, 0, vertices_total, i * 12, vertices.length);
            System.arraycopy(textures, 0, textures_total, i * 8, textures.length);

            i++;
        }

        backgroundModel = new Model(vertices_total, textures_total);
    }

    /**
     * generates {@code playerModel} when called
     */
    private void generatePlayerModel() {
        float x = -camera.getPosition().y * 6.0f;
        float y = camera.getPosition().x * 6.0f;

        final float[] player = new float[]{
                // TOP RIGHT
                x * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM RIGHT
                x * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                // BOTTOM LEFT
                x * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // TOP LEFT
                x * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
        };

        playerModel = new Model(player, textures);
    }

    private int counter = 0;
    private float speed;
    private boolean vertical;

    /**
     * renders the maze
     */
    public void render() {
        playerLocation = maze.getPlayerLocation();
        if (counter > 0) {
            Vector3f curPos = camera.getPosition();
            Vector3f newPosition;
            if (vertical) {
                // when moving vertically, we need to consider the fact that the world is rotated by 30 degrees
                newPosition = new Vector3f(
                        curPos.x,
                        curPos.y + ( speed * (float) Math.cos(Math.toRadians(-30.0)) ),
                        curPos.z - ( speed * (float) Math.tan(Math.toRadians(30.0)) )
                );
            } else {
                newPosition = new Vector3f(
                        curPos.x + speed,
                        curPos.y,
                        curPos.z);
            }

            camera.setPosition( newPosition );
            SHADER.setCamera(camera);
            counter--;
        }

        generatePlayerModel();

        renderBackgrounds();
        renderWalls();
        renderPlayer();
        renderCeilings();
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
        this.speed = speed * BLOCK_WIDTH * 2.0f;
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

