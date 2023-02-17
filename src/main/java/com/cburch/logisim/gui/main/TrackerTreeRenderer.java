/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/.
 *
 * Created by christian.mueller@heig-vd.ch (CMR) 11.02.14
 *
 * */

package com.cburch.logisim.gui.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.cburch.logisim.gui.generic.ProjectExplorer;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.util.GraphicsUtil;

/**
 * Renders the model to show the explorer tree.
 *
 * @author christian.mueller@heig-vd.ch
 * @since 2.11.0.t
 */
public class TrackerTreeRenderer extends DefaultTreeCellRenderer {

	private static class RendererIcon implements Icon {
		private ComponentFactory factory;
		private boolean isCurrentView;

		RendererIcon(ComponentFactory factory, boolean isCurrentView) {
			this.factory = factory;
			this.isCurrentView = isCurrentView;
		}

		public int getIconHeight() {
			return 20;
		}

		public int getIconWidth() {
			return 20;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			ComponentDrawContext context = new ComponentDrawContext(c, null,
					null, g, g);
			factory.paintIcon(context, x, y, factory.createAttributeSet());

			// draw magnifying glass if appropriate
			if (isCurrentView) {
				int tx = x + 13;
				int ty = y + 13;
				int[] xp = { tx - 1, x + 18, x + 20, tx + 1 };
				int[] yp = { ty + 1, y + 20, y + 18, ty - 1 };
				g.setColor(ProjectExplorer.MAGNIFYING_INTERIOR);
				g.fillOval(x + 5, y + 5, 10, 10);
				g.setColor(Color.BLACK);
				g.drawOval(x + 5, y + 5, 10, 10);
				g.fillPolygon(xp, yp, xp.length);
			}
		}
	}

	private static final long serialVersionUID = 1L;

	@Override
	/**
	 * Makes a renderer for a single node. The renderer is a JLabel.
	 * If the node has invalid owner or integrity (or its children have),
	 * the color of the label will be adapted (red) to show the error in the tree.
	 * One option to implement is to hide all nodes that have valid attributes. This
	 * can unfortunately not be done in the renderer, due to a lack of java API. If
	 * you want to implement this, you have to make a copy of the model who exclude the
	 * valid nodes.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component renderer = super.getTreeCellRendererComponent(tree, value,
				selected, expanded, leaf, row, hasFocus);
		TrackerTreeModel model = (TrackerTreeModel) tree.getModel();

		if (renderer instanceof JLabel && value instanceof TrackerTreeNode) {
			JLabel label = (JLabel) renderer;
			TrackerTreeNode node = (TrackerTreeNode) value;

			if (!node.isRoot()) {

				/* Customize layout to signal invalid owner/integrity */
				if (node.hasValidOwner(model.getRootState().getProject()
						.getTracker()))
					label.setForeground(GraphicsUtil.GREEN_DARK);
				else
					label.setForeground(GraphicsUtil.RED);

				if (node.hasValidIntegrity()) {
					label.setOpaque(false);
				} else {
					label.setForeground(Color.WHITE);
					label.setBackground(GraphicsUtil.RED);
					label.setOpaque(true);
				}
			}

			/* Add component/circuit icon */
			ComponentFactory factory = node.getComponentFactory();
			if (factory != null) {
				label.setIcon(new RendererIcon(factory, node
						.isCurrentView(model)));
			}
		}

		return renderer;
	}
}
