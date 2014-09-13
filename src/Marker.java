import java.util.List;
import javax.swing.*;
import java.awt.*;

public class Marker {
	private static final int RADIUS = 4;
	private static final int BORDER_RADIUS = 5;

	private YXPoint location;	// Graphical coordinates
	private Color color;		// Color of inner circle
	private String description;	// e.g. "Voter group"
	private Object datum;		// Associated group identifier
	private boolean selected;	// Mostly unused

	/* Each marker is used to represent the location of a single group in the model.
	 * Datum is a reference to this group, but it's only used as a unique identifier.
	 * Description is an inelegant way to distinguish markers associated with voter
	 * groups from those associated with candidate groups.
	 */
	public Marker (YXPoint location, Color color, String description, Object datum) {
		this.location = location;
		this.color = color;
		this.description = description;
		this.datum = datum;
	}

	public YXPoint getLocation() {
		return location;
	}
	public Color getColor() {
		return color;
	}
	public String getDescription() {
		return description;
	}
	public Object getDatum() {
		return datum;
	}

	public void setLocation(int y, int x) {
		int dy = y - location.y;
		int dx = x - location.x;
		location.y += dy;
		location.x += dx;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean arg) {
		selected = arg;
	}
	public void draw(Graphics page) {
		// Draw border
		page.setColor(selected ? Color.white : Color.gray);
		page.drawOval(location.x-BORDER_RADIUS, location.y-BORDER_RADIUS, 2*BORDER_RADIUS,2*BORDER_RADIUS);

		// Draw inner disk
		page.setColor(color);
		page.fillOval(location.x-RADIUS, location.y-RADIUS,2*RADIUS,2*RADIUS);
	}

	/* Check if a point (graphical coordinates) is inside the area where the marker is being drawn.
	 * Used to determine whether a marker is being clicked.
	 */
	public boolean checkBounds(YXPoint cursor) {
		if (Math.hypot(cursor.x-location.x, cursor.y-location.y)<BORDER_RADIUS) {
			return true;
		}
		else {
			return false;
		}
	}
}