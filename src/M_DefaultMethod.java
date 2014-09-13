import java.util.*;

public class M_DefaultMethod implements I_VotingMethod {

	// Random winner
	public int selectWinner(I_Pollable[] voters, I_Electable[] candidates) {
		Random gen = new Random();
		return gen.nextInt(candidates.length);
	}
}