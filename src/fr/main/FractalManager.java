package fr.main;

import java.awt.Color;

import fr.draw.FractalColorer;
import fr.draw.FractalDrawerDelegate;
import fr.fractal.Fractal;
import fr.gui.ComplexNumberField;
import gt.component.ComponentCreator;
import gt.gameentity.CartesianSpace;
import gt.gameentity.IGameImage;
import gt.gameentity.IGameImageDrawer;
import gt.util.EMath;

public class FractalManager {
    private static final FractalManager instance = new FractalManager();

    private Fractal fractal;
    private final CartesianSpace cs;
    private final FractalColorer colorer;
    private FractalDrawerDelegate delegate;

    private int numIterations = FractalMain.DEFAULT_NUM_ITERATIONS;

    private FractalManager() {
        fractal = ComplexNumberField.textToFractal(FractalMain.DEFAULT_FRACTAL_TEXT);
        cs = new CartesianSpace(ComponentCreator.DEFAULT_WIDTH, ComponentCreator.DEFAULT_HEIGHT, -2.5, -2.5, 5, 5);
        colorer = new FractalColorer(FractalMain.DEFAULT_NUM_BASE_COLORS, FractalMain.DEFAULT_NUM_BETWEEN_COLORS);
    }

    public static void setImageDrawer(IGameImageDrawer imageDrawer) {
        instance.delegate = new FractalDrawerDelegate(imageDrawer, ComponentCreator.DEFAULT_WIDTH, ComponentCreator.DEFAULT_HEIGHT);
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

    public static void setNumberOfIterations(int numIterations) {
        instance.numIterations = numIterations;
        instance.delegate.requestReset();
    }

    public static Color getColor(double imageX, double imageY) {
        double x = instance.cs.getX(imageX);
        double y = instance.cs.getY(imageY);
        return instance.colorer.getColor(instance.fractal.getIterations(x, y, instance.numIterations), instance.numIterations);
    }

    public static Color getColor(double imageX, double imageY, int calculationsX) {
        double x = instance.cs.getX(imageX);
        double y = instance.cs.getY(imageY);
        double dx = instance.cs.getWidth(1.0 / calculationsX);
        return instance.colorer.getColor(instance.fractal.getIterations(x, y, calculationsX, dx, instance.numIterations), instance.numIterations);
    }

    public static void setFractal(Fractal fractal) {
        instance.fractal = fractal;
        instance.delegate.requestReset();
    }

    public static IGameImage requestImage() {
        int imageWidth = EMath.round(instance.cs.getImageWidth());
        int imageHeight = EMath.round(instance.cs.getImageHeight());
        return instance.delegate.requestImage(imageWidth, imageHeight);
    }

    public static boolean isDrawingComplete() {
        return instance.delegate.isDrawingComplete();
    }

    public static void setImageSize(double width, double height, boolean savingImage) {
        instance.cs.setSize(width, height);
        instance.delegate.requestReset(savingImage);
    }

    public static int getImageWidth() {
        return EMath.round(instance.cs.getImageWidth());
    }

    public static int getImageHeight() {
        return EMath.round(instance.cs.getImageHeight());
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

    public static void zoom(double n, int x, int y) {
        instance.cs.zoom(n, x, y);
        instance.delegate.requestReset();
    }

    public static void move(int x, int y) {
        instance.cs.move(x, y);
        instance.delegate.requestReset();
    }

    public static void updateDrawers() {
        instance.delegate.checkSplit();
    }

    public static Color[] getBaseColors() {
        return instance.colorer.getBaseColors();
    }

    public static void setBaseColors(Color[] baseColors) {
        instance.colorer.setBaseColors(baseColors);
        instance.delegate.requestReset();
    }
}
