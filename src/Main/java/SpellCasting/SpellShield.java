package SpellCasting;

import Levels.Characters.Player;

public class SpellShield extends Spell {
    public SpellShield() {
        super(60);
    }

    private int prevHealth;
    private int prevMana;
    private int manaCost = 15;
    private int shieldValue = 15;

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
        prevMana = player.getMana();
        prevHealth = player.getHealth();

        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            player.setMana(prevMana - manaCost);
            player.setHealth(prevHealth + shieldValue);
        }

    }

    @Override
    public void renderSpell() {

    }
}
