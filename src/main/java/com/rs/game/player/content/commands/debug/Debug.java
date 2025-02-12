package com.rs.game.player.content.commands.debug;

import java.util.Arrays;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.commands.Commands;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.EnterChunkHandler;

@PluginEventHandler
public class Debug {
	
	public static EnterChunkHandler handleTempleChunks = new EnterChunkHandler() {
		@Override
		public void handle(EnterChunkEvent e) {
			if (!Settings.getConfig().isDebug())
				return;
			if (e.getEntity() instanceof Player) {
				Player player = (Player) e.getEntity();
				if (player.getTempB("visChunks") && player.hasStarted()) {
					player.devisualizeChunk(e.getEntity().getLastChunkId());
					player.visualizeChunk(e.getChunkId());
					player.sendMessage("Chunk: " + e.getChunkId());
				}
			}
		}
	};
	
	public static ButtonClickHandler debugButtons = new ButtonClickHandler() {
		@Override
		public boolean handleGlobal(ButtonClickEvent e) {
			if (Settings.getConfig().isDebug())
				System.out.println(e.getInterfaceId() + ", " + e.getComponentId() + ", " + e.getSlotId() + ", " + e.getSlotId2());
			return false;
		}
		public void handle(ButtonClickEvent e) { }
	};

	@ServerStartupEvent
	public static void startup() {
		if (!Settings.getConfig().isDebug())
			return;
		
//		Commands.add(Rights.PLAYER, "example [arg1 (optionalArg2)]", "This is an example command to replicate.", (p, args) -> {
//			
//		});
		
		Commands.add(Rights.PLAYER, "coords,getpos,mypos,pos,loc", "Gets the coordinates for the tile.", (p, args) -> {
			p.sendMessage("Coords: " + p.getX() + "," + p.getY() + "," + p.getPlane() + ", regionId: " + p.getRegionId() + ", chunkX: " + p.getChunkX() + ", chunkY: " + p.getChunkY());
			p.sendMessage("JagCoords: " + p.getPlane() + ","+p.getRegionX()+","+p.getRegionY()+","+p.getXInScene(p.getSceneBaseChunkId())+","+p.getYInScene(p.getSceneBaseChunkId()));
		});
		
		Commands.add(Rights.PLAYER, "search,si,itemid [item name]", "Searches for items containing the words searched.", (p, args) -> {
			p.getPackets().sendPanelBoxMessage("Searching for items containing: " + Arrays.toString(args));
			for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
				boolean contains = true;
				for (int idx = 0; idx < args.length; idx++) {
					if (!ItemDefinitions.getDefs(i).getName().toLowerCase().contains(args[idx].toLowerCase()) || ItemDefinitions.getDefs(i).isLended()) {
						contains = false;
						continue;
					}
				}
				if (contains)
					p.getPackets().sendPanelBoxMessage("Result found: " + i + " - " + ItemDefinitions.getDefs(i).getName() + " " + (ItemDefinitions.getDefs(i).isNoted() ? "(noted)" : "") + "" + (ItemDefinitions.getDefs(i).isLended() ? "(lent)" : ""));
			}
		});
		
		Commands.add(Rights.PLAYER, "showhitchance", "Toggles the display of your hit chance when attacking opponents.", (p, args) -> {
			p.setTempB("hitChance", p.getTempB("hitChance"));
			p.sendMessage("Hit chance display: " + p.getTempB("hitChance"));
		});
		
		Commands.add(Rights.PLAYER, "item,spawn [itemId (amount)]", "Spawns an item with specified id and amount.", (p, args) -> {
			if (ItemDefinitions.getDefs(Integer.valueOf(args[0])).getName().equals("null")) {
				p.sendMessage("That item is unused.");
				return;
			}
			p.getInventory().addItem(Integer.valueOf(args[0]), args.length >= 2 ? Integer.valueOf(args[1]) : 1);
			p.stopAll();
		});
		
		Commands.add(Rights.PLAYER, "master,max", "Maxes all stats out.", (p, args) -> {
			for (int skill = 0; skill < 25; skill++)
				p.getSkills().setXp(skill, 105000000);
			p.reset();
			p.getAppearance().generateAppearanceData();
		});
		
		Commands.add(Rights.PLAYER, "setlevel [skillId level]", "Sets a skill to a specified level.", (p, args) -> {
			if (!p.getEquipment().isEmpty()) {
				p.sendMessage("Please unequip everything you're wearing first.");
				return;
			}
			int skill = Integer.parseInt(args[0]);
			int level = Integer.parseInt(args[1]);
			if (level < 0 || level > (skill == Constants.DUNGEONEERING ? 120 : 99)) {
				p.sendMessage("Please choose a valid level.");
				return;
			}
			if (skill < 0 || skill >= Constants.SKILL_NAME.length) {
				p.sendMessage("Please choose a valid skill.");
				return;
			}
			p.getSkills().set(skill, level);
			p.getSkills().setXp(skill, Skills.getXPForLevel(level));
			p.getAppearance().generateAppearanceData();
			p.reset();
			p.sendMessage("Successfully set " + Constants.SKILL_NAME[skill] + " to " + level + ".");
		});
		
