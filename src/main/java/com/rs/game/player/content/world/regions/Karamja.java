package com.rs.game.player.content.world.regions;

import com.rs.game.ForceMovement;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Karamja  {
	
	public static NPCClickHandler handlePirateJackieFruit = new NPCClickHandler(1055) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
						}
					});
				}
			});
		}
	};
	
	public static NPCClickHandler handleKalebParamaya = new NPCClickHandler(512) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
						}
					});
				}
			});
		}
	};
	
	public static NPCClickHandler handleJungleForesters = new NPCClickHandler(401, 402) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
						}
					});
				}
			});
		}
	};
	
	public static ObjectClickHandler handleBrimhavenDungeonEntrance = new ObjectClickHandler(new Object[] { 5083 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2713, 9564, 0));
		}
	};
	
	public static ObjectClickHandler handleBrimhavenDungeonExit = new ObjectClickHandler(new Object[] { 5084 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2745, 3152, 0));
		}
	};
	
	public static ObjectClickHandler handleJogreLogWalk = new ObjectClickHandler(new Object[] { 2332 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getX() > 2908)
				Agility.walkToAgility(e.getPlayer(), 155, new WorldTile(2906, 3049, 0), 0);
			else
				Agility.walkToAgility(e.getPlayer(), 155, new WorldTile(2910, 3049, 0), 0);
		}
	};
	
	public static ObjectClickHandler handleMossGiantRopeSwings = new ObjectClickHandler(new Object[] { 2322, 2323 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			final WorldTile toTile = e.getObjectId() == 2322 ? new WorldTile(2704, 3209, 0) : new WorldTile(2709, 3205, 0);
			if (Agility.hasLevel(e.getPlayer(), 10)) {
				if (e.isAtObject()) {
					if (e.getObjectId() == 2322 ? e.getPlayer().getX() == 2704 : e.getPlayer().getX() == 2709) {
						e.getPlayer().sendMessage("You can't reach that.", true);
						return;
					}
					e.getPlayer().lock();
					e.getPlayer().faceObject(e.getObject());
					e.getPlayer().setNextAnimation(new Animation(751));
					World.sendObjectAnimation(e.getPlayer(), e.getObject(), new Animation(497));

					e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 1, toTile, 3, Utils.getAngleTo(toTile.getX() - e.getPlayer().getX(), toTile.getY() - e.getPlayer().getY())));
					e.getPlayer().sendMessage("You skillfully swing across the rope.", true);
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							e.getPlayer().unlockNextTick();
							e.getPlayer().getSkills().addXp(Constants.AGILITY, 0.1);
							e.getPlayer().setNextWorldTile(toTile);
						}

					});
					e.getPlayer().unlock();
				}
			}
		}
	};
	
	public static ObjectClickHandler handleJogreWaterfallSteppingStones = new ObjectClickHandler(new Object[] { 2333, 2334, 2335 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 30))
				return;
			e.getPlayer().setNextAnimation(new Animation(741));
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, e.getObject(), 1, Utils.getAngleTo(e.getObject().getX() - e.getPlayer().getX(), e.getObject().getY() - e.getPlayer().getY())));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().setNextWorldTile(new WorldTile(e.getObject()));
				}
			}, 0);
		}
	};
	
	public static ObjectClickHandler handleRareTreeDoors = new ObjectClickHandler(new Object[] { 9038, 9039 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP2) {
				if (e.getPlayer().getX() >= e.getObject().getX()) {
					Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
				} else {
					if (e.getPlayer().getInventory().containsItem(6306, 10)) {
						Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
						e.getPlayer().getInventory().deleteItem(6306, 10);
					} else {
						e.getPlayer().sendMessage("You need 10 trading sticks to use this door.");
					}
				}
			} else if (e.getOpNum() == ClientPacket.OBJECT_OP1) {
				if (e.getPlayer().getX() >= e.getObject().getX()) {
					Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
				} else {
					e.getPlayer().sendOptionDialogue("Pay 10 trading sticks to enter?", new String[] {"Yes", "No"}, new DialogueOptionEvent() {

						@Override
						public void run(Player player) {
							if (getOption() == 1) {
								if (e.getPlayer().getInventory().containsItem(6306, 10)) {
									Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
									e.getPlayer().getInventory().deleteItem(6306, 10);
								} else {
									e.getPlayer().sendMessage("You need 10 trading sticks to use this door.");
								}
							}
						}

					});
				}
			}
		}
	};
	
	public static ObjectClickHandler handleCrandorVolcanoCrater = new ObjectClickHandler(new Object[] { 25154 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2834, 9657, 0));
		}
	};
	
	public static ObjectClickHandler handleCrandorVolcanoRope = new ObjectClickHandler(new Object[] { 25213 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2832, 3255, 0));
		}
	};
	
	public static ObjectClickHandler handleKaramjaVolcanoRocks = new ObjectClickHandler(new Object[] { 492 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2857, 9569, 0));
		}
	};
	
	public static ObjectClickHandler handleKaramjaVolcanoRope = new ObjectClickHandler(new Object[] { 1764 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2855, 3169, 0));
		}
	};
	
	public static ObjectClickHandler handleElvargHiddenWall = new ObjectClickHandler(new Object[] { 2606 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Doors.handleDoor(e.getPlayer(), e.getObject());
		}
	};
	
	public static ObjectClickHandler handleShiloFurnaceDoor = new ObjectClickHandler(new Object[] { 2266, 2267 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 2267)
				return;
			if (e.getPlayer().getY() > e.getObject().getY())
				Doors.handleDoor(e.getPlayer(), e.getObject());
			else {
				if (e.getPlayer().getInventory().containsItem(995, 20)) {
					e.getPlayer().getInventory().deleteItem(995, 20);
					Doors.handleDoor(e.getPlayer(), e.getObject());
				} else {
					e.getPlayer().sendMessage("You need 20 gold to use this furnace.");
				}
			}
		}
	};
	
	public static ObjectClickHandler handleTzhaarEnter = new ObjectClickHandler(new Object[] { 68134 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(4667, 5059, 0));
		}
	};
	
	public static ObjectClickHandler handleTzhaarExit = new ObjectClickHandler(new Object[] { 68135 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2845, 3170, 0));
		}
	};
	
	public static ObjectClickHandler handleJogreCaveEnter = new ObjectClickHandler(new Object[] { 2584 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2830, 9522, 0));
		}
	};
	
	public static ObjectClickHandler handleJogreCaveExit = new ObjectClickHandler(new Object[] { 2585 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2824, 3120, 0));
		}
	};
	
	public static ObjectClickHandler handleShiloEnter = new ObjectClickHandler(new Object[] { 2216 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("You quickly climb over the cart.");
			e.getPlayer().ladder(e.getPlayer().getX() > e.getObject().getX() ? e.getPlayer().transform(-4, 0, 0) : e.getPlayer().transform(4, 0, 0));
		}
	};
	
	public static ObjectClickHandler handleShiloCartEnter = new ObjectClickHandler(new Object[] { 2230 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2833, 2954, 0));
		}
	};
	
	public static ObjectClickHandler handleShiloCartExit = new ObjectClickHandler(new Object[] { 2265 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2778, 3210, 0));
		}
	};
}
