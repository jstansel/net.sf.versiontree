/*
 * VersionTree - Eclipse Plugin 
 * Copyright (C) 2003 Andr� Langhorst <andre@masse.de>
 * This program is free software; you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free Software 
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program; if not, write to the 
 * Free Software Foundation, Inc., 
 * 59 TemplePlace - Suite 330, Boston, MA 02111-1307, USA 
 */
package net.sf.versiontree.layout.interval;


/**
 * @author Andre
 * Value class for storing pairs of Points and arbitrary objects
 */
public class IntervalObjectPair {
	/**
	 * @param intval
	 * @param obj
	 */
	public IntervalObjectPair(Interval intval, Object obj) {
		i = intval;
		o = obj;
	}
	public Interval i;
	public Object o;
}
