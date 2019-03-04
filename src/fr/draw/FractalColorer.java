package fr.draw;

import java.awt.Color;
import java.util.Random;

import gt.component.ComponentCreator;
import gt.gameentity.DrawingMethods;

public class FractalColorer implements DrawingMethods {
    private static final Random RANDOM = new Random();

    private Color[] baseColors;
    private Color[] allColors;

    public FractalColorer(int numBaseColors, int numBetweenColors) {
        resetBaseColors(0, numBaseColors);
        resetAllColors(numBetweenColors);
    }

    public void resetColors() {
        int numBetween = allColors.length / baseColors.length;
        resetBaseColors(0, baseColors.length);
        resetAllColors(numBetween);
    }

    public void setNumberOfColors(int numColors) {
        int numBetween = allColors.length / baseColors.length;
        int numToCopy = Math.min(baseColors.length, numColors);
        int numToAdd = numColors - numToCopy;
        resetBaseColors(numToCopy, numToAdd);
        resetAllColors(numBetween);
    }

    public void setNumberOfBetweenColors(int numColors) {
        resetAllColors(numColors);
    }

    private void resetBaseColors(int numToCopy, int numToAdd) {
        int newLength = numToCopy + Math.max(numToAdd, 0);
        Color[] newBaseColors = new Color[newLength];
        int i = 0;
        for (; i < numToCopy; ++i) {
            newBaseColors[i] = baseColors[i];
        }
        for (; i < newLength; ++i) {
            newBaseColors[i] = newRandomColor();
        }
        baseColors = newBaseColors;
    }

    private void resetAllColors(int numBetween) {
        int newLength = baseColors.length * numBetween;
        Color[] newAllColors = new Color[newLength];
        int allColorIndex = 0;

        Color previousColor = baseColors[0];
        for (int baseColorIndex = 0; baseColorIndex < baseColors.length; ++baseColorIndex) {
            newAllColors[allColorIndex++] = previousColor;
            Color nextColor = baseColors[(baseColorIndex + 1) % baseColors.length];
            for (int fadeColorIndex = 1; fadeColorIndex < numBetween; ++fadeColorIndex) {
                newAllColors[allColorIndex++] = fadeToColor(previousColor, nextColor, (double) fadeColorIndex / numBetween);
            }
            previousColor = nextColor;
        }
        allColors = newAllColors;
    }

    public Color getColor(int iterations, int maxIterations) {
        return iterations >= maxIterations ? ComponentCreator.backgroundColor() : allColors[iterations % allColors.length];
    }

    public Color getColor(int[] iterations, int maxIterations) {
        double red = 0;
        double green = 0;
        double blue = 0;
        for (int i = 0; i < iterations.length; ++i) {
            Color color = getColor(iterations[i], maxIterations);
            if (color == null) {
                "".toString();
            }
            red += color.getRed();
            green += color.getGreen();
            blue += color.getBlue();
        }
        return new Color(round(red / iterations.length), round(green / iterations.length), round(blue / iterations.length));
    }

    private static Color newRandomColor() {
        return new Color(RANDOM.nextInt(256 * 256 * 256 - 1));
    }
}
