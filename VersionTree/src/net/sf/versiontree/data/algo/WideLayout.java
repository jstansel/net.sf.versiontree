/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, Andr� Langhorst.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Andr� Langhorst <andre@masse.de> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.data.algo;

import java.util.HashMap;
import java.util.List;

import net.sf.versiontree.data.AbstractVersionTreeHelper;
import net.sf.versiontree.data.BranchTree;
import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;


/**
 * Produces "wide" trees, revisions belonging to a branch or close
 * together, while branches are moved right until they fit into
 * the version tree layout
  * @author Andre
 */
public class WideLayout extends AbstractLayout {
	private HashMap<Integer, Integer> reservedHeight;

	public WideLayout() {
		reservedHeight  =  new HashMap<Integer, Integer>();
	}

	public void walk(ITreeElement treeElement, int x, int y) {
		if (treeElement instanceof IRevision) {
			IRevision succ = AbstractVersionTreeHelper.getRevisionFromTreeElements(treeElement.getChildren());
			if (succ != null) {
				// if is not last revision step down
				walk(succ, x, y+1);
			}
			foreachBranchStepDownAndOptimize(x, y, ((IRevision)treeElement));
		} else if (treeElement.getChildren().size() != 0) {
			walk(treeElement.getChildren().get(0), x, y+1);
		}
		m_delegate.draw(treeElement,x,y);
		reservedHeight.put(new Integer(x), new Integer(y));
	}

	private void foreachBranchStepDownAndOptimize(int x, int y, IRevision rev) {
		int x_tmp = x;
		// always start drawing the branch with the lowest height
		List<IBranch> branches = AbstractVersionTreeHelper.getHeightSortedBranchesForRevision(rev, emptyBranches, naBranches, branchFilter);

		for (IBranch branch : branches) {
			int d = 0;
			do {
				if (reservedHeight.containsKey(new Integer(x_tmp + 1))) {
					d = y + branch.getHeight() + 1 // +1 == include branch
						- reservedHeight.get(new Integer(x_tmp + 1)).intValue();
				} else {
					d = 0;
				}
				if (d <= 0) {
					// space for new branch available
					walk(branch, x_tmp+1, y);
				} else {
					// there is no space free but we block a
					// line straight until we can draw
					reservedHeight.put(new Integer(++x_tmp), new Integer(y));
				}
			} while (d > 0);
		}
	}

	public void walk(BranchTree bt) {
		if (bt != null) {
			IBranch headBranch = bt.getHeadBranch();
			if (headBranch != null) {
				walk(headBranch,0,0);
			}
		}
		reservedHeight.clear();
	}
}
