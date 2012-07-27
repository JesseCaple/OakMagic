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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoureourgirth.gary.oakmagic.spellTypes.SustainedSpell;

public class Weaken extends SustainedSpell {
	
	public Weaken(int id) {
		super(id, 1, 200);
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
	protected void toggleOn(Player caster) {
		LivingEntity liveEntity = (LivingEntity) this.getTargetEntity(caster);
		if (liveEntity instanceof Player) {
			Chat.showNonCasterMessage(caster,"§7" + caster.getName() + " has began weakening " + ((Player) liveEntity).getName() + ".");
			caster.sendMessage("§7You begin weakening " + ((Player) liveEntity).getName() + ".");
			((Player) liveEntity).sendMessage("§4You are being weakened.");
		} else {
			Chat.showNonCasterMessage(caster,"§7" + caster.getName() + " has began weakening the " + liveEntity.getType().getName() + ".");
			caster.sendMessage("§7You begin weakening the " +  liveEntity.getType().getName() + ".");
		}
		liveEntity.removePotionEffect(PotionEffectType.WEAKNESS);
		liveEntity.removePotionEffect(PotionEffectType.HUNGER);
		liveEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100000000, 3));
		liveEntity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100000000, 1));
	}

	@Override
	protected void toggleOff(Player caster) {	
		LivingEntity liveEntity = (LivingEntity) this.getTargetEntity(caster);
		liveEntity.removePotionEffect(PotionEffectType.WEAKNESS);
		liveEntity.removePotionEffect(PotionEffectType.HUNGER);
		if (liveEntity instanceof Player) ((Player) liveEntity).sendMessage("§7You are no longer being weakened.");
		caster.sendMessage("§7Sustained cast stopped: weakness.");
	}

	@Override
	protected void nextTick(Player caster) {
		LivingEntity target = ((LivingEntity) this.getTargetEntity(caster));
		target.damage(1);
	}

	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
