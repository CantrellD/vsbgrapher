import java.util.*;

public class CandidateGroup implements I_Group<I_Electable> {

	private YXPoint location;
	private ArrayList<Candidate> cList;
	int candidateCount;
	int radius;

	public CandidateGroup(YXPoint location, int candidateCount, int radius) {
		build(location, candidateCount, radius);
	}

	/* Location (internal coordinates) is for the center of the group. Candidate count is
	 * the number of candidates in the group. Radius is the distance between the candidates
	 * and the center of the group. Candidates are evenly spaced along a ring.
	 */
	public void build(YXPoint location, int candidateCount, int radius) {
		this.location = new YXPoint(location.y, location.x);
		this.candidateCount = candidateCount;
		this.radius = radius;

		cList = new ArrayList<Candidate>(candidateCount);
		Candidate temp;
		double theta;
		int y;
		int x;

		for (int i=0; i<candidateCount; i++) {
			theta = 2*Math.PI*(i/(double)candidateCount);
			y = (int)(radius * Math.cos(theta));
			x = (int)(radius * Math.sin(theta));
			cList.add(new Candidate(new YXPoint(location.y+y,location.x+x)));
		}
	}

	public List<? extends I_Electable> getMembers() {
		return new ArrayList<Candidate>(cList);
	}

	public YXPoint getLocation() {
		return location;
	}

	public void setLocation(YXPoint newLocation) {
		int dy = newLocation.y-location.y;
		int dx = newLocation.x-location.x;
		location.y += dy;
		location.x += dx;
		for (Candidate c : cList) {
			c.move(dy, dx);
		}
	}
	public List<String> getProperties() {
		ArrayList<String> result = new ArrayList<>(5);
		result.add(Integer.toString(location.x));
		result.add(Integer.toString(location.y));
		result.add(Integer.toString(candidateCount));
		result.add(Integer.toString(radius));
		return result;
	}
}