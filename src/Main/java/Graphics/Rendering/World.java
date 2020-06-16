package Graphics.Rendering;

import Graphics.OpenGL.Light;
import Graphics.OpenGL.Shader;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Assets.Tiles.Background;
import Levels.Assets.Tiles.Wall;
import Levels.Characters.Enemy;
import Levels.Characters.EyeBall;
import Levels.Characters.Player;
import Levels.Framework.Maze;
import Levels.Framework.Point;
import Levels.Framework.joml.Vector3f;
import Levels.Objects.GuideBall;
import Levels.Objects.MagicBall;
import Levels.Objects.Object3D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.toRadians;

/**
 * Class for rendering the world and (for now) the player
 */
public class World {
    // the constants needed for rendering:
    // the maze for data on what to draw where
    private final Maze maze;
    // the shader to use when rendering
    private final Shader SHADER = new Shader("mazeShader");
    // the camera containing the camera position and projection on the world
    private final Camera camera;
    // the transfomation to display the world in the proper position
    private final Transform transform;
    // the tile renderer to actaully draw the world and the player
    private final TileRenderer renderer = TileRenderer.getInstance();
    // the radius around the player that is actually rendered
    private final int RADIUS = 8;

    Vector3f DARK_ATTENUATION = new Vector3f(.5f, .2f, 1.5f);
    Vector3f LIGHT_ATTENUATION = new Vector3f(.5f, .2f, .5f);

    // the light object
    private final Light[] lights = {
            new Light(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),
            new Light(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),
            new Light(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),
            new Light(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),
            new Light(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),

            new Light(new Vector3f(2, 4, 1f), new Vector3f(1f, 0.2f, 0.2f), MagicBall.getInstance(), LIGHT_ATTENUATION),
            new Light(new Vector3f(2, 4, 5f), new Vector3f(1f, 0.2f, 0.2f), null, LIGHT_ATTENUATION),

            new Light(new Vector3f(6, 4, 1f), new Vector3f(1f, 1f, 0.2f), MagicBall.getInstance(), LIGHT_ATTENUATION),
            new Light(new Vector3f(6, 4, 5f), new Vector3f(1f, 1f, 0.2f), null, LIGHT_ATTENUATION),

            new Light(new Vector3f(10, 4, 1f), new Vector3f(0.2f, 1f, 0.2f), MagicBall.getInstance(), LIGHT_ATTENUATION),
            new Light(new Vector3f(10, 4, 5f), new Vector3f(0.2f, 1f, 0.2f), null, LIGHT_ATTENUATION),

            new Light(new Vector3f(2, 16, 1f), new Vector3f(1f, 0.2f, 1f), MagicBall.getInstance(), LIGHT_ATTENUATION),
            new Light(new Vector3f(2, 16, 5f), new Vector3f(1f, 0.2f, 1f), null, LIGHT_ATTENUATION),

            new Light(new Vector3f(6, 16, 1f), new Vector3f(0.2f, 0.2f, 1f), MagicBall.getInstance(), LIGHT_ATTENUATION),
            new Light(new Vector3f(6, 16, 5f), new Vector3f(0.2f, 0.2f, 1f), null, LIGHT_ATTENUATION),

            new Light(new Vector3f(10, 16, 1f), new Vector3f(0.2f, 1f, 1f), MagicBall.getInstance(), LIGHT_ATTENUATION),
            new Light(new Vector3f(10, 16, 5f), new Vector3f(0.2f, 1f, 1f), null, LIGHT_ATTENUATION)
    };

    // variables to keep track of the player location in the world
    private float xPlayer;
    private float yPlayer;

    private Player player;

    private GuideBall guide;

    // List that holds the enemies
    private ArrayList<Enemy> enemyList = new ArrayList<>();

