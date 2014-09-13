// Classes which need to be notified when the main model changes should implement this interface.
public interface I_ModelObserver {
	public void notify(ModelEvent event);
}