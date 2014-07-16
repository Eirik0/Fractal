package julia;

public class JuliaSizer {
	private double x0;
	private double y0;
	private double juliaWidth;
	private double juliaHeight;
	private int imageWidth;
	private int imageHeight;
	// Cached division enforced through setScale()
	private double scaleX;
	private double scaleY;

	public JuliaSizer(double x0, double y0, double juliaWidth, double juliaHeight, int imageWidth, int imageHeight) {
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;

		setJuliaBounds(x0, y0, juliaWidth, juliaHeight);
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public double getX(double imageX) {
		return x0 + imageX * scaleX;
	}

	public double getY(double imageY) {
		return y0 + imageY * scaleY;
	}

	public void zoom(int rotations) {
		double dy = -(juliaHeight / 10) * rotations;
		double dx = -(juliaWidth / 10) * rotations;

		setJuliaBounds(x0 + dx, y0 + dy, juliaWidth - 2 * dx, juliaHeight - 2 * dy);
	}

	public void zoomTo(double ul_x, double ul_y, double br_x, double br_y) {
		double x = getX(ul_x);
		double y = getY(ul_y);

		setJuliaBounds(x, y, getX(br_x) - x, getY(br_y) - y);
	}

	public void setImageDimensions(int width, int height) {
		// This makes the bounds of the JuliaSet scale with the viewable window
		double dx = (double) width / imageWidth;
		double dy = (double) height / imageHeight;
		// center
		setJuliaBounds(x0 + juliaWidth * (1 - dx) / 2, y0 + juliaHeight * (1 - dy) / 2, juliaWidth * dx, juliaHeight * dy);

		imageWidth = width;
		imageHeight = height;

		setScale();
	}

	public void setJuliaBounds(double x0, double y0, double width, double height) {
		this.x0 = x0;
		this.y0 = y0;

		juliaWidth = width;
		juliaHeight = height;

		// This makes the aspect ratio of the JuliaSet and the image 1:1
		double imageAspectRatio = (double) imageWidth / imageHeight;
		double juliaAspectRatio = width / height;
		// scale width
		// TODO scale height sometimes ?
		juliaWidth *= (imageAspectRatio / juliaAspectRatio);

		this.x0 -= (juliaWidth - width) / 2;
		
		setScale();
	}
	
	private void setScale() {
		scaleX = juliaWidth / imageWidth;
		scaleY = juliaHeight / imageHeight;
	}
}
