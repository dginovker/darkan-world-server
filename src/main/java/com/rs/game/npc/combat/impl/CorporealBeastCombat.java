package com.rs.game.npc.combat.impl;

import java.util.List;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.corp.CorporealBeast;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class CorporealBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 8133 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandomInclusive(3) == 0 && npc.getHitpoints() < (npc.getMaxHitpoints()/2)) {
			CorporealBeast beast = (CorporealBeast) npc;
			beast.spawnDarkEnergyCore();
		}
		int size = npc.getSize();
		final List<Entity> possibleTargets = npc.getPossibleTargets();
		int attackStyle = Utils.getRandomInclusive(4);
		if (attackStyle == 0 || attackStyle == 1) { // melee
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
				attackStyle = 2 + Utils.getRandomInclusive(2); // set mage
			else {
				npc.setNextAnimation(new Animation(attackStyle == 0 ? defs.getAttackEmote() : 10058));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
		}
		if (attackStyle == 2) { // powerfull mage spiky ball
			npc.setNextAnimation(new Animation(10410));
			delayHit(npc, World.sendProjectile(npc, target, 1825, 41, 16, 10, 1, 16, 0).getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, 650, AttackStyle.MAGE, target)));
		} else if (attackStyle == 3) { // translucent ball of energy
			npc.setNextAnimation(new Animation(10410));
			int delay = World.sendProjectile(npc, target, 1823, 41, 16, 10, 1, 16, 0).getTaskDelay();
			delayHit(npc, delay, target, getMagicHit(npc, getMaxHit(npc, 550, AttackStyle.MAGE, target)));
			if (target instanceof Player) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						int skill = Utils.getRandomInclusive(2);
						skill = skill == 0 ? Constants.MAGIC : (skill == 1 ? Constants.SUMMONING : Constants.PRAYER);
						Player player = (Player) target;
						if (skill == Constants.PRAYER)
							player.getPrayer().drainPrayer(10 + Utils.getRandomInclusive(40));
						else {
							int lvl = player.getSkills().getLevel(skill);
							lvl -= 1 + Utils.getRandomInclusive(4);
							player.getSkills().set(skill, lvl < 0 ? 0 : lvl);
						}
						player.sendMessage("Your " + Constants.SKILL_NAME[skill] + " has been slighly drained!");
					}

				}, delay);
			}
		} else if (attackStyle == 4) {
			npc.setNextAnimation(new Animation(10410));
			final WorldTile tile = new WorldTile(target);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					for (int i = 0; i < 6; i++) {
						final WorldTile newTile = new WorldTile(tile, 3);
						if (!World.floorAndWallsFree(newTile, 1))
							continue;
						for (Entity t : possibleTargets) {
							if (Utils.getDistance(newTile.getX(), newTile.getY(), t.getX(), t.getY()) > 1 || !t.lineOfSightTo(newTile, false))
								continue;
							delayHit(npc, 0, t, getMagicHit(npc, getMaxHit(npc, 350, AttackStyle.MAGE, t)));
						}
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								World.sendSpotAnim(npc, new SpotAnim(1806), newTile);
							}
						}, World.sendProjectile(tile, newTile, 1824, 0, 0, 0, 1, 30, 0).getTaskDelay());
					}
				}
			}, World.sendProjectile(npc, tile, 1824, 41, 16, 0, 1, 16, 0).getTaskDelay());
		}
		return npc.getAttackSpeed();
	}
}
