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
package net.sf.versiontree.layout.cmd;

import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.layout.LayoutIntvalAlgoContext;

import org.eclipse.swt.graphics.Point;

/**
 * @author Andre
 * Performs operations using IRevision objects
 */
public class LayoutAlgoRevisionPreCmd implements ICommand {
	private LayoutIntvalAlgoContext ctx;

	public LayoutAlgoRevisionPreCmd(LayoutIntvalAlgoContext ctx) {
		this.ctx = ctx;

	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.ui.Command#execute(java.lang.Object)
	 */
	public void execute(Object obj) {
		IRevision rev = (IRevision) obj;
		
		ctx.position.y++;
		/* collision check using all branches */
		ctx.position.y += ctx.strategy.algorithm(ctx.position, rev.numBranchTags(), ctx.ivManager).y;
		/* allocate new interval */
		ctx.ivManager.set(ctx.position.x,
			new Point(ctx.position.y, ctx.position.y ), // revisions always have size 1
			rev);
	}

}
