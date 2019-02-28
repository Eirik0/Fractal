package julia;

public class Fractals {
	public static final int MAX_ITERATIONS = 20000;

	public static interface Fractal {
		public int getIterations(double x, double y);

		public default int[] getIterations(int x, int y, double pixelPerData) {
			int dataPer = (int) Math.round(1 / pixelPerData);
			int[] iterations = new int[dataPer * dataPer];
			int i = 0;
			for (double xOffset = 0; xOffset < 1; xOffset += pixelPerData) {
				for (double yOffset = 0; yOffset < 1; yOffset += pixelPerData) {
					iterations[i] = getIterations(JuliaSizer.getX(x + xOffset), JuliaSizer.getY(y + yOffset));
					++i;
				}
			}
			return iterations;
		}
	}

	public static class MandelbrotSet implements Fractal {
		@Override
		public int getIterations(double x, double y) {
			int count = 0;

			double x1 = x;
			double y1 = y;

			double xsq = x * x;
			double ysq = y * y;

			while (xsq + ysq < 4 && count <= MAX_ITERATIONS) {
				xsq = x * x;
				ysq = y * y;
				double xy = x * y;

				y = xy + xy + y1;
				x = xsq - ysq + x1;

				++count;
			}

			return count;
		}
	}

	public static class JuliaSet implements Fractal {
		private final double cx;
		private final double cy;

		public JuliaSet(double cx, double cy) {
			this.cx = cx;
			this.cy = cy;
		}

		public double getCx() {
			return cx;
		}

		public double getCy() {
			return cy;
		}

		@Override
		public int getIterations(double x, double y) {
			int count = 0;

			double xsq = x * x;
			double ysq = y * y;

			while (xsq + ysq < 4 && count <= MAX_ITERATIONS) {
				xsq = x * x;
				ysq = y * y;
				double xy = x * y;

				y = xy + xy + cy;
				x = xsq - ysq + cx;

				++count;
			}

			return count;
		}
	}
}
