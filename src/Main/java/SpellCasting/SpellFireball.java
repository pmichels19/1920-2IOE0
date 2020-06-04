package SpellCasting;

import Levels.Characters.Player;

public class SpellFireball extends Spell {
    @Override
    public void castSpell(Object[] args) {
        Player player = (Player) args[0];
    }

    @Override
    public void renderSpell() {

    }
}
