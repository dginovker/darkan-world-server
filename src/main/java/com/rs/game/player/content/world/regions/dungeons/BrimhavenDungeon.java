package com.rs.game.player.content.world.regions.dungeons;

import com.rs.game.ForceMovement;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.skills.woodcutting.Hatchet;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class BrimhavenDungeon {
	
	public static ObjectClickHandler handleVines = new ObjectClickHandler(new Object[] { 5103, 5104, 5105, 5106, 5107 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Hatchet defs = Hatchet.getBest(e.getPlayer());
			if (defs == null) {
				e.getPlayer().sendMessage("You need a hatchet that you are able to use at your Woodcutting level to chop this.");
				return;
			}
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(defs.getAnim());
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					WorldTile tile = new WorldTile(e.getObject());
					if (e.getObject().getRotation() == 3 || e.getObject().getRotation() == 1)
						tile.setLocation(e.getObject().transform(e.getPlayer().getX() < e.getObject().getX() ? 1 : -1, 0, 0));
					else
						tile.setLocation(e.getObject().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 1 : -1, 0));
					e.getPlayer().unlock();
					e.getPlayer().setNextWorldTile(tile);
				}
			}, 4);
		}
	};
	
	public static ObjectClickHandler handleSteppingStones = new ObjectClickHandler(new Object[] { 5110, 5111 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 12))
				return;
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(new Animation(741));
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, e.getObject(), 1, Utils.getAngleTo(e.getObject().getX() - e.getPlayer().getX(), e.getObject().getY() - e.getPlayer().getY())));
			if (e.getObject().getId() == 5110) {
				WorldTasksManager.schedule(new WorldTask() {
					int ticks = 0;

					@Override
					public void run() {
						ticks++;
						if (ticks == 1) {
							e.getPlayer().setNextWorldTile(e.getObject());
						} else if (ticks == 2 || ticks == 3) {
							WorldTile next = e.getPlayer().transform(0, -1, 0);
							if (ticks == 2) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else if (ticks == 4 || ticks == 5) {
							WorldTile next = e.getPlayer().transform(-1, 0, 0);
							if (ticks == 4) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else if (ticks == 6 || ticks == 7) {
							WorldTile next = e.getPlayer().transform(-1, 0, 0);
							if (ticks == 6) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else if (ticks == 8 || ticks == 9) {
							WorldTile next = e.getPlayer().transform(0, -1, 0);
							if (ticks == 8) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else if (ticks == 10 || ticks == 11) {
							WorldTile next = e.getPlayer().transform(0, -1, 0);
							if (ticks == 10) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else {
							e.getPlayer().unlock();
							stop();
							return;
						}
					}
				}, 0, 0);
			} else {
				WorldTasksManager.schedule(new WorldTask() {
					int ticks = 0;

					@Override
					public void run() {
						ticks++;
						if (ticks == 1) {
							e.getPlayer().setNextWorldTile(e.getObject());
						} else if (ticks == 2 || ticks == 3) {
							WorldTile next = e.getPlayer().transform(0, 1, 0);
							if (ticks == 2) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else if (ticks == 4 || ticks == 5) {
							WorldTile next = e.getPlayer().transform(0, 1, 0);
							if (ticks == 4) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else if (ticks == 6 || ticks == 7) {
							WorldTile next = e.getPlayer().transform(1, 0, 0);
							if (ticks == 6) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else if (ticks == 8 || ticks == 9) {
							WorldTile next = e.getPlayer().transform(1, 0, 0);
							if (ticks == 8) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else if (ticks == 10 || ticks == 11) {
							WorldTile next = e.getPlayer().transform(0, 1, 0);
							if (ticks == 10) {
								e.getPlayer().setNextAnimation(new Animation(741));
								e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, next, 1, Utils.getAngleTo(next.getX() - e.getPlayer().getX(), next.getY() - e.getPlayer().getY())));
							} else {
								e.getPlayer().setNextWorldTile(next);
							}
						} else {
							e.getPlayer().unlock();
							stop();
							return;
						}
					}
				}, 0, 0);
			}
		}
	};
	
	public static ObjectClickHandler handleRedDragonJump = new ObjectClickHandler(false, new Object[] { 55342 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 34))
				return;
			e.getPlayer().walkToAndExecute(new WorldTile(2681, 9540, 0), new Runnable() {
				@Override
				public void run() {
					WorldTile face = new WorldTile(2681, 9537, 0);
					e.getPlayer().setNextAnimation(new Animation(14717));
					e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer(), 0, face, 1, Utils.getAngleTo(face.getX() - e.getPlayer().getX(), face.getY() - e.getPlayer().getY())));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							e.getPlayer().setNextAnimation(new Animation(14718));
							e.getPlayer().setNextWorldTile(new WorldTile(2697, 9524, 0));
						}
					}, 1);
				}
			});
		}
	};
	
	public static ObjectClickHandler handleRedDragonLogBalance = new ObjectClickHandler(new Object[] { 5088, 5090 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 30))
				return;
			final int id = e.getObject().getId();
			boolean back = id == 5088;
			e.getPlayer().lock(4);
			final WorldTile tile = back ? new WorldTile(2687, 9506, 0) : new WorldTile(2682, 9506, 0);
			final boolean isRun = e.getPlayer().isRunning();
			e.getPlayer().setRun(false);
			e.getPlayer().addWalkSteps(tile.getX(), tile.getY(), -1, false);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().setRun(isRun);
				}
			}, 4);
		}
	};

	public static ObjectClickHandler handleStairs = new ObjectClickHandler(new Object[] { 5094, 5096, 5097, 5098 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getObjectId()) {
			case 5094:
				e.getPlayer().setNextWorldTile(new WorldTile(2643, 9595, 2));
				break;
			case 5096:
				e.getPlayer().setNextWorldTile(new WorldTile(2649, 9591, 0));
				break;
			case 5097:
				e.getPlayer().setNextWorldTile(new WorldTile(2637, 9510, 2));
				break;
			case 5098:
				e.getPlayer().setNextWorldTile(new WorldTile(2637, 9517, 0));
				break;
			}
		}
	};
	
}
