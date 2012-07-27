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

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoreourgirth.gary.oakmagic.OakMagic;
import com.ignoreourgirth.gary.oakmagic.spellTypes.Spell;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class Gate extends Spell{

	public Gate(int id) {
		super(id);
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(caster.getName());
			if (resident.hasTown()) {
				if (resident.getTown().hasSpawn()) {
					return true;
				} else {
					super.showValidationFailure(caster, "Your town must have a spawn set to use this spell.");
				}
			} else {
				super.showValidationFailure(caster, "You must be a resident of a town to use this spell.");
			}
		} catch (NotRegisteredException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
		return false;
	}

	@Override
	public void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		try {
			HashSet<Player> playersToTOP = new HashSet<Player>();
			playersToTOP.add(caster);
			Location locationFrom = caster.getLocation();
			Resident resident = TownyUniverse.getDataSource().getResident(caster.getName());
			Town town = resident.getTown();
			Location townSpawnLocation = town.getSpawn();
			townSpawnLocation.add(new Location(townSpawnLocation.getWorld(), 0, 0, 0, townSpawnLocation.getYaw() * -1, townSpawnLocation.getPitch() * -1));
			Chunk townChunk = townSpawnLocation.getChunk();
			if (!townChunk.isLoaded()) {townChunk.load();}
			if (spellLevel == 2) {
				List<Entity> entityList = caster.getNearbyEntities(AOERadius, 0, AOERadius);
				for (Entity entity : entityList) {
					if (entity instanceof Player) {
						playersToTOP.add((Player) entity);
					}
				}
				for (Entity entity : entityList) {
					if (entity instanceof Player) {
						Player nextPlayer = (Player) entity;
						Chat.broadcastMessage(nextPlayer ,"§7" + nextPlayer .getName() + " gated away.", playersToTOP);
						Location nextFromLocation = nextPlayer.getLocation();
						if (nextPlayer.getVehicle() != null) nextPlayer.getVehicle().eject();
						Vector distanceVector = new Vector(nextFromLocation.getX() - locationFrom.getX() , 0, nextFromLocation.getZ() - locationFrom.getZ());
						Location relativeLocation = townSpawnLocation.clone().add(distanceVector);
						Location finalLocation = new Location(relativeLocation.getWorld(), relativeLocation.getX(), relativeLocation.getY(), relativeLocation.getZ(), nextFromLocation.getYaw(), nextFromLocation.getPitch());
						nextPlayer.teleport(finalLocation);
						finalLocation.getWorld().playEffect(finalLocation, Effect.CLICK1, 0);
						finalLocation.getWorld().playEffect(finalLocation, Effect.MOBSPAWNER_FLAMES, 1);
						nextFromLocation.getWorld().playEffect(nextFromLocation, Effect.MOBSPAWNER_FLAMES, 1);
						Chat.broadcastMessage(nextPlayer ,"§7" + nextPlayer.getName() + " has gated in.", playersToTOP);
						nextPlayer.sendMessage("§2You have been gated to " + town.getName() + ".");;
					}
				}
			}
			
			if (caster.getVehicle() != null) caster.getVehicle().eject();
			Chat.broadcastMessage(caster,"§7" + caster.getName() + " gated away.", playersToTOP);
			Location finalLocation = new Location(townSpawnLocation.getWorld(), townSpawnLocation.getX(), townSpawnLocation.getY(), townSpawnLocation.getZ(), locationFrom.getYaw(), locationFrom.getPitch());
			caster.teleport(finalLocation);
			finalLocation.getWorld().playEffect(finalLocation, Effect.CLICK1, 0);
			finalLocation.getWorld().playEffect(finalLocation, Effect.MOBSPAWNER_FLAMES, 1);
			locationFrom.getWorld().playEffect(locationFrom, Effect.MOBSPAWNER_FLAMES, 1);
			caster.sendMessage("§2You have gated to " + town.getName() + ".");
			Chat.broadcastMessage(caster ,"§7" + caster.getName() + " has gated in.", playersToTOP);
			
		} catch (NotRegisteredException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		} catch (TownyException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
	}
	
	private final int AOERadius = 8;
	private final Effect animationEffect = Effect.ENDER_SIGNAL;
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (spellLevel == 2) {
			int endEffect = Math.round(AOERadius/2);
			int startEffect = endEffect * -1;
			World casterWorld = caster.getWorld();
			Location casterLocation =  caster.getLocation().add(new Vector(0, .3, 0));
			for (int x = startEffect; x <= endEffect; x++) {
				for (int z = startEffect; z <= endEffect; z++) {
					casterWorld.playEffect(casterLocation.clone().add(new Vector(x, 0, z)), animationEffect, 0);
				}
			}	

			
		}
	}
	
}
