package Saves;

import Levels.Assets.Items.Item;
import Levels.Characters.Player;

import java.io.File;
import java.io.PrintWriter;

/**
 * class that will manage the save slots made by players.
 *
 * This class can load in data from save slots into the player and maze object and
 * put data from the player and maze object, into the requested save slot
 */
public class SaveManager {

    private static final File[] SLOTS = new File[] {
            new File("src/Main/Java/Saves/Slot_0"),
            new File("src/Main/Java/Saves/Slot_1"),
            new File("src/Main/Java/Saves/Slot_2")
    };

    /**
     * saves the current maze state and the current player state to the requested save slot
     *
     * @param slot the slot to save to
     */
    public void saveToSlot(int slot) {
        // start a stringbuilder to write the save file
        StringBuilder stringBuilder = new StringBuilder();
        Player player = Player.getInstance();

        // start with the inventory
        Item[] inventory = player.getInventory();
        stringBuilder.append("inventory: {\n");
        for (int i = 0; i < inventory.length; i++) {
            // append the index i, together with the id of the item at index i
            stringBuilder
                    .append("    ")
                    .append(i)
                    .append(":")
                    .append(inventory[i].getId())
                    .append("\n");
        }
        stringBuilder.append("}\n");

        // append the current health to max health ratio
        stringBuilder
                .append(player.getCurrentHealth())
                .append("/")
                .append(player.getMaxHealth())
                .append("\n");
        // do the same for the mana
        stringBuilder
                .append(player.getCurrentMana())
                .append("/")
                .append(player.getMaxMana())
                .append("\n");

        // the player data is now ready for saving
        File data = new File(SLOTS[slot].getPath() + "data.plr");
        try {
            PrintWriter printer = new PrintWriter(data);
            printer.print( stringBuilder.toString() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * loads the data from the requested slot into the player and maze object
     *
     * @param slot the slot to load data from
     * @return whether the load has been completed successfully
     */
    public boolean loadFromSlot(int slot) {
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

        // load the data into the player and return true afterwards
        Player player = Player.getInstance();
        player.loadData( player_data );
        return true;
    }

    /**
     * loads the data from the last modified save slot
     *
     * @return whether the latest slot was successfully loaded
     */
    public boolean loadLatest() {
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
}
