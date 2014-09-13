import java.util.*;

public class Proxy implements I_Pollable {
	private YXPoint location; // Position in internal coordinates. Models ideology.
	private double ballotWeight;

	/* Proxies are voters. They can be polled to determine their preferences,
	 * which are derived from their location in internal coordinates relative
	 * electable classes. Greater distance implies greater opposition.
	 */
	public Proxy(YXPoint location, double ballotWeight) {
		this.location = new YXPoint(location);
		this.ballotWeight = ballotWeight;
	}

	public void move(int dy, int dx) {
		location.y += dy;
		location.x += dx;
	}

	public YXPoint getLocation() {
		// Defensive copy. Not necessary if YXPoint becomes immutable.
		return new YXPoint(location);
	}

	/* This method only exists to allow slightly faster voting method
	 * implementations. It probably shouldn't be used at all.
	 */
	public YXPoint unsafeGetLocation() {
		return location;
	}

	public double getBallotWeight() {
		return ballotWeight;
	}

	// Return voter's distance, i.e. opposition, to a candidate.
	public double poll(I_Electable c) {
		double dy = c.getLocation().y-location.y;
		double dx = c.getLocation().x-location.x;
		return Math.sqrt(dy*dy+dx*dx); // Should probably use Math.hyot().
	}

	/* Returns an array of candidate indexes in descending order of support.
	 * Note that a custom implementation of merge sort is used, so bugs are
	 * more probable than usual.
	 */
	public int[] ordinalPoll(I_Electable[] candidates) {
		double dy;
		double dx;
		double[] polls = new double[candidates.length];
		int[] ballot = new int[candidates.length];
		int[] oldBallot = new int[candidates.length];
		int[] temp;
		for (int i=0; i<candidates.length; i++) {
			dy = candidates[i].getLocation().y-location.y;
			dx = candidates[i].getLocation().x-location.x;
			polls[i] = dy*dy+dx*dx; // Reasonably confident this works.
			ballot[i] = i;
		}

		// Distance between subarrays
		int step = 1;

		// Indexes of subarrays to be merged together
		int a;
		int b;

		// Indexes of lowest unmerged values within subarrays
		int i;
		int j;

		// Sort by repeatedly merging the subarray starting at a with the subarray starting at b
		while (step < candidates.length) {
			a = 0;
			b = step;
			temp = oldBallot;
			while (b < candidates.length) {
				i = 0;
				j = 0;
				while (i+j != step+step && (i != step || b+j != candidates.length)) {
					if (j == step || b+j == candidates.length || (i != step && polls[ballot[a+i]] <= polls[ballot[b+j]]) ) {
						temp[a+i+j] = ballot[a+i];
						i++;
					} else {
						temp[a+i+j] = ballot[b+j];
						j++;
					}
				}
				a = b+step;
				b = a+step;
			}
			oldBallot = ballot;
			ballot = temp;
			step += step;
		}
		return ballot;
	}

	/* Returns normalized cardinal utilities, with 0 as max opposition and 1 as min opposition.
	 * Returns all zeroes if max opposition is zero, all ones if max and min are equal and nonzero.
	 * Note that this method hasn't been tested.
	 */
	public double[] cardinalPoll(I_Electable[] candidates) {
		double dy;
		double dx;
		double[] ballot = new double[candidates.length];
		for (int i=0; i<ballot.length; i++) {
			dy = candidates[i].getLocation().y-location.y;
			dx = candidates[i].getLocation().x-location.x;
			ballot[i] = Math.sqrt(dy*dy+dx*dx);
		}
		double min = ballot[0];
		double max = ballot[0];
		for (int i=1; i<ballot.length; i++) {
			if (ballot[i] < min) {
				min = ballot[i];
			} else if (ballot[i] > max) {
				max = ballot[i];
			}
		}
		if (max > 0) {
			for (int i=0; i<ballot.length; i++) {
				ballot[i] = 1 - ((ballot[i] - min) / max);
			}
		} else {
			ballot = new double[candidates.length];
		}
		return ballot;
	}

	/* Pretty sure this isn't used anywhere. Might come back at some point and see if
	 * look up tables can be used to speed up poll methods. I suspect not.
	 */
	private double calcDistance(YXPoint location) {
		return Math.hypot(this.location.x - location.x, this.location.y - location.y);
	}

	/* No longer used. Its purpose was to allow the use of Collections.sort() for the
	 * ordinalPoll method. Leaving it here in case I decide manual implementation of a
	 * sort algorithm was foolish.
	 */
	private class Option implements Comparable<Option> {
		public int index;
		public double distance;

		public Option(int index, double distance) {
			this.index = index;
			this.distance = distance;
		}

		public int compareTo(Option o) {
			return (int)(distance - o.distance); // May be backwards.
		}
	}
}