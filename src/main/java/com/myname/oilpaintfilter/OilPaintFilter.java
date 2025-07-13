package com.myname.oilpaintfilter;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class OilPaintFilter {

    public static BufferedImage applyOilPaintingEffect(BufferedImage image) {
        int radius = 4;
        int intensityLevels = 20;

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage output = new BufferedImage(width, height, image.getType());

        int[] intensityCount = new int[intensityLevels];
        int[] sumR = new int[intensityLevels];
        int[] sumG = new int[intensityLevels];
        int[] sumB = new int[intensityLevels];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Reset histograms
                for (int i = 0; i < intensityLevels; i++) {
                    intensityCount[i] = sumR[i] = sumG[i] = sumB[i] = 0;
                }

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;

                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            Color color = new Color(image.getRGB(nx, ny));
                            int avg = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                            int intensity = (avg * intensityLevels) / 256;

                            intensityCount[intensity]++;
                            sumR[intensity] += color.getRed();
                            sumG[intensity] += color.getGreen();
                            sumB[intensity] += color.getBlue();
                        }
                    }
                }

                // Find max intensity
                int maxIndex = 0;
                for (int i = 1; i < intensityLevels; i++) {
                    if (intensityCount[i] > intensityCount[maxIndex]) {
                        maxIndex = i;
                    }
                }

                int r = sumR[maxIndex] / intensityCount[maxIndex];
                int g = sumG[maxIndex] / intensityCount[maxIndex];
                int b = sumB[maxIndex] / intensityCount[maxIndex];

                output.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        return output;
    }
}