    /**
     * initialises the global variables for the renderer
     *
     * @param maze   the maze that has to be rendered
     * @param width  the width of the window
     * @param height the height of the window
     */
    public World(Maze maze, int width, int height) {
        // prepare the camera by setting up its perspective
        camera = new Camera();
        camera.setPerspective((float) toRadians(40.0), (float) width / (float) height, 0.01f, 1000.0f);

        // prepare the transformations of the world
        transform = new Transform();
        transform.getRotation().rotateAxis(-(float) toRadians(35.0), 1, 0, 0);

        // set the maze object
        this.maze = maze;

        // position the camera
        resetCameraPosition();

        // Initialize enemies
        for (Point i : maze.enemyLocation) {
            EyeBall eyeball = new EyeBall(100, 100);
            eyeball.initializePosition(i.getX(), i.getY(), maze.getGrid().length);
            enemyList.add(eyeball);
        }
    }

    /**
     * method that centers the camera on the player
     */
    public void resetCameraPosition() {
        // initialize the player location variables
        yPlayer = maze.getPlayerLocation().getX();
        xPlayer = maze.getPlayerLocation().getY();

        // initialize the camera by centering it on the player
        camera.setPosition(new Vector3f(
                xPlayer * 2,
                (maze.getGrid().length - yPlayer) * 2 - 10,
                16
        ));

        this.player = Player.getInstance();
    }

    // sets used for gathering points to determine drawing locations of tiles
    private final Set<Point> floors = new HashSet<>();
    private final Set<Point> leftWalls = new HashSet<>();
    private final Set<Point> rightWalls = new HashSet<>();
    private final Set<Point> faceWalls = new HashSet<>();
    private final Set<Point> ceilings = new HashSet<>();

    /**
     * renders the maze
     */
    public void render() {
        // set the camera and shader for the renderer
        renderer.setShader(SHADER);
        renderer.setCamera(camera);
        renderer.setTransform(transform);
        SHADER.setLights(lights);

        fillRenderSets();
        renderSets();
        clearRenderSets();
    }

    /**
     * fills the sets of points that are rendered in the render method
     */
    private void fillRenderSets() {
        char[][] grid = maze.getGrid();

        int x_player = maze.getPlayerLocation().getX();
        int y_player = maze.getPlayerLocation().getY();

        // get the start and end coordinates of both x and y axis
        int x_start = Math.max(x_player - RADIUS, 0);
        int y_start = Math.max(y_player - RADIUS, 0);
        int x_end = Math.min(x_player + RADIUS + 1, grid.length);
        int y_end = Math.min(y_player + RADIUS + 1, grid[x_player].length);

        for (int i = x_start; i < x_end; i++) {
            for (int j = y_start; j < y_end; j++) {
                // determine what tile needs to be drawn and fill that into the sets made above
                if (grid[i][j] == Maze.MARKER_WALL) {
                    // we check if the tile to left is also a wall
                    if (j > 0 && grid[i][j - 1] != Maze.MARKER_WALL) {
                        // if there is no wall to the left, we wish to draw the left side of this wall
                        leftWalls.add(new Point(j, grid.length - i));
                    }

                    // we check if the tile to right is also a wall
                    if (j < grid[i].length - 1 && grid[i][j + 1] != Maze.MARKER_WALL) {
                        // if there is no wall to the right, we wish to draw the right side of this wall
                        rightWalls.add(new Point(j, grid.length - i));
                    }

                    // we check if the tile in front is also a wall
                    if (i < grid.length - 1 && grid[i + 1][j] != Maze.MARKER_WALL) {
                        // if there is no wall in front, we wish to draw the face of this wall
                        faceWalls.add(new Point(j, grid.length - i));
                    }

                    // we always want to render a ceiling:
                    ceilings.add(new Point(j, grid.length - i));
                } else if (grid[i][j] != Maze.MARKER_WALL) {
                    floors.add(new Point(j, grid.length - i));
                }
            }
        }
    }

