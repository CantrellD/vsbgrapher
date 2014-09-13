import java.util.*;

public class M_IRV implements I_VotingMethod {
	public int selectWinner(I_Pollable[] voters, I_Electable[] candidates) {
		double[] votes;
		double[] vote = new double[candidates.length];
		int minIndex;
		ArrayList<Integer> remainingCandidates = new ArrayList<>(candidates.length);
		for (int i=0; i<candidates.length; i++) {
			remainingCandidates.add(i);
		}
		while (remainingCandidates.size() > 1) {
			votes = new double[remainingCandidates.size()];
			for (I_Pollable v : voters) {
				for (int i=0; i<votes.length; i++) {
					vote[i] = v.poll(candidates[remainingCandidates.get(i)]);
				}
				minIndex = 0;
				for (int i=1; i<votes.length; i++) {
					if (vote[i] < vote[minIndex]) {
						minIndex = i;
					}
				}
				votes[minIndex] += v.getBallotWeight();
			}
			minIndex = 0;
			for (int i=1; i<votes.length; i++) {
				if (votes[i] < votes[minIndex]) {
					minIndex = i;
				}
			}
			remainingCandidates.remove((int)(minIndex));
		}
		return remainingCandidates.get(0);
	}
}