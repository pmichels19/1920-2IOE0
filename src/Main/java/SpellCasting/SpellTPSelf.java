package SpellCasting;

import Graphics.Rendering.World;
import Levels.Characters.Player;
import Levels.Framework.Maze;
import Levels.Framework.joml.Vector3f;
import Main.Main;

import java.util.Random;

public class SpellTPSelf extends Spell {

    private final int manaCost = 10;

    private int newX;
    private int newY;

    Random rand = new Random();

    public SpellTPSelf() {
        super(0);
    }

    public void castSpell(Object[] args) {
        Maze maze = Main.getMaze();
        World world = Main.getWorld();
        Player player = Player.getInstance();

        int prevMana = player.getMana();

        if (prevMana < manaCost) {
            // Not enough mana to cast spell
            System.out.println("No Mana!");
        } else {
            // Use mana to cast spell
            player.setMana(prevMana - manaCost);

            char[][] grid = maze.getGrid();

            char newPosition = Maze.MARKER_WALL;

            // As long as the new positions is not an empty space, find a new location
            while ( newPosition != Maze.MARKER_SPACE) {
                // find a position to teleport the player to
                newPosition = getNewPositions(grid);
            }
            // teleport the player to the position
            maze.setPlayerLocation(newX, newY);
            player.setPosition(new Vector3f(newX, newY, 1.5f));
            //player.setGridPosition(newX, newY, grid.length);
            world.resetCameraPosition();

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
