import java.util.*;

public class M_Condorcet implements I_VotingMethod {

	/* Note that a random winner is chosen if no Condorcet winner exists.
	 * Random member of the smith set might be better.
	 */
	public int selectWinner(I_Pollable[] voters, I_Electable[] candidates) {
		Random gen = new Random();
		double[] iVotes;
		double[] jVotes;
		double ballotWeight;
		boolean winner;
		int index;

		for (int i=0; i<candidates.length; i++) {
			iVotes = new double[candidates.length];
			jVotes = new double[candidates.length];
			for (I_Pollable v : voters) {
				ballotWeight = v.getBallotWeight();
				for (int j=0; j<candidates.length; j++) {
					if (i == j) {
						continue;
					}
					if (v.poll(candidates[i]) < v.poll(candidates[j])) {
						iVotes[j] += ballotWeight;
					} else {
						jVotes[j] += ballotWeight;
					}
				}
			}
			winner = true;
			index = 0;
			while (winner && index<candidates.length) {
				if (iVotes[index] < jVotes[index]) {
					winner = false;
				}
				index++;
			}
			if (winner) {
				return i;
			}
		}
		return gen.nextInt(candidates.length);
	}
}