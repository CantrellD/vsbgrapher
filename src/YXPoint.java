import java.awt.*;

/* Wrote this back when I was using 2D arrays a lot, because statements like
 * points[row][col] = new Point(col, row); seemed needlessly confusing.
 * In hindsight I should've just used Point.
 */
public class YXPoint {

	/* I could make these final so that defensive copies wouldn't be needed, but
	 * then updating values would be slower. Leaving it as is for now.
	 */
	public int y;
	public int x;

	// Used to hold two ints representing x and y positions in a single object.
	public YXPoint(int y, int x) {
		this.y = y;
		this.x = x;
	}
	public YXPoint(Point point) {
		this.y = point.y;
		this.x = point.x;
	}
	public YXPoint(YXPoint yx) {
		this.y = yx.y;
		this.x = yx.x;
	}
	public boolean equals(YXPoint point) {
		return (point.y == y && point.x == x);
	}
	public String toString() {
		return "YXPoint(y=" + y + ", x=" + x + ")";
	}
}
