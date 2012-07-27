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
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakmagic.OakMagic;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Mark extends Spell{
	
	public Mark(int id) {
		super(id);
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		return true;
	}

	@Override
	public void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Location location = caster.getLocation();
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
					"INSERT INTO oakmagic_markrecall(player, world, x, y, z, yaw) VALUES (?,?,?,?,?,?)" +
					"ON DUPLICATE KEY UPDATE world=?, x=?, y=?, z=?, yaw=?");
			statement.setString(1, caster.getName());
			statement.setString(2, location.getWorld().getName());
			statement.setDouble(3, location.getX());
			statement.setDouble(4, location.getY());
			statement.setDouble(5, location.getZ());
			statement.setFloat(6, location.getYaw());
			statement.setString(7, location.getWorld().getName());
			statement.setDouble(8, location.getX());
			statement.setDouble(9, location.getY());
			statement.setDouble(10, location.getZ());
			statement.setFloat(11, location.getYaw());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
		location.getWorld().playEffect(location, Effect.CLICK2, 0);
		caster.sendMessage(ChatColor.GRAY + "== Location marked ==");//
	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			try {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("DELETE FROM oakmagic_markrecall WHERE player=?");
				statement.setString(1, ((Player) event.getEntity()).getName());
				statement.executeUpdate();
				statement.close();
			} catch (SQLException ex) {
				OakMagic.log.log(Level.SEVERE, ex.getMessage());
			}
		}
	}
}
