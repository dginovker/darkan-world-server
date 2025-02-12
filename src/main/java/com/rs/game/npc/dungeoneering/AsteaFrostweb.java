package com.rs.game.npc.dungeoneering;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public final class AsteaFrostweb extends DungeonBoss {

	private int meleeNPCId;
	private int switchPrayersDelay;
	private int spawnedSpiders;
	private NPC[] spiders;

	public AsteaFrostweb(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(9965, 10021, 3), manager.getBossLevel()), tile, manager, reference);
		spiders = new NPC[6];
		setHitpoints(getMaxHitpoints());
		meleeNPCId = this.getId();
		resetSwitchPrayersDelay();
	}

	public void resetSwitchPrayersDelay() {
		switchPrayersDelay = 35; // 25sec
	}

	public void switchPrayers() {
		setNextNPCTransformation(getId() == meleeNPCId + 2 ? meleeNPCId : getId() + 1);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		if (switchPrayersDelay > 0)
			switchPrayersDelay--;
		else {
			switchPrayers();
			resetSwitchPrayersDelay();
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		for (NPC minion : spiders) {
			if (minion == null)
				continue;
			minion.sendDeath(this);
		}
	}

	// max 6 spiders per fight
	public void spawnSpider() {
		if (spawnedSpiders >= spiders.length)
			return;
		// spawnedSpiders
		for (int tryI = 0; tryI < 10; tryI++) {
			WorldTile tile = new WorldTile(this, 2);
			if (World.floorAndWallsFree(tile, 1)) {
				NPC spider = spiders[spawnedSpiders++] = new NPC(64, tile, true);
				spider.setForceAgressive(true);
				break;
			}
		}
	}

	@Override
	public void handlePreHit(final Hit hit) {
		super.handlePreHit(hit);
		if (getId() == meleeNPCId) {
			if (hit.getLook() == HitLook.MELEE_DAMAGE)
				hit.setDamage(0);
		} else if (getId() == meleeNPCId + 1) {
			if (hit.getLook() == HitLook.MAGIC_DAMAGE)
				hit.setDamage(0);
		} else if (hit.getLook() == HitLook.RANGE_DAMAGE)
			hit.setDamage(0);
	}

}
