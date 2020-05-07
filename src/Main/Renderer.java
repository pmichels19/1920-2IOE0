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
    private Map<Point, Background> backgrounds;
    private Map<Point, Decoration> decorations;
    private Set<Point> walls;

    private Point playerLocation;
    private Camera camera;
    private Transform transform;
    private Maze maze;
    private final Shader SHADER = new Shader("testShader");

    private char[][] grid;

    private final float BLOCK_WIDTH = 1.0f / 6.0f;

    private final float[] textures = new float[] {
            0, 1,
            1, 1,
            1, 0,
            0, 0
    };

    private List<Model> lefts, rights, ups, ceilings;

    public Renderer(Maze maze, int width, int height) {
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
        transform = new Transform();
        transform.getRotation().rotateAxis((float) Math.toRadians(270.0), 0, 0, 1);
        transform.getRotation().rotateAxis((float) Math.toRadians(-30.0), 0, 1, 0);

        backgrounds = new HashMap<>();
        decorations = new HashMap<>();
        walls = new HashSet<Point>();

        this.maze = maze;

        lefts = new ArrayList<>();
        rights = new ArrayList<>();
        ups = new ArrayList<>();
        ceilings = new ArrayList<>();
    }

    private void gatherGridInfo() {

        // gather the data on the grid
        grid = maze.getGrid();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // fill the mappings according to the grid in the maze class
                if (grid[i][j] == 'x') {
                    walls.add(new Point(i, j));
                    continue;
                } else if (grid[i][j] == 'P') {
                    playerLocation = new Point(i, j);
                    backgrounds.put(playerLocation, Background.PLAYER);
                }

                // we always want a background if there is not a wall on a tile
                backgrounds.put(new Point(i, j), Background.BASIC);
            }
        }
    }

    public void render() {
        gatherGridInfo();

        SHADER.bind();
        SHADER.setCamera(camera);
        SHADER.setTransform(transform);

        for ( Map.Entry<Point, Background> entry : backgrounds.entrySet() ) {
            renderBackgrounds( entry.getKey(), entry.getValue() );
        }

//        for ( Map.Entry<Point, Decoration> entry : decorations.entrySet() ) {
//            renderDecorations( entry.getKey(), entry.getValue() );
//        }

        for ( Point point : walls ) {
            generateWallModels( point );
        }

        // draw the walls
        renderWalls();

        // clear the maps so we can keep that juicy 60 fps
        walls.clear();
        backgrounds.clear();
        decorations.clear();
    }

    private void renderWalls() {
        SHADER.setUniform("sampler", Wall.CASTLE_WALL.getSampler());
        Wall.CASTLE_WALL.bindTexture();

        for (Model model : lefts) {
            model.render();
        }
        lefts.clear();

        for (Model model : rights) {
            model.render();
        }
        rights.clear();

        for (Model model : ups) {
            model.render();
        }
        ups.clear();

        // draw the ceilings
        SHADER.setUniform("sampler", Wall.CEILING.getSampler());
        Wall.CEILING.bindTexture();
        for (Model model : ceilings) {
            model.render();
        }
        ceilings.clear();
    }

    private void renderBackgrounds(Point point, Background background) {
        // use the point to get the vertices so we can draw the wall tile
        float x = ( point.getX() - playerLocation.getX() ) * 2.0f;
        float y = ( point.getY() - playerLocation.getY() ) * 2.0f;
        final float[] vertices = new float[] {
                (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
        };

        Model model = new Model(vertices, textures);

        // get the texture ready for rendering
        SHADER.setUniform("sampler", background.getSampler());
        background.bindTexture();

        model.render();
    }

    private void renderDecorations(Point point, Decoration decoration) {

    }

    private void generateWallModels(Point point) {
        final int x_point = point.getX();
        final int y_point = point.getY();

        // use the point to get the vertices so we can draw the wall tile
        float x = ( x_point - playerLocation.getX() ) * 2.0f;
        float y = ( y_point - playerLocation.getY() ) * 2.0f;

        if (y_point > 0 && grid[x_point][y_point - 1] != 'x' && y > 0) {
            // walls on the left
            final float[] vertices = new float[] {
                    // TOP LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // TOP RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // BOTTOM RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f,
                    // BOTTOM LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f
            };

            Model model = new Model(vertices, textures);
            lefts.add(model);
        } else if (y_point < grid[x_point].length - 1 && grid[x_point][y_point + 1] != 'x' && y < 0) {
            // walls on the right
            final float[] vertices = new float[] {
                    // TOP LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // TOP RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // BOTTOM RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                    // BOTTOM LEFT
                    (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f
            };

            rights.add( new Model(vertices, textures) );
        }

        if (x_point < grid.length - 1 && grid[x_point + 1][y_point] != 'x' || x_point == grid.length - 1) {
            // walls on the face
            final float[] vertices = new float[] {
                    // BOTTOM LEFT
                    (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // TOP LEFT
                    (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                    // TOP RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 0.0f,
                    // BOTTOM RIGHT
                    (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 0.0f
            };

            ups.add( new Model(vertices, textures) );
        }

        final float[] ceiling = new float[] {
                // TOP LEFT
                (x - 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // TOP RIGHT
                (x + 1.0f) * BLOCK_WIDTH, (y + 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // BOTTOM RIGHT
                (x + 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH,
                // BOTTOM LEFT
                (x - 1.0f) * BLOCK_WIDTH, (y - 1.0f) * BLOCK_WIDTH, 2 * BLOCK_WIDTH
        };

        // and return the ceiling model
        ceilings.add( new Model(ceiling, textures) );
    }
}

class Point {
    private int x;
    private int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
