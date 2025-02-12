package com.rs.game.player.content.skills.dungeoneering;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.item.ItemsContainer;
import com.rs.game.object.GameObject;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.dialogues.DungeonDifficulty;
import com.rs.game.player.content.skills.dungeoneering.dialogues.DungeonLeaveParty;
import com.rs.game.player.content.skills.dungeoneering.dialogues.DungeonPartyStart;
import com.rs.game.player.content.skills.dungeoneering.dialogues.DungeonSize;
import com.rs.game.player.content.skills.dungeoneering.dialogues.PrestigeReset;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.controllers.DamonheimController;
import com.rs.game.player.controllers.DungeonController;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.player.managers.InterfaceManager.Tab;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DungManager {
	
	private static final int[] UPGRADE_COSTS = { 135, 175, 335, 660, 1360, 3400, 6800, 18750, 58600, 233000 };

	private int tokens;
	private boolean[] currentProgress;
	private int previousProgress;
	private int maxFloor;
	private int maxComplexity;
	private ItemsContainer<Item> bindedItems;
	private Item bindedAmmo;
	private boolean[] visitedResources;
	private int[] kinshipTiers;
	private int kinshipResets;
	private KinshipPerk activeRingPerk;
	private KinshipPerk quickSwitch;

	private Object rejoinKey;

	private transient Player player;
	private transient DungeonPartyManager party;
	private transient Player invitingPlayer;

	public static enum ResourceDungeon {
		EDGEVILLE_DUNGEON(10, 1100, 52849, new WorldTile(3132, 9933, 0), 52867, new WorldTile(991, 4585, 0)),
		DWARVEN_MINE(15, 1500, 52855, new WorldTile(3034, 9772, 0), 52864, new WorldTile(1041, 4575, 0)),
		EDGEVILLE_DUNGEON_2(20, 1600, 52853, new WorldTile(3104, 9826, 0), 52868, new WorldTile(1135, 4589, 0)),
		KARANJA_VOLCANO(25, 2100, 52850, new WorldTile(2845, 9557, 0), 52869, new WorldTile(1186, 4598, 0)),
		DAEMONHEIM_PENINSULA(30, 2400, 52861, new WorldTile(3513, 3666, 0), 52862, new WorldTile(3498, 3633, 0)),
		BAXTORIAN_FALLS(35, 3000, 52857, new WorldTile(2578, 9898, 0), 52873, new WorldTile(1256, 4592, 0)),
		MINING_GUILD(45, 4400, 52856, new WorldTile(3022, 9741, 0), 52866, new WorldTile(1052, 4521, 0)),
		TAVERLY_DUNGEON_1(55, 6200, 52851, new WorldTile(2854, 9841, 0), 52870, new WorldTile(1394, 4588, 0)),
		TAVERLY_DUNGEON_2(60, 7000, 52852, new WorldTile(2912, 9810, 0), 52865, new WorldTile(1000, 4522, 0)),
		VARRICK_SEWERS(65, 8500, 52863, new WorldTile(3164, 9878, 0), 52876, new WorldTile(1312, 4590, 0)),
		CHAOS_TUNNELS(70, 9600, 52858, new WorldTile(3160, 5521, 0), 52874, new WorldTile(1238, 4524, 0)),
		AL_KHARID(75, 11400, 52860, new WorldTile(3298, 3307, 0), 52872, new WorldTile(1182, 4515, 0)),
		BRIMHAVEM_DUNGEON(80, 12800, 52854, new WorldTile(2697, 9442, 0), 52871, new WorldTile(1140, 4499, 0)),
		POLYPORE_DUNGEON(82, 13500, 64291, new WorldTile(4661, 5491, 3), 64291, new WorldTile(4695, 5625, 3)),
		ASGARNIAN_ICE_DUNGEON(85, 15000, 52859, new WorldTile(3033, 9599, 0), 52875, new WorldTile(1297, 4510, 0))

		;

		private ResourceDungeon(int level, int xp) {
			outsideId = insideId = -1;
		}

		private int level, outsideId, insideId;
		private double xp;
		private WorldTile inside, outside;

		private ResourceDungeon(int level, double xp, int outsideId, WorldTile outside, int insideId,
				WorldTile inside) {
			this.level = level;
			this.xp = xp;
			this.outsideId = outsideId;
			this.outside = outside;
			this.insideId = insideId;
			this.inside = inside;
		}
	}

	public DungManager(Player player) {
		setPlayer(player);
		reset();
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (visitedResources == null) // temporary
			visitedResources = new boolean[ResourceDungeon.values().length];
	}
	
	public void setKinshipLevel(KinshipPerk perk, int level) {
		if (kinshipTiers == null) {
			kinshipTiers = new int[KinshipPerk.values().length];
			kinshipResets = 5;
		}
		kinshipTiers[perk.ordinal()] = level;
		refreshKinship();
	}
	
	public int getKinshipTier(KinshipPerk perk) {
		if (kinshipTiers == null) {
			kinshipTiers = new int[KinshipPerk.values().length];
			kinshipResets = 5;
		}
		return kinshipTiers[perk.ordinal()];
	}

	public static ButtonClickHandler handleKinshipInter = new ButtonClickHandler(993) {
		@Override
		public void handle(ButtonClickEvent e) {
			Item ring = e.getPlayer().getTempO("kinshipToBeCustomized");
			if (ring == null) {
				e.getPlayer().closeInterfaces();
				return;
			}
			switch(e.getComponentId()) {
			case 257:
			case 242:
			case 227:
			case 212:
				e.getPlayer().setTempI("kinshipTab", (257-e.getComponentId()) / 15);
				e.getPlayer().getDungManager().refreshKinshipStrings();
				break;
			case 139:
			case 46:
			case 88:
				e.getPlayer().getDungManager().promptRingUpgrade(getPerk(e.getPlayer().getTempI("kinshipTab"), e.getComponentId() == 139 ? 0 : e.getComponentId() == 46 ? 1 : 2));
				break;
			case 278:
				e.getPlayer().setTempO("perkUpgradePrompt", null);
				break;
			case 279:
				e.getPlayer().getDungManager().upgradePerk(e.getPlayer().getTempO("perkUpgradePrompt"));
				break;
			case 137:
			case 44:
			case 86:
				if (e.getPacket() == ClientPacket.IF_OP1)
					e.getPlayer().getDungManager().activeRingPerk = getPerk(e.getPlayer().getTempI("kinshipTab"), e.getComponentId() == 137 ? 0 : e.getComponentId() == 44 ? 1 : 2);
				else
					e.getPlayer().getDungManager().quickSwitch = getPerk(e.getPlayer().getTempI("kinshipTab"), e.getComponentId() == 137 ? 0 : e.getComponentId() == 44 ? 1 : 2);
				if (e.getPlayer().getDungManager().activeRingPerk != null)
					ring.setId(e.getPlayer().getDungManager().activeRingPerk.getItemId());
				e.getPlayer().getEquipment().refresh(Equipment.RING);
				e.getPlayer().getInventory().refresh();
				e.getPlayer().getDungManager().refreshKinshipStrings();
				break;
			case 190:
				e.getPlayer().getDungManager().promptResetRing();
				break;
			}
		}
	};

	private void upgradePerk(KinshipPerk perk) {
		if (perk == null)
			return;
		player.setTempO("perkUpgradePrompt", null);
		int cost = UPGRADE_COSTS[kinshipTiers[perk.ordinal()]];
		if (tokens < cost) {
			player.sendMessage("You don't have enough tokens to upgrade that.");
			return;
		}
		kinshipTiers[perk.ordinal()]++;
		tokens -= cost;
		refreshKinship();
	}

	private void promptRingUpgrade(KinshipPerk perk) {
		player.setTempO("perkUpgradePrompt", perk);
		String upgradeInfo = EnumDefinitions.getEnum(3089 + perk.ordinal()).getStringValue(kinshipTiers[perk.ordinal()]);
		upgradeInfo += "<br><br>";
		upgradeInfo += EnumDefinitions.getEnum(3089 + perk.ordinal()).getStringValue(kinshipTiers[perk.ordinal()]+1);
		player.getPackets().setIFText(993, 276, upgradeInfo);
		player.getPackets().setIFText(993, 277, "Upgrade cost: " + Utils.formatNumber(UPGRADE_COSTS[kinshipTiers[perk.ordinal()]]));
		player.getPackets().setIFText(993, 298, Utils.formatNumber(tokens));
		player.getPackets().setIFHidden(993, 261, false);
	}

	public void customizeKinship(Item ring) {
		player.setTempO("kinshipToBeCustomized", ring);
		player.setTempI("kinshipTab", 0);
		player.getInterfaceManager().sendInterface(993);
		refreshKinship();
	}
	
	private void promptResetRing() {
		if (kinshipResets <= 0) {
			player.sendMessage("You have no kinship resets left.");
			return;
		}
		player.sendOptionDialogue("Would you like to reset your ring? You have " + kinshipResets + " resets left.", new String[] { "Yes, reset my ring.", "Nevermind." }, new DialogueOptionEvent() {
			@Override
			public void run(Player player) {
				if (option == 1) {
					int tokensToRefund = 0;
					for (int i = 0;i < kinshipTiers.length;i++) {
						for (int lvl = 0;lvl <= kinshipTiers[i];lvl++) {
							if (lvl > 0)
								tokensToRefund += UPGRADE_COSTS[lvl-1];
						}
					}
					tokens += tokensToRefund;
					kinshipTiers = new int[KinshipPerk.values().length];
					kinshipResets--;
					refreshKinship();
					player.sendMessage("You reset the ring, and are refunded " + Utils.formatNumber(tokensToRefund) + " dungeoneering tokens.");
				}
			}
		});
	}
		
	public void refreshKinship() {
		for (KinshipPerk p : KinshipPerk.values())
			player.getVars().setVarBit(p.getVarbit(), getKinshipTier(p));
		player.getVars().setVarBit(8065, activeRingPerk == null ? 0 : activeRingPerk.ordinal()+1);
		if (player.getInterfaceManager().containsInterface(993))
			player.getPackets().sendRunScriptBlank(3494);
		refreshKinshipStrings();
	}
	
	public void refreshKinshipStrings() {
		player.getPackets().setIFText(993, 138, player.getDungManager().getStatus(getPerk(player.getTempI("kinshipTab"), 0)));
		player.getPackets().setIFText(993, 45, player.getDungManager().getStatus(getPerk(player.getTempI("kinshipTab"), 1)));
		player.getPackets().setIFText(993, 87, player.getDungManager().getStatus(getPerk(player.getTempI("kinshipTab"), 2)));
	}
	
	private String getStatus(KinshipPerk perk) {
		if (activeRingPerk == perk)
			return "In-Use";
		if (quickSwitch == perk)
			return "Quick-switch";
		return "Switch-to";
	}
	
	private static KinshipPerk getPerk(int page, int offset) {
		int idx = page * 3 + offset;
		if (idx < 0 || idx > 11)
			return null;
		return KinshipPerk.values()[idx];
	}
	
	public static ObjectClickHandler handleResourceDungeonEntrance = new ObjectClickHandler(new Object[] { "Mysterious entrance", "Mysterious door" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().getDungManager().enterResourceDungeon(e.getObject());
		}
	};
	
	public static ItemClickHandler handleKinship = new ItemClickHandler(Utils.streamObjects(15707, Utils.range(18817, 18828)), new String[] { "Customise", "Quick-switch", "Teleport to Daemonheim", "Open party interface" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Teleport to Daemonheim":
				Magic.sendDamonheimTeleport(e.getPlayer(), new WorldTile(3449, 3698, 0));
				break;
			case "Open party interface":
				e.getPlayer().getDungManager().openPartyInterface();
				break;
			case "Customise":
				if (e.getPlayer().getControllerManager().isIn(DungeonController.class))
					e.getPlayer().getDungManager().customizeKinship(e.getItem());
				else
					e.getPlayer().sendMessage("You cannot customize your ring outside of a dungeon.");
				break;
			case "Quick-switch":
				if (!e.getPlayer().getControllerManager().isIn(DungeonController.class))
					return;
				e.getPlayer().closeInterfaces();
				KinshipPerk active = e.getPlayer().getDungManager().activeRingPerk;
				KinshipPerk quickSwitch = e.getPlayer().getDungManager().quickSwitch;
				if (active != null && quickSwitch != null) {
					e.getPlayer().getDungManager().activeRingPerk = quickSwitch;
					e.getPlayer().getDungManager().quickSwitch = active;
					e.getItem().setId(quickSwitch.getItemId());
					if (e.isEquipped())
						e.getPlayer().getEquipment().refresh(Equipment.RING);
					else
						e.getPlayer().getInventory().refresh(e.getItem().getSlot());
				} else {
					e.getPlayer().sendMessage("You need to have an active perk set and a quick-switch to use this feature.");
				}
				break;
			}
		}
	};

	public boolean enterResourceDungeon(GameObject object) {
		int i = 0;
		for (ResourceDungeon dung : ResourceDungeon.values()) {
			if (object.getId() == dung.outsideId || object.getId() == dung.insideId) {
				if (player.getSkills().getLevelForXp(Constants.DUNGEONEERING) < dung.level) {
					player.getDialogueManager().execute(new SimpleMessage(), "You need a dungeoneering level of "
							+ dung.level + " to enter this dungeoneering resource.");
					return true;
				}
				if (dung == ResourceDungeon.POLYPORE_DUNGEON)
					Magic.sendTeleportSpell(player, 13288, 13285, 2516, 2517, 0, 0,
							object.getX() == 4695 && object.getY() == 5626 ? dung.outside : dung.inside, 1, false,
							Magic.OBJECT_TELEPORT);
				else
					Magic.sendTeleportSpell(player, 13288, 13285, 2516, 2517, 0, 0,
							object.getId() == dung.insideId ? dung.outside : dung.inside, 1, false,
							Magic.OBJECT_TELEPORT);
				if (!visitedResources[i]) {
					visitedResources[i] = true;
					player.getSkills().addXp(Constants.DUNGEONEERING, dung.xp);
				}
				return true;
			}
			i++;
		}
		return false;
	}

	public void addBindedItem(int itemId) {
		bindedItems.add(new Item(itemId));
	}

	public void setBindedAmmo(int itemId, int dg_bindedAmmoAmount) {
		if (bindedAmmo == null) {
			bindedAmmo = new Item(itemId);
		}
		bindedAmmo.setId(itemId);
		bindedAmmo.setAmount(dg_bindedAmmoAmount);
	}

	public void bind(Item item, int slot) {
		ItemDefinitions defs = item.getDefinitions();
		int bindId = DungeonUtils.getBindedId(item);
		if (bindId == -1)
			return;
		if (DungeonUtils.isBindAmmo(item)) {
			if (bindedAmmo != null && (!defs.isStackable() || bindedAmmo.getId() != bindId)) {
				player.sendMessage("A currently bound item must be destroyed before another item may be bound.");
				return;
			}
			player.getInventory().deleteItem(slot, item);
			item.setId(bindId);
			player.getInventory().addItem(item);
			if (bindedAmmo == null)
				bindedAmmo = new Item(item);
			else
				bindedAmmo.setAmount(bindedAmmo.getAmount() + item.getAmount());
			if (bindedAmmo.getAmount() > 255)
				bindedAmmo.setAmount(255);
		} else {
			if (bindedItems.getUsedSlots() >= DungeonUtils
					.getMaxBindItems(player.getSkills().getLevelForXp(Constants.DUNGEONEERING))) {
				player.sendMessage("A currently bound item must be destroyed before another item may be bound.");
				return;
			}
			item.setId(bindId);
			player.getInventory().refresh(slot);
			bindedItems.add(new Item(item));
		}
		player.sendMessage(
				"You bind the " + defs.getName() + " to you. Check in the smuggler to manage your bound items.");
	}

	public void unbind(Item item) {
		if (bindedAmmo != null && bindedAmmo.getId() == item.getId())
			bindedAmmo = null;
		else
			bindedItems.remove(item);
	}

	public Item getBindedAmmo() {
		return bindedAmmo;
	}

	public boolean isInside() {
		return party != null && party.getDungeon() != null;
	}

	public ItemsContainer<Item> getBindedItems() {
		return bindedItems;
	}

	public void reset() {
		currentProgress = new boolean[DungeonConstants.FLOORS_COUNT];
		previousProgress = 0;
		bindedItems = new ItemsContainer<Item>(10, false);
		maxFloor = maxComplexity = 1;
		visitedResources = new boolean[ResourceDungeon.values().length];
	}

	public boolean isTickedOff(int floor) {
		return currentProgress[floor - 1];
	}

	public int getCurrentProgress() {
		int count = 0;
		for (boolean b : currentProgress)
			if (b)
				count++;
		return count;
	}

	public boolean getVisitedResourcesB(int pos) {
		return visitedResources[pos];
	}

	public boolean getCurrentProgresB(int pos) {
		return currentProgress[pos];
	}

	public void setCurrentProgres(int pos, boolean b) {
		currentProgress[pos] = b;
	}

	public void setVisitedResource(int pos, boolean b) {
		visitedResources[pos] = b;
	}

	public int getPreviousProgress() {
		return previousProgress;
	}

	public void setPreviousProgress(int progress) {
		this.previousProgress = progress;
	}

	public int getPrestige() {
		return previousProgress;
	}

	public void tickOff(int floor) {
		currentProgress[floor - 1] = true;
		refreshCurrentProgress();
	}

	public void resetProgress() {
		previousProgress = getCurrentProgress();
		currentProgress = new boolean[DungeonConstants.FLOORS_COUNT];
		refreshCurrentProgress();
		refreshPreviousProgress();
	}

	public void removeTokens(int tokens) {
		this.tokens -= tokens;
		if (tokens < 0)
			tokens = 0;
	}

	public void addTokens(int tokens) {
		this.tokens += tokens;
	}

	public int getTokens() {
		return tokens;
	}

	public Object getRejoinKey() {
		return rejoinKey;
	}

	public void setRejoinKey(Object rejoinKey) {
		this.rejoinKey = rejoinKey;
	}

	public int getMaxFloor() {
		return maxFloor;
	}

	public void setMaxFloor(int maxFloor) {
		this.maxFloor = maxFloor;
	}

	public void increaseMaxFloor() {
		if (maxFloor == 60)
			return;
		maxFloor++;
	}

	public void increaseMaxComplexity() {
		maxComplexity++;
	}

	public int getMaxComplexity() {
		return maxComplexity;
	}

	public void setMaxComplexity(int maxComplexity) {
		this.maxComplexity = maxComplexity;
	}
	
	public static ButtonClickHandler handleDungTab = new ButtonClickHandler(939) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 59 && e.getComponentId() <= 72) {
				int playerIndex = (e.getComponentId() - 59) / 3;
				if ((e.getComponentId() & 0x3) != 0)
					e.getPlayer().getDungManager().pressOption(playerIndex,
							e.getPacket() == ClientPacket.IF_OP1 ? 0 : e.getPacket() == ClientPacket.IF_OP2 ? 1 : 2);
				else
					e.getPlayer().getDungManager().pressOption(playerIndex, 3);
			} else if (e.getComponentId() == 45)
				e.getPlayer().getDungManager().formParty();
			else if (e.getComponentId() == 33 || e.getComponentId() == 36)
				e.getPlayer().getDungManager().checkLeaveParty();
			else if (e.getComponentId() == 43)
				e.getPlayer().getDungManager().invite();
			else if (e.getComponentId() == 102)
				e.getPlayer().getDungManager().changeComplexity();
			else if (e.getComponentId() == 108)
				e.getPlayer().getDungManager().changeFloor();
			else if (e.getComponentId() == 87)
				e.getPlayer().getDungManager().openResetProgress();
			else if (e.getComponentId() == 94)
				e.getPlayer().getDungManager().switchGuideMode();
			else if (e.getComponentId() == 112) {
				e.getPlayer().getInterfaceManager().sendTab(Tab.QUEST);
			}
		}
	};
	
	public static ButtonClickHandler handleInviteScreen = new ButtonClickHandler(949) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 65)
				e.getPlayer().getDungManager().acceptInvite();
			else if (e.getComponentId() == 61 || e.getComponentId() == 63)
				e.getPlayer().closeInterfaces();
		}
	};
	
	public static ButtonClickHandler handleComplexitySelect = new ButtonClickHandler(938) {
		@Override
		public void handle(ButtonClickEvent e) {
			switch (e.getComponentId()) {
			case 39:
				e.getPlayer().getDungManager().confirmComplexity();
				break;
			case 56:
				e.getPlayer().getDungManager().selectComplexity(1);
				break;
			case 61:
				e.getPlayer().getDungManager().selectComplexity(2);
				break;
			case 66:
				e.getPlayer().getDungManager().selectComplexity(3);
				break;
			case 71:
				e.getPlayer().getDungManager().selectComplexity(4);
				break;
			case 76:
				e.getPlayer().getDungManager().selectComplexity(5);
				break;
			case 81:
				e.getPlayer().getDungManager().selectComplexity(6);
				break;
			}
		}
	};
	
	public static ButtonClickHandler handleFloorSelect = new ButtonClickHandler(947) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 48 && e.getComponentId() <= 107)
				e.getPlayer().getDungManager().selectFloor((e.getComponentId() - 48) + 1);
			else if (e.getComponentId() == 766)
				e.getPlayer().getDungManager().confirmFloor();
		}
	};

	public void openPartyInterface() {
		player.getInterfaceManager().sendTab(Tab.QUEST, 939);
		player.getInterfaceManager().openGameTab(Tab.QUEST);
		player.getPackets().sendVarc(234, 3);// Party Config Interface
		refresh();
	}

	public void refresh() {
		refreshFloor();
		refreshCurrentProgress();
		refreshPreviousProgress();
		refreshComplexity();
		refreshPartyDetailsComponents();
		refreshPartyGuideModeComponent();
		refreshNames();
	}

	public void refreshPartyGuideModeComponent() {
		if (!player.getInterfaceManager().containsInterface(939))
			return;
		player.getPackets().setIFHidden(939, 93, party == null || !party.getGuideMode());
	}

	/*
	 * called aswell when player added/removed to party
	 */
	public void refreshPartyDetailsComponents() {
		if (!player.getInterfaceManager().containsInterface(939))
			return;
		if (party != null) {
			if (party.isLeader(player)) { // is party leader stuff here
				player.getPackets().setIFHidden(939, 37, true);
				player.getPackets().setIFHidden(939, 31, false);
				player.getPackets().setIFHidden(939, 105, true);// Complexity change
				player.getPackets().setIFHidden(939, 111, true);// Floor change
				player.getPackets().setIFHidden(939, 38, false);
				player.getPackets().setIFHidden(939, 33, false);
			} else { // not party leader stuff here
				player.getPackets().setIFHidden(939, 37, true);
				player.getPackets().setIFHidden(939, 34, false);
				player.getPackets().setIFHidden(939, 31, true);
				player.getPackets().setIFHidden(939, 105, false);// Complexity change
				player.getPackets().setIFHidden(939, 111, false);// Floor change
				player.getPackets().setIFHidden(939, 38, true);
			}
		} else if (party == null) {
			player.getPackets().setIFHidden(939, 37, false);
			player.getPackets().setIFHidden(939, 34, true);
			player.getPackets().setIFHidden(939, 38, true);
			player.getPackets().setIFHidden(939, 36, false);
			player.getPackets().setIFHidden(939, 33, true);
			player.getPackets().setIFHidden(939, 105, false);// Complexity change
			player.getPackets().setIFHidden(939, 111, false);// Floor change
		}
	}

	public void pressOption(int playerIndex, int option) {
		player.stopAll();
		if (party == null || playerIndex >= party.getTeam().size())
			return;
		Player player = party.getTeam().get(playerIndex);
		if (player == null)
			return;
		DungeonManager dungeon = party.getDungeon();
		if (option == 0) {
			if (dungeon == null) {
				this.player.sendMessage("You must be in a dungeon to do that.");
				return;
			}
			if (player == this.player) {
				this.player.sendMessage("Why don't you just use your inventory and stat interfaces?");
				return;
			}
		} else if (option == 1) {
			if (player == this.player) {
				this.player.sendMessage("You can't kick yourself!");
				return;
			}
			if (!party.isLeader(this.player)) {
				this.player.sendMessage("Only your party's leader can kick a party member!");
				return;
			}
			if (player.isLocked() || dungeon != null && dungeon.isBossOpen()) {
				this.player.sendMessage("You can't kick this player right now.");
				return;
			}
			player.getDungManager().leaveParty();
		} else if (option == 2) {
			if (party.isLeader(player)) {
				this.player.sendMessage("You can't promote the party leader.");
				return;
			}
			if (!party.isLeader(this.player)) {
				this.player.sendMessage("Only your party's leader can promote a leader!");
				return;
			}
			party.setLeader(player);
			for (Player p2 : party.getTeam())
				party.refreshPartyDetails(p2);
		} else if (option == 3) {
			if (player != this.player) {
				this.player.sendMessage("You can't switch another player shared-xp.");
				return;
			}
			player.sendMessage("Shared xp is currently disabled.");
		}
	}

	public void invite() {
		if (party == null || !party.isLeader(player))
			return;
		player.stopAll();
		if (party.getDungeon() != null) {
			player.sendMessage("You can't do that right now.");
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendInputNameScript("Enter name:");
				player.getTemporaryAttributes().put("DUNGEON_INVITE", Boolean.TRUE);
			}
		});
	}

	public void acceptInvite() {
		Player invitedBy = (Player) player.getTemporaryAttributes().remove("DUNGEON_INVITED_BY");
		if (invitedBy == null)
			return;
		DungeonPartyManager party = invitedBy.getDungManager().getParty();
		if (invitedBy.getDungManager().invitingPlayer != player || party == null || !party.isLeader(invitedBy)) {
			player.closeInterfaces();
			player.sendMessage("You can't do that right now.");
			return;
		}
		if (party.getTeam().size() >= 5) {
			player.closeInterfaces();
			player.sendMessage("The party is full.");
			return;
		}
		if (party.getComplexity() > maxComplexity) {
			player.closeInterfaces();
			player.sendMessage("You can't do this complexity.");
			return;
		}
		if (party.getFloor() > maxFloor) {
			player.closeInterfaces();
			player.sendMessage("You can't do this floor.");
			return;
		}
		invitedBy.getDungManager().resetInvitation();
		invitedBy.getDungManager().getParty().setDificulty(0);
		invitedBy.getDungManager().getParty().add(player);
		player.stopAll();
		invitedBy.stopAll();
	}

	public void invite(String name) {
		player.stopAll();
		if (party == null) {
			final Player p2 = World.getPlayer(name);
			if (p2 == null) {
				player.sendMessage("Unable to find " + name);
				return;
			}
			DungeonPartyManager party = p2.getDungManager().getParty();
			if (p2.getDungManager().invitingPlayer != player || player.getPlane() != 0 || party == null
					|| !party.isLeader(p2)) {
				player.sendMessage("You can't do that right now.");
				return;
			}
			player.getTemporaryAttributes().put("DUNGEON_INVITED_BY", p2);
			player.getInterfaceManager().sendInterface(949);
			for (int i = 0; i < 5; i++) {
				Player teamMate = i >= party.getTeam().size() ? null : party.getTeam().get(i);
				player.getPackets().sendVarcString(284 + i, teamMate == null ? "" : teamMate.getDisplayName());
				player.getPackets().sendVarc(1153 + i,
						teamMate == null ? -1 : teamMate.getSkills().getCombatLevelWithSummoning());
				player.getPackets().sendVarc(1158 + i,
						teamMate == null ? -1 : teamMate.getSkills().getLevelForXp(Constants.DUNGEONEERING));
				player.getPackets().sendVarc(1163 + i,
						teamMate == null ? -1 : teamMate.getSkills().getHighestSkillLevel());
				player.getPackets().sendVarc(1168 + i, teamMate == null ? -1 : teamMate.getSkills().getTotalLevel());
			}
			player.getPackets().sendVarc(1173, party.getFloor());
			player.getPackets().sendVarc(1174, party.getComplexity());
			player.setCloseInterfacesEvent(new Runnable() {
				@Override
				public void run() {
					p2.getDungManager().expireInvitation();
					player.getTemporaryAttributes().remove("DUNGEON_INVITED_BY");
				}

			});
		} else {
			if (!party.isLeader(player) || party.getDungeon() != null) {
				player.sendMessage("You can't do that right now.");
				return;
			}
			if (party.getSize() >= 5) {
				player.sendMessage("Your party is full.");
				return;
			}
			Player p2 = World.getPlayer(name);
			if (p2 == null) {
				player.sendMessage("That player is offline, or has privacy mode enabled.");
				return;
			}
			if (!(p2.getControllerManager().getController() instanceof DamonheimController)) {
				player.sendMessage("You can only invite a player in or around Daemonheim.");
				return;
			}
			if (p2.getDungManager().party != null) {
				player.sendMessage(p2.getDisplayName() + " is already in a party.");
				return;
			}
			if (p2.getInterfaceManager().containsScreenInter() || p2.isCantTrade() || p2.isLocked()) {
				player.sendMessage("The other player is busy.");
				return;
			}
			expireInvitation();
			invitingPlayer = p2;
			player.sendMessage("Sending party invitation to " + p2.getDisplayName() + "...");
			p2.getPackets().sendDungeonneringRequestMessage(player);
		}

	}

	public void openResetProgress() {
		player.stopAll();
		player.getDialogueManager().execute(new PrestigeReset());
	}

	public void switchGuideMode() {
		if (party == null) {
			player.sendMessage("You must be in a party to do that.");
			return;
		}
		if (party.getDungeon() != null) {
			player.sendMessage("You cannot change the guide mode once the dungeon has started.");
			return;
		}
		if (!party.isLeader(this.player)) {
			this.player.sendMessage("Only your party's leader can switch guide mode!");
			return;
		}
		player.stopAll();
		party.setGuideMode(!party.getGuideMode());
		if (party.getGuideMode())
			player.sendMessage(
					"Guide mode enabled. Your map will show you the critical path, but you will receive an xp penalty.");
		else
			player.sendMessage("Guide mode disabled. Your map will no longer show the critical path.");
		for (Player p2 : party.getTeam())
			p2.getDungManager().refreshPartyGuideModeComponent();
	}

	public static int PLAYER_1_FLOORS = 608;
	public static int PLAYER_2_FLOORS = 486;
	public static int PLAYER_3_FLOORS = 364;
	public static int PLAYER_4_FLOORS = 242;
	public static int PLAYER_5_FLOORS = 120;
	public static int PLAYER_1_FLOORS_COMPLETE = 670;
	public static int PLAYER_2_FLOORS_COMPLETE = 548;
	public static int PLAYER_3_FLOORS_COMPLETE = 426;
	public static int PLAYER_4_FLOORS_COMPLETE = 304;
	public static int PLAYER_5_FLOORS_COMPLETE = 183;

	public void changeFloor() {
		if (party == null) {
			player.sendMessage("You must be in a party to do that.");
			return;
		}
		if (party.getDungeon() != null) {
			player.sendMessage("You cannot change these settings while in a dungeon.");
			return;
		}
		if (!party.isLeader(this.player)) {
			this.player.sendMessage("Only your party's leader can change floor!");
			return;
		}
		player.stopAll();
		player.getInterfaceManager().sendInterface(947);
		for (int i = 0; i < party.getMaxFloor(); i++)
			player.getPackets().setIFHidden(947, 48 + i, false);
		int highestFloor = 0;
		for (int index = party.getTeam().size() - 1; index >= 0; index--) {
			Player teamMate = party.getTeam().get(index);
			int startComponentCompleted = 0;
			int startComponentAvailable = 0;
			if (index == 0) {
				startComponentCompleted = PLAYER_1_FLOORS_COMPLETE;
				startComponentAvailable = PLAYER_1_FLOORS;
			} else if (index == 1) {
				startComponentCompleted = PLAYER_2_FLOORS_COMPLETE;
				startComponentAvailable = PLAYER_2_FLOORS;
			} else if (index == 2) {
				startComponentCompleted = PLAYER_3_FLOORS_COMPLETE;
				startComponentAvailable = PLAYER_3_FLOORS;
			} else if (index == 3) {
				startComponentCompleted = PLAYER_4_FLOORS_COMPLETE;
				startComponentAvailable = PLAYER_4_FLOORS;
			} else if (index == 4) {
				startComponentCompleted = PLAYER_5_FLOORS_COMPLETE;
				startComponentAvailable = PLAYER_5_FLOORS;
			}
			if (teamMate.getDungManager().getMaxFloor() > highestFloor)
				highestFloor = teamMate.getDungManager().getMaxFloor();
			for (int floor = 0; floor < teamMate.getDungManager().getMaxFloor(); floor++) {
				player.getPackets().setIFHidden(947, startComponentAvailable + floor, false);
				if (teamMate.getDungManager().currentProgress[floor])
					player.getPackets().setIFHidden(947, startComponentCompleted + floor - 1, false);
			}
			player.getPackets().setIFPosition(947, startComponentAvailable + 121, 3,
					teamMate.getDungManager().getMaxFloor() * 10);
		}
		player.getPackets().sendRunScriptReverse(3285, new Object[] { Math.min(highestFloor * 12 + 80, 700) });
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getTemporaryAttributes().remove("DUNG_FLOOR");
			}
		});
	}

	public void selectFloor(int floor) {
		if (party == null) {
			player.sendMessage("You must be in a party to do that.");
			return;
		}
		/*
		 * cant happen, cuz u cant click anyway but oh well
		 */
		if (party.getMaxFloor() < party.getMaxFloor()) {
			player.sendMessage("A member in your party can't do this floor.");
			return;
		}
		player.getPackets().setIFText(947, 765, "" + floor);
		player.getTemporaryAttributes().put("DUNG_FLOOR", floor);
	}

	public void confirmFloor() {
		Integer selectedFloor = (Integer) player.getTemporaryAttributes().remove("DUNG_FLOOR");
		player.stopAll();
		if (party == null) {
			player.sendMessage("You must be in a party to do that.");
			return;
		}
		if (selectedFloor == null)
			selectedFloor = party.getMaxFloor();
		if (party.getMaxFloor() < party.getMaxFloor()) {
			player.sendMessage("A member in your party can't do this floor.");
			return;
		}
		if (party.getDungeon() != null) {
			player.sendMessage("You cannot change these settings while in a dungeon.");
			return;
		}
		if (!party.isLeader(player)) {
			player.sendMessage("Only your party's leader can change floor!");
			return;
		}
		party.setFloor(selectedFloor);
	}

	public void changeComplexity() {
		if (party == null) {
			player.sendMessage("You must be in a party to do that.");
			return;
		}
		if (party.getDungeon() != null) {
			player.sendMessage("You cannot change these settings while in a dungeon.");
			return;
		}
		if (!party.isLeader(player)) {
			player.sendMessage("Only your party's leader can change complexity!");
			return;
		}
		player.stopAll();
		player.getInterfaceManager().sendInterface(938);
		selectComplexity(party.getMaxComplexity());
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getTemporaryAttributes().remove("DUNG_COMPLEXITY");
			}
		});
	}

	public void selectComplexity(int complexity) {
		if (party == null) {
			player.sendMessage("You must be in a party to do that.");
			return;
		}
		if (party.getMaxComplexity() < complexity) {
			player.sendMessage("A member in your party can't do this complexity.");
			return;
		}
		Integer selectedComplexity = (Integer) player.getTemporaryAttributes().remove("DUNG_COMPLEXITY");
		if (selectedComplexity != null) {
			markComplexity(selectedComplexity, false);
		}
		markComplexity(complexity, true);
		hideSkills(complexity);
		int penalty = complexity == 6 ? 0 : ((6 - complexity) * 5 + 25);
		player.getPackets().setIFText(938, 42, "" + complexity);
		player.getPackets().setIFText(938, 119, penalty + "% XP Penalty");
		player.getTemporaryAttributes().put("DUNG_COMPLEXITY", complexity);
	}

	public void confirmComplexity() {
		Integer selectedComplexity = (Integer) player.getTemporaryAttributes().remove("DUNG_COMPLEXITY");
		player.stopAll();
		if (selectedComplexity == null)
			return;
		if (party == null) {
			player.sendMessage("You must be in a party to do that.");
			return;
		}
		if (party.getMaxComplexity() < selectedComplexity) {
			player.sendMessage("A member in your party can't do this complexity.");
			return;
		}
		if (party.getDungeon() != null) {
			player.sendMessage("You cannot change these settings while in a dungeon.");
			return;
		}
		if (!party.isLeader(player)) {
			player.sendMessage("Only your party's leader can change complexity!");
			return;
		}
		party.setComplexity(selectedComplexity);
	}

	private void markComplexity(int complexity, boolean mark) {
		player.getPackets().setIFHidden(938, 57 + ((complexity - 1) * 5), !mark);
	}

	private static final String[] COMPLEXITY_SKILLS = { "Combat", "Cooking", "Firemaking", "Woodcutting", "Fishing",
			"Creating Weapons", "Mining", "Runecrafting", "Farming Textiles", "Hunting", "Creating Armour",
			"Farming Seeds", "Herblore", "Thieving", "Summoning", "Construction" };

	private void hideSkills(int complexity) {
		int count = 0;
		if (complexity >= 1)
			count += 1;
		if (complexity >= 2)
			count += 4;
		if (complexity >= 3)
			count += 3;
		if (complexity >= 4)
			count += 3;
		if (complexity >= 5)
			count += 5;
		if (complexity >= 6)
			count += 1;
		for (int i = 0; i < COMPLEXITY_SKILLS.length; i++) {
			player.getPackets().setIFText(938, 90 + i, (i >= count ? "<col=383838>" : "") + COMPLEXITY_SKILLS[i]);
		}
	}

	public void expireInvitation() {
		if (invitingPlayer == null)
			return;
		player.sendMessage("Your dungeon party invitation to " + invitingPlayer.getDisplayName() + " has expired.");
		invitingPlayer.sendMessage("A dungeon party invitation from " + player.getDisplayName() + " has expired.");
		invitingPlayer = null;
	}

	public void enterDungeon(boolean selectSize) {
		player.stopAll();
		expireInvitation();
		if (party == null) {
			player.getDialogueManager().execute(new DungeonPartyStart());
			return;
		}
		if (party.getDungeon() != null) // cant happen
			return;
		if (!party.isLeader(player)) {
			player.sendMessage("Only your party's leader can start a dungeon!");
			return;
		}
		if (party.getFloor() == 0) {
			changeFloor();
			return;
		}
		if (party.getComplexity() == 0) {
			changeComplexity();
			return;
		}
		if (party.getDificulty() == 0) {
			if (party.getTeam().size() == 1)
				party.setDificulty(1);
			else {
				player.getDialogueManager().execute(new DungeonDifficulty(), party.getTeam().size());
				return;
			}
		}
		if (selectSize) {
			if (party.getComplexity() == 6) {
				player.getDialogueManager().execute(new DungeonSize());
				return;
			} else
				party.setSize(DungeonConstants.SMALL_DUNGEON);
		}
		for (Player p2 : party.getTeam()) {
			for (Item item : p2.getInventory().getItems().getItems()) {
				if (item != null && item.getId() != 15707) {
					player.sendMessage(
							p2.getDisplayName() + " is carrying items that cannot be taken into Daemonheim.");
					return;
				}
			}
			for (Item item : p2.getEquipment().getItemsCopy()) {
				if (item != null && item.getId() != 15707) {
					player.sendMessage(
							p2.getDisplayName() + " is carrying items that cannot be taken into Daemonheim.");
					return;
				}
			}
			if (p2.getFamiliar() != null || p2.getPet() != null) {
				player.sendMessage(
						p2.getDisplayName() + " is carrying a familiar that cannot be taken into Daemonheim.");
				return;
			}
			if (p2.getPlane() != 0 || p2.getInterfaceManager().containsScreenInter() || p2.isLocked()
					|| !(p2.getControllerManager().getController() instanceof DamonheimController)) {
				player.sendMessage(p2.getDisplayName() + " is busy.");
				return;
			}
		}
		party.start();
	}

	public void setSize(int size) {
		if (party == null || !party.isLeader(player) || party.getComplexity() != 6)
			return;
		party.setSize(size);
	}

	public void setDificulty(int dificulty) {
		if (party == null || !party.isLeader(player))
			return;
		party.setDificulty(dificulty);
	}

	public void resetInvitation() {
		if (invitingPlayer == null)
			return;
		invitingPlayer = null;
	}

	public void refreshNames() {
		if (party == null) {
			player.getPackets().setIFHidden(939, 59, true);
			player.getPackets().setIFHidden(939, 62, true);
			player.getPackets().setIFHidden(939, 65, true);
			player.getPackets().setIFHidden(939, 68, true);
			player.getPackets().setIFHidden(939, 71, true);
			return;
		}
		int index = 0;
		int teamSize = party.getTeam().size();
		for (Player p2 : party.getTeam()) {
			player.getPackets().sendVarcString(292 + index++, p2.getDisplayName());
		}
		if (teamSize < 5) {
			if (teamSize == 4) {
				player.getPackets().setIFHidden(939, 71, true);
			} else if (teamSize == 3) {
				player.getPackets().setIFHidden(939, 65, true);
				player.getPackets().setIFHidden(939, 68, true);
			} else if (teamSize == 2) {
				player.getPackets().setIFHidden(939, 65, true);
				player.getPackets().setIFHidden(939, 68, true);
				player.getPackets().setIFHidden(939, 71, true);
			} else if (teamSize == 1) {
				player.getPackets().setIFHidden(939, 62, true);
				player.getPackets().setIFHidden(939, 65, true);
				player.getPackets().setIFHidden(939, 68, true);
				player.getPackets().setIFHidden(939, 71, true);
			}
		}
	}

	public void refreshFloor() {
		player.getPackets().sendVarc(1180, party == null ? 0 : party.getFloor());
	}

	public void refreshComplexity() {
		player.getPackets().sendVarc(1183, party == null ? 0 : party.getComplexity());
	}

	public void refreshCurrentProgress() {
		player.getPackets().sendVarc(1181, getCurrentProgress());
	}

	public void refreshPreviousProgress() {
		player.getPackets().sendVarc(1182, previousProgress);
	}

	public DungeonPartyManager getParty() {
		return party;
	}

	public void setParty(DungeonPartyManager party) {
		this.party = party;
	}

	public void formParty() {
		if (!player.getInterfaceManager().containsInterface(939))
			openPartyInterface();
		if (party != null)
			return;
		if (!(player.getControllerManager().getController() instanceof DamonheimController) || player.getPlane() != 0) {
			player.sendMessage("You can only form a party in or around Daemonheim.");
			return;
		}
		player.stopAll();
		new DungeonPartyManager().add(player);
		refreshFloor();
		refreshCurrentProgress();
		refreshPreviousProgress();
		refreshComplexity();
		refreshPartyDetailsComponents();
		refreshPartyGuideModeComponent();
		refreshNames();
	}

	public void finish() {
		if (party != null)
			party.leaveParty(player, true);
	}

	public void checkLeaveParty() {
		if (party == null)
			return;
		if (party.getDungeon() != null)
			player.getDialogueManager().execute(new DungeonLeaveParty());
		else
			leaveParty();
	}

	public void leaveParty() {
		if (party != null)
			party.leaveParty(player, false);
	}

	public KinshipPerk getActivePerk() {
		if (activeRingPerk == null || getKinshipTier(activeRingPerk) == 0)
			return null;
		return activeRingPerk;
	}
}
