import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class ButtonGridPanel extends JPanel {

	private String[] buttonNames;
	private JButton[] buttonArray;

	// Creates a panel with one button for every string in the argument,
	// arranged in a grid with col buttons per row.
	protected void build(int colCount, String... buttonNames) {
		this.buttonNames = buttonNames;

		setLayout(new GridLayout(0, colCount));
		setBackground(Color.lightGray);

		buttonArray = new JButton[buttonNames.length];
		for (int i=0; i<buttonArray.length; i++) {
			buttonArray[i] = new JButton(buttonNames[i]);
			buttonArray[i].addActionListener(new CustomListener());
			add(buttonArray[i]);
		}

		setMaximumSize(new Dimension(getMaximumSize().width,getPreferredSize().height));
		setMinimumSize(new Dimension(getMinimumSize().width,getPreferredSize().height));
	}

	protected abstract void buttonPressed(int i);

	private class CustomListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			for (int i=0; i<buttonArray.length; i++) {
				if (buttonArray[i].equals((JButton)(event.getSource()))) {
					buttonPressed(i);
				}
			}
		}
	}
}