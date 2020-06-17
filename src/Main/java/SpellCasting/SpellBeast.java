package SpellCasting;

import Levels.Characters.Player;

import java.util.Timer;

public class SpellBeast extends Spell {

    Player player = Player.getInstance();
    Timer timer = new Timer();

    private int prevMana;
    private int manaCost = 10;

    public SpellBeast() {
        super(10);
    }

    @Override
    public void castSpell(Object[] args) {

    }

    @Override
    public void renderSpell() {

    }
}
