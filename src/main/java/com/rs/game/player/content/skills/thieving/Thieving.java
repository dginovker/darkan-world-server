package com.rs.game.player.content.skills.thieving;

import java.util.Set;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

/**
 * Handles the Thieving Skill
 * 
 * @author Dragonkk
 * 
 */
public class Thieving {

	public enum Stalls {
		VEGETABLE(4706, 2, new int[] { 1957, 1965, 1942, 1982, 1550 }, 1, 2, 10, 634),
		VEGETABLE2(66696, 2, new int[] { 1957, 1965, 1942, 1982, 1550 }, 1, 2, 10, 66697),

		CAKE(34384, 5, new int[] { 1891, 1901, 2309 }, 1, 2, 16, 34381),
		CAKE2(6163, 5, new int[] { 1891, 1901, 2309 }, 1, 2, 16, 6984),
		CAKE3(66692, 5, new int[] { 1891, 1901, 2309 }, 1, 2, 16, 66693),

		CRAFTING(4874, 5, new int[] { 1755, 1592, 1597 }, 1, 7, 16, 4797),
		CRAFTING2(6166, 5, new int[] { 1755, 1592, 1597 }, 1, 7, 16, 6984),
		CRAFTING3(66700, 5, new int[] { 1755, 1592, 1597 }, 1, 7, 16, 66701),

		MONKEY_FOOD(4875, 5, new int[] { 1963 }, 1, 7, 16, 4797),

		MONKEY_GENERAL(4876, 5, new int[] { 1931, 2347, 590 }, 1, 7, 16, 4797),

		TEA_STALL(635, 5, new int[] { 1978 }, 1, 7, 16, 634),

		SILK_STALL(34383, 20, new int[] { 950 }, 1, 2, 24, 34381),

		WINE_STALL(14011, 22, new int[] { 1937, 1993, 1987, 1935, 7919 }, 1, 16, 27, 2046),
		WINE_STALL2(66694, 22, new int[] { 1937, 1993, 1987, 1935, 7919 }, 1, 16, 27, 66695),

		SEED_STALL(7053, 27, new int[] { 5096, 5097, 5098, 5099, 5100, 5101, 5102, 5103, 5105 }, 1, 11, 10, 2047),
		SEED_STALL2(66698, 27, new int[] { 5096, 5097, 5098, 5099, 5100, 5101, 5102, 5103, 5105 }, 1, 11, 10, 66699),

		FUR_STALL(34387, 35, new int[] { 6814, 958 }, 1, 15, 36, 34381),
		FUR_STALL2(4278, 35, new int[] { 6814, 958 }, 1, 15, 36, 634),

		FISH_STALL(4705, 42, new int[] { 331, 359, 377 }, 1, 16, 42, 634),
		FISH_STALL2(4277, 42, new int[] { 331, 359, 377 }, 1, 16, 42, 634),

		CROSSBOW_STALL(17031, 49, new int[] { 877, 9420, 9440 }, 1, 11, 52, 6984),

		SILVER_STALL(34382, 50, new int[] { 442 }, 1, 30, 54, 34381),
		SILVER_STALL2(6164, 50, new int[] { 442 }, 1, 30, 54, 6984),

		SPICE_STALL(34386, 65, new int[] { 2007 }, 1, 80, 81, 34381),

		MAGIC_STALL(4877, 65, new int[] { 556, 557, 554, 555, 563 }, 1, 80, 100, 4797),

		SCIMITAR_STALL(4878, 65, new int[] { 1323 }, 1, 80, 100, 4797),

		GEM_STALL(34385, 75, new int[] { 1623, 1621, 1619, 1617 }, 1, 180, 16, 34381),
		GEM_STALL2(6162, 75, new int[] { 1623, 1621, 1619, 1617 }, 1, 180, 16, 6984);

		private int[] item;
		private int level;
		private int amount;
		private int objectId;
		private int replaceObject;
		private double experience;
		private int seconds;

		Stalls(int objectId, int level, int[] item, int amount, int seconds, double experience, int replaceObject) {
			this.objectId = objectId;
			this.level = level;
			this.item = item;
			this.amount = amount;
			this.seconds = seconds;
			this.experience = experience;
			this.replaceObject = replaceObject;
		}

		public int getReplaceObject() {
			return replaceObject;
		}

		public int getObjectId() {
			return objectId;
		}

		public int getItem(int count) {
			return item[count];
		}

		public int getAmount() {
			return amount;
		}

		public int getLevel() {
			return level;
		}

		public int getTime() {
			return seconds;
		}

		public double getExperience() {
			return experience;
		}
	}

	public static boolean isGuard(int npcId) {
		if (npcId == 32 || npcId == 21 || npcId == 2256 || npcId == 2132 || npcId == 2236 || npcId == 23 || npcId == 8813)
			return true;
		else
			return false;
	}

