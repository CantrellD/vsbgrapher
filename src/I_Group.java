import java.util.*;

// Should be implemented by any class which models groups of voters or candidates
public interface I_Group<E> {
	public List<? extends E> getMembers();
	public YXPoint getLocation();				// Returns location of group center in internal coordinates
	public List<String> getProperties();		// Returns current values of important properties
}