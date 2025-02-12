package com.rs.game.player.content.world.regions;

import com.rs.game.player.Player;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Lumbridge  {
	
	public static final String WHEAT_DEPOSITED = "wheatInMill";
	public static final String WHEAT_GRINDED = "wheatGrinded";
	
	public static NPCClickHandler handleExplorerJack = new NPCClickHandler(7969) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.EXPLORERS_RING).getStart());
						}
					});
				}
			});
		}
	};
	
	public static NPCClickHandler handleBob = new NPCClickHandler(519) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 1) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
						addOptions("What would you like to say?", new Options() {
							@Override
							public void create() {
								option("What do you have for sale?", () -> ShopsHandler.openShop(player, "bobs_brilliant_axes"));
								option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.EXPLORERS_RING).getStart());
							}
						});
					}
				});
			} else {
				ShopsHandler.openShop(e.getPlayer(), "bobs_brilliant_axes");
			}
		}
	};
	
	public static ItemOnObjectHandler handleWheatDeposit = new ItemOnObjectHandler(new Object[] { 70034 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (e.getItem().getId() == 1947) {
				if (e.getPlayer().get(WHEAT_DEPOSITED) == Boolean.FALSE) {
					e.getPlayer().getInventory().deleteItem(1947, 1);
					e.getPlayer().setNextAnimation(new Animation(832));
					e.getPlayer().sendMessage("You put the wheat in the hopper.");
					e.getPlayer().save(WHEAT_DEPOSITED, Boolean.TRUE);
				}
			}
		}
	};

	public static void updateWheat(Player player) {
		player.getVars().setVar(695, player.get(WHEAT_GRINDED) == Boolean.TRUE ? 1 : 0);
	}
	
	public static LoginHandler updateWheatLogin = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			updateWheat(e.getPlayer());
		}
	};
	
	public static NPCClickHandler handleDukeHoratio = new NPCClickHandler(741) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().sendOptionDialogue("Would you like an anti-dragon shield?", new String[] {"Yes", "No"}, new DialogueOptionEvent() {
				@Override
				public void run(Player player) {
					if (option == 1)
						player.getInventory().addItem(1540, 1);
				}
			});
		}
	};

	public static ObjectClickHandler handleCellarLadders = new ObjectClickHandler(new Object[] { 24360, 24365 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 24360)
				e.getPlayer().setNextWorldTile(new WorldTile(3190, 9834, 0));
			else if (e.getObjectId() == 24365)
				e.getPlayer().setNextWorldTile(new WorldTile(3188, 3433, 0));
		}
	};
	
	public static ObjectClickHandler handleRFDChest = new ObjectClickHandler(new Object[] { 12308 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch (e.getOpNum()) {
			case OBJECT_OP1:
				e.getPlayer().getBank().openBank();
				break;
			case OBJECT_OP2:
				ShopsHandler.openShop(e.getPlayer(), "culinaromancer_food_10");
				break;
			case OBJECT_OP3:
				ShopsHandler.openShop(e.getPlayer(), "culinaromancer_equipment_10");
				break;
			default:
				break;
			}
		}
	};
	
	public static ObjectClickHandler handleLadders = new ObjectClickHandler(new Object[] { 36771, 36772 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 36771) {
				e.getPlayer().useLadder(new WorldTile(3207, 3222, 3));
			} else if (e.getObjectId() == 36772) {
				e.getPlayer().useLadder(new WorldTile(3207, 3224, 2));
			}
		}
	};
	
	public static ObjectClickHandler handleTakeFlour = new ObjectClickHandler(new Object[] { 36880 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getInventory().containsItem(1931, 1)) {
				if (e.getPlayer().get(WHEAT_GRINDED) == Boolean.TRUE) {
					e.getPlayer().save(WHEAT_GRINDED, Boolean.FALSE);
					e.getPlayer().save(WHEAT_DEPOSITED, Boolean.FALSE);
					e.getPlayer().sendMessage("You take the ground flour.");
					e.getPlayer().setNextAnimation(new Animation(832));
					e.getPlayer().getInventory().deleteItem(1931, 1);
					e.getPlayer().getInventory().addItem(1933, 1);
					updateWheat(e.getPlayer());
				}
			} else {
				e.getPlayer().sendMessage("You need an empty pot to gather the flour.");
			}
		}
	};
	
	public static ObjectClickHandler handleWindmillLever = new ObjectClickHandler(new Object[] { 2718 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("You pull the lever.");
			if (e.getPlayer().get(WHEAT_DEPOSITED) == Boolean.TRUE) {
				e.getPlayer().save(WHEAT_GRINDED, Boolean.TRUE);
				e.getPlayer().sendMessage("You hear the grinding of stones and the wheat falls below.");
				updateWheat(e.getPlayer());
			}
		}
	};
	
	public static ObjectClickHandler handleHatchetStump = new ObjectClickHandler(new Object[] { 36974 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getInventory().containsItem(1351, 1)) {
				e.getPlayer().sendMessage("You have already taken the hatchet.");
			} else {
				e.getPlayer().getInventory().addItem(1351, 1);
			}
		}
	};
	
	public static ObjectClickHandler handleStaircases1 = new ObjectClickHandler(new Object[] { 45481, 45482 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 45481)
				e.getPlayer().setNextWorldTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 2 ? -3 : 0, 1));
			else if (e.getObjectId() == 45482)
				e.getPlayer().setNextWorldTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 2 ? 3 : 0, -1));
		}
	};
	
	public static ObjectClickHandler handleStaircases2 = new ObjectClickHandler(new Object[] { 45483, 45484 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 45483)
				e.getPlayer().setNextWorldTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 2 ? 3 : 0, 1));
			else if (e.getObjectId() == 45484)
				e.getPlayer().setNextWorldTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 2 ? -3 : 0, -1));
		}
	};
	
	public static ObjectClickHandler handleChurchLadders = new ObjectClickHandler(new Object[] { 36984, 36986 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getPlayer().transform(e.getObjectId() == 36984 ? -2 : 2, 0, 1));
		}
	};
	
	public static ObjectClickHandler handleChurchLaddersT2Up = new ObjectClickHandler(new Object[] { 36988, 36989 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getPlayer().transform(e.getObjectId() == 36988 ? -1 : 1, -1, 1));
		}
	};
	
	public static ObjectClickHandler handleChurchLaddersT2Down = new ObjectClickHandler(new Object[] { 36990, 36991 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getPlayer().transform(e.getObjectId() == 36990 ? 1 : -1, 1, -1));
		}
	};
}
