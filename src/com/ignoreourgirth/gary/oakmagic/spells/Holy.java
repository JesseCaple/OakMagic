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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import com.ignoureourgirth.gary.oakmagic.spellTypes.Spell;

public class Holy extends Spell{
	
	public Holy(int id) {
		super(id);
		spellDescription = "Disintegrates zombies and skeletons leaving nothing but ash.";
		isProjectileSpell = true;
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (targetEntity != null) return (targetEntity.getType() == EntityType.ZOMBIE || targetEntity.getType() == EntityType.SKELETON );
		return false;
	}

	@Override
	public void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Monster mob = (Monster) targetEntity;
		mob.getLocation().getWorld().playEffect(mob.getLocation(), Effect.ENDER_SIGNAL, 0);
		mob.getLocation().getWorld().playEffect(mob.getLocation(), Effect.ENDER_SIGNAL, 0);
		mob.damage(10000, caster);
	}
	
	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
