package com.rs.game.player.content.holidayevents.halloween.hw07;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.managers.EmotesManager.Emote;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Muncher07D extends Conversation {
	
	private static Animation FLINCH = new Animation(6563);
	private static Animation BITE = new Animation(6565);

	public Muncher07D(Player player, NPC muncher) {
		super(player);
		addPlayer(HeadE.CHEERFUL, "Here, boy!");
		addPlayer(HeadE.TERRIFIED, "Whoaah!", () -> {
			muncher.faceEntity(player);
			muncher.forceTalk("Grrrrrr");
			muncher.setNextAnimation(FLINCH);
		});
		
		Dialogue op = addOption("He looks mad, what would you like to do?", "Stroke him"/*, "Try to entertain him"*/, "Blow a raspberry at him");
		
		op.addPlayer(HeadE.NERVOUS, "Okay, touching him seems to be a bad idea.", () -> {
			player.setNextAnimation(new Animation(7271));
			muncher.forceTalk("Grrrrrr");
			muncher.setNextAnimation(FLINCH);
		});
		
		//op.addPlayer(HeadE.CALM_TALK, "Entertain");
		
		op.addPlayer(HeadE.LAUGH, "Hehe. This'll make him think twice!")
		.addNext(() -> {
			player.lock();
			WorldTasksManager.schedule(new WorldTask() {
				int stage = 0;
				@Override
				public void run() {
					if (stage == 0) {
						player.faceEntity(muncher);
					} else if (stage == 1) {
						player.setNextAnimation(Emote.RASPBERRY.getAnim());
					} else if (stage == 3) {
						muncher.setNextAnimation(BITE);
						player.fakeHit(new Hit(player.getHitpoints(), HitLook.TRUE_DAMAGE));
					} else if (stage == 4) {
						player.sendDeath(null);
					} else if (stage == 11) {
						player.startConversation(new Dialogue().addPlayer(HeadE.SKEPTICAL_THINKING, "Maybe that wasn't so wise."));
					}
					stage++;
				}
			}, 0, 0);
		});
		
		create();
	}

	public static NPCClickHandler handleMuncher = new NPCClickHandler(2329) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Muncher07D(e.getPlayer(), e.getNPC()));
		}
	};
}
