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

package ch.njol.skript.api;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.api.exception.InitException;
import ch.njol.skript.api.exception.ParseException;
import ch.njol.skript.events.EvtRightclick;
import ch.njol.skript.lang.ExprParser.ParseResult;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.Literal;

/**
 * A SkriptEvent is like a condition. It is called when any of the registered events occurs.
 * An instance of this class should then check whether the event applies
 * (e.g. the rightclick event is included in the PlayerInteractEvent which also includes lefclicks, thus the SkriptEvent {@link EvtRightclick} checks whether it was a rightclick or
 * not).<br/>
 * It is also needed if the event has parameters.
 * 
 * @author Peter Güttinger
 * @see Skript#registerEvent(Class, Class, String...)
 * @see Skript#registerEvent(Class, Class[], String...)
 */
public abstract class SkriptEvent implements Expression, Debuggable {
	
	public static class SkriptEventInfo<E extends SkriptEvent> extends ExpressionInfo<E> {
		
		public Class<? extends Event>[] events;
		
		public SkriptEventInfo(final String[] patterns, final Class<E> c, final Class<? extends Event>[] events) {
			super(patterns, c);
			this.events = events;
		}
	}
	
	@Override
	public void init(final ch.njol.skript.lang.Variable<?>[] vars, final int matchedPattern, final ParseResult parseResult) throws InitException, ParseException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * called just after the constructor
	 * 
	 * @param args
	 * @return
	 */
	public abstract void init(final Literal<?>[] args, int matchedPattern, ParseResult parser);
	
	/**
	 * checks whether the given Event applies, e.g. the leftclick event is only part of the PlayerInteractEvent, and this checks whether the player rightclicked or not. This method
	 * will only be called for events this SkriptEvent is registered for.
	 * 
	 * @param e
	 * @return true in most cases.
	 */
	public abstract boolean check(Event e);
	
}
