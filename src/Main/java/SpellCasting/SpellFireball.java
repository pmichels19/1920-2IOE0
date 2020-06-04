package SpellCasting;

import Levels.Characters.Player;

public class SpellFireball extends Spell {
<<<<<<< Updated upstream

    @Override
    public void cast() {

    }

=======
    @Override
    public void castSpell(Object[] args) {
        Player player = (Player) args[0];
    }

    @Override
    public void renderSpell() {

    }
>>>>>>> Stashed changes
}
