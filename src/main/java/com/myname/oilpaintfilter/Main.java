package com.myname.oilpaintfilter;

public class Main {
	
    public static void main(String[] args) {
        String framesDir = "frames";
        String outSeq = "output_sequential";

        // === Profiling Marker ===
        System.out.println("[Profiler Marker] Starting Sequential Processor. Attach VisualVM now!");

        System.out.println("==== Running Sequential Processor ====");
        SequentialProcessor.processFrames(framesDir, outSeq);

        System.out.println("\n==== Running Parallel Processor ====");

        int[] threadsToTest = {1, 2, 4, 8};

        for (int threads : threadsToTest) {
            System.out.printf("\n[Profiler Marker] Parallel run with %d threads. Attach VisualVM now!\n", threads);

            String outPar = "output_parallel_" + threads + "_threads";
            ParallelProcessor.processFrames(framesDir, outPar, threads);
        }

        System.out.println("\n=== All done! ===");
        System.out.println("Copy the CSV output above into a spreadsheet for plots!");
    }
}
