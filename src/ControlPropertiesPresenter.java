import java.util.*;
import javax.swing.*;

public class ControlPropertiesPresenter {

	MainModel model;
	MainPresenter presenter;
	ControlPropertiesPanel panel;

	public ControlPropertiesPresenter(MainModel model, MainPresenter presenter, ControlPropertiesPanel panel) {
		this.model = model;
		this.presenter = presenter;
		this.panel = panel;
	}

	// Method is called when either of the 'add group' buttons are pressed. Tries to create group with specified properties.
	public void cardButtonPressed(String card) {
		List<String> userInput;
		List<Integer> args;
		if (card.equals(ControlPropertiesPanel.VOTER_CARD)) {
			userInput = panel.getProperties(ControlPropertiesPanel.VOTER_CARD);
			args = model.validateUserDefinedVoterProperties(userInput);
			if (args != null) {
				model.createVoterGroup(new YXPoint(args.get(1), args.get(0)), args.get(2), args.get(3), args.get(4));
			}
		} else if (card.equals(ControlPropertiesPanel.CANDIDATE_CARD)) {
			userInput = panel.getProperties(ControlPropertiesPanel.CANDIDATE_CARD);
			args = model.validateUserDefinedCandidateProperties(userInput);
			if (args != null) {
				model.createCandidateGroup(new YXPoint(args.get(1), args.get(0)), args.get(2), args.get(3));
			}
		}
	}
}