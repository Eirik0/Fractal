package fr.fractal;

public interface Fractal {
    int getIterations(double x, double y, int maxIterations);

    default int[] getIterations(double x0, double y0, int calculationsX, double dx, int maxIterations) {
        int[] iterations = new int[calculationsX * calculationsX];
        int i = 0;
        for (double x = 0; x < calculationsX; ++x) {
            for (double y = 0; y < calculationsX; ++y) {
                iterations[i++] = getIterations(x0 + x * dx, y0 + y * dx, maxIterations);
            }
        }
        return iterations;
    }
}
