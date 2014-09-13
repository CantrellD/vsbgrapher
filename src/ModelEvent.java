public class ModelEvent {
	public static final int VOTERS_CREATED = 0;
	public static final int VOTERS_DELETED = 1;
	public static final int VOTERS_UPDATED = 2;
	public static final int VOTERS_MOVED = 3;
	public static final int CANDIDATES_CREATED = 4;
	public static final int CANDIDATES_DELETED = 5;
	public static final int CANDIDATES_UPDATED = 6;
	public static final int CANDIDATES_MOVED = 7;

	private int eventType;

	// Makes notifications more informative in principle. Isn't used in practice.
	public ModelEvent(int eventType) {
		this.eventType = eventType;
	}

	public int getEventType() {
		return eventType;
	}
}