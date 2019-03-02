package fr.main;

import java.awt.Color;

import fr.draw.FractalColorer;
import fr.fractal.Fractal;

public class FractalManager {
    private static final int MAX_ITERATIONS = 20000;

    private final FractalColorer colorer = new FractalColorer();

    private static final FractalManager instance = new FractalManager();

    private FractalManager() {
    }

    public static void resetColors() {
        instance.colorer.resetColors();
    }

    public static void setNumberOfColors(int numColors) {
        instance.colorer.setNumberOfColors(numColors);
    }

    public static void setNumberOfBetweenColors(int numColors) {
        instance.colorer.setNumberOfBetweenColors(numColors);
    }

    public static Color getColor(Fractal fractal, double x, double y) {
        return instance.colorer.getColor(fractal.getIterations(x, y, MAX_ITERATIONS), MAX_ITERATIONS);
    }

    public static Color getColor(Fractal fractal, double x, double y, double pixelPerData) {
        return instance.colorer.getColor(fractal.getIterations(x, y, pixelPerData, MAX_ITERATIONS), MAX_ITERATIONS);
    }
}
