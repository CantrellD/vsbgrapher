import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class MainPresenter {

	MainModel model;

	MainPanel mainPanel;
	MarkerPanel markerPanel;
	ControlPropertiesPanel controlPropertiesPanel;
	ControlRemovalPanel controlRemovalPanel;
	ControlGraphPanel controlGraphPanel;

	/* MainPresenter is responsible for tasks involving multiple panels, since it's
	 * the only class with references to (almost) all of them. At the moment graphing
	 * is the only task which requires multiple panels, namely ControlGraph and Marker.
	 */
	public MainPresenter(MainModel model) {
		this.model = model;
	}

	/* Method is called by ControlGraphPresenter. Independent variable is either a voter group
	 * or a candidate group; the color of the graph at a given point represents some measure
	 * of an election in which the independent variable exists at that point. If boundaries is
	 * true, lines are drawn between candidate groups (voter group as iVariable) or around voter
	 * groups (candidate group as iVariable). Dependent variable wasn't passed as an argument
	 * because it's just an int that this class already has access to.
	 */
	public void graphRequested(I_VotingMethod voteSys, Object iVariable, boolean boundaries) {
		// Make it apparent that work is being done
		mainPanel.showWaitCursor();

		// Tell marker panel to create a blank graph and return a reference to it. A reference is also kept.
		BufferedImage canvas = markerPanel.createVSBGraphCanvas();

		Graphics page = canvas.getGraphics();	// Retrieve the part of the graph that can be drawn on.
		Lens lens = markerPanel.getLens();		// Get an object that can convert points between coordinate systems
		int ySize = canvas.getHeight();			// Determine vertical size of the graph in graphical coordinates.
		int xSize = canvas.getWidth();			// Determine horizontal size of the graph in graphical coordinates.

		// These should be renamed since they're no longer lists.
		I_Pollable[] vList = model.getVoters();
		I_Electable[] cList = model.getCandidates();

		/* Distance (graphical coordinates) between adjacent locations occupied at different times by the indepedent
		 * variable. In other words, a lazy way to set graph resolution. Note that generating a graph with step set
		 * to 4 is 16 times faster than generating a graph with step set to 1.
		 */
		int step = 4;

		if (controlGraphPanel.getDependentVariable() == ControlGraphPanel.WINNER) {
			/* Draw graph based on winning candidate by e.g. showing red when a red candidate wins.
			 */

			// Determine the color of each candidate. Mostly involves identifying the group they're in.
			Color[] cColors = new Color[cList.length];
			Object o = null;
			for (int i=0; i<cList.length; i++) {
				for (I_Group g : model.getCandidateGroups()) {
					if (g.getMembers().contains(cList[i])) {
						o = g;
					}
				}
				cColors[i] = model.getColor(o);
			}

			int winner;							// Index of winner in cList / their color in cColors
			YXPoint pixel = new YXPoint(0,0);	// Destination for independent variable in graphical coordinates
			for (int y=0; y<ySize; y+=step) {
				pixel.y = y;
				for (int x=0; x<xSize; x+=step) {
					pixel.x = x;

					model.moveGroup(iVariable, lens.toInternal(pixel));
					winner = voteSys.selectWinner(vList, cList);

					page.setColor(cColors[winner]);
					page.fillRect(x,y,step,step);
				}
			}
		} else if (controlGraphPanel.getDependentVariable() == ControlGraphPanel.REGRET) {
			/* Draw graph based on avoidable regret, given by (A-B)/A where A is the average opppsition.
			 * to the winning candidate and B is the average opposition to the best possible candidate.
			 * Not convinced this is a good measure, since avoidable regret will always be low when there
			 * aren't any good candidates, but I needed a consistent way to generate positive percentages,
			 * and I'm not convinced that choosing the worst candidate should cause a bright spot on the
			 * graph if their opposition is similar to that of the best candidate..
			 */
			int winner;							// Index of winner in cList / their average opposition in cOpposition.
			double[] cOpposition;				// Average opposition for each candidate. Could probably use total.
			double minOpp;						// Average opposition for the best possible candidate
			double ballotWeight;				// Current voter's ballot weight, only exists to avoid method calls.
			int c;								// R, G, and B value for point on (grayscale) graph
			YXPoint pixel = new YXPoint(0,0); 	// Destination for independent variable in graphical coordinates
			for (int y=0; y<ySize; y+=step) {
				pixel.y = y;
				for (int x=0; x<xSize; x+=step) {
					pixel.x = x;

					model.moveGroup(iVariable, lens.toInternal(pixel));
					winner = voteSys.selectWinner(vList, cList);

					// Calculate average opposition for each candidate.
					cOpposition = new double[cList.length];
					for (I_Pollable v : vList) {
						ballotWeight = v.getBallotWeight();
						for (int i=0; i<cList.length; i++) {
							cOpposition[i] += v.poll(cList[i])*ballotWeight;
						}
					}
					for (int i=0; i<cOpposition.length; i++) {
						cOpposition[i] /= vList.length;
					}

					// Identiy average opposition for best possible candidate
					minOpp = cOpposition[0];
					for (int i=1; i<cOpposition.length; i++) {
						if (cOpposition[i] < minOpp) {
							minOpp = cOpposition[i];
						}
					}

					// Final preventable regret / color calculation.
					c = 0;
					if (cOpposition[winner] > 0) {
						c = (int)(255*(cOpposition[winner]-minOpp)/(cOpposition[winner]));
					}

					page.setColor(new Color(c,c,c));
					page.fillRect(x,y,step,step);
				}
			}
		}

		model.deleteGroup(iVariable);
		if (boundaries && controlGraphPanel.getIndependentVariable() == ControlGraphPanel.NEW_VOTER_GROUP) {
			// Draw lines between each pair of candidate groups
			List<? extends I_Group> cGroups = model.getCandidateGroups();
			YXPoint gLocationA;
			YXPoint gLocationB;
			YXPoint gLocationC;
			double slope;
			int intercept;
			for (int i=0, s=cGroups.size(); i<s; i++) {
				// Location of candidate one
				gLocationA = lens.toGraphical(cGroups.get(i).getLocation());
				for (int j=i+1; j<s; j++) {

					// Location of candidate two
					gLocationB = lens.toGraphical(cGroups.get(j).getLocation());

					// Location of midpoint between the two candidates
					gLocationC = new YXPoint((gLocationA.y + gLocationB.y) / 2, (gLocationA.x + gLocationB.x) / 2);

					if (gLocationB.y-gLocationA.y != 0) {
						// Calculate slope of line orthogonal to the one that would pass through both candidates
						slope = -(gLocationB.x-gLocationA.x)/(double)(gLocationB.y-gLocationA.y);

						// Draw line with calculated slope which passes through midpoint
						intercept = gLocationC.y - (int)(slope*gLocationC.x);
						page.setColor(Color.white);
						page.drawLine(0,intercept,xSize,(int)(slope*xSize)+intercept);
					} else {
						// Draw vertical line passing through midpoint
						intercept = gLocationC.x;
						page.setColor(Color.white);
						page.drawLine(intercept,0,intercept,ySize);
					}
				}
			}
			//
		} else if (boundaries && controlGraphPanel.getIndependentVariable() == ControlGraphPanel.NEW_CANDIDATE_GROUP) {
			// Draw circles centered at voter groups which pass through candidate groups
			List<? extends I_Group> vGroups = model.getVoterGroups();
			List<? extends I_Group> cGroups = model.getCandidateGroups();
			YXPoint gLocationA;
			YXPoint gLocationB;
			int radius;
			for (I_Group v : vGroups) {
				// Location of voter group
				gLocationA = lens.toGraphical(v.getLocation());
				for (I_Group c : cGroups) {
					// Location of candidate group
					gLocationB = lens.toGraphical(c.getLocation());

					// Calculate distance between voter group and candidate group, use as radius and draw circle
					radius = (int)Math.hypot(gLocationB.x-gLocationA.x,gLocationB.y-gLocationA.y);
					page.setColor(Color.white);
					page.drawOval(gLocationA.x-radius,gLocationA.y-radius,2*radius,2*radius);
				}
			}
		}

		// Tell marker panel that graph must be redrawn
		markerPanel.redraw();

		// Make it apparent that graph is finished
		mainPanel.showNormalCursor();
	}

	// Last minute changes.
	public List<String> getNewVoterGroupProperties() {
		List<String> result = controlPropertiesPanel.getProperties(ControlPropertiesPanel.VOTER_CARD);
		result.remove(1);
		result.remove(0);
		return result;
	}

	public List<String> getNewCandidateGroupProperties() {
		List<String> result = controlPropertiesPanel.getProperties(ControlPropertiesPanel.CANDIDATE_CARD);
		result.remove(1);
		result.remove(0);
		return result;
	}

	// Methods used to give MainPresenter references to important panels.
	public void registerMainPanel(MainPanel panel) {
		mainPanel = panel;
	}
	public void registerMarkerPanel(MarkerPanel panel) {
		markerPanel = panel;
	}
	public void registerControlPropertiesPanel(ControlPropertiesPanel panel) {
		controlPropertiesPanel = panel;
	}
	public void registerControlRemovalPanel(ControlRemovalPanel panel) {
		controlRemovalPanel = panel;
	}
	public void registerControlGraphPanel(ControlGraphPanel panel) {
		controlGraphPanel = panel;
	}
}