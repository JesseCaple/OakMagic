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
package com.ignoureourgirth.gary.oakmagic.spellTypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.ignoreourgirth.gary.oakcorelib.MagicUseEvent;
import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakmagic.CastAnimationTask;
import com.ignoreourgirth.gary.oakmagic.CastTimerTask;
import com.ignoreourgirth.gary.oakmagic.Chat;
import com.ignoreourgirth.gary.oakmagic.OakMagic;

public abstract class Spell implements Listener  {
	
	public enum TargetType {self, other, location;}
	
	private boolean isValid;
	
	private boolean Enabled;
	private int SpellID;
	private String SpellName;
	private int CastTime;
	private int CoolDown;
	private int MP;
	
	public boolean isSpellProjectile() {return isProjectileSpell;}
	public boolean getIsValid() {return isValid;}
	public String getSpellName() {return SpellName;}
	public int getSpellID() {return SpellID;}
	public int getCastTime() {return CastTime;}
	public int getCoolDown() {return CoolDown;}
	public int getMP() {return MP;}
	public boolean getEnabled() {return Enabled;}
	
	protected Effect projectileEffect = Effect.SMOKE;
	protected int maxSpellLevel = 1;
	protected boolean isFireSpell = false;
	protected boolean isHostileSpell = false;
	protected boolean isProjectileSpell = false;
	protected boolean isDestructiveSpell = false;
	protected String spellDescription = ChatColor.GRAY + "There is no description for this spell.";
	
