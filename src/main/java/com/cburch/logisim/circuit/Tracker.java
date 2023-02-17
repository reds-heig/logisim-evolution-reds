/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.circuit;

import java.util.Arrays;
import java.util.LinkedList;

public class Tracker {

	private LinkedList<String> selectedAuthors;
	private CircuitState circuitState;

	public Tracker() {
		selectedAuthors = new LinkedList<>();
	}

	public CircuitState getCircuitState() {
		return circuitState;
	}

	public LinkedList<String> getSelectedAuthors() {
		return selectedAuthors;
	}

	public void setCircuitState(CircuitState circuitState) {
		this.circuitState = circuitState;
	}

	public void setSelectedAuthors(String[] usernames) {
		selectedAuthors = new LinkedList<String>(Arrays.asList(usernames));
	}

}
