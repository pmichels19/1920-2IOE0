package SpellCasting;

import Levels.Characters.Player;

import static Main.Input.InGameController.tryDoorInteraction;

public class SpellUnlock extends Spell {

    private int prevMana;
    private int manaCost = 10;

    public SpellUnlock() {
        super(0);
    }

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana");
        } else {
            player.setMana(prevMana - manaCost);
            tryDoorInteraction();
        }
    }

    @Override
    public void renderSpell() {

    }
}
