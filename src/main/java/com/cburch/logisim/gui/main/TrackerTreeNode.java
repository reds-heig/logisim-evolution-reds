/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. 
 * 
 * Created by christian.mueller@heig-vd.ch (CMR) 11.02.14
 * 
 * */

package com.cburch.logisim.gui.main;

import javax.swing.tree.DefaultMutableTreeNode;

import com.cburch.logisim.circuit.Tracker;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.instance.StdAttr;

/**
 * Abstract representation of a node. A node is either a circuit or a component.
 * 
 * @author christian.mueller@heig-vd.ch
 * @since 2.11.0.t
 */
public abstract class TrackerTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	private Component comp;

	public Component getComponent() {
		return comp;
	}

	public abstract ComponentFactory getComponentFactory();

	public abstract boolean hasValidIntegrity();

	public abstract boolean hasValidOwner(Tracker tracker);

	public boolean isCurrentView(TrackerTreeModel model) {
		return false;
	}

	public boolean isRoot() {
		return false;
	}

	public void setComponent(Component comp) {
		this.comp = comp;
	}

	@Override
	public String toString() {
		String ret = "";

		if (getComponent() == null)
			return ret;

		ret += getComponent().getFactory().getName();

		/* Add label */
		if (getComponent().getAttributeSet().containsAttribute(StdAttr.LABEL)
				&& !getComponent().getAttributeSet().getValue(StdAttr.LABEL)
						.equals(""))
			ret += " - "
					+ getComponent().getAttributeSet().getValue(StdAttr.LABEL);

		/* Add owner */
		if (getComponent().getAttributeSet().containsAttribute(StdAttr.OWNER))
			ret += " ("
					+ getComponent().getAttributeSet().getValue(StdAttr.OWNER)
					+ ")";

		return ret;
	}
}
