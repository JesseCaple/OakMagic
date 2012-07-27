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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ignoreourgirth.gary.oakcorelib.CommandPreprocessor.OnCommand;
import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakcorelib.StringFormats;
import com.ignoreourgirth.gary.oakmagic.spellTypes.Spell;

public class Commands {

	@OnCommand ("mp")
	public void showMPInformation(Player player) {
		player.sendMessage(ChatColor.GRAY + "MP: " + OakCoreLib.getXPMP().getTotalMP(player) + ChatColor.WHITE +  " / " + ChatColor.GRAY + OakCoreLib.getXPMP().getMaxMP(player));
	}
	
	@OnCommand ("meditate")
	public void meditate(Player player) {
		Meditation.start(player);
	}
	
	@OnCommand ("magiclist")
	public void listSpells(Player player) {
		try {
			int spellCount = 0;
			StringBuilder spellList = new StringBuilder();
			spellList.append("§fKnown Spells: ");
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT SpellID, SpellLevel  FROM oakmagic_players WHERE PlayerName=?");
			statement.setString(1, player.getName());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				spellCount ++;
				int nextID = result.getInt(1);
				int nextlevel = result.getInt(2);
				PreparedStatement statement2 = OakCoreLib.getDB().prepareStatement("SELECT SpellName FROM oakmagic_spells WHERE SpellID=?");
				statement2.setInt(1, nextID);
				ResultSet result2 = statement2.executeQuery();
				if (result2.next()) {
					if (spellCount > 1) spellList.append("§f, ");
					spellList.append("§a");
					if (nextlevel > 1) {
						spellList.append(result2.getString(1) + " " + StringFormats.toRomanNumeral(nextlevel));
					} else {
						spellList.append(result2.getString(1));
					}
				}
				result2.close();
				statement2.close();
			}
			result.close();
			statement.close();
			if (spellCount > 0)  {
				player.sendMessage(spellList.toString());
			} else {
				player.sendMessage("§fYou do not know any spells.");
			}
		} catch (SQLException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
	}
	
	@OnCommand ("makescroll")
	public void makeScroll(Player player, String spellName, int spellLevel, boolean isLearnable) {
		ItemStack stack = player.getItemInHand();
		spellName = spellName.toLowerCase();
		if (OakMagic.tracker.spellsByName.containsKey(spellName)) {
			Spell spell = OakMagic.tracker.spellsByName.get(spellName);
			if (stack.getType() == Material.MAP) {
				if (stack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) > 0) {
					player.sendMessage("§4This scroll is already enchanted.");
				} else {
					stack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, spell.getSpellID());
					stack.addUnsafeEnchantment(Enchantment.DIG_SPEED, spellLevel);
					if (isLearnable) {
						stack.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
						player.sendMessage("§2Made learnable spell: " + spell.getSpellName() + " " + StringFormats.toRomanNumeral(spellLevel));
					} else {
						player.sendMessage("§2Made usable scroll: " + spell.getSpellName() + " " + StringFormats.toRomanNumeral(spellLevel));
					}
				}
			} else {
				player.sendMessage("§4You must be holding a map.");
			}
		} else {
			player.sendMessage("§4A spell with that name does not exist.");
			return;
		}
	}
	
	@OnCommand (value="bind", optionals=1)
	public void enchantWand(Player player, String spellName, int spellLevel) {
		ItemStack stack = player.getItemInHand();
		if (stack.getType() == Material.STICK) {
			spellName = spellName.toLowerCase();
			if (OakMagic.tracker.spellsByName.containsKey(spellName)) {
				Spell spell = OakMagic.tracker.spellsByName.get(spellName);
				if (spell.playerKnowsSpell(player, spellLevel)) {
					stack.removeEnchantment(Enchantment.ARROW_DAMAGE);
					stack.removeEnchantment(Enchantment.DIG_SPEED);
					stack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, spell.getSpellID());
					stack.addUnsafeEnchantment(Enchantment.DIG_SPEED, spellLevel);
					if (spellLevel == 1) {
						player.sendMessage("§2Bound spell '" + spell.getSpellName() + "' to your wand.");
					} else {
						player.sendMessage("§2Bound spell '" + spell.getSpellName() + " " + StringFormats.toRomanNumeral(spellLevel) + "' to your wand.");
					}
				} else {
					player.sendMessage("§4You do not know a spell by this name.");
				}
			} else {
				player.sendMessage("§4You do not know a spell by this name.");
			}
		} else {
			player.sendMessage("§4You must be holding a wand.");
		}
	}
	
	@OnCommand (value="magic", optionals=1)
	public void magicCommand(Player player, String spellName, int spellLevel) {
		spellName = spellName.toLowerCase();
		if (OakMagic.tracker.spellsByName.containsKey(spellName)) {
			Spell spell = OakMagic.tracker.spellsByName.get(spellName);
			if (spell.playerKnowsSpell(player, spellLevel)) {
				Entity nullEntity = null;
				if (spell.isValid(player, nullEntity, spellLevel)) {
					spell.cast(player, nullEntity, spellLevel);
				}
			} else {
				player.sendMessage("§4You do not know a spell by this name.");
			}
		} else {
			player.sendMessage("§4You do not know a spell by this name.");
		}
	}
	
}
