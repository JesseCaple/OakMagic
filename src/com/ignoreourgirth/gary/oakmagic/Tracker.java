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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Tracker {

	public Hashtable<String, Spell> spellsByName;
	public Hashtable<Integer, Spell> spellsByID;
	public Hashtable<Player, CastTimerTask> tasks;
		
	public Tracker(){
		tasks = new Hashtable<Player, CastTimerTask>();
		spellsByName = new Hashtable<String, Spell>();
		spellsByID = new Hashtable<Integer, Spell>();

		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT Enabled, SpellID, SpellName FROM oakmagic_spells");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				if (result.getInt(1) > 0) {
					int nextID = result.getInt(2);
					String nextName = result.getString(3);
					Spell nextSpell = getSpellClass(nextID, nextName);
					if (nextSpell != null) {
						OakMagic.server.getPluginManager().registerEvents(nextSpell, OakMagic.plugin);
						spellsByName.put(nextName.toLowerCase(), nextSpell);
						spellsByID.put(nextID, nextSpell);
					} else {
						OakMagic.log.log(Level.WARNING, "Could not find: " + nextName);
					}
				}
			}
			result.close();
			statement.close();
		} catch (SQLException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Spell getSpellClass(int id, String name) {
		try {
			Object[] instanceArguments = new Object[] {id};
			Class[] constructorArguments =  new Class[] {int.class};
			Class spellClass = Class.forName("com.ignoreourgirth.gary.oakmagic.spells." + name);
			Constructor spellConstructor = spellClass.getConstructor(constructorArguments);
			Object instancedClass = spellConstructor.newInstance(instanceArguments) ;
			Spell returnValue = (Spell) instancedClass;
			//OakMagic.log.info("Loaded: " + spellClass.getSimpleName());
			return returnValue;
		} catch (ClassNotFoundException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		} catch (InstantiationException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		} catch (IllegalAccessException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		} catch (SecurityException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		} catch (NoSuchMethodException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		} catch (IllegalArgumentException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		} catch (InvocationTargetException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
		return null;
	}

}
