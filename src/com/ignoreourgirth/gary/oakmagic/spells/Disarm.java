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
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ignoreourgirth.gary.oakmagic.spellTypes.Spell;

public class Disarm extends Spell {
	
	public Disarm(int id) {
		super(id);
		isHostileSpell = true;
		isProjectileSpell = true;
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (caster == targetEntity) {
			return false;
		}
		if (targetEntity instanceof Player) return (((Player) targetEntity).getItemInHand().getType() != Material.AIR);
		return false;
	}
	
	@Override
	protected void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Player targetPlayer = (Player) targetEntity;
		if (targetPlayer.getItemInHand().getType() != Material.AIR) {
			targetPlayer.getLocation().getWorld().dropItem(targetPlayer.getLocation(), targetPlayer.getItemInHand()).setPickupDelay(300);
			targetPlayer.setItemInHand(new ItemStack(Material.AIR));
		}
	}

	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
