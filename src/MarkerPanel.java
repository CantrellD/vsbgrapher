import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;


public class MarkerPanel extends JPanel implements I_ModelObserver {

	private MainModel model;
	private MarkerPresenter presenter;

	private Dimension size;						// Size, in graphical coordinates, of the region where groups can exist.
	private Lens lens;							// Allows conversion between coordinate systems.
	private ArrayList<Marker> markers;			// Used to represent group locations.
	private YXPoint[] relativePositions;		// Offsets for each moving marker, relative to cursor.
	private Marker activeMarker;				// The (or a) marker which is being dragged by cursor.
	private BufferedImage populationGraph;		// Shows voter positions. Tries to show ballot weights.
	private BufferedImage vsbGraph;				// Graph requested by user through ControlGraphPanel.

	/* MarkerPanel is a visualization of the region where groups exist. Markers, which show group locations,
	 * can be moved with the mouse, causing group locations to be updated in the model. Markers can also be
	 * used to edit or delete their associated groups through the use of a context menu. Individual voters
	 * and candidates are drawn at the appropriate locations.
	 */
	public MarkerPanel(MainModel mainModel, MainPresenter mainPresenter) {
		model = mainModel;
		presenter = new MarkerPresenter(mainModel, mainPresenter, this);
		model.addObserver(this);
		mainPresenter.registerMarkerPanel(this);

		size = new Dimension(512,512);
		lens = new Lens(model.getSize(), size);

		markers = new ArrayList<Marker>();

		setBackground(Color.black);
		setPreferredSize(size);
		setMaximumSize(size);

		addMouseMotionListener(new MouseMovementListener());
		addMouseListener(new MouseButtonListener());

		addMouseListener(new MenuListener());
	}

	public void redraw() {
		repaint();
	}

	public void paintComponent(Graphics page) {
		super.paintComponent(page);
     	if (populationGraph != null) {
     		page.drawImage(populationGraph, 0, 0, null);
     	}
     	if (vsbGraph != null) {
     		page.drawImage(vsbGraph, 0, 0, null);
     	}

		/* Draw individual candidates. Would be done with populationGraph if draw order weren't important.
		 * Might be worth doing on a separate BufferedImage if transparency is supported.
		 */
		YXPoint gLocation;
     	for (I_Group<I_Electable> g : model.getCandidateGroups()) {
			for (I_Electable p : g.getMembers()) {
				gLocation = lens.toGraphical(p.getLocation());
				page.setColor(Color.gray);
				page.fillRect(gLocation.x-2, gLocation.y-2, 5, 5);
				page.setColor(model.getColor(g));
				page.fillRect(gLocation.x-1, gLocation.y-1, 3, 3);
			}
		}

		// Draw the markers.
		for (Marker marker : markers) {
			marker.draw(page);
		}
	}

	/* Called when the state of main model changes, unless notifications are being suppressed.
	 * Deletes any existing VSBGraph, recreates all of the markers, redraws populationGraph,
	 * and then repaints the panel.
	 */
	public void notify(ModelEvent event) {
		vsbGraph = null;

		// Recreate markers //
		// Make note of which groups were associated with selected / active markers
		ArrayList<Object> selectedData = new ArrayList<>();
		Object activeDatum = null;
		for (Marker m : markers) {
			if (m.isSelected()) {
				selectedData.add(m.getDatum());
			}
			if (m == activeMarker) {
				activeDatum = m.getDatum();
			}
		}

		// Delete markers
		markers = new ArrayList<Marker>();

		// Create new markers
		for (I_Group g : model.getVoterGroups()) {
			markers.add(new Marker(lens.toGraphical(g.getLocation()), model.getColor(g), "VoterGroup", g));
		}
		for (I_Group g : model.getCandidateGroups()) {
			markers.add(new Marker(lens.toGraphical(g.getLocation()), model.getColor(g), "CandidateGroup", g));
		}

		/* Select / make active any markers associated with groups that were themselves associated with
		 * selected / active markers prior to the notification. It would probably make more sense to update
		 * marker locations and colors directly instead of recreating them, so as to avoid this process.
		 */
		for (Marker m : markers) {
			if (selectedData.contains(m.getDatum())) {
				m.setSelected(true);
			}
			if (activeDatum == m.getDatum()) {
				activeMarker = m;
			}
		}
		//

		// Redraw population graph //
		/* This doesn't work very well, because voters can be drawn over one another, and because
		 * a single voter with ballot weight 10 makes it impossible to see the members of a sufficiently
		 * precise group with total ballot weight 1000. Still not sure how best to fix it.
		 */
		populationGraph = new BufferedImage(size.width, size.height,BufferedImage.TYPE_INT_RGB);
		Graphics page = populationGraph.getGraphics();

		// Identify the highest ballot weight in the model
		double max = 0;
		for (I_Group<I_Pollable> group : model.getVoterGroups()) {
			for (I_Pollable p : group.getMembers()) {
				max = (p.getBallotWeight() > max) ? p.getBallotWeight() : max;
			}
		}

		// Draw voters
		YXPoint gLocation;
		int c;
		for (I_Group<I_Pollable> group : model.getVoterGroups()) {
			for (I_Pollable p : group.getMembers()) {
				c = (int)((p.getBallotWeight()/max)*255.0);
				gLocation = lens.toGraphical(p.getLocation());
				page.setColor(new Color(c,c,c));
				page.drawRect(gLocation.x, gLocation.y, 1, 1);
			}
		}
		//

		// Redraw
		repaint();
	}

