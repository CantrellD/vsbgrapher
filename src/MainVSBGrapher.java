/*	Copyright (C) 2014  Douglas Cantrell
 *
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MainVSBGrapher {						// Application only
//public class MainVSBGrapher extends JApplet {		// Applet only

	public static final boolean AN_APPLET = false;	// Application only
	//public static final boolean AN_APPLET = true;	// Applet only

	// Only needed for applications, but it won't be called even if it's included in an applet.
	public static void add(Object o) {System.out.println("Conversion to applet failed.");}

	public static void main(String[] args) {		// Application only
	//@Override										// Applet only
	//public void init() {							// Applet only

		// MainModel is responsible for managing voter and candidate objects
		MainModel model = new MainModel();

		// MainPresenter is responsbile for tasks which involve multiple panels (just graphing for now)
		MainPresenter presenter = new MainPresenter(model);

		// MainPanel is responsible for creating the graphical user interface
		MainPanel panel = new MainPanel(model, presenter);

		// Boilerplate plus an intro message
		if (!AN_APPLET) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					JFrame frame = new JFrame();
					frame.add(panel);
					frame.pack();
					frame.setVisible(true);
					showIntroMessage(panel);
				}
			};
			SwingUtilities.invokeLater(r);
		} else {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					add(panel);
					showIntroMessage(panel);
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void showIntroMessage(JPanel panel) {
		JOptionPane.showMessageDialog(panel,	// Should probably use main panel for the other dialog boxes as well, somehow.

			"Voting System Behavior Grapher v0.4.09.01\n"																	+
			"Contact: cantrell.douglas@gmail.com\n"																			+
			"\n"																											+
			"Notes:\n"																										+
			"  -  Voters and candidates are modeled as points on a plane. Voters prefer candidates who are closer to\n"		+
			"     them on that plane.\n"																					+
			"  -  The best possible candidate is assumed to be the one with the lowest average distance to voters.\n"		+
			"  -  Possible voter locations are represented visually as gray pixels. Lighter gray is used for locations\n"	+
			"     with relatively many voters. This causes voters to be less visible when spread across many locations.\n"	+
			"  -  Preventable regret is calculated as (W-O)/W, where W is average distance to the winning candidate,\n"		+
			"     and O is average distance to the best possible candidate. Lighter gray indicates higher regret.\n"		+
			"  -  Default values for independent variables are pulled from the 'New Group Properties' text fields.\n"		+
			"  -  Groups can be moved by clicking and dragging them. They can be edited or deleted via context menu.\n"		+
			"  -  Voters approve of closer-than-average candidates during Approval Voting elections.\n"						+
			"  -  Voters sincerely report normalized cardinal preferences during Score Voting elections.\n"					+
			"  -  Voters give one point to the most distant candidate during Borda Count elections, two to the next\n"		+
			"     most distant, and so on.\n"																				+
			"  -  Graphs based on the Condorcet criterion will report random winners when no Condorcet winner exists.\n"	+
			"  -  Voters are not modeled in a realistic way, so election outcomes are not likely to be realistic.\n"		+
			"  -  This is an alpha release, so generated graphs may contain inaccuracies. Bug reports are appreciated.\n",

			"About", JOptionPane.PLAIN_MESSAGE);
	}
}
