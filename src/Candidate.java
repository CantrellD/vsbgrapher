public class Candidate implements I_Electable {

	private YXPoint location; // Position in internal coordinates. Models ideology.

	public Candidate(YXPoint location) {
		this.location = location;
	}

	public YXPoint getLocation() {
		return new YXPoint(location);
	}

	public void move(int dy, int dx) {
		location.y += dy;
		location.x += dx;
	}
}