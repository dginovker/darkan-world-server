package com.rs.game.npc.qbd;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.skills.prayer.Prayer;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

/**
 * Handles the Queen Black Dragon's melee attack.
 * 
 * @author Emperor
 * 
 */
public final class MeleeAttack implements QueenAttack {

	/**
	 * The default melee animation.
	 */
	private static final Animation DEFAULT = new Animation(16717);

	/**
	 * The east melee animation.
	 */
	private static final Animation EAST = new Animation(16744);

	/**
	 * The west melee animation.
	 */
	private static final Animation WEST = new Animation(16743);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		if (victim.getX() < npc.getBase().getX() + 31) {
			npc.setNextAnimation(WEST);
		} else if (victim.getX() > npc.getBase().getX() + 35) {
			npc.setNextAnimation(EAST);
		} else {
			npc.setNextAnimation(DEFAULT);
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				stop();
				int hit = 0;
				if (victim.getPrayer().active(Prayer.DEFLECT_MELEE)) {
					victim.setNextAnimation(new Animation(12573));
					victim.setNextSpotAnim(new SpotAnim(2230));
					victim.sendMessage("You are unable to reflect damage back to this creature.");
				} else if (victim.getPrayer().active(Prayer.PROTECT_MELEE)) {
					victim.setNextAnimation(new Animation(PlayerCombat.getDefenceEmote(victim)));
				} else {
					hit = Utils.random(0 + Utils.random(150), 360);
					victim.setNextAnimation(new Animation(PlayerCombat.getDefenceEmote(victim)));
				}
				victim.applyHit(new Hit(npc, hit, hit == 0 ? HitLook.MISSED : HitLook.MELEE_DAMAGE));
			}
		});
		return Utils.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return victim.getY() > npc.getBase().getY() + 32;
	}

}