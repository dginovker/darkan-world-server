package com.rs.game.player.content;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.game.player.Player;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class Lamps {

	public static final int LAMP_SMALL = 0;
	public static final int LAMP_MEDIUM = 1;
	public static final int LAMP_BIG = 2;
	public static final int LAMP_HUGE = 3;

	public static final int[] SELECTABLE_XP_LAMPS = new int[] { 23713, 23714, 23715, 23716, 12628, 18782, 20960 };
	public static final int[] SELECTABLE_XP_LAMPS_TYPES = new int[] { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE, LAMP_SMALL, LAMP_SMALL, LAMP_SMALL };
	public static final int[][] SKILL_LAMPS = new int[][] { { 23717, 23718, 23719, 23720 }, { 23725, 23726, 23727, 23728 }, { 23721, 23722, 23723, 23724 }, { 23753, 23754, 23755, 23756 }, { 23729, 23730, 23731, 23732 },
			{ 23737, 23738, 23739, 23740 }, { 23733, 23734, 23735, 23736 }, { 23798, 23799, 23800, 23801 }, { 23806, 23807, 23808, 23809 }, { 23774, 23775, 23776, 23777 }, { 23794, 23795, 23796, 23797 }, { 23802, 23803, 23804, 23805 },
			{ 23769, 23770, 23771, 23773 }, { 23790, 23791, 23792, 23793 }, { 23786, 23787, 23788, 23789 }, { 23761, 23762, 23763, 23764 }, { 23757, 23758, 23759, 23760 }, { 23765, 23766, 23767, 23768 }, { 23778, 23779, 23780, 23781 },
			{ 23810, 23811, 23812, 23813 }, { 23741, 23742, 23743, 23744 }, { 23782, 23783, 23784, 23785 }, { 23745, 23746, 23747, 23748 }, { 23814, 23815, 23816, 23817 }, { 23749, 23750, 23751, 23752 } };
	public static final int[][] SKILL_LAMPS_TYPES = new int[][] { { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE },
			{ LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE },
			{ LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE },
			{ LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE },
			{ LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE },
			{ LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE },
			{ LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, { LAMP_SMALL, LAMP_MEDIUM, LAMP_BIG, LAMP_HUGE }, };

	public static final int[] OTHER_LAMPS = new int[] {}; // for future

	private static final int[] DIALOGUE_INTERFACE_C2S = new int[] { Constants.ATTACK, Constants.MAGIC, Constants.MINING, Constants.WOODCUTTING, Constants.AGILITY, Constants.FLETCHING, Constants.THIEVING, Constants.STRENGTH, Constants.RANGE, Constants.SMITHING, Constants.FIREMAKING,
			Constants.HERBLORE, Constants.SLAYER, Constants.CONSTRUCTION, Constants.DEFENSE, Constants.PRAYER, Constants.FISHING, Constants.CRAFTING, Constants.FARMING, Constants.HUNTER, Constants.SUMMONING, Constants.HITPOINTS, Constants.DUNGEONEERING, Constants.COOKING,
			Constants.RUNECRAFTING };

	private static final double[] BASE_LAMPS_XP = new double[] { 62.5, 69, 77, 85, 94, 104, 115, 127, 139, 154, 170, 188, 206, 229, 252, 262, 274, 285, 298, 310, 325, 337, 352, 367.5, 384, 399, 405, 414, 453, 473, 514, 528, 536, 551, 583, 609, 635,
			662, 692, 721, 752, 785, 818, 854, 890, 929, 971, 1013, 1055, 1101, 1149, 1200, 1250, 1305, 1362, 1422, 1485, 1542, 1617, 1685, 1758, 1836, 1912, 2004.5, 2085, 2172, 2269, 2379, 2471, 2593, 2693, 2810, 2947, 3082, 3214, 3339, 3496, 3648,
			3793, 3980, 4166, 4348, 4522, 4762, 4919, 5150, 5376, 5593, 5923, 6122, 6452, 6615, 6929, 7236, 7533, 8065, 8348, 8602 };

	public static void processLampClick(Player player, int slot, int id) {
		if (isSelectable(id)) {
			openSelectableInterface(player, slot, id);
		} else if (isSkillLamp(id)) {
			openSkillDialog(player, slot, id);
		} else if (isOtherLamp(id)) {
			// for future
		}
	}
	
	private static void sendSelectedSkill(Player player) {
		if (player.getTemporaryAttributes().get("lampInstance") == null) {
			return;
		}
		Lamp lamp = (Lamp) player.getTemporaryAttributes().get("lampInstance");
		EnumDefinitions map = EnumDefinitions.getEnum(681);
		if (lamp.getSelectedSkill() == map.getDefaultIntValue()) {
			player.getPackets().sendVarc(1796, map.getDefaultIntValue());
			return;
		}

		long key = map.getKeyForValue(lamp.getSelectedSkill());
		player.getPackets().sendVarc(1796, (int) key);
	}
	
	public static void openSelectableInterface(Player player, int slot, int id) {
		player.getDialogueManager().execute(new Dialogue() {
			Lamp lamp = new Lamp(id, slot, 1);
			
			@Override
			public void start() {
				if (id == 12628)
					lamp.setXp(500);
				player.getTemporaryAttributes().put("lampInstance", lamp);
				player.getVars().setVar(738, 1); //has house
				player.getVars().setVarBit(2187, 1);
				player.getInterfaceManager().sendInterface(1263);
				player.getPackets().sendVarcString(358, "What skill would you like XP in?");
				sendSelectedSkill(player);
				player.getPackets().sendVarc(1797, 0);
				player.getPackets().sendVarc(1798, lamp.getReq()); //level required to use lamp
				player.getPackets().sendVarc(1799, id);
				for (int i = 13; i < 38; i++)
					player.getPackets().setIFRightClickOps(1263, i, -1, 0, 0);
				player.getPackets().setIFTargetParams(new IFTargetParams(1263, 39, 1, 26).enableContinueButton());
			}

			@Override
			public void run(int interfaceId, int componentId) {
				if (componentId == 39) {
					if (!player.getInventory().containsItem(lamp.getId(), 1)) {
						player.getTemporaryAttributes().remove("lampInstance");
						player.closeInterfaces();
						return;
					}
	
					player.getInventory().deleteItem(slot, new Item(lamp.getId(), 1));
					double xpAmt = lamp.getXp() != 0 ? lamp.getXp() : getExp(player.getSkills().getLevelForXp(lamp.getSelectedSkill()), selectableLampType(lamp.getId()));
					if (lamp.getId() == 18782) {
						int lvl = player.getSkills().getLevelForXp(lamp.getSelectedSkill());
						if (lvl < 30) {
							xpAmt = (EnumDefinitions.getEnum(716).getIntValueAtIndex(lvl) - EnumDefinitions.getEnum(716).getIntValueAtIndex(lvl-1));
						} else {
							xpAmt = (Math.pow(lvl, 3) - 2 * Math.pow(lvl, 2) + 100 * lvl) / 20.0;
						}
					} else if (lamp.getId() == 20960) {
						int lvl = player.getSkills().getLevelForXp(lamp.getSelectedSkill());
						xpAmt = (lvl*lvl) - (2*lvl) + 100;
					}
					double exp = player.getSkills().addXpLamp(lamp.getSelectedSkill(), xpAmt);
					player.closeInterfaces();
					player.sendMessage("You have been awarded " + Utils.getFormattedNumber(exp, ',') + " XP in " + Constants.SKILL_NAME[lamp.getSelectedSkill()] + "!");
					player.getTemporaryAttributes().remove("lampInstance");
				} else {
					player.getTemporaryAttributes().remove("lampInstance");
					player.closeInterfaces();
				}
			}

			@Override
			public void finish() {
				
			}
			
		});
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getTemporaryAttributes().remove("lampInstance");
			}
		});
	}
	
	public static ButtonClickHandler handleButtons = new ButtonClickHandler(1263) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getTemporaryAttributes().get("lampInstance") == null) {
				e.getPlayer().closeInterfaces();
				return;
			}
			Lamp lamp = (Lamp) e.getPlayer().getTemporaryAttributes().get("lampInstance");
			if (e.getComponentId() >= 13 && e.getComponentId() <= 37) {
				int skill = DIALOGUE_INTERFACE_C2S[e.getComponentId() - 13];
				lamp.setSelectedSkill(skill);
				sendSelectedSkill(e.getPlayer());
			}
		}
	};

	private static void openSkillDialog(Player player, final int slot, final int id) {
		final int type = skillLampType(id);
		final int skillId = skillLampSkillId(id);

		player.getDialogueManager().execute(new Dialogue() {
			@Override
			public void start() {
				sendOptionsDialogue("Rub Lamp?", "Gain <col=ff0000>" + Constants.SKILL_NAME[skillId] + "</col> experience", "Cancel");
			}

			@Override
			public void run(int interfaceId, int componentId) {
				if (componentId != Dialogue.OPTION_1 || !player.getInventory().containsItem(id, 1)) {
					end();
					return;
				}

				player.getInventory().deleteItem(slot, new Item(id, 1));
				double exp = player.getSkills().addXpLamp(skillId, getExp(player.getSkills().getLevelForXp(skillId), type));
				sendDialogue("<col=0000ff>Your wish has been granted!</col>", "You have been awarded " + Utils.getFormattedNumber(exp, ',') + " XP in " + Constants.SKILL_NAME[skillId] + "!");
			}

			@Override
			public void finish() {
			}

		});
	}

	private static double getExp(int skillLevel, int lampType) {
		double xp;
		if (skillLevel <= BASE_LAMPS_XP.length)
			xp = BASE_LAMPS_XP[skillLevel - 1];
		else
			xp = BASE_LAMPS_XP[BASE_LAMPS_XP.length - 1];

		for (int i = 0; i < lampType; i++)
			xp *= 2D;
		return xp;
	}

	private static int selectableLampType(int id) {
		for (int i = 0; i < SELECTABLE_XP_LAMPS.length; i++) {
			if (SELECTABLE_XP_LAMPS[i] == id)
				return SELECTABLE_XP_LAMPS_TYPES[i];
		}
		return -1;
	}

	public static boolean isSelectable(int id) {
		for (int i = 0; i < SELECTABLE_XP_LAMPS.length; i++) {
			if (SELECTABLE_XP_LAMPS[i] == id)
				return true;
		}
		return false;
	}

	private static int skillLampType(int id) {
		for (int skillId = 0; skillId < SKILL_LAMPS.length; skillId++) {
			for (int i = 0; i < SKILL_LAMPS[skillId].length; i++) {
				if (SKILL_LAMPS[skillId][i] == id)
					return SKILL_LAMPS_TYPES[skillId][i];
			}
		}
		return -1;
	}

	private static int skillLampSkillId(int id) {
		for (int skillId = 0; skillId < SKILL_LAMPS.length; skillId++) {
			for (int i = 0; i < SKILL_LAMPS[skillId].length; i++) {
				if (SKILL_LAMPS[skillId][i] == id)
					return skillId;
			}
		}
		return -1;
	}

	public static boolean isSkillLamp(int id) {
		for (int skillId = 0; skillId < SKILL_LAMPS.length; skillId++) {
			for (int i = 0; i < SKILL_LAMPS[skillId].length; i++) {
				if (SKILL_LAMPS[skillId][i] == id)
					return true;
			}
		}
		return false;
	}

	public static boolean isOtherLamp(int id) {
		for (int i = 0; i < OTHER_LAMPS.length; i++) {
			if (OTHER_LAMPS[i] == id)
				return true;
		}
		return false;
	}

}
