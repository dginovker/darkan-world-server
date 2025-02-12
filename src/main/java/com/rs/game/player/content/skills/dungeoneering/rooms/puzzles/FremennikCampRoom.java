package com.rs.game.player.content.skills.dungeoneering.rooms.puzzles;

import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.Fletching;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.player.content.skills.smithing.Smithing;
import com.rs.game.player.dialogues.FremennikScoutD;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class FremennikCampRoom extends PuzzleRoom {

	public static final int FREMENNIK_SCOUT = 11001;
	private static final int[] RAW_FISH =
	{ 49522, 49523, 49524, 49524, 49524 };
	private static final int[] COOKED_FISH =
	{ 49525, 49526, 49527, 49527, 49527 };
	private static final int[] BARS =
	{ 49528, 49529, 49530, 49530, 49530 };
	private static final int[] BATTLE_AXES =
	{ 49531, 49532, 49533, 49533, 49533 };
	private static final int[] LOGS =
	{ 49534, 49535, 49536, 49536, 49536 };
	private static final int[] BOWS =
	{ 49537, 49538, 49539, 49539, 49539 };

	private int stage = 0;

	@Override
	public void openRoom() {
		manager.spawnNPC(reference, FREMENNIK_SCOUT, 8, 5, false, DungeonConstants.NORMAL_NPC);
	}

	@Override
	public boolean processObjectClick1(Player player, GameObject object) {
		if (object.getId() == RAW_FISH[type]) {
			if (!hasRequirement(player, Constants.COOKING)) {
				player.sendMessage("You need a cooking level of " + getRequirement(Constants.COOKING) + " to cook these fish.");
				return false;
			}
			giveXP(player, Constants.COOKING);
			replaceObject(object, COOKED_FISH[type]);
			advance(player);
			player.setNextAnimation(new Animation(897));
			return false;
		} else if (object.getId() == BARS[type]) {
			if (!hasRequirement(player, Constants.SMITHING)) {
				player.sendMessage("You need a smithing level of " + getRequirement(Constants.SMITHING) + " to smith these battle axes.");
				return false;
			}
			if (!player.getInventory().containsOneItem(Smithing.DUNG_HAMMER)) {
				player.sendMessage("You need a hammer to smith battle axes.");
				return false;
			}
			giveXP(player, Constants.SMITHING);
			replaceObject(object, BATTLE_AXES[type]);
			advance(player);
			player.setNextAnimation(new Animation(898));
			player.setNextSpotAnim(new SpotAnim(2123));
			return false;
		} else if (object.getId() == LOGS[type]) {
			if (!hasRequirement(player, Constants.FLETCHING)) {
				player.sendMessage("You need a fletching level of " + getRequirement(Constants.FLETCHING) + " to fletch these bows.");
				return false;
			}
			if (!player.getInventory().containsOneItem(Fletching.DUNGEONEERING_KNIFE)) {
				player.sendMessage("You need a knife to fletch bows.");
				return false;
			}
			giveXP(player, Constants.FLETCHING);
			replaceObject(object, BOWS[type]);
			advance(player);
			player.setNextAnimation(new Animation(1248));
			return false;
		}
		return true;
	}

	public void advance(Player player) {
		if (++stage == 3) {
			setComplete();
			player.getDialogueManager().execute(new FremennikScoutD(), this);
		}
	}

	@Override
	public boolean processNPCClick1(Player player, NPC npc) {
		if (npc.getId() == FREMENNIK_SCOUT) {
			player.getDialogueManager().execute(new FremennikScoutD(), this);
			return false;
		}
		return true;
	}

	@Override
	public String getCompleteMessage() {
		return null;
	}

}
