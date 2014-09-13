import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MainPanel extends JPanel {

	ControlPanel controlPanel; // GUI for graph parameters, new groups, collective group removal.
	MarkerPanel markerPanel; // Graphical representation of model state. Some interactive features. Shows graphs.

	// MainPanel is responsbile for creating the GUI.
	public MainPanel(MainModel mainModel, MainPresenter mainPresenter) {
		setPreferredSize(new Dimension(1000,600));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBackground(Color.lightGray); // Pretty sure I overuse this method; something to fix eventually.

		controlPanel = new ControlPanel(mainModel, mainPresenter);
		markerPanel = new MarkerPanel(mainModel, mainPresenter);

		// Set controlPanel height equal to markerPanel height, then give mainPresenter a reference to mainPanel.
		controlPanel.setMaximumSize(new Dimension(controlPanel.getPreferredSize().width, markerPanel.getMaximumSize().height));
		mainPresenter.registerMainPanel(this);

		add(Box.createHorizontalGlue());
		add(Box.createRigidArea(new Dimension(2, 0)));
		add(controlPanel);
		add(Box.createRigidArea(new Dimension(4, 0)));
		add(markerPanel);
		add(Box.createRigidArea(new Dimension(2, 0)));
		add(Box.createHorizontalGlue());
	}

	// Changes cursor to indicate that work is being done. Used during graph generation.
	public void showWaitCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void showNormalCursor() {
		setCursor(null);
	}
}