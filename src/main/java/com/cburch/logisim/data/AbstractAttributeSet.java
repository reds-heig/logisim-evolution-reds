/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.data;

import java.util.ArrayList;


import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.tools.Integrity;
import com.cburch.logisim.util.LocaleManager;

import java.util.Date;

public abstract class AbstractAttributeSet implements Cloneable, AttributeSet {
  private ArrayList<AttributeListener> listeners = null;

    private boolean integrityIsValid = false;
    private boolean toInit = true;

  @Override
  public void addAttributeListener(AttributeListener l) {
    if (listeners == null) listeners = new ArrayList<>();
    listeners.add(l);
  }

  public boolean amIListening(AttributeListener l) {
    return listeners.contains(l);
  }

  @Override
  public Object clone() {
    try {
      AbstractAttributeSet ret = (AbstractAttributeSet) super.clone();
      ret.listeners = new ArrayList<>();
      this.copyInto(ret);
      return ret;
    } catch (CloneNotSupportedException ex) {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public boolean containsAttribute(Attribute<?> attr) {
    return getAttributes().contains(attr);
  }

  protected abstract void copyInto(AbstractAttributeSet dest);

  protected void fireAttributeListChanged() {
    if (listeners != null) {
      final var event = new AttributeEvent(this);
      for (final var l : new ArrayList<>(listeners)) {
        l.attributeListChanged(event);
      }
    }
  }

  protected <V> void fireAttributeValueChanged(Attribute<? super V> attr, V value, V oldvalue) {
    if (listeners != null) {
      final var event = new AttributeEvent(this, attr, value, oldvalue);
      final var ls = new ArrayList<>(listeners);
      for (final var l : ls) {
        l.attributeValueChanged(event);
      }
    }
  }

  @Override
  public Attribute<?> getAttribute(String name) {
    for (Attribute<?> attr : getAttributes()) {
      if (attr.getName().equals(name)) {
        return attr;
      }
    }
    return null;
  }

  @Override
  public boolean isReadOnly(Attribute<?> attr) {
    return false;
  }

  @Override
  public boolean isToSave(Attribute<?> attr) {
    return attr.isToSave();
  }

  @Override
  public void removeAttributeListener(AttributeListener l) {
    listeners.remove(l);
    if (listeners.isEmpty()) listeners = null;
  }

  @Override
  public void setReadOnly(Attribute<?> attr, boolean value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setToInit(boolean value) {
        toInit = value;
    }

  @Override
  public boolean isToInit() {
        return toInit;
    }

  public boolean hasValidIntegrity() throws NoIntegrityAttributeException {

        if (containsAttribute(StdAttr.INTEGRITY)) {
            String integrityCheck = this.getValue(StdAttr.INTEGRITY);

            String date;
            if (this.getValue(StdAttr.DATE) != null)
                date = LocaleManager.PARSER_SDF.format(this
                        .getValue(StdAttr.DATE));
            else
                date = "";

            String integrityHash = Integrity.getHashOf(this
                    .getValue(StdAttr.OWNER)
                    + date
                    + this.getValue(StdAttr.VERSION)
                    + this.getValue(StdAttr.UUID));

            integrityIsValid = integrityCheck.equals(integrityHash);
            return integrityIsValid;

        } else {
            throw new NoIntegrityAttributeException(
                    "Element has no integrity attribute");
        }
    }
}
