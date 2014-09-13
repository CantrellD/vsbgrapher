import javax.swing.*;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class ControlGraphPresenter {

	MainModel model;
	MainPresenter presenter;
	ControlGraphPanel panel;

	public ControlGraphPresenter(MainModel model, MainPresenter presenter, ControlGraphPanel panel) {
		this.model = model;
		this.presenter = presenter;
		this.panel = panel;
	}

	/* Method is called when one of the graph buttons is pressed. Boundaries is false
	 * if basic button was pressed, true if alternate button was pressed. Starting to think
	 * I should stop using dialog boxes for everything, or at least find a way to reuse code.
	 */
	public void graphRequested(boolean boundaries) {
		// Create voting system object //
		I_VotingMethod voteSys;
		switch (panel.getMethod()) {
			case ControlGraphPanel.PLURALITY:	voteSys = new M_Plurality();
												break;
			case ControlGraphPanel.APPROVAL:	voteSys = new M_Approval();
												break;
			case ControlGraphPanel.BORDA:		voteSys = new M_Borda();
												break;
			case ControlGraphPanel.MAGIC_BEST:	voteSys = new M_MagicBest();
												break;
			case ControlGraphPanel.IRV:			voteSys = new M_IRV();
												break;
			case ControlGraphPanel.SCORE:		voteSys = new M_Score();
												break;
			case ControlGraphPanel.CONDORCET:	voteSys = new M_Condorcet();
												break;
			case ControlGraphPanel.MAGIC_WORST:	voteSys = new M_MagicWorst();
												break;
			default:							voteSys = new M_DefaultMethod();
												break;
		}
		//

		// Disable notifications //
		model.mute();
		//

		// Try to create independent variable //
		Object iVariable = null;
		if (panel.getIndependentVariable() == ControlGraphPanel.NEW_VOTER_GROUP) {
			if (model.getCandidates().length < 1) {
				JOptionPane.showMessageDialog(	new JPanel(), "Invalid request: Please add candidates.",
												"Error", JOptionPane.ERROR_MESSAGE);
			} else {
				// Create abridged new voter group dialog
				TextFieldPanel vPanel = new TextFieldPanel();
				vPanel.build(MainModel.VOTER_PROPERTIES[2],MainModel.VOTER_PROPERTIES[3], MainModel.VOTER_PROPERTIES[4]);
				//ArrayList<String> defaults = new ArrayList<>(3);
				//defaults.add(MainModel.VOTER_DEFAULTS[2]);
				//defaults.add(MainModel.VOTER_DEFAULTS[3]);
				//defaults.add(MainModel.VOTER_DEFAULTS[4]);
				List<String> defaults = presenter.getNewVoterGroupProperties();	// Last minute change to allow user defined defaults
				vPanel.setAllText(defaults);

				// Check if user clicks 'ok' or 'cancel'
				int result = JOptionPane.showConfirmDialog(null, vPanel, "Voter Group", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					// Get validated user input
					ArrayList<String> userInput = new ArrayList<>(5);
					userInput.add(MainModel.VOTER_DEFAULTS[0]);
					userInput.add(MainModel.VOTER_DEFAULTS[1]);
					userInput.addAll(vPanel.getAllText());
					List<Integer> properties = model.validateUserDefinedVoterProperties(userInput);

					// Finish creating independent variable (or fail)
					if (properties != null) {
						iVariable = model.createVoterGroup(	new YXPoint(properties.get(1), properties.get(0)), properties.get(2),
															properties.get(3), properties.get(4));
					}
				}
			}
		} else if (panel.getIndependentVariable() == ControlGraphPanel.NEW_CANDIDATE_GROUP) {
			if (model.getVoters().length < 1) {
				JOptionPane.showMessageDialog(	new JPanel(), "Invalid request: Please add voters.",
												"Error", JOptionPane.ERROR_MESSAGE);
			} else {
				// Create abridged new candidate group dialog
				TextFieldPanel cPanel = new TextFieldPanel();
				cPanel.build(MainModel.CANDIDATE_PROPERTIES[2],MainModel.CANDIDATE_PROPERTIES[3]);
				//ArrayList<String> defaults = new ArrayList<>(2);
				//defaults.add(MainModel.CANDIDATE_DEFAULTS[2]);
				//defaults.add(MainModel.CANDIDATE_DEFAULTS[3]);
				List<String> defaults = presenter.getNewCandidateGroupProperties();	// Last minute change to allow user defined defaults
				cPanel.setAllText(defaults);

				// Check if user clicks 'ok' or 'cancel'
				int result = JOptionPane.showConfirmDialog(null, cPanel, "Candidate Group", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					// Get validated user input
					ArrayList<String> userInput = new ArrayList<>(4);
					userInput.add(MainModel.CANDIDATE_DEFAULTS[0]);
					userInput.add(MainModel.CANDIDATE_DEFAULTS[1]);
					userInput.addAll(cPanel.getAllText());
					List<Integer> properties = model.validateUserDefinedCandidateProperties(userInput);

					// Finish creating independent variable (or fail)
					if (properties != null) {
						iVariable = model.createCandidateGroup(	new YXPoint(properties.get(1), properties.get(0)), properties.get(2),
																properties.get(3));
					}
				}
			}
		}
		//

		// Create graph and delete independent variable (or do nothing) //
		if (iVariable != null) {
			presenter.graphRequested(voteSys, iVariable, boundaries);
		}
		//

		// Reenable notifications //
		model.unmute();
		//
	}
}