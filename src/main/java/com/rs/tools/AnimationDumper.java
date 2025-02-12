package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.BASDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.SpotAnimDefinitions;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.lib.util.Utils;

public class AnimationDumper {
	
	public static void main(String[] args) throws IOException {
		Cache.init(Settings.getConfig().getCachePath());
		
		File file = new File("animationDump.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		
		File file2 = new File("unidAnimSkeles.txt");
		if (file2.exists())
			file2.delete();
		else
			file2.createNewFile();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 727\n");
		writer.flush();
		
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2));
		writer2.append("//Version = 727\n");
		writer2.flush();
		
		Map<Integer, String> used = new HashMap<>();
		for (int npcId = 0;npcId < Utils.getNPCDefinitionsSize();npcId++) {
			NPCDefinitions npcDef = NPCDefinitions.getDefs(npcId);
			if (npcDef.basId != -1) {
				if (BASDefinitions.getDefs(npcDef.basId).standAnimation != -1) {
					if (AnimationDefinitions.getDefs(BASDefinitions.getDefs(npcDef.basId).standAnimation).frameSetIds != null) {
						int skeleton = AnimationDefinitions.getDefs(BASDefinitions.getDefs(npcDef.basId).standAnimation).frameSetIds[0];
						for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
							AnimationDefinitions check = AnimationDefinitions.getDefs(i);
							if (check == null)
								continue;
							if (check.frameSetIds == null || check.frameSetIds[0] == -1)
								continue;
							if (check.frameSetIds[0] == skeleton) {
								if (used.get(i) != null)
									used.put(i, used.get(i) + ", " + npcId + " (" + npcDef.getName() + ")" );
								else
									used.put(i, "["+AnimationDefinitions.getDefs(i).frameSetIds[0] + "] " + npcId + " (" + npcDef.getName() + ")");
								if (skeleton == 2183 || skeleton == 1843)
									used.put(i, "["+AnimationDefinitions.getDefs(i).frameSetIds[0] + "] Player animation/Humanoid");
							}
						}
					}
				}
			}
		}
		
		for (int spotAnimId = 0;spotAnimId < Utils.getSpotAnimDefinitionsSize();spotAnimId++) {
			SpotAnimDefinitions defs = SpotAnimDefinitions.getDefs(spotAnimId);
			if (defs.animationId != -1) {
				if (used.get(defs.animationId) != null)
					used.put(defs.animationId, used.get(defs.animationId) + ", SpotAnim: " + spotAnimId );
				else
					used.put(defs.animationId, "["+AnimationDefinitions.getDefs(defs.animationId).frameSetIds[0] + "] SpotAnim: " + spotAnimId);
			}
		}
		
		for (int objectId = 0;objectId < Utils.getObjectDefinitionsSize();objectId++) {
			ObjectDefinitions defs = ObjectDefinitions.getDefs(objectId);
			if (defs.animations != null) {
				for (int animId : defs.animations) {
					if (used.get(animId) != null)
						used.put(animId, used.get(animId) + ", Object: " + objectId );
					else
						used.put(animId, "["+AnimationDefinitions.getDefs(animId).frameSetIds[0] + "] Object: " + objectId);
				}
			}
		}
		
		Map<Integer, Integer> UNIDS = new HashMap<>();
		
		for (int i = 0; i < Utils.getAnimationDefinitionsSize(); i++) {
			String name = used.get(i);
			if (name == null) {
				switch(AnimationDefinitions.getDefs(i).frameSetIds[0]) {
				case 2409:
				case 2410:
				case 2411:
				case 2414:
				case 2417:
				case 2418:
				case 2421:
				case 2429:
				case 2420:
					name = "Humanoid chathead";
					break;
				case 3102:
					name = "Troll/Tzhaar chathead";
					break;
				case 1815:
					name = "Child chathead";
					break;
				case 1981:
					name = "Werewolf/Cat chathead";
					break;
				case 1459:
					name = "Penguin chathead";
					break;
				case 81:
				case 83:
					name = "Old NPC chathead";
					break;
				default:
					UNIDS.put(AnimationDefinitions.getDefs(i).frameSetIds[0], i);
					name = "[" + AnimationDefinitions.getDefs(i).frameSetIds[0] + "] Unknown";
					break;
				}
			}
			
			writer.append(i + ": " + name);
			writer.newLine();
			writer.flush();
		}
		
		List<Integer> keys = new ArrayList<>(UNIDS.keySet());
		Collections.sort(keys);
		for (int skele : keys) {
			writer2.append(UNIDS.get(skele) + ",");
			writer2.newLine();
			writer2.flush();
		}
		
		writer.close();
		writer2.close();
	}

}
