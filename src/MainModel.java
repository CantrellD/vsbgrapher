import java.util.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class MainModel /*implements Observable*/ {

	// These should probably be in I_Group
	public static final String[] VOTER_PROPERTIES = {"X Position", "Y Position", "Population", "Sigma", "Precision"};
	public static final String[] CANDIDATE_PROPERTIES = {"X Position", "Y Position", "Count", "Radius"};
	public static final String[] VOTER_DEFAULTS = {"0", "0", "1000000", "100", "30"};
	public static final String[] CANDIDATE_DEFAULTS = {"0","0","1","0"};

	// 'Voter' and 'proxy' are basically synonymous in this program. Probably should have just used 'voter'.
	private ArrayList<ProxyGroup> pGroupList;
	private ArrayList<CandidateGroup> cGroupList;

	// List of objects which are notified when something changes (unless model is muted)
	private ArrayList<I_ModelObserver> oList;

	// Size, in internal coordinates, of the region where groups can exist. Members of groups can exist outside this region.
	private Dimension size;

	// Used to disable notifications when graphs are being generated.
	private boolean mute;

	/* Responsible for maintaining primary record of voter and candidate groups, reporting changes to registered observers,
	 * validating user input, and assigning a color to each group.
	 */
	public MainModel() {
		pGroupList = new ArrayList<ProxyGroup>();
		cGroupList = new ArrayList<CandidateGroup>();
		oList = new ArrayList<I_ModelObserver>();
		size = new Dimension(600,600);
		mute = false;
	}

	// OBSERVATION
	public void addObserver(I_ModelObserver newObserver) {
		if (!oList.contains(newObserver)) {
			oList.add(newObserver);
		}
	}

	private void notifyObservers(ModelEvent e) {
		if (!mute) {
			for (I_ModelObserver o : oList) {
				o.notify(e);
			}
		}
	}

	public void mute() {
		mute = true;
	}

	public void unmute() {
		mute = false;
	}

	public Object createVoterGroup(YXPoint location, int population, int sigma, int precision) {
		ProxyGroup temp = new ProxyGroup(location, population, sigma, precision);
		pGroupList.add(temp);
		notifyObservers(new ModelEvent(ModelEvent.VOTERS_CREATED));
		return temp;
	}

	public Object createCandidateGroup(YXPoint location, int cCount, int radius) {
		CandidateGroup temp = new CandidateGroup(location, cCount, radius);
		cGroupList.add(temp);
		notifyObservers(new ModelEvent(ModelEvent.CANDIDATES_CREATED));
		return temp;
	}

	// DELETE
	public boolean deleteVoterGroup(Object targetGroup) {
		if (pGroupList.contains(targetGroup)) {
			pGroupList.remove(targetGroup);
			notifyObservers(new ModelEvent(ModelEvent.VOTERS_DELETED));
			return true;
		} else {
			return false;
		}
	}

	public boolean deleteCandidateGroup(Object targetGroup) {
		if (cGroupList.contains(targetGroup)) {
			cGroupList.remove(targetGroup);
			notifyObservers(new ModelEvent(ModelEvent.CANDIDATES_DELETED));
			return true;
		} else {
			return false;
		}
	}

	public void deleteAllVoterGroups() {
		pGroupList = new ArrayList<ProxyGroup>();
		notifyObservers(new ModelEvent(ModelEvent.VOTERS_DELETED));
	}

	public void deleteAllCandidateGroups() {
		cGroupList = new ArrayList<CandidateGroup>();
		notifyObservers(new ModelEvent(ModelEvent.CANDIDATES_DELETED));
	}

	// RETRIEVE
	public List<? extends I_Group> getVoterGroups() {
		return new ArrayList<ProxyGroup>(pGroupList);
	}

	public List<? extends I_Group> getCandidateGroups() {
		return new ArrayList<CandidateGroup>(cGroupList);
	}

	// Wouldn't normally return an array, but lists were too slow for graphing
	public I_Pollable[] getVoters() {
		int temp=0;
		for (ProxyGroup g : pGroupList) {
			temp += g.getMembers().size();
		}
		I_Pollable[] result = new I_Pollable[temp];
		temp=0;
		for (ProxyGroup g : pGroupList) {
			for (I_Pollable p : g.getMembers()) {
				result[temp++] = p;
			}
		}
		return result;
	}

	public I_Electable[] getCandidates() {
		int temp=0;
		for (CandidateGroup g : cGroupList) {
			temp += g.getMembers().size();
		}
		I_Electable[] result = new I_Electable[temp];
		temp=0;
		for (CandidateGroup g : cGroupList) {
			for (I_Electable c : g.getMembers()) {
				result[temp++] = c;
			}
		}
		return result;
	}

	// UPDATE
	public boolean updateVoterGroup(Object targetGroup, YXPoint location, int population, int sigma, int precision) {
		if (pGroupList.contains(targetGroup)) {
			pGroupList.get(pGroupList.indexOf(targetGroup)).build(location, population, sigma, precision);
			notifyObservers(new ModelEvent(ModelEvent.VOTERS_UPDATED));
			return true;
		} else {
			return false;
		}
	}

	public boolean updateCandidateGroup(Object targetGroup, YXPoint location, int cCount, int radius) {
		if (cGroupList.contains(targetGroup)) {
			cGroupList.get(cGroupList.indexOf(targetGroup)).build(location, cCount, radius);
			notifyObservers(new ModelEvent(ModelEvent.CANDIDATES_UPDATED));
			return true;
		} else {
			return false;
		}
	}

	public boolean moveVoterGroup(Object targetGroup, YXPoint newLocation) {
		if (pGroupList.contains(targetGroup)) {
			pGroupList.get(pGroupList.indexOf(targetGroup)).setLocation(newLocation);
			notifyObservers(new ModelEvent(ModelEvent.VOTERS_MOVED));
			return true;
		} else {
			return false;
		}
	}

	public boolean moveCandidateGroup(Object targetGroup, YXPoint newLocation) {
		if (cGroupList.contains(targetGroup)) {
			cGroupList.get(cGroupList.indexOf(targetGroup)).setLocation(newLocation);
			notifyObservers(new ModelEvent(ModelEvent.CANDIDATES_MOVED));
			return true;
		} else {
			return false;
		}
	}

	// ALTERNATE

	public boolean deleteGroup(Object targetGroup) {
		if (pGroupList.contains(targetGroup)) {
			pGroupList.remove(targetGroup);
			notifyObservers(new ModelEvent(ModelEvent.VOTERS_DELETED));
			return true;
		} else if (cGroupList.contains(targetGroup)) {
			cGroupList.remove(targetGroup);
			notifyObservers(new ModelEvent(ModelEvent.CANDIDATES_DELETED));
			return true;
		} else {
			return false;
		}

	}

	public boolean moveGroup(Object targetGroup, YXPoint newLocation) {
		if (pGroupList.contains(targetGroup)) {
			pGroupList.get(pGroupList.indexOf(targetGroup)).setLocation(newLocation);
			notifyObservers(new ModelEvent(ModelEvent.VOTERS_MOVED));
			return true;
		} else if (cGroupList.contains(targetGroup)) {
			cGroupList.get(cGroupList.indexOf(targetGroup)).setLocation(newLocation);
			notifyObservers(new ModelEvent(ModelEvent.CANDIDATES_MOVED));
			return true;
		} else {
			return false;
		}
	}

	// MISCELLANEOUS / MISPLACED

	// Returns size, in internal coordinates, of the region where groups can exist.
	public Dimension getSize() {
		return new Dimension(size.width, size.height);
	}

	/* Returns one of eight colors (or darker variants of the same) for candidate groups,
	 * based on their index in cGroupList. Returns gray for voter groups in pGroupList.
	 * Returns white if group isn't found in either list. This method should probably be
	 * in a different class, with int as the argument type.
	 *
	 * Note that candidates in the same group have the same color.
	 */
	public Color getColor(Object group) {
		if (pGroupList.contains(group)) {
			return Color.gray;
		} else if (cGroupList.contains(group)) {
			Color result;
			int index = cGroupList.indexOf(group);
			switch (index%8) {
				case 0:	result = Color.red;
						break;
				case 1:	result = Color.blue;
						break;
				case 2:	result = Color.green;
						break;
				case 3:	result = Color.yellow;
						break;
				case 4:	result = Color.magenta;
						break;
				case 5:	result = Color.cyan;
						break;
				case 6:	result = Color.orange;
						break;
				case 7:	result = Color.pink;
						break;
				default:result = Color.black;
			}
			for (int i=0, s=index/8; i<s; i++) {
				result = result.darker();
			}
			return result;
		} else {
			return Color.white;
		}
	}

	public List<Integer> validateUserDefinedVoterProperties(List<String> input) {
		List<Integer> properties = validateUserDefinedProperties(input);

		if (properties == null) {
			return null;
		}

		// Check that precision isn't greater than 600. Still possible to run out of memory with many groups.
		if (properties.get(4) > 600) {
			JOptionPane.showMessageDialog(	new JPanel(), "Invalid user input: Values greater than 600 are not supported for all fields.",
											"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return properties;
	}

	public List<Integer> validateUserDefinedCandidateProperties(List<String> input) {
		List<Integer> properties = validateUserDefinedProperties(input);

		if (properties == null) {
			return null;
		}

		// Check that candidate count isn't greater than 600. Still possible to run out of memory with many groups.
		if (properties.get(2) > 600) {
			JOptionPane.showMessageDialog(	new JPanel(), "Invalid user input: Values greater than 600 are not supported for all fields.",
											"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return properties;
	}

	private List<Integer> validateUserDefinedProperties(List<String> input) {
		ArrayList<Integer> properties = new ArrayList<>(input.size());

		if (input.size() < 4) {
			throw new IllegalArgumentException();
		}

		// Check that input strings can be converted to ints
		int lastIndex = 0;
		try {
			for (int i=0; i<input.size(); i++) {
				lastIndex = i;
				properties.add(Integer.parseInt(input.get(i)));
			}
		} catch (NumberFormatException e) {
			if (input.get(lastIndex).matches("-?\\d+")) { // Regular expression to check for integer that was too large to parse
				JOptionPane.showMessageDialog(	new JPanel(), "Invalid user input: Integers outside the range ["
												+Integer.MIN_VALUE+", "+Integer.MAX_VALUE+"] are not supported.",
												"Error", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(	new JPanel(), "Invalid user input: Non-integer values are not supported.",
												"Error", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		}

		// Check that user defined position is within boundaries set by size
		if (properties.get(0) < -size.width/2 || properties.get(0) > size.width/2 || properties.get(1) < -size.width/2 || properties.get(1) > size.width/2) {
			JOptionPane.showMessageDialog(	new JPanel(), "Invalid user input: Position values must be in the range [-300, 300].",
											"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		// Check that non-position values are non-negative
		for (int i=2; i<properties.size(); i++) {
			if (properties.get(i) < 0) {
				JOptionPane.showMessageDialog(	new JPanel(), "Invalid user input: Negative values are not supported for all fields.",
												"Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}

		// Return (mostly) validated input
		return properties;
	}
}