	public Spell(int id) {
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT * FROM oakmagic_spells WHERE SpellID=?");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				SpellID = result.getInt(1);
				SpellName = result.getString(2);
				CastTime = result.getInt(3);
				CoolDown = result.getInt(4);
				MP = result.getInt(5);
				Enabled = Boolean.parseBoolean(result.getString(6));
				isValid = true;
			} else {
				isValid = false;
			}
			result.close();
			statement.close();
		} catch (SQLException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
	}
	
	public void showCastEffect(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		Location feet = new Location(caster.getWorld(), caster.getLocation().getX(), caster.getLocation().getY() - .2, caster.getLocation().getZ());
		feet.getWorld().playEffect(feet, Effect.MOBSPAWNER_FLAMES, 1);
		onCastTick(caster, targetEntity, targetLocation, spellLevel);
	}
	
	public boolean playerKnowsSpell(Player player, int spellLevel) {
		boolean returnValue = false;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT SpellLevel FROM oakmagic_players WHERE SpellID=? AND PlayerName=?");
			statement.setInt(1, SpellID);
			statement.setString(2, player.getName());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				if (result.getInt(1) == spellLevel) returnValue = true;	
			}
			result.close();
			statement.close();
		} catch (SQLException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
		return returnValue;
	}
	
	public boolean clearTempSpell(Player player, int spellLevel) {
		boolean returnValue = false;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("DELETE FROM oakmagic_players WHERE SpellID=? AND PlayerName=? AND TempScroll='1'");
			statement.setInt(1, SpellID);
			statement.setString(2, player.getName());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
		return returnValue;
	}
	
	//returns seconds until player can cast again
	public int spellIsOnCooldown(Player player) {
		int returnValue = 0;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT CastedAt FROM oakmagic_cooldowns WHERE PlayerName=? AND SpellName=?");
			statement.setString(1, player.getName());
			statement.setString(2, SpellName);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				Calendar lastCasted = Calendar.getInstance();
				lastCasted.setTime(result.getTimestamp(1));
				Calendar expiresAt = Calendar.getInstance();
				expiresAt.setTime(result.getTimestamp(1));
				expiresAt.add(Calendar.SECOND, CoolDown);
				returnValue = (int) ((expiresAt.getTime().getTime() -  new java.util.Date().getTime()) / 1000);
				if (returnValue < 1) {
					returnValue = 0;
					PreparedStatement statement2 = OakCoreLib.getDB().prepareStatement("DELETE FROM oakmagic_cooldowns WHERE PlayerName=? AND SpellName=?");
					statement2.setString(1, player.getName());
					statement2.setString(2, SpellName);
					statement2.executeUpdate();
					statement2.close();
				}
			}
			result.close();
			statement.close();
		} catch (SQLException ex) {
			OakMagic.log.log(Level.SEVERE, ex.getMessage());
		}
		return returnValue;
	}
	
	public void cast(Player caster, Entity targetEntity, int level) {
		cast(caster, targetEntity, null, level);
	}
	
	public void cast(Player caster, Location targetLocation, int level) {
		cast(caster, null, targetLocation, level);
	}
	
	public boolean isValid(Player caster, Entity targetEntity, int level) {
		return isValid(caster, targetEntity, null, level);
	}
	
	public boolean isValid(Player caster, Location targetLocation, int level) {
		return isValid(caster, null, targetLocation, level);
	}
	
	private boolean validateSpellType(Player caster, Entity targetEntity, Location targetLocation) {
		if (isDestructiveSpell) {
			if (targetEntity != null) {
				if (!OakMagic.destructiveSpellAllowed(targetEntity.getLocation())) {
					caster.sendMessage("§cDestructive spells are not allowed here.");
					return false;
				}
			} else if (targetLocation != null) {
				if (!OakMagic.destructiveSpellAllowed(targetLocation)) {
					caster.sendMessage("§cDestructive spells are not allowed here.");
					return false;
				}
			} else {
				if (!OakMagic.destructiveSpellAllowed(caster.getLocation())) {
					caster.sendMessage("§cDestructive spells are not allowed here.");
					return false;
				}
			}
		}
		if (isFireSpell) {
			if (targetEntity != null) {
				if (!OakMagic.fireSpellAllowed(targetEntity.getLocation())) {
					caster.sendMessage("§cFire spells are not allowed here.");
					return false;
				}
			} else if (targetLocation != null) {
				if (!OakMagic.fireSpellAllowed(targetLocation)) {
					caster.sendMessage("§cFire spells are not allowed here.");
					return false;
				}
			} else {
				if (!OakMagic.fireSpellAllowed(caster.getLocation())) {
					caster.sendMessage("§cFire spells are not allowed here.");
					return false;
				}
			}
		}
		if (isHostileSpell) {
			if (targetEntity != null) {
				if (!OakMagic.hostileSpellAllowed(targetEntity.getLocation())) {
					caster.sendMessage("§cHostile spells are not allowed here.");
					return false;
				}
			} else if (targetLocation != null) {
				if (!OakMagic.hostileSpellAllowed(targetLocation)) {
					caster.sendMessage("§cHostile spells are not allowed here.");
					return false;
				}
			} else {
				if (!OakMagic.hostileSpellAllowed(caster.getLocation())) {
					caster.sendMessage("§cHostile spells are not allowed here.");
					return false;
				}
			}
		}
		return true;
	}
	
	protected boolean isValid(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		int cooldownRemaining = spellIsOnCooldown(caster);
		if (cooldownRemaining == 0) {
			if (isProjectileSpell && (targetEntity == null)) return true;
			if (!validateSpellType(caster, targetEntity, targetLocation)) return false;
			return canCast(caster, targetEntity, targetLocation, spellLevel);
		} else {
			int seconds = cooldownRemaining % 60;
			cooldownRemaining /= 60;
			int minutes = cooldownRemaining % 60;
			cooldownRemaining /= 60;
			int hours = cooldownRemaining % 24;
			cooldownRemaining /= 24;
			int days = cooldownRemaining;
			StringBuilder timeRemaining = new StringBuilder();
			caster.sendMessage("§4Spell is still on cooldown.");
			timeRemaining.append("§fRemaining: ");
			if (days > 0) timeRemaining.append(days + " days ");
			if (hours > 0) timeRemaining.append(hours + " hours ");
			if (minutes > 0) timeRemaining.append(minutes + " minutes ");
			if (seconds > 0 && cooldownRemaining > 59) timeRemaining.append("and ");
			timeRemaining.append(seconds + " seconds.");
			caster.sendMessage(timeRemaining.toString());
			return false;
		}
	}
	
	protected void cast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel) {
		int MPNeeded = MP;
		if (spellLevel > 1) MPNeeded =  (int) Math.round(MP * (Math.pow(spellLevel, 1.5)));
		if (OakCoreLib.getXPMP().getTotalMP(caster) >= MPNeeded) {
			if (CastTime > 0) {
				Chat.showCastStartMessage(caster, this, spellLevel);
			}
			CastTimerTask castDelayTask = new CastTimerTask(caster, this, targetEntity, targetLocation, spellLevel);
			CastAnimationTask animationTask = new CastAnimationTask(castDelayTask);
			OakMagic.server.getScheduler().scheduleSyncDelayedTask(OakMagic.plugin, castDelayTask, CastTime * 20);
			OakMagic.server.getScheduler().scheduleSyncDelayedTask(OakMagic.plugin, animationTask, 5L);
			OakMagic.tracker.tasks.put(caster, castDelayTask);
		} else {
			caster.sendMessage("§4You don't have enough MP.");
		}
	}
	
	public void executeSpell(Player caster, Entity targetEntity, Location targetLocation, int spellLevel, boolean mpAlreadyCalculated) {
		saveCooldown(caster);
		clearTempSpell(caster, spellLevel);
		execute(caster, targetEntity, targetLocation, spellLevel);
		if (caster.getGameMode() != GameMode.CREATIVE) {
			if (!mpAlreadyCalculated) {
				if (spellLevel > 1) {
					OakCoreLib.getXPMP().addMP(caster, (int) Math.round(MP * (Math.pow(spellLevel, 1.5)) * -1));
				} else {
					OakCoreLib.getXPMP().addMP(caster, MP * -1);
				}
			}
		}
		OakMagic.server.getPluginManager().callEvent(new MagicUseEvent(SpellID, spellLevel, caster, targetEntity, targetLocation));
	}
	
	public void executeProjectileSpell(final Player caster, final int spellLevel) {
		if (this instanceof ToggleableSpell) {
			ToggleableSpell toggleSpell = (ToggleableSpell) this;
			if (toggleSpell.isActiveFor(caster)) {
				toggleSpell.forceOffForPlayer(caster);
				return;
			}
		}
		int MPNeeded = MP;
		if (spellLevel > 1) MPNeeded =  (int) Math.round(MP * (Math.pow(spellLevel, 1.5)));
		if (!(OakCoreLib.getXPMP().getTotalMP(caster) >= MPNeeded)) {
			caster.sendMessage("§4You don't have enough MP.");
			return;
		} 
		OakCoreLib.getXPMP().addMP(caster, MPNeeded * -1);
		Location start = caster.getLocation().add(caster.getLocation().getDirection().multiply(2).add(new Vector(0,2,0)));
		Location target = caster.getTargetBlock(null, 60).getLocation();
		if (target == null) return;
		
		World world = target.getWorld();
		
		double x1 = start.getX();
		double y1 = start.getY();
		double z1 = start.getZ();
		double x2 = target.getX();
		double y2 = target.getY();
		double z2 = target.getZ();
		double xDifference = x2 - x1;
		double yDifference = y2 - y1;
		double zDifference = z2 - z1;
		double xDistance =  Math.abs(xDifference);
		double yDistance =  Math.abs(yDifference);
		double zDistance =  Math.abs(zDifference);
		
		final List<Entity> entities = caster.getNearbyEntities(xDistance + 40, yDistance + 20, zDistance + 40);
		int maxLoops = (int) Math.round(xDistance);
		if (yDistance > xDistance) maxLoops = (int) Math.round(yDistance);
		if (zDistance > xDistance && zDistance > yDistance) maxLoops = (int) Math.round(zDistance);
		maxLoops ++;
		
		
		final HashSet<Integer> targetFound = new HashSet<Integer>();
		for (int i = 1; i <= maxLoops; i++) {
			final double progress = (((double)i)/((double)maxLoops));
			final Location nextLocation = new Location(world, x1 + xDifference * progress, y1 + yDifference * progress, z1 + zDifference * progress);
			OakMagic.server.getScheduler().scheduleSyncDelayedTask(OakMagic.plugin, new Runnable() {
				public void run() {
					if (!targetFound.contains(1)) {
					   	nextLocation.getWorld().playEffect(nextLocation, projectileEffect, 3); 
						for (Entity entity : entities) {
							Location entityLocation = entity.getLocation();
							double entityXDistance = Math.abs(nextLocation.getX() - entityLocation.getX());
							double entityYDistance = Math.abs(nextLocation.getY() - entityLocation.getY());
							double entityZDistance = Math.abs(nextLocation.getZ() - entityLocation.getZ());
							if (entityXDistance < 1.4 && entityYDistance < 1.7 && entityZDistance < 1.4) {
								nextLocation.getWorld().playEffect(nextLocation, Effect.EXTINGUISH, 1); 
								if (!validateSpellType(caster, entity, null)) return;
								if (canCast(caster, entity, null, spellLevel)) executeSpell(caster, entity, null, spellLevel, true);
								targetFound.add(1);
								return;
							}
						}
						if (progress >= 1.0) {
							if (!validateSpellType(caster, null, nextLocation)) return;
							if (canCast(caster, null, nextLocation, spellLevel)) executeSpell(caster, null, nextLocation, spellLevel, true);
						}
					}
			   }
			}, (i - 1));
		}
	}
	
	protected void saveCooldown(Player player) {
		if (player.getGameMode() != GameMode.CREATIVE) {
			try {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("INSERT INTO oakmagic_cooldowns (PlayerName, SpellName, CastedAt) VALUES (?, ?, ?)");
				statement.setString(1, player.getName());
				statement.setString(2, SpellName);
				statement.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
				statement.executeUpdate();
				statement.close();
			} catch (SQLException ex) {
				OakMagic.log.log(Level.SEVERE, ex.getMessage());
			}
		}
	}
	
	protected void showValidationFailure(Player player, String reason) {
		player.sendMessage("§4Cast failed.");
		player.sendMessage("§7" + reason);
	}
	
	protected abstract void onCastTick(Player caster, Entity targetEntity, Location targetLocation, int spellLevel);
	protected abstract boolean canCast(Player caster, Entity targetEntity, Location targetLocation, int spellLevel);
	protected abstract void execute(Player caster, Entity targetEntity, Location targetLocation, int spellLevel);

}
