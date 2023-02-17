package com.cburch.logisim.util;

import java.util.ArrayList;
import java.util.Iterator;

import com.cburch.logisim.prefs.AppPreferences;

/**
 * User class represent the end user using Logisim. This was introduced with the
 * tracker, as it needs a user name to be stored with the components attributes.
 * A user is represented only by its name. Users are stored in the program
 * preferences, in the user's personal folder in his OS.
 * 
 * @author christian.mueller@heig-vd.ch
 * @since 2.11.0.t
 */
public class User {

	/**
	 * Remove all users from the user list.
	 */
	public static void empty() {
		users.clear();
		users.add(UNKNOWN_USER);
	}

	/**
	 * Find a user is the existing user list by his name.
	 * 
	 * @param name
	 * @return The user if found, else <code>null</code>
	 */
	public static User findByName(String name) {
		name = name.toLowerCase();
		Iterator<User> it = users.iterator();
		while (it.hasNext()) {
			User user = it.next();
			if (user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Get an array of the existing users
	 * 
	 * @return Array of existing users
	 */
	public static User[] getAvailableUsers() {
		return (User[]) users.toArray(new User[users.size()]);
	}

	/**
	 * Get the default active user from the preferences.
	 * 
	 * @return The default user
	 */
	public static User getDefaultActive() {
		return findByName(AppPreferences.USER_DEFAULT.get());
	}

	/**
	 * User factory. User is automatically added to the user list. It is not
	 * possible to add the default <i>unknown</i> user.
	 * 
	 * @param name
	 *            User name
	 * @return The newly created user
	 */
	public static User newUser(String name) {
		if (name.equals(UserManager.UNKNOWN_USER_NAME))
			return UNKNOWN_USER;
		User usr = new User(name);
		users.add(usr);
		return usr;
	}

	/**
	 * Set the default active user in the preferences.
	 * 
	 * @param newDefautActivelUser
	 */
	public static void setDefaultActive(User newDefautActivelUser) {
		AppPreferences.USER_DEFAULT.set(newDefautActivelUser.getName());
	}

	private String name;

	private static final ArrayList<User> users = new ArrayList<User>();

	public static final User UNKNOWN_USER = new User(
			UserManager.UNKNOWN_USER_NAME);

	/**
	 * User constructor.
	 * 
	 * @param name
	 *            : user name, will be put to lower case.
	 */
	private User(String name) {
		this.name = name.toLowerCase();
	}

	/**
	 * Get the user name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Removes the user from the existing user list.
	 */
	public void remove() {
		Iterator<User> it = users.iterator();
		while (it.hasNext()) {
			User user = it.next();
			if (user.equals(this)) {
				it.remove();
			}
		}
	}
}
