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
package com.ignoreourgirth.gary.oakmagic.spells;

import java.util.Hashtable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoreourgirth.gary.oakmagic.OakMagic;
import com.ignoureourgirth.gary.oakmagic.spellTypes.ToggleableSpell;

public class Hide extends ToggleableSpell{

	private Hashtable<Player, Location> startLocations;
	
	public Hide(int id) {
		super(id, 1);
		startLocations = new Hashtable<Player, Location>();
	}

	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		return true;
	}
	
	@Override
	protected void toggleOn(Player caster) {
		startLocations.put(caster, caster.getLocation());
		Chat.showNonCasterMessage(caster,"§7" + caster.getName() + " disappeared.");
		caster.sendMessage("§7You are now hidden.");
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
        {
			onlinePlayer.hidePlayer(caster);
        }
	}

	@Override
	protected void toggleOff(Player caster) {
		startLocations.remove(caster);
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
        {
			onlinePlayer.showPlayer(caster);
        }
		caster.sendMessage("§7Hide canceled. You are visible");
	}
	
	
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event) {
		Player eventPlayer = event.getPlayer();
		for (Player player: OakMagic.server.getOnlinePlayers())
        {
			if (isActiveFor(player)) eventPlayer.hidePlayer(player);
        }
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (isActiveFor(event.getPlayer())) {
			Location oldLocation = startLocations.get(event.getPlayer());
			boolean playerMoved = 
					(event.getTo().getX() > oldLocation.getX() + 0.3 ) || (event.getTo().getX() < oldLocation.getX() - 0.3 ) ||
					(event.getTo().getY() > oldLocation.getY() + 0.3 ) || (event.getTo().getY() < oldLocation.getY() - 0.3 ) ||
					(event.getTo().getZ() > oldLocation.getZ() + 0.3 ) || (event.getTo().getZ() < oldLocation.getZ() - 0.3 );
			if (playerMoved) forceOffForPlayer(event.getPlayer());
		}
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (isActiveFor(event.getPlayer())) forceOffForPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (isActiveFor(event.getPlayer())) forceOffForPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (isActiveFor(event.getPlayer())) forceOffForPlayer(event.getPlayer());
	}

	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
	
}
