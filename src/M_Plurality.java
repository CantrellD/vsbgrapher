import java.util.*;

public class M_Plurality implements I_VotingMethod {
	public int selectWinner(I_Pollable[] voters, I_Electable[] candidates) {
		double[] votes = new double[candidates.length];
		double[] vote = new double[candidates.length];
		int minIndex;

		for (I_Pollable v : voters) {
			for (int i=0; i<candidates.length; i++) {
				vote[i] = v.poll(candidates[i]);
			}
			minIndex = 0;
			for (int i=1; i<vote.length; i++) {
				if (vote[i] < vote[minIndex]) {
					minIndex = i;
				}
			}
			votes[minIndex] += v.getBallotWeight();
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