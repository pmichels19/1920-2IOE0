package Levels.Framework;

import Main.Point;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * class for communicating about the maze with both AI and renderer
 *
 * This keeps check of the state of the maze
 */
public class Maze {
    private char[][] grid;

    public final static char MARKER_WALL = 'x';
    public final static char MARKER_PLAYER = 'P';
    public final static char MARKER_SPACE = ' ';

    /**
     * reads a new file into the maze object
     *
     * @param filename the path to the maze file
     * @throws FileNotFoundException if the filepath does not lead to an existing file
     */
    public Maze(String filename) throws IOException {
        File maze = new File("src/Levels/Framework/" + filename + ".mze");

        // read the file into a List line by line
        BufferedReader reader = new BufferedReader( new FileReader(maze) );
        List<String> lines = reader.lines().collect( Collectors.toList() );

        grid = new char[ lines.size() ][ lines.get(0).length() ];

        for (int i = 0; i < grid.length; i++) {
            grid[i] = lines.get(i).toCharArray();
        }
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
     * returns the part of the grid in a 12 tile radius around the player
     *
     * @return a subset of {@code grid}
     */
    public char[][] getNearbyGrid() {
        Point loc = getPlayerLocation();
        int range = 11;
        int x = loc.getX();
        int y = loc.getY();

        // set up variables for deciding size of subgrid
        int x_start = x - range + 1;
        int x_end = x + range;
        while (x_start < 0) {
            x_start++;
        }
        while (x_end > grid.length) {
            x_end--;
        }

        int y_start = y - range + 1;
        int y_end = y + range;
        while (y_start < 0) {
            y_start++;
        }
        while (y_end > grid[0].length) {
            y_end--;
        }

        char[][] result = new char[x_end - x_start][y_end - y_start];

        for (int i = x_start; i < x_end; i++) {
            for (int j = y_start; j < y_end; j++) {
                result[i - x_start][j - y_start] = grid[i][j];
            }
        }

        return result;
    }

    public Point getPlayerLocation() {
        int x = -1;
        int y = -1;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == MARKER_PLAYER) {
                    x = i;
                    y = j;
                    break;
                }
            }
        }

        // we check if the player was found
        if (x == -1) throw new IllegalStateException("The player has vanished into the void somehow");

        return new Point(x, y);
    }

    // TODO: make methods to move the (N)PCs, currently holding off on this to make sure this works with the AI part
}
