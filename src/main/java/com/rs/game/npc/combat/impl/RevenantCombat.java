package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class RevenantCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13465, 13466, 13467, 13468, 13469, 13470, 13471, 13472, 13473, 13474, 13475, 13476, 13477, 13478, 13479, 13480, 13481 };
	}

	public int getMagicAnimation(NPC npc) {
		switch (npc.getId()) {
		case 13465:
			return 7500;
		case 13466:
		case 13467:
		case 13468:
		case 13469:
			return 7499;
		case 13470:
		case 13471:
			return 7506;
		case 13472:
			return 7503;
		case 13473:
			return 7507;
		case 13474:
			return 7496;
		case 13475:
			return 7497;
		case 13476:
			return 7515;
		case 13477:
			return 7498;
		case 13478:
			return 7505;
		case 13479:
			return 7515;
		case 13480:
			return 7508;
		case 13481:
		default:
			// melee emote, better than 0
			return npc.getCombatDefinitions().getAttackEmote();
		}
	}

	public int getRangeAnimation(NPC npc) {
		switch (npc.getId()) {
		case 13465:
			return 7501;
		case 13466:
		case 13467:
		case 13468:
		case 13469:
			return 7513;
		case 13470:
		case 13471:
			return 7519;
		case 13472:
			return 7516;
		case 13473:
			return 7520;
		case 13474:
			return 7521;
		case 13475:
			return 7510;
		case 13476:
			return 7501;
		case 13477:
			return 7512;
		case 13478:
			return 7518;
		case 13479:
			return 7514;
		case 13480:
			return 7522;
		case 13481:
		default:
			// melee emote, better than 0
			return npc.getCombatDefinitions().getAttackEmote();
		}
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (npc.getHitpoints() < npc.getMaxHitpoints() / 2 && Utils.random(5) == 0)
			npc.heal(100);

		int attackStyle = Utils.random(3);
		if (attackStyle == 2) { // checks if can melee
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			if ((distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1))
				attackStyle = Utils.random(2);
		}
		if (attackStyle != 2 && target instanceof Player) {
			Player targetPlayer = (Player) target;
			targetPlayer.getPackets().sendSound(202, 0, 1);
		}
		switch (attackStyle) {
		case 0: // magic
			int damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target);
			if (target instanceof Player) {
				if (((Player) target).isRevenantImmune()) {
					damage = 0;
				}
			}
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			World.sendProjectile(npc, target, 1276, 34, 16, 30, 35, 16, 0);
			if (damage > 0) {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						target.setNextSpotAnim(new SpotAnim(1277, 0, 100));
						if (Utils.random(5) == 0) {
							target.setNextSpotAnim(new SpotAnim(363));
							target.freeze(Ticks.fromSeconds(5));
						}
					}

				}, 2);
			}
			npc.setNextAnimation(new Animation(getMagicAnimation(npc)));
			break;
		case 1: // range
			int damage2 = getMaxHit(npc, defs.getMaxHit(), AttackStyle.RANGE, target);
			if (target instanceof Player) {
				if (((Player) target).isRevenantImmune()) {
					damage2 = 0;
				}
			}
			delayHit(npc, 2, target, getRangeHit(npc, damage2));
			World.sendProjectile(npc, target, 1278, 34, 16, 30, 35, 16, 0);
			npc.setNextAnimation(new Animation(getRangeAnimation(npc)));
			break;
		case 2: // melee
			int damage3 = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target);
			if (target instanceof Player) {
				if (((Player) target).isRevenantImmune()) {
					damage3 = 0;
				}
			}
			delayHit(npc, 0, target, getMeleeHit(npc, damage3));
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			break;
		}
		return npc.getAttackSpeed();
	}
}
