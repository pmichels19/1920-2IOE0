package Main;

import Graphics.OpenGL.Texture;
import Levels.Assets.Items.Item;
import Levels.Assets.Tiles.GUIElements;
import Levels.Characters.Player;
import Levels.Framework.Maze;
import Levels.Framework.Point;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static Levels.Assets.Items.Item.getItemById;

/**
 * class that will manage the save slots made by players.
 *
 * This class can load in data from save slots into the player and maze object and
 * put data from the player and maze object, into the requested save slot
 */
public class SaveManager {

    private static final File[] SLOTS = new File[] {
            new File("Saves/Slot_0"),
            new File("Saves/Slot_1"),
            new File("Saves/Slot_2")
    };

    private static final Texture[] SAVE_TEXTURES = new Texture[3];

    /**
     * retrieves the latestSave screenshot as a texture
     *
     * @param slot the slot to get the texture from
     */
    public static Texture getTexture(int slot) {
        // if a texture was not yet made for the corresponding save slot, try to make a new one
        if (SAVE_TEXTURES[slot] == null) {
            Texture texture;
            // if the last save image exists, use it as a texture
            if ( new File( SLOTS[slot].getPath() + "/lastSave.png" ).isFile() ) {
                texture = new Texture( SLOTS[slot].getPath() + "/lastSave.png" );
            } else {
                // otherwise we just use the same white as used for the gui background
                texture = GUIElements.BACKGROUND.getTexture();
            }

            // fill the save texture
            SAVE_TEXTURES[slot] = texture;
        }

        return SAVE_TEXTURES[slot];
    }

