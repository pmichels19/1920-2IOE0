package SpellCasting;

import Levels.Characters.Player;
import Main.Input.InGameController;

public class SpellUnlock extends Spell {
    int manaCost = 10;

    public SpellUnlock() {
        super(0);
    }

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
        int prevMana = player.getMana();

        if (prevMana < manaCost) {
            // Not enough mana to cast spell
            System.out.println("No Mana!");
        } else {
            // Use mana to cast spell
            player.setMana(prevMana - manaCost);
            InGameController.tryDoorInteraction(true);
        }
    }

    @Override
    public void renderSpell() {

    }
}
