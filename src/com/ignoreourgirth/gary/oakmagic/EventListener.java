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
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakcorelib.StringFormats;
import com.ignoreourgirth.gary.oakmagic.spellTypes.Spell;

public class EventListener implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player eventPlayer = event.getPlayer();
		if (OakMagic.tracker.tasks.containsKey(eventPlayer)) {
			CastTimerTask castDelayEvent = OakMagic.tracker.tasks.get(eventPlayer);
			Location oldLocation = castDelayEvent.getLocation();
			boolean playerMoved = (
					 Math.abs(event.getTo().getX() - oldLocation.getX()) > 0.2 || 
				     Math.abs(event.getTo().getY() - oldLocation.getY()) > 0.2 ||
					 Math.abs(event.getTo().getZ() - oldLocation.getZ()) > 0.2 );
			if (playerMoved) {
				if (castDelayEvent.isCancelable()) {
					castDelayEvent.setCanceled(true);
					OakMagic.tracker.tasks.remove(eventPlayer);
					Chat.showCastInterruptMessage(eventPlayer);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player eventPlayer = event.getPlayer();
		if (OakMagic.tracker.tasks.containsKey(eventPlayer)) {
			CastTimerTask castDelayEvent = OakMagic.tracker.tasks.get(eventPlayer);
			if (castDelayEvent.isCancelable()) {
				castDelayEvent.setCanceled(true);
				OakMagic.tracker.tasks.remove(eventPlayer);
				Chat.showCastInterruptMessage(eventPlayer);
			}
		} else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)  {
			ItemStack stack = event.getPlayer().getItemInHand();
			if (stack.getType() == Material.MAP) {
				int id = stack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
				if (id > 0) {
					boolean isLearnable = (stack.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK) > 0);
					int level = stack.getEnchantmentLevel(Enchantment.DIG_SPEED);
					if (level == 0) level = 1;
					if (OakMagic.tracker.spellsByID.containsKey(id)) {
						Spell spell = OakMagic.tracker.spellsByID.get(id);
						if (!spell.playerKnowsSpell(eventPlayer, level)) {
							try {
								PreparedStatement statement = OakCoreLib.getDB().prepareStatement("INSERT INTO oakmagic_players(SpellID, PlayerName, SpellLevel, TempScroll) VALUES(?, ?, ?, ?)");
								statement.setInt(1, id);
								statement.setString(2, eventPlayer.getName());
								statement.setInt(3, level);
								statement.setBoolean(4, !isLearnable);
								statement.executeUpdate();
								statement.close();
							} catch (SQLException ex) {
								OakMagic.log.log(Level.SEVERE, ex.getMessage());
							}
							if (isLearnable) {
								eventPlayer.sendMessage("§2You learned the spell " + "§6" + spell.getSpellName() + " " + StringFormats.toRomanNumeral(level) + "§2.");
							} else {
								eventPlayer.sendMessage("§2You may now cast this spell once: " + "§6" + spell.getSpellName() + " " + StringFormats.toRomanNumeral(level) + "§2.");
							}
							eventPlayer.getInventory().removeItem(eventPlayer.getInventory().getItemInHand());
						} else {
							eventPlayer.sendMessage("§4You already know this spell.");
						}
					} else {
						eventPlayer.sendMessage("§4You don't understand this scroll.");
					}
				}
			} else if (stack.getType() == Material.STICK) {
				int id = stack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
				if (id > 0) {
					int level = stack.getEnchantmentLevel(Enchantment.DIG_SPEED);
					if (OakMagic.tracker.spellsByID.containsKey(id)) {
						Entity nullEntity = null;
						Spell spell = OakMagic.tracker.spellsByID.get(id);
						if (spell.playerKnowsSpell(eventPlayer, level)) {
							if (spell.isValid(event.getPlayer(), nullEntity, level)) spell.cast(eventPlayer, nullEntity, level);
						}
					} else {
						eventPlayer.sendMessage("§4You don't understand how to use this wand.");
						
					}
				}
			}
		}  else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)  {
			ItemStack stack = event.getPlayer().getItemInHand(); 
			if (stack.getType() == Material.STICK) {
				int id = stack.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
				if (id > 0) {
					int level = stack.getEnchantmentLevel(Enchantment.DIG_SPEED);
					if (OakMagic.tracker.spellsByID.containsKey(id)) {
						Spell spell = OakMagic.tracker.spellsByID.get(id);
						if (spell.playerKnowsSpell(eventPlayer, level)) {
							if (spell.isValid(event.getPlayer(), event.getPlayer(), level)) spell.cast(event.getPlayer(), event.getPlayer(), level);
						}
					} else {
						eventPlayer.sendMessage("§4You don't understand how to use this wand.");
						
					}
				}
			 }
		}
		if (Meditation.isMeditating(eventPlayer)) Meditation.cancel(eventPlayer);
	}
	
	
}
