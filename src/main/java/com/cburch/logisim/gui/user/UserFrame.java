/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. 
 * 
 * Created by christian.mueller@heig-vd.ch (CMR) 11.02.14
 * */

package com.cburch.logisim.gui.user;

import static com.cburch.logisim.gui.Strings.S;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import com.cburch.logisim.gui.generic.LDialog;
import com.cburch.logisim.gui.generic.LFrame;
import com.cburch.logisim.util.UserListener;
import com.cburch.logisim.util.UserManager;

/**
 * Dialog asking user to accept the program terms of use. Logisim cannot be used
 * until this dialog is closed. Actually, dialog can be closed without accepting
 * the terms. UserFrame listens on current active user change to update the
 * corresponding label. It is automatically shown at project opening or through
 * the menu.
 * 
 * @author christian.muller@heig-vd.ch
 * @since 2.11.0.t
 */
public class UserFrame extends LDialog implements UserListener {

	/**
	 * Dialog buttons action listener.
	 * 
	 * @author christian.muller@heig-vd.ch
	 * @since 2.11.0.t
	 */
	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object src = event.getSource();
			if (src == acceptBtn) {
				WindowEvent e = new WindowEvent(UserFrame.this,
						WindowEvent.WINDOW_CLOSING);
				UserFrame.this.processWindowEvent(e);
			} else if (src == changeUserBtn) {
				UserSelectionFrame.showUserFrame();
			}
		}
	}

	/**
	 * Shows up the dialog in center of screen. Modality is set to prevent
	 * Logisim being used until the dialog is closed.
	 * 
	 * @param owner
	 *            The calling frame
	 */
	public static void showUserFrame(LFrame owner) {
		LDialog dialog = new UserFrame(owner, "User manager", true);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
	}

	private static final long serialVersionUID = 1L;

	private MyActionListener actionListener = new MyActionListener();
	private JLabel activeUserLabel = new JLabel();
	private JLabel activeUser = new JLabel();
	private JTextArea additionnalInfos = new JTextArea();
	private JButton changeUserBtn = new JButton();

	private JButton acceptBtn = new JButton();

	private UserFrame(LFrame owner, String title,
			boolean rootPaneCheckingEnabled) {
		super(owner, title, rootPaneCheckingEnabled);

		UserManager.addUserListener(this);

		setDefaultCloseOperation(HIDE_ON_CLOSE);

		/* Action listeners */
		changeUserBtn.addActionListener(actionListener);
		acceptBtn.addActionListener(actionListener);

		/* Add components to layout */
		Container contents = getContentPane();
		contents.setPreferredSize(new Dimension(450, 200));

		contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

		activeUserLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		contents.add(activeUserLabel);

		activeUser.setAlignmentX(Component.CENTER_ALIGNMENT);
		activeUser.setFont(new Font(activeUser.getFont().getName(), activeUser
				.getFont().getStyle(), 24));
		activeUser.setBackground(Color.WHITE);
		activeUser.setOpaque(true);
		Border paddingBorder = BorderFactory.createEmptyBorder(10, 50, 10, 50);
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		activeUser.setBorder(BorderFactory.createCompoundBorder(border,
				paddingBorder));
		contents.add(activeUser);

		additionnalInfos.setEditable(false);
		additionnalInfos.setMargin(new Insets(20, 20, 20, 20));
		additionnalInfos.setOpaque(false);
		additionnalInfos.setLineWrap(true);
		additionnalInfos.setWrapStyleWord(true);
		contents.add(additionnalInfos);

		JPanel buttonPanel = new JPanel();

		buttonPanel.add(changeUserBtn);

		acceptBtn.setFocusPainted(true);
		buttonPanel.add(acceptBtn);

		contents.add(buttonPanel);

		/* Set texts */
		activeUserLabel.setText(S.get("userActiveLabel"));
		activeUser.setText(UserManager.getUser().getName());
		additionnalInfos.setText(S.get("userAdditionnalInfos"));
		changeUserBtn.setText(S.get("userChangeBtn"));
		acceptBtn.setText(S.get("userAcceptButton"));

		pack();
	}

	@Override
	/**
	 * Update current active user label when it changes.
	 */
	public void userChanged() {
		activeUser.setText(UserManager.getUser().getName());
	}

}
