package com.rs.game.player.content.skills.magic;

import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.InterfaceOnObjectEvent;
import com.rs.plugin.handlers.InterfaceOnObjectHandler;

@PluginEventHandler
public class OrbImbuing {

	public static int UNPOWERED = 567;

	public enum Orbs {
		WATER(2151, 60, 56, 66, 571, 149, Rune.WATER), 
		EARTH(29415, 64, 60, 70, 575, 151, Rune.EARTH), 
		FIRE(2153, 71, 63, 73, 569, 152, Rune.FIRE), 
		AIR(2152, 74, 66, 76, 573, 150, Rune.AIR);

		private int objectId;
		private int componentId;
		private int req;
		private int xp;
		private int orbId;
		private int gfx;
		private Rune rune;

		private Orbs(int objectId, int componentId, int req, int xp, int orbId, int gfx, Rune rune) {
			this.objectId = objectId;
			this.componentId = componentId;
			this.req = req;
			this.xp = xp;
			this.orbId = orbId;
			this.gfx = gfx;
			this.rune = rune;
		}

		public int getObjectId() {
			return objectId;
		}

		public int getComponentId() {
			return componentId;
		}

		public int getReq() {
			return req;
		}

		public int getXp() {
			return xp;
		}

		public int getOrbId() {
			return orbId;
		}

		public int getGfx() {
			return gfx;
		}

		public Rune getRune() {
			return rune;
		}
	}

	static class OrbChargingAction extends Action {

		private Orbs orb;
		private WorldTile tile;

		public OrbChargingAction(Orbs orb, WorldTile tile) {
			this.orb = orb;
			this.tile = tile;
		}

		public boolean checkAll(Player player) {
			if (player.getSkills().getLevel(Constants.MAGIC) < orb.getReq()) {
				player.sendMessage("You need a magic level of " + orb.getReq() + " to imbue " + orb.name().toLowerCase() + " orbs.");
				return false;
			}
			return true;
		}

		@Override
		public boolean start(Player player) {
			if (!checkAll(player))
				return false;
			return true;
		}

		@Override
		public boolean process(Player player) {
			if (!checkAll(player))
				return false;
			return true;
		}

		@Override
		public int processWithDelay(Player player) {
			if (player.getInventory().containsItem(UNPOWERED, 1)) {
				if (player.getInventory().containsItem(564, 3) && Magic.checkRunes(player, true, new RuneSet(orb.getRune(), 30))) {
					player.getInventory().deleteItem(564, 3);
					player.getInventory().deleteItem(UNPOWERED, 1);
					player.getInventory().addItem(orb.getOrbId(), 1);
					player.getSkills().addXp(Constants.MAGIC, orb.getXp());
					player.setNextSpotAnim(new SpotAnim(orb.getGfx(), 0, 100));
					player.setNextAnimation(new Animation(726));
					player.setNextFaceWorldTile(tile);
				} else {
					player.sendMessage("You have run out of runes.");
					return -1;
				}
			} else {
				player.sendMessage("You've run out of orbs to imbue.");
				return -1;
			}
			return 3;
		}

		@Override
		public void stop(Player player) {

		}
	}
	
	public static InterfaceOnObjectHandler handle = new InterfaceOnObjectHandler(true, new int[] { 192 }, new int[] { 60, 64, 71, 74 }) {
		@Override
		public void handle(InterfaceOnObjectEvent e) {
			if (e.isAtObject()) {
				Orbs orb = null;
				for (Orbs o : Orbs.values()) {
					if (e.getObjectId() == o.getObjectId() && e.getComponentId() == o.getComponentId()) {
						orb = o;
						break;
					}
				}
				if (orb == null) {
					e.getPlayer().sendMessage("Try using this spell on the correct obelisk.");
					return;
				}
				e.getPlayer().getActionManager().setAction(new OrbChargingAction(orb, e.getObject()));
			}
		}
	};
}
