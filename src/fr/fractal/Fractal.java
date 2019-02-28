package fr.fractal;

import fr.julia.JuliaSizer;

public interface Fractal {
    int MAX_ITERATIONS = 20000;

    int getIterations(double x, double y);

    default int[] getIterations(int x, int y, double pixelPerData) {
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
