/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, Andr� Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Andr� Langhorst <andre@masse.de> - initial implementation
 *     Jan Karstens <jan.karstens@web.de> - extensions
 *******************************************************************************/
package net.sf.versiontree.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * Data structure for walking version trees 
 * @author Andre  */
public class BranchTree {

	private int numberOfBranches = 0;

	private IBranch headBranch;
	private IRevision rootRevision;

	private HashMap branches;
	private HashMap revisions;

	public boolean isEmpty() {
		return numberOfBranches == 0;
	}
	
	/**
	 * Sets up all data structures from CVS Logs 
	 */
	public BranchTree(ILogEntry[] logs, String selectedRevision) {
		// no logs, empty tree
		if (logs.length <= 0)
			return;

		// basic initializations
		revisions = new HashMap(logs.length);
		branches = new HashMap((int) (Math.ceil(logs.length / 20)));

		// set up branches, revision
		setUpHashMaps(logs, selectedRevision);
		// build complete tree structure
		buildCompleteTreeStructure();
	}
	
	private void setUpHashMaps(ILogEntry[] logs, String selectedRevision) {
		headBranch = new BranchData(IBranch.HEAD_NAME, IBranch.HEAD_PREFIX);
		branches.put(headBranch.getBranchPrefix(), headBranch);

		// create revisions hashmap and branches + remember selected
		for (int i = 0; i < logs.length; i++) {
			ILogEntry logEntry = logs[i];
			IRevision currentRevision = new RevisionData(logEntry);
			revisions.put(currentRevision.getRevision(), currentRevision);
			ifIsRootSetRoot(logEntry, currentRevision);
			// check if this is the selected revision
			ifIsActiveRevisionInIDErememberIt(
				selectedRevision,
				currentRevision);
			createBranches(logEntry);
		}
		
		// connect revisions to branches
		headBranch.addChild(rootRevision);
		for (Iterator iter = revisions.values().iterator(); iter.hasNext();) {
			IRevision currentRevision = (IRevision) iter.next();
			String branchPrefix = currentRevision.getBranchPrefix();
			BranchData branch = (BranchData) branches.get(branchPrefix);
			if (branch == null) {
				if (currentRevision.getRevision() == IRevision.INITIAL_REVISION) {
					branch = (BranchData) branches.get(IBranch.HEAD_PREFIX);
				} else {
					// no branch tag! create adhoc branch
					int branchNumber = Integer.parseInt(branchPrefix
							.substring(branchPrefix.lastIndexOf(".") + 1));
					branch = createBranch(branchPrefix, branchNumber, "<n/a>");
					String parentPrefix = branchPrefix.substring(0,
							branchPrefix.lastIndexOf("."));
					IRevision branchParent = (IRevision) revisions
							.get(parentPrefix);
					branchParent.addChild(branch);
				}
			}
			// get/create the branch and add revision
			branch.addRevisionData(currentRevision);
		}
	}
	
	/**
	 * Creates all branches for the given log entry (including
	 * empty branches).
	 * @param logEntry
	 */
	private void createBranches(ILogEntry logEntry) {
		int branchNumber = 0;
		CVSTag[] tags = logEntry.getTags();
		for (int j = tags.length - 1; j >= 0; j--) {
			if (tags[j].getType() == CVSTag.BRANCH) {
				branchNumber += 2;
				createBranch(logEntry.getRevision() + "." + branchNumber, branchNumber, tags[j].getName());
			}
		}
	}
	
	private BranchData createBranch(String branchPrefix, int branchNumber, String name) {
		BranchData branch = new BranchData();
		branch.setBranchPrefix(branchPrefix);
		branch.setName(name);
		branches.put(branchPrefix, branch);
		return branch;
	}

	private void buildCompleteTreeStructure() {
		for (Iterator iter = branches.values().iterator(); iter.hasNext();) {
			IBranch outerBranch = (IBranch) iter.next();

			// for all revisions of a branch in order
			List revs = outerBranch.getRevisions();
			Collections.sort(revs);

			Iterator innerIter = revs.iterator();
			IRevision prev = null;
			while (innerIter.hasNext()) {
				IRevision innerRevision = (IRevision) innerIter.next();
				// ... the next revision is a child
				if (prev != null)
					prev.addChild(innerRevision);
				// ... all branches are children
				for (Iterator innerBranches =
					findBranchesForRevision(innerRevision).iterator();
					innerBranches.hasNext();
					) {
					IBranch branchChild = (IBranch) innerBranches.next();
					innerRevision.addChild(branchChild);
					branchChild.setParent(innerRevision);
				}
				// previous revision is parent,
				innerRevision.setParent(
					prev == null
						? (ITreeElement) outerBranch
						: (ITreeElement) prev);
				if (prev == null)
					outerBranch.addChild(innerRevision);
				prev = innerRevision;
			}
		}
	}

	public List findBranchesForRevision(IRevision rev) {
		List sortedBranches = new ArrayList();
		int branchId = 2;
		IBranch branch = (IBranch) branches.get(rev.getRevision()+ "." + branchId);
		while (branch != null) {
			sortedBranches.add(branch);
			branchId += 2;
			branch = (IBranch) branches.get(rev.getRevision()+ "." + branchId);
		}
		return sortedBranches;
	}

	/** 
	 * the active revision currently has a "*" currently displayed
	 */
	private void ifIsActiveRevisionInIDErememberIt(
		String selectedRevision,
		IRevision currentRevision) {
		if (currentRevision.getRevision().equals(selectedRevision))
			currentRevision.setState(IRevision.STATE_CURRENT);
	}
	
	/** 
	 * the root revision is recognized by its revision number
	 */
	private void ifIsRootSetRoot(
		ILogEntry logEntry, IRevision currentRevision) {
		// check if this is the root revision. The repository can have 1.1 only or 1.1 and 1.1.1.1!
		// 1.1.1.1 rulez!
		if (logEntry.getRevision().equals(IRevision.FIRST_REVISION)
				|| logEntry.getRevision().equals(IRevision.INITIAL_REVISION)) {
			if (rootRevision == null) {
				rootRevision = currentRevision;
			} else if (!rootRevision.equals(IRevision.INITIAL_REVISION)){
				rootRevision = currentRevision;
			}
		}
	}

	/** 
	 * gets head Branch for decension 
	 */
	public IBranch getHeadBranch() {
		return headBranch;
	}

}
