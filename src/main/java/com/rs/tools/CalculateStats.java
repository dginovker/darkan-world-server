package com.rs.tools;

import java.util.Arrays;

import com.rs.game.npc.combat.NPCCombatDefinitions;

public class CalculateStats {
	
	public static void main(String[] args) {
		calculate(1001, 3000);
		calculate(785, 2000);
		calculate(280, 250);
		calculate(69, 88);
	}
	
	public static void calculate(int combat, int hp) {
		int[] levels = generateLevels(combat, hp);
		
		System.out.println("Accuracy: " + (getCombatLevel(levels[0], levels[1], levels[2], levels[3], levels[4], hp)-combat));
		System.out.println(Arrays.toString(levels));
	}
	
	public static int[] generateLevels(int combat, int hp) {
		int[] levels = new int[5];
		int def = (int) (((combat/1.32 + 1) / 0.25) - hp);
		combat -= (int) ((def + hp) * 0.25) + 1;
		int off = (int) ((combat/0.325)/1.5);
		
		int avg = (def+off)/2;
		
		levels[NPCCombatDefinitions.DEFENSE] = avg;
		levels[NPCCombatDefinitions.ATTACK] = avg;
		levels[NPCCombatDefinitions.RANGE] = avg;
		levels[NPCCombatDefinitions.MAGIC] = avg;
		return levels;
	}
	
	public static int getCombatLevel(int attack, int defense, int strength, int range, int magic, int hp) {
		int combatLevel = 3;
		combatLevel = (int) ((defense + hp) * 0.25) + 1;
		double melee = (attack + strength) * 0.325;
		double ranger = Math.floor(range * 1.5) * 0.325;
		double mage = Math.floor(magic * 1.5) * 0.325;
		if (melee >= ranger && melee >= mage) {
			combatLevel += melee;
		} else if (ranger >= melee && ranger >= mage) {
			combatLevel += ranger;
		} else if (mage >= melee && mage >= ranger) {
			combatLevel += mage;
		}
		return combatLevel;
	}

}
