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
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoreourgirth.gary.oakmagic.spellTypes.ToggleableSpell;

public class FeatherFall extends ToggleableSpell {
	
	public FeatherFall(int id) {
		super(id, 0);
	}

	@Override
	public boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		return true;
	}
	
	@Override
	protected void toggleOn(Player caster) {
		Chat.showNonCasterMessage(caster,"§7" + caster.getName() + " gained effect: FeatherFall.");
		caster.sendMessage("§7You feel a bit odd.");
	}

	@Override
	protected void toggleOff(Player caster) {	
		caster.sendMessage("§7FeatherFall effect has worn off.");
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		if (event.getEntity() instanceof Player) {
			Player eventPlayer = (Player) event.getEntity();
			if (isActiveFor(eventPlayer)) {
				if (event.getCause() == DamageCause.FALL) {
					eventPlayer.getWorld().playEffect(eventPlayer.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
					eventPlayer.getWorld().playEffect(eventPlayer.getLocation(), Effect.EXTINGUISH, 0);
					if (eventPlayer.getGameMode() != GameMode.CREATIVE) OakCoreLib.getXPMP().addMP(eventPlayer, -40);
					event.setCancelled(true);
				}
			}
		}
	}

	@Override
	protected void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
	}
}