	/* Creates a blank image for another class to draw on. The image will be shown when
	 * paintCompnent is next called, at which time it should no longer be blank. When a
	 * notification is received from the main model, the image will be deleted.
	 */
	public BufferedImage createVSBGraphCanvas() {
		vsbGraph = new BufferedImage(size.width, size.height,BufferedImage.TYPE_INT_RGB);
		return vsbGraph;
	}

	public Lens getLens() {
		return lens;
	}

	private void moveMarker(int index, YXPoint newLocation) {
		if (newLocation.x < 0) {
			newLocation.x = 0;
		}
		if (newLocation.y < 0) {
			newLocation.y = 0;
		}
		if (newLocation.x > size.width) {
			newLocation.x = size.width;
		}
		if (newLocation.y > size.height) {
			newLocation.y = size.height;
		}
		markers.get(index).setLocation(newLocation.y, newLocation.x);
		repaint();
	}

	// Returns the first marker found at the given point, or null. Starts by checking 'top' i.e. recently drawn markers.
	private Marker checkForMarker(YXPoint location) {
		int index = -1;
		for (int i=markers.size()-1; i>=0; i--) {
			if (markers.get(i).checkBounds(location)) {
				return markers.get(i);
			}
		}
		return null;
	}

	// Checks if the ctrl key was held during the given mouse event.
	private boolean checkCtrl(MouseEvent e) {
		boolean result;

		int ctrlMask = InputEvent.CTRL_DOWN_MASK;
		if ((e.getModifiersEx() & ctrlMask) == ctrlMask) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}


	private class MouseMovementListener implements MouseMotionListener {

		public void mouseDragged(MouseEvent event) {
			YXPoint cursor = new YXPoint(event.getPoint());

			/* If any marker is moving, update the position of all selected markers (if user initially clicked a
			 * selected marker) or just the marker the user initially clicked (otherwise).
			 */
			if (activeMarker != null) {
				YXPoint newLocation;
				for (int i=0; i<markers.size(); i++) {
					if (markers.get(i)==activeMarker || (activeMarker.isSelected() && markers.get(i).isSelected())) {
						newLocation = new YXPoint(cursor.y + relativePositions[i].y, cursor.x + relativePositions[i].x);
						moveMarker(i, newLocation);
					}
				}
			}
		}
		public void mouseMoved(MouseEvent event) {
		}
	}
	private class MouseButtonListener implements MouseListener {
		public void mousePressed(MouseEvent event) {
			YXPoint cursor = new YXPoint(event.getPoint());

			// Identify the first marker beneath the cursor, if any
			Marker cMarker = checkForMarker(cursor);

			// If left mouse button is pressed:
			if (event.getButton() == event.BUTTON1) {
				// If any marker is beneath the cursor
				if (cMarker != null) {
					// Start moving the detected marker (and possibly others)
					activeMarker = cMarker;

					// Make note of where every marker is, relative to cursor.
					relativePositions = new YXPoint[markers.size()];
					for (int i=0; i<markers.size(); i++) {
						relativePositions[i] = new YXPoint(	markers.get(i).getLocation().y - cursor.y,
															markers.get(i).getLocation().x - cursor.x);
					}
				}
			}
		}
		public void mouseReleased(MouseEvent event) {
			// If left mouse button is released:
			if (event.getButton() == event.BUTTON1) {

				// If any marker is moving:
				if (activeMarker != null) {

					// Tell this panel's presenter that the moving markers have finished moving.
					if (activeMarker.isSelected()) {
						// Note that the name 'markers' points to a different list for each iteration of the loop.
						List<Marker> mList = markers;
						for (Marker m : mList) {
							if (m.isSelected()) {
								presenter.markerDropped(m);
							}
						}
					} else {
						presenter.markerDropped(activeMarker);
					}

					// Stop moving all markers.
					activeMarker = null;
				}
				repaint(); // Not sure this is necessary.
			}
		}

