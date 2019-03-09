package fr.fractal;

public class BurningShip implements Fractal {
    @Override
    public int getIterations(double x, double y, int maxIterations) {
        int count = 0;

        double x0 = x;
        double y0 = y;

        double xsq = x * x;
        double ysq = y * y;

        while (xsq + ysq < 4 && count <= maxIterations) {
            xsq = x * x;
            ysq = y * y;
            double xy = x * y;

            x = xsq - ysq + x0;
            y = Math.abs(xy + xy) + y0;

            ++count;
        }

        return count;
    }
}
