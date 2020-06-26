package SpellCasting;

import Graphics.IO.Timer;
import Levels.Characters.Player;

public abstract class Spell {

    int duration;
    double castMoment;


    public Spell(int duration) {
        this.duration = duration;
    }

    // game logic for all spells
    public abstract void castSpell(Object[] args);

    // graphics for all spells
    public abstract void renderSpell();

    // make spell with string returned from google VisionML
    public static Spell determineSpell(String spell) {
        Spell ret;
        System.out.println(spell);
        switch (spell) {
            case "illuminate":
                ret = new SpellIlluminate();
                break;
            case "fireball":
                ret = new SpellFireball();
                break;
            case "heal":
                ret = new SpellHeal();
                break;
            case "agility":
                ret = new SpellAgility();
                break;
            case "unlock":
                ret = new SpellUnlock();
                break;
            case "cloak":
                ret = new SpellCloak();
                break;
            case "shield":
                ret = new SpellShield();
                break;
            case "scan":
                ret = new SpellScan();
                break;
            case "guide":
                ret = new SpellGuide();
                break;
            case "summon":
                ret = new SpellBeast();
                break;
            case "tp_self":
                ret = new SpellTPSelf();
                break;
            case "tp_npc":
                ret = new SpellTPNPC();
                break;
            case "lightning":
                ret = new SpellLightning();
                break;
            default:
                // to cancel spell casting
                ret = new SpellEmpty();
                break;
        }
        return ret;
    }

    public boolean checkDuration() {
        if (Timer.getTime() - castMoment > duration) {
            Player.getInstance().setAgilityPower(0);
            return true;
        }
        return false;
    }
}
