package SpellCasting;

import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;

public class SpellFireball extends Spell {

    Vector3f loc;
    int dir;

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
        loc = player.getPosition();
        dir = player.getDirection();
        System.out.println(loc + ", " + dir);
    }

    @Override
    public void renderSpell() {

    }
}
