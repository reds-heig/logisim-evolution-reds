/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.util;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The visual list to select the current active user.
 * 
 * @author christian.mueller@heig-vd.ch
 * @since 2.11.0.t
 */
@SuppressWarnings("rawtypes")
class UserSelector extends JList implements ListSelectionListener {

	/**
	 * An option of the UserSelector list. Each line represent a single user.
	 * 
	 * @author christian.mueller@heig-vd.ch
	 * @since 2.11.0.t
	 */
	private static class UserOption implements Runnable {
		private User user;
		private String text;

		UserOption(User user) {
			this.user = user;
			this.text = user.getName();
		}

		public void run() {
			if (!UserManager.getUser().equals(user)) {
				UserManager.setUser(user);
			}
		}

		@Override
		public String toString() {
			return text;
		}
	}

	private static final long serialVersionUID = 1L;

	private UserOption[] items;

	UserSelector() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		refreshUserList();
		addListSelectionListener(this);
	}

	/**
	 * Create a model for the selector.
	 * 
	 * @param users
	 *            User array to add to the selector
	 * @return The new model
	 */
	@SuppressWarnings("unchecked")
	private DefaultListModel createModel(User[] users) {
		DefaultListModel model = new DefaultListModel();
		items = new UserOption[users.length];
		int i = 0;
		for (User u : users) {
			items[i] = new UserOption(u);
			model.addElement(items[i++]);
		}
		return model;
	}

	/**
	 * Refresh user list in the selector. This should be called each time the
	 * user changes. Visible row count is modified to better fit the number of
	 * users, but has a max size of 8.
	 */
	@SuppressWarnings("unchecked")
	public void refreshUserList() {
		setVisibleRowCount(Math.min(User.getAvailableUsers().length, 8));
		setModel(createModel(User.getAvailableUsers()));
	}

	/**
	 * Refreshes the layout when selector value is changed.
	 */
	public void valueChanged(ListSelectionEvent e) {
		UserOption opt = (UserOption) getSelectedValue();
		if (opt != null) {
			SwingUtilities.invokeLater(opt);
		}
	}
}
