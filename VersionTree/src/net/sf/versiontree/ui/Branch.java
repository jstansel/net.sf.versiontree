/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, Andr� Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Jan Karstens <jan.karstens@web.de> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.ui;

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.data.IBranch;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Jan
 *
 * Custom made widget that represents a branch in the revision
 * graph.
 */
public class Branch extends Canvas {

	private static final int inset = 3;
	private static final int offsetBetweenStrings = 2;

	private IBranch branchData;

	private int width;
	private int height;

	private Color background;

	/**
	 * Creates a widget representing a branch.
	 * @param arg0 the parent Component
	 * @param arg1 style
	 */
	public Branch(Composite parent, int style) {
		super(parent, style);

		IPreferenceStore store =
			VersionTreePlugin.getDefault().getPreferenceStore();
		height = store.getInt(VersionTreePlugin.P_DEFAULT_ELEMENT_HEIGHT);
		width = store.getInt(VersionTreePlugin.P_DEFAULT_ELEMENT_WIDTH);

		// Parse background color
		String color =
			store.getString(VersionTreePlugin.P_BRANCH_BACKGROUNDCOLOR);
		int temp1 = color.indexOf(',');
		int temp2 = color.indexOf(',', temp1 + 1);
		background =
			new Color(
				null,
				Integer.valueOf(color.substring(0, temp1)).intValue(),
				Integer.valueOf(color.substring(temp1 + 1, temp2)).intValue(),
				Integer
					.valueOf(color.substring(temp2 + 1, color.length()))
					.intValue());

		// add paint listener
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Branch.this.paintControl(e);
			}
		});
		// add dispose listner
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				background.dispose();
			}
		});
	}

	/**
	 * Paints the control.
	 * @param e Paint Event
	 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle size = getBounds();

		gc.setBackground(background);
		gc.fillRoundRectangle(0, 0, size.width, size.height, 20, 20);

		int yOffset = inset;
		Point extent = gc.stringExtent(branchData.getName());
		gc.drawString(
			branchData.getName(),
			(size.width / 2) - (extent.x / 2),
			yOffset);
		yOffset += offsetBetweenStrings + extent.y;
		extent = gc.stringExtent(branchData.getBranchPrefix());
		gc.drawString(
			branchData.getBranchPrefix(),
			(size.width / 2) - (extent.x / 2),
			yOffset);

		gc.drawRoundRectangle(0, 0, size.width, size.height, 20, 20);
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(width, height);
	}

	/**
	 * Sets the given branch data. Also sets the tooltip and
	 * triggers a redraw().
	 * @param branchData
	 */
	public void setBranchData(IBranch branchData) {
		this.branchData = branchData;
		setToolTipText(branchData.getName());
		redraw();
	}

	/**
	 * Returns the branch data.
	 * @return branch data.
	 */
	public IBranch getBranchData() {
		return branchData;
	}

}
