/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.comp;


import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Tracker;
import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.NoIntegrityAttributeException;
import com.cburch.logisim.gui.main.TrackerTreeCircuitNode;
import com.cburch.logisim.gui.main.TrackerTreeCompNode;
import com.cburch.logisim.gui.main.TrackerTreeModel;
import com.cburch.logisim.instance.StdAttr;
import java.awt.Graphics;

public abstract class AbstractComponent implements Component {
  private TrackerTreeCompNode compNode = null;
  
  protected AbstractComponent() {}

  @Override
  public boolean contains(Location pt) {
    final var bds = getBounds();
    if (bds == null) return false;
    return bds.contains(pt, 1);
  }

  @Override
  public boolean contains(Location pt, Graphics g) {
    final var bds = getBounds(g);
    if (bds == null) return false;
    return bds.contains(pt, 1);
  }

  @Override
  public boolean endsAt(Location pt) {
    for (final var data : getEnds()) {
      if (data.getLocation().equals(pt)) return true;
    }
    return false;
  }

  @Override
  public Bounds getBounds(Graphics g) {
    return getBounds();
  }

  @Override
  public EndData getEnd(int index) {
    return getEnds().get(index);
  }


  public abstract void propagate(CircuitState state);

  @Override
  public boolean hasValidIntegrity() {
    /* Check integrity */
    if (getAttributeSet().containsAttribute(StdAttr.INTEGRITY)) {
      try {
        return ((AbstractAttributeSet) getAttributeSet())
            .hasValidIntegrity();
      } catch (NoIntegrityAttributeException e) {
        return true;
      }
    }
    return true;
  }

  @Override
  public boolean hasValidOwner(Tracker tracker) {
    if (!getAttributeSet().containsAttribute(StdAttr.OWNER))
      return true;

    return tracker.getSelectedAuthors().contains(
        getAttributeSet().getValue(StdAttr.OWNER));
  }

    @Override
  public TrackerTreeCircuitNode getTrackerExplorerCircuitNode(
      TrackerTreeModel model, Circuit circuite, CircuitState state) {
    return null;
  }

  @Override
  public TrackerTreeCompNode getTrackerExplorerCompNode(
      TrackerTreeCircuitNode parent) {

    if (compNode == null)
      compNode = new TrackerTreeCompNode(this, parent);

    return compNode;
  }
}
