package fr.fractal;

public class JuliaSet implements Fractal {
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
    public int getIterations(double x, double y, int maxIterations) {
        int count = 0;

        double xsq = x * x;
        double ysq = y * y;

        while (xsq + ysq < 4 && count <= maxIterations) {
            xsq = x * x;
            ysq = y * y;
            double xy = x * y;

            x = xsq - ysq + cx;
            y = xy + xy + cy;

            ++count;
        }

        return count;
    }
}
