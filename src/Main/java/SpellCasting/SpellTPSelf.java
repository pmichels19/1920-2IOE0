package SpellCasting;

import Levels.Characters.Player;
import Levels.Framework.Maze;

import java.util.Random;

public class SpellTPSelf extends Spell {

    private int manaCost = 10;
    private int newX = 0;
    private int newY = 0;

    Maze maze;
    Random rand = new Random();

    Player player;

    public void castSpell(Object[] args) {

        int prevMana = player.getCurrentMana();

        if (prevMana < manaCost) {
            // Spell will not be cast
        } else {
            // Use mana to cast spell
            player.setCurrentMana(player.getMaxMana() - manaCost);

            char[][] grid = maze.getGrid();

            // find a position to teleport the player to
            while ( grid[newX][newY] != ' ') {
                grid[newX][newY] = getNewPositions(grid);
            }
            // teleport the player to the position
            maze.setPlayerLocation(newX, newY);
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
        return rand.nextInt(grid.length - 1);
    }

    private int getNewY(char[][] grid, int newX) {
        return rand.nextInt(grid[newX].length - 1);
    }
}
