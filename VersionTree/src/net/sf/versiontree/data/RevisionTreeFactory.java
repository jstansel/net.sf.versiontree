/*
 * VersionTree - Eclipse Plugin 
 * Copyright (C) 2003 Jan Karstens <jan.karstens@web.de>
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
package net.sf.versiontree.data;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RevisionTreeFactory {

	public static IBranch[] createRevisionTree(
		ILogEntry[] logs,
		String selectedRevision) {
		// return empty array if no logs available
		if (logs.length == 0) {
			return new IBranch[0];
		}

		// this array will hold the newly created revision objects
		HashMap revisions = new HashMap(logs.length);
		// the first revision in the HEAD branch
		IRevision rootRevision = null;

		// this hashmap will hold all branch objects
		HashMap branches = new HashMap();
		BranchData headBranch = new BranchData();
		headBranch.setName(IBranch.HEAD_NAME);
		headBranch.setBranchPrefix(IBranch.HEAD_PREFIX);
		branches.put(headBranch.getBranchPrefix(), headBranch);

		// scan logs array
		for (int i = 0; i < logs.length; i++) {
			// create new revision object
			ILogEntry logEntry = logs[i];
			IRevision currentRevision = new RevisionData(logEntry);
			revisions.put(currentRevision.getRevision(), currentRevision);

			// check if this is the root revision
			if (logEntry.getRevision().equals("1.1")
				|| logEntry.getRevision().equals("1.1.1.1"))
				rootRevision = currentRevision;

			// get the branch for this revision
			String branchPrefix = currentRevision.getBranchPrefix();
			BranchData branch = (BranchData) branches.get(branchPrefix);
			if (branch == null) {
				branch = new BranchData();
				branch.setBranchPrefix(branchPrefix);
				//TODO: set branch name from sticky tag
				branch.setName(branchPrefix);
				branches.put(branchPrefix, branch);
			}
			// add revision to this branch
			branch.addRevisionData(currentRevision);

			// check if this is the selected revision
			if (currentRevision.getRevision().equals(selectedRevision))
				currentRevision.setState(IRevision.STATE_SELECTED);

		}

		// now return the branches in an array
		IBranch[] branchArray = new IBranch[branches.size()];
		Iterator iter = branches.values().iterator();
		for (int i = 0; iter.hasNext(); i++) {
			BranchData branch = (BranchData) iter.next();
			branch.commitRevisionData();
			IRevision source =
				(IRevision) revisions.get(branch.getBranchSourceRevision());
			branch.setSource(source);
			branchArray[i] = (IBranch) branch;
		}
		return branchArray;
	}

}