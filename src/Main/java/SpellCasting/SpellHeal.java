package SpellCasting;

import Levels.Characters.Player;

public class SpellHeal extends Spell {

    Player player = Player.getInstance();

    private final int manaCost = 10;
    private final int lifeRestore = 10;
    private final int maxHP = player.getMaxHealth();
    private int newHP;

    @Override
    public void castSpell(Object[] args) {

        int prevMana = player.getMana();

        if (prevMana < manaCost) {
            // Not enough mana to cast spell
            System.out.println("No Mana!");
        } else {
            player.setMana(prevMana - manaCost);
            int prevHP = player.getHealth();
            player.setHealth(prevHP + lifeRestore);
            if (prevHP + lifeRestore >= maxHP) {
                player.setHealth(maxHP);
            }
        }
    }

    @Override
    public void renderSpell() {

    }
}
