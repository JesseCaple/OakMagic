/*******************************************************************************
 * Copyright (c) 2012 GaryMthrfkinOak (Jesse Caple).
 * 
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.ignoreourgirth.gary.oakmagic;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakcorelib.StringFormats;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Chat {

	private static final int defaultRadius = 70;
	
	public static void showCastStartMessage(Player caster, Spell spell, int level) {
		if (level > 1) {
			caster.sendMessage("§7You begin casting: §6" + spell.getSpellName() +  " " + StringFormats.toRomanNumeral(level) + "§7 (" + spell.getCastTime() + "s)");
			showNonCasterMessage(caster, "§7" + caster.getName() + " begins casting §6" + spell.getSpellName() +  " " + StringFormats.toRomanNumeral(level) + "§7.");
		} else {
			caster.sendMessage("§7You begin casting: §6" + spell.getSpellName() + "§7 (" + spell.getCastTime() + "s)");
			showNonCasterMessage(caster, "§7" + caster.getName() + " begins casting §6" + spell.getSpellName() + "§7.");
		}
	}
	
	public static void showCastInterruptMessage(Player caster) {
		showNonCasterMessage(caster, ChatColor.GRAY + caster.getName() + "'s cast was interrupted.");
		caster.sendMessage("§4Cast interrupted.");
	}
	
	public static void showCastFinishMessage(Player caster, Spell spell) {
		showNonCasterMessage(caster, ChatColor.GRAY + caster.getName() + " has finished casting " + spell.getSpellName() + ".");
		caster.sendMessage("§7Cast complete.");
	}
	
	public static void showNonCasterMessage(Player caster, String message) {
		HashSet<Player> excludeCaster = new HashSet<Player>();
		excludeCaster.add(caster);
		broadcastMessage(caster, message, excludeCaster);
	}
	
	public static void broadcastMessage(Player centerOn, String message) {
		broadcastToNearbyPlayers(centerOn, defaultRadius, message, new HashSet<Player>());
	}
	
	public static void broadcastMessage(Player centerOn, String message, HashSet<Player> exclude) {
		broadcastToNearbyPlayers(centerOn, defaultRadius, message, exclude);
	}
	
	public static void broadcastMessage(Player centerOn, int radius, String message) {
		broadcastToNearbyPlayers(centerOn, radius, message, new HashSet<Player>());
	}
	
	public static void broadcastMessage(Player centerOn, int radius, String message, HashSet<Player> exclude) {
		broadcastToNearbyPlayers(centerOn, radius, message, exclude);
	}
	
	private static void broadcastToNearbyPlayers(Player centerOn, int distance, String message, HashSet<Player> exclude) {
        if (exclude == null) exclude = new HashSet<Player>();
        for (Entity entity : centerOn.getNearbyEntities(distance, 25, distance)) {
        	if (entity instanceof Player) {
        		Player nextPlayer = (Player) entity;
        		if (!(exclude.contains(nextPlayer))) nextPlayer.sendMessage(message);
        	}
        }
    }
}
