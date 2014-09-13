import javax.swing.*;
import java.awt.*;

public class Lens {

	private Dimension internalResolution;
	private Dimension graphicalResolution;
	private double yScale;
	private double xScale;

	/* Lens is used to change internal coordinates to graphical coordinates, and vice versa.
	 * Note that graphical coordinates are always positive, while internal coordinates are divided
	 * evenly between positive and negative values. It probably would have made more sense to have
	 * different classes for points in different coordinate systems.
	 */
	public Lens(Dimension internalResolution, Dimension graphicalResolution) {
		this.internalResolution = internalResolution;
		this.graphicalResolution = graphicalResolution;
		yScale = internalResolution.height / (double) graphicalResolution.height;
		xScale = internalResolution.width / (double) graphicalResolution.width;
	}
	public YXPoint toGraphical(YXPoint internalPoint) {
		return new YXPoint(	(int) (internalPoint.y * (1/yScale) + (graphicalResolution.height / 2)),
								(int) (internalPoint.x * (1/xScale) + (graphicalResolution.width / 2)));
	}
	public YXPoint toInternal(YXPoint graphicalPoint) {
		return new YXPoint(	(int) ((graphicalPoint.y - (graphicalResolution.height / 2)) * yScale),
							(int) ((graphicalPoint.x - (graphicalResolution.width / 2)) * xScale));
	}
}