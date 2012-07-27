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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.ignoreourgirth.gary.oakmagic.OakMagic;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Ensnare extends Spell {
	
	
	public Ensnare(int id) {
		super(id);
		isHostileSpell = true;
		isProjectileSpell = true;
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (caster == targetEntity) {
			return false;
		}
		if (targetEntity instanceof LivingEntity) return true;
		return false;
	}
	
	@Override
	protected void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		final LivingEntity liveEntity = (LivingEntity) targetEntity;
		boolean success = false;
		if (liveEntity instanceof Player) {
			success = true;
			liveEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, -8));
			liveEntity.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING , 400, -8));
			liveEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 400, 5));
			((Player) liveEntity).sendMessage("§c" + caster.getName() + " has ensnared you!");
			caster.sendMessage("§7You have ensnared " + ((Player) liveEntity).getName() + ".");
		} else {
			success = true;
			liveEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 600, 3));
			caster.sendMessage("§7You have ensnared the " + (liveEntity).getType().getName() + ".");
			final int taskID = OakMagic.server.getScheduler().scheduleSyncRepeatingTask(OakMagic.plugin, new Runnable() {
				public void run() {
					liveEntity.setVelocity(new Vector());
			    }
			}, 1, 1);
			OakMagic.server.getScheduler().scheduleSyncDelayedTask(OakMagic.plugin, new Runnable() {
				public void run() {
					OakMagic.server.getScheduler().cancelTask(taskID);
			    }
			}, 600);
		}
		if (success) {
			liveEntity.getWorld().playEffect(liveEntity.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 0);
			liveEntity.getWorld().playEffect(liveEntity.getLocation(), Effect.SMOKE, 3);
			liveEntity.getWorld().playEffect(liveEntity.getLocation(), Effect.SMOKE, 2);
			liveEntity.getWorld().playEffect(liveEntity.getLocation(), Effect.SMOKE, 1);
			liveEntity.getWorld().playEffect(liveEntity.getLocation(), Effect.SMOKE, 0);
		}
	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
	

}
