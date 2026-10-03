// Main.java
package com.project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int CAPACITY  = 3;  // places de l'aparcament
    private static final int NUM_CARS  = 10; // cotxes a simular
    private static final int POOL_SIZE = 6;  // fils del pool (> CAPACITY per veure esperes)

    public static void main(String[] args) {
        System.out.println("Aparcament amb " + CAPACITY + " places, " + NUM_CARS + " cotxes.");

        ParkingLot parking = new ParkingLot(CAPACITY);
        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);

        for (int i = 1; i <= NUM_CARS; i++) {
            executor.submit(new CarTask(parking, i));
        }

        executor.shutdown(); // no admet més tasques
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                System.out.println("Temps esgotat, forçant tancament...");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Simulació acabada. Executor tancat.");
    }
}
