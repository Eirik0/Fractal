package julia;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

public class JuliaColorer {
	private Random r = new Random();
	private double sq_3 = Math.sqrt(3);

	private Map<Integer, Color> colorPalette = new HashMap<Integer, Color>();
	private int size = 1;

	private static final JuliaColorer instance = new JuliaColorer();

	public static JuliaColorer getInstance() {
		return instance;
	}

	private JuliaColorer() {
		setColorPalette();
	}

	public void reset() {
		setColorPalette();
	}

	public Color getColor(int iterations, int maxIterations) {
		if (iterations > maxIterations - 1) {
			return Color.BLACK;
		}

		return colorPalette.get(iterations % size);
	}

	public Color getColor(int[] iterations, int maxIterations) {
		int red = 0;
		int green = 0;
		int blue = 0;
		int count = 0;

		for (int i : iterations) {
			Color c = getColor(i, maxIterations);
			if (c == null) {
				return Color.BLACK;
			}
			red += c.getRed();
			green += c.getGreen();
			blue += c.getBlue();
			++count;
		}

		return new Color((int) ((float) red / count), (int) ((float) green / count), (int) ((float) blue / count));
	}

	private void setColorPalette() {
		colorPalette.clear();
		colorPalette.put(0, Color.BLACK);

		List<Color> sortedRandomColors = null;
		int shading = r.nextInt(96) + 32;

		sortedRandomColors = IntStream.range(0, r.nextInt(1000) + 1).parallel()
				.mapToObj(i -> new Color(r.nextInt(255 - shading) + shading, r.nextInt(255 - shading) + shading, r.nextInt(255 - shading) + shading))
				.sorted((c1, c2) -> -(BigDecimal.valueOf(getHue(c1)).compareTo(BigDecimal.valueOf(getHue(c2))))).collect(Collectors.toList());

		int numOfCols = r.nextInt(6) + 1;
		int dColor = shading / numOfCols;

		for (int i = 1; i < sortedRandomColors.size(); i += numOfCols) {
			Color color = sortedRandomColors.get(i);
			colorPalette.put(i, color);

			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();

			for (int j = 0; j < numOfCols; ++j) {
				colorPalette.put(i + j, new Color(red - dColor * j, green - dColor * j, blue - dColor * j));
			}
		}

		size = colorPalette.size();
	}

	private double getHue(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		return Math.atan2(sq_3 * (g - b), 2 * r - g - b);
	}
}
