/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. 
 * 
 * Created by christian.mueller@heig-vd.ch (CMR) 11.02.14
 * 
 * */

package com.cburch.logisim.gui.main;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import com.cburch.logisim.circuit.Tracker;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;

/**
 * Tree node of a component in the tracker explorer.
 * 
 * @author christian.mueller@heig-vd.ch
 * @since 2.11.0.t
 */
public class TrackerTreeCompNode extends TrackerTreeNode {

	private static final long serialVersionUID = 1L;

	public TrackerTreeCompNode(Component comp, TrackerTreeCircuitNode parent) {
		setComponent(comp);
	}

	@Override
	public Enumeration<javax.swing.tree.TreeNode> children() {
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public ComponentFactory getComponentFactory() {
		return getComponent().getFactory();
	}

	@Override
	public int getIndex(TreeNode node) {
		return 0;
	}

	@Override
	public boolean hasValidIntegrity() {
		return getComponent().hasValidIntegrity();
	}

	@Override
	public boolean hasValidOwner(Tracker tracker) {
		return getComponent().hasValidOwner(tracker);
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

}
