import java.util.*;

public class M_Approval implements I_VotingMethod {

	// Candidates perceived to be better than average are approved.
	public int selectWinner(I_Pollable[] voters, I_Electable[] candidates) {
		double[] votes = new double[candidates.length];
		double[] vote = new double[candidates.length];
		double sum;
		double avg;
		double ballotWeight;

		for (I_Pollable v : voters) {
			ballotWeight = v.getBallotWeight();
			sum = 0;
			for (int i=0; i<candidates.length; i++) {
				vote[i] = v.poll(candidates[i]);
				sum += vote[i];
			}
			avg = sum/candidates.length;
			for (int i=0; i<candidates.length; i++) {
				if (vote[i] < avg) {
					votes[i] += ballotWeight;
				}
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