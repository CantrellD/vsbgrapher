import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class TextFieldPanel extends JPanel{

	private JPanel panelA;				// Panel containing labels
	private JPanel panelB;				// Panel containing text fields
	private JLabel[] labelArray;
	private JTextField[] fieldArray;
	private Color background;

	// TextFieldPanel creates a label and a text field for each string passed as an argument.
	public void build(String... fieldNames) {

		panelA = new JPanel(); // Holds labels
		panelB = new JPanel(); // Holds text fields

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		panelA.setLayout(new GridLayout(fieldNames.length,1));
		panelA.setBackground(background);
		panelB.setLayout(new GridLayout(fieldNames.length,1));
		panelB.setBackground(background);

		labelArray = new JLabel[fieldNames.length];
		fieldArray = new JTextField[fieldNames.length];
		for (int i=0; i<fieldNames.length; i++) {
			fieldArray[i] = new JTextField();
			labelArray[i] = new JLabel(fieldNames[i]);
			panelA.add(labelArray[i]);
			panelB.add(fieldArray[i]);
		}
		panelA.setMaximumSize(new Dimension(panelA.getPreferredSize().width,
											panelB.getMinimumSize().height));
		panelB.setMaximumSize(new Dimension(panelB.getMaximumSize().width,
											panelB.getMinimumSize().height));
		add(panelA);
		 add(Box.createRigidArea(new Dimension(4,0)));
		add(panelB);
	}

	/* The background should be set prior to calling build, unless you want the background set
	 * to null for some reason. I don't atually know what that does, but it solved an issue I was
	 * having with text fields in dialog boxes. The issue should be fixed properly at some point.
	 */
	public void setBackground(Color color) {
		background = color;
	}

	public void setAllText(List<String> newText) {
		for (int i=0; i<fieldArray.length; i++) {
			fieldArray[i].setText(newText.get(i));
		}
	}
	public List<String> getAllText() {
		ArrayList<String> result = new ArrayList<>(fieldArray.length);
		for (int i=0; i<fieldArray.length; i++) {
			result.add(fieldArray[i].getText());
		}
		return result;
	}
}