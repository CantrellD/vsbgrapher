import java.util.*;

public class M_Borda implements I_VotingMethod {

	// Lowest ranked candidate gets one point, next lowest two, etc.
	public int selectWinner(I_Pollable[] voters, I_Electable[] candidates) {
		double[] votes = new double[candidates.length];
		int[] vote = new int[candidates.length];
		double ballotWeight;

		for (I_Pollable v : voters) {
			ballotWeight = v.getBallotWeight();
			vote = v.ordinalPoll(candidates);
			for (int i=0; i<candidates.length; i++) {
				votes[vote[i]] += ballotWeight*(candidates.length-i);
			}
		}
		int maxIndex = 0;
		for (int i=1; i<votes.length; i++) {
			if (votes[i] > votes[maxIndex]) {
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}