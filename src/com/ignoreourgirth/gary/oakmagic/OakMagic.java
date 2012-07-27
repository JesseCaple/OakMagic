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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.ignoreourgirth.gary.oakcorelib.CommandPreprocessor;
import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class OakMagic extends JavaPlugin  {
	
	public static Logger log;
	public static Plugin plugin;
	public static Server server;
	public static Tracker tracker;
	
	public void onEnable() {
		log = this.getLogger();
		plugin = this;
		server = this.getServer();
		tracker = new Tracker();
		new Meditation();
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		CommandPreprocessor.addExecutor(new Commands());
	}
	
	public void onDisable() {
		OakCoreLib.getXPMP().resetMPRegenBlocks();
	}
	
	public static boolean destructiveSpellAllowed(Location location) {
		if (!OakCoreLib.getWorldGuard().getRegionManager(location.getWorld()).getApplicableRegions(location).allows(DefaultFlag.TNT)) return false;
		if (TownyUniverse.getTownBlock(location) != null) {
			return false;
		}
		return hostileSpellAllowed(location);
	}
	
	public static boolean fireSpellAllowed(Location location) {
		if (!OakCoreLib.getWorldGuard().getRegionManager(location.getWorld()).getApplicableRegions(location).allows(DefaultFlag.LIGHTER)) return false;
		TownBlock townBlock = TownyUniverse.getTownBlock(location);
		if (townBlock != null) {
			try {
				if (!townBlock.getTown().isFire()) return false;
			} catch (NotRegisteredException ex) {
				log.log(Level.SEVERE, ex.getMessage());
			}
		}
		return hostileSpellAllowed(location);
	}
	
	public static boolean hostileSpellAllowed(Location location) {
		if (!OakCoreLib.getWorldGuard().getRegionManager(location.getWorld()).getApplicableRegions(location).allows(DefaultFlag.PVP)) return false;
		TownBlock townBlock = TownyUniverse.getTownBlock(location);
		if (TownyUniverse.getTownBlock(location) != null) {
			try {
				if (!townBlock.getTown().isPVP()) return false;
			} catch (NotRegisteredException ex) {
				log.log(Level.SEVERE, ex.getMessage());
			}
		}
		return true;
	}
	
}
