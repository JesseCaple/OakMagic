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

import com.ignoreourgirth.gary.oakmagic.spellTypes.Spell;

public class Heal extends Spell {
	
	public Heal(int id) {
		super(id);
		isProjectileSpell = true;
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		if (targetEntity instanceof Player) return true;
		return false;
	}
	
	@Override
	protected void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Player targetPlayer = (Player) targetEntity;
		int newHealth = targetPlayer.getHealth() + 4 * spellLevel;
		if (newHealth > targetPlayer.getMaxHealth()) newHealth = targetPlayer.getMaxHealth();
		targetPlayer.setHealth(newHealth);
		targetPlayer.getLocation().getWorld().playEffect(targetPlayer.getLocation(), Effect.CLICK1, 1);
		if (targetPlayer == caster) {
			caster.sendMessage("§eYou have healed yourself.");
		} else {
			targetPlayer.sendMessage("§e" + caster.getName() + " has healed you.");
		}
	}

	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
