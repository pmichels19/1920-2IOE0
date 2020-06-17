package SpellCasting;

import Levels.Characters.Player;

public class SpellHeal extends Spell {

    private final int manaCost = 10;
    private final int lifeRestore = 10;

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
        int prevMana = player.getMana();

        if (prevMana < manaCost) {
            // Not enough mana to cast spell
            System.out.println("No Mana!");
        } else {
            player.setMana(prevMana - manaCost);
            int prevHP = player.getHealth();
            player.setHealth(prevHP + lifeRestore);
        }
    }

    @Override
    public void renderSpell() {

    }
}
