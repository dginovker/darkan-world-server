package com.rs.game.player.content.dialogue.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.statements.NPCStatement;
import com.rs.game.player.content.dialogue.statements.OptionStatement;
import com.rs.game.player.content.dialogue.statements.PlayerStatement;
import com.rs.game.player.content.dialogue.statements.SimpleStatement;
import com.rs.lib.Constants;

public class OsmanD extends Conversation {
		
	public OsmanD(Player player, int npcId) {
		super(player);

		if (player.getInventory().containsOneItem(10848, 10849, 10850, 10851)) {
			addNext(new PlayerStatement(HeadE.CHEERFUL, "I have some sq'irk juice for you."));
			addNext(new Dialogue(new SimpleStatement("Osman imparts some Thieving advice to you as a reward for the sq'irk juice."), () -> {
				int totalXp = player.getInventory().getAmountOf(10851) * 350;
				totalXp += player.getInventory().getAmountOf(10848) * 1350;
				totalXp += player.getInventory().getAmountOf(10850) * 2350;
				totalXp += player.getInventory().getAmountOf(10849) * 3000;
				player.getInventory().deleteItem(10848, Integer.MAX_VALUE);
				player.getInventory().deleteItem(10849, Integer.MAX_VALUE);
				player.getInventory().deleteItem(10850, Integer.MAX_VALUE);
				player.getInventory().deleteItem(10851, Integer.MAX_VALUE);
				player.getSkills().addXp(Constants.THIEVING, totalXp);
			}));
		} else {
			addNext(new PlayerStatement(HeadE.CHEERFUL, "Hi, I'd like to talk about sq'irks."));
			addNext(new NPCStatement(npcId, HeadE.CHEERFUL, "Alright, what would you like to know about sq'irks?"));
			addNext("SqOp", new OptionStatement("What would you like to know?", "Where can I find sq'irks?", "Why can't you get the sq'irks yourself?", "How should I squeeze the fruit?", "Is there a reward for getting these sq'irks?", "What's so good about sq'irk juice then?"));
			
			getStage("SqOp")
			.addNext(new PlayerStatement(HeadE.CONFUSED, "Where can I find sq'irks?"))
			.addNext(new NPCStatement(npcId, HeadE.FRUSTRATED, "There is a sorceress near the south eastern edge of Al Kharid who grows them. Once upon a time we considered each other friends."))
			.addNext(new PlayerStatement(HeadE.CONFUSED, "What happened?"))
			.addNext(new NPCStatement(npcId, HeadE.FRUSTRATED, "We fell out, and now she won't give me any more fruit."))
			.addNext(new PlayerStatement(HeadE.CONFUSED, "So all I have to do is ask her for some fruit for you?"))
			.addNext(new NPCStatement(npcId, HeadE.NERVOUS, "I doubt it will be that easy. She is not renowned for her generosity and is very secretive about her garden's location."))
			.addNext(new PlayerStatement(HeadE.LAUGH, "Oh come on, it should be easy enough to find!"))
			.addNext(new NPCStatement(npcId, HeadE.SHAKING_HEAD, "Her garden has remained hidden even to me - the chief spy of Al Kharid. I belive her garden must be hidden by magical means."))
			.addNext(new PlayerStatement(HeadE.CHEERFUL, "This should be an interesting task. How many sq'irks do you want?"))
			.addNext(new NPCStatement(npcId, HeadE.CHEERFUL, "I'll reward you as many as you can get your hands on but could you please squeeze the fruit into a glass first?"))
			.addNext(getStage("SqOp"));
			
			getStage("SqOp")
			.addNext(new PlayerStatement(HeadE.CONFUSED, "Why can't you get the sq'irks yourself?"))
			.addNext(new NPCStatement(npcId, HeadE.FRUSTRATED, "I may have mentioned that I had a falling out with Sorceress. Well, unsurprisingly, she refuses to give me any more of her garden's produce."))
			.addNext(getStage("SqOp"));
			
			getStage("SqOp")
			.addNext(new PlayerStatement(HeadE.CONFUSED, "How should I squeeze the fruit?"))
			.addNext(new NPCStatement(npcId, HeadE.CHEERFUL, "Use a pestle and mortal to squeeze the sq'irks. Make sure you have an empty glass with you to collect the juice."))
			.addNext(getStage("SqOp"));
			
			getStage("SqOp")
			.addNext(new PlayerStatement(HeadE.CONFUSED, "Is there a reward for getting these sq'irks?"))
			.addNext(new NPCStatement(npcId, HeadE.LAUGH, "Of course there is. I am a generous man. I'll teach you the art of Thieving for your troubles."))
			.addNext(new PlayerStatement(HeadE.CONFUSED, "How much training will you give?"))
			.addNext(new NPCStatement(npcId, HeadE.CHEERFUL, "That depends on the quantity and ripeness of the sq'irks you put into the juice."))
			.addNext(getStage("SqOp"));
			
			getStage("SqOp")
			.addNext(new PlayerStatement(HeadE.CONFUSED, "What's so good about sq'irk juice then?"))
			.addNext(new NPCStatement(npcId, HeadE.LAUGH, "Ah it's sweet, sweet nectar for a thief or spy; it makes light fingers lighter, fleet fleet flightier and comes in four different colours for those who are easily amused."))
			.addNext(new SimpleStatement("Osman starts salivating at the thought of sq'irk juice."))
			.addNext(new PlayerStatement(HeadE.SKEPTICAL, "It wouldn't have any addictive properties, would it?"))
			.addNext(new NPCStatement(npcId, HeadE.CHEERFUL, "It only holds power over those with poor self-control, something which I have an abundance of."))
			.addNext(new PlayerStatement(HeadE.SKEPTICAL, "I see."))
			.addNext(getStage("SqOp"));
		}
		
		create();
	}

}
