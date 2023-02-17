/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. 
 * 
 * Created by christian.mueller@heig-vd.ch (CMR) 11.02.14
 * */

package com.cburch.logisim.gui.user;

import static com.cburch.logisim.gui.Strings.S;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.Normalizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cburch.logisim.gui.generic.LFrame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.TableLayout;
import com.cburch.logisim.util.UserManager;
import com.cburch.logisim.util.WindowMenuItemManager;

/**
 * This frame allows to change the active user and delete or add a user. It is
 * called from UserFrame dialog only.
 * 
 * @author christian.muller@heig-vd.ch
 * @since 2.11.0.t
 */
public class UserSelectionFrame extends LFrame.Dialog {
	/**
	 * Action listener for the frame buttons.
	 * 
	 * @author christian.muller@heig-vd.ch
	 * @since 2.11.0.t
	 */
	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object src = event.getSource();
			if (src == close) {
				WindowEvent e = new WindowEvent(UserSelectionFrame.this,
						WindowEvent.WINDOW_CLOSING);
				UserSelectionFrame.this.processWindowEvent(e);
			} else if (src == newUserBtn) {

				String users[] = newUserTextField.getText().split(
						UserManager.USERS_SEPARATOR);

				for (String username : users) {
					username = username.trim();
					if (username.length() > 0) {
						UserManager.addUser(deAccentSymb(username));
					}
				}

				newUserTextField.setText("");
				pack();
			} else if (src == activeUserDeleteBtn) {
				UserManager.deleteActiveUser();
				pack();
			}
		}
	}

	private static class RestrictedLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getMaximumSize() {
			return getPreferredSize();
		}
	}

	/**
	 * Set up the frame with its menu.
	 * 
	 * @author christian.muller@heig-vd.ch
	 * @since 2.11.0.t
	 * 
	 */
	private static class WindowMenuManager extends WindowMenuItemManager
			implements LocaleListener {
		private UserSelectionFrame window = null;

		WindowMenuManager() {
			super(S.get("userFrameMenuItem"), true);
			LocaleManager.addLocaleListener(this);
		}

		@Override
		public JFrame getJFrame(boolean create, java.awt.Component parent) {
			if (create) {
				if (window == null) {
					window = new UserSelectionFrame();
					frameOpened(window);
				}
			}
			return window;
		}

		public void localeChanged() {
			setText(S.get("userFrameMenuItem"));
		}
	}

	public static void initializeManager() {
		MENU_MANAGER = new WindowMenuManager();
	}

	/**
	 * Shows up the selection frame. It gets a modal exclusion type, else it
	 * would be frozen until UserFrame is closed.
	 */
	public static void showUserFrame() {
		JFrame frame = new UserSelectionFrame();
		frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.toFront();
	}

	private static final long serialVersionUID = 1L;

	private static WindowMenuManager MENU_MANAGER = null;

	private MyActionListener actionListener = new MyActionListener();

	private JButton close = new JButton();

	private JLabel activeUserLabel = new RestrictedLabel();
	private JButton activeUserDeleteBtn = new JButton();
	private JLabel newUserLabel = new JLabel();
	private JButton newUserBtn = new JButton();
	private JTextField newUserTextField = new JTextField();
	private JComponent userSelector;

	private UserSelectionFrame() {
        super(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

		/* Active user selector */
		userSelector = UserManager.createUserSelector();
		Box activeUserPanel = new Box(BoxLayout.X_AXIS);
		activeUserPanel.add(Box.createGlue());
		activeUserPanel.add(activeUserLabel);
		activeUserLabel.setMaximumSize(activeUserPanel.getPreferredSize());
		activeUserLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		activeUserPanel.add(userSelector);
		userSelector.setAlignmentY(Component.TOP_ALIGNMENT);
		activeUserPanel.add(Box.createGlue());

		/* Active user delete button */
		JPanel activeUserDelete = new JPanel();
		activeUserDelete.add(activeUserDeleteBtn);
		activeUserDeleteBtn.addActionListener(actionListener);

		/* New user panel */
		JPanel newUserPanel = new JPanel(new TableLayout(3));
		newUserTextField.setColumns(10);
		newUserPanel.add(newUserLabel);
		newUserPanel.add(newUserTextField);
		newUserPanel.add(newUserBtn);

		newUserBtn.addActionListener(actionListener);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(close);
		close.addActionListener(actionListener);

		Container contents = getContentPane();
		contents.setPreferredSize(new Dimension(450, 300));

		contents.setLayout(new BoxLayout(contents, BoxLayout.PAGE_AXIS));
		contents.add(activeUserPanel);
		contents.add(activeUserDelete);
		contents.add(newUserPanel);

		contents.add(buttonPanel, BorderLayout.SOUTH);

		activeUserLabel.setText(S.get("userActiveLabel"));
		activeUserDeleteBtn.setText(S.get("userDelete"));
		newUserLabel.setText(S.get("userAddNew"));
		newUserBtn.setText(S.get("userAddBtn"));
		close.setText(S.get("userSelAcceptButton"));

		pack();
	}

	/**
	 * Remove accents from letters, and convert non-alphanumeric symbols to
	 * underscores. Inspired by a StackOverflow answer:
	 * http://stackoverflow.com/
	 * questions/1008802/converting-symbols-accent-letters-to-english-alphabet
	 * 
	 * In addition it remove control char
	 * 
	 * @param str
	 *            string to "clean"
	 * @return the string where all the accents and non-alphanumeric symbols
	 *         have been removed
	 */
	public String deAccentSymb(String str) {
		String nfdNormalizedString = Normalizer.normalize(str,
				Normalizer.Form.NFD);
		String removedAccents = nfdNormalizedString.replaceAll("\\p{IsM}+", "");
		removedAccents = removedAccents.replaceAll("[\u0000-\u001f]", "");
		return removedAccents.replaceAll("[^A-Za-z0-9]", "_");
	}
}
