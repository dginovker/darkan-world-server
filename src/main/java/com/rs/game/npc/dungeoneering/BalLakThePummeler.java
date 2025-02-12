package com.rs.game.npc.dungeoneering;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class BalLakThePummeler extends DungeonBoss {

	private boolean skip;
	private int barPercentage;

	private List<PoisionPuddle> puddles = new CopyOnWriteArrayList<PoisionPuddle>();

	public BalLakThePummeler(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(10128, 10141), manager.getBossLevel()), tile, manager, reference);
		setLureDelay(6000); //this way you can lure him hehe, still not as much as outside dung npcs
		setHitpoints(getMaxHitpoints());
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		super.processNPC();
		skip = !skip;
		if (!skip) {
			boolean reduced = false;
			for (PoisionPuddle puddle : puddles) {
				puddle.cycles++;
				if (puddle.canDestroyPoision()) {
					puddles.remove(puddle);
					continue;
				}
				List<Entity> targets = getPossibleTargets(true);
				if (WorldUtil.collides(getX(), getY(), getSize(), puddle.tile.getX(), puddle.tile.getY(), 1)) {
					barPercentage = barPercentage > 1 ? barPercentage - 2 : 0;
					sendDefenceBar();
					reduced = true;
				}
				for (Entity t : targets) {
					if (!t.withinDistance(puddle.tile, 2))
						continue;
					t.applyHit(new Hit(this, (int) Utils.random((int) (t.getHitpoints() * 0.25)) + 1, HitLook.TRUE_DAMAGE));
				}
			}
			if (!reduced) {
				if (!isUnderCombat()) {
					if (barPercentage > 0) {
						barPercentage--;
						sendDefenceBar();
					}
				} else {
					if (barPercentage < 100) {
						barPercentage++;
						sendDefenceBar();
					}
				}
			}
		}
	}

	@Override
	public void processHit(Hit hit) {
		int damage = hit.getDamage();
		HitLook look = hit.getLook();
		if (damage > 0) {
			if (look == HitLook.MELEE_DAMAGE || look == HitLook.RANGE_DAMAGE || look == HitLook.MAGIC_DAMAGE) {
				double multiplier = (100D - ((double) barPercentage)) / 100D;
				hit.setDamage((int) (damage * multiplier));
			}
		}
		super.processHit(hit);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		puddles.clear();
		sendDefenceBar();
	}

	private void sendDefenceBar() {
		if (isDead())
			getManager().hideBar(getReference());
		else
			getManager().showBar(getReference(), "Demon's Defence", barPercentage);
	}

	private static class PoisionPuddle {
		final WorldTile tile;
		int cycles;

		public PoisionPuddle(WorldTile tile, int barPercentage) {
			this.tile = tile;
		}

		public boolean canDestroyPoision() {
			return cycles == 15;
		}
	}

	public void addPoisionBubble(WorldTile tile) {
		puddles.add(new PoisionPuddle(tile, barPercentage));
		World.sendSpotAnim(this, new SpotAnim(2588), tile);
	}

	public List<PoisionPuddle> getPoisionPuddles() {
		return puddles;
	}
}
