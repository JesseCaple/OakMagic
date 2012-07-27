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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoreourgirth.gary.oakmagic.OakMagic;
import com.ignoreourgirth.gary.oakmagic.spellTypes.Spell;

public class SummonMeteor extends Spell{
	
	private static final float meteorPitch = 75f;
	private static final float explosiveYeild = 50f;
	private static final double meteorStartHeight = 220;
	private static final int tickDelay = 400;
	
	public SummonMeteor(int id) {
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
		caster.sendMessage("§7Cast complete.");
		Chat.broadcastMessage(caster, 350, ChatColor.GOLD + "WARNING!!!");
		Chat.broadcastMessage(caster, 350, ChatColor.RED + ":: Seek deep subterranean shelter immediately.");
		Chat.broadcastMessage(caster, 350, ChatColor.RED + ":: A large meteor has been sighted. Collision is imminent.");
		final Location meteorLocation = new Location(caster.getWorld(), caster.getLocation().getX(), meteorStartHeight, caster.getLocation().getZ(), caster.getLocation().getYaw(), meteorPitch);
		OakMagic.server.getScheduler().scheduleSyncDelayedTask(OakMagic.plugin, new Runnable() {
    		public void run() {
    			((Fireball) meteorLocation.getWorld().spawn(meteorLocation, Fireball.class)).setYield(explosiveYeild); 
    		}
    	}, tickDelay);
		
	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
