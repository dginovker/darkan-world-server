package com.rs.game.player.content.world.regions;

import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class PollnivneachSlayerDungeon {
	
	public static ObjectClickHandler handleBarriers = new ObjectClickHandler(new Object[] { 31435, 31436 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextSpotAnim(new SpotAnim(1659));
			if (e.getObjectId() == 31435)
				AgilityShortcuts.forceMovementInstant(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() > e.getObject().getX() ? -2 : 2, 0), 10584, 1);
			else
				AgilityShortcuts.forceMovementInstant(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() > e.getObject().getY() ? -2 : 2), 10584, 1);
		}
	};
	
	public static ObjectClickHandler handleDownStairsNE = new ObjectClickHandler(new Object[] { 31412 }, new WorldTile(3374, 9426, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(-52, -5061));
		}
	};
	
	public static ObjectClickHandler handleUpStairsNE = new ObjectClickHandler(new Object[] { 31417 }, new WorldTile(3317, 4364, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(52, 5061));
		}
	};
	
	public static ObjectClickHandler handleDownStairsSE = new ObjectClickHandler(new Object[] { 31412 }, new WorldTile(3377, 9367, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(-54, -5028));
		}
	};
	
	public static ObjectClickHandler handleUpStairsSE = new ObjectClickHandler(new Object[] { 31417 }, new WorldTile(3318, 4339, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(54, 5028));
		}
	};
	
	public static ObjectClickHandler handleDownStairsSW = new ObjectClickHandler(new Object[] { 31412 }, new WorldTile(3338, 9368, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(-72, -5029));
		}
	};
	
	public static ObjectClickHandler handleUpStairsSW = new ObjectClickHandler(new Object[] { 31417 }, new WorldTile(3271, 4339, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(72, 5029));
		}
	};
	
	public static ObjectClickHandler handleDownStairsNW = new ObjectClickHandler(new Object[] { 31412 }, new WorldTile(3340, 9426, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(-68, -5059));
		}
	};
	
	public static ObjectClickHandler handleUpStairsNW = new ObjectClickHandler(new Object[] { 31417 }, new WorldTile(3277, 4367, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(68, 5059));
		}
	};
}
