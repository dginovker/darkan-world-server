package com.rs.game.player.content.skills.prayer;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.skills.prayer.Burying.Bone;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;

@PluginEventHandler
public class BoneAltar  {
	
	public static final int ANIM = 3705;
	public static final int GFX = 624;
	
	public enum Altar {
		OAK(13179, 2.0f),
		TEAK(13182, 2.1f),
		CLOTH(13185, 2.25f),
		MAHOGANY(13188, 2.5f),
		LIMESTONE(13191, 2.75f),
		MARBLE(13194, 3.0f),
		GILDED(13197, 3.5f);
		
		int objectId;
		float xpMul;
		
		private Altar(int objectId, float xpMul) {
			this.objectId = objectId;
			this.xpMul = xpMul;
		}
		
		public int getObjectId() {
			return objectId;
		}
		
		public float getXpMul() {
			return xpMul;
		}
	}
	
	static class BoneAction extends Action {
		
		private Altar altar;
		private Bone bone;
		private GameObject object;
		
		public BoneAction(Altar altar, Bone bone, GameObject object) {
			this.altar = altar;
			this.bone = bone;
			this.object = object;
		}
		
		@Override
		public boolean start(Player player) {
			if (object != null && player.getInventory().containsItem(bone.getId(), 1))
				return true;
			else
				return false;
		}

		@Override
		public boolean process(Player player) {
			if (player.getInventory().containsItem(bone.getId(), 1) && object != null)
				return true;
			else
				return false;
		}

		@Override
		public int processWithDelay(Player player) {
			if (player.getInventory().containsItem(bone.getId(), 1)) {
				player.incrementCount(ItemDefinitions.getDefs(bone.getId()).getName()+" offered at altar");
				player.getInventory().deleteItem(bone.getId(), 1);
				player.getSkills().addXp(Constants.PRAYER, bone.getExperience()*altar.getXpMul());
				player.setNextAnimation(new Animation(ANIM));
				World.sendSpotAnim(null, new SpotAnim(GFX), object);
			}
			return 2;
		}

		@Override
		public void stop(Player player) {
			
		}
		
	}
	
	public static ItemOnObjectHandler handleBonesOnAltar = new ItemOnObjectHandler(new Object[] { 13179, 13182, 13185, 13188, 13191, 13194, 13197 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			Altar altar = null;
			Bone bone = null;
			for (Altar altars : Altar.values()) {
				if (altars.getObjectId() == e.getObject().getId()) {
					altar = altars;
					break;
				}
			}
			for (Bone bones : Bone.values()) {
				if (bones.getId() == e.getItem().getId()) {
					bone = bones;
					break;
				}
			}
			e.getPlayer().getActionManager().setAction(new BoneAction(altar, bone, e.getObject()));
		}
	};

}
