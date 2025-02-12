package com.rs.game.player.content.world.regions;

import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Desert  {
	
	private enum CarpetLocation {
		SHANTAY_PASS(2291, new WorldTile(3308, 3109, 0)),
		BEDABIN_CAMP(2292, new WorldTile(3180, 3045, 0)),
		S_POLLNIVNEACH(2293, new WorldTile(3351, 2942, 0)),
		N_POLLNIVNEACH(2294, new WorldTile(3349, 3003, 0)),
		UZER(2295, new WorldTile(3469, 3113, 0)),
		SOPHANEM(2297, new WorldTile(3285, 2813, 0)),
		MENAPHOS(2299, new WorldTile(3245, 2813, 0)),
		NARDAH(3020, new WorldTile(3401, 2916, 0)),
		MONKEY_COLONY(13237, new WorldTile(3227, 2988, 0));
		
		private int npcId;
		private WorldTile tile;
		
		public static CarpetLocation forId(int npcId) {
			for (CarpetLocation loc : CarpetLocation.values())
				if (loc.npcId == npcId)
					return loc;
			return null;
		}
		
		private CarpetLocation(int npcId, WorldTile tile) {
			this.npcId = npcId;
			this.tile = tile;
		}
	}
	
	public static NPCClickHandler handleCarpetMerchants = new NPCClickHandler(2291, 2292, 2293, 2294, 2295, 2297, 2299, 3020, 13237) {
		@Override
		public void handle(NPCClickEvent e) {
			CarpetLocation loc = CarpetLocation.forId(e.getNPC().getId());
			switch(loc) {
			case SHANTAY_PASS:
				e.getPlayer().sendOptionDialogue("Where would you like to travel?", new String[] { "Pollnivneach", "Bedabin Camp", "Uzer", "Monkey Colony", "Nevermind" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(CarpetLocation.N_POLLNIVNEACH.tile);
						else if (option == 2)
							player.setNextWorldTile(CarpetLocation.BEDABIN_CAMP.tile);
						else if (option == 3)
							player.setNextWorldTile(CarpetLocation.UZER.tile);
						else if (option == 4)
							player.setNextWorldTile(CarpetLocation.MONKEY_COLONY.tile);
					}
				});
				break;
			case N_POLLNIVNEACH:
			case UZER:
			case BEDABIN_CAMP:
			case MONKEY_COLONY:
				e.getPlayer().sendOptionDialogue("Where would you like to travel?", new String[] { "Shantay Pass", "Nevermind" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(CarpetLocation.SHANTAY_PASS.tile);
					}
				});
				break;
			case S_POLLNIVNEACH:
				e.getPlayer().sendOptionDialogue("Where would you like to travel?", new String[] { "Nardah", "Sophanem", "Menaphos", "Nevermind" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(CarpetLocation.NARDAH.tile);
						else if (option == 2)
							player.setNextWorldTile(CarpetLocation.SOPHANEM.tile);
						else if (option == 3)
							player.setNextWorldTile(CarpetLocation.MENAPHOS.tile);
					}
				});
				break;
			case NARDAH:
			case SOPHANEM:
			case MENAPHOS:
				e.getPlayer().sendOptionDialogue("Where would you like to travel?", new String[] { "Pollnivneach", "Nevermind" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(CarpetLocation.S_POLLNIVNEACH.tile);
					}
				});
				break;
			default:
				break;
			}
		}
	};
	
	public static ObjectClickHandler handleSpiritWaterfall = new ObjectClickHandler(new Object[] { 10417, 63173 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 63173)
				e.getPlayer().useStairs(new WorldTile(3348, 9535, 0));
			else
				e.getPlayer().useStairs(new WorldTile(3370, 3129, 0));
		}
	};
	
	public static LoginHandler unlockMonkeyColonyRugMerchant = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			e.getPlayer().getVars().setVarBit(8628, 1);
			e.getPlayer().getVars().setVarBit(8628, 1);
			e.getPlayer().getVars().setVarBit(8628, 1);
			e.getPlayer().getVars().setVarBit(395, 1); //unlock menaphos rug
		}
	};
	
	public static ObjectClickHandler handleCurtainDoors = new ObjectClickHandler(new Object[] { 1528 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getRotation() == 2 || e.getObject().getRotation() == 0) {
				e.getPlayer().walkOneStep(e.getPlayer().getX() > e.getObject().getX() ? -1 : 1, 0, false);
			} else {
				e.getPlayer().walkOneStep(0, e.getPlayer().getY() == e.getObject().getY() ? -1 : 1, false);
			}
		}
	};
	
	public static ObjectClickHandler handleEnterTTMine = new ObjectClickHandler(new Object[] { 2675, 2676 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(new WorldTile(3279, 9427, 0));
		}
	};
	
	public static ObjectClickHandler handleExitTTMine = new ObjectClickHandler(new Object[] { 2690, 2691 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(new WorldTile(3301, 3036, 0));
		}
	};
	
	public static NPCClickHandler handleBanditBartender = new NPCClickHandler(1921) {
		@Override
		public void handle(NPCClickEvent e) {
			ShopsHandler.openShop(e.getPlayer(), "the_big_heist_lodge");
		}
	};
	
	public static ObjectClickHandler handlePyramidBackEntrance = new ObjectClickHandler(new Object[] { 6481 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3233, 9310, 0));
		}
	};
	
	public static ObjectClickHandler handlePyramidSarcophagi = new ObjectClickHandler(new Object[] { 6516 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().isAt(3233, 9309)) {
				e.getPlayer().setNextWorldTile(new WorldTile(3233, 2887, 0));
			} else {
				e.getPlayer().sendMessage("You search the sarcophagus but find nothing.");
			}
		}
	};
}