		public void mouseEntered(MouseEvent event) {}
		public void mouseExited(MouseEvent event) {}
		public void mouseClicked(MouseEvent event) {
			YXPoint cursor = new YXPoint(event.getPoint());

			// Identify the first marker beneath the cursor, if any
			Marker cMarker = checkForMarker(cursor);

			// If the left mouse button was clicked:
			if (event.getButton() == event.BUTTON1) {

				// If any marker is beneath cursor:
				if (cMarker != null) {

					// If ctrl was held when the mouse was clicked:
					if (checkCtrl(event)) {

						// Select the marker beneath the cursor if not already selected, deselect otherwise.
						cMarker.setSelected(!cMarker.isSelected());
					} else {

						// Select the marker beneath the cursor and deselect all other markers.
						for (Marker m : markers) {
							if (m == cMarker) {
								m.setSelected(true);
							} else {
								m.setSelected(false);
							}
						}
					}

				} else {
					// Deselect all markers.
					for (Marker m : markers) {
						m.setSelected(false);
					}
				}
				repaint();
			}

			// If the middle mouse button was clicked:
			if (event.getButton() == event.BUTTON2) {
				repaint();
			}
		}
	}

	private class MenuListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			YXPoint cursor = new YXPoint(e.getPoint());
			Marker cMarker = checkForMarker(cursor);
			// If user requested context menu: (right click in Windows)
			if (e.isPopupTrigger()) {
				// If a marker is beneath the cursor:
				if (cMarker != null) {
					showMenu(e, cMarker);
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			YXPoint cursor = new YXPoint(e.getPoint());
			Marker cMarker = checkForMarker(cursor);

			// If user requested context menu: (right click in Windows)
			if (e.isPopupTrigger()) {
				// If a marker is beneath the cursor:
				if (cMarker != null) {
					showMenu(e, cMarker);
				}
			}
		}

		// Creates a context menu that knows which marker was beneath the cursor when the menu was requested.
		private void showMenu(MouseEvent e, Marker m) {
			ContextMenu menu = new ContextMenu(m);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	private class ContextMenu extends JPopupMenu {

		// Allows specific groups to be deleted or have their properties changed.
		public ContextMenu(Marker m) {
			JMenuItem editGroupMenuItem = new JMenuItem("Edit group");
			JMenuItem deleteGroupMenuItem = new JMenuItem("Delete group");
			add(editGroupMenuItem);
			add(deleteGroupMenuItem);
			editGroupMenuItem.addActionListener(new MenuActionListener(m, "Edit"));
			deleteGroupMenuItem.addActionListener(new MenuActionListener(m, "Delete"));
		}
	}

	private class MenuActionListener implements ActionListener {

		Marker m;		// The marker associated with the context menu.
		String desc;	// Describes the menu item this listener is associated with.

		// Might be able to make this a private class within ContextMenu so that event.getSource() can be used.
		public MenuActionListener(Marker m, String desc) {
			this.m = m;
			this.desc = desc;
		}

		// Called automatically when the menu item associated with this listener is clicked.
		public void actionPerformed(ActionEvent actionEvent) {
			if (desc.equals("Edit")) {
				presenter.modificationRequested(m);
			} else if (desc.equals("Delete")) {
				presenter.deletionRequested(m);
			}
    	}
	}
}