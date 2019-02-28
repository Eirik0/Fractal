package fr.julia;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JuliaColorer {
    public static final int DEFAULT_NUMBER_OF_COLORS = 5;
    public static final int DEFAULT_DISTANCE_BETWEEN_COLORS = 50;

    private static List<Color> baseColors = new ArrayList<>();
    private static List<Color> colors = new ArrayList<>();

    private static int numberOfColors = DEFAULT_NUMBER_OF_COLORS;
    private static int distanceBetweenColors = DEFAULT_DISTANCE_BETWEEN_COLORS;

    public static void resetColors() {
        baseColors.clear();
        for (int i = 0; i < numberOfColors; ++i) {
            baseColors.add(randomColor());
        }
        setColors(numberOfColors, distanceBetweenColors, true);
    }

    public static void setNumberOfColors(int cols) {
        setColors(cols, distanceBetweenColors, true);
    }

    public static void setDistanceBetweenColors(int dist) {
        setColors(numberOfColors, dist, false);
    }

    public static void setColors(int cols, int dist, boolean resetBaseColors) {
        if (resetBaseColors) {
            int diff = cols - baseColors.size();
            if (diff > 0) {
                for (int i = 0; i < diff; ++i) {
                    baseColors.add(randomColor());
                }
            } else if (diff < 0) {
                for (int i = 0; i < -diff; ++i) {
                    baseColors.remove(baseColors.size() - 1);
                }
            }
        }

        ArrayList<Color> newColors = new ArrayList<>();
        for (int i = 0; i < baseColors.size(); ++i) {
            Color currentColor = baseColors.get(i);
            Color nextColor = baseColors.get((i + 1) % baseColors.size());

            int currentRed = currentColor.getRed();
            int currentGreen = currentColor.getGreen();
            int currentBlue = currentColor.getBlue();

            double dRed = (double) (nextColor.getRed() - currentRed) / dist;
            double dGreen = (double) (nextColor.getGreen() - currentGreen) / dist;
            double dBlue = (double) (nextColor.getBlue() - currentBlue) / dist;

            for (int d = 0; d < dist; ++d) {
                newColors.add(new Color((int) (currentRed + d * dRed), (int) (currentGreen + d * dGreen), (int) (currentBlue + d * dBlue)));
            }
        }

        colors = newColors;
        numberOfColors = cols;
        distanceBetweenColors = dist;
    }

    private static Color randomColor() {
        return new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
    }

    public static Color getColor(int iterations) {
        if (iterations > Fractals.MAX_ITERATIONS - 1 || colors.size() == 0) {
            return Color.BLACK;
        }

        return colors.get(iterations % colors.size());
    }

    public static Color getColor(int[] iterations) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int count = 0;

        for (int i : iterations) {
            Color c = getColor(i);

            red += c.getRed();
            green += c.getGreen();
            blue += c.getBlue();
            ++count;
        }

        return new Color((int) ((float) red / count), (int) ((float) green / count), (int) ((float) blue / count));
    }
}
