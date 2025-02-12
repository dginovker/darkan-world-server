package com.rs.game.player.content.world.regions.dungeons;

import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.managers.EmotesManager.Emote;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class StrongholdOfSecurity {
	
	public static ItemClickHandler handleSkullSceptreTele = new ItemClickHandler(new Object[] { 9013 }, new String[] { "Invoke" }) {
		@Override
		public void handle(ItemClickEvent e) {
			Magic.sendTeleportSpell(e.getPlayer(), 9601, -1, 1683, -1, 0, 0, new WorldTile(3080, 3424, 0), 4, true, Magic.MAGIC_TELEPORT);
		}
	};
	
	public static ObjectClickHandler handleEntrance = new ObjectClickHandler(new Object[] { 16154 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(1859, 5243, 0));
		}
	};
	
	public static ObjectClickHandler handleExitLadders = new ObjectClickHandler(new Object[] { 16148, 16080, 16078, 16112, 16049, 16048 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(3081, 3421, 0));
		}
	};
	
	public static ObjectClickHandler handleF1Shortcut = new ObjectClickHandler(new Object[] { 16150 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getEmotesManager().unlockedEmote(Emote.FLAP))
				e.getPlayer().setNextWorldTile(new WorldTile(1907, 5221, 0));
			else
				e.getPlayer().sendMessage("The portal does not allow you to enter yet.");
		}
	};
	
	public static ObjectClickHandler handleF1DownLadder = new ObjectClickHandler(new Object[] { 16149 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2042, 5245, 0));
		}
	};
	
	public static ObjectClickHandler handleF2Shortcut = new ObjectClickHandler(new Object[] { 16082 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getEmotesManager().unlockedEmote(Emote.SLAP_HEAD))
				e.getPlayer().setNextWorldTile(new WorldTile(2022, 5214, 0));
			else
				e.getPlayer().sendMessage("The portal does not allow you to enter yet.");
		}
	};
	
	public static ObjectClickHandler handleF2DownLadder = new ObjectClickHandler(new Object[] { 16081 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2123, 5252, 0));
		}
	};
	
	public static ObjectClickHandler handleF3Shortcut = new ObjectClickHandler(new Object[] { 16116 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getEmotesManager().unlockedEmote(Emote.IDEA))
				e.getPlayer().setNextWorldTile(new WorldTile(2144, 5279, 0));
			else
				e.getPlayer().sendMessage("The portal does not allow you to enter yet.");
		}
	};
	
	public static ObjectClickHandler handleF3DownLadder = new ObjectClickHandler(new Object[] { 16115 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2358, 5215, 0));
		}
	};
	
	public static ObjectClickHandler handleF4Shortcut = new ObjectClickHandler(new Object[] { 16050 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getEmotesManager().unlockedEmote(Emote.STOMP))
				e.getPlayer().setNextWorldTile(new WorldTile(2344, 5213, 0));
			else
				e.getPlayer().sendMessage("The portal does not allow you to enter yet.");
		}
	};
	
	public static ObjectClickHandler handleGates = new ObjectClickHandler(new Object[] { 16123, 16124, 16065, 16066, 16089, 16090, 16043, 16044 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().lock(3);
			e.getPlayer().setNextAnimation(new Animation(4282));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					WorldTile tile;
					switch (e.getObject().getRotation()) {
					case 0:
						tile = new WorldTile(e.getObject().getX() == e.getPlayer().getX() ? e.getObject().getX() - 1 : e.getObject().getX(), e.getPlayer().getY(), 0);
						break;
					case 1:
						tile = new WorldTile(e.getPlayer().getX(), e.getObject().getY() == e.getPlayer().getY() ? e.getObject().getY() + 1 : e.getObject().getY(), 0);
						break;
					case 2:
						tile = new WorldTile(e.getObject().getX() == e.getPlayer().getX() ? e.getObject().getX() + 1 : e.getObject().getX(), e.getPlayer().getY(), 0);
						break;
					case 3:
					default:
						tile = new WorldTile(e.getPlayer().getX(), e.getObject().getY() == e.getPlayer().getY() ? e.getObject().getY() - 1 : e.getObject().getY(), 0);
						break;
					}
					e.getPlayer().setNextWorldTile(tile);
					e.getPlayer().setNextAnimation(new Animation(4283));
					e.getPlayer().faceObject(e.getObject());
				}
			}, 0);
		}
	};

}
