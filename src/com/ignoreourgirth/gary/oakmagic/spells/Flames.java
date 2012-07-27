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
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Flames extends Spell{
	
	public Flames(int id) {
		super(id);
		isFireSpell = true;
		isHostileSpell = true;
		isProjectileSpell = true;
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (caster == targetEntity) {
			return false;
		}
		return true;
	}

	@Override
	public void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (targetEntity != null) {
			targetEntity.setFireTicks(100);
			if (targetEntity instanceof  LivingEntity) {
				LivingEntity castEntity = ((LivingEntity) targetEntity);
				castEntity.damage(5, caster);
			}
			targetLocation = targetEntity.getLocation();
		} 
		
		for(int x =-1; x < 2; x++) {
			for(int y =-1; y < 2; y++) {
				for(int z =-1; z < 2; z++) {
					Block nextBlock = new Location(targetLocation.getWorld(), targetLocation.getX() + x, targetLocation.getY() + y, targetLocation.getZ() + z).getBlock();
					if (nextBlock.getType() == Material.AIR) {
						nextBlock.setType(Material.FIRE);
					}
				}
			}
		}

	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