    /**
     * removes all files from the specified save slot
     *
     * @param slot the slot to empty
     */
    public static void purgeSlot(int slot) {
        // before starting a new game, reset the player and the maze
        Player.resetPlayer();
        try {
            Main.getMaze().rebuildGrid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        checkExistingSaves();
        File[] save_data = SLOTS[slot].listFiles();
        for (File file : save_data) {
            if ( !file.delete() ) {
                // if the file could not be deleted, we have a problem
                throw new IllegalStateException("Could not purge save slot " + slot);
            }
        }

        // also reset the save texture
        SAVE_TEXTURES[slot] = null;
    }

    /**
     * saves the current maze state and the current player state to the requested save slot
     *
     * @param slot the slot to save to
     */
    public static void saveToSlot(int slot) {
        checkExistingSaves();
        // start a stringbuilder to write the save file
        StringBuilder stringBuilder = new StringBuilder();
        Player player = Player.getInstance();

        // start with the inventory
        Item[] inventory = player.getInventory();
        stringBuilder.append("# the inventory of the player (index:item_id)\n");
        for (int i = 0; i < inventory.length; i++) {
            // append the index i, together with the id of the item at index i
            stringBuilder
                    .append(i)
                    .append(":")
                    .append( inventory[i] == null ? "0" : inventory[i].getId() )
                    .append("\n");
        }

        // append the current health to max health ratio
        stringBuilder
                .append("# current health / max health\n")
                .append( player.getHealth() )
                .append("/")
                .append( player.getMaxHealth() )
                .append("\n");
        // do the same for the mana
        stringBuilder
                .append("# current mana / max mana\n")
                .append( player.getMana() )
                .append("/")
                .append( player.getMaxMana() )
                .append("\n");
        // and append the current player location
        Point playerLocation = Main.getMaze().getPlayerLocation();
        stringBuilder
                .append("# player location\n")
                .append( playerLocation.getX() )
                .append(":")
                .append( playerLocation.getY() )
                .append("\n");
        // then the collected items by the player
        List<Point> collectedItems = player.getCollected();
        stringBuilder
                .append("# collected item locations\n")
                .append(collectedItems.size())
                .append("\n");
        for (Point point : collectedItems) {
            stringBuilder
                    .append( point.getX() )
                    .append(" ")
                    .append( point.getY() )
                    .append("\n");
        }
        // finally the opened doors
        List<Point> opened_doors = player.getOpenedDoors();
        stringBuilder
                .append("# opened door locations\n")
                .append(opened_doors.size())
                .append("\n");
        for (Point point : opened_doors) {
            stringBuilder
                    .append( point.getX() )
                    .append(" ")
                    .append( point.getY() )
                    .append("\n");
        }

        // the player data is now ready for saving
        File data = new File(SLOTS[slot].getPath() + "/data.plr");
        try {
            // write the data gathered about the player into the data.plr file
            PrintWriter printer = new PrintWriter(data);
            printer.print( stringBuilder.toString() );
            printer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // get the path objects from the lastSave.png made when opening the pause menu
            Path screenshotLocation = (new File("Saves/lastSave.png")).toPath();
            // get the destination path, inside a save slot folder
            Path saveSlotLocation = (new File(SLOTS[slot] + "/lastSave.png")).toPath();
            // and then copy the lastSave.png into the slot
            Files.copy(
                    screenshotLocation,
                    saveSlotLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );
            // after updating, we need to update the texture for the save slot
            SAVE_TEXTURES[slot] = new Texture(SLOTS[slot] + "/lastSave.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method checks if the saveslot folders exits, if they dont, they are made
     */
    private static void checkExistingSaves() {
        // check if the Saves folder exists, if not: make one
        File savesFolder = new File("Saves");
        if ( !savesFolder.exists() ) {
            if ( !savesFolder.mkdir() ) {
                throw new IllegalStateException("Failed to create Saves directory");
            }
        }

        // also check the save slot folders:
        for ( File slot : SLOTS ) {
            if ( !slot.exists() ) {
                if ( !slot.mkdir() ) {
                    throw new IllegalStateException("Failed to create save slot directory");
                }
            }
        }
    }

    /**
     * loads the data from the requested slot into the player and maze object
     *
     * @param slot the slot to load data from
     * @return whether the load has been completed successfully
     */
    public static boolean loadFromSlot(int slot) {
        checkExistingSaves();
        if (slot < 0 || slot >= SLOTS.length) {
            return false;
        }

        File[] saveData = SLOTS[slot].listFiles();
        if (saveData == null || saveData.length == 0) {
            return false;
        }

        // get the data.plr file
        File player_data = null;
        for ( File file : saveData ) {
            if ( file.getName().equals("data.plr") ) {
                player_data = file;
            }
        }

        // check if the player_data was initialized, if not, we can not load
        if (player_data == null) {
            return false;
        }

        // load the data into the player
        return loadData( player_data );
    }

    /**
     * loads the data from the last modified save slot
     *
     * @return whether the latest slot was successfully loaded
     */
    public static boolean loadLatest() {
        // we start two variables to keep track of the latest used save slot
        int latestSlot = -1;
        long lastModified = -1;

        // we check, if there is save data in the slot, which save data was modified last
        File[] saveData;
        for (int i = 0; i < SLOTS.length; i++) {
            saveData = SLOTS[i].listFiles();
            // check if there exists save data in slot i
            if (saveData == null || saveData.length == 0) {
                continue;
            }

            // loop over the files in the save slot to find the player data
            for ( File file : saveData ) {
                if ( file.getName().equals("data.plr") ) {
                    // if this player data was modified later than the player data found until now, we replace the
                    // latestSlot and lastModified
                    if ( lastModified < file.lastModified() ) {
                        latestSlot = i;
                        lastModified = file.lastModified();
                    }
                }
            }
        }

        // now that the latest modified save slot has been found, we can load it into the player and maze object
        return loadFromSlot( latestSlot );
    }

    private static boolean loadData(File player_data) {
        // before loading in a new save, reset the player and the maze
        Player.resetPlayer();
        try {
            Main.getMaze().rebuildGrid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String line;
        String[] splitLine;
        Scanner scanner = null;
        Player player = Player.getInstance();
        try {
            scanner = new Scanner(player_data);
            scanner.useDelimiter("\\Z");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (scanner == null) {
            return false;
        }

        // the first line is a dummy line
        line = scanner.nextLine();

        // then we read the inventory
        Item[] newInventory = new Item[5];
        for (int i = 0; i < newInventory.length; i++) {
            line = scanner.nextLine();
            // remove the : between the indexex and the it ids
            splitLine = line.split(":");
            // fill the inventory
            newInventory[ Integer.parseInt( splitLine[0] ) ] = getItemById( Integer.parseInt( splitLine[1] ) );
        }
        player.setInventory( newInventory );

        // we then have another dummy line
        line = scanner.nextLine();

        // then we read the current health and max health
        line = scanner.nextLine();
        splitLine = line.split("/");
        player.setMaxHealth( Integer.parseInt( splitLine[1] ) );
        player.setHealth( Integer.parseInt( splitLine[0] ) );

        // and we do the same for the mana
        line = scanner.nextLine();

        // then we read the current health and max health
        line = scanner.nextLine();
        splitLine = line.split("/");
        player.setMaxMana( Integer.parseInt( splitLine[1] ) );
        player.setMana( Integer.parseInt( splitLine[0] ) );

        // we set the player location
        line = scanner.nextLine();  // a dummy line again
        line = scanner.nextLine();
        splitLine = line.split(":");
        Main.getMaze().setPlayerLocation(
                Integer.parseInt( splitLine[0] ),
                Integer.parseInt( splitLine[1] )
        );

        // finally set the collected items
        line = scanner.nextLine();  // dummy line
        // get the amount of collected items
        int collected = Integer.parseInt( scanner.nextLine().trim() );
        // retrieve the grid from the main class
        char[][] grid = Main.getMaze().getGrid();
        // fill the new CollectedItems list
        List<Point> collectedItems = new ArrayList<>();
        for (int i = 0; i < collected; i++) {
            splitLine = scanner.nextLine().split("\\s+");
            int x = Integer.parseInt( splitLine[0] );
            int y = Integer.parseInt( splitLine[1] );
            collectedItems.add( new Point(x, y) );
            // remove the items from the maze, as they have already been collected
            grid[x][y] = Maze.MARKER_SPACE;
        }
        // set the collected items
        player.setCollected(collectedItems);

        // finally set the collected items
        line = scanner.nextLine();  // dummy line
        // get the amount of collected items
        int opened = Integer.parseInt( scanner.nextLine().trim() );
        // fill the new CollectedItems list
        List<Point> opened_doors = new ArrayList<>();
        for (int i = 0; i < opened; i++) {
            splitLine = scanner.nextLine().split("\\s+");
            int x = Integer.parseInt( splitLine[0] );
            int y = Integer.parseInt( splitLine[1] );
            opened_doors.add( new Point(x, y) );
            // remove the items from the maze, as they have already been collected
            grid[grid.length - y][x] = Maze.MARKER_SPACE;
        }
        // set the collected items
        player.setOpenedDoors(opened_doors);

        return true;
    }
}