import java.util.*;

public class M_MagicBest implements I_VotingMethod {
	public int selectWinner(I_Pollable[] voters, I_Electable[] candidates) {
		double[] antivotes = new double[candidates.length];
		double ballotWeight;

		for (I_Pollable v : voters) {
			ballotWeight = v.getBallotWeight();
			for (int i=0; i<candidates.length; i++) {
				antivotes[i] += v.poll(candidates[i])*ballotWeight;
			}
		}
		int minIndex = 0;
		for (int i=1; i<antivotes.length; i++) {
			if (antivotes[i] < antivotes[minIndex]) {
				minIndex = i;
			}
		}
		return minIndex;
	}
}