package fr.main;

import java.awt.Color;
import java.awt.image.BufferedImage;

import fr.draw.FractalColorer;
import fr.fractal.Fractal;
import fr.gui.ComplexNumberField;
import fr.julia.JuliaImageDrawerDelegate;
import gt.component.ComponentCreator;
import gt.gameentity.CartesianSpace;

public class FractalManager {
    private static final int MAX_ITERATIONS = 20000;

    private static final FractalManager instance = new FractalManager();

    private Fractal fractal;
    private CartesianSpace cs;
    private final FractalColorer colorer;
    private final JuliaImageDrawerDelegate delegate;

    private FractalManager() {
        fractal = ComplexNumberField.textToFractal(FractalMain.DEFAULT_FRACTAL_TEXT);
        cs = new CartesianSpace(ComponentCreator.DEFAULT_WIDTH, ComponentCreator.DEFAULT_HEIGHT, -2.5, -2.5, 5, 5);
        colorer = new FractalColorer();
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

    public static Color getColor(double imageX, double imageY) {
        double x = instance.cs.getX(imageX);
        double y = instance.cs.getY(imageY);
        return instance.colorer.getColor(instance.fractal.getIterations(x, y, MAX_ITERATIONS), MAX_ITERATIONS);
    }

    public static Color getColor(double imageX, double imageY, int calculationsX) {
        double x = instance.cs.getX(imageX);
        double y = instance.cs.getY(imageY);
        double dx = instance.cs.getWidth(1.0 / calculationsX);
        return instance.colorer.getColor(instance.fractal.getIterations(x, y, calculationsX, dx, MAX_ITERATIONS), MAX_ITERATIONS);
    }

    public static void setFractal(Fractal fractal) {
        instance.fractal = fractal;
        instance.delegate.requestReset();
    }

    public static BufferedImage requestImage(int imageWidth, int imageHeight) {
        return instance.delegate.requestImage(imageWidth, imageHeight);
    }

    public static void setImageSize(int width, int height) {
        instance.cs.setSize(width, height);
        instance.delegate.requestReset();
    }

    public static void setFractalBounds(double x0, double y0, double width, double height) {
        instance.cs.setBounds(x0, y0, width, height);
        instance.delegate.requestReset();
    }

    public static void zoomTo(double x0, double y0, double x1, double y1) {
        double x = instance.cs.getX(x0);
        double y = instance.cs.getY(y0);
        double width = instance.cs.getWidth(x1 - x0);
        double height = instance.cs.getWidth(y1 - y0);
        instance.cs.setBounds(x, y, width, height);
        instance.delegate.requestReset();
    }

    public static void zoom(int n) {
        instance.cs.zoom(0.1 * n);
        instance.delegate.requestReset();
    }
}
