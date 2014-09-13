import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RadioGridPanel extends JPanel{
	private int selectedButton;
	private JRadioButton[] buttonArray;
	private ButtonGroup buttonGroup;

	/* RadioGridPanel creates a group of radio buttons, with one button for each
	 * string passed as an argument. It arranges them in a grid with a specified
	 * number of columns.
	 */
	public void build(int colCount, int initialSelection, String... buttonNames) {
		buttonGroup = new ButtonGroup();
		setLayout(new GridLayout(0, colCount));
		setBackground(Color.lightGray);
		buttonArray = new JRadioButton[buttonNames.length];
		for (int i=0; i<buttonNames.length; i++) {
			buttonArray[i] = new JRadioButton(buttonNames[i]);
			buttonArray[i].setBackground(Color.lightGray);
			buttonArray[i].addActionListener(new RadioListener());
			buttonGroup.add(buttonArray[i]);
			add(buttonArray[i]);
		}
		selectButton(initialSelection);
	}

	public void selectButton(int index) {
		if (index != -1) {
			buttonArray[index].setSelected(true);
			selectedButton = index;
			buttonSelected(index);
		}
	}

	public int getSelectedButton() {
		return selectedButton;
	}

	// Called automatically whenever a radio button is selected.
	protected void buttonSelected(int index) {}

	private class RadioListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {
		    Object source = event.getSource();
			for (int i=0; i<buttonArray.length; i++) {
				if (source == buttonArray[i]) {
					selectButton(i);
					break;
				}
			}
		}
	}
}