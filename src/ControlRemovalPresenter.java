import java.util.*;
import javax.swing.*;

public class ControlRemovalPresenter {

	MainModel model;
	MainPresenter presenter;
	ControlRemovalPanel panel;

	public ControlRemovalPresenter(MainModel model, MainPresenter presenter, ControlRemovalPanel panel) {
		this.model = model;
		this.presenter = presenter;
		this.panel = panel;
	}

	// Wasn't very consistent when naming presenter methods. Something to fix eventually.
	public void removeVotersButtonPressed() {
		model.deleteAllVoterGroups();
	}
	public void removeCandidatesButtonPressed() {
		model.deleteAllCandidateGroups();
	}
	public void resetModelButtonPressed() {
		model.deleteAllVoterGroups();
		model.deleteAllCandidateGroups();
	}
}