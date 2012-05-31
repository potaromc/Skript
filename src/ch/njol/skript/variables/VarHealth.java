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

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.api.Changer.ChangeMode;
import ch.njol.skript.api.Getter;
import ch.njol.skript.lang.ExprParser.ParseResult;
import ch.njol.skript.lang.SimpleVariable;
import ch.njol.skript.lang.Variable;

/**
 * @author Peter Güttinger
 * 
 */
public class VarHealth extends SimpleVariable<Float> {
	
	static {
		Skript.registerVariable(VarHealth.class, Float.class, "health [of %livingentities%]", "%livingentities%'[s] health");
	}
	
	private Variable<LivingEntity> entities;
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(final Variable<?>[] vars, final int matchedPattern, final ParseResult parser) {
		entities = (Variable<LivingEntity>) vars[0];
	}
	
	@Override
	public String getDebugMessage(final Event e) {
		return "health of " + entities.getDebugMessage(e);
	}
	
	@Override
	protected Float[] getAll(final Event e) {
		return entities.getArray(e, Float.class, new Getter<Float, LivingEntity>() {
			@Override
			public Float get(final LivingEntity entity) {
				return Float.valueOf(1f / 2 * entity.getHealth());
			}
		});
	}
	
	@Override
	public Class<?> acceptChange(final ChangeMode mode) {
		return Float.class;
	}
	
	@Override
	public void change(final Event e, final Variable<?> delta, final ChangeMode mode) {
		int s = 0;
		if (mode != ChangeMode.CLEAR)
			s = Math.round(((Float) delta.getSingle(e)).floatValue() * 2);
		switch (mode) {
			case CLEAR:
			case SET:
				for (final LivingEntity entity : entities.getArray(e)) {
					entity.setHealth(s);
				}
				return;
			case ADD:
				for (final LivingEntity entity : entities.getArray(e)) {
					entity.setHealth(entity.getHealth() + s);
				}
				return;
			case REMOVE:
				for (final LivingEntity entity : entities.getArray(e)) {
					entity.setHealth(entity.getHealth() - s);
				}
				return;
		}
	}
	
	@Override
	public Class<? extends Float> getReturnType() {
		return Float.class;
	}
	
	@Override
	public String toString() {
		return "the health of " + entities;
	}
	
	@Override
	public boolean isSingle() {
		return entities.isSingle();
	}
	
}
