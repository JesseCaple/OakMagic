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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class CastTimerTask implements Runnable {

	public boolean isCancelable() { return cancelable; }
	
	private boolean cancelable;
	private boolean canceled;
	private boolean finished;
	private Player caster;
	private Entity thisTargetEntity;
	private Location thisTargetLocation;
	private Location location;
	private Spell spellToExecute;
	private int givenSpellLevel;

	public CastTimerTask(Player player, Spell spell, Entity targetEntity, Location targetLocation, int spellLevel)
	{
		canceled = false;
		caster = player;
		location = player.getLocation();
		spellToExecute = spell;
		givenSpellLevel = spellLevel;
		thisTargetEntity = targetEntity;
		thisTargetLocation = targetLocation;
		cancelable = (spellToExecute.getCastTime() > 0);
	}
	
	@Override
	public void run() {
		if (!canceled) {
			finished = true;
			if (spellToExecute.isSpellProjectile() && caster != thisTargetEntity) {
				spellToExecute.executeProjectileSpell(caster, givenSpellLevel);
			} else {
				spellToExecute.executeSpell(caster, thisTargetEntity, thisTargetLocation, givenSpellLevel, false);
			}
			OakMagic.tracker.tasks.remove(caster);
		}
	}
	
	public void setCanceled(boolean value) {
		if (cancelable) canceled = value;
	}
	
	public Player getPlayer() {
		return caster;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Location getTarget() {
		return thisTargetLocation;
	}
	
	public Entity getEntity() {
		return thisTargetEntity;
	}
	
	public boolean getCanceled() {
		return canceled;
	}
	
	public boolean getFinished() {
		return finished;
	}
	
	public Spell getSpell() {
		return spellToExecute;
	}
	
	public int getSpellLevel() {
		return givenSpellLevel;
	}

}
