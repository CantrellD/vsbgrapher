import java.util.*;

// Should be implemented by any class which models voters.
public interface I_Pollable {

	public double poll(I_Electable c);						// Returns opposition to a candidate, i.e. distance (not squared distance)
	public int[] ordinalPoll(I_Electable[] candidates);		// Returns an array of candidate indexes in descending order of support.
	public double[] cardinalPoll(I_Electable[] candidates); // Returns normalized cardinal utilities, with 0 as max opposition.
	public double getBallotWeight();						// Could be anything from zero to Integer.MAX_VALUE
	public YXPoint getLocation();							// Used for drawing.
	public YXPoint unsafeGetLocation();						// Useful if you want to manually calculate preferences quickly.
}