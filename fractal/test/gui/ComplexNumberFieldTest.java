package gui;

import julia.Fractals.JuliaSet;
import julia.Fractals.MandelbrotSet;

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

		assertEquals(-1.23, juliaSet.getCx(), 0.0000001);
		assertEquals(0, juliaSet.getCy(), 0.0000001);
	}

	@Test
	public void testImaginarySimple() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("i");

		assertEquals(juliaSet.getCx(), 0, 0.0000001);
		assertEquals(juliaSet.getCy(), 1, 0.0000001);
	}

	@Test
	public void testImaginaryTimesConstant() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("-1.23i");

		assertEquals(juliaSet.getCx(), 0, 0.0000001);
		assertEquals(juliaSet.getCy(), -1.23, 0.0000001);
	}

	@Test
	public void testRealPlusImaginary() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("0.12+1.23i");

		assertEquals(juliaSet.getCx(), 0.12, 0.0000001);
		assertEquals(juliaSet.getCy(), 1.23, 0.0000001);
	}

	@Test
	public void testMinusRealMinusImaginary() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("-0.12-1.23i");

		assertEquals(juliaSet.getCx(), -0.12, 0.0000001);
		assertEquals(juliaSet.getCy(), -1.23, 0.0000001);
	}

	@Test
	public void testMinusRealMinusI() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("-0.12-i");

		assertEquals(juliaSet.getCx(), -0.12, 0.0000001);
		assertEquals(juliaSet.getCy(), -1, 0.0000001);
	}

	@Test
	public void testRealPlusSpaceI() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("1+ i");

		assertEquals(juliaSet.getCx(), 1, 0.0000001);
		assertEquals(juliaSet.getCy(), 1, 0.0000001);
	}

	@Test
	public void testIMinusReal() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("i-.12");

		assertEquals(juliaSet.getCx(), -.12, 0.0000001);
		assertEquals(juliaSet.getCy(), 1, 0.0000001);
	}

	@Test
	public void testImaginaryPlusReal() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("1.23i+0.12");

		assertEquals(juliaSet.getCx(), 0.12, 0.0000001);
		assertEquals(juliaSet.getCy(), 1.23, 0.0000001);
	}

	@Test
	public void testMinusI() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("-i");

		assertEquals(juliaSet.getCx(), 0, 0.0000001);
		assertEquals(juliaSet.getCy(), -1, 0.0000001);
	}

	@Test
	public void testMinusIMinusReal() {
		JuliaSet juliaSet = (JuliaSet) new ComplexNumberField(null).textToFractal("-i-2");

		assertEquals(juliaSet.getCx(), -2, 0.0000001);
		assertEquals(juliaSet.getCy(), -1, 0.0000001);
	}
}
