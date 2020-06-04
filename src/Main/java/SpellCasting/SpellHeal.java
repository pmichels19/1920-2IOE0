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

        int prevMana = player.getCurrentMana();

        if (prevMana < manaCost) {
            // Not enough mana to cast spell
            System.out.println("No Mana!");
        } else {
            player.setCurrentMana(prevMana - manaCost);
            int prevHP = player.getCurrentHealth();
            player.setCurrentHealth(prevHP + lifeRestore);
            if (prevHP + lifeRestore >= maxHP) {
                player.setCurrentHealth(maxHP);
            }
        }
    }

    @Override
    public void renderSpell() {

    }
}
