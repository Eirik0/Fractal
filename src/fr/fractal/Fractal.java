package fr.fractal;

import fr.julia.JuliaSizer;

public interface Fractal {
    int getIterations(double x, double y, int maxIterations);

    default int[] getIterations(double x, double y, double pixelPerData, int maxIterations) {
        int dataPer = (int) Math.round(1 / pixelPerData);
        int[] iterations = new int[dataPer * dataPer];
        int i = 0;
        for (double xOffset = 0; xOffset < 1; xOffset += pixelPerData) {
            for (double yOffset = 0; yOffset < 1; yOffset += pixelPerData) {
                iterations[i] = getIterations(JuliaSizer.getX(x + xOffset), JuliaSizer.getY(y + yOffset), maxIterations);
                ++i;
            }
        }
        return iterations;
    }
}
