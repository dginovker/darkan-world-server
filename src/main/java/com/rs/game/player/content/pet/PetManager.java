package com.rs.game.player.content.pet;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.ForceTalk;
import com.rs.game.npc.pet.Pet;
import com.rs.game.player.Player;
import com.rs.game.player.content.ItemConstants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

/**
 * The pet manager.
 * 
 * @author Emperor
 * 
 */
public final class PetManager {

	/**
	 * The pet details mapping, sorted by item id.
	 */
	private final Map<Integer, PetDetails> petDetails = new HashMap<Integer, PetDetails>();

	/**
	 * The player.
	 */
	private transient Player player;

	/**
	 * The current NPC id.
	 */
	private int npcId;

	/**
	 * The current item id.
	 */
	private int itemId;

	/**
	 * The troll baby's name (if any).
	 */
	private String trollBabyName;

	/**
	 * Constructs a new {@code PetManager} {@code Object}.
	 */
	public PetManager() {
		/*
		 * empty.
		 */
	}

	/**
	 * Spawns a pet.
	 * 
	 * @param itemId
	 *            The item id.
	 * @param deleteItem
	 *            If the item should be removed.
	 * @return {@code True} if we were dealing with a pet item id.
	 */
	public boolean spawnPet(int itemId, boolean deleteItem) {
		Pets pets = Pets.forId(itemId);
		if (pets == null) {
			return false;
		}
		if (player.getPet() != null || player.getFamiliar() != null) {
			player.sendMessage("You already have a follower.");
			return true;
		}
		if (!hasRequirements(pets)) {
			return true;
		}
		int baseItemId = pets.getBabyItemId();
		PetDetails details = petDetails.get(baseItemId);
		if (details == null) {
			details = new PetDetails(pets.getGrowthRate() == 0.0 ? 100.0 : 0.0);
			petDetails.put(baseItemId, details);
		}
		int id = pets.getItemId(details.getStage());
		if (itemId != id) {
			player.sendMessage("This is not the right pet, grow the pet correctly.");
			return true;
		}
		int npcId = pets.getNpcId(details.getStage());
		if (npcId > 0) {
			Pet pet = new Pet(npcId, itemId, player, player, pets.getGrownNpcId() == -1 && pets.getOvergrownNpcId() == -1 ? null : details);
			this.npcId = npcId;
			this.itemId = itemId;
			pet.setGrowthRate(pets.getGrowthRate());
			player.setPet(pet);
			if (deleteItem) {
				player.setNextAnimation(new Animation(827));
				player.getInventory().deleteItem(itemId, 1);
			}
			return true;
		}
		return true;
	}

	/**
	 * Checks if the player has the requirements for the pet.
	 * 
	 * @param pet
	 *            The pet.
	 * @return {@code True} if so.
	 */
	private boolean hasRequirements(Pets pet) {
		switch (pet) {
		case TZREK_JAD:
			if (!player.isCompletedFightCaves()) {
				player.sendMessage("You need to complete at least one fight cave minigame to use this pet.");
				return false;
			}
			if (!player.isWonFightPits()) {
				player.sendMessage("You need to win at least one fight pits minigame to use this pet.");
				return false;
			}
			return true;
		default:
			return true;
		}
	}

	/**
	 * Initializes the pet manager.
	 */
	public void init() {
		if (npcId > 0 && itemId > 0) {
			spawnPet(itemId, false);
		}
	}

	/**
	 * Makes the pet eat.
	 * 
	 * @param foodId
	 *            The food item id.
	 * @param npc
	 *            The pet NPC.
	 */
	public void eat(int foodId, Pet npc) {
		if (npc != player.getPet()) {
			player.sendMessage("This isn't your pet!");
			return;
		}
		Pets pets = Pets.forId(itemId);
		if (pets == null) {
			return;
		}
		if (pets == Pets.TROLL_BABY) {
			if (!ItemConstants.isTradeable(new Item(foodId))) {
				player.sendMessage("Your troll baby won't eat this item.");
				return;
			}
			if (trollBabyName == null) {
				trollBabyName = ItemDefinitions.getDefs(foodId).getName();
				npc.setName(trollBabyName);
				npc.setNextForceTalk(new ForceTalk("YUM! Me likes " + trollBabyName + "!"));
			}
			player.getInventory().deleteItem(foodId, 1);
			player.sendMessage("Your pet happily eats the " + ItemDefinitions.getDefs(foodId).getName() + ".");
			return;
		}
		for (int food : pets.getFood()) {
			if (food == foodId) {
				player.getInventory().deleteItem(food, 1);
				player.sendMessage("Your pet happily eats the " + ItemDefinitions.getDefs(food).getName() + ".");
				player.setNextAnimation(new Animation(827));
				npc.getDetails().updateHunger(-15.0);
				return;
			}
		}
		player.sendMessage("Nothing interesting happens.");
	}

	/**
	 * Removes the details for this pet.
	 * 
	 * @param npcId
	 *            The item id of the pet.
	 */
	public void removeDetails(int itemId) {
		Pets pets = Pets.forId(itemId);
		if (pets == null) {
			return;
		}
		petDetails.remove(pets.getBabyItemId());
	}

	/**
	 * Gets the player.
	 * 
	 * @return The player.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the player.
	 * 
	 * @param player
	 *            The player to set.
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Gets the npcId.
	 * 
	 * @return The npcId.
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * Sets the npcId.
	 * 
	 * @param npcId
	 *            The npcId to set.
	 */
	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

	/**
	 * Gets the itemId.
	 * 
	 * @return The itemId.
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * Sets the itemId.
	 * 
	 * @param itemId
	 *            The itemId to set.
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * Gets the trollBabyName.
	 * 
	 * @return The trollBabyName.
	 */
	public String getTrollBabyName() {
		return trollBabyName;
	}

	/**
	 * Sets the trollBabyName.
	 * 
	 * @param trollBabyName
	 *            The trollBabyName to set.
	 */
	public void setTrollBabyName(String trollBabyName) {
		this.trollBabyName = trollBabyName;
	}

}