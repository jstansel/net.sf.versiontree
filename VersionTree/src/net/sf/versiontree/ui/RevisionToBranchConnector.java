/*
 * Created on 05.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.versiontree.ui;

import net.sf.versiontree.Globals;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RevisionToBranchConnector extends Canvas {

	private int arcSize = 9;

	int mainDirection = Globals.NORTH_SOUTH;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RevisionToBranchConnector(Composite parent, int style) {
		super(parent, style);
		// add paint listener
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				RevisionToBranchConnector.this.paintControl(e);
			}
		});
	}

	/**
	 * @param e Paint Event
	 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle bounds = this.getBounds();
		Point point1 = new Point(0, 0);
		Point point2;
		Point point3;
		Point point4 = new Point(bounds.width - 2, bounds.height);
		if (mainDirection == Globals.NORTH_SOUTH) {
			point2 = new Point(bounds.width - arcSize - 2, 0);
			point3 = new Point(bounds.width - 2, arcSize);
			gc.drawArc(point2.x-arcSize, point2.y, arcSize*2, arcSize*2, 0, 90);
		} else {
			point2 = new Point(2, bounds.height - arcSize - 2);
			point3 = new Point(bounds.width + 2, -2);
			gc.drawArc(point2.x, point2.y-arcSize, arcSize*2, arcSize*2, 180, 90);
		}
		gc.drawLine(point1.x, point1.y, point2.x, point2.y);
		gc.drawLine(point3.x, point3.y, point4.x, point4.y);
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		Rectangle bounds = this.getBounds();
		return new Point(bounds.height, bounds.height);
	}

	/**
	 * @return
	 */
	public int getMainDirection() {
		return mainDirection;
	}

	/**
	 * @param i
	 */
	public void setMainDirection(int i) {
		if (i == Globals.NORTH_SOUTH || i == Globals.WEST_EAST) {
			mainDirection = i;
			redraw();
		}
	}

}
