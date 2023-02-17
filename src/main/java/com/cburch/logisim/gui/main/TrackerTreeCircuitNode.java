/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. 
 * 
 * Created by christian.mueller@heig-vd.ch (CMR) 11.02.14
 * 
 * */

package com.cburch.logisim.gui.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitAttributes;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.SubcircuitFactory;
import com.cburch.logisim.circuit.Tracker;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.instance.StdAttr;

/**
 * Tree node of a circuit in the tracker explorer.
 * 
 * @author christian.mueller@heig-vd.ch
 * @since 2.11.0.t
 */
public class TrackerTreeCircuitNode extends TrackerTreeNode implements
		CircuitListener, AttributeListener, Comparator<Component> {

	private static class CompareByName implements Comparator<Object> {
		public int compare(Object a, Object b) {
			return a.toString().compareToIgnoreCase(b.toString());
		}
	}

	private static class CompareByObjType implements Comparator<Object> {
		public int compare(Object a, Object b) {
			return a.getClass().toString()
					.compareToIgnoreCase(b.getClass().toString());
		}
	}

	private static final long serialVersionUID = 1L;

	private TrackerTreeModel model;
	private CircuitState circuitState;
	private Circuit circuit;

	public TrackerTreeCircuitNode(TrackerTreeModel model, Circuit circuit,
			CircuitState circuitState, Component subcircComp) {

		this.model = model;
		this.circuitState = circuitState;
		this.circuit = circuit;

		setComponent(subcircComp);
		// this.circuit = ((SubcircuitFactory)
		// subcircComp.getFactory()).getSubcircuit();
		// setParent(parent);

		setUserObject(this.toString());

		circuitState.getCircuit().addCircuitListener(this);
		if (subcircComp != null) {
			subcircComp.getAttributeSet().addAttributeListener(this);
		} else {
			circuitState.getCircuit().getStaticAttributes()
					.addAttributeListener(this);
		}

		add(new DefaultMutableTreeNode("Loading...", false));
	}

	//
	// AttributeListener methods
	//
	public void attributeListChanged(AttributeEvent e) {
	}

	// @Override
	// public boolean isRoot() {
	// return root;
	// }

	public void attributeValueChanged(AttributeEvent e) {
		Object attr = e.getAttribute();
		if (attr == CircuitAttributes.CIRCUIT_LABEL_ATTR
				|| attr == StdAttr.LABEL) {
			model.fireNodeChanged(this);
		}
	}

	public void circuitChanged(CircuitEvent event) {
		int action = event.getAction();
		if (action == CircuitEvent.ACTION_SET_NAME) {
			model.fireNodeChanged(this);
		} else if (action != CircuitEvent.ACTION_INVALIDATE) {
			loadChildren();
			model.fireStructureChanged(this);
		}
	}

	public int compare(Component a, Component b) {
		if (a != b) {
			String aName = a.getFactory().getDisplayName();
			String bName = b.getFactory().getDisplayName();
			int ret = aName.compareToIgnoreCase(bName);
			if (ret != 0)
				return ret;
		}
		return a.getLocation().toString().compareTo(b.getLocation().toString());
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	public CircuitState getCircuitState() {
		return circuitState;
	}

	@Override
	public ComponentFactory getComponentFactory() {
		return circuitState.getCircuit().getSubcircuitFactory();
	}

	// @Override
	// public int getChildCount() {
	// return children.size();
	// }

	// @Override
	// public MutableTreeNode getParent() {
	// return parent;
	// }

	// @Override
	// public int getIndex(TreeNode node) {
	// return children.indexOf(node);
	// }

	@Override
	public boolean hasValidIntegrity() {
		return getCircuitState().getCircuit().hasValidIntegrity();
	}

	@Override
	public boolean hasValidOwner(Tracker tracker) {
		return getCircuitState().getCircuit().hasValidOwner(tracker);
	}

	// @Override
	// public Enumeration<TreeNode> children() {
	// return Collections.enumeration(children);
	// }

	@Override
	public boolean isCurrentView(TrackerTreeModel model) {
		return model.getCurrentView() == circuitState;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isRoot() {
		return getParent() == null;
	}

	/**
	 * Compute and refresh the list of children of the node.
	 */
	public void loadChildren() {

		ArrayList<TrackerTreeNode> newChildren = new ArrayList<TrackerTreeNode>();

		/* Add children components to lists */
		for (Component comp : circuit.getNonWires()) {

			/* Subcircuits */
			if (comp.getFactory() instanceof SubcircuitFactory) {

				CircuitState substate = ((SubcircuitFactory) comp.getFactory())
						.getSubstate(circuitState, comp);

				TrackerTreeNode toAdd = comp
						.getTrackerExplorerCircuitNode(model,
								((SubcircuitFactory) comp.getFactory())
										.getSubcircuit(), substate);
				if (toAdd != null) {
					newChildren.add(toAdd);
				}

				/* Components */
			} else {
				TrackerTreeNode toAdd = comp.getTrackerExplorerCompNode(this); // model.mapComponentToNode(comp,
																				// this);
				if (toAdd != null) {
					newChildren.add(toAdd);
				}
			}
		}

		/* Sort the list */
		Collections.sort(newChildren, new CompareByName());
		Collections.sort(newChildren, new CompareByObjType());

		setChildren(newChildren);
	}

	private void setChildren(List<TrackerTreeNode> children) {
		removeAllChildren();
		setAllowsChildren(children.size() > 0);
		for (MutableTreeNode node : children) {
			add(node);
		}
		model.fireStructureChanged(this);
	}

	@Override
	public String toString() {

		if (isRoot()) {
			return circuitState.getCircuit().toString();
		}

		return super.toString();

	}

}
