package Levels.Framework;

import Levels.Assets.Items.Item;
import Levels.Objects.Door;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * class for communicating about the maze with both AI and renderer
 *
 * This keeps check of the state of the maze
 */
public class Maze {
    private char[][] grid;

    public Point playerLocation;
    public Point endLocation;
    private final File file;

    public final static char MARKER_DOOR = 'D';
    public final static char MARKER_PLAYER = 'P';
    public final static char MARKER_SPACE = ' ';
    public final static char MARKER_ENEMY = 'e';
    public final static char MARKER_WALL = 'x';
    public final static char MARKER_END = 'f';
    public final static char MARKER_END_WALL = 'F';
    // the markers for item orbs based on the item ids found in the Item class
    public final static char MARKER_HEART = (char) Item.HEART + '0';
    public final static char MARKER_MANA = (char) Item.MANA + '0';
    public final static char MARKER_BOOT = (char) Item.BOOT + '0';
    public final static char MARKER_KEY = (char) Item.KEY + '0';
    public final static char MARKER_HPOT = (char) Item.H_POTION + '0';
    public final static char MARKER_MPOT = (char) Item.M_POTION + '0';

    public final static List<Character> ITEM_MARKERS = new ArrayList<>( Arrays.asList(
            MARKER_HEART,
            MARKER_MANA,
            MARKER_BOOT,
            MARKER_KEY,
            MARKER_HPOT,
            MARKER_MPOT
    ) );

    public static List<Point> enemyLocation = new ArrayList<>();

    /**
     * reads a new file into the maze object
     *
     * @param filename the path to the maze file
     */
    public Maze(String filename) {
        file = new File("src/Main/java/Levels/Framework/" + filename + ".mze");
        try {
            rebuildGrid();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * builds the grid as it is specified in {@code file}
     *
     * @throws FileNotFoundException if the file can not be found
     */
    public void rebuildGrid() throws FileNotFoundException {
        // read the file into a List line by line
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        List<String> lines = reader.lines().collect( Collectors.toList() );

        grid = new char[ lines.size() ][ lines.get(0).length() ];

        for (int i = 0; i < grid.length; i++) {
            grid[i] = lines.get(i).toCharArray();
        }

        int x_player = -1;
        int y_player = -1;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == MARKER_PLAYER) {
                    x_player = i;
                    y_player = j;
                    break;
                }
                if (grid[i][j] == MARKER_ENEMY) {
                    enemyLocation.add(new Point(i, j));
                    break;
                }
                if (grid[i][j] == MARKER_END) {
                    endLocation = new Point(i, j);
                    break;
                }
            }
        }

        playerLocation = new Point(x_player, y_player);
    }

    /**
     * returns the entire grid
     *
     * @return {@code grid}
     */
    public char[][] getGrid() {
        return grid;
    }

    /**
     * changes the player location both in the grid and the point that is kept as a record
     *
     * @param x the new x coordinate
     * @param y the new y coordinate
     */
    public void setPlayerLocation(int x, int y) {
        // remove the current player
        grid[ playerLocation.getX() ][ playerLocation.getY() ] = MARKER_SPACE;

        // set the x and y coordinates into the playerLocation
        playerLocation.setX(x);
        playerLocation.setY(y);

        // place a player at the given x and y
        grid[ playerLocation.getX() ][ playerLocation.getY() ] = MARKER_PLAYER;
    }

    /**
     * Returns the position of th player in the grid as a point, if the player marker could not be found in the grid
     * an exception is thrown
     *
     * @return {@code new Point(x_player, y_player)}
     */
    public Point getPlayerLocation() {
        return playerLocation;
    }

    public void saveCurrentMaze() throws FileNotFoundException {
        PrintWriter printer = new PrintWriter( file );

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                printer.print( grid[i][j] );
            }

            if (i < grid.length - 1) {
                printer.println();
            }
        }

        printer.close();
    }

    /**
     * Moves the player up by one tile
     */
    public void moveUp() {
        // get the current player location
        int x = playerLocation.getX();
        int y = playerLocation.getY();

        if ( canMoveUp() ) {
            grid[x][y] = MARKER_SPACE;
            grid[x - 1][y] = MARKER_PLAYER;

            playerLocation.setX(x - 1);
        }
    }

    /**
     * detects if the player can move one tile upwards
     *
     * @return {@code grid[x - 1][y] == MARKER_SPACE}
     */
    public boolean canMoveUp() {
        // get the current player location
        int x = playerLocation.getX();
        int y = playerLocation.getY();

        return grid[x - 1][y] == MARKER_SPACE || grid[x - 1][y] == MARKER_END;
    }

    /**
     * Moves the player down by one tile
     */
    public void moveDown() {
        // get the current player location
        int x = playerLocation.getX();
        int y = playerLocation.getY();

        if ( canMoveDown() ) {
            grid[x][y] = MARKER_SPACE;
            grid[x + 1][y] = MARKER_PLAYER;

            playerLocation.setX(x + 1);
        }
    }

    /**
     * detects if the player can move one tile downwards
     *
     * @return {@code grid[x + 1][y] == MARKER_SPACE}
     */
    public boolean canMoveDown() {
        // get the current player location
        int x = playerLocation.getX();
        int y = playerLocation.getY();

        return grid[x + 1][y] == MARKER_SPACE || grid[x + 1][y] == MARKER_END;
    }

    /**
     * Moves the player to the left by one tile
     */
    public void moveLeft() {
        // get the current player location
        int x = playerLocation.getX();
        int y = playerLocation.getY();

        if ( canMoveLeft() ) {
            grid[x][y] = MARKER_SPACE;
            grid[x][y - 1] = MARKER_PLAYER;

            playerLocation.setY(y - 1);
        }
    }

    /**
     * detects if the player can move one tile to the left
     *
     * @return {@code grid[x][y - 1] == MARKER_SPACE}
     */
    public boolean canMoveLeft() {
        // get the current player location
        int x = playerLocation.getX();
        int y = playerLocation.getY();

        return grid[x][y - 1] == MARKER_SPACE || grid[x][y - 1] == MARKER_END;
    }

    /**
     * Moves the player to the right by one tile
     */
    public void moveRight() {
        // get the current player location
        int x = playerLocation.getX();
        int y = playerLocation.getY();

        if ( canMoveRight() ) {
            grid[x][y] = MARKER_SPACE;
            grid[x][y + 1] = MARKER_PLAYER;

            playerLocation.setY(y + 1);
        }
    }

    /**
     * detects if the player can move one tile to the right
     *
     * @return {@code grid[x][y + 1] == MARKER_SPACE}
     */
    public boolean canMoveRight() {
        // get the current player location
        int x = playerLocation.getX();
        int y = playerLocation.getY();

        return grid[x][y + 1] == MARKER_SPACE || grid[x][y + 1] == MARKER_END;
    }
}
