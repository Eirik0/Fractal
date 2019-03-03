package fr.julia;

public class JuliaSizer {
    private static double x0;
    private static double y0;

    private static double juliaWidth;
    private static double juliaHeight;

    private static int imageWidth;
    private static int imageHeight;

    // Cached division enforced through setScale()
    private static double scaleX;
    private static double scaleY;

    public static void init(double x0, double y0, double juliaWidth, double juliaHeight, int newImageWidth, int newImageHeight) {
        imageWidth = newImageWidth;
        imageHeight = newImageHeight;

        setJuliaBounds(x0, y0, juliaWidth, juliaHeight);
    }

    public static double getX(double imageX) {
        return x0 + imageX * scaleX;
    }

    public static double getY(double imageY) {
        return y0 + imageY * scaleY;
    }

    public static void zoom(int rotations) {
        double dy = -(juliaHeight / 10) * rotations;
        double dx = -(juliaWidth / 10) * rotations;

        setJuliaBounds(x0 + dx, y0 + dy, juliaWidth - 2 * dx, juliaHeight - 2 * dy);
    }

    public static void zoomTo(double ul_x, double ul_y, double br_x, double br_y) {
        setJuliaBounds(getX(ul_x), getY(ul_y), getX(br_x) - getX(ul_x), getY(br_y) - getY(ul_y));
    }

    public static void setImageDimensions(int width, int height) {
        double dx = (double) width / imageWidth;
        double dy = (double) height / imageHeight;

        imageWidth = width;
        imageHeight = height;

        setJuliaBounds(x0 + juliaWidth * (1 - dx) / 2, y0 + juliaHeight * (1 - dy) / 2, juliaWidth * dx, juliaHeight * dy);
    }

    public static void setJuliaBounds(double x, double y, double width, double height) {
        x0 = x;
        y0 = y;

        juliaWidth = width;
        juliaHeight = height;

        // This makes the aspect ratio of the JuliaSet and the image 1:1
        double imageAspectRatio = (double) imageWidth / imageHeight;
        double juliaAspectRatio = width / height;

        if (imageAspectRatio > juliaAspectRatio) {
            juliaWidth *= (imageAspectRatio / juliaAspectRatio);
            x0 -= (juliaWidth - width) / 2;
        } else {
            juliaHeight *= (juliaAspectRatio / imageAspectRatio);
            y0 -= (juliaHeight - height) / 2;
        }

        setScale();
    }

    private static void setScale() {
        scaleX = juliaWidth / imageWidth;
        scaleY = juliaHeight / imageHeight;
    }
}
