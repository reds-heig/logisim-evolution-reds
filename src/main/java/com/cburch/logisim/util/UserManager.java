/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.util;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.cburch.logisim.prefs.AppPreferences;

/**
 * The user manager manages the active user selection and which users are
 * available.
 * 
 * @author christian.mueller@heig-vd.ch
 * @since 2.11.0.t
 */
public class UserManager {
  /**
   * Create a new user if he does'nt exist yet. Then set the new user as
   * active. Fires UserListChanged.
   * 
   * @param name
   *          The new user's name
   */
  public static void addUser(String name) {

    User newUser = User.findByName(name);

    if (newUser == null) {
      newUser = User.newUser(name);
    }
    setUser(newUser);
    fireUserListChanged();
  }

  /**
   * Add a listener to the current active user.
   * 
   * @param l
   *          The new listener
   */
  public static void addUserListener(UserListener l) {
    listeners.add(l);
  }

  /**
   * Create a new user selector.
   * 
   * @return The user selector scroll pane
   */
  public static JComponent createUserSelector() {

    userSelector = new UserSelector();
    fireUserListChanged();
    return new JScrollPane(userSelector);
  }

  /**
   * Delete the current active user from the user list. Set active user back
   * to the unknown user.
   */
  public static void deleteActiveUser() {
    if (getUser().getName().equals(UNKNOWN_USER_NAME))
      return;

    getUser().remove();
    setUser(User.UNKNOWN_USER);
    fireUserListChanged();
  }

  /**
   * Notify all listeners that the current active user has changed
   */
  private static void fireUserChanged() {
    for (UserListener l : listeners) {
      l.userChanged();
    }
  }

  /**
   * Call user list listeners. New user list is also written to preferences.
   */
  public static void fireUserListChanged() {
    writeUserSettings();
    userSelector.refreshUserList();
    selectActiveUser();
  }

  public static User getUser() {
    User ret = currentActiveUser;
    if (ret == null) {
      ret = User.getDefaultActive();
      currentActiveUser = ret;
    }
    return ret;
  }

  /**
   * Get user list from program preferences.
   * 
   * @return An array of the available users
   */
  public static User[] getUserFromPreferences() {
    String users[] = AppPreferences.USER_LIST.get().split(USERS_SEPARATOR);

    User.empty();

    if (users != null) {
      for (int i = 0; i < users.length; ++i) {
        if (users[i] != null) {
          User.newUser(users[i]);
        }
      }
    }

    return User.getAvailableUsers();
  }

  /**
   * Remove a listener from the current active user listener list.
   * 
   * @param l
   *          The listener to remove
   */
  public static void removeUserListener(UserListener l) {
    listeners.remove(l);
  }

  /**
   * Selects the active user in the user list.
   */
  public static void selectActiveUser() {
    for (int i = 0; i < userSelector.getModel().getSize(); ++i) {
      if (userSelector.getModel().getElementAt(i).toString()
          .equals(UserManager.getUser().getName())) {
        userSelector.setSelectedIndex(i);
      }
    }
  }

  /**
   * Define new selected user. Fires UserChanged.
   * 
   * @param selectedUser
   */
  public static void setUser(User selectedUser) {

    /* Do nothing if user didn't change */
    if (!selectedUser.equals(getUser())) {

      /* Change active user */
      currentActiveUser = selectedUser;
      /* Default user is last used */
      User.setDefaultActive(currentActiveUser);

      fireUserChanged();
    }
  }

  /**
   * Write the user list to the program preferences. This should be done each
   * time the user list changes.
   */
  public static void writeUserSettings() {
    User[] users = User.getAvailableUsers();
    String strUsers = "";

    for (int i = 0; i < users.length; ++i) {
      if (i != 0)
        strUsers = strUsers + USERS_SEPARATOR;

      strUsers = strUsers + users[i].getName();
    }

    AppPreferences.USER_LIST.set(strUsers);
  }

  // static members
  public static final String UNKNOWN_USER_NAME = "unknown";

  public static final String AUTOGEN_PREFIX = "autogen:";

  public static final String USERS_SEPARATOR = ",";

  private static User currentActiveUser = null;

  public static UserManager userManager = new UserManager();

  public static UserSelector userSelector;

  private static ArrayList<UserListener> listeners = new ArrayList<UserListener>();

  private UserManager() {

  }

}
