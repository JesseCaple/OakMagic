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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Hadouken extends Spell{
	
	private static final float explosiveYeild = 1.8f;
	
	public Hadouken(int id) {
		super(id);
		isFireSpell = true;
		isDestructiveSpell = true;
		isHostileSpell = true;
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		return true;
	}

	@Override
	public void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Vector facingVector = caster.getLocation().getDirection().multiply(3).add(new Vector(0,1,0));
		Location spawnLocation = caster.getLocation().add(facingVector);
    	((Fireball) spawnLocation.getWorld().spawn(spawnLocation, Fireball.class)).setYield(explosiveYeild);
	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
