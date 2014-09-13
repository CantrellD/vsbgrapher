import javax.swing.*;
import java.awt.*;

public class ControlGraphPanel extends JPanel {

	// User-visible names for the voting methods
	public static final String[] METHOD_NAMES = {"Plurality", "Approval", "Borda", "Magic Best",
												"IRV", "Score", "Condorcet", "Magic Worst"};
	// Voting methods
	public static final int PLURALITY = 0;
	public static final int APPROVAL = 1;
	public static final int BORDA = 2;
	public static final int MAGIC_BEST = 3;
	public static final int IRV = 4;
	public static final int SCORE = 5;
	public static final int CONDORCET = 6;
	public static final int MAGIC_WORST = 7;

	// Independent variables
	public static final int NEW_VOTER_GROUP = 0;
	public static final int NEW_CANDIDATE_GROUP = 1;

	// Dependent variables
	public static final int WINNER = 0;
	public static final int REGRET = 1;

	// GUI
	private RadioGridPanel methodPanel;
	private RadioGridPanel iVariablePanel;
	private RadioGridPanel dVariablePanel;
	private GraphButtonPanel genButtonPanel;

	// Other
	private MainModel model;
	private ControlGraphPresenter presenter;


	public ControlGraphPanel(MainModel mainModel, MainPresenter mainPresenter) {
		model = mainModel;
		presenter = new ControlGraphPresenter(mainModel, mainPresenter, this);
		mainPresenter.registerControlGraphPanel(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.lightGray);

		methodPanel = new RadioGridPanel();
		iVariablePanel = new RadioGridPanel();
		dVariablePanel = new RadioGridPanel();
		genButtonPanel = new GraphButtonPanel();

		methodPanel.build(4, 0, METHOD_NAMES);
		iVariablePanel.build(2,0, "New Voter Group Location", "New Candidate Group Location");
		dVariablePanel.build(2, 0, "Winning Candidate", "Preventable Regret");
		genButtonPanel.build(2, "Basic Graph", "Alternate Graph");

		methodPanel.setBorder(BorderFactory.createTitledBorder("Method"));
		iVariablePanel.setBorder(BorderFactory.createTitledBorder("Independent Variable"));
		dVariablePanel.setBorder(BorderFactory.createTitledBorder("Dependent Variable"));

		add(methodPanel);
		add(iVariablePanel);
		add(dVariablePanel);
		add(genButtonPanel);
	}
	public int getMethod() {
		return methodPanel.getSelectedButton();
	}
	public int getIndependentVariable() {
		return iVariablePanel.getSelectedButton();
	}
	public int getDependentVariable() {
		return dVariablePanel.getSelectedButton();
	}

	private class GraphButtonPanel extends ButtonGridPanel {
		public void buttonPressed(int i) {
			presenter.graphRequested(i==1);		// Passes 'true' if 'Alternate Graph' was pressed, false otherwise.
		}
	}
}