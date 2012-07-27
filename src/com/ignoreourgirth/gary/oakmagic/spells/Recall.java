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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoreourgirth.gary.oakmagic.OakMagic;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Recall extends Spell{

	public Recall(int id) {
		super(id);
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		boolean recordExists = false;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT COUNT(*) FROM oakmagic_markrecall WHERE player=?;");
			statement.setString(1, caster.getName());
			ResultSet result = statement.executeQuery();
			recordExists = result.next();
			result.close();
			statement.close();
		} catch (SQLException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
		return recordExists;
	}

	@Override
	public void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Location recalTo = null;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"SELECT world, x, y, z, yaw FROM oakmagic_markrecall WHERE player=?;");
			statement.setString(1, caster.getName());
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				World recallWorld = OakMagic.server.getWorld(result.getString(1));
				recalTo = new Location(recallWorld, result.getDouble(2), result.getDouble(3), result.getDouble(4), result.getFloat(5), 0f);
			}
			result.close();
			statement.close();
		} catch (SQLException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
		if (recalTo != null) {
			Location locationFrom = caster.getLocation();
			Chunk destinationChunk = recalTo.getChunk();
			if (caster.getVehicle() != null) {caster.getVehicle().eject();}
			if (!destinationChunk.isLoaded()) {destinationChunk.load();}
			Chat.showNonCasterMessage(caster,"§7" + caster.getName() + " teleported away.");
			caster.teleport(recalTo);
			caster.getLocation().getWorld().playEffect(caster.getLocation(), Effect.CLICK1, 0);
			recalTo.getWorld().playEffect(recalTo, Effect.MOBSPAWNER_FLAMES, 1);
			locationFrom.getWorld().playEffect(locationFrom, Effect.MOBSPAWNER_FLAMES, 1);
			caster.sendMessage(ChatColor.GRAY + "You have teleported to your marked location.");
			Chat.showNonCasterMessage(caster, ChatColor.GRAY + caster.getName() + " has teleported in.");
		} else {
			caster.sendMessage(ChatColor.RED + "No location marked.");
		}	
	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
