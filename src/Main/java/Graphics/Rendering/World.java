package Graphics.Rendering;

import Graphics.OpenGL.Light;
import Graphics.OpenGL.Shader;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Assets.Items.Item;
import Levels.Assets.Tiles.Background;
import Levels.Assets.Tiles.Wall;
import Levels.Characters.Enemy;
import Levels.Characters.EyeBall;
import Levels.Characters.Player;
import Levels.Framework.Maze;
import Levels.Framework.Point;
import Levels.Framework.joml.Vector3f;
import Levels.Objects.*;

import java.util.*;

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

    Vector3f NO_LIGHT = new Vector3f(.5f, .2f, 100f);
    Vector3f DARK_ATTENUATION = new Vector3f(.5f, .2f, 1.5f);
    Vector3f LIGHT_ATTENUATION = new Vector3f(.5f, .2f, .5f);

    // the light object
    private Light[] player_lights = {
            new Light(new Vector3f(), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),
            new Light(new Vector3f(), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),
            new Light(new Vector3f(), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),
            new Light(new Vector3f(), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),
            new Light(new Vector3f(), new Vector3f(1f, 1f, 1f), null, LIGHT_ATTENUATION),

            new Light(new Vector3f(), new Vector3f(1f, 1f, 1f), null, NO_LIGHT),
            new Light(new Vector3f(), new Vector3f(1f, 1f, 1f), null, NO_LIGHT),
    };

    // variables to keep track of the player location in the world
    private float xPlayer;
    private float yPlayer;

    private Player player;

    private GuideBall guide;
    private boolean reset = true;
    private FireBall fireBall;
    private boolean shot = false;

    // List that holds the enemies
    private final ArrayList<Enemy> enemyList = new ArrayList<>();

    // the map that holds light objects to be rendered at the position defined by the point
    private final Map<Point, Set<Light>> lightMap = new HashMap<>();
    private final Map<Point, Door> doorMap = new HashMap<>();

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
        int enemyCount = 1;
        for (int i = 0; i < enemyCount; i++) {
            EyeBall eyeball = new EyeBall(100, 100);
//            eyeball.initializePosition(maze.getGrid().length / enemyCount * i, maze.getGrid().length / enemyCount * i, maze.getGrid().length);
            // TODO: Set spawn location for enemies in the maze
            eyeball.initializePosition(1, 1, maze.getGrid().length);
            enemyList.add(eyeball);
        }

        createLights();
        createDoors();
    }

    /**
     * method that fills the {@code lightMap}
     */
    private void createLights() {
        // loop over the grid to identify where to place lightsources
        char[][] grid = maze.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if ( Maze.ITEM_MARKERS.contains( grid[y][x] ) ) {
                    Point position = new Point(x, grid.length - y);
                    // if no set of lights exists for the current position yet, make one
                    if ( !lightMap.containsKey( position ) ) {
                        lightMap.put( position, new HashSet<>() );
                    }
                    Set<Light> toAdd = lightMap.get(position);
                    // add the orb with the desired light color
                    toAdd.add(
                            new Light(
                                    new Vector3f(x * 2, (grid.length - y) * 2, 1f),
                                    Item.getColorById(Character.getNumericValue(grid[y][x])),
                                    MagicBall.getInstance(),
                                    LIGHT_ATTENUATION
                            )
                    );
                    toAdd.add(
                            new Light(
                                    new Vector3f(x * 2, (grid.length - y) * 2, 5f),
                                    Item.getColorById(Character.getNumericValue(grid[y][x])),
                                    null,
                                    LIGHT_ATTENUATION
                            )
                    );
                }
            }
        }
    }

    /**
     * method that fills the {@code doorMap}
     */
    private void createDoors() {
        // loop over the grid to identify where to place lightsources
        char[][] grid = maze.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if ( grid[y][x] == Maze.MARKER_DOOR ) {
                    doorMap.put(
                            new Point(x, grid.length - y),
                            new Door(
                                    grid[y - 1][x] == Maze.MARKER_WALL && grid[y + 1][x] == Maze.MARKER_WALL,
                                    x,
                                    grid.length - y
                            )
                    );
                }
            }
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
    }

    // sets used for gathering points to determine drawing locations of tiles
    private final Set<Point> floors = new HashSet<>();
    private final Set<Point> leftWalls = new HashSet<>();
    private final Set<Point> rightWalls = new HashSet<>();
    private final Set<Point> faceWalls = new HashSet<>();
    private final Set<Point> ceilings = new HashSet<>();

    private final Set<Light> lights = new HashSet<>();
    private final Set<Door> doors = new HashSet<>();

    /**
     * renders the maze
     */
    public void render() {
        // first, retrieve the player instance
        player = Player.getInstance();

        // set the camera and shader for the renderer
        renderer.setShader(SHADER);
        renderer.setCamera(camera);
        renderer.setTransform(transform);
        SHADER.setUniform("invisibility", player.getInvisibility());

        fillRenderSets();
        setActiveLights();
        renderSets();
        checkDoors();
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
        // the radius around the player that is actually rendered
        int RADIUS = 8;
        int x_start = Math.max(x_player - RADIUS, 0);
        int y_start = Math.max(y_player - RADIUS, 0);
        int x_end = Math.min(x_player + RADIUS + 1, grid.length);
        int y_end = Math.min(y_player + RADIUS + 1, grid[x_player].length);

        for (int i = x_start; i < x_end; i++) {
            for (int j = y_start; j < y_end; j++) {
                Point position = new Point( j, grid.length - i );
                // determine what tile needs to be drawn and fill that into the sets made above
                if (grid[i][j] == Maze.MARKER_WALL) {
                    // we check if the tile to left is also a wall
                    if (j > 0 && grid[i][j - 1] != Maze.MARKER_WALL) {
                        // if there is no wall to the left, we wish to draw the left side of this wall
                        leftWalls.add(position);
                    }

                    // we check if the tile to right is also a wall
                    if (j < grid[i].length - 1 && grid[i][j + 1] != Maze.MARKER_WALL) {
                        // if there is no wall to the right, we wish to draw the right side of this wall
                        rightWalls.add(position);
                    }

                    // we check if the tile in front is also a wall
                    if (i < grid.length - 1 && grid[i + 1][j] != Maze.MARKER_WALL) {
                        // if there is no wall in front, we wish to draw the face of this wall
                        faceWalls.add(position);
                    }

                    // we always want to render a ceiling:
                    ceilings.add(position);
                } else if ( Maze.ITEM_MARKERS.contains( grid[i][j] ) ) {
                    floors.add(position);
                    // if the maze entry contains an item, then there should be a set of light objects for it
                    if (lightMap.containsKey(position)) {
                        lights.addAll(lightMap.get(position));
                    }
                } else if (grid[i][j] == Maze.MARKER_DOOR) {
                    floors.add(position);
                    if ( doorMap.containsKey(position) ) {
                        doors.add( doorMap.get(position) );
                    }
                } else if (grid[i][j] != Maze.MARKER_WALL) {
                    floors.add(position);
                }
            }
        }
    }

    /**
     * method that passes the lights that are currently active in the world to the shader
     */
    private void setActiveLights() {
        // after filling the sets, we want to check which lights are active and pass those to the shader
        Light[] active_lights = new Light[ lights.size() + player_lights.length ];
        int i = 0;
        for (Light light : lights) {
            active_lights[i] = light;
            i++;
        }

        if(player.hasGuide()) {
            if (guide == null || guide.isNew) {
                guide = new GuideBall();
                guide.setPosition(new Vector3f(maze.playerLocation.getX(), maze.playerLocation.getY(), 3f));
                guide.setColor(new Vector3f(.7f, .7f, .7f));
                guide.initializePosition(maze.playerLocation.getX(), maze.playerLocation.getY(), maze.getGrid().length);
                player_lights[5].setAttenuation(LIGHT_ATTENUATION);
                guide.isNew = false;
            }

            player_lights[5].setPosition(new Vector3f(guide.getPosition().x, guide.getPosition().y, 1f));
            guide.move(maze.endLocation, maze.getGrid());
            renderer.renderObject(guide);
            reset = true;
        } else {
            Timer timer = new Timer();
            if (guide != null) {
                guide.isNew = true;
                guide.setColor(new Vector3f(.1f, .1f, .1f));
                player_lights[5].setAttenuation(DARK_ATTENUATION);
                renderer.renderObject(guide);

                if (reset) {
                    reset = false;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            guide = null;
                            player_lights[5].setAttenuation(NO_LIGHT);
                        }
                    }, 2 * 1000);
                }
            }
        }

        if (player.fireballShot()) {
            if (fireBall == null) {
                fireBall = new FireBall();
                fireBall.setPosition(new Vector3f(maze.playerLocation.getX(), maze.playerLocation.getY(), 3f));
                fireBall.setColor(new Vector3f(1f, .75f, .0f));
                fireBall.initializePosition(maze.playerLocation.getX(), maze.playerLocation.getY(), maze.getGrid().length);
                player_lights[6].setAttenuation(LIGHT_ATTENUATION);
                shot = true;
            }
            if (fireBall.getMazePosition().equals(nearestEnemyLoc(fireBall.getMazePosition()))) {
                player.setFireball(false);
                shot = false;
                fireBall = null;
                player_lights[6].setAttenuation(NO_LIGHT);
            }
            if (shot) {
                player_lights[6].setPosition(new Vector3f(fireBall.getPosition().x, fireBall.getPosition().y, 1f));
                fireBall.move(nearestEnemyLoc(fireBall.getMazePosition()), maze.getGrid());
                renderer.renderObject(fireBall);
            }
        }

        // correct the location of the player lights
        player_lights[0].setPosition(new Vector3f(player.getPosition().x, player.getPosition().y, 1f));
        player_lights[0].setAttenuation(player.getCurrentAttenuation());
        player_lights[1].setPosition(new Vector3f(player.getPosition().x + 1f, player.getPosition().y, 5f));
        player_lights[1].setAttenuation(player.getCurrentAttenuation());
        player_lights[2].setPosition(new Vector3f(player.getPosition().x - 1f, player.getPosition().y, 5f));
        player_lights[2].setAttenuation(player.getCurrentAttenuation());
        player_lights[3].setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 1f, 5f));
        player_lights[3].setAttenuation(player.getCurrentAttenuation());
        player_lights[4].setPosition(new Vector3f(player.getPosition().x, player.getPosition().y - 1f, 5f));
        player_lights[4].setAttenuation(player.getCurrentAttenuation());

        active_lights[i] = player_lights[0];
        active_lights[i + 1] = player_lights[1];
        active_lights[i + 2] = player_lights[2];
        active_lights[i + 3] = player_lights[3];
        active_lights[i + 4] = player_lights[4];
        active_lights[i + 5] = player_lights[5];
        active_lights[i + 6] = player_lights[6];

        SHADER.setLights( active_lights );
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

        for (Door door : doors) {
            if (!door.isVertical()) {
                float x_door = door.getX() - (1f - (((float) door.getOffset()) / ((float) Door.getToggleFrames())));
                renderer.renderTile(Wall.FENCE.getTexture(), x_door, door.getY() + 0.5f, TileRenderer.FACES);
            }
        }

        for (Point point : faceWalls) {
            renderer.addNormalMap(Wall.BRICKWALL_NORMAL.getTexture());
            renderer.renderTile(Wall.BRICKWALL.getTexture(), point.getX(), point.getY(), TileRenderer.FACES);
        }

        for (Door door : doors) {
            if (door.isVertical()) {
                float y_door = door.getY() - (1f - (((float) door.getOffset()) / ((float) Door.getToggleFrames())));
                renderer.renderTile(Wall.FENCE.getTexture(), door.getX() + 0.5f, y_door, TileRenderer.LEFTS);
            }
        }

        // Render enemies
        for (Enemy enemy : enemyList) {
            enemy.move(maze.getPlayerLocation(), maze.getGrid());
            renderer.renderCharacter(enemy);
        }

        // Render player
        player.setGamePositionAndRotate(xPlayer, yPlayer, maze.getGrid().length);
        renderer.renderCharacter(player);

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
     * this method checks the doors in the doors set to see if any doors have been opened
     */
    private void checkDoors() {
        for (Door door : doors) {
            if ( door.isOpen() ) {
                // if the door has been opened, remove it from the doorMap and put a space in the grid on the door entry
                doorMap.remove( new Point( door.getX(), door.getY() ) );
                char[][] grid = maze.getGrid();
                // remove the door from the grid, so the player can walk through
                grid[grid.length - door.getY()][door.getX()] = Maze.MARKER_SPACE;
            }
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
        doors.clear();
        lights.clear();
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
     * returns the door found at point {@code point} in the maze
     *
     * @param point a key in the doormap
     * @return {@code doorMap.getOrDefault(point, null)}
     */
    public Door getDoor(Point point) {
        return doorMap.getOrDefault(point, null);
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

    /**
    *  Get nearest enemy to attack with fireball
    */
    private Point nearestEnemyLoc(Point p) {
        Enemy nearest = null;
        float score = Float.MAX_VALUE;
        int px = p.getX();
        int py = p.getY();
        for (Enemy e : this.enemyList) {
            int x = e.getMazePosition().getX();
            int y = e.getMazePosition().getY();
            float newScore = (x - px) * (x - px) + (y - py) * (y - py);
            if (newScore < score) {
                nearest = e;
                score = newScore;
            }
        }
        return nearest.getMazePosition();
    }

    public List<Enemy> getEnemyList() {
        return this.enemyList;
    }

    public Light[] getLights() {
        return this.player_lights;
    }

    public void setLights(Light[] lights) {
        this.player_lights = lights;
    }
}

