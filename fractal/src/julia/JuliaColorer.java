package julia;

import gui.FractalMain.Fractal;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

public class JuliaColorer {
	public static int DEFAULT_NUMBER_OF_COLORS = 5;
	public static int DEFAULT_DISTANCE_BETWEEN_COLORS = 50;

	private static Random r = new Random();

	private static List<Color> baseColors = new ArrayList<>();
	private static List<Color> colors = new ArrayList<>();
	private static int numberOfColors = DEFAULT_NUMBER_OF_COLORS;
	private static int distanceBetweenColors = DEFAULT_DISTANCE_BETWEEN_COLORS;

	public static void resetColors() {
		baseColors.clear();
		for (int i = 0; i < numberOfColors; ++i) {
			baseColors.add(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
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
					baseColors.add(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
				}
			} else if (diff < 0) {
				for (int i = 0; i < -diff; ++i) {
					baseColors.remove(baseColors.size() - 1);
				}
			}
		}

		colors.clear();
		for (int i = 0; i < baseColors.size(); ++i) {
			Color currentColor = baseColors.get(i);
			Color nextColor = baseColors.get((i + 1) % baseColors.size());

			int currentRed = currentColor.getRed();
			int currentGreen = currentColor.getGreen();
			int currentBlue = currentColor.getBlue();

			int dRed = (nextColor.getRed() - currentRed) / dist;
			int dGreen = (nextColor.getGreen() - currentGreen) / dist;
			int dBlue = (nextColor.getBlue() - currentBlue) / dist;

			for (int d = 0; d < dist; ++d) {
				colors.add(new Color(currentRed + d * dRed, currentGreen + d * dGreen, currentBlue + d * dBlue));
			}
		}
		numberOfColors = cols;
		distanceBetweenColors = dist;
	}

	public static Color getColor(int iterations) {
		if (iterations > Fractal.MAX_ITERATIONS - 1 || colors.size() == 0) {
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
			if (c == null) {
				// Not thread safe
				return Color.BLACK;
			}
			red += c.getRed();
			green += c.getGreen();
			blue += c.getBlue();
			++count;
		}

		return new Color((int) ((float) red / count), (int) ((float) green / count), (int) ((float) blue / count));
	}
}
