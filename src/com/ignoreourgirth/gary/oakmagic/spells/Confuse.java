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

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Confuse extends Spell {
	
	public Confuse(int id) {
		super(id);
		isHostileSpell = true;
		isProjectileSpell = true;
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (caster == targetEntity) {
			return false;
		}else if (targetEntity instanceof Player) return true;
		return false;
	}
	
	@Override
	protected void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Player targetPlayer = (Player) targetEntity;
		if (spellLevel == 1) {
			targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 240, 10));
			caster.sendMessage("§7You have confused " + ((Player) targetPlayer).getName() + ".");
		} else if (spellLevel == 2) {
			targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 400, 1));
			targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 400, 11));
			caster.sendMessage("§7You have severely confused " + ((Player) targetPlayer).getName() + ".");
		}
		targetPlayer.getWorld().playEffect(targetPlayer.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 0);
		targetPlayer.getWorld().playEffect(targetPlayer.getLocation(), Effect.SMOKE, 2);
		targetPlayer.getWorld().playEffect(targetPlayer.getLocation(), Effect.SMOKE, 0);
	}

	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
