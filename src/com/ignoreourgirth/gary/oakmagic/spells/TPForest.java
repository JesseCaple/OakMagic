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

import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoreourgirth.gary.oakmagic.OakMagic;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class TPForest extends Spell{

	Location tpDestination;
	
	public TPForest(int id) {
		super(id);
		tpDestination = new Location(OakMagic.server.getWorld("new_world"), 0.549200, 67.7, 281.47499);
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Chunk destinationChunk = tpDestination.getChunk();
		if (!destinationChunk.isLoaded()) {destinationChunk.load();}
		return true;
	}

	@Override
	public void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Location locationFrom = caster.getLocation();
		Chunk destinationChunk = tpDestination.getChunk();
		if (caster.getVehicle() != null) {caster.getVehicle().eject();}
		if (!destinationChunk.isLoaded()) {destinationChunk.load();}
		Chat.showNonCasterMessage(caster,"§7" + caster.getName() + " teleported away.");
		caster.teleport(tpDestination);
		caster.getLocation().getWorld().playEffect(caster.getLocation(), Effect.CLICK1, 0);
		for(int i=1; i<6; i++){
			tpDestination.getWorld().playEffect(tpDestination, Effect.MOBSPAWNER_FLAMES, 1);
			locationFrom.getWorld().playEffect(locationFrom, Effect.MOBSPAWNER_FLAMES, 1);
        }
		caster.sendMessage("§2You have teleported to the forest.");
		Chat.showNonCasterMessage(caster, "§7" + caster.getName() + " has teleported in.");
	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
