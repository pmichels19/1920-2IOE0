package SpellCasting;

import Levels.Characters.Player;
import Levels.Framework.Maze;
import Levels.Framework.joml.Vector3f;

import java.util.Random;

public class SpellTPSelf extends Spell {

    private final int manaCost = 10;

    private int newX = 0;
    private int newY = 0;

    Random rand = new Random();

    Player player = Player.getInstance();
    Maze maze;

    public void castSpell(Object[] args) {

        maze = (Maze) args[0];

        int prevMana = player.getCurrentMana();

        if (prevMana < manaCost) {
            // Not enough mana to cast spell
            System.out.println("No Mana!");
        } else {
            // Use mana to cast spell
            player.setCurrentMana(prevMana - manaCost);

            char[][] grid = maze.getGrid();

            // find a position to teleport the player to
            while ( grid[newX][newY] != ' ') {
                grid[newX][newY] = getNewPositions(grid);
            }
            // teleport the player to the position
            maze.setPlayerLocation(newX, newY);
            player.setPosition(new Vector3f (newX, newY, 1.5f));
            // TODO: move the model and the camera of the player in the maze, rather than just the player object
        }
    }
    
    @Override
    public void renderSpell() {
        
    }

    private char getNewPositions(char[][] grid) {
        newX = getNewX(grid);
        newY = getNewY(grid, newX);

        return grid[newX][newY];
    }

    private int getNewX(char[][] grid) {
        return rand.nextInt(grid.length);
    }

    private int getNewY(char[][] grid, int newX) {
        return rand.nextInt(grid[newX].length);
    }
}
