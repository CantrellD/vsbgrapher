import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Arrays;

public class ControlPropertiesPanel extends JPanel {

	// Card identifiers
	public static final String VOTER_CARD = "New Voters Panel";
	public static final String CANDIDATE_CARD = "New Candidates Panel";

	// Card selection interface
	private ContentControlPanel contentControlPanel;

	// Card container
	private JPanel contentPanel;

	// Cards
	private JPanel voterCard;
	private JPanel candidateCard;

	// Card contents. Note that the two buttons have the same label / position.
	private TextFieldPanel voterPropertiesPanel;
	private TextFieldPanel candidatePropertiesPanel;
	private JButton voterCardButton;
	private JButton candidateCardButton;

	// Other
	private MainModel model;
	private ControlPropertiesPresenter presenter;
	private String currentCard;

	public ControlPropertiesPanel(MainModel mainModel, MainPresenter mainPresenter) {
		model = mainModel;
		presenter = new ControlPropertiesPresenter(mainModel, mainPresenter, this);
		mainPresenter.registerControlPropertiesPanel(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.lightGray);

		// Create voter card contents
		voterPropertiesPanel = new TextFieldPanel();
		voterPropertiesPanel.setBackground(Color.lightGray);
		voterPropertiesPanel.build(MainModel.VOTER_PROPERTIES);
		voterPropertiesPanel.setBorder(BorderFactory.createTitledBorder("New Group Properties"));
		voterCardButton = new JButton("Add Group");
		voterCardButton.addActionListener(new CardButtonListener(VOTER_CARD));

		// Create voter card
		voterCard = new JPanel();
		voterCard.setLayout(new BorderLayout());
		voterCard.setBackground(Color.lightGray);
		voterCard.add(voterPropertiesPanel, BorderLayout.PAGE_START);
		voterCard.add(voterCardButton, BorderLayout.PAGE_END);

		// Create candidate card contents
		candidatePropertiesPanel = new TextFieldPanel();
		candidatePropertiesPanel.setBackground(Color.lightGray);
		candidatePropertiesPanel.build(MainModel.CANDIDATE_PROPERTIES);
		candidatePropertiesPanel.setBorder(BorderFactory.createTitledBorder("New Group Properties"));
		candidateCardButton = new JButton("Add Group");
		candidateCardButton.addActionListener(new CardButtonListener(CANDIDATE_CARD));

		// Create candidate card
		candidateCard = new JPanel();
		candidateCard.setLayout(new BorderLayout());
		candidateCard.setBackground(Color.lightGray);
		candidateCard.add(candidatePropertiesPanel, BorderLayout.PAGE_START);
		candidateCard.add(candidateCardButton, BorderLayout.PAGE_END);

		// Create card container
		contentPanel = new JPanel(new CardLayout());
		contentPanel.add(voterCard, VOTER_CARD);
		contentPanel.add(candidateCard, CANDIDATE_CARD);

		// Create card selection interface
		contentControlPanel = new ContentControlPanel(contentPanel, VOTER_CARD, CANDIDATE_CARD);
		contentControlPanel.setBorder(BorderFactory.createTitledBorder("New Group Type"));

		add(contentControlPanel);
		add(contentPanel);
		setMaximumSize(new Dimension(getMaximumSize().width,getPreferredSize().height));
	}

	// Returns the identifier of the card currently being displayed
	public String getCurrentCardID() {
		return currentCard;
	}

	/* Returns list containing contents of text fields from the specified card, likely written by user.
	 * Should probably just return contents of text fields from current card instead.
	 */
	public List<String> getProperties(String cardID) {
		if (cardID.equals(VOTER_CARD)) {
			return voterPropertiesPanel.getAllText();
		} else if (cardID.equals(CANDIDATE_CARD)) {
			return candidatePropertiesPanel.getAllText();
		} else {
			throw new IllegalArgumentException("invalid cardID");
		}
	}
	private class ContentControlPanel extends RadioGridPanel {
		JPanel contentPanel;
		String[] potentialContentArray;
		CardLayout cl;

		// Causes different cards to be shown depending on which radio button is selected.
		public ContentControlPanel(JPanel contentPanel, String... potentialContentArray) {
			this.contentPanel = contentPanel;
			this.potentialContentArray = potentialContentArray;
			cl = (CardLayout)(contentPanel.getLayout());
			build(2, 0, "Voter Group", "Candidate Group");
		}

		// Called automatically whenever a radio button is selected.
		protected void buttonSelected(int i) {
			cl.show(contentPanel, potentialContentArray[i]);
			currentCard = potentialContentArray[i];
			if (i == 0) {
				voterPropertiesPanel.setAllText(Arrays.asList(MainModel.VOTER_DEFAULTS));
			} else if (i == 1) {
				candidatePropertiesPanel.setAllText(Arrays.asList(MainModel.CANDIDATE_DEFAULTS));
			}
		}
	}
	private class CardButtonListener implements ActionListener {

		String card;

		/* 'card' used to distinguish between buttons. Should've used event.getSource() and distinct
		 * methods in presenter instead.
		 */
		public CardButtonListener(String card) {
			this.card = card;
		}

		public void actionPerformed (ActionEvent event) {
			presenter.cardButtonPressed(card);
		}
	}
}