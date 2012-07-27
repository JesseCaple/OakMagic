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

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoreourgirth.gary.oakmagic.OakMagic;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Hellfire extends Spell{
	
	private Random randomGen = new Random();
	
	private static final int totalFireballs = 600;
	private static final int baseDelay = 120;
	private static final int radius = 130;
	private static final float explosiveYeild = 4.0f;
	private static final float fireballPitch = 90f;
	private static final double fireballStartHeight = 200;
	
	public Hellfire(int id) {
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

		Chat.showNonCasterMessage(caster,"§4" + caster.getName() + " has called forth hellfire!");
		caster.sendMessage("§7You have called forth hellfire.");
		
		final World baseWorld = caster.getWorld();
		float fireballYaw = caster.getLocation().getYaw();
		double baseX = caster.getLocation().getX();
		double baseZ = caster.getLocation().getZ();
		int radiusHalf = (radius/2);
        
        for (int loop = 0; loop < totalFireballs; loop++) {
        	int nextDelay = (int) (baseDelay + (loop * 4) + (randomGen.nextDouble() * 400));
        	double XModifier = randomGen.nextDouble() * radiusHalf;
        	double ZModifier = randomGen.nextDouble() * radiusHalf;
        	if (randomGen.nextDouble() > .5) XModifier *= -1;
        	if (randomGen.nextDouble() > .5) ZModifier *= -1;
        	final Location nextLocation = new Location(baseWorld, baseX + XModifier, fireballStartHeight, baseZ + ZModifier, fireballYaw, fireballPitch);
        	OakMagic.server.getScheduler().scheduleSyncDelayedTask(OakMagic.plugin, new Runnable() {
        		public void run() {
        			((Fireball) baseWorld.spawn(nextLocation, Fireball.class)).setYield(explosiveYeild);
        		}
        	}, nextDelay);
        } 
        
	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
