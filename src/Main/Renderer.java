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

    private final Point playerLocation;
    private final Maze maze;
    private final Shader SHADER = new Shader("testShader");

    private Model wallModel;
    private Model ceilModel;
    private Model backgroundModel;

    private Point globalPlayerLocation;

    private char[][] grid;

    private boolean playerMoved;

    private final float BLOCK_WIDTH = 1.0f / 6.0f;

    private final float[] textures = new float[] {
            0, 1,
            1, 1,
            1, 0,
            0, 0
    };

    public Renderer(Maze maze, int width, int height) {
        // prepare the camera
        Camera camera = new Camera();
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

        playerLocation = new Point(-1, -1);
        globalPlayerLocation = maze.getPlayerLocation();

        SHADER.bind();
        SHADER.setCamera(camera);
        SHADER.setTransform(transform);

        playerMoved = true;
    }

    private void gatherGridInfo() {
        // gather the data on the grid
        grid = maze.getNearbyGrid();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // fill the mappings according to the grid in the maze class
                if (grid[i][j] == Maze.MARKER_WALL) {
                    walls.add(new Point(i, j));
                    continue;
                } else if (grid[i][j] == Maze.MARKER_PLAYER) {
                    playerLocation.setX(i);
                    playerLocation.setY(j);

                    // check if the player moved in the maze globally
                    Point newLocation = maze.getPlayerLocation();
                    if ( !globalPlayerLocation.equals( newLocation ) ) {
                        playerMoved = true;
                    }
                }

                // we always want a background if there is not a wall on a tile
                backgrounds.add(new Point(i, j));
            }
        }
    }

    public void render() {
        gatherGridInfo();

        renderBackgrounds();
        renderWalls();

        // set the playermoved to false
        playerMoved = false;

        // clear the maps for the next render
        walls.clear();
        backgrounds.clear();
        decorations.clear();
    }

    private void renderBackgrounds() {
        if ( playerMoved ) {
            generateBackgroundModel();
        }

        // get the texture ready for rendering
        SHADER.setUniform("sampler", Background.BASIC.getSampler());
        Background.BASIC.bindTexture();

        backgroundModel.render();

    }

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

    private void renderDecorations(Point point, Decoration decoration) {

    }

    private void renderWalls() {
        if ( playerMoved ) {
            generateWallModel();
        }

        SHADER.setUniform("sampler", Wall.CASTLE_WALL.getSampler());
        Wall.CASTLE_WALL.bindTexture();
        wallModel.render();

        SHADER.setUniform("sampler", Wall.CEILING.getSampler());
        Wall.CEILING.bindTexture();
        ceilModel.render();
    }

    private void generateWallModel() {
        List<float[]> vertices_list = new ArrayList<>();
        List<float[]> ceilings_list = new ArrayList<>();

        for (Point point : walls) {
            final int x_point = point.getX();
            final int y_point = point.getY();

            // use the point to get the vertices so we can draw the wall tile
            float x = (x_point - playerLocation.getX()) * 2.0f;
            float y = (y_point - playerLocation.getY()) * 2.0f;

            if (y_point > 0 && grid[x_point][y_point - 1] != 'x' && y > 0) {
                // walls on the left
                final float[] vertices = new float[]{
                        // TOP LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // BOTTOM RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                        // BOTTOM LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f
                };

                vertices_list.add(vertices);
            } else if (y_point < grid[x_point].length - 1 && grid[x_point][y_point + 1] != 'x' && y < 0) {
                // walls on the right
                final float[] vertices = new float[]{
                        // TOP LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // BOTTOM RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                        // BOTTOM LEFT
                        (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f
                };

                vertices_list.add(vertices);
            }

            if (x_point < grid.length - 1 && grid[x_point + 1][y_point] != 'x' || x_point == grid.length - 1) {
                // walls on the face
                final float[] vertices = new float[]{
                        // BOTTOM LEFT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP LEFT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                        // TOP RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                        // BOTTOM RIGHT
                        (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f
                };

                vertices_list.add(vertices);
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
}

