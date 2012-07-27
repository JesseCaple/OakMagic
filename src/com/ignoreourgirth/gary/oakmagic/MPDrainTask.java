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
package com.ignoreourgirth.gary.oakmagic;

import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoureourgirth.gary.oakmagic.spellTypes.ToggleableSpell;

public class MPDrainTask implements Runnable {

	private Player playerToDrain;
	private int mpToDrain;
	private ToggleableSpell refrencedSpell;
	
	public MPDrainTask(Player player, ToggleableSpell spell)
	{
		playerToDrain = player;
		refrencedSpell = spell;
		mpToDrain = spell.getMPDrained() * -1;
	}
	
	
	@Override
	public void run() {
		if (OakCoreLib.getXPMP().getTotalMP(playerToDrain) + mpToDrain < 1) {
			refrencedSpell.forceOffForPlayer(playerToDrain);
		} else {
			OakCoreLib.getXPMP().addMP(playerToDrain, mpToDrain);
		}
	}

}
