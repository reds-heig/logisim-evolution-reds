/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. 
 * 
 * Created by christian.mueller@heig-vd.ch (CMR) 11.02.14
 * 
 * */

package com.cburch.logisim.gui.main;

import java.util.ArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.cburch.logisim.circuit.CircuitState;

/**
 * Tracker tree data model. Contains every node, who can be circuit or
 * components.
 * 
 * @author christian.mueller@heig-vd.ch
 * @since 2.11.0.t
 * 
 */
public class TrackerTreeModel implements TreeModel {
	private ArrayList<TreeModelListener> listeners;
	private TrackerTreeCircuitNode root;
	private CircuitState currentView;

	public TrackerTreeModel(CircuitState rootState) {
		this.listeners = new ArrayList<TreeModelListener>();
		this.root = new TrackerTreeCircuitNode(this, rootState.getCircuit(),
				rootState, null);

		root.loadChildren();
		this.currentView = null;
	}

	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	/**
	 * Find the path of a node. Wind up the tree to find the whole path of a
	 * node.
	 * 
	 * @param node
	 * @return Its path
	 */
	private TreePath findPath(Object node) {
		ArrayList<Object> path = new ArrayList<Object>();
		Object current = node;
		while (current instanceof TrackerTreeNode) {
			path.add(0, current);
			current = ((TrackerTreeNode) current).getParent();
		}
		if (current != null) {
			path.add(0, current);
		}
		return new TreePath(path.toArray());
	}

	protected void fireNodeChanged(Object node) {
		TreeModelEvent e = new TreeModelEvent(this, findPath(node));
		for (TreeModelListener l : listeners) {
			l.treeNodesChanged(e);
		}
	}

	protected void fireStructureChanged(Object node) {
		TreeModelEvent e = new TreeModelEvent(this, findPath(node));
		for (TreeModelListener l : listeners) {
			l.treeStructureChanged(e);
		}
	}

	/**
	 * Get the child at a specified index.
	 */
	public Object getChild(Object parent, int index) {
		if (parent instanceof TrackerTreeNode) {
			return ((TrackerTreeNode) parent).getChildAt(index);
		} else {
			return null;
		}
	}

	/**
	 * Get the number of children of a node.
	 */
	public int getChildCount(Object parent) {
		if (parent instanceof TrackerTreeNode) {
			return ((TrackerTreeNode) parent).getChildCount();
		} else {
			return 0;
		}
	}

	public CircuitState getCurrentView() {
		return currentView;
	}

	/**
	 * Get the index value of a child in the parent, given the node.
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof TrackerTreeNode
				&& child instanceof TrackerTreeNode) {
			return ((TrackerTreeNode) parent).getIndex((TrackerTreeNode) child);
		} else {
			return -1;
		}
	}

	public Object getRoot() {
		return root;
	}

	public CircuitState getRootState() {
		return root.getCircuitState();
	}

	/**
	 * Check if node is a component or an empty circuit.
	 */
	public boolean isLeaf(Object node) {

		if (node instanceof TrackerTreeNode) {
			return ((TrackerTreeNode) node).isLeaf();
		} else {
			return true;
		}
	}

	private TrackerTreeCircuitNode mapToNode(CircuitState state) {

		TreePath path = mapToPath(state);
		if (path == null) {
			return null;
		} else {
			return (TrackerTreeCircuitNode) path.getLastPathComponent();
		}
	}

	/**
	 * Get the path of a circuit state.
	 * 
	 * @param state
	 * @return The circuit path in the tree
	 */
	public TreePath mapToPath(CircuitState state) {

		if (state == null)
			return null;

		ArrayList<CircuitState> path = new ArrayList<CircuitState>();
		CircuitState current = state;
		CircuitState parent = current.getParentState();

		while (parent != null && parent != state) {
			path.add(current);
			current = parent;
			parent = current.getParentState();
		}

		Object[] pathNodes = new Object[path.size() + 1];
		pathNodes[0] = root;
		int pathPos = 1;
		TrackerTreeCircuitNode node = root;

		for (int i = path.size() - 1; i >= 0; i--) {

			current = path.get(i);
			TrackerTreeCircuitNode oldNode = node;

			for (int j = 0, n = node.getChildCount(); j < n; j++) {
				Object child = node.getChildAt(j);
				if (child instanceof TrackerTreeCircuitNode) {
					TrackerTreeCircuitNode circNode = (TrackerTreeCircuitNode) child;
					if (circNode.getCircuitState() == current) {
						node = circNode;
						break;
					}
				}
			}

			if (node == oldNode) {
				return null;
			}

			pathNodes[pathPos] = node;
			pathPos++;
		}

		return new TreePath(pathNodes);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	public void setCurrentView(CircuitState state) {
		if (currentView != state) {
			CircuitState oldView = currentView;
			currentView = state;

			TrackerTreeCircuitNode node1 = mapToNode(oldView);
			if (node1 != null)
				fireNodeChanged(node1);

			TrackerTreeCircuitNode node2 = mapToNode(state);
			if (node2 != null)
				fireNodeChanged(node2);
		}
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		throw new UnsupportedOperationException();
	}
}
