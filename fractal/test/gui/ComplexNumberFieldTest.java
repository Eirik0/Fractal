package gui;

import gui.FractalMain.JuliaSet;
import gui.FractalMain.MandelbrotSet;

import org.junit.*;

public class ComplexNumberFieldTest extends Assert {

	@Test
	public void testBlank() {
		MandelbrotSet mandelbrotSet = (MandelbrotSet) ComplexNumberField.textToFractal("");

		assertTrue("Expected MandelBrotSet", mandelbrotSet instanceof MandelbrotSet);
	}

	@Test
	public void testDouble() {
		JuliaSet juliaSet = (JuliaSet) ComplexNumberField.textToFractal("-1.23");

		assertEquals(-1.23, juliaSet.cx, 0.0000001);
		assertEquals(0, juliaSet.cy, 0.0000001);
	}

	@Test
	public void testImaginarySimple() {
		JuliaSet juliaSet = (JuliaSet) ComplexNumberField.textToFractal("i");

		assertEquals(juliaSet.cx, 0, 0.0000001);
		assertEquals(juliaSet.cy, 1, 0.0000001);
	}
}
