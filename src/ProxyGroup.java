import java.util.*;

public class ProxyGroup implements I_Group<I_Pollable> {

	// Proxies are generated within a grid 2*RANGE*sigma across. Three sigma rule is relevant.
	private static final double RANGE = 3.0;

	private YXPoint location;
	private int constituents;
	private int sigma;
	private int precision;
	private ArrayList<Proxy> pList;

	/* Location (internal coordinates) is for the center of the group. Constituents is for the
	 * desired sum of all ballot weights in the group. Sigma is the standard deviation for the
	 * normal distribution used to assign ballot weights based on distance from center of the
	 * group. Precision of n gives an n by n grid of proxies which is six sigma across.
	 *
	 * Note that a proxy's ballot weight is supposed to represent the expected number of voters
	 * for whom they would be the best representative if voters were randomly generated.
	 *
	 * Oh, and sigma is a distance in internal coordinates, I suppose.
	 */
	public ProxyGroup(YXPoint location, int constituents, int sigma, int precision) {
		build(location, constituents, sigma, precision);
	}

	public void build(YXPoint location, int constituents, int sigma, int precision) {
		this.location = new YXPoint(location.y, location.x);
		this.constituents = constituents;
		this.sigma = sigma;
		this.precision = precision;

		int rowCount = precision;
		int colCount = precision;
		pList = new ArrayList<Proxy>(rowCount*colCount);
		YXPoint vLocation;

		// Generate proxy locations. Also calculate the size of the sample group used to determine
		// the popularity of each proxy, for want of a better description.
		YXPoint[] locArray = new YXPoint[rowCount*colCount];
		double tempSum = 0;

		double yStep = (2*RANGE*sigma)/(double)(rowCount-1);
		double xStep = (2*RANGE*sigma)/(double)(colCount-1);
		int index = 0;
		for (int row=0; row<rowCount; row++) {
			for (int col=0; col<colCount; col++) {
				locArray[index] = new YXPoint(	(int)((row*yStep)+(location.y+yStep/2)-(yStep*rowCount/2)),
												(int)((col*xStep)+(location.x+xStep/2)-(xStep*colCount/2)));
				tempSum += gauss(Math.hypot(location.x-locArray[index].x, location.y-locArray[index].y), (double)sigma);
				index++;
			}
		}

		// Calculate popularity of each proxy, and create the proxies. Not very precise, probably.
		for (int i=0; i<locArray.length; i++) {
			pList.add(new Proxy(	locArray[i], constituents * gauss(Math.hypot(location.x-locArray[i].x, location.y-locArray[i].y),
									(double)sigma) / tempSum));
		}
	}

	public List<? extends I_Pollable> getMembers() {
		return new ArrayList<Proxy>(pList);
	}

	public YXPoint getLocation() {
		return new YXPoint(location);
	}

	public void setLocation(YXPoint newLocation) {
		int dy = newLocation.y-location.y;
		int dx = newLocation.x-location.x;
		location.y += dy;
		location.x += dx;
		for (Proxy p : pList) {
			p.move(dy, dx);
		}
	}

	public List<String> getProperties() {
		ArrayList<String> result = new ArrayList<>(5);
		result.add(Integer.toString(location.x));
		result.add(Integer.toString(location.y));
		result.add(Integer.toString(constituents));
		result.add(Integer.toString(sigma));
		result.add(Integer.toString(precision));
		return result;
	}

	// Normal distribution function. Used for ballot weights.
	private static double gauss(double r, double sigma) {
		return Math.pow(Math.E,(-r*r/(2*sigma*sigma)))/
			   (Math.sqrt(2*Math.PI)*sigma);
	}
}