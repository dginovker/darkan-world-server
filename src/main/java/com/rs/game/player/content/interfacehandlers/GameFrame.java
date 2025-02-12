package com.rs.game.player.content.interfacehandlers;

import com.rs.game.player.actions.Rest;
import com.rs.game.player.managers.InterfaceManager;
import com.rs.game.player.managers.InterfaceManager.Tab;
import com.rs.lib.net.ClientPacket;
import com.rs.net.LobbyCommunicator;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.ReportsManager;

@PluginEventHandler
public class GameFrame {
	
	public static ButtonClickHandler handlePrayerOrb = new ButtonClickHandler(749) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 4) {
				if (e.getPacket() == ClientPacket.IF_OP1) // activate
					e.getPlayer().getPrayer().switchQuickPrayers();
				else if (e.getPacket() == ClientPacket.IF_OP2) // switch
					e.getPlayer().getPrayer().switchSettingQuickPrayer();
			}
		}
	};
	
	public static ButtonClickHandler handleRunOrb = new ButtonClickHandler(750) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 4) {
				if (e.getPacket() == ClientPacket.IF_OP1) {
					e.getPlayer().toggleRun(e.getPlayer().isResting() ? false : true);
					if (e.getPlayer().isResting())
						e.getPlayer().stopAll();
				} else if (e.getPacket() == ClientPacket.IF_OP2) {
					if (e.getPlayer().isResting()) {
						e.getPlayer().stopAll();
						return;
					}
					if (e.getPlayer().getEmotesManager().isAnimating()) {
						e.getPlayer().sendMessage("You can't rest while perfoming an emote.");
						return;
					}
					if (e.getPlayer().isLocked()) {
						e.getPlayer().sendMessage("You can't rest while perfoming an action.");
						return;
					}
					e.getPlayer().stopAll();
					e.getPlayer().getActionManager().setAction(new Rest());
				}
			}
		}
	};
	
	public static ButtonClickHandler handleAudioSettingsTab = new ButtonClickHandler(429) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 18)
				e.getPlayer().getInterfaceManager().sendTab(Tab.SETTINGS);
		}
	};

	public static ButtonClickHandler handleChatSettings = new ButtonClickHandler(982) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 5)
				e.getPlayer().getInterfaceManager().sendTab(Tab.SETTINGS);
			else if (e.getComponentId() == 41)
				e.getPlayer().setPrivateChatSetup(e.getPlayer().getPrivateChatSetup() == 0 ? 1 : 0);
			else if (e.getComponentId() >= 17 && e.getComponentId() <= 36) {
				e.getPlayer().setClanChatSetup(e.getComponentId() - 17);
			} else if (e.getComponentId() >= 97 && e.getComponentId() <= 116)
				e.getPlayer().setGuestChatSetup(e.getComponentId() - 97);
			else if (e.getComponentId() >= 49 && e.getComponentId() <= 66)
				e.getPlayer().setPrivateChatSetup(e.getComponentId() - 48);
			else if (e.getComponentId() >= 72 && e.getComponentId() <= 91)
				e.getPlayer().setFriendChatSetup(e.getComponentId() - 72);
		}
	};

	public static ButtonClickHandler handleSettingsTab = new ButtonClickHandler(261) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getInterfaceManager().containsInventoryInter())
				return;
			if (e.getComponentId() == 22) {
				if (e.getPlayer().getInterfaceManager().containsScreenInter()) {
					e.getPlayer().sendMessage("Please close the interface you have open before setting your graphic options.");
					return;
				}
				e.getPlayer().stopAll();
				e.getPlayer().getInterfaceManager().sendInterface(742);
			} else if (e.getComponentId() == 12)
				e.getPlayer().switchAllowChatEffects();
			else if (e.getComponentId() == 13) { // chat setup
				e.getPlayer().getInterfaceManager().sendTab(Tab.SETTINGS, 982);
			} else if (e.getComponentId() == 14)
				e.getPlayer().switchMouseButtons();
			else if (e.getComponentId() == 24) // audio options
				e.getPlayer().getInterfaceManager().sendTab(Tab.SETTINGS, 429);
			else if (e.getComponentId() == 16) // house options
				e.getPlayer().getInterfaceManager().sendTab(Tab.SETTINGS, 398);
		}
	};
	
	public static ButtonClickHandler handleChatboxGameBar = new ButtonClickHandler(751) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 14)
				ReportsManager.report(e.getPlayer());
			if (e.getComponentId() == 23) {
				if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().setClanStatus(0);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					e.getPlayer().setClanStatus(1);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().setClanStatus(2);
			} else if (e.getComponentId() == 32) {
				if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().setFilterGame(false);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().setFilterGame(true);
			} else if (e.getComponentId() == 0) {
				if (e.getPacket() == ClientPacket.IF_OP2) {
					e.getPlayer().getSocial().setFcStatus(0);
					LobbyCommunicator.updateAccount(e.getPlayer());
				} else if (e.getPacket() == ClientPacket.IF_OP3) {
					e.getPlayer().getSocial().setFcStatus(1);
					LobbyCommunicator.updateAccount(e.getPlayer());
				} else if (e.getPacket() == ClientPacket.IF_OP4) {
					e.getPlayer().getSocial().setFcStatus(2);
					LobbyCommunicator.updateAccount(e.getPlayer());
				}
			} else if (e.getComponentId() == 23) {
				if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().setClanStatus(0);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					e.getPlayer().setClanStatus(1);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().setClanStatus(2);
			} else if (e.getComponentId() == 17) {
				if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().setAssistStatus(0);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					e.getPlayer().setAssistStatus(1);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().setAssistStatus(2);
				else if (e.getPacket() == ClientPacket.IF_OP6) {
					// ASSIST XP Earned/Time
				}
			}
		}
	};
	
	public static ButtonClickHandler handleWorldMap = new ButtonClickHandler(755) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 44)
				e.getPlayer().getInterfaceManager().setWindowsPane(e.getPlayer().getInterfaceManager().hasRezizableScreen() ? 746 : 548);
			else if (e.getComponentId() == 42) {
				e.getPlayer().getHintIconsManager().removeAll(); //TODO find hintIcon index
				e.getPlayer().getVars().setVar(1159, 1);
			}
		}
	};
	
	public static ButtonClickHandler handleButtons = new ButtonClickHandler(InterfaceManager.FIXED_TOP, InterfaceManager.RESIZEABLE_TOP) {
		@Override
		public void handle(ButtonClickEvent e) {
			if ((e.getInterfaceId() == 548 && e.getComponentId() == 167) || (e.getInterfaceId() == 746 && e.getComponentId() == 208)) {
				if (e.getPlayer().getInterfaceManager().containsScreenInter() || e.getPlayer().inCombat(10000)) {
					e.getPlayer().sendMessage("Please finish what you're doing before opening the price checker.");
					return;
				}
				e.getPlayer().stopAll();
				e.getPlayer().getPriceCheckManager().openPriceCheck();
				return;
			}
			if ((e.getInterfaceId() == 548 && e.getComponentId() == 157) || (e.getInterfaceId() == 746 && e.getComponentId() == 200)) {
				if (e.getPacket() == ClientPacket.IF_OP2) {
					e.getPlayer().getHintIconsManager().removeAll();
					e.getPlayer().getVars().setVar(1159, 1);
					return;
				}
				if (e.getPlayer().getInterfaceManager().containsScreenInter() || e.getPlayer().getInterfaceManager().containsInventoryInter() || e.getPlayer().inCombat(10000)) {
					e.getPlayer().sendMessage("Please finish what you're doing before opening the world map.");
					return;
				}
				e.getPlayer().getInterfaceManager().setTopInterface(755, false);
				int posHash = e.getPlayer().getX() << 14 | e.getPlayer().getY();
				e.getPlayer().getPackets().sendVarc(622, posHash); // map open center pos
				e.getPlayer().getPackets().sendVarc(674, posHash); // player position
			} else if ((e.getInterfaceId() == 548 && e.getComponentId() == 35) || (e.getInterfaceId() == 746 && e.getComponentId() == 55)) {
				if (e.getPacket() == ClientPacket.IF_OP1)
					e.getPlayer().getSkills().switchXPDisplay();
				else if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().getSkills().switchXPPopup();
				else if (e.getPacket() == ClientPacket.IF_OP3)
					e.getPlayer().getSkills().setupXPCounter();
			} else if ((e.getInterfaceId() == 746 && e.getComponentId() == 207) || (e.getInterfaceId() == 548 && e.getComponentId() == 159)) {
				if (e.getPacket() == ClientPacket.IF_OP4) {
					if (e.getPlayer().getInterfaceManager().containsScreenInter()) {
						e.getPlayer().sendMessage("Please finish what you're doing before opening the price checker.");
						return;
					}
					e.getPlayer().stopAll();
					e.getPlayer().getPriceCheckManager().openPriceCheck();
				}
			}
		}
	};
	
	public static ButtonClickHandler handleAudioOptionsClose = new ButtonClickHandler(743) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 20)
				e.getPlayer().stopAll();
		}
	};
	
	public static ButtonClickHandler handleGraphicsSettingsClose = new ButtonClickHandler(742) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 46)
				e.getPlayer().stopAll();
		}
	};
}