	public static void handleStalls(final Player player, final GameObject object) {
		for (final Stalls stall : Stalls.values()) {
			if (stall.getObjectId() == object.getId()) {
				if (player.getAttackedBy() != null && player.inCombat()) {
					player.sendMessage("You can't do this while you're under combat.");
					return;
				}
				final GameObject emptyStall = new GameObject(stall.getReplaceObject(), ObjectType.SCENERY_INTERACT, object.getRotation(), object.getX(), object.getY(), object.getPlane());
				if (player.getSkills().getLevel(Constants.THIEVING) < stall.getLevel()) {
					player.sendMessage("You need a thieving level of " + stall.getLevel() + " to steal from this.", true);
					return;
				}
				if (player.getInventory().getFreeSlots() <= 0) {
					player.sendMessage("Not enough space in your inventory.", true);
					return;
				}

				player.setNextAnimation(new Animation(881));
				player.lock(2);
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (!World.getRegion(object.getRegionId()).objectExists(object)) {
							stop();
							return;
						}
						player.getInventory().addItem(stall.getItem(Utils.getRandomInclusive(stall.item.length - 1)), Utils.getRandomInclusive(stall.getAmount()));
						player.getSkills().addXp(Constants.THIEVING, stall.getExperience());
						checkGuards(player);
						World.spawnObjectTemporary(emptyStall, stall.getTime()*2);
						stop();
					}
				}, 0, 0);
			}
		}
	}

	public static void checkGuards(Player player) {
		NPC guard = null;
		int lastDistance = -1;
		for (int regionId : player.getMapRegionsIds()) {
			Set<Integer> npcIndexes = World.getRegion(regionId).getNPCsIndexes();
			if (npcIndexes == null)
				continue;
			for (int npcIndex : npcIndexes) {
				NPC npc = World.getNPCs().get(npcIndex);
				if (npc == null)
					continue;
				if (!isGuard(npc.getId()) || npc.isUnderCombat() || npc.isDead() || !npc.withinDistance(player, 4) || !npc.lineOfSightTo(player, false))
					continue;
				int distance = (int) Utils.getDistance(npc.getX(), npc.getY(), player.getX(), player.getY());
				if (lastDistance == -1 || lastDistance > distance) {
					guard = npc;
					lastDistance = distance;
				}
			}
		}
		if (guard != null) {
			guard.setNextForceTalk(new ForceTalk("Hey, what do you think you are doing!"));
			guard.setTarget(player);
		}
	}
	
	public static void checkTrapsChest(Player player, GameObject object, int openedId, int level , int respawnTime, double xp, DropSet set) {
		Item[] loot = DropTable.calculateDrops(player, set);
		checkTrapsChest(player, object, openedId, level , respawnTime, xp, loot);
	}
	
	public static void checkTrapsChest(Player player, GameObject object, int openedId, int level, int respawnTime, double xp, Item... loot) {
		player.faceObject(object);
		if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("You don't have any space in your inventory.");
			return;
		}
		if (player.getSkills().getLevel(Constants.THIEVING) < level) {
			player.sendMessage("You need a higher thieving level.");
			return;
		}
		player.setNextAnimation(new Animation(536));
		player.lock(2);
		player.getSkills().addXp(Constants.THIEVING, xp);
		player.incrementCount("Chests thieved");
		object.setIdTemporary(openedId, Ticks.fromSeconds(respawnTime));
		for (Item item : loot) {
			if (item != null)
				player.getInventory().addItem(item);
		}
	}

	public static boolean pickDoor(Player player, GameObject object) {
		if (player.getTemporaryAttributes().get("numbFingers") == null)
			player.getTemporaryAttributes().put("numbFingers", 0);
		int thievingLevel = player.getSkills().getLevel(Constants.THIEVING);
		int increasedChance = getIncreasedChance(player);
		int decreasedChance = (Integer) player.getTemporaryAttributes().get("numbFingers");
		int level = Utils.getRandomInclusive(thievingLevel + (increasedChance - decreasedChance)) + 1;
		double ratio = level / (Utils.getRandomInclusive(45 + 5) + 1);
		if (Math.round(ratio * thievingLevel) < (player.inCombat() ? 50 : 40)) {
			player.sendMessage("You fail to unlock the door and your hands begin to numb down.");
			player.getTemporaryAttributes().put("numbFingers", decreasedChance + 1);
			return false;
		}
		player.sendMessage("You successfully unlock the door.");
		Doors.handleDoor(player, object);
		return true;
	}

	private static int getIncreasedChance(Player player) {
		int chance = 0;
		if (Equipment.getItemSlot(Equipment.HANDS) == 10075)
			chance += 12;
		player.getEquipment();
		if (Equipment.getItemSlot(Equipment.CAPE) == 15349)
			chance += 15;
		return chance;
	}

}