    /**
     * renders the tiles as specified in the sets filled in in {@code fillRenderSets}
     */
    private void renderSets() {
        for (Point point : floors) {
            renderer.addNormalMap(Background.DIRT_NORMAL.getTexture());
            renderer.renderTile(Background.DIRT.getTexture(), point.getX(), point.getY(), TileRenderer.FLOOR);
        }

        for (Point point : leftWalls) {
            renderer.addNormalMap(Wall.BRICKWALL_NORMAL.getTexture());
            renderer.renderTile(Wall.BRICKWALL.getTexture(), point.getX(), point.getY(), TileRenderer.LEFTS);
        }

        for (Point point : rightWalls) {
            renderer.addNormalMap(Wall.BRICKWALL_NORMAL.getTexture());
            renderer.renderTile(Wall.BRICKWALL.getTexture(), point.getX(), point.getY(), TileRenderer.RIGHT);
        }

        for (Point point : faceWalls) {
            renderer.addNormalMap(Wall.BRICKWALL_NORMAL.getTexture());
            renderer.renderTile(Wall.BRICKWALL.getTexture(), point.getX(), point.getY(), TileRenderer.FACES);
        }

        // Render enemies
        for (Enemy enemy : enemyList) {
            enemy.move(maze.getPlayerLocation(), maze.getGrid());
            renderer.renderCharacter(enemy);
        }

        // Render player
        player.setGridPosition(xPlayer, yPlayer, maze.getGrid().length);
        renderer.renderCharacter(player);

        if(player.hasGuide()) {
            if (guide == null) {
                System.out.println("INIT/.....");
                guide = new GuideBall();
                guide.setPosition(new Vector3f(maze.playerLocation.getX(), maze.playerLocation.getY(), 3f));
                guide.setColor(new Vector3f(.7f, .7f, .7f));
                guide.initializePosition(maze.playerLocation.getX(), maze.playerLocation.getY(), maze.getGrid().length);
            }
            guide.move(maze.endPoint, maze.getGrid());
            renderer.renderObject(guide);
        } else {
            guide = null;
        }

        lights[0].setPosition(new Vector3f(player.getPosition().x, player.getPosition().y, 1f));
        lights[0].setAttenuation(player.getCurrentAttenuation());
        lights[1].setPosition(new Vector3f(player.getPosition().x + 1f, player.getPosition().y, 5f));
        lights[1].setAttenuation(player.getCurrentAttenuation());
        lights[2].setPosition(new Vector3f(player.getPosition().x - 1f, player.getPosition().y, 5f));
        lights[2].setAttenuation(player.getCurrentAttenuation());
        lights[3].setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 1f, 5f));
        lights[3].setAttenuation(player.getCurrentAttenuation());
        lights[4].setPosition(new Vector3f(player.getPosition().x, player.getPosition().y - 1f, 5f));
        lights[4].setAttenuation(player.getCurrentAttenuation());

        for (Light light : lights) {
            Object3D obj = light.getObject();
            if (light.getObject() != null) {
                Vector3f pos = new Vector3f(light.getPosition().x, light.getPosition().y, 0f);
                obj.setPosition(pos);
                obj.setColor(light.getColor());
                renderer.renderObject(obj);
            }
        }

        for (Point point : ceilings) {
            renderer.renderTile(Wall.CEILING.getTexture(), point.getX(), point.getY(), TileRenderer.CEILS);
        }
    }

    /**
     * clears all the sets with points specifying render positions
     */
    private void clearRenderSets() {
        floors.clear();
        leftWalls.clear();
        rightWalls.clear();
        faceWalls.clear();
        ceilings.clear();
    }

    /**
     * moves the player character as specified by the parameters
     *
     * @param speed    the distance covered in a single frame
     * @param vertical whether movement is supposed to be vertical or not
     */
    public void movePlayer(float speed, boolean vertical) {
        // calculate the amount of movement in the x and y direction
        xPlayer += vertical ? 0f : speed;
        yPlayer += vertical ? -speed : 0f;
        Vector3f playerPos = this.player.getPosition();
        playerPos.z = 1.5f;

        // upon moving the player, we also have to move the camera
        adjustCamera(speed * 2f, vertical);
    }

    /**
     * moves the camera, as specified by the parameters
     *
     * @param speed    the distance covered by the movement in a single frame
     * @param vertical whether movement is supposed to be vertical or not
     */
    private void adjustCamera(float speed, boolean vertical) {
        // calculate the new camera position
        Vector3f cameraPos = camera.getPosition();
        camera.setPosition(new Vector3f(
                cameraPos.x + (vertical ? 0 : speed),
                cameraPos.y + (vertical ? speed : 0),
                cameraPos.z
        ));

        // adjust the camera for the shader, so it actually has an effect on the position of the render
        SHADER.setCamera(camera);
    }
}

