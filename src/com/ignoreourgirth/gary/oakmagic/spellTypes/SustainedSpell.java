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
package com.ignoreourgirth.gary.oakmagic.spellTypes;

import java.util.Hashtable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import com.ignoreourgirth.gary.oakmagic.OakMagic;

public abstract class SustainedSpell extends ToggleableSpell  {
	
	private Hashtable<Player, Integer> sustainedEventIDs;
	private long taskInterval;
	
	public SustainedSpell(int id, int mpDrained, long tickInterval) {
		super(id, mpDrained);
		sustainedEventIDs = new Hashtable<Player, Integer>();
		taskInterval = tickInterval;
	}
	
	@Override
	public void executeSpell(final Player caster, final Entity targetEntity, final Location targetLocation, int spellLevel, boolean mpAlreadyCalculated) {
		int idReturned = OakMagic.server.getScheduler().scheduleSyncRepeatingTask(OakMagic.plugin, new Runnable() {
			   public void run() {
				   nextTick(caster);
			   }
		}, taskInterval, taskInterval);
		sustainedEventIDs.put(caster, idReturned);
		super.executeSpell(caster, targetEntity, targetLocation, spellLevel, mpAlreadyCalculated);
	}
	
	@Override
	public void forceOffForPlayer(Player player) {
		Bukkit.getScheduler().cancelTask(sustainedEventIDs.get(player));
		sustainedEventIDs.remove(player);
		super.forceOffForPlayer(player);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player eventPlayer = (Player) event.getEntity();
			if (isActiveFor(eventPlayer)) {
				this.forceOffForPlayer(eventPlayer);
			}
		}
	}
	
	protected abstract void nextTick(Player caster);
	
}
