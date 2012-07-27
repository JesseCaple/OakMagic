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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoureourgirth.gary.oakmagic.spellTypes.ToggleableSpell;

public class Rush extends ToggleableSpell {
	
	public Rush(int id) {
		super(id, 2);
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		return true;
	}
	
	@Override
	protected void toggleOn(Player caster) {
		Chat.showNonCasterMessage(caster,"§7" + caster.getName() + " gained effect: Rush.");
		caster.removePotionEffect(PotionEffectType.SPEED);
		caster.removePotionEffect(PotionEffectType.JUMP);
		caster.removePotionEffect(PotionEffectType.HUNGER);
		caster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 8));
		caster.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000000, 5));
		caster.sendMessage("§7You feel an intense adrenaline surge.");
	}

	@Override
	protected void toggleOff(Player caster) {	
		caster.removePotionEffect(PotionEffectType.SPEED);
		caster.removePotionEffect(PotionEffectType.JUMP);
		caster.sendMessage("§7You feel normal again.");
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player eventPlayer = (Player) event.getEntity();
			if (isActiveFor(eventPlayer)) {
				if (event.getCause() == DamageCause.FALL) {
					if (eventPlayer.getFallDistance() < 11) {
						event.setCancelled(true);
					} else {
						int newDamage = (int) Math.round(event.getDamage() * 0.4) - 3;
						if (newDamage < 0)  newDamage = 0;
						event.setDamage(newDamage);
					}
				}
			}
		}
	}

	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
	
}
