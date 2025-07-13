package com.myname.oilpaintfilter;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SequentialProcessor {

    public static void processFrames(String inputFolder, String outputFolder) {
        File inputDir = new File(inputFolder);
        File outputDir = new File(outputFolder);
        if (!outputDir.exists()) outputDir.mkdirs();

        File[] frames = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (frames == null || frames.length == 0) {
            System.out.println("No frames found in " + inputFolder);
            return;
        }

        System.out.println("[Profiler Marker] Running sequential baseline...");
        System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());
        System.out.printf("Processing %d frames%n", frames.length);

        long startTime = System.nanoTime();
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        for (File frame : frames) {
            try {
                BufferedImage img = ImageIO.read(frame);
                BufferedImage processed = OilPaintFilter.applyOilPaintingEffect(img);
                ImageIO.write(processed, "png", new File(outputDir, frame.getName()));
                System.out.println("Processed: " + frame.getName());
            } catch (Exception e) {
                System.err.println("Error: " + frame.getName());
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();
        long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeSec = (endTime - startTime) / 1e9;
        long usedMemMB = Math.max(0, (afterUsedMem - beforeUsedMem)) / (1024 * 1024);

        System.out.printf("Sequential processing completed in %.2f seconds%n", timeSec);
        System.out.printf("Approx. memory used: %d MB%n", usedMemMB);
        System.out.printf("CSV Output: SEQ,%.2f%n", timeSec);
    }
}
