package com.project;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    private static final int DATA_SIZE = 5_000_000;

    // Resultats parcials compartits entre els fils (clau = nom del càlcul)
    private static final ConcurrentHashMap<String, Double> resultats = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        long t0 = System.nanoTime();

        // Conjunt de dades gran (llavor fixa perquè el resultat sigui reproduïble)
        double[] dades = generarDades(DATA_SIZE, 42L);
        System.out.println("Conjunt de dades generat: " + dades.length + " valors");

        // La barrera espera 3 tasques; quan l'última arriba, s'executa l'acció final
        CyclicBarrier barrera = new CyclicBarrier(3, () -> {
            long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
            System.out.println("Tots els càlculs han acabat. Resultats finals:");
            System.out.println(String.format(Locale.US, "  Suma:                %.4f", resultats.get("suma")));
            System.out.println(String.format(Locale.US, "  Mitjana:             %.6f", resultats.get("mitjana")));
            System.out.println(String.format(Locale.US, "  Desviació estàndard: %.6f", resultats.get("desviacio")));
            System.out.println("Temps total: " + ms + "ms");
        });

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(tascaSuma(dades, barrera));
        executor.submit(tascaMitjana(dades, barrera));
        executor.submit(tascaDesviacio(dades, barrera));

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("Temps d'espera esgotat, forçant la parada...");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static double[] generarDades(int mida, long llavor) {
        Random random = new Random(llavor);
        double[] dades = new double[mida];
        for (int i = 0; i < mida; i++) {
            dades[i] = random.nextDouble() * 100; // valors entre 0 i 100
        }
        return dades;
    }

    private static Runnable tascaSuma(double[] dades, CyclicBarrier barrera) {
        return () -> {
            try {
                System.out.println("[Suma] Començant el càlcul...");
                double suma = 0;
                for (double d : dades) suma += d;
                resultats.put("suma", suma);
                System.out.println("[Suma] Completat.");
                barrera.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.err.println("Error a la tasca Suma: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        };
    }

    private static Runnable tascaMitjana(double[] dades, CyclicBarrier barrera) {
        return () -> {
            try {
                System.out.println("[Mitjana] Començant el càlcul...");
                double suma = 0;
                for (double d : dades) suma += d;
                double mitjana = suma / dades.length;
                resultats.put("mitjana", mitjana);
                System.out.println("[Mitjana] Completat.");
                barrera.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.err.println("Error a la tasca Mitjana: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        };
    }

    private static Runnable tascaDesviacio(double[] dades, CyclicBarrier barrera) {
        return () -> {
            try {
                System.out.println("[Desviació] Començant el càlcul...");
                // Cada tasca és independent: calcula la seva pròpia mitjana
                double suma = 0;
                for (double d : dades) suma += d;
                double mitjana = suma / dades.length;

                double sumaQuadrats = 0;
                for (double d : dades) sumaQuadrats += (d - mitjana) * (d - mitjana);
                double desviacio = Math.sqrt(sumaQuadrats / dades.length); // poblacional

                resultats.put("desviacio", desviacio);
                System.out.println("[Desviació] Completat.");
                barrera.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.err.println("Error a la tasca Desviació: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        };
    }
}
