package fr.fractal;

public class MandelbrotSet implements Fractal {
    @Override
    public int getIterations(double x, double y, int maxIterations) {
        int count = 0;

        double x1 = x;
        double y1 = y;

        double xsq = x * x;
        double ysq = y * y;

        while (xsq + ysq < 4 && count <= maxIterations) {
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
