package com.rs.game.player.content.skills.construction;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Bank;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.construction.House.RoomReference;
import com.rs.game.player.content.skills.construction.HouseConstants.Builds;
import com.rs.game.player.content.skills.construction.HouseConstants.Room;
import com.rs.game.player.content.skills.construction.HouseConstants.Servant;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class ServantNPC extends NPC {

	private Servant servant;
	private Player owner;
	private House house;
	private boolean follow, greetGuests;
	private int[][] checkNearDirs;

	public ServantNPC(House house) {
		super(house.getServant().getId(), house.getPortal().transform(1, 0, 0), true);
		servant = house.getServant();
		owner = house.getPlayer();
		this.house = house;
		this.checkNearDirs = Utils.getCoordOffsetsNear(super.getSize());
		if (owner.getSkills().getLevel(Constants.CONSTRUCTION) < servant.getLevel()) {
			house.setServantOrdinal((byte) -1);
			return;
		}
	}

	public void fire() {
		house.setServantOrdinal((byte) -1);
	}

	public long getBankDelay() {
		return servant.getBankDelay();
	}

	public boolean isFollowing() {
		return follow;
	}

	public void setFollowing(boolean follow) {
		this.follow = follow;
		if (!follow)
			setNextFaceEntity(null);
	}

	public void makeFood(final Builds[] builds) {
		if (house == null)
			return;
		setFollowing(false);
		if (house.isBuildMode()) {
			owner.sendMessage("Your servant cannot prepare food while in building mode.");
			return;
		}
		String basicResponse = "I appologise, but I cannot serve " + (owner.getAppearance().isMale() ? "Sir" : "Madam") + " without";
		final RoomReference kitchen = house.getRoom(Room.KITCHEN), diningRoom = house.getRoom(Room.DINING_ROOM);
		if (kitchen == null) {
			owner.getDialogueManager().execute(new SimpleNPCMessage(), getId(), basicResponse + " a proper kitchen.");
			return;
		} else if (diningRoom == null) {
			owner.getDialogueManager().execute(new SimpleNPCMessage(), getId(), basicResponse + " a proper dining room.");
			return;
		} else {

			for (Builds build : builds) {
				if (!kitchen.containsBuild(build)) {
					owner.getDialogueManager().execute(new SimpleNPCMessage(), getId(), basicResponse + " a " + build.toString().toLowerCase() + ".");
					return;
				}
			}

			if (!diningRoom.containsBuild(HouseConstants.Builds.DINING_TABLE)) {
				owner.getDialogueManager().execute(new SimpleNPCMessage(), getId(), basicResponse + " a dining table");
				return;
			}

			final WorldTile kitchenTile = house.getCenterTile(kitchen);
			final WorldTile diningRoomTile = house.getCenterTile(diningRoom);

			setCantInteract(true);
			house.incrementPaymentStage();

			WorldTasksManager.schedule(new WorldTask() {

				int count = 0, totalCount = 0, index = 0;

				@Override
				public void run() {
					if (!house.isLoaded()) {
						stop();
						return;
					}
					count++;
					if (count == 1) {
						setNextForceTalk(new ForceTalk("I shall return in a moment."));
						setNextAnimation(new Animation(858));
						totalCount = (builds.length * 3) + count;
					} else if (count == 2) {
						setNextWorldTile(new WorldTile(World.getFreeTile(kitchenTile, 2)));
					} else if (totalCount > 0 && index < builds.length) {
						int calculatedCount = totalCount - count;
						Builds build = builds[index];
						if (calculatedCount % 3 == 0) {
							setNextAnimation(new Animation(build == Builds.STOVE ? 897 : 3659));
							index++;
						} else if (calculatedCount % 1 == 0)
							calcFollow(house.getWorldObjectForBuild(kitchen, build), true);
					} else if (count == totalCount + 3) {
						setNextWorldTile(World.getFreeTile(diningRoomTile, 2));
					} else if (count == totalCount + 4 || count == totalCount + 5) {
						WorldTile diningTable = house.getWorldObjectForBuild(diningRoom, Builds.DINING_TABLE);
						if (count == totalCount + 4)
							calcFollow(diningTable, true);
						else {
							setNextAnimation(new Animation(808));
							int rotation = kitchen.getRotation();
							for (int x = 0; x < (rotation == 1 || rotation == 3 ? 2 : 4); x++)
								for (int y = 0; y < (rotation == 1 || rotation == 3 ? 4 : 2); y++)
									World.addGroundItem(new Item(builds.length == 6 ? 7736 : builds.length == 5 ? house.getServant().getFoodId() : HouseConstants.BEERS[kitchen.getBuildSlot(Builds.BARRELS)]), diningTable.transform(x, y, 0), null, false, 300);
							setCantInteract(false);
							stop();
						}
					}
				}
			}, 2, 2);

		}
	}

	/**
	 * Types : 0 - Take item from bank, 1 - Logs to Plank, 2 - Notes to Item, 3
	 * - Bank item
	 * 
	 * @param item
	 * @param quantity
	 * @param type
	 */
	public void requestType(int item, int quantity, final byte type) {
		final Bank bank = owner.getBank();
		final ItemDefinitions defs = ItemDefinitions.getDefs(item);
		int inventorySize = servant.getInventorySize();
		if (!bank.containsItem(defs.isNoted() ? defs.getCertId() : item, 1) && type == 0) {
			owner.getDialogueManager().execute(new SimpleNPCMessage(), getId(), "It appears you do not have this item in your bank.");
			return;
		} else if (quantity > inventorySize) {
			owner.getDialogueManager().execute(new SimpleNPCMessage(), getId(), "I'm sorry. I can only hold " + inventorySize + " items during a trip.");
			return;
		}
		setNextNPCTransformation(1957);

		if (type == 1 || type == 2) {
			int amountOwned = owner.getInventory().getAmountOf(item);
			if (quantity > amountOwned) {
				quantity = amountOwned;
				if (quantity > inventorySize)
					quantity = inventorySize;
			}
		}

		final int[] plank = SawmillOperator.getPlankForLog(item);
		if (plank != null && type == 1) {
			final int cost = (int) ((plank[1] * 0.7) * quantity);
			if (owner.getInventory().getAmountOf(995) < cost) {
				owner.getDialogueManager().execute(new SimpleNPCMessage(), getId(), "You do not have enough coins to cover the costs of the sawmill.");
				return;
			}
		}

		if (type == 0 || type == 1 || type == 2) {
			if (type == 2 || type == 0) {
				int freeSlots = owner.getInventory().getFreeSlots();
				if (quantity > freeSlots)
					quantity = freeSlots;
			} else if (type == 1)
				owner.getInventory().removeItems(new Item(995, (int) (quantity * (plank[1] * 0.7))));
			if (type != 0)
				owner.getInventory().deleteItem(item, quantity);
		}

		final int completeQuantity = quantity;
		setCantInteract(true);

		if (defs.isNoted())
			item = defs.getCertId();
		final int finalItem = item;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextNPCTransformation(servant.getId());
				setCantInteract(false);
				if (!owner.isRunning() || !house.isLoaded() || !house.getPlayers().contains(owner)) {
					if (type == 1 || type == 2) {
						bank.addItem(new Item(finalItem, completeQuantity), false);
					}
					return;
				}
				house.incrementPaymentStage();
				if (type == 0) {
					if (bank.containsItem(finalItem, completeQuantity)) {
						bank.withdrawItemDel(finalItem, completeQuantity);
						owner.getInventory().addItem(finalItem, completeQuantity, true);
					}
				} else if (type == 1) {
					owner.getInventory().addItem(plank[0], completeQuantity);
				} else if (type == 2) {
					owner.getInventory().addItem(finalItem, completeQuantity);
				} else {
					for (int i = 0; i < completeQuantity; i++) {
						bank.depositItem(owner.getInventory().getItems().getThisItemSlot(finalItem), completeQuantity, false);
					}
				}
				owner.getDialogueManager().execute(new SimpleNPCMessage(), getId(), type == 3 ? "I have successfully deposited your items into your bank. No longer will the items be at risk from thieves." : "I have returned with the items you asked me to retrieve.");
			}
		}, (int) servant.getBankDelay());
	}

	public void call() {
		int size = getSize();
		WorldTile teleTile = null;
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
			final WorldTile tile = new WorldTile(new WorldTile(owner.getX() + checkNearDirs[0][dir], owner.getY() + checkNearDirs[1][dir], owner.getPlane()));
			if (World.floorAndWallsFree(tile, size)) {
				teleTile = tile;
				break;
			}
		}
		if (teleTile == null) {
			return;
		}
		setNextWorldTile(teleTile);
	}

	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
			setNextFaceEntity(owner);
		int size = getSize();
		int targetSize = owner.getSize();
		if (WorldUtil.collides(getX(), getY(), size, owner.getX(), owner.getY(), targetSize) && !owner.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(owner.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(owner.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), owner.getY() - size)) {
							return;
						}
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!lineOfSightTo(owner, true) || !WorldUtil.isInRange(getX(), getY(), size, owner.getX(), owner.getY(), targetSize, 0))
			calcFollow(owner, 2, true, true);
	}

	@Override
	public void processNPC() {
		if (greetGuests && !withinDistance(getRespawnTile(), 5)) {
			greetGuests = false;
		}
		if (!follow) {
			super.processNPC();
			return;
		}
		if (!withinDistance(owner, 12)) {
			call();
			return;
		}
		sendFollow();
	}

	public boolean isGreetGuests() {
		return greetGuests;
	}

	public void setGreetGuests(boolean greetGuests) {
		this.greetGuests = greetGuests;
	}

	public Servant getServantData() {
		return servant;
	}
}
