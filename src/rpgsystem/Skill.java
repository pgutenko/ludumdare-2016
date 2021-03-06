package rpgsystem;

import mote4.util.audio.AudioPlayback;
import rpgbattle.BattleManager;
import rpgbattle.PlayerSkills;
import rpgbattle.fighter.Fighter;
import rpgbattle.fighter.FighterStats;
import ui.BattleUIManager;
import ui.MenuHandler;
import ui.components.BattleAnimation;

/**
 * Special attack that can be used in battle.
 * @author Peter
 */
public enum Skill implements Pickupable {
    SKILL_PHYS(),
    SKILL_FIRE(),
    SKILL_FIRE2(),
    SKILL_ELEC(),
    SKILL_ICE(),
    SKILL_DARK(),
    SKILL_LIGHT(),
    SKILL_HEAL(),
    SKILL_POISON();

    public SkillData data;
    Skill() {
        this.data = new SkillData(this);
    }
    
    public boolean useIngame(MenuHandler handler) {
        switch (data.effect) {
            case HEAL:
                FighterStats stats = BattleManager.getPlayer().stats;
                if (stats.health == stats.maxHealth) {
                    handler.showDialogue("Your health is full.", data.spriteName);
                    AudioPlayback.playSfx("sfx_menu_invalid");
                } else if ((data.usesSP && data.cost() > stats.stamina) ||
                          (!data.usesSP && data.cost() > stats.mana)) {
                    if (data.usesSP)
                        handler.showDialogue("You don't have enough SP.", data.spriteName);
                    else
                        handler.showDialogue("You don't have enough MP.", data.spriteName);
                    AudioPlayback.playSfx("sfx_menu_invalid");
                } else {
                    if (data.usesSP)
                        BattleManager.getPlayer().drainStamina(data.cost());
                    else
                        BattleManager.getPlayer().drainMana(data.cost());
                    BattleManager.getPlayer().restoreHealth(data.power());
                    handler.showDialogue("You regain health.", data.spriteName);
                    AudioPlayback.playSfx("sfx_skill_heal");
                    return true;
                }
                return false;
            default:
                handler.showDialogue("You can't use this here.", data.spriteName);
                AudioPlayback.playSfx("sfx_menu_invalid");
                return false;
        }
    }
    public void useBattle(MenuHandler handler, int magicStat, Fighter... targets) {
        switch (data.effect) {
            case ATTACK:
                for (Fighter f : targets) {
                    boolean crit = Math.random() < (data.critRate()/100.0);
                    f.damage(data.element, magicStat, data.power(), data.accuracy(), crit);
                    f.addAnim(new BattleAnimation(data.animType));
                    AudioPlayback.playSfx(data.sfxName);
                }
                break;
            case ATTACK_DARK:
                for (Fighter f : targets) {
                    f.darkDamage(data.element, data.power()/100.0, data.accuracy());
                    f.addAnim(new BattleAnimation(data.animType));
                    AudioPlayback.playSfx(data.sfxName);
                }
                break;
            case ATTACK_LIGHT:
                for (Fighter f : targets) {
                    f.darkDamage(data.element, 1, data.accuracy());
                    f.addAnim(new BattleAnimation(data.animType));
                    AudioPlayback.playSfx(data.sfxName);
                }
                break;
            case HEAL:
                for (Fighter f : targets) {
                    f.restoreHealth(data.power());
                    f.addAnim(new BattleAnimation(data.animType));
                    AudioPlayback.playSfx(data.sfxName);
                }
                break;
            case POISON:
                for (Fighter f : targets) {
                    f.inflictStatus(StatusEffect.POISON, data.accuracy());
                    f.addAnim(new BattleAnimation(data.animType));
                }
                break;
            default:
                handler.showDialogue("You can't use this here.");
                AudioPlayback.playSfx("sfx_menu_invalid");
                break;
        }
    }

    @Override
    public String pickupName() { return data.name+" skill"; };
    @Override
    public String overworldSprite() { return data.spriteName; };
    @Override
    public void pickup() {
        PlayerSkills.addAvailableSkill(this);
    }
}