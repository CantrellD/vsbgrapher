import java.util.*;

public interface I_VotingMethod {
	public int selectWinner(I_Pollable[] voters, I_Electable[] candidates);
}