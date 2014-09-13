import javax.swing.*;
import java.awt.Color;
import java.util.List;

public class MarkerPresenter {

	MainModel model;
	MainPresenter presenter;
	MarkerPanel panel;

	// Responsible for changing the model based on user interaction with MarkerPanel.
	public MarkerPresenter(MainModel model, MainPresenter presenter, MarkerPanel panel) {
		this.model = model;
		this.presenter = presenter;
		this.panel = panel;
	}

	// Called when the user finishes moving a marker to a new location through click+drag.
	public void markerDropped(Marker marker) {
		model.moveGroup(marker.getDatum(), panel.getLens().toInternal(marker.getLocation()));
	}

	// Called when the user selects delete from a marker's context menu.
	public void deletionRequested(Marker marker) {
		model.deleteGroup(marker.getDatum());
	}

	// Called when the user selects modify from a marker's context menu.
	public void modificationRequested(Marker marker) {
		List<String> currentValues;
		List<String> userInput;
		List<Integer> newValues;
		if (marker.getDescription().equals("VoterGroup")) {
			currentValues = ((I_Group)marker.getDatum()).getProperties();
			TextFieldPanel vPanel = new TextFieldPanel();
			vPanel.build(MainModel.VOTER_PROPERTIES);
			vPanel.setAllText(currentValues);
     		int result = JOptionPane.showConfirmDialog(null, vPanel, "Properties", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				userInput = vPanel.getAllText();
				newValues = model.validateUserDefinedVoterProperties(userInput);
				if (newValues != null) {
					model.updateVoterGroup(	marker.getDatum(), new YXPoint(newValues.get(1), newValues.get(0)), newValues.get(2),
											newValues.get(3), newValues.get(4));
				}
			}
		} else if (marker.getDescription().equals("CandidateGroup")) {
			currentValues = ((I_Group)marker.getDatum()).getProperties();
			TextFieldPanel cPanel = new TextFieldPanel();
			cPanel.build(MainModel.CANDIDATE_PROPERTIES);
			cPanel.setAllText(currentValues);
     		int result = JOptionPane.showConfirmDialog(null, cPanel, "Properties", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				userInput = cPanel.getAllText();
				newValues = model.validateUserDefinedCandidateProperties(userInput);
				if (newValues != null) {
					model.updateCandidateGroup(	marker.getDatum(), new YXPoint(	newValues.get(1), newValues.get(0)), newValues.get(2),
												newValues.get(3));
				}
			}
		}
	}
}