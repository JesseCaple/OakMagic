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

import com.ignoreourgirth.gary.oakmagic.spellTypes.Spell;

public class Bomb extends Spell{
			
	public Bomb(int id) {
		super(id);
		isFireSpell = true;
		isDestructiveSpell = true;
		isHostileSpell = true;
		isProjectileSpell = true;
		projectileEffect = Effect.POTION_BREAK;
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
		int entityDamage = 15 * spellLevel;
		float explosionStrength = 3.0f * spellLevel;
		if (targetEntity != null) {
			if (targetEntity instanceof  LivingEntity) {
				LivingEntity castEntity = ((LivingEntity) targetEntity);
				castEntity.damage(entityDamage, caster);
			}
			targetLocation = targetEntity.getLocation();
		}
		targetLocation.getWorld().createExplosion(targetLocation, explosionStrength, true);
	}

	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
