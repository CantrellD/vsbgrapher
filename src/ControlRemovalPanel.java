public class ControlRemovalPanel extends ButtonGridPanel {

	String[] buttonNames = {"Remove All Voters", "Remove All Candidates", "Reset Model"};

	MainModel model;
	ControlRemovalPresenter presenter;

	// Note that the 'Add Group' buttons are part of ControlPropertiesPanel.
	public ControlRemovalPanel(MainModel mainModel, MainPresenter mainPresenter) {
		build(1, buttonNames);
		model = mainModel;
		presenter = new ControlRemovalPresenter(mainModel, mainPresenter, this);
		mainPresenter.registerControlRemovalPanel(this);
	}

	protected void buttonPressed(int i) {
		if (i==0) {
			presenter.removeVotersButtonPressed();
		} else if (i==1) {
			presenter.removeCandidatesButtonPressed();
		} else if (i==2) {
			presenter.resetModelButtonPressed();
		}
	}
}