/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim;

import com.cburch.logisim.generated.BuildInfo;
import com.cburch.logisim.gui.generic.OptionPane;
import com.cburch.logisim.gui.start.Startup;
import com.cburch.logisim.prefs.AppPreferences;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

public class Main {
  /**
   * Application entry point.
   *
   * @param args Optional arguments.
   */
  public static void main(String[] args) {
    System.setProperty("apple.awt.application.name", BuildInfo.name);
    try {
      if (!GraphicsEnvironment.isHeadless()) {
        FlatLightLaf.installLafInfo();
        FlatDarkLaf.installLafInfo();
        FlatDarculaLaf.installLafInfo();
        FlatIntelliJLaf.installLafInfo();

        UIManager.setLookAndFeel(AppPreferences.LookAndFeel.get());
        UIManager.put(
            "ToolTip.font",
            new FontUIResource("SansSerif", Font.BOLD, AppPreferences.getScaled(12)));
      }
    } catch (ClassNotFoundException
        | UnsupportedLookAndFeelException
        | IllegalAccessException
        | InstantiationException e) {
      e.printStackTrace();
    }

    final var startup = Startup.parseArgs(args);
    if (startup == null) System.exit(10);
    if (startup.shallQuit()) System.exit(0);

    if (!startup.autoUpdate()) {
      try {
        startup.run();
      } catch (Throwable e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        if (!startup.isTty)
            JOptionPane.showMessageDialog(null, result.toString());
        System.exit(-1);
      }
    }
  }

  public static boolean headless = false;

  // FloppyDisk unicode character: https://charbase.com/1f4be-unicode-floppy-disk
  public static final String DIRTY_MARKER = "\ud83d\udcbe";

  public static boolean hasGui() {
    return !headless;
  }

  /* Added by REDS */
  public static final int FINAL_REVISION = Integer.MAX_VALUE / 4;
  public static final LogisimVersion VERSION = new LogisimVersion(3, 9, 0, "dev");
  public static final String VERSION_NAME = VERSION.toString();
  public static final int COPYRIGHT_YEAR = 2014;

  public static boolean ANALYZE = true;
  /**
   * This flag enables auto-updates. It is true by default, so that users
   * normally check for updates at startup. On the other hand, this might  be
   * annoying for developers, therefore we let them disable it from the
   * command line with the '-noupdates' option.
   */
  public static boolean UPDATE = true;

  /**
   * URL for the automatic updater
   */
  public static final String UPDATE_URL = "http://reds-data.heig-vd.ch/logisim-evolution/logisim_evolution_version.xml";
}
