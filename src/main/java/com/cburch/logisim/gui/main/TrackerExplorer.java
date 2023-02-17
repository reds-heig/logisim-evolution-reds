/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/.
 * 
 * Created by christian.mueller@heig-vd.ch (CMR) 11.02.14
 * 
 * */

package com.cburch.logisim.gui.main;
import static com.cburch.logisim.data.Strings.S;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.SubcircuitFactory;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectEvent;
import com.cburch.logisim.proj.ProjectListener;

/**
 * This is the main view of the explorer. It appears in the GUI on the left pane
 * when clicking on the magnifying class or through the <i>View Tracker
 * explorer</i> menu option in the <i>Project</i> menu. The explorer shows all
 * the components in a tree view and changed the color of the label depending if
 * the component's owner is selected in the author list or not.
 * 
 * @author christian.muller@heig-vd.ch
 * @since 2.11.0.t
 */
class TrackerExplorer extends JPanel implements ProjectListener, MouseListener,
		ActionListener {
	/**
	 * Extended combobox with multiple selection possibility.
	 * 
	 * @author christian.muller@heig-vd.ch
	 * @since 2.11.0.t
	 */
	@SuppressWarnings("rawtypes")
	public class JComboCheckBox extends JComboBox {

		class ComboBoxRenderer implements ListCellRenderer {
			private JLabel defaultLabel;

			public ComboBoxRenderer() {
				setOpaque(true);
			}

			public java.awt.Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				if (value instanceof java.awt.Component) {
					java.awt.Component c = (java.awt.Component) value;
					if (isSelected) {
						c.setBackground(list.getSelectionBackground());
						c.setForeground(list.getSelectionForeground());
					} else {
						c.setBackground(list.getBackground());
						c.setForeground(list.getForeground());
					}
					return c;
				} else {
					if (defaultLabel == null)
						defaultLabel = new JLabel(
								S.get("trackerNoOwners"));
					else
						defaultLabel.setText(S.get("trackerNoOwners"));
					return defaultLabel;
				}
			}
		}

		private static final long serialVersionUID = 1L;

		public JComboCheckBox() {
			setup();
		}

		@SuppressWarnings("unchecked")
		public JComboCheckBox(ComboBoxModel aModel) {
			super(aModel);
			setup();
		}

		@SuppressWarnings("unchecked")
		public JComboCheckBox(JCheckBox[] items) {
			super(items);
			setup();
		}

		@SuppressWarnings("unchecked")
		public JComboCheckBox(Vector items) {
			super(items);
			setup();
		}

		@SuppressWarnings("unchecked")
		public void addItem(JCheckBox item) {
			super.addItem(item);
		}

		public String[] getSelectedItems() {
			ComboBoxModel model = super.getModel();

			int size = model.getSize();
			int j = 0;

			String[] items = new String[size];

			for (int i = 0; i < size; i++) {
				JCheckBox cb = ((JCheckBox) model.getElementAt(i));
				if (cb.isSelected())
					items[j++] = cb.getText();
			}

			String[] ret = new String[j];

			for (int k = 0; k < j; k++) {
				ret[k] = items[k];
			}

			return ret;
		}

		@SuppressWarnings("unchecked")
		private void setup() {
			setRenderer(new ComboBoxRenderer());
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (getSelectedItem() instanceof JCheckBox) {
						JCheckBox jcb = (JCheckBox) getSelectedItem();
						jcb.setSelected(!jcb.isSelected());
					}
				}
			});
		}
	}

	private static final long serialVersionUID = 1L;
	private Project project;
	private TrackerTreeModel model;
	private TrackerTreeRenderer renderer;
	private JTree tree;
	private JComboCheckBox authorSelector;
	private JLabel authorSelectorLabel;

	private JCheckBox showCompWithValidOwner;

	/**
	 * Tracker explorer panel constructor.
	 * 
	 * @param proj
	 *            The current open project the tracker must analyze.
	 */
	TrackerExplorer(Project proj) {
		super(new BorderLayout());

		this.project = proj;

		/* Author selector */
		JPanel authorPanel = new JPanel(new BorderLayout());

		authorSelectorLabel = new JLabel(
				S.get("trackerAuthorSelectorLabel"));
		authorPanel.add(authorSelectorLabel, BorderLayout.LINE_START);

		authorSelector = new JComboCheckBox();
		authorSelector.addActionListener(this);
		authorPanel.add(authorSelector, BorderLayout.CENTER);

		/* Valid owner comp option */
		showCompWithValidOwner = new JCheckBox();

		showCompWithValidOwner.setText("Show valid components");
		showCompWithValidOwner.setSelected(true);
		showCompWithValidOwner.setAlignmentX(LEFT_ALIGNMENT);

		// showCompWithValidOwner.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// TrackerExplorer.this.repaint();
		// }
		// });

		/* Construct header panel */
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

		headerPanel.add(authorPanel);
		// headerPanel.add(showCompWithValidOwner);

		add(headerPanel, BorderLayout.PAGE_START);

		/* Add data */
		model = new TrackerTreeModel(proj.getCircuitState());
		model.setCurrentView(project.getCircuitState());
		tree = new JTree(model);

		renderer = new TrackerTreeRenderer();

		tree.setCellRenderer(renderer);
		tree.addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {

			}

			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				TreePath path = event.getPath();
				if (path.getLastPathComponent() instanceof TrackerTreeCircuitNode) {
					TrackerTreeCircuitNode node = (TrackerTreeCircuitNode) path
							.getLastPathComponent();
					node.loadChildren();
					model.fireStructureChanged(node);
				}
			}
		});

		tree.addMouseListener(this);
		tree.setToggleClickCount(3);

		add(new JScrollPane(tree), BorderLayout.CENTER);

		proj.addProjectListener(this);

		refreshOwnerList();
		actionPerformed(null);
	}

	@Override
	/** If owner list selection changed, repaint the tree 
	 */
	public void actionPerformed(ActionEvent arg0) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				project.getTracker().setSelectedAuthors(
						authorSelector.getSelectedItems());
				expandCollapseNodes(((TrackerTreeCircuitNode) tree.getModel()
						.getRoot()));
				TrackerExplorer.this.repaint();
			}
		});
	}

	private void checkForPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			; // do nothing
		}
	}

	/**
	 * Goes through circuit to find all owners of component. If a subcircuit is
	 * found, calls itself recursively
	 * 
	 * @param circuit
	 *            Circuit to discover owners
	 * @return A LinkedList of discovered owners
	 */
	public ArrayList<String> discoverOwners(CircuitState circuit) {

		ArrayList<String> owners = new ArrayList<String>();

		for (Component comp : circuit.getCircuit().getNonWires()) {
			if (comp.getFactory() instanceof SubcircuitFactory) {
				SubcircuitFactory factory = (SubcircuitFactory) comp
						.getFactory();
				owners.addAll(discoverOwners(factory.getSubstate(circuit, comp)));
			} else {
				if (comp.getAttributeSet().containsAttribute(StdAttr.OWNER))
					owners.add(comp.getAttributeSet().getValue(StdAttr.OWNER));
			}
		}
		return owners;
	}

	/*

	 */
	/**
	 * Goes through the tree and expend all node who have components where owner
	 * does not correspond to author. It is recursively called on the children
	 * nodes. This is actually bugged and totally disabled, it does nothing.
	 * 
	 * @param node
	 *            Node to expand, usually the root
	 */
	private void expandCollapseNodes(TrackerTreeCircuitNode node) {

		// if (node.getChildCount() == 0)
		// return;
		//
		// /* Recursive call to expand/collapse child nodes */
		// for (TreeNode o : node.getChildren()) {
		// if (o instanceof TrackerTreeCircuitNode) {
		// TrackerTreeCircuitNode n = (TrackerTreeCircuitNode) o;
		// expandCollapseNodes(n);
		// }
		// }
		//
		// if (node instanceof TrackerTreeCircuitNode) {
		// TreePath nodePath = node.getPath();
		//
		// /* Expand node if necessary (bad owner or integrity) */
		// if (nodePath != null
		// && (model.getRoot().equals(node)
		// || node.hasDirtyHashChilds() || node
		// .hasDirtyOwnerChilds())) {
		// tree.expandPath(node.getPath());
		// /* Or collapse */
		// } else {
		// tree.collapsePath(node.getPath());
		// }
		// }

	}

	/**
	 * Get all owners of a circuit and subcircuit components. Removes
	 * duplicates.
	 * 
	 * @param circuit
	 *            Circuit on which to find owners
	 * @return An array of all the circuit's components owners
	 */
	public String[] getOwners(CircuitState circuit) {
		ArrayList<String> ownersLL = discoverOwners(circuit);

		ownersLL = new ArrayList<String>(new HashSet<String>(ownersLL));

		return ownersLL.toArray(new String[ownersLL.size()]);
	}

	public void mouseClicked(MouseEvent e) {

		if (e.getClickCount() == 1) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if (path != null) {
				Object last = path.getLastPathComponent();

				if (last instanceof TrackerTreeNode
						&& project
								.getCircuitState()
								.getCircuit()
								.getNonWires()
								.contains(
										((TrackerTreeNode) last).getComponent())) {

					if (last instanceof TrackerTreeCompNode) {
						TrackerTreeCompNode node = (TrackerTreeCompNode) last;
						project.getFrame().viewComponentAttributes(
								((TrackerTreeCircuitNode) node.getParent())
										.getCircuitState().getCircuit(),
								node.getComponent());
					}

					if (last instanceof TrackerTreeCircuitNode) {
						TrackerTreeCircuitNode node = (TrackerTreeCircuitNode) last;
						project.getFrame().viewComponentAttributes(
								node.getCircuitState().getCircuit(),
								node.getComponent());
					}
				}
			}
		}

		if (e.getClickCount() == 2) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if (path != null) {
				Object last = path.getLastPathComponent();
				if (last instanceof TrackerTreeCircuitNode) {
					TrackerTreeCircuitNode node;
					node = (TrackerTreeCircuitNode) last;

					if (node.isRoot()) {
						project.setCircuitState(node.getCircuitState()
								.getParentState());
					} else {
						project.setCircuitState(node.getCircuitState());
					}
				}

				if (last instanceof TrackerTreeCompNode) {
					TrackerTreeCompNode node;
					node = (TrackerTreeCompNode) last;
					project.setCircuitState(((TrackerTreeCircuitNode) node
							.getParent()).getCircuitState());
				}
			}
		}
	}

	//
	// MouseListener methods
	//
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		requestFocus();
		checkForPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		checkForPopup(e);
	}

	//
	// ProjectListener methods
	//
	/**
	 * If project changes, the model of the tree and the user list have both to
	 * be updated.
	 * 
	 */
	public void projectChanged(ProjectEvent event) {
		int action = event.getAction();

		/* Change the root circuit in the tracker explorer */
		if (action == ProjectEvent.ACTION_SET_STATE) {
			CircuitState root = project.getTracker().getCircuitState();
			if (model.getRootState() != root) {
				model = new TrackerTreeModel(root);
				tree.setModel(model);
			}
			model.setCurrentView(project.getCircuitState());
			TreePath path = model.mapToPath(project.getCircuitState());
			if (path != null) {
				tree.scrollPathToVisible(path);
			}
		}

		/*
		 * Here we check the event source to know if we have to refresh the
		 * tracker view. We try to refresh at least as possible because it makes
		 * a lot of overhead and it could be uncomfortable for the user to have
		 * the node expand/collapse at every minor action.
		 */
		if (action == ProjectEvent.ACTION_SET_CURRENT
				|| action == ProjectEvent.ACTION_COMPLETE
				|| action == ProjectEvent.UNDO_COMPLETE) {
			refreshOwnerList();
			expandCollapseNodes(((TrackerTreeCircuitNode) tree.getModel()
					.getRoot()));
		}
	}

	/**
	 * Refresh the <i>presumed authors</i> user list.
	 */
	private void refreshOwnerList() {
		boolean first = true;

		authorSelector.removeAllItems();

		for (String username : getOwners(project.getSimulator()
				.getCircuitState())) {

			/* Select active authors */
			Boolean checked = false;

			if (project.getTracker().getSelectedAuthors() != null) {
				for (String usr : project.getTracker().getSelectedAuthors()) {
					if (username.equals(usr))
						checked = true;
				}
			}

			/*
			 * The default behavior of combobox is to select first item, this
			 * will inverse the state of it, so we make a double inversion. Best
			 * would be to override default behavior instead of this.
			 */
			if (first)
				checked = !checked;
			first = false;

			authorSelector.addItem(new JCheckBox(username, checked));
		}
	}
}
