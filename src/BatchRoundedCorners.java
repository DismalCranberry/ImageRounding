import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BatchRoundedCorners {
    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        // Enable antialiasing and high-quality rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setComposite(AlphaComposite.Src);

        // Draw a rounded rectangle as the mask
        RoundRectangle2D round = new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius);
        g2.setColor(java.awt.Color.WHITE);
        g2.fill(round);

        // Composite the original image atop the mask
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();
        return output;
    }

    public static void main(String[] args) {
        // Configure input and output folders
        File inputDir = new File("res");
        File outputDir = new File("output");
        int cornerRadius = 50; // adjust radius as needed

        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.err.println("Input directory 'res' does not exist or is not a directory.");
            return;
        }

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File[] files = inputDir.listFiles((_, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
        });

        if (files == null || files.length == 0) {
            System.out.println("No image files found in 'res' directory.");
            return;
        }

        for (File file : files) {
            try {
                BufferedImage original = ImageIO.read(file);
                if (original == null) {
                    System.out.println("Skipping non-image file: " + file.getName());
                    continue;
                }
                BufferedImage rounded = makeRoundedCorner(original, cornerRadius);
                String outputName = file.getName().replaceFirst("\\.[^.]+$", "") + "_rounded.png";
                File outputFile = new File(outputDir, outputName);
                ImageIO.write(rounded, "PNG", outputFile);
                System.out.println("Processed: " + file.getName() + " -> " + outputName);
            } catch (IOException e) {
                System.err.println("Failed to process " + file.getName() + ": " + e.getMessage());
            }
        }
        System.out.println("Batch processing complete. Rounded images are in 'output' folder.");
    }
}
