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
package com.ignoureourgirth.gary.oakmagic.spellTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakmagic.MPDrainTask;
import com.ignoreourgirth.gary.oakmagic.OakMagic;

public abstract class ToggleableSpell extends Spell  {
	
	private int mpToDrain;
	private HashSet<Player> spellActiveForPlayers;
	private Hashtable<Player, Integer> MPDrainEventIDs;
	private HashMap<Player, Location> targetLocations;
	private HashMap<Player, Entity> targetEntities;
	private HashMap<Player, Integer> spellLevels;
	
	public int getMPDrained() {return mpToDrain;}
	protected boolean isActiveFor(Player player) {return spellActiveForPlayers.contains(player);}
	protected Location getTargetLocation(Player player) {return targetLocations.get(player);}
	protected Entity getTargetEntity(Player player) {return targetEntities.get(player);}
	protected int getSpellLevel(Player player) {return spellLevels.get(player);}
	
	public ToggleableSpell(int id, int mpDrained) {
		super(id);
		mpToDrain = mpDrained;
		MPDrainEventIDs = new Hashtable<Player, Integer>();
		targetLocations = new HashMap<Player, Location>();
		targetEntities = new HashMap<Player, Entity>();
		spellLevels = new HashMap<Player, Integer>();
		spellActiveForPlayers = new HashSet<Player> ();
	}
	
	public void forceOffForPlayer(Player player) {
		if (spellActiveForPlayers.contains(player)) {
			spellActiveForPlayers.remove(player);
			if (MPDrainEventIDs.containsKey(player)) {
				Bukkit.getScheduler().cancelTask(MPDrainEventIDs.get(player));
				MPDrainEventIDs.remove(player);
				OakCoreLib.getXPMP().allowMPRegen(player);
			}
			toggleOff(player);
			targetLocations.remove(player);
			targetEntities.remove(player);
			spellLevels.remove(player);
		}
	}
	
	@Override
	protected boolean isValid(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (!spellActiveForPlayers.contains(caster)) {
			return super.isValid(caster, targetEntity, targetLocation, spellLevel);
		} else {
			forceOffForPlayer(caster);
			return false;
		}
	}
	
	@Override
	protected void cast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (!spellActiveForPlayers.contains(caster)) {
			super.cast(caster, targetEntity, targetLocation, spellLevel);
		} else {
			forceOffForPlayer(caster);
		}
	}
	
	@Override
	public void executeSpell(Player caster, Entity targetEntity, Location targetLocation, int spellLevel, boolean mpAlreadyCalculated) {
		saveCooldown(caster);
		spellActiveForPlayers.add(caster);
		targetLocations.put(caster, targetLocation);
		targetEntities.put(caster, targetEntity);
		spellLevels.put(caster, spellLevel);
		if (mpToDrain > 0 && caster.getGameMode() != GameMode.CREATIVE) {
			int idReturned = OakMagic.server.getScheduler().scheduleSyncRepeatingTask(OakMagic.plugin, new MPDrainTask(caster, this), 20L, 20L);
			MPDrainEventIDs.put(caster, idReturned);
			OakCoreLib.getXPMP().blockMPRegen(caster);
		}
		toggleOn(caster);
		if (!mpAlreadyCalculated) OakCoreLib.getXPMP().addMP(caster, getMP() * -1);
	}
	
	@Override
	protected void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		OakMagic.log.log(Level.WARNING, "ToggleableSpell is calling final execute.");
	}
	
	protected abstract void toggleOn(Player caster);
	protected abstract void toggleOff(Player caster);
}
