package Levels.Framework;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * class for communicating about the maze with both AI and renderer
 *
 * This keeps check of the state of the maze
 */
public class Maze {
    private char[][] grid;

    /**
     * reads a new file into the maze object
     *
     * @param filePath the path to the maze file
     * @throws FileNotFoundException if the filepath does not lead to an existing file
     */
    public Maze(String filePath) throws IOException {
        File maze = new File(filePath);

        // read the file into a List line by line
        BufferedReader reader = new BufferedReader( new FileReader(maze) );
        List<String> lines = reader.lines().collect( Collectors.toList() );

        grid = new char[ lines.size() ][ lines.get(0).length() ];

        for (int i = 0; i < grid.length; i++) {
            grid[i] = lines.get(i).toCharArray();
        }
    }

    public char[][] getGrid() {
        return grid;
    }

    // TODO: make methods to move the (N)PCs, currently holding off on this to make sure this works with the AI part
}
