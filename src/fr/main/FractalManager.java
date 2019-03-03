package fr.main;

import java.awt.Color;
import java.awt.image.BufferedImage;

import fr.draw.FractalColorer;
import fr.fractal.Fractal;
import fr.gui.ComplexNumberField;
import fr.julia.JuliaImageDrawerDelegate;
import fr.julia.JuliaSizer;
import gt.component.ComponentCreator;

public class FractalManager {
    private static final int MAX_ITERATIONS = 20000;

    private static final FractalManager instance = new FractalManager();

    private final JuliaImageDrawerDelegate delegate;

    private Fractal fractal = ComplexNumberField.textToFractal(FractalMain.DEFAULT_FRACTAL_TEXT);

    private final FractalColorer colorer = new FractalColorer();

    private FractalManager() {
        JuliaSizer.init(-2.5, -2.5, 5, 5, ComponentCreator.DEFAULT_WIDTH, ComponentCreator.DEFAULT_HEIGHT);
        delegate = new JuliaImageDrawerDelegate(ComponentCreator.DEFAULT_WIDTH, ComponentCreator.DEFAULT_HEIGHT);
    }

    public static void resetColors() {
        instance.colorer.resetColors();
        instance.delegate.requestReset();
    }

    public static void setNumberOfColors(int numColors) {
        instance.colorer.setNumberOfColors(numColors);
        instance.delegate.requestReset();
    }

    public static void setNumberOfBetweenColors(int numColors) {
        instance.colorer.setNumberOfBetweenColors(numColors);
        instance.delegate.requestReset();
    }

    public static Color getColor(double x, double y) {
        return instance.colorer.getColor(instance.fractal.getIterations(x, y, MAX_ITERATIONS), MAX_ITERATIONS);
    }

    public static Color getColor(double x, double y, double pixelPerData) {
        return instance.colorer.getColor(instance.fractal.getIterations(x, y, pixelPerData, MAX_ITERATIONS), MAX_ITERATIONS);
    }

    public static void setFractal(Fractal fractal) {
        instance.fractal = fractal;
        instance.delegate.requestReset();
    }

    public static BufferedImage requestImage() {
        return instance.delegate.requestImage();
    }

    public static void setImageSize(int width, int height) {
        JuliaSizer.setImageDimensions(width, height);
        instance.delegate.requestReset();
    }

    public static void setFractalBounds(double x0, double y0, double width, double height) {
        JuliaSizer.setJuliaBounds(x0, y0, width, height);
        instance.delegate.requestReset();
    }

    public static void zoomTo(double x0, double y0, double x1, double y1) {
        JuliaSizer.zoomTo(x0, y0, x1, y1);
        instance.delegate.requestReset();
    }

    public static void zoom(int n) {
        JuliaSizer.zoom(n);
        instance.delegate.requestReset();
    }

}
