package SpellCasting;

public abstract class Spell {

    public Spell() {

    }

    // game logic for all spells
    public abstract void castSpell(Object[] args);

    // graphics for all spells
    public abstract void renderSpell();

    // make spell with string returned from google VisionML
    public static Spell determineSpell(String spell) {
        Spell ret;
        switch (spell) {
            case "illuminate":
                ret = new SpellIlluminate();
                break;
            case "fireball":
                ret = new SpellFireball();
                break;
            case "HEAL":
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
            case "beast":
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
                // to cancel spellcasting
                ret = new SpellEmpty();
                break;
        }
        return ret;
    }
}
