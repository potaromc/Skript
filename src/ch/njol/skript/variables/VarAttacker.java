/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011, 2012 Peter Güttinger
 * 
 */

package ch.njol.skript.variables;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import ch.njol.skript.Skript;
import ch.njol.skript.api.Changer.ChangeMode;
import ch.njol.skript.api.Converter;
import ch.njol.skript.data.DefaultChangers;
import ch.njol.skript.lang.ExprParser.ParseResult;
import ch.njol.skript.lang.SimpleLiteral;
import ch.njol.skript.lang.SimpleVariable;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.variables.VarAttacker.Attacker;

/**
 * 
 * @author Peter Güttinger
 * 
 */
public class VarAttacker extends SimpleVariable<Attacker> {
	
	public static final class Attacker {
		private final Object attacker;
		
		public Attacker(final Object attacker) {
			this.attacker = attacker;
		}
		
		public Object getAttacker() {
			return attacker;
		}
		
		@Override
		public String toString() {
			return Skript.toString(attacker);
		}
	}
	
	static {
		Skript.registerVariable(VarAttacker.class, Attacker.class, "(attacker|damager)");
		Skript.registerConverter(Attacker.class, Entity.class, new Converter<Attacker, Entity>() {
			@Override
			public Entity convert(final Attacker a) {
				if (a.getAttacker() instanceof Entity)
					return (Entity) a.getAttacker();
				return null;
			}
		});
		Skript.registerConverter(Attacker.class, Block.class, new Converter<Attacker, Block>() {
			@Override
			public Block convert(final Attacker a) {
				if (a.getAttacker() instanceof Block)
					return (Block) a.getAttacker();
				return null;
			}
		});
		Skript.registerConverter(Attacker.class, Inventory.class, new Converter<Attacker, Inventory>() {
			@Override
			public Inventory convert(final Attacker a) {
				if (a.getAttacker() instanceof Block) {
					final BlockState state = ((Block) a.getAttacker()).getState();
					if (state instanceof InventoryHolder)
						return ((InventoryHolder) state).getInventory();
				} else if (a.getAttacker() instanceof InventoryHolder) {
					return ((InventoryHolder) a.getAttacker()).getInventory();
				}
				return null;
			}
		});
	}
	
	@Override
	public void init(final Variable<?>[] vars, final int matchedPattern, final ParseResult parser) {}
	
	@Override
	protected Attacker[] getAll(final Event e) {
		return new Attacker[] {new Attacker(getAttacker(e))};
	}
	
	private static Object getAttacker(final Event e) {
		if (e instanceof EntityDamageByEntityEvent) {
			if (((EntityDamageByEntityEvent) e).getDamager() instanceof Projectile) {
				return ((Projectile) ((EntityDamageByEntityEvent) e).getDamager()).getShooter();
			}
			return ((EntityDamageByEntityEvent) e).getDamager();
		} else if (e instanceof EntityDamageByBlockEvent) {
			return ((EntityDamageByBlockEvent) e).getDamager();
		} else if (e instanceof EntityDeathEvent) {
			return getAttacker(((EntityDeathEvent) e).getEntity().getLastDamageCause());
		} else if (e instanceof VehicleDamageEvent) {
			return ((VehicleDamageEvent) e).getAttacker();
		} else if (e instanceof VehicleDestroyEvent) {
			return ((VehicleDestroyEvent) e).getAttacker();
		}
		return null;
	}
	
	@Override
	public Class<? extends Attacker> getReturnType() {
		return Attacker.class;
	}
	
	@Override
	public String getDebugMessage(final Event e) {
		if (e == null)
			return "attacker";
		return Skript.getDebugMessage(getSingle(e) == null ? null : getSingle(e).attacker);
	}
	
	@Override
	public String toString() {
		return "the attacker";
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public Class<?> acceptChange(final ChangeMode mode) {
		return DefaultChangers.inventoryChanger.acceptChange(mode);
	}
	
	@Override
	public void change(final Event e, final Variable<?> delta, final ChangeMode mode) {
		final Object a = getAttacker(e);
		Inventory invi = null;
		if (a instanceof InventoryHolder) {
			invi = ((InventoryHolder) a).getInventory();
		} else if (a instanceof Block && ((Block) a).getState() instanceof InventoryHolder) {
			invi = ((InventoryHolder) ((Block) a).getState()).getInventory();
		}
		if (invi == null)
			return;
		DefaultChangers.inventoryChanger.change(e, new SimpleLiteral<Inventory>(invi), delta, mode);
	}
	
}
