import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {

	ControlGraphPanel gPanel;			// Used to define graph parameters, request graph
	ControlPropertiesPanel pPanel;		// Used to add groups with specific properties
	ControlRemovalPanel rPanel;			// Used to remove many groups from the model simultaneously

	// This is just used to arrange things vertically. It should probably be a private class in MainPanel.
	public ControlPanel(MainModel mainModel, MainPresenter mainPresenter) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.lightGray);

		gPanel = new ControlGraphPanel(mainModel, mainPresenter);
		pPanel = new ControlPropertiesPanel(mainModel, mainPresenter);
		rPanel = new ControlRemovalPanel(mainModel, mainPresenter);

		add(gPanel);
		add(Box.createRigidArea(new Dimension(0, 2)));
		add(Box.createVerticalGlue());
		add(Box.createRigidArea(new Dimension(0, 2)));
		add(pPanel);
		add(rPanel);
	}
}