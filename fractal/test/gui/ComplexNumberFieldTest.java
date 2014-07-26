package gui;

import gui.FractalMain.JuliaSet;
import gui.FractalMain.MandelbrotSet;

import org.junit.*;

public class ComplexNumberFieldTest extends Assert {

	@Test
	public void testBlank() {
		MandelbrotSet mandelbrotSet = (MandelbrotSet) new ComplexNumberField(null).textToFractal("");

		assertTrue("Expected MandelBrotSet", mandelbrotSet instanceof MandelbrotSet);
	}

	@Test
	public void testDouble() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("-1.23");

		assertEquals(-1.23, juliaSet.cx, 0.0000001);
		assertEquals(0, juliaSet.cy, 0.0000001);
	}

	@Test
	public void testImaginarySimple() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("i");

		assertEquals(juliaSet.cx, 0, 0.0000001);
		assertEquals(juliaSet.cy, 1, 0.0000001);
	}

	@Test
	public void testImaginaryTimesConstant() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("-1.23i");

		assertEquals(juliaSet.cx, 0, 0.0000001);
		assertEquals(juliaSet.cy, -1.23, 0.0000001);
	}

	@Test
	public void testRealPlusImaginary() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("0.12+1.23i");

		assertEquals(juliaSet.cx, 0.12, 0.0000001);
		assertEquals(juliaSet.cy, 1.23, 0.0000001);
	}

	@Test
	public void testMinusRealMinusImaginary() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("-0.12-1.23i");

		assertEquals(juliaSet.cx, -0.12, 0.0000001);
		assertEquals(juliaSet.cy, -1.23, 0.0000001);
	}

	@Test
	public void testImaginaryPlusReal() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("1.23i+0.12");

		assertEquals(juliaSet.cx, 1.23, 0.0000001);
		assertEquals(juliaSet.cy, 0.12, 0.0000001);
	}
}
