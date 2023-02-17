/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.util;

/**
 * Listener interface for all classes who wants to be advised on current active
 * user changes.
 * 
 * @author christian.mueller@heig-vd.ch
 * @since 2.11.0.t
 */
public interface UserListener {
  public void userChanged();
}
