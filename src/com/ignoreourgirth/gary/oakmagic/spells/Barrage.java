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

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Barrage extends Spell{
	
	//private static final float explosiveYeild = 1.8f;
	
	private final double arrowSpeed = 1.8;
	
	public Barrage(int id) {
		super(id);
		isHostileSpell = true;
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		return true;
	}

	@Override
	public void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		
		int spreadRadius = 3;
		ArrayList<Location> spawnLocations = new ArrayList<Location>();
		Location base = caster.getLocation().add(caster.getEyeLocation().getDirection().multiply(3).add(new Vector(0, 1.8, 0)));
		
		if (spellLevel == 2) {
			spreadRadius = 7;
		} else if (spellLevel == 3) {
			spreadRadius = 20;
		}
		
		spawnLocations.add(base);
		for (int i = 2; i < spreadRadius + 2; i++) {
    		spawnLocations.add(adjustedLocation(base, -90,   0f, i));
    		spawnLocations.add(adjustedLocation(base, -90, -45f, i));
    		spawnLocations.add(adjustedLocation(base, -90,  45f, i));
    		spawnLocations.add(adjustedLocation(base,  90,   0f, i));
    		spawnLocations.add(adjustedLocation(base,  90, -45f, i));
    		spawnLocations.add(adjustedLocation(base,  90,  45f, i));
    		spawnLocations.add(adjustedLocation(base,  90, -90f, i));
    		spawnLocations.add(adjustedLocation(base,  90,  90f, i));
    		if (spellLevel > 1) {
        		spawnLocations.add(adjustedLocation(base, -90, -22.5f, i));
        		spawnLocations.add(adjustedLocation(base, -90,  22.5f, i));
        		spawnLocations.add(adjustedLocation(base,  90, -22.5f, i));
        		spawnLocations.add(adjustedLocation(base,  90,  22.5f, i));
    		}
    		if (spellLevel > 2) {
        		spawnLocations.add(adjustedLocation(base, -90, -11.25f, i));
        		spawnLocations.add(adjustedLocation(base, -90,  11.25f, i));
        		spawnLocations.add(adjustedLocation(base,  90, -11.25f, i));
        		spawnLocations.add(adjustedLocation(base,  90,  11.25f, i));
    		}
		}
		
		Vector veolocityVector = caster.getLocation().getDirection().multiply(arrowSpeed);
		for (Location location : spawnLocations) {
			Arrow arrow = location.getWorld().spawn(location, Arrow.class);
    		arrow.setShooter(caster);
    		arrow.setBounce(false);
    		arrow.setVelocity(veolocityVector);
    		if (spellLevel == 3) arrow.setFireTicks(400);
		}
		
	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
	
	private Location adjustedLocation(Location base, int yaw, float pitch, int distance) {
		Location returnValue = base.clone();
		returnValue.setYaw(base.getYaw() + yaw);
		returnValue.setPitch(pitch);
		returnValue.add(returnValue.getDirection().multiply(distance * 1.3));
		returnValue.setYaw(base.getYaw());
		returnValue.setPitch(base.getPitch());
		return returnValue;
	}
}
