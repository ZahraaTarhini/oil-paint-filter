package com.myname.oilpaintfilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelProcessor {

    public static void processFrames(String inputFolder, String outputFolder, int numThreads) {
        File inputDir = new File(inputFolder);
        File outputDir = new File(outputFolder);
        if (!outputDir.exists()) outputDir.mkdirs();

        File[] frames = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (frames == null || frames.length == 0) {
            System.out.println("No frames found in " + inputFolder);
            return;
        }

        System.out.printf("[Profiler Marker] Running parallel version with %d threads...%n", numThreads);
        System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());
        System.out.printf("Processing %d frames%n", frames.length);

        ForkJoinPool pool = new ForkJoinPool(numThreads);
        System.out.println("ForkJoinPool parallelism level: " + pool.getParallelism());

        long startTime = System.nanoTime();
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        pool.invoke(new FrameTask(frames, 0, frames.length, outputDir));
        pool.shutdown();

        long endTime = System.nanoTime();
        long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeSec = (endTime - startTime) / 1e9;
        long usedMemMB = Math.max(0, (afterUsedMem - beforeUsedMem)) / (1024 * 1024);

        System.out.printf("Parallel processing completed in %.2f seconds%n", timeSec);
        System.out.printf("Approx. memory used: %d MB%n", usedMemMB);
        System.out.printf("CSV Output: PAR,%d,%.2f%n", numThreads, timeSec);
    }

    static class FrameTask extends RecursiveAction {
        private static final int THRESHOLD = 10;
        private final File[] frames;
        private final int start, end;
        private final File outputFolder;

        public FrameTask(File[] frames, int start, int end, File outputFolder) {
            this.frames = frames;
            this.start = start;
            this.end = end;
            this.outputFolder = outputFolder;
        }

        @Override
        protected void compute() {
            System.out.printf("Processing frames %d to %d on thread: %s%n",
                    start, end, Thread.currentThread().getName());

            if (end - start <= THRESHOLD) {
                for (int i = start; i < end; i++) {
                    try {
                        BufferedImage img = ImageIO.read(frames[i]);
                        BufferedImage processed = OilPaintFilter.applyOilPaintingEffect(img);
                        File out = new File(outputFolder, frames[i].getName());
                        ImageIO.write(processed, "png", out);
                        System.out.println("Processed: " + frames[i].getName());
                    } catch (Exception e) {
                        System.err.println("Failed: " + frames[i].getName());
                        e.printStackTrace();
                    }
                }
            } else {
                int mid = (start + end) / 2;
                invokeAll(
                        new FrameTask(frames, start, mid, outputFolder),
                        new FrameTask(frames, mid, end, outputFolder)
                );
            }
        }
    }
}
