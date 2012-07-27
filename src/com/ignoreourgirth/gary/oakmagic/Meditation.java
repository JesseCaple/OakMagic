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

import java.util.Hashtable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;

public class Meditation {

	private static final long initalDelay = 200L;
	private static final long delay = 20L;
	private static Hashtable<Player, MeditationTask> tasks = new Hashtable<Player, MeditationTask>();
	
	public static void start(Player player) {
		if (!isMeditating(player)) {
			MeditationTask task = new MeditationTask(player);
			task.setID(OakMagic.server.getScheduler().scheduleSyncRepeatingTask(OakMagic.plugin, task, initalDelay, delay));
			tasks.put(player, task);
			player.sendMessage(ChatColor.GRAY + "   [meditation started] ");
		}
	}
	
	public static void cancel(Player player) {
		if (isMeditating(player)) {
			tasks.get(player).cancelTask();
			tasks.remove(player);
			player.sendMessage(ChatColor.GRAY + "   [meditation stopped] ");
		}
	}
	
	public static boolean isMeditating(Player player) {
		return tasks.containsKey(player);
	}
	
	
	private static class MeditationTask implements Runnable {

		private double mpGain = 1;
		
		private int taskID;
		private boolean canceled;
		private Player targetPlayer;
		private Location location;
		
		protected MeditationTask(Player player) { 
			targetPlayer = player; 
			location = player.getLocation().clone();
			OakCoreLib.getXPMP().blockMPRegen(player);
		}
		
		protected void setID(int ID) { if (ID == 0) taskID = ID; }
		
		protected void cancelTask() { 
			canceled = true; 
			OakCoreLib.getXPMP().allowMPRegen(targetPlayer);
			OakMagic.server.getScheduler().cancelTask(taskID);
	    }
		
		@Override
		public void run() {
			if (!canceled) {
				if (    Math.abs(targetPlayer.getLocation().getX() - location.getX()) > 0.2 || 
						Math.abs(targetPlayer.getLocation().getY() - location.getY()) > 0.2 ||
						Math.abs(targetPlayer.getLocation().getZ() - location.getZ()) > 0.2) {
							Meditation.cancel(targetPlayer);
				} else {
					mpGain *= 1.09;
					OakCoreLib.getXPMP().addMP(targetPlayer, (int) Math.round(mpGain));
					if (OakCoreLib.getXPMP().getMaxMP(targetPlayer) == OakCoreLib.getXPMP().getTotalMP(targetPlayer)) Meditation.cancel(targetPlayer);
				}
			}
		}

	}
}
