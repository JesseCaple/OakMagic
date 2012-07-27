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

public class CastAnimationTask implements Runnable {

	private CastTimerTask linkedSpell;
	
	public CastAnimationTask(CastTimerTask delayEvent) {
		linkedSpell = delayEvent;
		delayEvent.getSpell().showCastEffect(delayEvent.getPlayer(), delayEvent.getEntity(), delayEvent.getLocation(), delayEvent.getSpellLevel());
	}
	
	@Override
	public void run() {
		if (!linkedSpell.getCanceled() && !linkedSpell.getFinished()) {
			CastAnimationTask animationTask = new CastAnimationTask(linkedSpell);
			OakMagic.server.getScheduler().scheduleSyncDelayedTask(OakMagic.plugin, animationTask, 5L);
		}
	}

}
