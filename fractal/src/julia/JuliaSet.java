package julia;

import java.util.*;
import java.util.stream.IntStream;

//TODO Change double to rational or something maybe
public class JuliaSet {
	// Setup
	private double cx;
	private double cy;

	public JuliaSet(double cx, double cy, int maxIterations) {
		this(cx, cy);
		this.maxIterations = maxIterations;
	}

	public JuliaSet(double cx, double cy) {
		setC(cx, cy);
	}

	public void setC(double x, double y) {
		cx = x;
		cy = y;
	}

	public JuliaSet clone() {
		return new JuliaSet(cx, cy, maxIterations);
	}

	// Calculation
	public int maxIterations = 20000;

	public int getIterations(double x, double y) {
		int count = 0;

		double x1 = x;
		double y1 = y;

		double xsq = x * x;
		double ysq = y * y;

		while (xsq + ysq < 4 && count <= maxIterations) {
			xsq = x * x;
			ysq = y * y;

			y = 2 * x * y + y1;
			x = xsq - ysq + x1;

			// For julia sets
			// double x0 = x;
			// x = x * x - y * y + cx;
			// y = 2 * x0 * y + cy;
			++count;
		}

		return count;
	}

	public int[] getIterations(List<Double> xs, List<Double> ys) {
		return IntStream.range(0, xs.size()).map(i -> getIterations(xs.get(i), ys.get(i))).toArray();
	}

	public void doubleMaxIterations() {
		maxIterations *= 2;
	}
}