		Commands.add(Rights.PLAYER, "reset", "Resets all stats to 1.", (p, args) -> {
			for (int skill = 0; skill < 25; skill++)
				p.getSkills().setXp(skill, 0);
			p.getSkills().init();
		});
		
		Commands.add(Rights.PLAYER, "copy [player name]", "Copies the other player's levels, equipment, and inventory.", (p, args) -> {
			Player target = World.getPlayer(Utils.concat(args));
			if (target == null) {
				p.sendMessage("Couldn't find player " + Utils.concat(args) + ".");
				return;
			}
			Item[] equip = target.getEquipment().getItemsCopy();
			for (int i = 0; i < equip.length; i++) {
				if (equip[i] == null)
					continue;

				p.getEquipment().set(i, new Item(equip[i]));
				p.getEquipment().refresh(i);
			}
			Item[] inv = target.getInventory().getItems().getItemsCopy();
			for (int i = 0; i < inv.length; i++) {
				if (inv[i] == null)
					continue;

				p.getInventory().getItems().set(i, new Item(inv[i]));
				p.getInventory().refresh(i);
			}
			for (int i = 0; i < p.getSkills().getLevels().length; i++) {
				p.getSkills().set(i, target.getSkills().getLevelForXp(i));
				p.getSkills().setXp(i, Skills.getXPForLevel(target.getSkills().getLevelForXp(i)));
			}
			p.getAppearance().generateAppearanceData();
		});
		
		Commands.add(Rights.PLAYER, "spellbook [modern/lunar/ancient]", "Switches to modern, lunar, or ancient spellbooks.", (p, args) -> {
			switch(args[0].toLowerCase()) {
			case "modern":
			case "normal":
				p.getCombatDefinitions().setSpellBook(0);
				break;
			case "ancient":
			case "ancients":
				p.getCombatDefinitions().setSpellBook(1);
				break;
			case "lunar":
			case "lunars":
				p.getCombatDefinitions().setSpellBook(2);
				break;
			default:
				p.sendMessage("Invalid spellbook. Spellbooks are modern, lunar, and ancient.");
				break;
			}
		});
		
		Commands.add(Rights.PLAYER, "prayers [normal/curses]", "Switches to curses, or normal prayers.", (p, args) -> {
			switch(args[0].toLowerCase()) {
			case "normal":
			case "normals":
				p.getPrayer().setPrayerBook(false);
				break;
			case "curses":
			case "ancients":
				p.getPrayer().setPrayerBook(true);
				break;
			default:
				p.sendMessage("Invalid prayer book. Prayer books are normal and curses.");
				break;
			}
		});
		
		Commands.add(Rights.PLAYER, "maxbank", "Sets all the item counts in the player's bank to 10m.", (p, args) -> {
			for (Item i : p.getBank().getContainerCopy())
				if (i != null)
					i.setAmount(10500000);
		});
		
		Commands.add(Rights.PLAYER, "clearbank,emptybank", "Empties the players bank entirely.", (p, args) -> {
			p.sendOptionDialogue("Clear bank?", new String[] { "Yes", "No" }, new DialogueOptionEvent() {
				@Override
				public void run(Player player) {
					if (getOption() == 1) {
						player.getBank().clear();
					}
				}
			});
		});
		
		Commands.add(Rights.PLAYER, "god", "Toggles god mode for the player.", (p, args) -> {
			boolean god = p.getTemporaryAttributes().get("godMode") != null ? (boolean) p.getTemporaryAttributes().get("godMode") : false;
			p.getTemporaryAttributes().put("godMode", !god);
			p.sendMessage("GODMODE: " + !god);
		});
		
		Commands.add(Rights.PLAYER, "infspec", "Toggles infinite special attack for the player.", (p, args) -> {
			boolean spec = p.getTemporaryAttributes().get("infSpecialAttack") != null ? (boolean) p.getTemporaryAttributes().get("infSpecialAttack") : false;
			p.getTemporaryAttributes().put("infSpecialAttack", !spec);
			p.sendMessage("INFINITE SPECIAL ATTACK: " + !spec);
		});
		
		Commands.add(Rights.PLAYER, "infpray", "Toggles infinite prayer for the player.", (p, args) -> {
			boolean spec = p.getTemporaryAttributes().get("infPrayer") != null ? (boolean) p.getTemporaryAttributes().get("infPrayer") : false;
			p.getTemporaryAttributes().put("infPrayer", !spec);
			p.sendMessage("INFINITE PRAYER: " + !spec);
		});
		
		// case "load":
					// if (!player.getInterfaceManager().containsInterface(762) || (Boolean)
					// player.getTemporaryAttributes().get("viewingOtherBank") != null && (Boolean)
					// player.getTemporaryAttributes().get("viewingOtherBank") == true) {
					// player.sendMessage("You must be in your bank screen to do
					// this.");
					// return true;
					// }
					// player.loadLoadout(cmd[1]);
					// return true;
					//
					// case "saveload":
					// player.saveLoadout(cmd[1]);
					// return true;
					//
					// case "delload":
					// player.deleteLoadout(cmd[1]);
					// return true;
					//
					// case "loadouts":
					// player.sendLoadoutText();
					// return true;
	}
}
