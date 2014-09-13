import java.util.*;

public class M_Score implements I_VotingMethod {
	public int selectWinner(I_Pollable[] voters, I_Electable[] candidates) {
		double[] votes = new double[candidates.length];
		double[] vote;
		double sum;
		double avg;
		double ballotWeight;

		for (I_Pollable v : voters) {
			ballotWeight = v.getBallotWeight();
			vote = v.cardinalPoll(candidates);
			for (int i=0; i<candidates.length; i++) {
				votes[i] += vote[i]*ballotWeight;
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